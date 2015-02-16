package com.application.wondernote.note;

import java.util.ArrayList;
import java.util.List;

import com.application.wondernote.R;
import com.application.wondernote.note.db.NoteCategorySQLiteOpenHelper;
import com.application.wondernote.note.db.NoteContentsSQLiteOpenHelper;
import com.application.wondernote.note.db.NoteSQLiteOpenHelper;
import com.application.wondernote.note.util.RecordList;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

// ノート読込画面
public class ReadNoteActivity extends ListActivity {
	
	// フィールド
	ArrayList<RecordList> record_list;

	// Activity初期起動メソッド
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// リストにアイテムを表示する
		getList();
	}

	/* -------------- リストのアイテム -------------- */
	class ListItem {
		int note_pageViewId; // ノートの１ページ目のViewのid
		String note_title; // ノートのタイトル
		String note_category; // ノートのカテゴリ
		String note_createdate; // ノートの作成日
		String note_updatedate; // ノートの更新日
		String note_remark; // ノートの備考

		// コンストラクタ
		ListItem(int note_pageViewId, String note_title, String note_category, String note_createdate,String note_updatedate, String note_remark) {
			this.note_pageViewId = note_pageViewId;
			this.note_title = note_title;
			this.note_category = note_category;
			this.note_createdate = note_createdate;
			this.note_updatedate = note_updatedate;
			this.note_remark = note_remark;
		}
	}

	/* -------------- リストのビューホルダー(Listに表示する文言) -------------- */
	static class ViewHolder {
		ImageView imageView; // ノートの１ページ目のView表示用
		TextView titleTextView; // ノートのタイトル表示用
		TextView categoryTextView; // ノートのカテゴリ表示用
		TextView createDateTextView; // ノートの作成日表示用
		TextView updateDateTextView; // ノートの更新日表示用
		TextView remarkTextView; // ノートの備考表示用
	}

	/* -------------- リストのカスタムアダプター -------------- */
	class ListItemAdapter extends ArrayAdapter<ListItem> {
		
		// レイアウトファイルからViewオブジェクトを生成するため
		LayoutInflater mInflater;

		// コンストラクタ
		ListItemAdapter(Context context, List<ListItem> items) {
			super(context, 0, items);
			mInflater = getLayoutInflater();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// ListItemオブジェクトを取得
			ListItem item = getItem(position);
			ViewHolder holder;
			// convertViewには使いまわすためのViewがあれば入っている
			if (convertView == null) {
				// ない場合はレイアウトファイルから生成する
				convertView = mInflater.inflate(R.layout.activity_read_note,
						null);
				// ViewHolderも作って
				holder = new ViewHolder();
				// 参照をセット
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.note_1pageView);
				holder.titleTextView = (TextView) convertView
						.findViewById(R.id.edit_note_create_title);
				holder.categoryTextView = (TextView) convertView
						.findViewById(R.id.note_category);
				holder.createDateTextView = (TextView) convertView
						.findViewById(R.id.note_createdate);
				holder.updateDateTextView = (TextView) convertView
						.findViewById(R.id.note_updatedate);
				holder.remarkTextView = (TextView) convertView
						.findViewById(R.id.edit_note_remark);
				// ViewHolderを使いまわせるようにセットしておく
				convertView.setTag(holder);
			} else {
				// ある場合はViewHolderを取り出して再利用
				holder = (ViewHolder) convertView.getTag();
			}
			// ホルダーに値をセット
			holder.imageView.setImageResource(item.note_pageViewId);
			holder.titleTextView.setText(item.note_title);
			holder.categoryTextView.setText(item.note_category);
			holder.createDateTextView.setText(item.note_createdate);
			holder.updateDateTextView.setText(item.note_updatedate);
			holder.remarkTextView.setText(item.note_remark);
			// 表示するViewを返す
			return convertView;
		}
	}

	/* -------------- リストの情報を取得 -------------- */
	public void getList() {
		
		// データを作る
		List<ListItem> items = new ArrayList<ListItem>();
		items = getNoteInfomation();

		// リストにアイテムを表示
		ListItemAdapter adapter = new ListItemAdapter(this, items);
		setListAdapter(adapter);

		// リストのアイテムをタッチした時の処理
		ListView listView = getListView();
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int list_id;
				ListView listView = (ListView) parent;
				Intent intent = new Intent(ReadNoteActivity.this, NoteActivity.class);	
				
				// position(アダプターの順番)から、idを取得する
				list_id = record_list.get(position).getId();
				intent.putExtra("_id", list_id);
				startActivity(intent);
			}
		});
	}

	/* -------------- ノートの情報を取得 -------------- */
	public ArrayList<ListItem> getNoteInfomation() {

		ArrayList<ListItem> note_list = new ArrayList<ListItem>();
		record_list = new ArrayList<RecordList>();
		
		// フィールド
		int id;
		String title;
		String category_name;
		String create_date;
		String update_date;
		String remark;
		boolean delete_flg;

		// ノートDBの呼び出し
		NoteSQLiteOpenHelper note_helper = new NoteSQLiteOpenHelper(this);
		SQLiteDatabase note_db = note_helper.getReadableDatabase();

		// ノートテーブル・カテゴリテーブルを結合し、テーブル内の情報を取得
		String column = "category_id";
		String sql = "SELECT * FROM " + NoteSQLiteOpenHelper.NOTE_TABLE
				+ " INNER JOIN "
				+ NoteCategorySQLiteOpenHelper.NOTE_CATEGORY_TABLE + " ON "
				+ NoteSQLiteOpenHelper.NOTE_TABLE + "." + column + " = "
				+ NoteCategorySQLiteOpenHelper.NOTE_CATEGORY_TABLE + "."
				+ column + ";";
		Cursor cursor = note_db.rawQuery(sql, null);

		// 参照先を一番始めにし、データを取得していく
		boolean isEof = cursor.moveToFirst();
		while (isEof) {
			// データベースからデータ取得
			id = cursor.getInt(cursor.getColumnIndex("_id"));
			title = cursor.getString(cursor.getColumnIndex("title"));
			category_name = cursor.getString(cursor.getColumnIndex("category_name"));
			create_date = cursor.getString(cursor.getColumnIndex("create_date"));
			update_date = cursor.getString(cursor.getColumnIndex("update_date"));
			remark = cursor.getString(cursor.getColumnIndex("remark"));
			delete_flg = getDeleteFlg(cursor.getInt(cursor.getColumnIndex("delete_flg")));
			
			// デリートフラグが立っていない時、リストに追加
			if(!delete_flg){
				// ノートをリストアイテムに追加する
				note_list.add(new ListItem(R.drawable.note_icon, title, category_name, create_date, update_date, remark));
				record_list.add(new RecordList(id));
			}
			isEof = cursor.moveToNext();
		}

		cursor.close();
		note_db.close();

		return note_list;
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
		getMenuInflater().inflate(R.menu.read_note, menu);
		return true;
	}

	// MENUのリストの中から選択し、該当ウィンドウを表示
	// (ノート読込画面では、表示切替のみ)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		// 新規ノート作成画面に遷移
		case R.id.button_createnote:
			Intent i = new Intent(this, CreateNoteActivity.class);
			startActivity(i);
			break;
		// ノート読込画面の表示を切替
		case R.id.note_read_switch:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	/* -------------- MENU END -------------- */

}
