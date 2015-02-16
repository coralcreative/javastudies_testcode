package com.application.wondernote.note;

import java.util.ArrayList;

import com.application.wondernote.R;
import com.application.wondernote.R.id;
import com.application.wondernote.R.layout;
import com.application.wondernote.R.menu;
import com.application.wondernote.note.db.NoteCategorySQLiteOpenHelper;
import com.application.wondernote.note.db.NoteContentsSQLiteOpenHelper;
import com.application.wondernote.note.db.NoteSQLiteOpenHelper;
import com.application.wondernote.note.util.NoteContents;
import com.application.wondernote.note.util.RecordCategoryList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

// ノート画面
public class NoteActivity extends Activity {

	// フィールド
	NoteContents note;
	boolean bSaveFlg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Activity起動時に、ソフトウェアキーボードが表示されないようにする
		this.getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_note);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// DBからノート情報を取得する
		Intent i = getIntent();
		getNoteInfomation(i.getIntExtra("_id", -1));

		// ノートの情報をセットする
		setNoteInfomation();
	}

	/* -------------- ノートの情報を取得 -------------- */
	public void getNoteInfomation(int id) {

		// フィールド(取得用データ)
		String title = "";
		int page = 0;
		int total_pages = 0;
		String date = "";
		String content = "";

		// 新規作成でないとき、データを読み込む
		if (id != -1) {
			// DBの呼び出し
			NoteSQLiteOpenHelper note_helper = new NoteSQLiteOpenHelper(this);
			SQLiteDatabase note_db = note_helper.getReadableDatabase();

			// ノートテーブル・コンテンツテーブルを結合し、テーブル内の情報を取得
			String column = "_id";
			String sql = "SELECT * FROM " + NoteSQLiteOpenHelper.NOTE_TABLE
					+ " INNER JOIN "
					+ NoteContentsSQLiteOpenHelper.NOTE_CONTENTS_TABLE + " ON "
					+ NoteSQLiteOpenHelper.NOTE_TABLE + "." + column + " = "
					+ NoteContentsSQLiteOpenHelper.NOTE_CONTENTS_TABLE + "."
					+ column + " WHERE " + NoteSQLiteOpenHelper.NOTE_TABLE
					+ "." + column + " = ?" + ";";
			Cursor cursor = note_db.rawQuery(sql,
					new String[] { String.valueOf(id) });

			// 参照先を一番始めにし、データを取得していく
			boolean isEof = cursor.moveToFirst();
			while (isEof) {
				title = cursor.getString(cursor.getColumnIndex("title"));
				page = cursor.getInt(cursor.getColumnIndex("page"));
				total_pages = cursor.getInt(cursor
						.getColumnIndex("total_pages"));
				date = cursor.getString(cursor.getColumnIndex("create_date"));
				content = cursor.getString(cursor.getColumnIndex("content"));

				// 値をコンストラクタに配置
				note = new NoteContents(id, title, page, total_pages, date,
						content);

				// 次の行へカーソル移動
				isEof = cursor.moveToNext();
			}
			cursor.close();
			note_db.close();
		}
		// 新規作成のとき
		else {
			date = createDate();
			note = new NoteContents(id, title, page, total_pages, date, content);
		}
	}

	/* -------------- ノートの情報をセット -------------- */
	public void setNoteInfomation() {

		// ノートタイトルの配置
		EditText titleEditText = (EditText) findViewById(R.id.edit_note_create_title);
		titleEditText.setText(note.getNoteTitle());

		// 日付
		EditText dateEditText = (EditText) findViewById(R.id.edit_note_date);
		dateEditText.setText(note.getNoteDate());

		// 現在ページと総ページ
		String pages = (note.getNotePage() + 1) + " / "
				+ (note.getNoteTotalPages() + 1);
		TextView pagesTextView = (TextView) findViewById(R.id.note_page);
		pagesTextView.setText(pages);

		// ノートの内容
		EditText contentEditText = (EditText) findViewById(R.id.edit_note_context);
		contentEditText.setText(note.getNoteContent());
	}

	/* -------------- ノート保存ダイアログの表示-------------- */
	public void showSaveDialog() {

		// ダイアログの表示
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("ノートの保存");
		alert.setMessage("ノートを保存しますか？");

		alert.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// キャンセルボタンが押された時は終了する
				finish();
			}
		});
		alert.setPositiveButton("保存", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// 既存ノートで保存ボタンが押されたときは、そのまま保存
				if (note.getNoteId() != -1) {
					saveNoteInfomation();
				}
				// 新規作成で保存をした時は、初回ポップアップを表示する
				else {
					showSaveNewCreateNoteDialog();
				}
				Toast.makeText(NoteActivity.this, "保存しました", Toast.LENGTH_LONG)
						.show();
				finish();
			}
		});
		alert.show();
	}

	/* -------------- ノートの情報を保存(新規保存の場合) -------------- */
	public void showSaveNewCreateNoteDialog() {
		// カスタムビューを設定
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.activity_create_note,
				(ViewGroup) findViewById(R.id.dialog_createnote));

		// アラートダイアログ を生成
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("新規ノートの保存");
		builder.setView(layout);
		builder.setNegativeButton("キャンセル", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// キャンセルボタンが押された時は終了する
				finish();
			}
		});
		builder.setPositiveButton("保存", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				saveNoteInfomation();
			}
		});
		// 表示
		builder.create().show();
	}

	/* -------------- ノートの情報を保存(既存ノート保存) -------------- */
	public void saveNoteInfomation() {

		// チェックしたカテゴリのIDの判定処理を作成し、初回保存時ポップアップを表示
		int _id = note.getNoteId();

		// edittextからデータを取得
		// ノートタイトル
		EditText titleEditText = (EditText) findViewById(R.id.edit_note_create_title);
		String title = titleEditText.getText().toString();
		// ノートの内容
		EditText contentEditText = (EditText) findViewById(R.id.edit_note_context);
		String content = contentEditText.getText().toString();

		// DBの呼び出し
		NoteContentsSQLiteOpenHelper note_helper = new NoteContentsSQLiteOpenHelper(
				this);
		SQLiteDatabase note_content_db = note_helper.getWritableDatabase();

		// where文にidを設定する
		String whereClause = "_id = " + _id;

		// 書き込みするデータをvalsesに追加
		ContentValues values = new ContentValues();
		values.put("content", content);

		// データの更新
		note_content_db.update(
				NoteContentsSQLiteOpenHelper.NOTE_CONTENTS_TABLE, values,
				whereClause, null);

		// データをDBに書き込む
		long result = note_content_db.update(
				NoteContentsSQLiteOpenHelper.NOTE_CONTENTS_TABLE, values,
				whereClause, null);
		note_content_db.close();

		if (result != -1) {
			Toast.makeText(this, R.string.dialog_savecomplete,
					Toast.LENGTH_LONG).show();

		}

		finish();
	}

	/* -------------- ノートの情報を保存(新規ノート保存) -------------- */
	public void saveNoteInfomation(String title, int catetory, String remark) {

		// チェックしたカテゴリのIDの判定処理を作成し、初回保存時ポップアップを表示
		int _id = note.getNoteId();
		
		// ノートの内容
		EditText contentEditText = (EditText) findViewById(R.id.edit_note_context);
		String content = contentEditText.getText().toString();

		// ノート読込画面からノートを編集し、保存しようとしたとき
		if (_id != -1) {

			// DBの呼び出し
			NoteContentsSQLiteOpenHelper note_helper = new NoteContentsSQLiteOpenHelper(
					this);
			SQLiteDatabase note_content_db = note_helper.getWritableDatabase();

			// where文にidを設定する
			String whereClause = "_id = " + _id;

			// 書き込みするデータをvalsesに追加
			ContentValues values = new ContentValues();
			values.put("content", content);

			// データの更新
			note_content_db.update(
					NoteContentsSQLiteOpenHelper.NOTE_CONTENTS_TABLE, values,
					whereClause, null);

			// データをDBに書き込む
			long result = note_content_db.update(
					NoteContentsSQLiteOpenHelper.NOTE_CONTENTS_TABLE, values,
					whereClause, null);
			note_content_db.close();

			if (result != -1) {
				Toast.makeText(this, R.string.dialog_savecomplete,
						Toast.LENGTH_LONG).show();

			}
		}

		// 新規ノート作成の場合
		else {

			// DBの呼び出し
			NoteSQLiteOpenHelper note_helper = new NoteSQLiteOpenHelper(this);
			SQLiteDatabase note_db = note_helper.getWritableDatabase();

			// idを新規に割り当てるために、DBからidを取得
			String sql = "SELECT _id FROM " + NoteSQLiteOpenHelper.NOTE_TABLE
					+ ";";
			Cursor cursor = note_db.rawQuery(sql, null);
			// 参照先を一番始めにし、データを取得していく
			boolean isEof = cursor.moveToFirst();
			while (isEof) {
				_id = cursor.getInt(cursor.getColumnIndex("_id")) + 1;
				isEof = cursor.moveToNext();
			}
			cursor.close();

			// DBに書き込み
			sql = "insert into " + NoteSQLiteOpenHelper.NOTE_TABLE + " values("
					+ _id + "," + title + "," + 0 + "," + note.getNoteDate()
					+ "," + note.getNoteDate() + "," + "" + "," + 0 + ");";
			note_db.execSQL(sql);

			// DBに書き込み
			sql = "insert into "
					+ NoteContentsSQLiteOpenHelper.NOTE_CONTENTS_TABLE
					+ " values(" + _id + "," + note.getNotePage() + ","
					+ note.getNoteTotalPages() + "," + content + ");";
			note_db.execSQL(sql);
			note_db.close();
		}
		finish();
	}

	/* -------------- ノート作成日の自動変換処理(新規ノート作成時) -------------- */
	public String createDate() {
		// 作成日に、今日の日付を指定
		Time time = new Time("Asia/Tokyo");
		time.setToNow();

		// 1~9月の場合、前に"0"を挿入する
		String month;
		if (time.month < 10) {
			month = "0" + String.valueOf(time.month + 1);
		} else {
			month = String.valueOf(time.month + 1);
		}

		// 1~9日の場合、前に"0"を挿入する
		String day;
		if (time.monthDay < 10) {
			day = "0" + String.valueOf(time.monthDay);
		} else {
			day = String.valueOf(time.monthDay);
		}

		// 日付をセット
		String date = time.year + "/" + month + "/" + day;
		return date;
	}

	/* -------------- 戻るボタン押下時の処理 -------------- */
	// 戻るボタンを押した時、ダイアログを表示する
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				showSaveDialog();
			}
		}
		return super.dispatchKeyEvent(event);
	}

	/* -------------- MENU START -------------- */
	// MENU押下時起動メソッド
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.note, menu);
		return true;
	}

	// MENUのリストの中から選択し、該当ウィンドウを表示
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.button_save) {
			showSaveDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	/* -------------- MENU END -------------- */
}
