package com.melon.tbook.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.melon.tbook.R;
import com.melon.tbook.adapter.RecordAdapter;
import com.melon.tbook.db.DBHelper;
import com.melon.tbook.db.Record;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView totalAmountTextView;
    private RecyclerView recordRecyclerView;
    private Button addRecordButton;
    private Button reportButton;
    private DBHelper dbHelper;
    private RecordAdapter recordAdapter;
    private Spinner monthSpinner, yearSpinner;
    private int selectMonth, selectYear;
    private TextView monthSummaryTextView;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        totalAmountTextView = findViewById(R.id.total_amount_text_view);
        recordRecyclerView = findViewById(R.id.record_recycler_view);
        addRecordButton = findViewById(R.id.add_record_button);
        reportButton = findViewById(R.id.report_button);
        monthSpinner = findViewById(R.id.month_spinner);
        yearSpinner = findViewById(R.id.year_spinner);
        monthSummaryTextView = findViewById(R.id.month_summary_text_view);

        dbHelper = new DBHelper(this);

        recordRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        addRecordButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddRecordActivity.class);
            startActivity(intent);
        });

        reportButton.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, ReportActivity.class);
            startActivity(intent);
        });


        recordAdapter = new RecordAdapter(new ArrayList<>());
        recordRecyclerView.setAdapter(recordAdapter);

        recordAdapter.setOnItemClickListener(recordId -> {
            dbHelper.deleteRecord(recordId);
            loadRecords();
        });


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
                loadRecords();
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
        loadRecords();
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadRecords();
    }


    private void loadRecords(){
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

        List<Record> allRecords = dbHelper.getAllRecords();
        List<Record> recordList = new ArrayList<>();

        for (Record record : allRecords){
            if(record.getDate().after(startDate) && record.getDate().before(endDate)){
                recordList.add(record);
            }
        }
        recordAdapter.setList(recordList);
        updateTotalAmount(recordList);
    }

    private void updateTotalAmount(List<Record> recordList) {
        double totalIncome = 0.0;
        double totalExpense = 0.0;
        double totalAmount = 0.0;
        for (Record record : dbHelper.getAllRecords()){
            if(record.getType().equals(getString(R.string.income))){
                totalIncome += record.getAmount();
            }else{
                totalExpense += record.getAmount();
            }
        }
        totalAmount = totalIncome - totalExpense;
        String totalAmountText = String.format(getString(R.string.total_amount), totalAmount);
        totalAmountTextView.setText(totalAmountText);


        double monthIncome = 0.0;
        double monthExpense = 0.0;

        for (Record record : recordList){
            if(record.getType().equals(getString(R.string.income))){
                monthIncome += record.getAmount();
            } else{
                monthExpense += record.getAmount();
            }
        }
        double monthTotal = monthIncome - monthExpense;
        String monthSummary = String.format("%d月总计：%.2f", selectMonth + 1, monthTotal);
        monthSummaryTextView.setText(monthSummary);

    }
}