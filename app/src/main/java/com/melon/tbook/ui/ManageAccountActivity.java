package com.melon.tbook.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.melon.tbook.R;
import com.melon.tbook.adapter.AccountAdapter;
import com.melon.tbook.db.DBHelper;

import java.util.List;

public class ManageAccountActivity extends AppCompatActivity {

    private EditText addAccountEditText;
    private Button addAccountButton;
    private RecyclerView accountRecyclerView;
    private DBHelper dbHelper;
    private AccountAdapter accountAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);
        addAccountEditText = findViewById(R.id.add_account_edit_text);
        addAccountButton = findViewById(R.id.add_account_button);
        accountRecyclerView = findViewById(R.id.account_recycler_view);

        dbHelper = new DBHelper(this);
        accountRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadAccounts();

        addAccountButton.setOnClickListener(v -> {
            addAccount();
        });

        accountAdapter.setOnItemClickListener(new AccountAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(int accountId) {
                dbHelper.deleteAccount(accountId);
                loadAccounts();
            }

            @Override
            public void onItemUpdate(int accountId, String accountName) {
                int row = dbHelper.updateAccount(accountId,accountName);
                if(row > 0){
                    loadAccounts();
                } else {
                    Toast.makeText(ManageAccountActivity.this,"更新失败",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadAccounts(){
        List<String> accountList = dbHelper.getAllAccounts();
        if(accountAdapter == null){
            accountAdapter = new AccountAdapter(accountList);
            accountRecyclerView.setAdapter(accountAdapter);
        }else{
            accountAdapter.setList(accountList);
        }

    }

    private void addAccount(){
        String accountName = addAccountEditText.getText().toString();
        if(accountName.isEmpty()){
            Toast.makeText(this,"账户名称不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        long id = dbHelper.addAccount(accountName);
        if(id > 0){
            addAccountEditText.setText("");
            loadAccounts();
        } else {
            Toast.makeText(this,"添加失败，可能已存在",Toast.LENGTH_SHORT).show();
        }
    }


}