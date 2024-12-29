package com.melon.tbook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.melon.tbook.R;
import com.melon.tbook.db.Record;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    private List<Record> recordList;
    private OnItemClickListener mItemClickListener;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public interface OnItemClickListener {
        void onItemDelete(int recordId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public RecordAdapter(List<Record> recordList) {
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record record = recordList.get(position);
        holder.typeTextView.setText(record.getType());
        holder.amountTextView.setText(String.format("%.2f", record.getAmount()));
        holder.categoryTextView.setText(record.getCategory());
        holder.remarkTextView.setText(record.getRemark());
        holder.dateTextView.setText(dateFormat.format(record.getDate()));

        //点击删除
        holder.deleteImageView.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemDelete(record.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView typeTextView;
        TextView amountTextView;
        TextView categoryTextView;
        TextView remarkTextView;
        TextView dateTextView;
        ImageView deleteImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            typeTextView = itemView.findViewById(R.id.item_type);
            amountTextView = itemView.findViewById(R.id.item_amount);
            categoryTextView = itemView.findViewById(R.id.item_category);
            remarkTextView = itemView.findViewById(R.id.item_remark);
            dateTextView = itemView.findViewById(R.id.item_date);
            deleteImageView = itemView.findViewById(R.id.item_delete);
        }
    }

    //刷新数据
    public void setList(List<Record> recordList){
        this.recordList = recordList;
        notifyDataSetChanged();
    }

}