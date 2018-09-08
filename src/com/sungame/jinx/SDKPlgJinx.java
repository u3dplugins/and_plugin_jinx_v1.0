package com.sungame.jinx;

import org.json.JSONObject;

import com.sdkplugin.bridge.U3DBridge;
import com.sdkplugin.extend.PluginBasic;
import com.sdkplugin.tools.Tools;

import cn.mangofun.xsdk.framework.Constants;
import cn.mangofun.xsdk.framework.ErrorCode;
import cn.mangofun.xsdk.framework.XSDKCallback;
import cn.mangofun.xsdk.framework.data.GameData;
import cn.mangofun.xsdk.openapi.XSDK;

public class SDKPlgJinx extends PluginBasic implements XSDKCallback.Callback {

	public SDKPlgJinx() {
		super();
		this.logLevel = LEV_LOG_NORMAL;
		this.logHead = "jinx";
	}

	static final String CMD_Login = "xskd_login";
	static final String CMD_Logout = "xskd_logout";
	static final String CMD_Exit = "xskd_exit";
	static final String CMD_HasForum = "xskd_hasForum";
	static final String CMD_OpenForum = "xskd_openForum";
	static final String CMD_HasService = "xskd_hasService";
	static final String CMD_OpenService = "xskd_openService";
	static final String CMD_HasCenter = "xskd_hasCenter";
	static final String CMD_OpenCenter = "xskd_openCenter";
	static final String CMD_Pay = "xskd_pay";

	// 数据
	static final String CMD_ZInitRole = "xskd_initRole";
	static final String CMD_ZCreateRole = "xskd_createRole";
	static final String CMD_ZEntryRole = "xskd_entryRole";
	static final String CMD_ZUpRole = "xskd_upRole";
	static final String CMD_ZGetUser = "xskd_getUser";

	private GameData gameData = new GameData();
	private String _strFmtResult = "funcName=[%s],data=[%s]";

	int _reInitCount = 6;
	int _reLoginCount = 5;
	
	String uid = "",uname = "";
	String rid = "", rname = "", rlev = "1";
	String serverId = "16888888";
	String serverName = "芒果互娱-jinx国内服务";

	boolean _isInited = false;
	boolean _isLogined = false;
	JSONObject objJsonGoods = null; // 商品计费列表

	@Override
	public void result(String funcName, String data) {
		try {
			logInfo(String.format(_strFmtResult, funcName, data));
			
			// 失败的时候都打印data
			JSONObject obj = new JSONObject(data);
			int code = obj.getInt(Constants.KEY_CODE);
			String result = "";
			if(obj.has(Constants.KEY_RESULT))
				result = obj.getString(Constants.KEY_RESULT);

			boolean isSuccess = code == ErrorCode.SUCCESS;
			if (Constants.FUNC_INIT.equalsIgnoreCase(funcName)) {
				if (isSuccess) {
					_isInited = true;
				} else {
					if (_reInitCount > 0) {
						_reInitCount--;
						doInitSDK();
					} else {
						doExit();
					}
				}
			} else if (Constants.FUNC_LOGIN.equalsIgnoreCase(funcName)) {
				if (isSuccess) {
					JSONObject jsonObj = new JSONObject(result);
					// String sdkTicket = jsonObj.getString("sdkTicket");
					JSONObject userInfo = jsonObj.getJSONObject("userInfo");
					uid = userInfo.getString("userId");
					if(userInfo.has("login_sdk_name"))
						uname = userInfo.getString("login_sdk_name");
					if(uname == null || "".equals(uname) || "null".equalsIgnoreCase(uname)) {
						uname = "";
					}

					_InitGameData();
					_reLoginCount = 5;
					_isLogined = true;
					
					_UserInfo(CMD_Login);
				} else {
					if (_reLoginCount > 0) {
						_reLoginCount--;
						_login(false);
					} else {
						Tools.msg2U3D(CODE_FAILS, "登录失败", CMD_Login, obj);
					}
				}
			} else if (Constants.FUNC_LOGOUT.equalsIgnoreCase(funcName)) {
				Tools.msg2U3D(isSuccess ? CODE_SUCCESS : CODE_FAILS, isSuccess ? "注销成功" : "注销失败", CMD_Logout, mapData);
				if (isSuccess) {
					_isLogined = false;
					gameData.setServerId("");
					gameData.setServerName("");
					gameData.setRoleId("");
					gameData.setRoleName("");
					gameData.setRoleLevel("");
					gameData.setData("roleCreateTime", "");
					if (Constants.LOGOUT_WITH_NOT_OPEN_LOGIN.equalsIgnoreCase(result)) {
						// 注销之后不会自动打开登录页面,游戏想要做全部平台注销后都自动打开调用登录，可以在这里面调用
						_login(true);
					} else if (Constants.LOGOUT_WITH_OPEN_LOGIN.equalsIgnoreCase(result)) {
						// 注销之后会自动打开登录页面
						_reLoginCount = 5;
					}
				}
			} else if (Constants.FUNC_GET_ORDER_ID.equalsIgnoreCase(funcName)) {
				Tools.msg2U3D(CODE_WAIT, "订单号请求成功", CMD_Pay, obj);
			} else if (Constants.FUNC_PAY.equalsIgnoreCase(funcName)) {
				Tools.msg2U3D(isSuccess ? CODE_SUCCESS : CODE_FAILS, isSuccess ? "充值成功" : "支付失败", CMD_Pay, obj);
			} else if (Constants.FUNC_EXIT.equalsIgnoreCase(funcName)) {
				if (isSuccess) {
					boolean imm = Constants.HAS_EXIT_PAGE_AND_PRESS_EXIT.equalsIgnoreCase(result);
					callExitGame(imm);
				} else {
					// "退出失败"
					Tools.msg2U3D(CODE_FAILS, "退出失败", CMD_Exit, obj);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void handlerMsg(final String cmd, JSONObject data) throws Exception {
		String _val1 = "", _val2 = "", _val3 = "", _val4 = "", _val5 = "";
		long _valL1 = 0;
		switch (cmd) {
		case CMD_Exit:
			doExit();
			break;
		case CMD_HasCenter:
			mapData.put("isHas", _hasPlatformCenter());
			Tools.msg2U3D(CODE_SUCCESS, "", cmd, mapData);
			break;
		case CMD_HasForum:
			mapData.put("isHas", _hasForum());
			Tools.msg2U3D(CODE_SUCCESS, "", cmd, mapData);
			break;
		case CMD_HasService:
			mapData.put("isHas", _hasService());
			Tools.msg2U3D(CODE_SUCCESS, "", cmd, mapData);
			break;
		case CMD_Login:
			_login(true);
			break;
		case CMD_Logout:
			_logout();
			break;
		case CMD_OpenCenter:
			_openPlatformCenter();
			break;
		case CMD_OpenForum:
			_openForum();
			break;
		case CMD_OpenService:
			_openService();
			break;
		case CMD_Pay:
			_val1 = data.getString("amount");
			_val2 = data.getString("productId");
			_val3  = data.getString("productName");
			_val4 = data.getString("productDesc");
			_val5 = data.getString("customData");
			_pay(_val1, _val2, _val3, _val4, _val5);
			break;
		case CMD_ZInitRole:
			_val1 = data.getString("rlev");
			_val2  = data.getString("rid");
			_val3 = data.getString("rname");
			if (data.has("serverId")) {
				_val4 = data.getString("serverId");
			}
			if (data.has("serverName")) {
				_val5 = data.getString("serverName");
			}
			_initRole(_val1, _val2, _val3, _val4, _val5);
			break;
		case CMD_ZCreateRole:
			_valL1 = data.getLong("sec");
			_createRole(_valL1);
			break;
		case CMD_ZEntryRole:
			_valL1 = data.getLong("sec");
			_entryRole(_valL1);
			break;
		case CMD_ZUpRole:
			_val1 = data.getString("rlev");
			_valL1 = data.getLong("sec");
			_upRole(_val1,_valL1);
			break;
		case CMD_ZGetUser:
			_UserInfo(null);
			break;
		default:
			super.handlerMsg(cmd, data);
			break;
		}
	}

	void _InitGameData() {
		// 奇虎360渠道要求参数
		gameData.setData("professionid", "1"); // 必传，职业ID，必须为数字，如果有则必传，如果没有，请说明原因
		gameData.setData("profession", "武士"); // 必传，职业名称，如果有则必传，如果没有，请说明原因
		gameData.setData("gender", "女"); // 必传，性别，只能传"男"、"女"，如果有则必传，如果没有，请说明原因
		gameData.setData("power", "132323000"); // 必传，战力数值，必须为数字，如果有则必传，如果没有，请说明原因
		gameData.setData("balance", "0"); // 必传，帐号余额，必须为数字，如果有则必传，如果没有，请说明原因
		gameData.setData("partyid", "120"); // 必传，所属帮派帮派ID，必须为数字，如果有则必传，如果没有，请说明原因
		gameData.setData("partyname", "一笑倾城"); // 必传，所属帮派名称，如果有则必传，如果没有，请说明原因
		gameData.setData("partyroleid", "1"); // 必传，帮派称号ID，必须为数字，帮主/会长必传1，其他可自定义，如果有则必传，如果没有，请说明原因
		gameData.setData("partyrolename", "帮主");// 必传，帮派称号名称，如果有则必传，如果没有，请说明原因
		gameData.setData("friendlist", "[{\"roleid\":0,\"intimacy\":\"0\",\"nexusid\":\"600\",\"nexusname\":\"情侣\"}]");
	}
	
	void _InitGoods() {
		if(objJsonGoods != null)
			return;
		try {
			String strJson = Tools.getTextInAssets(getCurContext(),"goods.json");
			if(!"".equals(strJson)) {
				objJsonGoods = new JSONObject(strJson);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void _initRole(String rlev, String rid, String rname, String serverId, String serverName) {
		if (serverId != null && !"".equalsIgnoreCase(serverId))
			this.serverId = serverId;
		if (serverName != null && !"".equalsIgnoreCase(serverName))
			this.serverName = serverName;

		if (rlev != null && !"".equalsIgnoreCase(rlev))
			this.rlev = rlev;

		if (rid != null && !"".equalsIgnoreCase(rid))
			this.rid = rid;

		if (rname != null && !"".equalsIgnoreCase(rname))
			this.rname = rname;

		gameData.setServerId(this.serverId);
		gameData.setServerName(this.serverName);
		gameData.setRoleId(this.rid);
		gameData.setRoleName(this.rname);
		gameData.setRoleLevel(this.rlev);
	}

	void _initRole(String relv) {
		_initRole(relv, null, null, null, null);
	}
	
	void _createRole(long sec) {
		if (!_isLogined)
			return;
		gameData.setData("roleCreateTime", String.valueOf(sec));
		XSDK.getInstance().onCreatRole(gameData);
	}

	void _entryRole(long sec) {
		if (!_isLogined)
			return;
		gameData.setData("roleCreateTime", String.valueOf(sec));
		XSDK.getInstance().onEnterGame(gameData);
	}

	void _upRole(String rlev,long sec) {
		if (!_isLogined)
			return;
		_initRole(rlev);
		gameData.setData("roleCreateTime", String.valueOf(sec));
		XSDK.getInstance().onLevelUp(gameData);
	}

	public void doInitSDK() {
		XSDK.getInstance().init(getCurActivity(), SDKPlgJinx.getInstance());
	}

	public void doExit() {
		XSDK.getInstance().exit(gameData);
	}

	public void callExitGame(boolean imm) {
		if (imm) {
			Tools.msg2U3D(CODE_SUCCESS, "成功退出", CMD_Exit,mapData);
			XSDK.getInstance().onExitGame(gameData);
			MainActivity.sendMsg(0);
		} else {
			MainActivity.sendMsg(1);
		}
	}

	void _login(boolean isReLogin) {
		if(!_isInited) {
			Tools.msg2U3D(CODE_FAILS, "登录失败:尚未初始化!",CMD_Login, new JSONObject());
			return;
		}
		if (isReLogin) {
			_reLoginCount = 5;
		}
		_isLogined = false;
		XSDK.getInstance().login();
	}

	void _logout() {
		if(!_isLogined)
			return;
		XSDK.getInstance().logout(gameData);
	}

	// 论坛
	boolean _hasForum() {
		return XSDK.getInstance().hasForum();
	}

	void _openForum() {
		if (_hasForum()) {
			XSDK.getInstance().openForum(gameData);
		}
	}

	// 客服
	boolean _hasService() {
		return XSDK.getInstance().hasCustomerService();
	}

	void _openService() {
		if (_hasService()) {
			XSDK.getInstance().enterCustomerService(gameData);
		}
	}

	// 用户中心
	boolean _hasPlatformCenter() {
		return XSDK.getInstance().hasPlatformCenter();
	}

	void _openPlatformCenter() {
		if (_hasPlatformCenter()) {
			XSDK.getInstance().enterPlatformCenter(gameData);
		}
	}

	void _pay(String strRMB, String productId, String productName, String productDesc,String customData) {
		if (!_isLogined) {
			Tools.msg2U3D(CODE_FAILS, "支付失败:尚未登录!", CMD_Pay, new JSONObject());
			return;
		}
		int amount = 0;
		try {
			amount = Integer.parseInt(strRMB);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (amount <= 0) {
			Tools.msg2U3D(CODE_FAILS, "支付失败:金额为负数了!", CMD_Pay, new JSONObject());
			return;
		}
		
		XSDK.getInstance().pay(amount, productId, productName, productDesc, "10", "钻石", customData, gameData);
	}
	
	void _UserInfo(String cmd) throws Exception {
		JSONObject data = new JSONObject();
		data.put("isLogined",_isLogined);
		data.put("user_id", uid);
		data.put("user_name", uname);
		data.put("hasForum", _hasForum());
		data.put("hasCenter", _hasPlatformCenter());
		data.put("hasService", _hasService());

		if (cmd != null && !"".equals(cmd)) {
			data.put("cmd", cmd);
			Tools.msg2U3D(CODE_SUCCESS, "", data);
			data.remove("cmd");
		}
		
		data.put("cmd", CMD_ZGetUser);
		Tools.msg2U3D(CODE_SUCCESS, "", data);
	}
	static private SDKPlgJinx _instance;

	static public SDKPlgJinx getInstance() {
		if (_instance == null) {
			_instance = new SDKPlgJinx();

			U3DBridge.setListener(_instance);
		}
		return _instance;
	}

}
