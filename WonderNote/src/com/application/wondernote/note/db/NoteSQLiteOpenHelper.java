package com.application.wondernote.note.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteSQLiteOpenHelper extends SQLiteOpenHelper {

	// データベース
	public static final String NOTE_DATABASE = "NOTE_DATABASE";
	// テーブル名
	public static final String NOTE_TABLE = "NOTE_TABLE";
	// CREATE文
	public static final String CREATE_TABLE = "CREATE TABLE "
			+ NOTE_TABLE
			+ " (_id INTEGER PRIMARY KEY NOT NULL"
			+ ",title TEXT NOT NULL"
			+ ",category_id INTEGER NOT NULL"
			+ ",create_date TEXT NOT NULL"
			+ ",update_date TEXT NOT NULL"
			+ ",remark TEXT"
			+ ",delete_flg INTEGER NOT NULL"
			+ ");";
	
	public NoteSQLiteOpenHelper(Context context) {
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
