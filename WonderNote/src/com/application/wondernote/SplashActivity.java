package com.application.wondernote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Window;

// �X�v���b�V�����
public class SplashActivity extends Activity {

	// Activity�����N�����\�b�h
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// �^�C�g���͔�\��
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// activity_splash.xml��View�Ɏw�肷��
		setContentView(R.layout.activity_splash);
		// 3�b�A�x��������splashHandler�����s
		Handler hdl = new Handler();
		hdl.postDelayed(new splashHandler(), 3000);
	}

	// splashHander(����ʑJ��)���\�b�h
	class splashHandler implements Runnable {
		public void run() {
			// �X�v���b�V��������Ɏ��s����Activity���w��
			Intent intent = new Intent(getApplication(), HomeActivity.class);
			startActivity(intent);
			// SplashActivity���I��������
			SplashActivity.this.finish();
		}
	}

	// �߂�{�^���̑���𖳌�������
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	
}