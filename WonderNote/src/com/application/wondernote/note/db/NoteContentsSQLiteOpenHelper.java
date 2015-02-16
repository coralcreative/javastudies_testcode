package com.application.wondernote.note.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteContentsSQLiteOpenHelper extends SQLiteOpenHelper {

	// �f�[�^�x�[�X
	public static final String NOTE_DATABASE = "NOTE_DATABASE";
	// �e�[�u����
	public static final String NOTE_CONTENTS_TABLE = "NOTE_CONTENTS_TABLE";
	// CREATE��
	public static final String CREATE_TABLE = "CREATE TABLE "
			+ NOTE_CONTENTS_TABLE
			+ "(_id INTEGER NOT NULL"
			+ ",page INTEGER NOT NULL"
			+ ",total_pages INTEGER NOT NULL"
			+ ",content TEXT"
			+ ",PRIMARY KEY(_id, page)"
			+ ");";

	public NoteContentsSQLiteOpenHelper(Context context) {
		super(context, NOTE_DATABASE, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
	}

}
