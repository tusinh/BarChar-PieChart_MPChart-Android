package com.xxmassdeveloper.mpchartexample;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

public class MyXAxisValueFormatter implements IAxisValueFormatter {
    private String[] mValues;
    private DecimalFormat mFormat;

    public MyXAxisValueFormatter(String[] values) {
        this.mValues = values;
    }

    public MyXAxisValueFormatter() {
//        this.mValues=values;
        mFormat = new DecimalFormat("###,###,###,###"); //###,###,###,##0.0
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int intValue = (int) value;
        if (mValues.length > intValue && intValue >= 0) return mValues[(int) value];
        return "";
//        return mFormat.format(value);

    }
}
