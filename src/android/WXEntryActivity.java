package __PACKAGE_NAME__;

import com.comall.cordova.wechat.WechatPlugin;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, WechatPlugin.appId);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onResp(BaseResp resp) {
        Log.i("onResp","1111122222333333");
    	switch(resp.getType()) {
    		case ConstantsAPI.COMMAND_PAY_BY_WX:
                Log.i("onResp",BaseResp.ErrCode.ERR_OK+"");
    			if(resp.errCode == BaseResp.ErrCode.ERR_OK){
                        Log.i("onResp","OK");
    				WechatPlugin.getCallbackContext().success();
    			}
    			else {
                    Log.i("onResp","ERROR");
    				WechatPlugin.getCallbackContext().error(
    						resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL ?
    						"支付已取消" : resp.errStr);
    			}
    			break;
    		case ConstantsAPI.COMMAND_SENDAUTH:
    			SendAuth.Resp authResp = (SendAuth.Resp)resp;
    			if(resp.errCode == BaseResp.ErrCode.ERR_OK){
    				WechatPlugin.getCallbackContext().success(authResp.code);
    			}
    			else {
    				WechatPlugin.getCallbackContext().error(
    						resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL ?
    						"授权已取消" : resp.errStr);
    			}
    			break;
    		default:
                Log.i("onResp","DEFAULT");
    			if(resp.errCode == BaseResp.ErrCode.ERR_OK){
    				WechatPlugin.getCallbackContext().success();
    			}
    			else {
    				WechatPlugin.getCallbackContext().error(
    						resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL ?
    						"用户已取消" : resp.errStr);
    			}
    	}
        finish();
    }

}
