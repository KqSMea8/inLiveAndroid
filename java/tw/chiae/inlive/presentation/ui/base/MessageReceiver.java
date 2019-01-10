package tw.chiae.inlive.presentation.ui.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;

/**
 * Created by rayyeh on 2017/9/22.
 */

public class MessageReceiver extends BroadcastReceiver {
    private SMSListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("RayTest","=====MessageReceiver=====");
        Bundle bundle = intent.getExtras();

        Object[] myObj = (Object[]) bundle.get("pdus");

        SmsMessage smsMsg[] = new SmsMessage[myObj.length];

        System.out.println(myObj.length);
        String msg = "";

        for (int i = 0; i < myObj.length; i++){
            SmsMessage message = SmsMessage.createFromPdu((byte[]) myObj[i]);
            String fromAddress = message.getOriginatingAddress();
            String msgBody = message.getMessageBody();
            Log.i("RayTest","msgBody: "+ msgBody);
                smsMsg[i] = SmsMessage.createFromPdu((byte[]) myObj[i]);
            msg += "發文者:" + smsMsg[i].getOriginatingAddress() + "\n";
            msg += "簡訊內容:" + smsMsg[i].getDisplayMessageBody() + "\n";
            if(msgBody.contains("【inLive硬直播】")){
                int startIndex = 5;
                String newMsg = msgBody.substring(startIndex,startIndex+6);
                Log.i("RayTest",startIndex+" newMsg: "+ newMsg);
                if(mListener!=null){
                    mListener.sendMsg(newMsg);
                }
            }
        }
       /* if(msg.contains("【inLive硬直播】")){
            int startIndex = msg.indexOf("驗證碼為：");

            String newMsg = msg.substring(startIndex,startIndex+6);
            Log.i("RayTest",startIndex+" newMsg: "+ newMsg);
        }*/
        Log.i("RayTest","SMS: "+ msg);


    }

    public void setSMSListener(SMSListener listener){
        this.mListener = listener;
    }

    public void removeListener(SMSListener smsListener) {
        smsListener = null;
        this.mListener = null;
    }

    public interface SMSListener {

        void sendMsg(String newMsg);
    }
}
