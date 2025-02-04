package com.example.warhammer40kdicecalculator.BitFunctionality;

import android.util.Log;
import android.widget.GridLayout;

import androidx.recyclerview.widget.SortedList;

import com.example.warhammer40kdicecalculator.Conditions;
import com.example.warhammer40kdicecalculator.Enums.Faction;

public  class BigBitField<SpecifiedEnum extends  Enum<SpecifiedEnum> & BitEnum<SpecifiedEnum>> {
    // Hard coded because I want to avoid dynamic allocations
    private long FirstBitField = 0;
    private long SecondBitField = 0;
    private final int MAXSIZE = 128;

    public BigBitField(SpecifiedEnum bitEnum)
    {
        if( bitEnum.GetValues().length > MAXSIZE)
        {
           Log.d("Enum bitfield", "To many variables in the enum, Max size needs to be increased");
        }
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
                FirstBitField =  (FirstBitField | index);
            }
            else
            {
                SecondBitField =  (SecondBitField | index);
            }
        }
        else
        {
            if(enumValue.ordinal() <= 64)
            {
                FirstBitField =  (FirstBitField & (~index));
            }
            else
            {
                SecondBitField =  (SecondBitField & (~index));
            }
        }

    }

}
