package com.bimart.main.report;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.bimart.R;
import com.bimart.adapter.PopupWindowWithCheckAdapter;
import com.bimart.adapter.PopupWindowWithCheckFilterKeyAdapter;
import com.bimart.adapter.TabTitleAdapter;
import com.bimart.constant.Constants;
import com.bimart.lisenter.LoadJson;
import com.bimart.main.report.fragment.ProductWithCategoryFragment;
import com.bimart.main.report.fragment.ProductWithCountDashboardFragment;
import com.bimart.main.report.fragment.ProductWithCountFragment;
import com.bimart.main.report.fragment.ProductWithRevenueDashboardFragment;
import com.bimart.main.report.fragment.ProductWithRevenueFragment;
import com.bimart.main.report.model.ProductReport;
import com.bimart.model.FilterCommon;
import com.bimart.model.Product;
import com.bimart.model.Tab;
import com.bimart.utils.MyLog;
import com.bimart.utils.ShowPopup;
import com.bimart.utils.SinglePopupWindow;
import com.bimart.utils.Utils;
import com.bimart.views.FontTextView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.xxmassdeveloper.mpchartexample.MyAxisValueFormatter;
import com.xxmassdeveloper.mpchartexample.MyXAxisValueFormatter;
import com.xxmassdeveloper.mpchartexample.MyXAxisValueFormatterInPieChart;
import com.xxmassdeveloper.mpchartexample.XYMarkerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ReportDashboardActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    ImageView imageback, imageright1;
    FontTextView textviewtitle, ftvtonkho_ht, totalrevenuesell, totalrevenuereturn, totalorder, totalrefund;
    LinearLayout mainlayout_drashboardreport, layoutproductwithtab;
    RecyclerView tabreport;

    PopupWindow popupFilterWindow;
    PopupWindowWithCheckFilterKeyAdapter popupWindowWithCheckFilterKeyAdapter;
    ArrayList<FilterCommon> datafilter = new ArrayList();
    TabTitleAdapter tabTitleAdapter;
    BarChart barchart;
    PieChart piechart;
    int positionToday = 0, positiontab = -1;
    ArrayList<Fragment> fragments;
    ProductWithCountDashboardFragment productWithCountDashboardFragment;
    ProductWithRevenueDashboardFragment productWithRevenueDashboardFragment;
    public ArrayList<ProductReport> arrayListProductRevenue = new ArrayList();
    public ArrayList<ProductReport> arrayListProductCount = new ArrayList();

    LoadJson loadJson;
    JSONObject mainObject;
    protected Typeface mTfRegular;
    protected Typeface mTfLight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_dashboard);

        mTfRegular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");

        Anhxa();
        setHeader();
        initTab();
        initPieChart();
        mainObject = Constants.getJSONParams(ReportDashboardActivity.this);
        try {
            mainObject.put("from_date", "");
            mainObject.put("to_date", "");
            mainObject.put("type", "");
            mainObject.put("x_shop_id", "");
            mainObject.put("type_date", datafilter.get(positionToday).key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadData();
        MyOnClick();
    }

    private void initPieChart() {
        piechart.setUsePercentValues(true);
        piechart.getDescription().setEnabled(false);
        piechart.setExtraOffsets(5, 10, 5, 5);

        piechart.setDragDecelerationFrictionCoef(0.95f);

//        mChart.setCenterTextTypeface(mTfLight);
        piechart.setCenterText("Biểu đồ từng cửa hàng");

        piechart.setDrawHoleEnabled(true);
        piechart.setHoleColor(Color.WHITE);

        piechart.setTransparentCircleColor(Color.WHITE);
        piechart.setTransparentCircleAlpha(110);

        piechart.setHoleRadius(58f);
        piechart.setTransparentCircleRadius(61f);

        piechart.setDrawCenterText(true);

        piechart.setRotationAngle(0);
        // enable rotation of the chart by touch
        piechart.setRotationEnabled(true);
        piechart.setHighlightPerTapEnabled(true);

//         mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);
        MyXAxisValueFormatterInPieChart xAxisFormatter = new MyXAxisValueFormatterInPieChart();
        // add a selection listener
        final XYMarkerView mv = new XYMarkerView(this, xAxisFormatter, 2, null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
        mv.setChartView(piechart); // For bounds control
        piechart.setMarker(mv); // Set the marker to the chart

        piechart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.i("VAL SELECTED",
                        "Value: " + e.getY() + ", index: " + h.getX()
                                + ", DataSet index: " + h.getDataSetIndex());

//        if (tooltipView != null)
//            tooltipView.closeNow();

            }

            @Override
            public void onValueDeSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });

//        setDataPieChart(0, 0);

//        mChart.animateY(1400, Easing.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);


        Legend l = piechart.getLegend();
        l.setEnabled(false);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        // entry label styling
        piechart.setEntryLabelColor(Color.BLACK);
        piechart.setEntryLabelTextSize(12f);
    }

    private void initBarChart() {

//        BarDataSet set1;
//        if (barchart.getData() != null &&
//                barchart.getData().getDataSetCount() > 0) {
//            set1 = (BarDataSet) barchart.getData().getDataSetByIndex(0);
//            set1.setValues(null);
//            barchart.getData().notifyDataChanged();
//            barchart.notifyDataSetChanged();
//        }

        barchart.clear();
        barchart.notifyDataSetChanged();
//        barchart.invalidate();
//        barchart.moveViewTo(0, 0, YAxis.AxisDependency.LEFT);
        barchart.fitScreen();

        barchart.setOnChartValueSelectedListener(this);
        barchart.setDrawBarShadow(false);
        barchart.setDrawValueAboveBar(true);
        barchart.getDescription().setEnabled(false);
        barchart.setDrawGridBackground(false);
        barchart.setFitBars(false);
        barchart.setHighlightFullBarEnabled(false);
//        mChart.setDrawMarkers(false);
        barchart.setDrawGridBackground(false);

        barchart.setMaxVisibleValueCount(60);

        barchart.setPinchZoom(true);
        barchart.getAxisRight().setDrawGridLines(false);
        barchart.getAxisLeft().setDrawGridLines(false);

//        barchart.getXAxis().setDrawGridLines(false);
//        barchart.setDragEnabled(false); // Bật / tắt kéo (panning) cho biểu đồ.
//        barchart.setScaleXEnabled(false); //Bật / tắt chia tỷ lệ trên trục x.


        // mChart.setDrawYLabels(false);


        IAxisValueFormatter custom = new MyAxisValueFormatter();
//        ((MyAxisValueFormatter) custom).key = typereviewob != null ? typereviewob.key : "";

        YAxis leftAxis = barchart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
//        leftAxis.setDrawLimitLinesBehindData(false);
//        leftAxis.setDrawTopYLabelEntry(false);
//        leftAxis.setDrawAxisLine(false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
//        leftAxis.setSpaceBottom(5f);
//        leftAxis.setGridLineWidth(0); // hide gridview theo chieu ngang
//        leftAxis.setGridColor(Color.TRANSPARENT); // hide gridview theo chieu ngang
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        barchart.getAxisRight().setEnabled(false);


        Legend l = barchart.getLegend();
        l.setXOffset(10f);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(12f);
        l.setXEntrySpace(4f);
        l.setEnabled(false);

//        setData(4, 10000000);

//        barchart.setData(null);


    }

    private void loadData() {
        StringEntity entity = new StringEntity(mainObject.toString(), ContentType.APPLICATION_JSON);
        MyLog.Log("ReportDashboardActivity params: " + mainObject.toString());
        if (loadJson != null) {
            loadJson.cancelAllRequests(false);
        }
        loadJson = new LoadJson(ReportDashboardActivity.this, Constants.REPORT_REVENUE_SELL_POS_ORDER, entity, new LoadJson.onLoadJson() {
            @Override
            public void onLoading() {
                MyGone();
            }

            @Override
            public void onCompleted(JSONObject response) {
//                MyLog.Log("ReportDashboardActivity response: " + response.toString());
                try {
                    JSONObject result = response.getJSONObject("result");
                    int code = result.getInt("code");
                    if (code == 1) {
                        JSONObject data = result.getJSONObject("data");
                        if (data.has("tonkho_ht")) {
                            JSONArray tonkho_ht = data.getJSONArray("tonkho_ht");
                            if (tonkho_ht.length() > 0) {
                                Double tonkho = (Double) tonkho_ht.get(0);
                                long ton = (new Double(tonkho)).longValue();
                                ftvtonkho_ht.setText(ton + "  sản phẩm");
                            } else {
                                ftvtonkho_ht.setText(0 + "  sản phẩm");
                            }
                        }

                        if (data.has("count_pos_order")) {
                            JSONArray count_pos_order = data.getJSONArray("count_pos_order");
                            if (count_pos_order.length() > 0) {
                                int count_pos = (int) count_pos_order.get(0);
                                totalorder.setText(count_pos + " Hóa đơn");
                            } else {
                                totalorder.setText(0 + " Hóa đơn");
                            }
                        }
                        if (data.has("count_returns_order")) {
                            JSONArray count_returns_order = data.getJSONArray("count_returns_order");
                            if (count_returns_order.length() > 0) {
                                int count_returns = (int) count_returns_order.get(0);
                                totalrefund.setText(count_returns + " Phiếu trả");
                            } else {
                                totalrefund.setText(0 + " Phiếu trả");
                            }
                        }
                        if (data.has("total_revenue_sell")) {
                            long total_revenue_sell = data.getLong("total_revenue_sell");
                            totalrevenuesell.setText(Utils.convertStringMoney(total_revenue_sell, 3) + " đ");
                        }
                        if (data.has("total_revenue_return")) {
                            JSONArray total_revenue_return = data.getJSONArray("total_revenue_return");

                            if (total_revenue_return.length() > 0 && !total_revenue_return.get(0).toString().equals("null")) {
                                Double total_revenuereturn = (Double) total_revenue_return.get(0);
                                long total_return = (new Double(total_revenuereturn)).longValue();
                                totalrevenuereturn.setText(Utils.convertStringMoney(total_return, 3) + " đ");
                            } else {
                                totalrevenuereturn.setText(Utils.convertStringMoney(0, 3) + " đ");
                            }
                        }

                        if (data.has("list_value_x") && data.has("list_value_y")) {

                            JSONArray list_value_x = data.getJSONArray("list_value_x");
                            JSONArray list_value_y = data.getJSONArray("list_value_y");
                            // load data barchart

                            String[] arrayvaluex = new String[list_value_x.length()];
                            ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();


//                            barchart.notifyDataSetChanged();
//                            barchart.clear();
//                            barchart.invalidate();
                            if (list_value_x.length() > 0 && list_value_y.length() > 0) {
                                // init barchart in here to refresh barchart 100% ,truc oy khong keo dai gia tri am.
                                //  neu dat o tren dau, nhin truc oy keo dai gia tri am -> xau!!!
                                initBarChart();
                                for (int i = 0; i < list_value_x.length(); i++) {
                                    String valuex = list_value_x.get(i).toString();
                                    arrayvaluex[i] = valuex;
                                    String revenue = list_value_y.get(i).toString();
                                    yVals2.add(new BarEntry(i, Float.parseFloat(revenue)));
                                }
                                BarDataSet set1;

                                set1 = new BarDataSet(yVals2, "Doanh thu");
                                set1.setDrawIcons(false);
                                set1.setColor(Color.parseColor("#3399ff"));
                                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                                dataSets.add(set1);
                                BarData databarchart = new BarData(dataSets);
                                databarchart.setValueTextSize(10f);
                                databarchart.setValueTypeface(mTfLight);
                                if (list_value_x.length() < 4 && list_value_x.length() > 0) {
                                    databarchart.setBarWidth(0.2f);
                                } else if (list_value_x.length() >= 4 && list_value_x.length() < 10) {
                                    databarchart.setBarWidth(0.4f);
                                } else if (list_value_x.length() >= 10 && list_value_x.length() < 20) {
                                    databarchart.setBarWidth(0.7f);
                                } else if (list_value_x.length() >= 20) {
                                    databarchart.setBarWidth(0.9f);
                                }


                                if (arrayvaluex.length > 0) {
                                    IAxisValueFormatter xAxisFormatter = new MyXAxisValueFormatter(arrayvaluex); //mChart
                                    XAxis xAxis = barchart.getXAxis();
                                    xAxis.setLabelRotationAngle(-80);//  text in Ox rotate
                                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                    xAxis.setTypeface(mTfLight);
                                    xAxis.setDrawGridLines(false);
                                    xAxis.setLabelCount(7);
//                                    xAxis.setDrawAxisLine(false);
                                    xAxis.setGranularity(1); // only intervals of 1 day
//                                xAxis.setLabelCount(yVals2.size());
//                                xAxis.setCenterAxisLabels(true);
//                                xAxis.setAxisMinimum(1);
                                    xAxis.setValueFormatter(xAxisFormatter);

                                    final XYMarkerView mv = new XYMarkerView(ReportDashboardActivity.this, xAxisFormatter, 1, arrayvaluex,
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    MyLog.Log("XYMarkerView onClick -> " + v);

                                                }
                                            });
//                                    mv.key = typereviewob != null ? typereviewob.key : "";
                                    mv.setChartView(barchart); // For bounds control
                                    barchart.setMarker(mv);
                                }

                                barchart.setData(databarchart);
//                                if (typereviewob.key.equals("user") || typereviewob.key.equals("revenue_config")) {
//                                    barchart.setVisibleXRangeMaximum(7);
//                                } else {
                                barchart.setVisibleXRangeMaximum(15);
//                                }

//                            barchart.moveViewToX(10);
                                barchart.notifyDataSetChanged();
                                barchart.invalidate();
//                                namebarchart.setText("Biểu đồ " + typereviewob.value.toLowerCase());
//                                layout_barchart.setVisibility(View.VISIBLE);
//                                layout_namebarchart.setVisibility(View.VISIBLE);

                            } else {
                                barchart.clear();
                                barchart.notifyDataSetChanged();
                                barchart.invalidate();
                            }
                        }

                        if (data.has("list_session_name") && data.has("list_sell_session")) {
                            // load data piechart
                            JSONArray list_session = data.getJSONArray("list_session_name");
                            JSONArray list_sell_session = data.getJSONArray("list_sell_session");
                            ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
                            piechart.clear();
                            boolean isshowpiechart = true;
                            if (list_sell_session.length() > 0 && list_session.length() > 0) {
                                for (int i = 0; i < list_session.length(); i++) {
                                    String session = list_session.get(i).toString();
                                    String sell_session = list_sell_session.get(i).toString();
                                    Float sellf = Float.parseFloat(sell_session);
                                    if (sellf < 0) {
                                        isshowpiechart = false;
                                        break;
                                    } else {
                                        entries.add(new PieEntry(sellf, session));
                                    }
                                }
                                if (isshowpiechart) {
                                    PieDataSet dataSet = new PieDataSet(entries, "Ghi chú");
                                    dataSet.setDrawIcons(false);

                                    dataSet.setSliceSpace(3f);
                                    dataSet.setIconsOffset(new MPPointF(0, 40));
                                    dataSet.setSelectionShift(8f);
                                    // add a lot of colors
                                    ArrayList<Integer> colors = new ArrayList<Integer>();

                                    for (int c : ColorTemplate.VORDIPLOM_COLORS)
                                        colors.add(c);

                                    for (int c : ColorTemplate.JOYFUL_COLORS)
                                        colors.add(c);

                                    for (int c : ColorTemplate.COLORFUL_COLORS)
                                        colors.add(c);

                                    for (int c : ColorTemplate.LIBERTY_COLORS)
                                        colors.add(c);

                                    for (int c : ColorTemplate.PASTEL_COLORS)
                                        colors.add(c);

                                    colors.add(ColorTemplate.getHoloBlue());
                                    dataSet.setColors(colors);
                                    PieData piedata = new PieData(dataSet);
                                    piedata.setValueFormatter(new PercentFormatter());
                                    piedata.setValueTextSize(11f);
                                    piedata.setValueTextColor(Color.BLACK);
                                    piechart.setData(piedata);
                                    // undo all highlights
                                    piechart.highlightValues(null);
                                    piechart.invalidate();

                                }


                            }

                        }

                        if (data.has("list_product_revenue")) {
                            JSONArray list_product_revenue = data.getJSONArray("list_product_revenue");
                            arrayListProductRevenue.clear();
                            if (list_product_revenue.length() > 0) {
                                for (int i = 0; i < list_product_revenue.length(); i++) {
                                    JSONObject item_productrevenue = list_product_revenue.getJSONObject(i);
                                    JSONObject productjson = item_productrevenue.getJSONObject("product");
                                    Product product = new Product(
                                            Utils.checkIntNullorFalse(productjson.getString("id")),
                                            productjson.getString("name"),
                                            productjson.getString("x_default_code")
                                    );
                                    ProductReport productReport = new ProductReport(
                                            item_productrevenue.getString("image"),
                                            product,
                                            item_productrevenue.getLong("total_revenue")
                                    );
                                    arrayListProductRevenue.add(productReport);
                                }
                                layoutproductwithtab.setVisibility(View.VISIBLE);

                            }
                            if (positiontab == 0) {
                                productWithRevenueDashboardFragment.productWithRevenueAdapter.setData(arrayListProductRevenue);
                            }

//                            layoutproductwithtab.setVisibility(View.VISIBLE);
                        }
                        if (data.has("list_product_count")) {
                            JSONArray list_product_count = data.getJSONArray("list_product_count");
                            arrayListProductCount.clear();
                            if (list_product_count.length() > 0) {
                                for (int i = 0; i < list_product_count.length(); i++) {
                                    JSONObject item_productcount = list_product_count.getJSONObject(i);
                                    JSONObject productjson = item_productcount.getJSONObject("product");
                                    Product product = new Product(
                                            Utils.checkIntNullorFalse(productjson.getString("id")),
                                            productjson.getString("name"),
                                            productjson.getString("x_default_code")
                                    );
                                    ProductReport productReport = new ProductReport(
                                            item_productcount.getString("image"),
                                            product,
                                            0,
                                            item_productcount.getLong("total_count")
                                    );
                                    arrayListProductCount.add(productReport);

                                }
                                layoutproductwithtab.setVisibility(View.VISIBLE);
                            }
                            if (positiontab == 1) {
                                productWithCountDashboardFragment.productWithCountAdapter.setData(arrayListProductCount);
                            }
//                            layoutproductwithtab.setVisibility(View.VISIBLE);
                        }

                    } else {
                        new ShowPopup(ReportDashboardActivity.this).info(result.getString("message"), getString(R.string.error)).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error, boolean needToShowErrorDialog) {

            }
        });
        loadJson.loadData();

    }

    private void MyGone() {
        layoutproductwithtab.setVisibility(View.GONE);
    }

    private void initTab() {
        tabreport.setLayoutManager(new GridLayoutManager(ReportDashboardActivity.this, 2));
        ArrayList<Tab> tabs = new ArrayList();

        tabs.add(new Tab("Top 10 sp theo doanh thu", true));
        tabs.add(new Tab("Top 10 sp theo số lượng", false));

        tabTitleAdapter = new TabTitleAdapter(ReportDashboardActivity.this, tabs, new TabTitleAdapter.OnMainLayoutClickListener() {
            @Override
            public void OnMainLayoutCLick(int position) {
                positiontab = position;
                onItemViewPagerSelected(position);
            }
        });
        tabreport.setAdapter(tabTitleAdapter);
        positiontab = 0;
        replaceFragmentContainer(0);
    }

    private void onItemViewPagerSelected(int position) {
        replaceFragmentContainer(position);
        tabTitleAdapter.setSelected(position);
    }

    private void replaceFragmentContainer(int i) {
        initFragments();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayoutproduct, fragments.get(i));
        transaction.commit();
    }

    private void initFragments() {
        fragments = new ArrayList<Fragment>();

        productWithRevenueDashboardFragment = new ProductWithRevenueDashboardFragment();
        productWithCountDashboardFragment = new ProductWithCountDashboardFragment();

        fragments.add(productWithRevenueDashboardFragment);
        fragments.add(productWithCountDashboardFragment);

    }

    private void initFilter() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.popup_menu_marginright5, null, false);
        popupFilterWindow = SinglePopupWindow.getInstance().getPopupWindowWrapContent(popupView);
        popupFilterWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupFilterWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        final RecyclerView popupWindowRecyclerView = (RecyclerView) popupView.findViewById(R.id.recyclerView);
        popupWindowRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        popupWindowRecyclerView.setNestedScrollingEnabled(false);
        popupWindowRecyclerView.setLayoutManager(new LinearLayoutManager(ReportDashboardActivity.this));

        Utils.setupUI(mainlayout_drashboardreport, ReportDashboardActivity.this, new Utils.OnMainUIClick() {
            @Override
            public void onMainUIClick() {
                if (popupFilterWindow.isShowing() && popupFilterWindow != null) {
                    popupFilterWindow.dismiss();
                    SinglePopupWindow.getInstance().dismissAllPopupWindow();
                }
            }
        }, true);
        popupWindowWithCheckFilterKeyAdapter = new PopupWindowWithCheckFilterKeyAdapter(ReportDashboardActivity.this, datafilter, new PopupWindowWithCheckFilterKeyAdapter.OnItemClick() {
            @Override
            public void onItemClick(FilterCommon filterCommon) {
                if (filterCommon.key.equals("option")) {
                    startActivity(new Intent(ReportDashboardActivity.this, ReportManagerActivity.class));
                } else {
                    textviewtitle.setText(filterCommon.value);
                    try {
                        mainObject.put("type_date", filterCommon.key);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loadData();
                }

            }
        });
        popupWindowRecyclerView.setAdapter(popupWindowWithCheckFilterKeyAdapter);
        popupWindowWithCheckFilterKeyAdapter.setSelected(positionToday);
    }

    private void setHeader() {
        imageright1.setImageResource(R.mipmap.ic_filter);
        datafilter.add(new FilterCommon(1, "today", "Hôm nay"));
        datafilter.add(new FilterCommon(2, "yesterday", "Hôm qua"));
        datafilter.add(new FilterCommon(3, "last_week", "7 ngày trước"));
        datafilter.add(new FilterCommon(4, "this_month", "Tháng này"));
        datafilter.add(new FilterCommon(5, "last_month", "Tháng trước"));
        datafilter.add(new FilterCommon(6, "option", "Tùy chọn"));
        initFilter();
        imageright1.setVisibility(View.VISIBLE);
        textviewtitle.setText(datafilter.get(positionToday).value);
    }

    private void MyOnClick() {
        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imageright1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (datafilter.size() > 0) {
                    if (!popupFilterWindow.isShowing()) {
                        SinglePopupWindow.getInstance().dismissAllPopupWindow();
                        popupFilterWindow.showAsDropDown(imageright1);
                    } else {
                        popupFilterWindow.dismiss();
                    }
                }
            }
        });
    }

    private void Anhxa() {
        imageback = findViewById(R.id.imageback);
        imageright1 = findViewById(R.id.imageright1);
        textviewtitle = findViewById(R.id.textviewtitle);
        mainlayout_drashboardreport = findViewById(R.id.mainlayout_drashboardreport);
        ftvtonkho_ht = findViewById(R.id.ftvtonkho_ht);
        totalrevenuesell = findViewById(R.id.totalrevenuesell);
        totalrevenuereturn = findViewById(R.id.totalrevenuereturn);
        totalorder = findViewById(R.id.totalorder);
        totalrefund = findViewById(R.id.totalrefund);
        tabreport = findViewById(R.id.tabreport);
        barchart = findViewById(R.id.barchart);
        piechart = findViewById(R.id.piechart);
        layoutproductwithtab = findViewById(R.id.layoutproductwithtab);


    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onValueDeSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
