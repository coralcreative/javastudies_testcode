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

// �m�[�g�Ǎ����
public class ReadNoteActivity extends ListActivity {
	
	// �t�B�[���h
	ArrayList<RecordList> record_list;

	// Activity�����N�����\�b�h
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// ���X�g�ɃA�C�e����\������
		getList();
	}

	/* -------------- ���X�g�̃A�C�e�� -------------- */
	class ListItem {
		int note_pageViewId; // �m�[�g�̂P�y�[�W�ڂ�View��id
		String note_title; // �m�[�g�̃^�C�g��
		String note_category; // �m�[�g�̃J�e�S��
		String note_createdate; // �m�[�g�̍쐬��
		String note_updatedate; // �m�[�g�̍X�V��
		String note_remark; // �m�[�g�̔��l

		// �R���X�g���N�^
		ListItem(int note_pageViewId, String note_title, String note_category, String note_createdate,String note_updatedate, String note_remark) {
			this.note_pageViewId = note_pageViewId;
			this.note_title = note_title;
			this.note_category = note_category;
			this.note_createdate = note_createdate;
			this.note_updatedate = note_updatedate;
			this.note_remark = note_remark;
		}
	}

	/* -------------- ���X�g�̃r���[�z���_�[(List�ɕ\�����镶��) -------------- */
	static class ViewHolder {
		ImageView imageView; // �m�[�g�̂P�y�[�W�ڂ�View�\���p
		TextView titleTextView; // �m�[�g�̃^�C�g���\���p
		TextView categoryTextView; // �m�[�g�̃J�e�S���\���p
		TextView createDateTextView; // �m�[�g�̍쐬���\���p
		TextView updateDateTextView; // �m�[�g�̍X�V���\���p
		TextView remarkTextView; // �m�[�g�̔��l�\���p
	}

	/* -------------- ���X�g�̃J�X�^���A�_�v�^�[ -------------- */
	class ListItemAdapter extends ArrayAdapter<ListItem> {
		
		// ���C�A�E�g�t�@�C������View�I�u�W�F�N�g�𐶐����邽��
		LayoutInflater mInflater;

		// �R���X�g���N�^
		ListItemAdapter(Context context, List<ListItem> items) {
			super(context, 0, items);
			mInflater = getLayoutInflater();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// ListItem�I�u�W�F�N�g���擾
			ListItem item = getItem(position);
			ViewHolder holder;
			// convertView�ɂ͎g���܂킷���߂�View������Γ����Ă���
			if (convertView == null) {
				// �Ȃ��ꍇ�̓��C�A�E�g�t�@�C�����琶������
				convertView = mInflater.inflate(R.layout.activity_read_note,
						null);
				// ViewHolder�������
				holder = new ViewHolder();
				// �Q�Ƃ��Z�b�g
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
				// ViewHolder���g���܂킹��悤�ɃZ�b�g���Ă���
				convertView.setTag(holder);
			} else {
				// ����ꍇ��ViewHolder�����o���čė��p
				holder = (ViewHolder) convertView.getTag();
			}
			// �z���_�[�ɒl���Z�b�g
			holder.imageView.setImageResource(item.note_pageViewId);
			holder.titleTextView.setText(item.note_title);
			holder.categoryTextView.setText(item.note_category);
			holder.createDateTextView.setText(item.note_createdate);
			holder.updateDateTextView.setText(item.note_updatedate);
			holder.remarkTextView.setText(item.note_remark);
			// �\������View��Ԃ�
			return convertView;
		}
	}

	/* -------------- ���X�g�̏����擾 -------------- */
	public void getList() {
		
		// �f�[�^�����
		List<ListItem> items = new ArrayList<ListItem>();
		items = getNoteInfomation();

		// ���X�g�ɃA�C�e����\��
		ListItemAdapter adapter = new ListItemAdapter(this, items);
		setListAdapter(adapter);

		// ���X�g�̃A�C�e�����^�b�`�������̏���
		ListView listView = getListView();
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int list_id;
				ListView listView = (ListView) parent;
				Intent intent = new Intent(ReadNoteActivity.this, NoteActivity.class);	
				
				// position(�A�_�v�^�[�̏���)����Aid���擾����
				list_id = record_list.get(position).getId();
				intent.putExtra("_id", list_id);
				startActivity(intent);
			}
		});
	}

	/* -------------- �m�[�g�̏����擾 -------------- */
	public ArrayList<ListItem> getNoteInfomation() {

		ArrayList<ListItem> note_list = new ArrayList<ListItem>();
		record_list = new ArrayList<RecordList>();
		
		// �t�B�[���h
		int id;
		String title;
		String category_name;
		String create_date;
		String update_date;
		String remark;
		boolean delete_flg;

		// �m�[�gDB�̌Ăяo��
		NoteSQLiteOpenHelper note_helper = new NoteSQLiteOpenHelper(this);
		SQLiteDatabase note_db = note_helper.getReadableDatabase();

		// �m�[�g�e�[�u���E�J�e�S���e�[�u�����������A�e�[�u�����̏����擾
		String column = "category_id";
		String sql = "SELECT * FROM " + NoteSQLiteOpenHelper.NOTE_TABLE
				+ " INNER JOIN "
				+ NoteCategorySQLiteOpenHelper.NOTE_CATEGORY_TABLE + " ON "
				+ NoteSQLiteOpenHelper.NOTE_TABLE + "." + column + " = "
				+ NoteCategorySQLiteOpenHelper.NOTE_CATEGORY_TABLE + "."
				+ column + ";";
		Cursor cursor = note_db.rawQuery(sql, null);

		// �Q�Ɛ����Ԏn�߂ɂ��A�f�[�^���擾���Ă���
		boolean isEof = cursor.moveToFirst();
		while (isEof) {
			// �f�[�^�x�[�X����f�[�^�擾
			id = cursor.getInt(cursor.getColumnIndex("_id"));
			title = cursor.getString(cursor.getColumnIndex("title"));
			category_name = cursor.getString(cursor.getColumnIndex("category_name"));
			create_date = cursor.getString(cursor.getColumnIndex("create_date"));
			update_date = cursor.getString(cursor.getColumnIndex("update_date"));
			remark = cursor.getString(cursor.getColumnIndex("remark"));
			delete_flg = getDeleteFlg(cursor.getInt(cursor.getColumnIndex("delete_flg")));
			
			// �f���[�g�t���O�������Ă��Ȃ����A���X�g�ɒǉ�
			if(!delete_flg){
				// �m�[�g�����X�g�A�C�e���ɒǉ�����
				note_list.add(new ListItem(R.drawable.note_icon, title, category_name, create_date, update_date, remark));
				record_list.add(new RecordList(id));
			}
			isEof = cursor.moveToNext();
		}

		cursor.close();
		note_db.close();

		return note_list;
	}
	
	/* ---------------- delete_flg���� ---------------- */
	public boolean getDeleteFlg(int delete_flg){
		// �f���[�g�t���O�������Ă��Ȃ��ꍇfalse(�폜���ڂłȂ�)
		if(delete_flg != 1){
			return false;
		}
		// �f���[�g�t���O�������Ă��鎞true(�폜����)
		else{
			return true;
		}	
	}

	/* -------------- MENU START -------------- */
	// MENU�������N�����\�b�h
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.read_note, menu);
		return true;
	}

	// MENU�̃��X�g�̒�����I�����A�Y���E�B���h�E��\��
	// (�m�[�g�Ǎ���ʂł́A�\���ؑւ̂�)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		// �V�K�m�[�g�쐬��ʂɑJ��
		case R.id.button_createnote:
			Intent i = new Intent(this, CreateNoteActivity.class);
			startActivity(i);
			break;
		// �m�[�g�Ǎ���ʂ̕\����ؑ�
		case R.id.note_read_switch:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	/* -------------- MENU END -------------- */

}
