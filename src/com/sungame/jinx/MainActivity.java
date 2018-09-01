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

/**
 * 类名 : Android 入口对象 <br/>
 * 作者 : canyon / 龚阳辉 <br/>
 * 时间 : 2018-01-02 17：01 <br/>
 * 功能 : 实现生命周期
 */
public class MainActivity extends com.unity3d.player.UnityPlayerActivity {
	TelephonyManager m_mgrTM = null;
	@Override
	public void onConfigurationChanged(Configuration arg0) {
		super.onConfigurationChanged(arg0);
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		initMHandler();
		this.m_mgrTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
		//横屏
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		SDKPlgJinx.reListenerState(this.m_mgrTM,true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SDKPlgJinx.reListenerState(this.m_mgrTM,false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	boolean _KeyCodeEvent(int keyCode, KeyEvent event) {
		//拦截返回键
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            //判断触摸UP事件才会进行返回事件处理 KeyEvent.ACTION_UP
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                onBackPressed();
                //只要是返回事件，直接返回true，表示消费掉
                return true;
            }
        }
        return false;
	}
	
//	@Override
//	public boolean dispatchKeyEvent(KeyEvent event) {
//		if(_KeyCodeEvent(event.getKeyCode(), event)){
//			return true;
//		}
//		return super.dispatchKeyEvent(event);
//	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(_KeyCodeEvent(keyCode, event)){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	void showTip4Exit() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
				.setTitle("退出游戏").setMessage("您确定要退出游戏么?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						exitGame();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
					default:
						break;
					}
				}

			};
		}
	}
	
	static Handler mHandler = null;

	static public void sendMsg(int state) {
		if (mHandler == null)
			return;

		Message msg = new Message();
		msg.what = state;
		mHandler.sendMessage(msg);
	}
}
