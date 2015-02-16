package com.application.wondernote;

import com.application.wondernote.note.NoteActivity;
import com.application.wondernote.note.ReadNoteActivity;
import com.application.wondernote.note.db.NoteCategorySQLiteOpenHelper;
import com.application.wondernote.note.db.NoteContentsSQLiteOpenHelper;
import com.application.wondernote.note.db.NoteSQLiteOpenHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

// ホーム画面
public class HomeActivity extends Activity implements OnClickListener{

	// Activity初期起動メソッド
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		// 各ボタン押下時に、どの画面に遷移させるかを登録する
		// 新規ノート作成画面
		Button create_note = (Button)findViewById(R.id.button_jumpCreateNote);
		create_note.setOnClickListener(this);
		// ノート読込画面
		Button read_note = (Button)findViewById(R.id.button_jumpReadNote);
		read_note.setOnClickListener(this);
		// 設定画面
		Button config_note = (Button)findViewById(R.id.button_jumpConfigNote);
		config_note.setOnClickListener(this);
		
		// アプリ初回起動時に、DBを作成する
		StartDBHelper helper = new StartDBHelper(this);
		SQLiteDatabase db = helper.getReadableDatabase();
		db.close();
	}
	
	// ボタン押下時の処理
	public void onClick(View v) {
		Intent i;
		switch(v.getId()){
		// 新規ノート作成画面に遷移
        case R.id.button_jumpCreateNote:
        	i= new Intent(this, NoteActivity.class);
        	startActivity(i);
            break;
        // ノート読み取り画面に遷移
        case R.id.button_jumpReadNote:
        	i= new Intent(this, ReadNoteActivity.class);
        	startActivity(i);
        	break;
        // 設定画面に遷移
        case R.id.button_jumpConfigNote:
        	break;
        }
	}
	
	/* ---------------- DBの作成 ---------------- */
	public class StartDBHelper extends SQLiteOpenHelper {

		// コンストラクタ
		public StartDBHelper(Context context) {
			super(context, NoteSQLiteOpenHelper.NOTE_DATABASE, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(NoteSQLiteOpenHelper.CREATE_TABLE);
			db.execSQL(NoteCategorySQLiteOpenHelper.CREATE_TABLE);
			db.execSQL(NoteContentsSQLiteOpenHelper.CREATE_TABLE);

			// カテゴリテーブルに「未分類」を追加
			String sql = "insert into "
					+ NoteCategorySQLiteOpenHelper.NOTE_CATEGORY_TABLE
					+ " values(0, '未分類', 0);";
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO 自動生成されたメソッド・スタブ
		}
	}
}
