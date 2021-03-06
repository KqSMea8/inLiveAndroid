package tw.chiae.inlive.wxapi;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.widget.CustomToast;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.Event.EventPayment;

import de.greenrobot.event.EventBus;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "WXPayEntryActivity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, Const.WX_APPID);
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
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

	/*	if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string. );
			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
			builder.show();
		}*/
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			if (resp.errCode == BaseResp.ErrCode.ERR_OK){
				EventBus.getDefault().post(new EventPayment(Const.PAY_TYPE_WEIXIN, Const.PAY_RESULT_STATUS_SUCCESS));
			}else {
				if (resp.errCode == BaseResp.ErrCode.ERR_COMM){
					CustomToast.makeCustomText(this,  "支付失败"+resp.toString(), Toast.LENGTH_SHORT).show();
				}else if (resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL){
					CustomToast.makeCustomText(this,  "取消支付", Toast.LENGTH_SHORT).show();
				}else if (resp.errCode == BaseResp.ErrCode.ERR_SENT_FAILED){
					CustomToast.makeCustomText(this,  "发送失败", Toast.LENGTH_SHORT).show();
				}
				EventBus.getDefault().post(new EventPayment(Const.PAY_TYPE_WEIXIN, Const.PAY_RESULT_STATUS_FAIL));
			}
		}
		this.finish();
	}
}