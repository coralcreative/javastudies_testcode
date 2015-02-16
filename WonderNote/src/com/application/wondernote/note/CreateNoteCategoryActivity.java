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

// �J�e�S���ǉ��E�ҏW���
public class CreateNoteCategoryActivity extends Activity implements
		OnClickListener {

	// �t�B�[���h
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

		// Activity�N�����ɁA�\�t�g�E�F�A�L�[�{�[�h���\������Ȃ��悤�ɂ���
		this.getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_create_note_category);

		// �e�{�^���������ɁA�ǂ̉�ʂɑJ�ڂ����邩��o�^����
		// �J�e�S���ǉ�
		Button add_category = (Button) findViewById(R.id.button_add);
		add_category.setOnClickListener(this);
		// �J�e�S���ύX
		Button change_category = (Button) findViewById(R.id.button_change);
		change_category.setOnClickListener(this);
		// �J�e�S���폜
		Button delete_category = (Button) findViewById(R.id.button_delete);
		delete_category.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// DB����J�e�S�������擾���A���X�g�r���[�̍��ڂɒǉ�
		lv = (ListView) findViewById(R.id.note_category_list);

		// �m�[�g�J�e�S�����e�[�u������擾����
		getNoteCategory();

		// adapter�ɒl���Z�b�g
		int count = 0;
		String[] category_name = new String[category.size()];
		for (RecordCategoryList c : category) {
			category_name[count] = c.getCategoryName();
			count++;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_single_choice, // ���W�I�{�^�����������X�g
				category_name);

		// �`�F�b�N�{�b�N�X�ɔz�u���A�l���Z�b�g
		lv.setAdapter(adapter);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		if (checkedPosition >= 0) {
			// �f�t�H���g�őI�����Ă�������
			lv.setItemChecked(checkedPosition, true);
		}
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent,
					final View view, final int position, final long id) {
				// ���ڂ��I�����ꂽ�Ƃ��̏���
				setCheckedPosition(position);
			}
		});
	}

	// �{�^���������̏���
	@Override
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		// �J�e�S���ǉ��{�^��������
		case R.id.button_add:
			addCategory();
			break;
		// �J�e�S���ҏW�{�^��������
		case R.id.button_change:
			changeCategory();
			break;
		// �J�e�S���폜�{�^��������
		case R.id.button_delete:
			Toast.makeText(this, "������", Toast.LENGTH_LONG).show();
			// deleteCategory();
			break;
		}
	}

	/* -------------- �m�[�g�J�e�S���̎擾���� -------------- */
	public void getNoteCategory() {

		category = new ArrayList<RecordCategoryList>();

		// �J�e�S��DB�̌Ăяo��
		NoteCategorySQLiteOpenHelper category_helper = new NoteCategorySQLiteOpenHelper(
				this);
		SQLiteDatabase category_db = category_helper.getReadableDatabase();

		// �e�[�u������J�e�S����������
		String sql = "SELECT * FROM NOTE_CATEGORY_TABLE WHERE category_id <> ?";
		Cursor cursor = category_db.rawQuery(sql, new String[] { "0" });

		int category_id;
		String category_name;
		boolean delete_flg;

		// �Q�Ɛ����Ԏn�߂ɂ��A�f�[�^���擾���Ă���
		boolean isEof = cursor.moveToFirst();
		while (isEof) {
			// �f�[�^�x�[�X����l���擾����
			category_id = cursor.getInt(cursor.getColumnIndex("category_id"));
			;
			category_name = cursor.getString(cursor
					.getColumnIndex("category_name"));
			delete_flg = getDeleteFlg(cursor.getInt(cursor
					.getColumnIndex("category_delete_flg")));

			// �f���[�g�t���O�������Ă��Ȃ����A���X�g�ɒǉ�
			if (!delete_flg) {
				// �J�e�S�������X�g�A�C�e���ɒǉ�����
				category.add(new RecordCategoryList(category_id, category_name,
						delete_flg));
			}
			isEof = cursor.moveToNext();
		}
		cursor.close();
		category_db.close();
	}

	/* -------------- �m�[�g�J�e�S���̒ǉ����� -------------- */
	public void addCategory() {

		// �J�e�S�����̕���������
		if (!checkCategoryLength()) {
			return;
		}

		// �J�e�S��DB�̌Ăяo��
		NoteCategorySQLiteOpenHelper category_helper = new NoteCategorySQLiteOpenHelper(
				this);
		SQLiteDatabase category_db = category_helper.getWritableDatabase();

		// �J�e�S�����̎擾
		int count = 0;
		String sql = "SELECT * FROM NOTE_CATEGORY_TABLE";
		Cursor cursor = category_db.rawQuery(sql, null);
		boolean isEof = cursor.moveToFirst();
		while (isEof) {
			count++;
			isEof = cursor.moveToNext();
		}
		cursor.close();

		// �J�e�S�����̏d������
		if (!checkCategoryNameOverlap()) {
			category_db.close();
			return;
		}

		// values�Ƀf�[�^��}��
		ContentValues values = new ContentValues();
		values.put("category_id", count);
		values.put("category_name", getCategoryName());
		values.put("category_delete_flg", 0);

		// �f�[�^��DB�ɏ�������
		long result = category_db.insert(
				NoteCategorySQLiteOpenHelper.NOTE_CATEGORY_TABLE, null, values);
		category_db.close();

		if (result != -1) {
			Toast.makeText(this, R.string.dialog_addcomplete, Toast.LENGTH_LONG)
					.show();
			finish();
		}
	}

	/* -------------- �m�[�g�J�e�S���̕ҏW���� -------------- */
	public void changeCategory() {

		// ���W�I�{�^���̔���
		if (!checkCategoryRadioButton()) {
			return;
		}

		// �J�e�S�����̕���������
		if (!checkCategoryLength()) {
			return;
		}

		// �J�e�S��DB�̌Ăяo��
		NoteCategorySQLiteOpenHelper category_helper = new NoteCategorySQLiteOpenHelper(
				this);
		SQLiteDatabase category_db = category_helper.getWritableDatabase();

		// �J�e�S�����̎擾
		int count = 0;
		String sql = "SELECT * FROM NOTE_CATEGORY_TABLE";
		Cursor cursor = category_db.rawQuery(sql, null);
		boolean isEof = cursor.moveToFirst();
		while (isEof) {
			count++;
			isEof = cursor.moveToNext();
		}
		cursor.close();

		// �J�e�S�����̏d������
		if (!checkCategoryNameOverlap()) {
			category_db.close();
			return;
		}

		// �`�F�b�N�����J�e�S����ID�̔��菈�����쐬
		int category_id = category.get(getCheckedPosition()).getCategoryId();
		String whereClause = "category_id = " + category_id;

		// �������݂���f�[�^��valses�ɒǉ�
		ContentValues values = new ContentValues();
		values.put("category_name", getCategoryName());

		// �f�[�^�̍X�V
		category_db.update(NoteCategorySQLiteOpenHelper.NOTE_CATEGORY_TABLE,
				values, whereClause, null);

		// �f�[�^��DB�ɏ�������
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

	/* -------------- �m�[�g�J�e�S���̍폜���� -------------- */
	public void deleteCategory() {

		// ���W�I�{�^���̔���
		if (!checkCategoryRadioButton()) {
			return;
		}

		// �J�e�S��DB�̌Ăяo��
		NoteCategorySQLiteOpenHelper category_helper = new NoteCategorySQLiteOpenHelper(
				this);
		SQLiteDatabase category_db = category_helper.getWritableDatabase();

		// �`�F�b�N�����J�e�S����ID�̔��菈�����쐬
		int category_id = category.get(getCheckedPosition()).getCategoryId();
		String whereClause = "category_id = " + category_id;

		// �������݂���f�[�^��valses�ɒǉ�
		ContentValues values = new ContentValues();
		values.put("category_delete_flg", 1);

		// �f�[�^�̍X�V
		category_db.update(NoteCategorySQLiteOpenHelper.NOTE_CATEGORY_TABLE,
				values, whereClause, null);

		// �����ɁANOTE_TABLE�̓��J�e�S�����ucategory_id=0(������)�v�ɂ��鏈����ǉ�

		// �f�[�^��DB�ɏ�������
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

	/* -------------- �J�e�S�������������菈�� -------------- */
	public boolean checkCategoryLength() {

		// �J�e�S�����̕�����ƕ��������擾
		EditText edit_category = (EditText) findViewById(R.id.note_category_name);
		int length = edit_category.getText().length();

		// �J�e�S����������l�̏ꍇ�́A�|�b�v�A�b�v�͕\�������Ƀf�[�^���Z�b�g����B
		if (!(length == 0) && !(length > 20)) {
			setCategoryName(edit_category.getText().toString());
			return true;
		}

		// �A���[�g�_�C�A���O�쐬
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		LinearLayout alertview = new LinearLayout(this);
		alertview.setOrientation(LinearLayout.VERTICAL);
		TextView text1 = new TextView(this);

		// �J�e�S�����������͂̎��A�|�b�v�A�b�v��\������B
		if (length == 0) {
			text1.setText("�J�e�S�������󗓂ł��B\n�J�e�S��������͂��Ă��������B");
		}
		// �J�e�S������20�����ȏ�̎��A�|�b�v�A�b�v��\������B
		else if (length > 20) {
			text1.setText("�J�e�S������20�����𒴂��Ă��܂��B\n�J�e�S������20�����ȓ��ɂ��Ă��������B");
		}

		// �_�C�A���O��view��ݒ�
		alert.setTitle("�G���[");
		alertview.addView(text1);
		alert.setView(alertview);
		alert.setPositiveButton("����", null);
		alert.show();
		return false;
	}

	/* -------------- �J�e�S�����̏d�����菈�� -------------- */
	public boolean checkCategoryNameOverlap() {

		String category_name = "";
		String edit_category_name = "";
		boolean bCategoryNameFlg = false;
		
		EditText edit_category = (EditText) findViewById(R.id.note_category_name);
		edit_category_name = (String)edit_category.getText().toString();

		// �J�e�S���������o�^����Ă��Ȃ����Atrue
		if(category.size() == 0){
			bCategoryNameFlg = true;
		}
		// �J�e�S����1�ȏ�o�^����Ă���ꍇ�A�J�e�S�����̔�����s��
		else{
			// �J�e�S�������擾����
			for (RecordCategoryList c : category) {
				
				// ���͂����J�e�S�������e�[�u���ɑ��݂����ꍇ��break���Afalse��Ԃ�
				category_name = c.getCategoryName();
				if (category_name.equals(edit_category_name)) {
					bCategoryNameFlg = false;
					break;
				}
				// ���͂����J�e�S�������e�[�u���ɑ��݂��Ȃ������ꍇ��true��Ԃ�
				else {
					bCategoryNameFlg = true;
				}
			}
		}
		
		// false�������ꍇ�A�A���[�g��\������
		if(!bCategoryNameFlg){
			// �A���[�g�_�C�A���O�쐬
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			LinearLayout alertview = new LinearLayout(this);
			alertview.setOrientation(LinearLayout.VERTICAL);
			TextView text1 = new TextView(this);
			text1.setText("�J�e�S���������ɓo�^����Ă��܂��B\n�ʂ̃J�e�S��������͂��Ă��������B");

			// �_�C�A���O��view��ݒ�
			alert.setTitle("�G���[");
			alertview.addView(text1);
			alert.setView(alertview);
			alert.setPositiveButton("����", null);
			alert.show();
		}

		return bCategoryNameFlg;
	}

	/* -------------- �J�e�S�����W�I�{�^�����菈�� -------------- */
	public boolean checkCategoryRadioButton() {

		// ���X�g�ɃJ�e�S�������擾
		int size = category.size();
		if (size == 0) {
			// �J�e�S�����o�^�̂Ƃ��A�A���[�g�\��
		}else{
			return true;
		}

		// ���W�I�{�^���������I������Ă��Ȃ����́A�A���[�g�\��
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		LinearLayout alertview = new LinearLayout(this);
		alertview.setOrientation(LinearLayout.VERTICAL);
		TextView text1 = new TextView(this);

		// ���W�I�{�^�����I���̎��A�|�b�v�A�b�v��\������B
		text1.setText("�J�e�S�����o�^����Ă��܂���B\n�J�e�S��������͂��u�ǉ��v���^�b�v���Ă��������B");

		// �_�C�A���O��view��ݒ�
		alert.setTitle("�G���[");
		alertview.addView(text1);
		alert.setView(alertview);
		alert.setPositiveButton("����", null);
		alert.show();
		return false;
	}

	/* -------------- GETTER��SETTER -------------- */
	// �J�e�S����
	public void setCategoryName(String category_name) {
		this.category_name = category_name;
	}

	public String getCategoryName() {
		return this.category_name;
	}

	// ���X�g�̑I����������
	public void setCheckedPosition(int checkedPosition) {
		this.checkedPosition = checkedPosition;
	}

	public int getCheckedPosition() {
		return this.checkedPosition;
	}

	/* ---------------- delete_flg���� ---------------- */
	public boolean getDeleteFlg(int delete_flg) {
		// �f���[�g�t���O�������Ă��Ȃ��ꍇfalse(�폜���ڂłȂ�)
		if (delete_flg != 1) {
			return false;
		}
		// �f���[�g�t���O�������Ă��鎞true(�폜����)
		else {
			return true;
		}
	}

}
