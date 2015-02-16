package com.application.wondernote.note.util;

// リストIDとアダプターの順番を記録
public class RecordList {
	
	// フィールド
	int id;
	
	// コンストラクタ
	public RecordList(int id){
		this.id = id;
	}

	/* -------------- GETTER -------------- */
	// アダプターの順番
	public int getId(){
		return this.id;
	}
}
