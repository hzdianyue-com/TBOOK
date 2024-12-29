package com.melon.tbook.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.melon.tbook.R;
import com.melon.tbook.db.DBHelper;
import com.melon.tbook.db.Record;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddRecordActivity extends AppCompatActivity {

    private RadioGroup accountTypeRadioGroup;
    private RadioButton incomeRadioButton, expenseRadioButton;
    private EditText amountEditText, remarkEditText;
    private Spinner categorySpinner;
    private Button saveButton, selectDateButton;
    private DBHelper dbHelper;
    private TextView dateTextView;
    private Calendar calendar;
    private Date selectDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        accountTypeRadioGroup = findViewById(R.id.account_type_radio_group);
        incomeRadioButton = findViewById(R.id.income_radio_button);
        expenseRadioButton = findViewById(R.id.expense_radio_button);
        amountEditText = findViewById(R.id.amount_edit_text);
        categorySpinner = findViewById(R.id.category_spinner);
        remarkEditText = findViewById(R.id.remark_edit_text);
        saveButton = findViewById(R.id.save_button);
        dateTextView = findViewById(R.id.date_text_view);
        selectDateButton = findViewById(R.id.select_date_button);

        dbHelper = new DBHelper(this);
        calendar = Calendar.getInstance();
        selectDate = calendar.getTime();
        dateTextView.setText(dateFormat.format(selectDate));



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);


        saveButton.setOnClickListener(v -> saveRecord());

        selectDateButton.setOnClickListener(v -> showDatePickerDialog());

    }

    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            selectDate = calendar.getTime();
            dateTextView.setText(dateFormat.format(selectDate));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void saveRecord() {
        String type = incomeRadioButton.isChecked() ? getString(R.string.income) : getString(R.string.expense);
        String amountStr = amountEditText.getText().toString();
        String category = categorySpinner.getSelectedItem().toString();
        String remark = remarkEditText.getText().toString();

        if(amountStr.isEmpty()){
            Toast.makeText(this, getString(R.string.error_amount), Toast.LENGTH_SHORT).show();
            return;
        }
        double amount = Double.parseDouble(amountStr);
        Record record = new Record(type, amount, category, remark, selectDate);
        long insertId = dbHelper.addRecord(record);

        if (insertId > 0) {
            finish();
        } else {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }


}