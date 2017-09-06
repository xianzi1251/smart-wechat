package com.comall.cordova.wechat;

import java.io.InputStream;
import java.net.URL;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WechatPlugin extends CordovaPlugin {
	public static final int THUMB_SIZE = 150;
	public static String appId;

	private static CallbackContext callbackContext;
	private Activity activity;

	private IWXAPI api;

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webview) {

		this.activity = cordova.getActivity();
		api = WXAPIFactory.createWXAPI(this.activity, null);

		// 添加微信平台
		WechatPlugin.appId = preferences.getString("WECHAT_APPKEY", "");
		api.registerApp(WechatPlugin.appId);
	}

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {

		WechatPlugin.callbackContext = callbackContext;

		if ("pay".equals(action)) {
			// 实例化一个新的微信支付请求
			JSONObject reqParams = new JSONObject(args.getString(0));
			PayReq req = new PayReq();
			req.appId = WechatPlugin.appId;
			req.partnerId = reqParams.getString("partnerId");
			req.packageValue = reqParams.getString("packageValue");
			req.prepayId = reqParams.getString("prepayId");
			req.nonceStr = reqParams.getString("nonceStr");
			req.timeStamp = reqParams.getString("timeStamp");
			req.sign = reqParams.getString("sign");

			api.sendReq(req);
		} if ("share".equals(action)) {
	        final int scene = args.getInt(0) == 0 ? SendMessageToWX.Req.WXSceneSession :
            	SendMessageToWX.Req.WXSceneTimeline;
            final String title = args.getString(1);
            final String content = args.getString(2);
            final String pic = args.getString(3);
	        final String url = args.getString(4);

			new AsyncTask<String, Void, Bitmap>() {

	            @Override
	            protected Bitmap doInBackground(String... params) {
	            	Bitmap bitmap = null;
	            	try {
	            		InputStream is = new URL(pic).openStream();
	            		bitmap = Bitmap.createScaledBitmap(
	            				BitmapFactory.decodeStream(is), THUMB_SIZE, THUMB_SIZE, true);
	            		is.close();
	            	} catch (Exception e) {
	                	Log.i("wechat.share", e.getMessage());
	                }
	                return bitmap;
	            }

	            @Override
	            protected void onPostExecute(Bitmap result) {
	                super.onPostExecute(result);
	                if (result != null) {
	                	WXWebpageObject webpage = new WXWebpageObject();
	    				webpage.webpageUrl = url;
	    				WXMediaMessage msg = new WXMediaMessage(webpage);
	    				msg.title = title;
	    				msg.description = content;
	    				msg.setThumbImage(result);
	    	            SendMessageToWX.Req req = new SendMessageToWX.Req();
	    	            req.transaction = String.valueOf(System.currentTimeMillis());
	    	            req.message = msg;
	    	            req.scene = scene;
	    	            api.sendReq(req);
	                } else {
	                	WechatPlugin.getCallbackContext().error("图片下载失败");
	                }
	            }

	        }.execute(new String[] {});
		} if ("login".equals(action)) {
			SendAuth.Req req = new SendAuth.Req();
			req.scope = "snsapi_userinfo";
			req.state = "wechat_login";
			api.sendReq(req);
		} else if ("checkAppInstalled".equals(action)) {
			boolean installed = api.isWXAppInstalled();
			callbackContext.success(installed + "");
		}

		return true;
	}

	static public CallbackContext getCallbackContext() {
		return WechatPlugin.callbackContext;
	}

}
