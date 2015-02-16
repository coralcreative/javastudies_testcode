package com.application.wondernote.note.util;

import android.text.format.Time;
import android.widget.EditText;

import com.application.wondernote.R;
import com.application.wondernote.note.NoteActivity;

// �m�[�g�̓��e
public class NoteContents {

	// �t�B�[���h
	int _id;
	String title;
	int page;
	int total_pages;
	String date;
	String content;

	// �R���X�g���N�^
	public NoteContents(int _id, String title, int page, int total_pages,
			String date, String content) {
		this._id = _id;
		this.title = title;
		this.page = page;
		this.total_pages = total_pages;
		this.date = date;
		this.content = content;
	}

	/* -------------- GETTER��SETTER -------------- */
	// �m�[�gID
	public void setNoteId(int _id) {
		this._id = _id;
	}
	public int getNoteId() {
		return this._id;
	}
	// �m�[�g�^�C�g��
	public void setNoteTitle(String title) {
		this.title = title;
	}
	public String getNoteTitle() {
		return this.title;
	}
	// �m�[�g�̌��݃y�[�W
	public void setNotePage(int page) {
		this.page = page;
	}
	public int getNotePage() {
		return this.page;
	}
	// �m�[�g�̑��y�[�W
	public void setNoteTotalPages(int total_pages) {
		this.total_pages = total_pages;
	}
	public int getNoteTotalPages() {
		return this.total_pages;
	}
	// �m�[�g�̓��t
	public void setNoteDate(String date) {
		this.date = date;
	}
	public String getNoteDate() {
		return this.date;
	}
	// �m�[�g�̓��e
	public void setNoteContent(String content) {
		this.content = content;
	}
	public String getNoteContent(){
		return this.content;
	}
}
