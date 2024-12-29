package com.melon.tbook.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.melon.tbook.R;
import com.melon.tbook.db.DBHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<String> categoryList;
    private OnItemClickListener mItemClickListener;
    private String type;
    private List<String> defaultCategories = Arrays.asList("工资薪水", "副业收入", "投资收益", "红包礼金", "奖金", "租金收入", "其他收入", "生活消费", "住房支出", "交通支出", "通讯支出", "保险", "娱乐休闲", "教育支出", "医疗支出", "储蓄投资", "其他支出");
    private Map<String, Integer> categoryIdMap = new HashMap<>();


    public interface OnItemClickListener {
        void onItemDelete(int categoryId);

        void onItemUpdate(int categoryId, String categoryName);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }


    public CategoryAdapter(List<String> categoryList, String type) {
        this.categoryList = categoryList;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String categoryName = categoryList.get(position);
        holder.categoryEditText.setText(categoryName);
        DBHelper dbHelper = new DBHelper(holder.itemView.getContext());
        int categoryId = getIdByCategoryName(categoryName, dbHelper);
        categoryIdMap.put(categoryName,categoryId);

        if (defaultCategories.contains(categoryName)) {
            holder.deleteImageView.setVisibility(View.GONE);
            holder.editImageView.setVisibility(View.GONE);
            holder.categoryEditText.setEnabled(false);
        } else {
            holder.deleteImageView.setVisibility(View.VISIBLE);
            holder.editImageView.setVisibility(View.VISIBLE);
            holder.categoryEditText.setEnabled(true);
            // 点击删除
            holder.deleteImageView.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemDelete(categoryId);
                }
            });

            holder.editImageView.setOnClickListener(v -> {
                String updateCategoryName = holder.categoryEditText.getText().toString();
                if (mItemClickListener != null) {
                    mItemClickListener.onItemUpdate(categoryId, updateCategoryName);
                }

            });

            holder.categoryEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }


    }

    private int getIdByCategoryName(String categoryName,DBHelper dbHelper){
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

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        EditText categoryEditText;
        ImageView deleteImageView;
        ImageView editImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryEditText = itemView.findViewById(R.id.item_category_edit_text);
            deleteImageView = itemView.findViewById(R.id.item_delete_category);
            editImageView = itemView.findViewById(R.id.item_edit_category);
        }
    }

    public void setList(List<String> categoryList, String type) {
        this.categoryList = categoryList;
        this.type = type;
        notifyDataSetChanged();
    }
}