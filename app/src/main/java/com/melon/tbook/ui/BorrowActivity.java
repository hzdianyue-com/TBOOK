package com.melon.tbook.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.melon.tbook.R;
import com.melon.tbook.db.Borrow;
import com.melon.tbook.db.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BorrowActivity extends AppCompatActivity {
    private RadioGroup borrowTypeRadioGroup;
    private RadioButton borrowInRadioButton,borrowOutRadioButton;
    private EditText borrowerEditText,amountEditText;
    private Button addBorrowButton,selectDateButton;
    private DBHelper dbHelper;
    private TextView borrowDateTextView;
    private Calendar calendar;
    private Date selectDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);
        borrowTypeRadioGroup = findViewById(R.id.borrow_type_radio_group);
        borrowInRadioButton = findViewById(R.id.borrow_in_radio_button);
        borrowOutRadioButton = findViewById(R.id.borrow_out_radio_button);
        borrowerEditText = findViewById(R.id.borrower_edit_text);
        amountEditText = findViewById(R.id.borrow_amount_edit_text);
        addBorrowButton = findViewById(R.id.add_borrow_button);
        borrowDateTextView = findViewById(R.id.borrow_date_text_view);
        selectDateButton = findViewById(R.id.select_borrow_date_button);

        dbHelper = new DBHelper(this);
        calendar = Calendar.getInstance();
        selectDate = calendar.getTime();
        borrowDateTextView.setText(dateFormat.format(selectDate));


        addBorrowButton.setOnClickListener(v -> addBorrow());
        selectDateButton.setOnClickListener(v -> showDatePickerDialog());

    }
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            selectDate = calendar.getTime();
            borrowDateTextView.setText(dateFormat.format(selectDate));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void addBorrow() {
        String type = borrowInRadioButton.isChecked() ? "借入" : "借出";
        String amountStr = amountEditText.getText().toString();
        String borrower = borrowerEditText.getText().toString();

        if(amountStr.isEmpty()){
            Toast.makeText(this,"金额不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        Borrow borrow = new Borrow(type,amount,borrower,selectDate);
        long insertId = dbHelper.addBorrow(borrow);
        if(insertId > 0){
            borrowerEditText.setText("");
            amountEditText.setText("");
            Toast.makeText(this,"添加成功",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"添加失败",Toast.LENGTH_SHORT).show();
        }
    }


}