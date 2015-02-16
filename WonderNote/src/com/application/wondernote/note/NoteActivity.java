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

// �m�[�g���
public class NoteActivity extends Activity {

	// �t�B�[���h
	NoteContents note;
	boolean bSaveFlg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Activity�N�����ɁA�\�t�g�E�F�A�L�[�{�[�h���\������Ȃ��悤�ɂ���
		this.getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_note);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// DB����m�[�g�����擾����
		Intent i = getIntent();
		getNoteInfomation(i.getIntExtra("_id", -1));

		// �m�[�g�̏����Z�b�g����
		setNoteInfomation();
	}

	/* -------------- �m�[�g�̏����擾 -------------- */
	public void getNoteInfomation(int id) {

		// �t�B�[���h(�擾�p�f�[�^)
		String title = "";
		int page = 0;
		int total_pages = 0;
		String date = "";
		String content = "";

		// �V�K�쐬�łȂ��Ƃ��A�f�[�^��ǂݍ���
		if (id != -1) {
			// DB�̌Ăяo��
			NoteSQLiteOpenHelper note_helper = new NoteSQLiteOpenHelper(this);
			SQLiteDatabase note_db = note_helper.getReadableDatabase();

			// �m�[�g�e�[�u���E�R���e���c�e�[�u�����������A�e�[�u�����̏����擾
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

			// �Q�Ɛ����Ԏn�߂ɂ��A�f�[�^���擾���Ă���
			boolean isEof = cursor.moveToFirst();
			while (isEof) {
				title = cursor.getString(cursor.getColumnIndex("title"));
				page = cursor.getInt(cursor.getColumnIndex("page"));
				total_pages = cursor.getInt(cursor
						.getColumnIndex("total_pages"));
				date = cursor.getString(cursor.getColumnIndex("create_date"));
				content = cursor.getString(cursor.getColumnIndex("content"));

				// �l���R���X�g���N�^�ɔz�u
				note = new NoteContents(id, title, page, total_pages, date,
						content);

				// ���̍s�փJ�[�\���ړ�
				isEof = cursor.moveToNext();
			}
			cursor.close();
			note_db.close();
		}
		// �V�K�쐬�̂Ƃ�
		else {
			date = createDate();
			note = new NoteContents(id, title, page, total_pages, date, content);
		}
	}

	/* -------------- �m�[�g�̏����Z�b�g -------------- */
	public void setNoteInfomation() {

		// �m�[�g�^�C�g���̔z�u
		EditText titleEditText = (EditText) findViewById(R.id.edit_note_create_title);
		titleEditText.setText(note.getNoteTitle());

		// ���t
		EditText dateEditText = (EditText) findViewById(R.id.edit_note_date);
		dateEditText.setText(note.getNoteDate());

		// ���݃y�[�W�Ƒ��y�[�W
		String pages = (note.getNotePage() + 1) + " / "
				+ (note.getNoteTotalPages() + 1);
		TextView pagesTextView = (TextView) findViewById(R.id.note_page);
		pagesTextView.setText(pages);

		// �m�[�g�̓��e
		EditText contentEditText = (EditText) findViewById(R.id.edit_note_context);
		contentEditText.setText(note.getNoteContent());
	}

	/* -------------- �m�[�g�ۑ��_�C�A���O�̕\��-------------- */
	public void showSaveDialog() {

		// �_�C�A���O�̕\��
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("�m�[�g�̕ۑ�");
		alert.setMessage("�m�[�g��ۑ����܂����H");

		alert.setNegativeButton("�L�����Z��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// �L�����Z���{�^���������ꂽ���͏I������
				finish();
			}
		});
		alert.setPositiveButton("�ۑ�", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// �����m�[�g�ŕۑ��{�^���������ꂽ�Ƃ��́A���̂܂ܕۑ�
				if (note.getNoteId() != -1) {
					saveNoteInfomation();
				}
				// �V�K�쐬�ŕۑ����������́A����|�b�v�A�b�v��\������
				else {
					showSaveNewCreateNoteDialog();
				}
				Toast.makeText(NoteActivity.this, "�ۑ����܂���", Toast.LENGTH_LONG)
						.show();
				finish();
			}
		});
		alert.show();
	}

	/* -------------- �m�[�g�̏���ۑ�(�V�K�ۑ��̏ꍇ) -------------- */
	public void showSaveNewCreateNoteDialog() {
		// �J�X�^���r���[��ݒ�
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.activity_create_note,
				(ViewGroup) findViewById(R.id.dialog_createnote));

		// �A���[�g�_�C�A���O �𐶐�
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("�V�K�m�[�g�̕ۑ�");
		builder.setView(layout);
		builder.setNegativeButton("�L�����Z��", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// �L�����Z���{�^���������ꂽ���͏I������
				finish();
			}
		});
		builder.setPositiveButton("�ۑ�", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				saveNoteInfomation();
			}
		});
		// �\��
		builder.create().show();
	}

	/* -------------- �m�[�g�̏���ۑ�(�����m�[�g�ۑ�) -------------- */
	public void saveNoteInfomation() {

		// �`�F�b�N�����J�e�S����ID�̔��菈�����쐬���A����ۑ����|�b�v�A�b�v��\��
		int _id = note.getNoteId();

		// edittext����f�[�^���擾
		// �m�[�g�^�C�g��
		EditText titleEditText = (EditText) findViewById(R.id.edit_note_create_title);
		String title = titleEditText.getText().toString();
		// �m�[�g�̓��e
		EditText contentEditText = (EditText) findViewById(R.id.edit_note_context);
		String content = contentEditText.getText().toString();

		// DB�̌Ăяo��
		NoteContentsSQLiteOpenHelper note_helper = new NoteContentsSQLiteOpenHelper(
				this);
		SQLiteDatabase note_content_db = note_helper.getWritableDatabase();

		// where����id��ݒ肷��
		String whereClause = "_id = " + _id;

		// �������݂���f�[�^��valses�ɒǉ�
		ContentValues values = new ContentValues();
		values.put("content", content);

		// �f�[�^�̍X�V
		note_content_db.update(
				NoteContentsSQLiteOpenHelper.NOTE_CONTENTS_TABLE, values,
				whereClause, null);

		// �f�[�^��DB�ɏ�������
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

	/* -------------- �m�[�g�̏���ۑ�(�V�K�m�[�g�ۑ�) -------------- */
	public void saveNoteInfomation(String title, int catetory, String remark) {

		// �`�F�b�N�����J�e�S����ID�̔��菈�����쐬���A����ۑ����|�b�v�A�b�v��\��
		int _id = note.getNoteId();
		
		// �m�[�g�̓��e
		EditText contentEditText = (EditText) findViewById(R.id.edit_note_context);
		String content = contentEditText.getText().toString();

		// �m�[�g�Ǎ���ʂ���m�[�g��ҏW���A�ۑ����悤�Ƃ����Ƃ�
		if (_id != -1) {

			// DB�̌Ăяo��
			NoteContentsSQLiteOpenHelper note_helper = new NoteContentsSQLiteOpenHelper(
					this);
			SQLiteDatabase note_content_db = note_helper.getWritableDatabase();

			// where����id��ݒ肷��
			String whereClause = "_id = " + _id;

			// �������݂���f�[�^��valses�ɒǉ�
			ContentValues values = new ContentValues();
			values.put("content", content);

			// �f�[�^�̍X�V
			note_content_db.update(
					NoteContentsSQLiteOpenHelper.NOTE_CONTENTS_TABLE, values,
					whereClause, null);

			// �f�[�^��DB�ɏ�������
			long result = note_content_db.update(
					NoteContentsSQLiteOpenHelper.NOTE_CONTENTS_TABLE, values,
					whereClause, null);
			note_content_db.close();

			if (result != -1) {
				Toast.makeText(this, R.string.dialog_savecomplete,
						Toast.LENGTH_LONG).show();

			}
		}

		// �V�K�m�[�g�쐬�̏ꍇ
		else {

			// DB�̌Ăяo��
			NoteSQLiteOpenHelper note_helper = new NoteSQLiteOpenHelper(this);
			SQLiteDatabase note_db = note_helper.getWritableDatabase();

			// id��V�K�Ɋ��蓖�Ă邽�߂ɁADB����id���擾
			String sql = "SELECT _id FROM " + NoteSQLiteOpenHelper.NOTE_TABLE
					+ ";";
			Cursor cursor = note_db.rawQuery(sql, null);
			// �Q�Ɛ����Ԏn�߂ɂ��A�f�[�^���擾���Ă���
			boolean isEof = cursor.moveToFirst();
			while (isEof) {
				_id = cursor.getInt(cursor.getColumnIndex("_id")) + 1;
				isEof = cursor.moveToNext();
			}
			cursor.close();

			// DB�ɏ�������
			sql = "insert into " + NoteSQLiteOpenHelper.NOTE_TABLE + " values("
					+ _id + "," + title + "," + 0 + "," + note.getNoteDate()
					+ "," + note.getNoteDate() + "," + "" + "," + 0 + ");";
			note_db.execSQL(sql);

			// DB�ɏ�������
			sql = "insert into "
					+ NoteContentsSQLiteOpenHelper.NOTE_CONTENTS_TABLE
					+ " values(" + _id + "," + note.getNotePage() + ","
					+ note.getNoteTotalPages() + "," + content + ");";
			note_db.execSQL(sql);
			note_db.close();
		}
		finish();
	}

	/* -------------- �m�[�g�쐬���̎����ϊ�����(�V�K�m�[�g�쐬��) -------------- */
	public String createDate() {
		// �쐬���ɁA�����̓��t���w��
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
		return date;
	}

	/* -------------- �߂�{�^���������̏��� -------------- */
	// �߂�{�^�������������A�_�C�A���O��\������
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
	// MENU�������N�����\�b�h
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.note, menu);
		return true;
	}

	// MENU�̃��X�g�̒�����I�����A�Y���E�B���h�E��\��
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
