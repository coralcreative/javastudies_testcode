package com.application.wondernote.note.util;

// �J�e�S��ID�ƃA�_�v�^�[�̏��Ԃ��L�^
public class RecordCategoryList {
	
	// �t�B�[���h
	int category_id;
	String category_name;
	boolean category_delete_flg;

	// �R���X�g���N�^
	public RecordCategoryList(int id, String name, boolean flg){
		category_id = id;
		category_name = name;
		category_delete_flg = flg;
	}
	
	/* -------------- GETTER��SETTER -------------- */
	// �J�e�S��ID
	public void setCategoryId(int category_id) {
		this.category_id = category_id;
	}
	public int getCategoryId() {
		return this.category_id;
	}
	// �J�e�S����
	public void setCategoryName(String category_name) {
		this.category_name = category_name;
	}
	public String getCategoryName() {
		return this.category_name;
	}
	// �J�e�S����
	public void setCategoryDeleteFlg(boolean category_delete_flg) {
		this.category_delete_flg = category_delete_flg;
	}
	public boolean getCategoryDeleteFlg() {
		return this.category_delete_flg;
	}

}
