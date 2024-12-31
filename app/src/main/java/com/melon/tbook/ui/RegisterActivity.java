package com.melon.tbook.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.melon.tbook.R;
import com.melon.tbook.db.DBHelper;
import com.melon.tbook.db.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.register_username_edit_text);
        passwordEditText = findViewById(R.id.register_password_edit_text);
        registerButton = findViewById(R.id.register_button);
        dbHelper = new DBHelper(this);

        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dbHelper.getUserByUserName(username) != null){
            Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show();
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        long insertId = dbHelper.addUser(user);
        if(insertId > 0){
            Toast.makeText(this,"注册成功",Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this,"注册失败",Toast.LENGTH_SHORT).show();
        }
    }

}