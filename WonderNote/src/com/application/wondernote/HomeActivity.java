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

// �z�[�����
public class HomeActivity extends Activity implements OnClickListener{

	// Activity�����N�����\�b�h
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		// �e�{�^���������ɁA�ǂ̉�ʂɑJ�ڂ����邩��o�^����
		// �V�K�m�[�g�쐬���
		Button create_note = (Button)findViewById(R.id.button_jumpCreateNote);
		create_note.setOnClickListener(this);
		// �m�[�g�Ǎ����
		Button read_note = (Button)findViewById(R.id.button_jumpReadNote);
		read_note.setOnClickListener(this);
		// �ݒ���
		Button config_note = (Button)findViewById(R.id.button_jumpConfigNote);
		config_note.setOnClickListener(this);
		
		// �A�v������N�����ɁADB���쐬����
		StartDBHelper helper = new StartDBHelper(this);
		SQLiteDatabase db = helper.getReadableDatabase();
		db.close();
	}
	
	// �{�^���������̏���
	public void onClick(View v) {
		Intent i;
		switch(v.getId()){
		// �V�K�m�[�g�쐬��ʂɑJ��
        case R.id.button_jumpCreateNote:
        	i= new Intent(this, NoteActivity.class);
        	startActivity(i);
            break;
        // �m�[�g�ǂݎ���ʂɑJ��
        case R.id.button_jumpReadNote:
        	i= new Intent(this, ReadNoteActivity.class);
        	startActivity(i);
        	break;
        // �ݒ��ʂɑJ��
        case R.id.button_jumpConfigNote:
        	break;
        }
	}
	
	/* ---------------- DB�̍쐬 ---------------- */
	public class StartDBHelper extends SQLiteOpenHelper {

		// �R���X�g���N�^
		public StartDBHelper(Context context) {
			super(context, NoteSQLiteOpenHelper.NOTE_DATABASE, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(NoteSQLiteOpenHelper.CREATE_TABLE);
			db.execSQL(NoteCategorySQLiteOpenHelper.CREATE_TABLE);
			db.execSQL(NoteContentsSQLiteOpenHelper.CREATE_TABLE);

			// �J�e�S���e�[�u���Ɂu�����ށv��ǉ�
			String sql = "insert into "
					+ NoteCategorySQLiteOpenHelper.NOTE_CATEGORY_TABLE
					+ " values(0, '������', 0);";
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
		}
	}
}
