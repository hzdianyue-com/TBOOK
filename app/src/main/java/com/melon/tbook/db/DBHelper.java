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
    private static final String COLUMN_ACCOUNT = "account"; // 新增子账户列

    private static final String TABLE_ACCOUNTS = "accounts";
    private static final String COLUMN_ACCOUNT_ID = "_id";
    private static final String COLUMN_ACCOUNT_NAME = "account_name";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String COLUMN_CATEGORY_ID = "_id";
    private static final String COLUMN_CATEGORY_NAME = "category_name";
    private static final String COLUMN_CATEGORY_TYPE = "category_type";
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_BORROW = "borrow";
    private static final String COLUMN_BORROW_ID = "_id";
    private static final String COLUMN_BORROW_TYPE = "borrow_type";
    private static final String COLUMN_BORROW_AMOUNT = "borrow_amount";
    private static final String COLUMN_BORROWER = "borrower";
    private static final String COLUMN_BORROW_DATE = "borrow_date";


    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建记录表
        String createTableQuery = "CREATE TABLE " + TABLE_RECORDS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_AMOUNT + " REAL, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_REMARK + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_ACCOUNT + " TEXT)"; // 新增子账户列
        db.execSQL(createTableQuery);

        // 创建账户表
        String createAccountTable = "CREATE TABLE " + TABLE_ACCOUNTS + " (" +
                COLUMN_ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ACCOUNT_NAME + " TEXT UNIQUE)";
        db.execSQL(createAccountTable);


        // 创建分类表
        String createCategoryTable = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CATEGORY_NAME + " TEXT UNIQUE, " +
                COLUMN_CATEGORY_TYPE + " TEXT)";
        db.execSQL(createCategoryTable);
        //创建用户表
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createUsersTable);
        //创建借入借出表
        String createBorrowTable = "CREATE TABLE " + TABLE_BORROW + " (" +
                COLUMN_BORROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BORROW_TYPE + " TEXT , " +
                COLUMN_BORROW_AMOUNT + " REAL , " +
                COLUMN_BORROWER + " TEXT , " +
                COLUMN_BORROW_DATE + " TEXT " + ")";
        db.execSQL(createBorrowTable);


        // 初始化默认分类
        getDefaultCategories(db);
        getDefaultAccount(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BORROW);
        onCreate(db);
    }
    //添加借款
    public long addBorrow(Borrow borrow) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BORROW_TYPE, borrow.getType());
        values.put(COLUMN_BORROW_AMOUNT,borrow.getAmount());
        values.put(COLUMN_BORROWER,borrow.getBorrower());
        values.put(COLUMN_BORROW_DATE,dateFormat.format(borrow.getBorrowDate()));

        long insertId = db.insert(TABLE_BORROW, null, values);
        db.close();
        return insertId;
    }

    //删除借款
    public void deleteBorrow(int borrowId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_BORROW,COLUMN_BORROW_ID + " = ?", new String[]{String.valueOf(borrowId)});
        db.close();
    }


    // 获取指定类型的借款
    public List<Borrow> getAllBorrows(String borrowType) {
        List<Borrow> borrows = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_BORROW, null, COLUMN_BORROW_TYPE + " = ?", new String[]{borrowType}, null, null, COLUMN_BORROW_DATE + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BORROW_ID));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BORROW_TYPE));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BORROW_AMOUNT));
                String borrower = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BORROWER));
                String dateString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BORROW_DATE));

                Date date = null;
                try {
                    date = dateFormat.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Borrow borrow = new Borrow(id,type,amount,borrower,date);
                borrows.add(borrow);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return borrows;
    }


    // 获取所有借款
    public List<Borrow> getAllBorrows() {
        List<Borrow> borrows = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_BORROW, null, null,null,null,null,COLUMN_BORROW_DATE + " DESC");

        if(cursor != null && cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BORROW_ID));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BORROW_TYPE));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BORROW_AMOUNT));
                String borrower = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BORROWER));
                String dateString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BORROW_DATE));

                Date date = null;
                try {
                    date = dateFormat.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Borrow borrow = new Borrow(id,type,amount,borrower,date);
                borrows.add(borrow);
            }while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return borrows;

    }

    // 更新借款
    public int updateBorrow(int borrowId,long reminderId,boolean isRepeat) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        int updateRow =  db.update(TABLE_BORROW,values,COLUMN_BORROW_ID + " = ?",new String[]{String.valueOf(borrowId)});
        db.close();
        return updateRow;
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
        values.put(COLUMN_ACCOUNT, record.getAccount()); // 新增子账户
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
                String account = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT)); // 新增子账户

                Date date = null;
                try {
                    date = dateFormat.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Record record = new Record(id, type, amount, category, remark, date, account);
                records.add(record);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return records;
    }


    // 添加子账户
    public long addAccount(String accountName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACCOUNT_NAME, accountName);
        long insertId = db.insert(TABLE_ACCOUNTS, null, values);
        db.close();
        return insertId;
    }

    // 删除子账户
    public void deleteAccount(int accountId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_ACCOUNTS,COLUMN_ACCOUNT_ID + " = ?", new String[]{String.valueOf(accountId)});
        db.close();
    }

    // 更新子账户
    public int updateAccount(int accountId,String accountName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACCOUNT_NAME, accountName);
        int updateRow = db.update(TABLE_ACCOUNTS, values,COLUMN_ACCOUNT_ID + " = ?",new String[]{String.valueOf(accountId)});
        db.close();
        return updateRow;

    }

    // 获取所有子账户
    public List<String> getAllAccounts() {
        List<String> accounts = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACCOUNTS, new String[]{COLUMN_ACCOUNT_NAME},null,null,null,null,null);

        if(cursor != null && cursor.moveToFirst()){
            do{
                String accountName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_NAME));
                accounts.add(accountName);
            }while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return accounts;

    }


    // 添加分类
    public long addCategory(String categoryName,String categoryType) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, categoryName);
        values.put(COLUMN_CATEGORY_TYPE, categoryType);
        long insertId = db.insert(TABLE_CATEGORIES, null, values);
        db.close();
        return insertId;
    }

    // 删除分类
    public void deleteCategory(int categoryId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CATEGORIES,COLUMN_CATEGORY_ID + " = ?", new String[]{String.valueOf(categoryId)});
        db.close();
    }

    // 更新分类
    public int updateCategory(int categoryId,String categoryName,String categoryType) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, categoryName);
        values.put(COLUMN_CATEGORY_TYPE, categoryType);
        int updateRow = db.update(TABLE_CATEGORIES, values,COLUMN_CATEGORY_ID + " = ?",new String[]{String.valueOf(categoryId)});
        db.close();
        return updateRow;

    }

    // 获取所有分类
    public List<String> getAllCategories(String categoryType) {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, new String[]{COLUMN_CATEGORY_NAME}, COLUMN_CATEGORY_TYPE + " = ?", new String[]{categoryType},null,null,null);

        if(cursor != null && cursor.moveToFirst()){
            do{
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME));
                categories.add(categoryName);
            }while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return categories;

    }

    private void getDefaultAccount(SQLiteDatabase db) {
        addAccount("默认", db);
        addAccount("网络账户", db);
        addAccount("理财账户", db);
        addAccount("银行卡账户", db);
        addAccount("支付宝", db);
        addAccount("微信钱包", db);
    }

    private void addAccount(String accountName,SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACCOUNT_NAME, accountName);
        db.insert(TABLE_ACCOUNTS, null, values);
    }

    private void getDefaultCategories(SQLiteDatabase db) {
        addCategory("工资薪水", "收入",db);
        addCategory("副业收入", "收入",db);
        addCategory("投资收益", "收入",db);
        addCategory("红包礼金", "收入",db);
        addCategory("奖金", "收入",db);
        addCategory("租金收入", "收入",db);
        addCategory("其他收入", "收入",db);

        addCategory("生活消费","支出",db);
        addCategory("住房支出","支出",db);
        addCategory("交通支出","支出",db);
        addCategory("通讯支出","支出",db);
        addCategory("保险","支出",db);
        addCategory("娱乐休闲","支出",db);
        addCategory("教育支出","支出",db);
        addCategory("医疗支出","支出",db);
        addCategory("家庭支出","支出",db);
        addCategory("储蓄投资","支出",db);
        addCategory("其他支出","支出",db);

    }
    private void addCategory(String categoryName,String categoryType,SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, categoryName);
        values.put(COLUMN_CATEGORY_TYPE,categoryType);
        db.insert(TABLE_CATEGORIES, null, values);
    }

    // 添加用户
    public long addUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PASSWORD, user.getPassword());
        long insertId = db.insert(TABLE_USERS, null, values);
        db.close();
        return insertId;
    }

    // 根据用户名获取用户
    public User getUserByUserName(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USERNAME + " = ?", new String[]{username}, null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
            user = new User(id,name,password);
            cursor.close();
        }

        db.close();
        return user;
    }

    // 获取用户
    public User getUser() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,null,null,null,null,null,null);
        User user = null;
        if(cursor != null && cursor.moveToFirst()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
            user = new User(id,name,password);
            cursor.close();

        }
        db.close();
        return user;
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