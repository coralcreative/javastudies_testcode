package com.application.wondernote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Window;

// スプラッシュ画面
public class SplashActivity extends Activity {

	// Activity初期起動メソッド
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// タイトルは非表示
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// activity_splash.xmlをViewに指定する
		setContentView(R.layout.activity_splash);
		// 3秒、遅延させてsplashHandlerを実行
		Handler hdl = new Handler();
		hdl.postDelayed(new splashHandler(), 3000);
	}

	// splashHander(次画面遷移)メソッド
	class splashHandler implements Runnable {
		public void run() {
			// スプラッシュ完了後に実行するActivityを指定
			Intent intent = new Intent(getApplication(), HomeActivity.class);
			startActivity(intent);
			// SplashActivityを終了させる
			SplashActivity.this.finish();
		}
	}

	// 戻るボタンの操作を無効化する
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