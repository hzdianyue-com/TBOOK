package com.melon.tbook.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.melon.tbook.R;
import com.melon.tbook.adapter.CategoryAdapter;
import com.melon.tbook.db.DBHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageCategoryActivity extends AppCompatActivity {

    private EditText addCategoryEditText;
    private Button addCategoryButton;
    private RecyclerView categoryRecyclerView;
    private DBHelper dbHelper;
    private CategoryAdapter categoryAdapter;
    private RadioGroup categoryTypeRadioGroup;
    private RadioButton incomeCategoryButton, expenseCategoryButton;
    private String selectType = "收入";
    private Map<String,Integer> categoryIdMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);
        addCategoryEditText = findViewById(R.id.add_category_edit_text);
        addCategoryButton = findViewById(R.id.add_category_button);
        categoryRecyclerView = findViewById(R.id.category_recycler_view);
        categoryTypeRadioGroup = findViewById(R.id.category_type_radio_group);
        incomeCategoryButton = findViewById(R.id.income_category_button);
        expenseCategoryButton = findViewById(R.id.expense_category_button);

        dbHelper = new DBHelper(this);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadCategories();

        addCategoryButton.setOnClickListener(v -> {
            addCategory();
        });

        categoryTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.income_category_button) {
                selectType = "收入";
            } else if (checkedId == R.id.expense_category_button) {
                selectType = "支出";
            }
            loadCategories();

        });


        categoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(int categoryId) {
                dbHelper.deleteCategory(categoryId);
                loadCategories();
            }

            @Override
            public void onItemUpdate(int categoryId, String categoryName) {
                int row = dbHelper.updateCategory(categoryId, categoryName, selectType);
                if (row > 0) {
                    loadCategories();
                    Toast.makeText(ManageCategoryActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManageCategoryActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (incomeCategoryButton.isChecked()) {
            selectType = "收入";
        }

    }

    private void loadCategories() {
        List<String> categoryList = dbHelper.getAllCategories(selectType);
        categoryIdMap.clear();
        List<String> allCategories = dbHelper.getAllCategories(selectType);
        for (String categoryName : allCategories){
            int categoryId = getIdByCategoryName(categoryName);
            categoryIdMap.put(categoryName,categoryId);
        }

        if (categoryAdapter == null) {
            categoryAdapter = new CategoryAdapter(categoryList, selectType);
            categoryRecyclerView.setAdapter(categoryAdapter);
        } else {
            categoryAdapter.setList(categoryList, selectType);
        }


    }

    private int getIdByCategoryName(String categoryName){
        int id = -1;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT _id FROM categories where category_name = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{categoryName});

        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            cursor.close();
        }
        db.close();
        return id;
    }


    private void addCategory() {
        String categoryName = addCategoryEditText.getText().toString();
        if (categoryName.isEmpty()) {
            Toast.makeText(this, "分类名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = dbHelper.addCategory(categoryName, selectType);
        if (id > 0) {
            addCategoryEditText.setText("");
            loadCategories();
        } else {
            Toast.makeText(this, "添加失败，可能已存在", Toast.LENGTH_SHORT).show();
        }
    }


}