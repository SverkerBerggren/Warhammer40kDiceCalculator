package com.app.DamageCalculator40k.BitFunctionality;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Iterator;

public  class BigBitField<SpecifiedEnum extends  Enum<SpecifiedEnum> & BitEnum<SpecifiedEnum>> implements Iterable<SpecifiedEnum> {
    // Hard coded because I want to avoid dynamic allocations
    protected long FirstBitField = 0;
    // Does not yet support more than 64 values
    private long SecondBitField = 0;
    private final int MAXSIZE = 64;
    protected int count = 0;
    private final SpecifiedEnum[] enumValues;

    // TODO: Ultra ghetto bababooey
    private final SpecifiedEnum firstEnum;

    public int Count()
    {
        return count;
    }

    public BigBitField<SpecifiedEnum> Copy()
    {
        BigBitField<SpecifiedEnum> newBitField = new BigBitField<>(firstEnum);
        newBitField.FirstBitField = FirstBitField;
        newBitField.SecondBitField = SecondBitField;
        newBitField.count = count;

        return newBitField;
    }

    public BigBitField(SpecifiedEnum bitEnum)
    {
        if( bitEnum.GetValues().length > MAXSIZE)
        {
           Log.d("Enum bitfield", "To many variables in the enum, Max size needs to be increased");
        }
        firstEnum = bitEnum;
        enumValues = bitEnum.GetValues();
    }

    public boolean IsSet(SpecifiedEnum enumValue)
    {
        long index = (long) 1 << enumValue.ordinal() ;
        if(enumValue.ordinal() <= 64)
        {
            return  (FirstBitField & index) != 0;
        }
        else
        {
            return (SecondBitField & index) != 0;
        }
    }
    public void Set(SpecifiedEnum enumValue, boolean value)
    {
        long index = (long) 1 << enumValue.ordinal();

        if(value)
        {
            if(enumValue.ordinal() <= 64)
            {
                long alreadySet = FirstBitField & index;
                if(alreadySet == 0)
                {
                    count++;
                }
                FirstBitField =  (FirstBitField | index);
            }
        }
        else
        {
            if(enumValue.ordinal() <= 64)
            {
                long alreadySet = FirstBitField & index;
                if(alreadySet != 0)
                {
                    count--;
                }
                FirstBitField =  (FirstBitField & (~index));
            }
        }
    }

    @NonNull
    @Override
    public Iterator<SpecifiedEnum> iterator() {
         Iterator<SpecifiedEnum> iterator = new Iterator<SpecifiedEnum>()
         {
            private int currentCount = 0;
            private int nextBitIndex = 0;
            @Override
            public boolean hasNext() {
                return currentCount < count;
            }

            @Override
            public SpecifiedEnum next() {
                if(!hasNext())
                {
                    throw new RuntimeException("Stub!");
                }
                int index = nextBitIndex;
                for(;index < MAXSIZE;index++)
                {
                    long IsSet = FirstBitField & ((long) 1 <<index);
                    if(IsSet != 0)
                    {
                        currentCount++;
                        nextBitIndex = index +1;
                        return enumValues[index];
                    }
                }
                return null;
            }
        };
        return  iterator;
    }
}
