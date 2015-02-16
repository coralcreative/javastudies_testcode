package com.application.wondernote.note.util;

// カテゴリIDとアダプターの順番を記録
public class RecordCategoryList {
	
	// フィールド
	int category_id;
	String category_name;
	boolean category_delete_flg;

	// コンストラクタ
	public RecordCategoryList(int id, String name, boolean flg){
		category_id = id;
		category_name = name;
		category_delete_flg = flg;
	}
	
	/* -------------- GETTERとSETTER -------------- */
	// カテゴリID
	public void setCategoryId(int category_id) {
		this.category_id = category_id;
	}
	public int getCategoryId() {
		return this.category_id;
	}
	// カテゴリ名
	public void setCategoryName(String category_name) {
		this.category_name = category_name;
	}
	public String getCategoryName() {
		return this.category_name;
	}
	// カテゴリ名
	public void setCategoryDeleteFlg(boolean category_delete_flg) {
		this.category_delete_flg = category_delete_flg;
	}
	public boolean getCategoryDeleteFlg() {
		return this.category_delete_flg;
	}

}
