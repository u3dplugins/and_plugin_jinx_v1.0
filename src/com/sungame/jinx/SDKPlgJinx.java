package com.sungame.jinx;

import org.json.JSONObject;

import com.sdkplugin.bridge.U3DBridge;
import com.sdkplugin.extend.PluginBasic;
import com.sdkplugin.tools.Tools;

public class SDKPlgJinx  extends PluginBasic{
	@Override
	protected void handlerMsg(String cmd, JSONObject data) throws Exception {
		switch (cmd) {
		case "mapInfo":
			Tools.msg2U3D(CODE_SUCCESS, "", Tools.ToData(cmd, mapData));
			break;
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
