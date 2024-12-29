package com.melon.tbook.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "tbook.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_RECORDS = "records";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_REMARK = "remark";
    private static final String COLUMN_DATE = "date";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());



    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_RECORDS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_AMOUNT + " REAL, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_REMARK + " TEXT, " +
                COLUMN_DATE + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        onCreate(db);
    }

    // 添加记账记录
    public long addRecord(Record record) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, record.getType());
        values.put(COLUMN_AMOUNT, record.getAmount());
        values.put(COLUMN_CATEGORY, record.getCategory());
        values.put(COLUMN_REMARK, record.getRemark());
        values.put(COLUMN_DATE, dateFormat.format(record.getDate()));
        long insertId = db.insert(TABLE_RECORDS, null, values);
        db.close();
        return insertId;
    }


    // 获取所有记账记录
    public List<Record> getAllRecords() {
        List<Record> records = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECORDS, null, null, null, null, null, COLUMN_DATE + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
                String remark = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REMARK));
                String dateString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));

                Date date = null;
                try {
                    date = dateFormat.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Record record = new Record(id, type, amount, category, remark, date);
                records.add(record);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return records;
    }

    // 计算总收入
    public double getTotalIncome() {
        double totalIncome = 0.0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECORDS, new String[]{"SUM(" + COLUMN_AMOUNT + ")"}, COLUMN_TYPE + " = ?", new String[]{"收入"}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            totalIncome = cursor.getDouble(0);
            cursor.close();
        }
        db.close();
        return totalIncome;
    }


    // 计算总支出
    public double getTotalExpense() {
        double totalExpense = 0.0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECORDS, new String[]{"SUM(" + COLUMN_AMOUNT + ")"}, COLUMN_TYPE + " = ?", new String[]{"支出"}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            totalExpense = cursor.getDouble(0);
            cursor.close();
        }
        db.close();
        return totalExpense;
    }


    // 删除记账记录
    public void deleteRecord(int recordId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_RECORDS, COLUMN_ID + " = ?", new String[]{String.valueOf(recordId)});
        db.close();
    }

}