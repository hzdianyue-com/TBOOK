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

import com.melon.tbook.R;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

    private List<String> accountList;
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

        // 点击删除
        holder.deleteImageView.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemDelete(position + 1);
            }
        });

        holder.editImageView.setOnClickListener(v -> {
            String updateAccountName = holder.accountEditText.getText().toString();
            if(mItemClickListener != null){
                mItemClickListener.onItemUpdate(position + 1,updateAccountName);
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