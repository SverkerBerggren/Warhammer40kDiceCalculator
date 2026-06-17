package core.BitFunctionality;


import core.Logging.Logging;

import java.util.Iterator;
import java.util.NoSuchElementException;

public  class BigBitField<SpecifiedEnum extends  Enum<SpecifiedEnum> & BitEnum<SpecifiedEnum>> implements Iterable<SpecifiedEnum> {
    // Hard coded because I want to avoid dynamic allocations
    protected long FirstBitField = 0;
    // Does not yet support more than 64 values
    private long SecondBitField = 0;
    private final int MAXSIZE = 64;
    private final SpecifiedEnum[] enumValues;

    private final Class<SpecifiedEnum> enumClass;
    public int Count() {
        return Long.bitCount(FirstBitField) + Long.bitCount(SecondBitField);
    }
    public BigBitField(Class<SpecifiedEnum> enumClass) {
        this.enumClass = enumClass;  // store it
        enumValues = enumClass.getEnumConstants();

        if (enumValues.length > MAXSIZE) {
            Logging.d("Enum bitfield", "Too many variables in the enum");
        }
    }

    public BigBitField<SpecifiedEnum> Copy() {
        BigBitField<SpecifiedEnum> newBitField = new BigBitField<>(enumClass);  // reuse stored class
        newBitField.FirstBitField = FirstBitField;
        newBitField.SecondBitField = SecondBitField;
        return newBitField;
    }

    public boolean IsSet(SpecifiedEnum enumValue) {
        return (FirstBitField & (1L << enumValue.ordinal())) != 0;
    }
    public void Set(SpecifiedEnum enumValue, boolean value)
    {
        long index = (long) 1 << enumValue.ordinal();

        if(value)
        {
            if(enumValue.ordinal() < 64)
            {
                FirstBitField =  (FirstBitField | index);
            }
        }
        else
        {
            if(enumValue.ordinal() < 64)
            {
                FirstBitField =  (FirstBitField & (~index));
            }
        }
    }

    @Override
    public Iterator<SpecifiedEnum> iterator() {
        return new Iterator<SpecifiedEnum>()
        {
           private int nextBitIndex = 0;
           @Override
           public boolean hasNext() {
               return (FirstBitField >>> nextBitIndex) != 0;
           }

           @Override
           public SpecifiedEnum next() {
               if (FirstBitField >>> nextBitIndex == 0) throw new NoSuchElementException();

               // Jump directly to the next set bit instead of scanning
               int index = nextBitIndex + Long.numberOfTrailingZeros(FirstBitField >>> nextBitIndex);
               nextBitIndex = index + 1;
               return enumValues[index];
           }
       };
    }
}
