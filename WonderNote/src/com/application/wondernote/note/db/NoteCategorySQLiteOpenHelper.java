package com.application.wondernote.note.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteCategorySQLiteOpenHelper extends SQLiteOpenHelper {

	// データベース
	public static final String NOTE_DATABASE = "NOTE_DATABASE";
	// テーブル名
	public static final String NOTE_CATEGORY_TABLE = "NOTE_CATEGORY_TABLE";
	// CREATE文
	public static final String CREATE_TABLE = "CREATE TABLE "
			+ NOTE_CATEGORY_TABLE
			+ "(category_id INTEGER PRIMARY KEY NOT NULL"
			+ ",category_name TEXT NOT NULL"
			+ ",category_delete_flg INTEGER NOT NULL"
			+ ");";

	public NoteCategorySQLiteOpenHelper(Context context) {
		super(context, NOTE_DATABASE, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 自動生成されたメソッド・スタブ
	}

}
