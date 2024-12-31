package com.melon.tbook.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.melon.tbook.R;
import com.melon.tbook.adapter.BorrowAdapter;
import com.melon.tbook.db.Borrow;
import com.melon.tbook.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class BorrowListActivity extends AppCompatActivity {

    private RecyclerView borrowRecyclerView;
    private DBHelper dbHelper;
    private BorrowAdapter borrowAdapter;
    private RadioGroup borrowTypeRadioGroup;
    private RadioButton borrowInRadioButton,borrowOutRadioButton;
    private String selectType = "借入";
    private Button addBorrowButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_list);

        borrowRecyclerView = findViewById(R.id.borrow_recycler_view);
        borrowTypeRadioGroup = findViewById(R.id.borrow_type_radio_group);
        borrowInRadioButton = findViewById(R.id.borrow_in_radio_button);
        borrowOutRadioButton = findViewById(R.id.borrow_out_radio_button);
        addBorrowButton = findViewById(R.id.add_borrow_button);

        dbHelper = new DBHelper(this);
        borrowRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        addBorrowButton.setOnClickListener(v -> {
            Intent intent = new Intent(BorrowListActivity.this, BorrowActivity.class);
            startActivity(intent);
        });

        borrowTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.borrow_in_radio_button) {
                selectType = "借入";
            } else if (checkedId == R.id.borrow_out_radio_button) {
                selectType = "借出";
            }
            loadBorrows();
        });

        borrowAdapter = new BorrowAdapter(new ArrayList<>());
        borrowRecyclerView.setAdapter(borrowAdapter);

        borrowAdapter.setOnItemClickListener(borrowId -> {
            dbHelper.deleteBorrow(borrowId);
            loadBorrows();
        });
        if (borrowInRadioButton.isChecked()){
            selectType = "借入";
        }

        loadBorrows();

    }
    private void loadBorrows() {
        List<Borrow> borrowList = dbHelper.getAllBorrows(selectType);
        borrowAdapter.setList(borrowList);
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadBorrows();
    }
}