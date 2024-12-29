package com.melon.tbook.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
    private Spinner monthSpinner, yearSpinner;
    private int selectMonth, selectYear;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
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
        monthSpinner = findViewById(R.id.month_spinner);
        yearSpinner = findViewById(R.id.year_spinner);

        dbHelper = new DBHelper(this);
        monthlyReportButton.setOnClickListener(v -> generateMonthlyReport());
        yearlyReportButton.setOnClickListener(v -> generateYearlyReport());


        initYearSpinner();

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectYear = Integer.parseInt(parent.getItemAtPosition(position).toString());
                initMonthSpinner(selectYear);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectMonth = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        generateMonthlyReport();
    }

    private void initYearSpinner() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        List<String> yearList = new ArrayList<>();
        for (int i = currentYear - 10; i <= currentYear + 10; i++) {
            yearList.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);
        int index = yearList.indexOf(String.valueOf(currentYear));
        yearSpinner.setSelection(index);
        selectYear = currentYear;
        initMonthSpinner(currentYear);

    }


    private void initMonthSpinner(int year){
        monthSpinner.setVisibility(View.VISIBLE);
        List<String> monthList = new ArrayList<>();
        for(int i = 1; i <= 12; i++){
            monthList.add(String.format("%02d",i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter);
        Calendar calendar = Calendar.getInstance();
        int index = calendar.get(Calendar.MONTH);
        monthSpinner.setSelection(index);
        selectMonth = index;
    }


    private void generateMonthlyReport() {
        String month =  String.format("%d-%02d",selectYear,selectMonth + 1);
        reportTitleTextView.setText(month + "  报表");

        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.YEAR,selectYear);
        startCal.set(Calendar.MONTH,selectMonth);
        startCal.set(Calendar.DAY_OF_MONTH,1);
        startCal.set(Calendar.HOUR_OF_DAY,0);
        startCal.set(Calendar.MINUTE,0);
        startCal.set(Calendar.SECOND,0);
        Date startDate = startCal.getTime();

        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.YEAR,selectYear);
        endCal.set(Calendar.MONTH,selectMonth);
        endCal.set(Calendar.DAY_OF_MONTH,endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
        endCal.set(Calendar.HOUR_OF_DAY,23);
        endCal.set(Calendar.MINUTE,59);
        endCal.set(Calendar.SECOND,59);
        Date endDate = endCal.getTime();

        generateReport(startDate,endDate);
    }

    private void generateYearlyReport() {
        String year = String.valueOf(selectYear);
        reportTitleTextView.setText(year + "  报表");
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.YEAR,selectYear);
        startCal.set(Calendar.MONTH,0);
        startCal.set(Calendar.DAY_OF_MONTH,1);
        startCal.set(Calendar.HOUR_OF_DAY,0);
        startCal.set(Calendar.MINUTE,0);
        startCal.set(Calendar.SECOND,0);
        Date startDate = startCal.getTime();


        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.YEAR,selectYear);
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