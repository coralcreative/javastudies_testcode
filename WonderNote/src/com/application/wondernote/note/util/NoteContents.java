package com.application.wondernote.note.util;

import android.text.format.Time;
import android.widget.EditText;

import com.application.wondernote.R;
import com.application.wondernote.note.NoteActivity;

// ノートの内容
public class NoteContents {

	// フィールド
	int _id;
	String title;
	int page;
	int total_pages;
	String date;
	String content;

	// コンストラクタ
	public NoteContents(int _id, String title, int page, int total_pages,
			String date, String content) {
		this._id = _id;
		this.title = title;
		this.page = page;
		this.total_pages = total_pages;
		this.date = date;
		this.content = content;
	}

	/* -------------- GETTERとSETTER -------------- */
	// ノートID
	public void setNoteId(int _id) {
		this._id = _id;
	}
	public int getNoteId() {
		return this._id;
	}
	// ノートタイトル
	public void setNoteTitle(String title) {
		this.title = title;
	}
	public String getNoteTitle() {
		return this.title;
	}
	// ノートの現在ページ
	public void setNotePage(int page) {
		this.page = page;
	}
	public int getNotePage() {
		return this.page;
	}
	// ノートの総ページ
	public void setNoteTotalPages(int total_pages) {
		this.total_pages = total_pages;
	}
	public int getNoteTotalPages() {
		return this.total_pages;
	}
	// ノートの日付
	public void setNoteDate(String date) {
		this.date = date;
	}
	public String getNoteDate() {
		return this.date;
	}
	// ノートの内容
	public void setNoteContent(String content) {
		this.content = content;
	}
	public String getNoteContent(){
		return this.content;
	}
}
