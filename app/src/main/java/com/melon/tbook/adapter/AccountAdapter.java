package com.melon.tbook.adapter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.melon.tbook.R;
import com.melon.tbook.db.DBHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

    private List<String> accountList;
    private List<String> defaultAccounts = Arrays.asList("默认", "网络账户", "理财账户", "银行卡账户", "支付宝", "微信钱包");
    private Map<String, Integer> accountIdMap = new HashMap<>();

    private OnItemClickListener mItemClickListener;


    public interface OnItemClickListener {
        void onItemDelete(int accountId);
        void onItemUpdate(int accountId, String accountName);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }


    public AccountAdapter(List<String> accountList) {
        this.accountList = accountList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String accountName = accountList.get(position);
        holder.accountEditText.setText(accountName);
        DBHelper dbHelper = new DBHelper(holder.itemView.getContext());
        int categoryId = getIdByAccountName(accountName, dbHelper);
        accountIdMap.put(accountName,categoryId);

        if (defaultAccounts.contains(accountName)) {
            holder.deleteImageView.setVisibility(View.GONE);
            holder.editImageView.setVisibility(View.GONE);
            holder.accountEditText.setEnabled(false);
        } else {
            holder.deleteImageView.setVisibility(View.VISIBLE);
            holder.editImageView.setVisibility(View.VISIBLE);
            holder.accountEditText.setEnabled(true);

            // 点击删除
            holder.deleteImageView.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemDelete(position + 1);
                }
            });

            holder.editImageView.setOnClickListener(v -> {
                String updateAccountName = holder.accountEditText.getText().toString();
                if (mItemClickListener != null) {
                    mItemClickListener.onItemUpdate(position + 1, updateAccountName);
                }

            });

            holder.accountEditText.addTextChangedListener(new TextWatcher() {
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

    private int getIdByAccountName(String categoryName,DBHelper dbHelper){
        int id = -1;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT _id FROM accounts where account_name = ?";
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
        return accountList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        EditText accountEditText;
        ImageView deleteImageView;
        ImageView editImageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            accountEditText = itemView.findViewById(R.id.item_account_edit_text);
            deleteImageView = itemView.findViewById(R.id.item_delete_account);
            editImageView = itemView.findViewById(R.id.item_edit_account);
        }
    }

    public void setList(List<String> accountList){
        this.accountList = accountList;
        notifyDataSetChanged();
    }

}