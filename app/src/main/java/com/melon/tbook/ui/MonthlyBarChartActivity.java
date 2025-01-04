package com.melon.tbook.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
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

public class MonthlyBarChartActivity extends AppCompatActivity {
    private CombinedChart barChart;
    private DBHelper dbHelper;
    private TextView reportTitleTextView;
    private TextView noDataTextView;
    private Spinner yearSpinner;
    private int selectYear;
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_bar_chart);
        barChart = findViewById(R.id.combined_chart);
        reportTitleTextView = findViewById(R.id.report_title);
        noDataTextView = findViewById(R.id.report_no_data);
        yearSpinner = findViewById(R.id.year_spinner);
        dbHelper = new DBHelper(this);

        initYearSpinner();

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectYear = Integer.parseInt(parent.getItemAtPosition(position).toString());
                generateMonthlyReport();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        generateMonthlyReport();
    }
    private void generateMonthlyReport() {
        String year = String.valueOf(selectYear);
        reportTitleTextView.setText(year + "  收支汇总");
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.YEAR, selectYear);
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

        List<Record> allRecords = dbHelper.getAllRecords();
        List<Record> recordList = new ArrayList<>();

        for (Record record : allRecords){
            if(record.getDate().after(startDate) && record.getDate().before(endDate)){
                recordList.add(record);
            }
        }
        Map<Integer, Double> monthlyIncome = new HashMap<>();
        Map<Integer,Double> monthlyExpense = new HashMap<>();
        Map<Integer,Double> monthlyTotal = new HashMap<>();
        for(Record record : recordList){
            Calendar recordCalendar = Calendar.getInstance();
            recordCalendar.setTime(record.getDate());
            int month = recordCalendar.get(Calendar.MONTH) + 1;
            double amount = record.getAmount();
            if (record.getType().equals(getString(R.string.income))) {
                monthlyIncome.put(month,monthlyIncome.getOrDefault(month,0.0) + amount);
                monthlyTotal.put(month,monthlyTotal.getOrDefault(month,0.0) + amount);
            } else{
                monthlyExpense.put(month, monthlyExpense.getOrDefault(month,0.0) + amount);
                monthlyTotal.put(month,monthlyTotal.getOrDefault(month,0.0) - amount);
            }
        }

        if (monthlyIncome.isEmpty() && monthlyExpense.isEmpty()) {
            barChart.setVisibility(View.GONE);
            noDataTextView.setVisibility(View.VISIBLE);
        } else {
            barChart.setVisibility(View.VISIBLE);
            noDataTextView.setVisibility(View.GONE);
            setupBarChart(monthlyIncome,monthlyExpense,monthlyTotal);
        }


    }
    private void setupBarChart(Map<Integer, Double> monthlyIncome, Map<Integer, Double> monthlyExpense, Map<Integer, Double> monthlyTotal) {
        List<BarEntry> barEntriesIncome = new ArrayList<>();
        List<BarEntry> barEntriesExpense = new ArrayList<>();
        List<Entry> lineEntries = new ArrayList<>();

        for(int i = 1; i <= 12; i++){
            float income =  monthlyIncome.getOrDefault(i,0.0).floatValue();
            float expense =  monthlyExpense.getOrDefault(i,0.0).floatValue();
            float total = monthlyTotal.getOrDefault(i,0.0).floatValue();
            barEntriesIncome.add(new BarEntry(i, income));
            barEntriesExpense.add(new BarEntry(i, -expense));
            lineEntries.add(new Entry(i,total));
        }

        BarDataSet dataSetIncome = new BarDataSet(barEntriesIncome,"收入");
        dataSetIncome.setColor(ColorTemplate.MATERIAL_COLORS[0]);
        dataSetIncome.setDrawValues(false);


        BarDataSet dataSetExpense = new BarDataSet(barEntriesExpense,"支出");
        dataSetExpense.setColor(ColorTemplate.MATERIAL_COLORS[2]);
        dataSetExpense.setDrawValues(false);

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "收支汇总");
        lineDataSet.setColor(Color.BLACK);
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setDrawValues(false);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

        BarData barData = new BarData(dataSetIncome, dataSetExpense);
        LineData lineData = new LineData(lineDataSet);


        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(lineData);

        barChart.setData(combinedData);

        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setHighlightFullBarEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(-100);

        XAxis xAxis =  barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(0.5f);
        xAxis.setAxisMaximum(12.5f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int)value) + "月";
            }
        });

        barChart.getAxisRight().setEnabled(true);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().setAxisMinimum(-100);
        barChart.getAxisLeft().addLimitLine(new com.github.mikephil.charting.components.LimitLine(0, "0"));
        barChart.getAxisRight().addLimitLine(new com.github.mikephil.charting.components.LimitLine(0, "0"));
        barChart.invalidate();
    }

}