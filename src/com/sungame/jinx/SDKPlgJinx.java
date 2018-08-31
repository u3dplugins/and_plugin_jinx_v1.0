package com.sungame.jinx;

import java.io.File;

import org.json.JSONObject;

import com.sdkplugin.bridge.U3DBridge;
import com.sdkplugin.extend.PluginBasic;
import com.sdkplugin.tools.Tools;

import android.content.Intent;
import android.net.Uri;

public class SDKPlgJinx  extends PluginBasic{
	
	public SDKPlgJinx() {
		super();
		this.logLevel = LEV_LOG_NORMAL;
		this.logHead = "jinx";
	}
	
	@Override
	protected void handlerMsg(String cmd, JSONObject data) throws Exception {
		switch (cmd) {
		default:
			super.handlerMsg(cmd, data);
			break;
		}
	}
	

	static SDKPlgJinx _instance;

	static public SDKPlgJinx getInstance() {
		if (_instance == null) {
			_instance = new SDKPlgJinx();

			U3DBridge.setListener(_instance);
		}
		return _instance;
	}
}
