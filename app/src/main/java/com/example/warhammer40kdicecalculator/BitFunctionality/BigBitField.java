package com.example.warhammer40kdicecalculator.BitFunctionality;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Iterator;

public  class BigBitField<SpecifiedEnum extends  Enum<SpecifiedEnum> & BitEnum<SpecifiedEnum>> implements Iterable<SpecifiedEnum> {
    // Hard coded because I want to avoid dynamic allocations
    private long FirstBitField = 0;
    // Does not yet support more than 64 values
    private long SecondBitField = 0;
    private final int MAXSIZE = 64;
    private int count = 0;
    private SpecifiedEnum[] enumValues;

    public BigBitField(SpecifiedEnum bitEnum)
    {
        if( bitEnum.GetValues().length > MAXSIZE)
        {
           Log.d("Enum bitfield", "To many variables in the enum, Max size needs to be increased");
        }
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
