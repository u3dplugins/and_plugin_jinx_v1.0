package com.sungame.jinx;

import java.io.File;

import org.json.JSONObject;

import com.sdkplugin.bridge.U3DBridge;
import com.sdkplugin.extend.PluginBasic;
import com.sdkplugin.tools.Tools;

import android.content.Intent;
import android.net.Uri;

public class SDKPlgJinx  extends PluginBasic{
	@Override
	protected void handlerMsg(String cmd, JSONObject data) throws Exception {
		String strVal1 = "",strVal2 = "";
		boolean isVal1 = false;
		switch (cmd) {
		case "isInApk":
			// 判断是否安装了某个apk
			if (data.has("pkgName")) {
				strVal1 = data.getString("pkgName");
			}
			if(!"".equals(strVal1)) {
				isVal1 = isInstalledApk(strVal1); // "com.facebook.katana"
			}
			mapData.put("isInApk", isVal1);
			jsonData = Tools.ToData(cmd,mapData);
			Tools.msg2U3D(CODE_SUCCESS, "", jsonData);
			break;
		case "shareImg":
			// 图片分享
			if (data.has("filepath")) {
				strVal1 = data.getString("filepath");
			}
			if (data.has("nType")) {
				strVal2 = data.getString("nType");
			}
			if(!"".equals(strVal1) && !"".equals(strVal2)) {
				if("instagram".equalsIgnoreCase(strVal2)) {
					isVal1 = true;
					shareInstagramIntent("image/*", strVal1);
				}
			}
			mapData.put("isState", isVal1);
			jsonData = Tools.ToData(cmd,mapData);
			Tools.msg2U3D(CODE_SUCCESS, "", jsonData);
			break;
		default:
			super.handlerMsg(cmd, data);
			break;
		}
	}
	
	/** type[image/*,video/*] **/
	private void shareInstagramIntent(String type, String mediaPath){
	    // Create the new Intent using the 'Send' action.
	    Intent share = new Intent(Intent.ACTION_SEND);

	    // Set the MIME type
	    share.setType(type);

	    // Create the URI from the media
	    File media = new File(mediaPath);
	    Uri uri = Uri.fromFile(media);

	    // Add the URI to the Intent.
	    share.putExtra(Intent.EXTRA_STREAM, uri);

	    // Broadcast the Intent.
	    getCurActivity().startActivity(Intent.createChooser(share, "Share to"));
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
