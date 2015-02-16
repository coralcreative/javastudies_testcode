package com.application.wondernote.note;

import java.util.ArrayList;
import java.util.List;

import com.application.wondernote.R;
import com.application.wondernote.R.id;
import com.application.wondernote.R.layout;
import com.application.wondernote.R.menu;
import com.application.wondernote.note.ReadNoteActivity.ListItem;
import com.application.wondernote.note.ReadNoteActivity.ListItemAdapter;
import com.application.wondernote.note.ReadNoteActivity.ViewHolder;
import com.application.wondernote.note.db.NoteCategorySQLiteOpenHelper;
import com.application.wondernote.note.db.NoteSQLiteOpenHelper;
import com.application.wondernote.note.util.RecordCategoryList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

// カテゴリ追加・編集画面
public class CreateNoteCategoryActivity extends Activity implements
		OnClickListener {

	// フィールド
	private ArrayList<RecordCategoryList> category;
	ArrayList<ListItem> items;
	private ListView lv;
	private boolean isChecked;
	private Cursor cursor;
	private String category_name;
	private int checkedPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Activity起動時に、ソフトウェアキーボードが表示されないようにする
		this.getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_create_note_category);

		// 各ボタン押下時に、どの画面に遷移させるかを登録する
		// カテゴリ追加
		Button add_category = (Button) findViewById(R.id.button_add);
		add_category.setOnClickListener(this);
		// カテゴリ変更
		Button change_category = (Button) findViewById(R.id.button_change);
		change_category.setOnClickListener(this);
		// カテゴリ削除
		Button delete_category = (Button) findViewById(R.id.button_delete);
		delete_category.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// DBからカテゴリ名を取得し、リストビューの項目に追加
		lv = (ListView) findViewById(R.id.note_category_list);

		// ノートカテゴリをテーブルから取得する
		getNoteCategory();

		// adapterに値をセット
		int count = 0;
		String[] category_name = new String[category.size()];
		for (RecordCategoryList c : category) {
			category_name[count] = c.getCategoryName();
			count++;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_single_choice, // ラジオボタンがついたリスト
				category_name);

		// チェックボックスに配置し、値をセット
		lv.setAdapter(adapter);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		if (checkedPosition >= 0) {
			// デフォルトで選択しておく項目
			lv.setItemChecked(checkedPosition, true);
		}
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent,
					final View view, final int position, final long id) {
				// 項目が選択されたときの処理
				setCheckedPosition(position);
			}
		});
	}

	// ボタン押下時の処理
	@Override
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		// カテゴリ追加ボタン押下時
		case R.id.button_add:
			addCategory();
			break;
		// カテゴリ編集ボタン押下時
		case R.id.button_change:
			changeCategory();
			break;
		// カテゴリ削除ボタン押下時
		case R.id.button_delete:
			Toast.makeText(this, "実装中", Toast.LENGTH_LONG).show();
			// deleteCategory();
			break;
		}
	}

	/* -------------- ノートカテゴリの取得処理 -------------- */
	public void getNoteCategory() {

		category = new ArrayList<RecordCategoryList>();

		// カテゴリDBの呼び出し
		NoteCategorySQLiteOpenHelper category_helper = new NoteCategorySQLiteOpenHelper(
				this);
		SQLiteDatabase category_db = category_helper.getReadableDatabase();

		// テーブルからカテゴリ数を検索
		String sql = "SELECT * FROM NOTE_CATEGORY_TABLE WHERE category_id <> ?";
		Cursor cursor = category_db.rawQuery(sql, new String[] { "0" });

		int category_id;
		String category_name;
		boolean delete_flg;

		// 参照先を一番始めにし、データを取得していく
		boolean isEof = cursor.moveToFirst();
		while (isEof) {
			// データベースから値を取得する
			category_id = cursor.getInt(cursor.getColumnIndex("category_id"));
			;
			category_name = cursor.getString(cursor
					.getColumnIndex("category_name"));
			delete_flg = getDeleteFlg(cursor.getInt(cursor
					.getColumnIndex("category_delete_flg")));

			// デリートフラグが立っていない時、リストに追加
			if (!delete_flg) {
				// カテゴリをリストアイテムに追加する
				category.add(new RecordCategoryList(category_id, category_name,
						delete_flg));
			}
			isEof = cursor.moveToNext();
		}
		cursor.close();
		category_db.close();
	}

	/* -------------- ノートカテゴリの追加処理 -------------- */
	public void addCategory() {

		// カテゴリ名の文字数判定
		if (!checkCategoryLength()) {
			return;
		}

		// カテゴリDBの呼び出し
		NoteCategorySQLiteOpenHelper category_helper = new NoteCategorySQLiteOpenHelper(
				this);
		SQLiteDatabase category_db = category_helper.getWritableDatabase();

		// カテゴリ数の取得
		int count = 0;
		String sql = "SELECT * FROM NOTE_CATEGORY_TABLE";
		Cursor cursor = category_db.rawQuery(sql, null);
		boolean isEof = cursor.moveToFirst();
		while (isEof) {
			count++;
			isEof = cursor.moveToNext();
		}
		cursor.close();

		// カテゴリ名の重複処理
		if (!checkCategoryNameOverlap()) {
			category_db.close();
			return;
		}

		// valuesにデータを挿入
		ContentValues values = new ContentValues();
		values.put("category_id", count);
		values.put("category_name", getCategoryName());
		values.put("category_delete_flg", 0);

		// データをDBに書き込む
		long result = category_db.insert(
				NoteCategorySQLiteOpenHelper.NOTE_CATEGORY_TABLE, null, values);
		category_db.close();

		if (result != -1) {
			Toast.makeText(this, R.string.dialog_addcomplete, Toast.LENGTH_LONG)
					.show();
			finish();
		}
	}

	/* -------------- ノートカテゴリの編集処理 -------------- */
	public void changeCategory() {

		// ラジオボタンの判定
		if (!checkCategoryRadioButton()) {
			return;
		}

		// カテゴリ名の文字数判定
		if (!checkCategoryLength()) {
			return;
		}

		// カテゴリDBの呼び出し
		NoteCategorySQLiteOpenHelper category_helper = new NoteCategorySQLiteOpenHelper(
				this);
		SQLiteDatabase category_db = category_helper.getWritableDatabase();

		// カテゴリ数の取得
		int count = 0;
		String sql = "SELECT * FROM NOTE_CATEGORY_TABLE";
		Cursor cursor = category_db.rawQuery(sql, null);
		boolean isEof = cursor.moveToFirst();
		while (isEof) {
			count++;
			isEof = cursor.moveToNext();
		}
		cursor.close();

		// カテゴリ名の重複処理
		if (!checkCategoryNameOverlap()) {
			category_db.close();
			return;
		}

		// チェックしたカテゴリのIDの判定処理を作成
		int category_id = category.get(getCheckedPosition()).getCategoryId();
		String whereClause = "category_id = " + category_id;

		// 書き込みするデータをvalsesに追加
		ContentValues values = new ContentValues();
		values.put("category_name", getCategoryName());

		// データの更新
		category_db.update(NoteCategorySQLiteOpenHelper.NOTE_CATEGORY_TABLE,
				values, whereClause, null);

		// データをDBに書き込む
		long result = category_db.update(
				NoteCategorySQLiteOpenHelper.NOTE_CATEGORY_TABLE, values,
				whereClause, null);
		category_db.close();

		if (result != -1) {
			Toast.makeText(this, R.string.dialog_changecomplete,
					Toast.LENGTH_LONG).show();
			finish();
		}
	}

	/* -------------- ノートカテゴリの削除処理 -------------- */
	public void deleteCategory() {

		// ラジオボタンの判定
		if (!checkCategoryRadioButton()) {
			return;
		}

		// カテゴリDBの呼び出し
		NoteCategorySQLiteOpenHelper category_helper = new NoteCategorySQLiteOpenHelper(
				this);
		SQLiteDatabase category_db = category_helper.getWritableDatabase();

		// チェックしたカテゴリのIDの判定処理を作成
		int category_id = category.get(getCheckedPosition()).getCategoryId();
		String whereClause = "category_id = " + category_id;

		// 書き込みするデータをvalsesに追加
		ContentValues values = new ContentValues();
		values.put("category_delete_flg", 1);

		// データの更新
		category_db.update(NoteCategorySQLiteOpenHelper.NOTE_CATEGORY_TABLE,
				values, whereClause, null);

		// ここに、NOTE_TABLEの同カテゴリを「category_id=0(未分類)」にする処理を追加

		// データをDBに書き込む
		long result = category_db.update(
				NoteCategorySQLiteOpenHelper.NOTE_CATEGORY_TABLE, values,
				whereClause, null);
		category_db.close();

		if (result != -1) {
			Toast.makeText(this, R.string.dialog_deletecomplete,
					Toast.LENGTH_LONG).show();
			finish();
		}
	}

	/* -------------- カテゴリ名文字数判定処理 -------------- */
	public boolean checkCategoryLength() {

		// カテゴリ名の文字列と文字数を取得
		EditText edit_category = (EditText) findViewById(R.id.note_category_name);
		int length = edit_category.getText().length();

		// カテゴリ名が正常値の場合は、ポップアップは表示せずにデータをセットする。
		if (!(length == 0) && !(length > 20)) {
			setCategoryName(edit_category.getText().toString());
			return true;
		}

		// アラートダイアログ作成
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		LinearLayout alertview = new LinearLayout(this);
		alertview.setOrientation(LinearLayout.VERTICAL);
		TextView text1 = new TextView(this);

		// カテゴリ名が未入力の時、ポップアップを表示する。
		if (length == 0) {
			text1.setText("カテゴリ名が空欄です。\nカテゴリ名を入力してください。");
		}
		// カテゴリ名が20文字以上の時、ポップアップを表示する。
		else if (length > 20) {
			text1.setText("カテゴリ名が20文字を超えています。\nカテゴリ名は20文字以内にしてください。");
		}

		// ダイアログにviewを設定
		alert.setTitle("エラー");
		alertview.addView(text1);
		alert.setView(alertview);
		alert.setPositiveButton("閉じる", null);
		alert.show();
		return false;
	}

	/* -------------- カテゴリ名の重複判定処理 -------------- */
	public boolean checkCategoryNameOverlap() {

		String category_name = "";
		String edit_category_name = "";
		boolean bCategoryNameFlg = false;
		
		EditText edit_category = (EditText) findViewById(R.id.note_category_name);
		edit_category_name = (String)edit_category.getText().toString();

		// カテゴリが何も登録されていない時、true
		if(category.size() == 0){
			bCategoryNameFlg = true;
		}
		// カテゴリが1個以上登録されている場合、カテゴリ名の判定を行う
		else{
			// カテゴリ名を取得する
			for (RecordCategoryList c : category) {
				
				// 入力したカテゴリ名がテーブルに存在した場合はbreakし、falseを返す
				category_name = c.getCategoryName();
				if (category_name.equals(edit_category_name)) {
					bCategoryNameFlg = false;
					break;
				}
				// 入力したカテゴリ名がテーブルに存在しなかった場合はtrueを返す
				else {
					bCategoryNameFlg = true;
				}
			}
		}
		
		// falseだった場合、アラートを表示する
		if(!bCategoryNameFlg){
			// アラートダイアログ作成
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			LinearLayout alertview = new LinearLayout(this);
			alertview.setOrientation(LinearLayout.VERTICAL);
			TextView text1 = new TextView(this);
			text1.setText("カテゴリ名が既に登録されています。\n別のカテゴリ名を入力してください。");

			// ダイアログにviewを設定
			alert.setTitle("エラー");
			alertview.addView(text1);
			alert.setView(alertview);
			alert.setPositiveButton("閉じる", null);
			alert.show();
		}

		return bCategoryNameFlg;
	}

	/* -------------- カテゴリラジオボタン判定処理 -------------- */
	public boolean checkCategoryRadioButton() {

		// リストにカテゴリ数を取得
		int size = category.size();
		if (size == 0) {
			// カテゴリ未登録のとき、アラート表示
		}else{
			return true;
		}

		// ラジオボタンが何も選択されていない時は、アラート表示
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		LinearLayout alertview = new LinearLayout(this);
		alertview.setOrientation(LinearLayout.VERTICAL);
		TextView text1 = new TextView(this);

		// ラジオボタン未選択の時、ポップアップを表示する。
		text1.setText("カテゴリが登録されていません。\nカテゴリ名を入力し「追加」をタップしてください。");

		// ダイアログにviewを設定
		alert.setTitle("エラー");
		alertview.addView(text1);
		alert.setView(alertview);
		alert.setPositiveButton("閉じる", null);
		alert.show();
		return false;
	}

	/* -------------- GETTERとSETTER -------------- */
	// カテゴリ名
	public void setCategoryName(String category_name) {
		this.category_name = category_name;
	}

	public String getCategoryName() {
		return this.category_name;
	}

	// リストの選択した順番
	public void setCheckedPosition(int checkedPosition) {
		this.checkedPosition = checkedPosition;
	}

	public int getCheckedPosition() {
		return this.checkedPosition;
	}

	/* ---------------- delete_flg判定 ---------------- */
	public boolean getDeleteFlg(int delete_flg) {
		// デリートフラグが立っていない場合false(削除項目でない)
		if (delete_flg != 1) {
			return false;
		}
		// デリートフラグが立っている時true(削除項目)
		else {
			return true;
		}
	}

}
