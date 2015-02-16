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

// �V�K�m�[�g�쐬���
public class CreateNoteActivity extends Activity implements OnClickListener {

	// �t�B�[���h
	ArrayList<RecordCategoryList> category;
	String note_title;
	int note_category;
	String note_create_date;
	String note_remark;

	// Activity�����N�����\�b�h
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Activity�N�����ɁA�\�t�g�E�F�A�L�[�{�[�h���\������Ȃ��悤�ɂ���
		this.getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_create_note);

		// �e�{�^���������ɁA�ǂ̉�ʂɑJ�ڂ����邩��o�^����
		// �m�[�g�V�K�쐬
		Button create = (Button) findViewById(R.id.button_create);
		create.setOnClickListener(this);
		// �L�����Z��
		Button cancel = (Button) findViewById(R.id.button_cancel);
		cancel.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// �X�s�i�[(�h���b�v�_�E�����X�g)�̐ݒ�
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// DB����J�e�S�������擾���A�X�s�i�[�ɒǉ�
		getCategoryList();
		for (RecordCategoryList c : category) {
			adapter.add(c.getCategoryName());
		}

		// �A�_�v�^�[��ݒ肵�܂�
		Spinner spinner = (Spinner) findViewById(id.spinner_note_category);
		spinner.setAdapter(adapter);

		// �X�s�i�[�̃A�C�e�����I�����ꂽ���ɌĂяo�����R�[���o�b�N���X�i�[��o�^���܂�
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// �I�����ꂽ�J�e�S��ID���Z�b�g
				setNoteCategory(category.get(position).getCategoryId());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		// �m�[�g�쐬���̐ݒ�
		insertCreateDate();
	}

	// �{�^���������̏���
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		// �m�[�g�Ǎ���ʂɑJ��
		case R.id.button_create:

			// �m�[�g�^�C�g���̕������`�F�b�N
			boolean bNoteTitleFlg = checkTitleLength();
			if (!bNoteTitleFlg) {
				break;
			}

			// ���l���̕������SETTER�ɑ}��
			EditText edit_note_remark = (EditText) findViewById(R.id.edit_note_remark);
			setNoteRemark(edit_note_remark.getText().toString());

			// DB�ɏ�񏑂����݂��Aid���擾
			int id = insertDBNoteData();

			// �m�[�g��ʂɑJ��
			i = new Intent(this, NoteActivity.class);
			i.putExtra("_id", id);
			startActivity(i);
			this.finish();
			break;

		// �m�[�g�ꗗ��ʂɑJ��
		case R.id.button_cancel:
			finish();
			break;
		}
	}

	/* -------------- �m�[�g�^�C�g�����������菈�� -------------- */
	public boolean checkTitleLength() {

		// �m�[�g�^�C�g���̕�����ƕ��������擾
		EditText edit_note_title = (EditText) findViewById(R.id.edit_note_create_title);
		int length = edit_note_title.getText().length();

		// �^�C�g��������l�̏ꍇ�́A�|�b�v�A�b�v�͕\�������Ƀf�[�^���Z�b�g����B
		if (!(length == 0) && !(length > 50)) {
			setNoteTitle(edit_note_title.getText().toString());
			return true;
		}

		// �A���[�g�_�C�A���O�쐬
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		LinearLayout alertview = new LinearLayout(this);
		alertview.setOrientation(LinearLayout.VERTICAL);
		TextView text1 = new TextView(this);

		// �^�C�g���������͂̎��A�|�b�v�A�b�v��\������B
		if (length == 0) {
			text1.setText("�^�C�g�����󗓂ł��B\n�^�C�g������͂��Ă��������B");
		}
		// �^�C�g����50�����ȏ�̎��A�|�b�v�A�b�v��\������B
		else if (length > 50) {
			text1.setText("�^�C�g����50�����𒴂��Ă��܂��B\n�^�C�g����50�����ȓ��ɂ��Ă��������B");
		}

		// �_�C�A���O��view��ݒ�
		alert.setTitle("�G���[");
		alertview.addView(text1);
		alert.setView(alertview);
		alert.setPositiveButton("����", null);
		alert.show();
		return false;
	}

	/* -------------- �m�[�g�쐬���̎������͏��� -------------- */
	public void insertCreateDate() {
		// �쐬���ɁA�����̓��t����͂���
		EditText create_date = (EditText) findViewById(R.id.note_createdate);
		Time time = new Time("Asia/Tokyo");
		time.setToNow();

		// 1~9���̏ꍇ�A�O��"0"��}������
		String month;
		if (time.month < 10) {
			month = "0" + String.valueOf(time.month + 1);
		} else {
			month = String.valueOf(time.month + 1);
		}

		// 1~9���̏ꍇ�A�O��"0"��}������
		String day;
		if (time.monthDay < 10) {
			day = "0" + String.valueOf(time.monthDay);
		} else {
			day = String.valueOf(time.monthDay);
		}

		// ���t���Z�b�g
		String date = time.year + "/" + month + "/" + day;
		create_date.setText(date);

		// �f�[�^��SETTER�ɑ}������
		setNoteCreateDate(date);

		// �ҏW�s�\�ɂ���
		create_date.setEnabled(false);
	}

	/* -------------- �m�[�g�J�e�S���̎擾���� -------------- */
	public void getCategoryList() {

		category = new ArrayList<RecordCategoryList>();

		// �J�e�S��DB�̌Ăяo��
		NoteCategorySQLiteOpenHelper category_helper = new NoteCategorySQLiteOpenHelper(this);
		SQLiteDatabase category_db = category_helper.getReadableDatabase();
				
		String sql = "SELECT * FROM NOTE_CATEGORY_TABLE;";
		Cursor cursor = category_db.rawQuery(sql, null);
		
		int category_id;
		String category_name;
		boolean delete_flg;
		
		// �Q�Ɛ����Ԏn�߂ɂ��A�f�[�^���擾���Ă���
		boolean isEof = cursor.moveToFirst();
		while (isEof) {
			// �f�[�^�x�[�X����l���擾����
			category_id = cursor.getInt(cursor.getColumnIndex("category_id"));;
			category_name = cursor.getString(cursor.getColumnIndex("category_name"));
			delete_flg = getDeleteFlg(cursor.getInt(cursor.getColumnIndex("category_delete_flg")));
			
			// �f���[�g�t���O�������Ă��Ȃ����A���X�g�ɒǉ�
			if(!delete_flg){
				// �J�e�S�������X�g�A�C�e���ɒǉ�����
				category.add(new RecordCategoryList(category_id, category_name, delete_flg));
			}
			isEof = cursor.moveToNext();
		}

		cursor.close();
		category_db.close();
	}

	/* -------------- DB�������ݏ��� -------------- */
	public int insertDBNoteData() {
		
		// DB�̌Ăяo��
		NoteSQLiteOpenHelper note_helper = new NoteSQLiteOpenHelper(this);
		SQLiteDatabase note_db = note_helper.getWritableDatabase();

		// �e�[�u������id������
		int id = 0;
		String sql = "SELECT * FROM " + NoteSQLiteOpenHelper.NOTE_TABLE + ";";
		Cursor cursor = note_db.rawQuery(sql, null);
		// �Q�Ɛ����Ԏn�߂ɂ��A�f�[�^���擾���Ă���
		boolean isEof = cursor.moveToFirst();
		while (isEof) {
			id = cursor.getInt(cursor.getColumnIndex("_id")) + 1;
			isEof = cursor.moveToNext();
		}
		cursor.close();

		// �������݂���f�[�^��valses�ɒǉ�
		ContentValues values = new ContentValues();
		values.put("_id", id);
		values.put("title", getNoteTitle());
		values.put("category_id", getNoteCategory());
		values.put("create_date", getNoteCreateDate());
		values.put("update_date", getNoteCreateDate());
		values.put("remark", getNoteRemark());
		values.put("delete_flg", 0);

		// �f�[�^�̏�������
		long result = note_db.insert(NoteSQLiteOpenHelper.NOTE_TABLE, null,
				values);
		
		// �m�[�g�R���e���cDB�̌Ăяo��
		NoteContentsSQLiteOpenHelper note_contents_helper = new NoteContentsSQLiteOpenHelper(this);
		note_db = note_contents_helper.getWritableDatabase();
		
		// �m�[�g�R���e���c(�m�[�g�̓��e)�e�[�u���ɂ������l��o�^
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

	/* -------------- GETTER��SETTER -------------- */
	// �m�[�g�^�C�g��
	public void setNoteTitle(String note_title) {
		this.note_title = note_title;
	}

	public String getNoteTitle() {
		return this.note_title;
	}

	// �m�[�g�J�e�S��
	public void setNoteCategory(int note_category) {
		this.note_category = note_category;
	}

	public int getNoteCategory() {
		return this.note_category;
	}

	// �m�[�g�쐬��
	public void setNoteCreateDate(String note_create_date) {
		this.note_create_date = note_create_date;
	}

	public String getNoteCreateDate() {
		return this.note_create_date;
	}

	// �m�[�g���l��
	public void setNoteRemark(String note_remark) {
		this.note_remark = note_remark;
	}

	public String getNoteRemark() {
		return this.note_remark;
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
		getMenuInflater().inflate(R.menu.create_note, menu);
		return true;
	}

	// MENU�̃��X�g�̒�����I�����A�Y���E�B���h�E��\��
	// (�m�[�g�Ǎ���ʂł́A�V�K�J�e�S���쐬�̂�)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		// �V�K�m�[�g�J�e�S���쐬��ʂɑJ��
		case R.id.button_createcategory:
			Intent i = new Intent(this, CreateNoteCategoryActivity.class);
			startActivity(i);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	/* -------------- MENU END -------------- */
}
