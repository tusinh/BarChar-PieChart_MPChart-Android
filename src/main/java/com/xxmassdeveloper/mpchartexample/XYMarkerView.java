
package com.xxmassdeveloper.mpchartexample;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.text.DecimalFormat;

import lib.mpchart.R;


/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class XYMarkerView extends MarkerView {

    private RelativeLayout mainLayout;
    private TextView tvContent;
    private IAxisValueFormatter xAxisValueFormatter;

    private DecimalFormat format;
    private int type=-1;
    String[] months;
    public String key="";

    View.OnClickListener onClickListener;


    public XYMarkerView(Context context, IAxisValueFormatter xAxisValueFormatter) {
        super(context, R.layout.custom_marker_view);

        this.xAxisValueFormatter = xAxisValueFormatter;
        mainLayout = findViewById(R.id.main_layout);
        tvContent = findViewById(R.id.tvContent);
        format = new DecimalFormat("###,###,###,###");
    }

    public XYMarkerView(Context context, IAxisValueFormatter xAxisValueFormatter,int type,String[] months, View.OnClickListener onClickListener) {
        super(context, R.layout.custom_marker_view);

        this.xAxisValueFormatter = xAxisValueFormatter;
        this.type=type;
        if(months!=null){
            this.months=months;
        }

        mainLayout = findViewById(R.id.main_layout);
        tvContent = findViewById(R.id.tvContent);
        format = new DecimalFormat("###,###,###,###");

        if(onClickListener != null) {
            this.onClickListener = onClickListener;
            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("ToanNM", "mainLayout.setOnClickListener");
                }
            });
            tvContent.setOnClickListener(onClickListener);
        }
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if(type ==2){
            PieEntry pieEntry =(PieEntry) e;
            tvContent.setText(pieEntry.getLabel()+": "+Utils.convertStringMoney((long) pieEntry.getValue(),3)+" đ");
        }else {
            if(key.equals("source_customer")){
                tvContent.setText(months[(int)e.getX()] + "\n" + "Tổng: "+format.format(e.getY()) + " KH");

            }else{
                tvContent.setText(months[(int)e.getX()] + "\n" + "Tổng: "+format.format(e.getY()) + " đ");

            }
        }
//        tvContent.setText("x: " + xAxisValueFormatter.getFormattedValue(e.getX(), null) + ", y: " + format.format(e.getY()));

        if(onClickListener != null) {
            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("tusinh", "mainLayout.setOnClickListener");
                }
            });
            tvContent.setOnClickListener(onClickListener);
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
