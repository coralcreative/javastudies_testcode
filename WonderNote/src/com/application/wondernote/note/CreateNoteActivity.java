package com.application.wondernote.note;

import java.util.ArrayList;

import com.application.wondernote.R;
import com.application.wondernote.R.id;
import com.application.wondernote.note.ReadNoteActivity.ListItem;
import com.application.wondernote.note.db.NoteCategorySQLiteOpenHelper;
import com.application.wondernote.note.db.NoteContentsSQLiteOpenHelper;
import com.application.wondernote.note.db.NoteSQLiteOpenHelper;
import com.application.wondernote.note.util.RecordCategoryList;
import com.application.wondernote.note.util.RecordList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager.LayoutParams;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

// 新規ノート作成画面
public class CreateNoteActivity extends Activity implements OnClickListener {

	// フィールド
	ArrayList<RecordCategoryList> category;
	String note_title;
	int note_category;
	String note_create_date;
	String note_remark;

	// Activity初期起動メソッド
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Activity起動時に、ソフトウェアキーボードが表示されないようにする
		this.getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_create_note);

		// 各ボタン押下時に、どの画面に遷移させるかを登録する
		// ノート新規作成
		Button create = (Button) findViewById(R.id.button_create);
		create.setOnClickListener(this);
		// キャンセル
		Button cancel = (Button) findViewById(R.id.button_cancel);
		cancel.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// スピナー(ドロップダウンリスト)の設定
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// DBからカテゴリ名を取得し、スピナーに追加
		getCategoryList();
		for (RecordCategoryList c : category) {
			adapter.add(c.getCategoryName());
		}

		// アダプターを設定します
		Spinner spinner = (Spinner) findViewById(id.spinner_note_category);
		spinner.setAdapter(adapter);

		// スピナーのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// 選択されたカテゴリIDをセット
				setNoteCategory(category.get(position).getCategoryId());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		// ノート作成日の設定
		insertCreateDate();
	}

	// ボタン押下時の処理
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		// ノート読込画面に遷移
		case R.id.button_create:

			// ノートタイトルの文字数チェック
			boolean bNoteTitleFlg = checkTitleLength();
			if (!bNoteTitleFlg) {
				break;
			}

			// 備考欄の文字列をSETTERに挿入
			EditText edit_note_remark = (EditText) findViewById(R.id.edit_note_remark);
			setNoteRemark(edit_note_remark.getText().toString());

			// DBに情報書き込みし、idを取得
			int id = insertDBNoteData();

			// ノート画面に遷移
			i = new Intent(this, NoteActivity.class);
			i.putExtra("_id", id);
			startActivity(i);
			this.finish();
			break;

		// ノート一覧画面に遷移
		case R.id.button_cancel:
			finish();
			break;
		}
	}

	/* -------------- ノートタイトル文字数判定処理 -------------- */
	public boolean checkTitleLength() {

		// ノートタイトルの文字列と文字数を取得
		EditText edit_note_title = (EditText) findViewById(R.id.edit_note_create_title);
		int length = edit_note_title.getText().length();

		// タイトルが正常値の場合は、ポップアップは表示せずにデータをセットする。
		if (!(length == 0) && !(length > 50)) {
			setNoteTitle(edit_note_title.getText().toString());
			return true;
		}

		// アラートダイアログ作成
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		LinearLayout alertview = new LinearLayout(this);
		alertview.setOrientation(LinearLayout.VERTICAL);
		TextView text1 = new TextView(this);

		// タイトルが未入力の時、ポップアップを表示する。
		if (length == 0) {
			text1.setText("タイトルが空欄です。\nタイトルを入力してください。");
		}
		// タイトルが50文字以上の時、ポップアップを表示する。
		else if (length > 50) {
			text1.setText("タイトルが50文字を超えています。\nタイトルは50文字以内にしてください。");
		}

		// ダイアログにviewを設定
		alert.setTitle("エラー");
		alertview.addView(text1);
		alert.setView(alertview);
		alert.setPositiveButton("閉じる", null);
		alert.show();
		return false;
	}

	/* -------------- ノート作成日の自動入力処理 -------------- */
	public void insertCreateDate() {
		// 作成日に、今日の日付を入力する
		EditText create_date = (EditText) findViewById(R.id.note_createdate);
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
		create_date.setText(date);

		// データをSETTERに挿入する
		setNoteCreateDate(date);

		// 編集不可能にする
		create_date.setEnabled(false);
	}

	/* -------------- ノートカテゴリの取得処理 -------------- */
	public void getCategoryList() {

		category = new ArrayList<RecordCategoryList>();

		// カテゴリDBの呼び出し
		NoteCategorySQLiteOpenHelper category_helper = new NoteCategorySQLiteOpenHelper(this);
		SQLiteDatabase category_db = category_helper.getReadableDatabase();
				
		String sql = "SELECT * FROM NOTE_CATEGORY_TABLE;";
		Cursor cursor = category_db.rawQuery(sql, null);
		
		int category_id;
		String category_name;
		boolean delete_flg;
		
		// 参照先を一番始めにし、データを取得していく
		boolean isEof = cursor.moveToFirst();
		while (isEof) {
			// データベースから値を取得する
			category_id = cursor.getInt(cursor.getColumnIndex("category_id"));;
			category_name = cursor.getString(cursor.getColumnIndex("category_name"));
			delete_flg = getDeleteFlg(cursor.getInt(cursor.getColumnIndex("category_delete_flg")));
			
			// デリートフラグが立っていない時、リストに追加
			if(!delete_flg){
				// カテゴリをリストアイテムに追加する
				category.add(new RecordCategoryList(category_id, category_name, delete_flg));
			}
			isEof = cursor.moveToNext();
		}

		cursor.close();
		category_db.close();
	}

	/* -------------- DB書き込み処理 -------------- */
	public int insertDBNoteData() {
		
		// DBの呼び出し
		NoteSQLiteOpenHelper note_helper = new NoteSQLiteOpenHelper(this);
		SQLiteDatabase note_db = note_helper.getWritableDatabase();

		// テーブルからidを検索
		int id = 0;
		String sql = "SELECT * FROM " + NoteSQLiteOpenHelper.NOTE_TABLE + ";";
		Cursor cursor = note_db.rawQuery(sql, null);
		// 参照先を一番始めにし、データを取得していく
		boolean isEof = cursor.moveToFirst();
		while (isEof) {
			id = cursor.getInt(cursor.getColumnIndex("_id")) + 1;
			isEof = cursor.moveToNext();
		}
		cursor.close();

		// 書き込みするデータをvalsesに追加
		ContentValues values = new ContentValues();
		values.put("_id", id);
		values.put("title", getNoteTitle());
		values.put("category_id", getNoteCategory());
		values.put("create_date", getNoteCreateDate());
		values.put("update_date", getNoteCreateDate());
		values.put("remark", getNoteRemark());
		values.put("delete_flg", 0);

		// データの書き込み
		long result = note_db.insert(NoteSQLiteOpenHelper.NOTE_TABLE, null,
				values);
		
		// ノートコンテンツDBの呼び出し
		NoteContentsSQLiteOpenHelper note_contents_helper = new NoteContentsSQLiteOpenHelper(this);
		note_db = note_contents_helper.getWritableDatabase();
		
		// ノートコンテンツ(ノートの内容)テーブルにも初期値を登録
		values.clear();
		values.put("_id", id);
		values.put("page", 0);
		values.put("total_pages", 0);
		
		note_db.insert(NoteContentsSQLiteOpenHelper.NOTE_CONTENTS_TABLE, null,values);
		note_db.close();
		
		if (result != -1) {
			Toast.makeText(this, R.string.dialog_addcomplete, Toast.LENGTH_LONG).show();
			finish();
		}
		
		return id;
	}

	/* -------------- GETTERとSETTER -------------- */
	// ノートタイトル
	public void setNoteTitle(String note_title) {
		this.note_title = note_title;
	}

	public String getNoteTitle() {
		return this.note_title;
	}

	// ノートカテゴリ
	public void setNoteCategory(int note_category) {
		this.note_category = note_category;
	}

	public int getNoteCategory() {
		return this.note_category;
	}

	// ノート作成日
	public void setNoteCreateDate(String note_create_date) {
		this.note_create_date = note_create_date;
	}

	public String getNoteCreateDate() {
		return this.note_create_date;
	}

	// ノート備考欄
	public void setNoteRemark(String note_remark) {
		this.note_remark = note_remark;
	}

	public String getNoteRemark() {
		return this.note_remark;
	}
	
	/* ---------------- delete_flg判定 ---------------- */
	public boolean getDeleteFlg(int delete_flg){
		// デリートフラグが立っていない場合false(削除項目でない)
		if(delete_flg != 1){
			return false;
		}
		// デリートフラグが立っている時true(削除項目)
		else{
			return true;
		}	
	}

	/* -------------- MENU START -------------- */
	// MENU押下時起動メソッド
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.create_note, menu);
		return true;
	}

	// MENUのリストの中から選択し、該当ウィンドウを表示
	// (ノート読込画面では、新規カテゴリ作成のみ)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		// 新規ノートカテゴリ作成画面に遷移
		case R.id.button_createcategory:
			Intent i = new Intent(this, CreateNoteCategoryActivity.class);
			startActivity(i);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	/* -------------- MENU END -------------- */
}
