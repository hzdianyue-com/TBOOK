package com.melon.tbook.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.melon.tbook.R;
import com.melon.tbook.db.DBHelper;
import com.melon.tbook.db.Record;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    private Button monthlyReportButton, yearlyReportButton;
    private PieChart pieChart;
    private DBHelper dbHelper;
    private TextView reportTitleTextView;
    private TextView noDataTextView;
    private  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        monthlyReportButton = findViewById(R.id.monthly_report_button);
        yearlyReportButton = findViewById(R.id.yearly_report_button);
        pieChart = findViewById(R.id.pie_chart);
        reportTitleTextView = findViewById(R.id.report_title);
        noDataTextView = findViewById(R.id.report_no_data);

        dbHelper = new DBHelper(this);
        monthlyReportButton.setOnClickListener(v -> generateMonthlyReport());
        yearlyReportButton.setOnClickListener(v -> generateYearlyReport());

        generateMonthlyReport();
    }

    private void generateMonthlyReport() {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        String month = dateFormat.format(now);
        reportTitleTextView.setText(month + "  报表");

        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.DAY_OF_MONTH,1);
        startCal.set(Calendar.HOUR_OF_DAY,0);
        startCal.set(Calendar.MINUTE,0);
        startCal.set(Calendar.SECOND,0);
        Date startDate = startCal.getTime();

        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.DAY_OF_MONTH,endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
        endCal.set(Calendar.HOUR_OF_DAY,23);
        endCal.set(Calendar.MINUTE,59);
        endCal.set(Calendar.SECOND,59);
        Date endDate = endCal.getTime();

        generateReport(startDate,endDate);
    }

    private void generateYearlyReport() {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        String year = yearFormat.format(now);
        reportTitleTextView.setText(year + "  报表");
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.MONTH,0);
        startCal.set(Calendar.DAY_OF_MONTH,1);
        startCal.set(Calendar.HOUR_OF_DAY,0);
        startCal.set(Calendar.MINUTE,0);
        startCal.set(Calendar.SECOND,0);
        Date startDate = startCal.getTime();


        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.MONTH,11);
        endCal.set(Calendar.DAY_OF_MONTH,endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
        endCal.set(Calendar.HOUR_OF_DAY,23);
        endCal.set(Calendar.MINUTE,59);
        endCal.set(Calendar.SECOND,59);
        Date endDate = endCal.getTime();

        generateReport(startDate,endDate);
    }

    private void generateReport(Date startDate, Date endDate) {
        List<Record> allRecords = dbHelper.getAllRecords();
        List<Record> recordList = new ArrayList<>();

        for (Record record : allRecords){
            if(record.getDate().after(startDate) && record.getDate().before(endDate)){
                recordList.add(record);
            }
        }

        Map<String, Double> categoryAmounts = new HashMap<>();
        for (Record record : recordList) {
            if (record.getType().equals(getString(R.string.expense))) { // 只统计支出
                String category = record.getCategory();
                double amount = record.getAmount();
                categoryAmounts.put(category, categoryAmounts.getOrDefault(category, 0.0) + amount);
            }
        }

        if(categoryAmounts.isEmpty()){
            pieChart.setVisibility(View.GONE);
            noDataTextView.setVisibility(View.VISIBLE);
        }else{
            pieChart.setVisibility(View.VISIBLE);
            noDataTextView.setVisibility(View.GONE);
            setupPieChart(categoryAmounts);
        }
    }

    private void setupPieChart(Map<String, Double> categoryAmounts) {
        List<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryAmounts.entrySet()) {
            pieEntries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("消费分析");
        pieChart.setDrawEntryLabels(false); // 不显示标签
        pieChart.invalidate();
    }
}