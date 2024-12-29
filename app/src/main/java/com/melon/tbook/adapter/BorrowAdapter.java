package com.melon.tbook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.melon.tbook.R;
import com.melon.tbook.db.Borrow;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BorrowAdapter extends RecyclerView.Adapter<BorrowAdapter.ViewHolder> {
    private List<Borrow> borrowList;
    private OnItemClickListener mItemClickListener;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());


    public BorrowAdapter(List<Borrow> borrowList) {
        this.borrowList = borrowList;
    }

    public interface OnItemClickListener {
        void onItemDelete(int borrowId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_borrow, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Borrow borrow = borrowList.get(position);
        holder.borrowType.setText(borrow.getType());
        holder.borrowAmount.setText(String.format("%.2f",borrow.getAmount()));
        holder.borrower.setText(borrow.getBorrower());
        holder.borrowDate.setText(dateFormat.format(borrow.getBorrowDate()));

        //点击删除
        holder.deleteImageView.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemDelete(borrow.getId());
            }
        });


    }

    @Override
    public int getItemCount() {
        return borrowList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView borrowType;
        TextView borrowAmount;
        TextView borrower;
        TextView borrowDate;
        ImageView deleteImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            borrowType = itemView.findViewById(R.id.item_borrow_type);
            borrowAmount = itemView.findViewById(R.id.item_borrow_amount);
            borrower = itemView.findViewById(R.id.item_borrower);
            borrowDate = itemView.findViewById(R.id.item_borrow_date);
            deleteImageView = itemView.findViewById(R.id.item_delete_borrow);

        }
    }

    public void setList(List<Borrow> borrowList){
        this.borrowList = borrowList;
        notifyDataSetChanged();
    }
}