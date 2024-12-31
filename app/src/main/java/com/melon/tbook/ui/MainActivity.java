package com.melon.tbook.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.melon.tbook.R;
import com.melon.tbook.adapter.AccountTotalAdapter;
import com.melon.tbook.db.Borrow;
import com.melon.tbook.db.DBHelper;
import com.melon.tbook.db.Record;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView totalAmountTextView;
    private RecyclerView accountTotalRecyclerView;
    private Button addRecordButton;
    private Button reportButton;
    private Button recordListButton;
    private Button borrowManageButton;
    private DBHelper dbHelper;
    private TextView monthSummaryTextView;
    private AccountTotalAdapter accountTotalAdapter;
    private TextView borrowInTextView;
    private TextView borrowOutTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        totalAmountTextView = findViewById(R.id.total_amount_text_view);
        accountTotalRecyclerView = findViewById(R.id.account_total_recycler_view);
        addRecordButton = findViewById(R.id.add_record_button);
        reportButton = findViewById(R.id.report_button);
        recordListButton = findViewById(R.id.record_list_button);
        monthSummaryTextView = findViewById(R.id.month_summary_text_view);
        borrowInTextView = findViewById(R.id.total_borrow_in_text_view);
        borrowOutTextView = findViewById(R.id.total_borrow_out_text_view);
        borrowManageButton = findViewById(R.id.borrow_manage_button);


        dbHelper = new DBHelper(this);
        accountTotalRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        addRecordButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddRecordActivity.class);
            startActivity(intent);
        });

        reportButton.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, ReportActivity.class);
            startActivity(intent);
        });

        recordListButton.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, RecordListActivity.class);
            startActivity(intent);
        });

        borrowManageButton.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, BorrowListActivity.class);
            startActivity(intent);
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        updateTotalAmount();
    }
    private void updateTotalAmount() {
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

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;

        double monthIncome = 0.0;
        double monthExpense = 0.0;

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

        List<Record> allRecords = dbHelper.getAllRecords();
        List<Record> recordList = new ArrayList<>();

        for (Record record : allRecords){
            if(record.getDate().after(startDate) && record.getDate().before(endDate)){
                recordList.add(record);
            }
        }


        for (Record record : recordList){
            if(record.getType().equals(getString(R.string.income))){
                monthIncome += record.getAmount();
            } else{
                monthExpense += record.getAmount();
            }
        }
        double monthTotal = monthIncome - monthExpense;
        String monthSummary = String.format("%d月总计：%.2f", month, monthTotal);
        monthSummaryTextView.setText(monthSummary);
        loadAccountTotalList();
        loadBorrowTotal();

    }


    private void loadAccountTotalList(){
        Map<String, AccountTotalAdapter.AccountInfo> accountTotal = getAccountTotal();
        List<Map.Entry<String, AccountTotalAdapter.AccountInfo>> entryList = new ArrayList<>(accountTotal.entrySet());
        if(accountTotalAdapter == null){
            accountTotalAdapter = new AccountTotalAdapter(entryList);
            accountTotalRecyclerView.setAdapter(accountTotalAdapter);
        } else {
            accountTotalAdapter.setList(entryList);
        }
    }

    private void loadBorrowTotal(){
        double totalBorrowIn = 0.0;
        double totalBorrowOut = 0.0;
        List<Borrow> borrows = dbHelper.getAllBorrows();
        for (Borrow borrow: borrows){
            if(borrow.getType().equals("借入")){
                totalBorrowIn += borrow.getAmount();
            } else{
                totalBorrowOut += borrow.getAmount();
            }
        }
        borrowInTextView.setText(String.format("%.2f",totalBorrowIn));
        borrowOutTextView.setText(String.format("%.2f",totalBorrowOut));
    }


    private Map<String, AccountTotalAdapter.AccountInfo> getAccountTotal(){
        Map<String,AccountTotalAdapter.AccountInfo> accountTotal = new HashMap<>();
        List<Record> allRecords = dbHelper.getAllRecords();
        for(Record record : allRecords){
            String accountName = record.getAccount();
            double amount = record.getAmount();
            if (accountName == null){
                accountName = "默认账户";
            }
            double income = 0.0;
            double expense = 0.0;
            if(record.getType().equals(getString(R.string.income))){
                income = amount;
            } else {
                expense = amount;
            }
            AccountTotalAdapter.AccountInfo accountInfo = accountTotal.getOrDefault(accountName,new AccountTotalAdapter.AccountInfo(0,0,0));
            accountInfo.setIncome(accountInfo.getIncome() + income);
            accountInfo.setExpense(accountInfo.getExpense() + expense);
            accountInfo.setTotal(accountInfo.getTotal() + income - expense);
            accountTotal.put(accountName,accountInfo);

        }
        return accountTotal;
    }
}