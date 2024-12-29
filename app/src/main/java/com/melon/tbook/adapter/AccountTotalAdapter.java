package com.melon.tbook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.melon.tbook.R;

import java.util.List;
import java.util.Map;

public class AccountTotalAdapter extends RecyclerView.Adapter<AccountTotalAdapter.ViewHolder> {
    private List<Map.Entry<String, AccountInfo>> accountTotals;

    public AccountTotalAdapter(List<Map.Entry<String, AccountInfo>> accountTotals) {
        this.accountTotals = accountTotals;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_total, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map.Entry<String, AccountInfo> entry = accountTotals.get(position);
        String accountName = entry.getKey();
        double income = entry.getValue().getIncome();
        double expense = entry.getValue().getExpense();
        double total = entry.getValue().getTotal();

        holder.accountName.setText(accountName);
        holder.accountIncome.setText(String.format("收入: %.2f", income));
        holder.accountExpense.setText(String.format("支出: %.2f", expense));
        holder.accountTotal.setText(String.format("%.2f", total));
    }

    @Override
    public int getItemCount() {
        return accountTotals.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView accountName;
        TextView accountIncome;
        TextView accountExpense;
        TextView accountTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            accountName = itemView.findViewById(R.id.item_account_name);
            accountIncome = itemView.findViewById(R.id.item_account_income);
            accountExpense = itemView.findViewById(R.id.item_account_expense);
            accountTotal = itemView.findViewById(R.id.item_account_total);
        }
    }

    public void setList(List<Map.Entry<String, AccountInfo>> accountTotals) {
        this.accountTotals = accountTotals;
        notifyDataSetChanged();
    }

    public static class AccountInfo{
        private double income;
        private double expense;
        private double total;

        public AccountInfo(double income, double expense, double total){
            this.income = income;
            this.expense = expense;
            this.total = total;
        }

        public double getIncome() {
            return income;
        }

        public void setIncome(double income) {
            this.income = income;
        }

        public double getExpense() {
            return expense;
        }

        public void setExpense(double expense) {
            this.expense = expense;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }
    }
}