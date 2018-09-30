package com.sungame.jinx;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import cn.mangofun.xsdk.openapi.XSDK;

/**
 * 类名 : Android 入口对象 <br/>
 * 作者 : canyon / 龚阳辉 <br/>
 * 时间 : 2018-01-02 17：01 <br/>
 * 功能 : 实现生命周期
 */
public class MainActivity extends com.unity3d.player.UnityPlayerActivity {
	TelephonyManager m_mgrTM = null;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		XSDK.getInstance().onConfigurationChanged(newConfig);
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		_bug4U56Fragment();
		initMHandler();
		this.m_mgrTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		XSDK.getInstance().init(MainActivity.this, SDKPlgJinx.getInstance());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		XSDK.getInstance().onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		SDKPlgJinx.reListenerState(this.m_mgrTM, true);
		XSDK.getInstance().onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		SDKPlgJinx.reListenerState(this.m_mgrTM, false);
		XSDK.getInstance().onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		XSDK.getInstance().onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		XSDK.getInstance().onNewIntent(intent);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		XSDK.getInstance().onRestart();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		XSDK.getInstance().onSaveInstanceState(outState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		XSDK.getInstance().onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		XSDK.getInstance().onStop();
	}
	
	void _bug4U56Fragment(boolean _isFlags) {
		getWindow().setFormat(2);
		this.mUnityPlayer = new CUnityPlayer(this);
	    if (_isFlags && this.mUnityPlayer.getSettings().getBoolean("hide_status_bar", true)) {
	      getWindow().setFlags(1024, 1024);
	    }
	    setContentView(this.mUnityPlayer);
	    this.mUnityPlayer.requestFocus();
	}
	
	void _bug4U56Fragment() {
		_bug4U56Fragment(true);
	}

	boolean _KeyCodeEvent(int keyCode, KeyEvent event) {
		// 拦截返回键
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			// 判断触摸UP事件才会进行返回事件处理 KeyEvent.ACTION_UP
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				onBackPressed();
				// 只要是返回事件，直接返回true，表示消费掉
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (_KeyCodeEvent(keyCode, event)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		SDKPlgJinx.getInstance().doExit();
	}

	void showTip4Exit() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this).setTitle("退出游戏").setMessage("您确定要退出游戏么?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SDKPlgJinx.getInstance().callExitGame(true);
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alertDialog.show();
	}

	void exitGame() {
		finish();
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}

	void initMHandler() {
		if (mHandler == null) {
			mHandler = new Handler(Looper.getMainLooper()) {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case 0:
						exitGame();
						break;
					case 1:
						showTip4Exit();
						break;
					default:
						break;
					}
				}

			};
		}
	}

	static private Handler mHandler = null;

	static public void sendMsg(int state) {
		if (mHandler == null)
			return;

		Message msg = new Message();
		msg.what = state;
		mHandler.sendMessage(msg);
	}
}
