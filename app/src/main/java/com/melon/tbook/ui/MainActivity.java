package com.melon.tbook.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.melon.tbook.R;
import com.melon.tbook.adapter.RecordAdapter;
import com.melon.tbook.db.DBHelper;
import com.melon.tbook.db.Record;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView totalAmountTextView;
    private RecyclerView recordRecyclerView;
    private Button addRecordButton;
    private Button reportButton;
    private DBHelper dbHelper;
    private RecordAdapter recordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        totalAmountTextView = findViewById(R.id.total_amount_text_view);
        recordRecyclerView = findViewById(R.id.record_recycler_view);
        addRecordButton = findViewById(R.id.add_record_button);
        reportButton = findViewById(R.id.report_button);

        dbHelper = new DBHelper(this);

        recordRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadRecords();

        addRecordButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddRecordActivity.class);
            startActivity(intent);
        });

        reportButton.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, ReportActivity.class);
            startActivity(intent);
        });

        recordAdapter.setOnItemClickListener(recordId -> {
            dbHelper.deleteRecord(recordId);
            loadRecords();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecords();
    }

    private void loadRecords(){
        List<Record> recordList = dbHelper.getAllRecords();
        if(recordAdapter == null){
            recordAdapter = new RecordAdapter(recordList);
            recordRecyclerView.setAdapter(recordAdapter);
        }else {
            recordAdapter.setList(recordList);
        }
        updateTotalAmount();
    }

    private void updateTotalAmount() {
        double totalIncome = dbHelper.getTotalIncome();
        double totalExpense = dbHelper.getTotalExpense();
        double totalAmount = totalIncome - totalExpense;

        String totalAmountText = String.format(getString(R.string.total_amount), totalAmount);
        totalAmountTextView.setText(totalAmountText);
    }
}