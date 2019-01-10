package tw.chiae.inlive.presentation.ui.room.pubmsg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.websocket.LightHeartMsg;
import tw.chiae.inlive.data.bean.websocket.RoomPublicMsg;
import tw.chiae.inlive.data.bean.websocket.SendGiftMsg;
import tw.chiae.inlive.data.bean.websocket.SystemMsg;
import tw.chiae.inlive.data.bean.websocket.SystemWelcome;
import tw.chiae.inlive.data.bean.websocket.UserPublicMsg;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerHolder;

/**
 * @author Muyangmin
 * @since 1.0.0
 * Recycle的viewholder
 */
public class PublicChatHolder_bak extends SimpleRecyclerHolder<RoomPublicMsg>  implements PublicChatHolderCallback {

    private final ImageView ivLevel;
    private final Context mHolderContext;
    private final TextView tvPublicName;
    private final ImageView ivPublicLevel;
    private final TextView tvPublicContent;
    //private final View vSpace2;
    private final ImageView iv_idICon;

    private LinearLayout llmainLayout;
    //private final View vSpace;
    private TextView tvContent,tvMsgMode,tvMsgName;


    private MsgItem item;
    private HashMap<String, Object> mData;

    public MsgItem getItem() {
        return item;
    }

    public void setItem(MsgItem item) {
        this.item = item;
    }

    public PublicChatHolder_bak(View itemView, Context context) {
        super(itemView);
        this.mHolderContext = context.getApplicationContext();
        iv_idICon = (ImageView)itemView.findViewById(R.id.id_icon);
        llmainLayout = (LinearLayout) itemView.findViewById(R.id.item_public_chat_main_layout);
        tvPublicName = (TextView) itemView.findViewById(R.id.tv_pub_msg);
        ivPublicLevel = (ImageView) itemView.findViewById(R.id.iv_pub_msg);
        tvContent = (TextView) itemView.findViewById(R.id.item_public_chat_tv);
        tvMsgMode = (TextView) itemView.findViewById(R.id.item_public_chat_flag_tv);
        ivLevel = (ImageView)itemView.findViewById(R.id.item_public_chat_level_iv);
        tvPublicContent = (TextView)itemView.findViewById(R.id.tv_pub_content);
/*
        vSpace = itemView.findViewById(R.id.view);
        vSpace2 = itemView.findViewById(R.id.view2);
*/


    }

    @Override
    public void displayData(final RoomPublicMsg data) {

        //final RelativeLayout.LayoutParams vParams = (RelativeLayout.LayoutParams) vSpace.getLayoutParams();
        if (data instanceof UserPublicMsg) {
            iv_idICon.setVisibility(View.VISIBLE);
            tvPublicContent.setVisibility(View.VISIBLE);
            llmainLayout.setVisibility(View.VISIBLE);
            //vSpace2.setVisibility(View.VISIBLE);

            tvMsgMode.setVisibility(View.GONE);
            ivLevel.setVisibility(View.GONE);
            //vSpace.setVisibility(View.GONE);
            tvContent.setVisibility(View.GONE);
            //vParams.addRule(RelativeLayout.BELOW, R.id.item_public_chat_main_layout);
            //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvContent.getLayoutParams();
            //params.addRule(RelativeLayout.RIGHT_OF, R.id.item_public_chat_main_layout);

            //tvContent.setLayoutParams(params);
            //buildUserChatSequence((UserPublicMsg) data);
        } else if (data instanceof LightHeartMsg ||data instanceof SendGiftMsg ||data instanceof SystemWelcome) {
            iv_idICon.setVisibility(View.VISIBLE);
            tvMsgMode.setVisibility(View.VISIBLE);
            ivLevel.setVisibility(View.VISIBLE);
            //vSpace.setVisibility(View.VISIBLE);
            tvContent.setVisibility(View.VISIBLE);
            //vSpace2.setVisibility(View.GONE);
            tvPublicContent.setVisibility(View.GONE);
            llmainLayout.setVisibility(View.GONE);
        } else if (data instanceof SystemMsg) {
            iv_idICon.setVisibility(View.VISIBLE);
            //vParams.addRule(RelativeLayout.BELOW, R.id.item_public_chat_tv);
            tvContent.setVisibility(View.VISIBLE);
            tvMsgMode.setVisibility(View.GONE);
            ivLevel.setVisibility(View.GONE);
            tvPublicContent.setVisibility(View.GONE);
            llmainLayout.setVisibility(View.GONE);
            //vSpace2.setVisibility(View.GONE);
            //vSpace.setVisibility(View.VISIBLE);
        }

        item = new MsgItem(this,mHolderContext,data);
    }

    private void cancelItem(){
        item.removeItem();
    }
/*
    private CharSequence buildUserNameIcon(int resId, UserPublicMsg data) {
        Drawable drawable;
        MsgUtils utils = MsgUtils.getInstance();
        int width = mContext.getResources().getDimensionPixelSize(R.dimen.user_level_width);
        int height = mContext.getResources().getDimensionPixelSize(R.dimen.user_level_height);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);
        if (bitmap == null) {
            return null;
        }
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        drawable = new BitmapDrawable(mContext.getResources(),scaledBitmap);
        String parseStr = "[@Level]";
        ImageSpan span = new ImageSpan(mContext,scaledBitmap,ImageSpan.ALIGN_BASELINE);
        CharSequence CName = getUserChatName(data);
        SpannableStringBuilder ssb = new SpannableStringBuilder(parseStr+" "+CName);
        ssb.setSpan(span, 0, parseStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return ssb;
    }
*/

    private CharSequence getUserChatName(UserPublicMsg msg) {
        MsgUtils utils = MsgUtils.getInstance();
        return TextUtils.concat(
                utils.buildUserName(msg.getFromClientName()));
    }

    private CharSequence buildUserChatSequence(UserPublicMsg msg) {
        MsgUtils utils = MsgUtils.getInstance();
        return TextUtils.concat(
                utils.buildPublicMsgContent(msg.getContent()));
    }

    //    系统警告信息显示
    private CharSequence buildSysChatSequence(SystemMsg msg) {
        MsgUtils utils = MsgUtils.getInstance();
//        CharSequence level = utils.buildLevel(msg.getLevel());
//        //if level is not legal
//        if (level == null) {
//            level = "";
//        }
//      这里可能会用到
        return TextUtils.concat(
                utils.buildPublicSysMsgContent(msg.getContent()));
    }

    //    系统欢迎信息显示
    private CharSequence buildWelcomeChatSequence(SystemWelcome msg) {
        MsgUtils utils = MsgUtils.getInstance();
        CharSequence level = utils.buildLevel(msg.getLevelid());
        //if level is not legal
        return TextUtils.concat(
                utils.buildPublicSysMsgName(msg.getClient_name()),
                " ",
                utils.buildPublicSysMsgWelcome(tvContent.getContext().getString(R.string.room_live_msg_myroom))
                //utils.buildPublicSysMsgContent(tvContent.getContext().getString(R.string.room_live_msg_myroom))
        );
    }


    private CharSequence buildLightHeartSequence(LightHeartMsg msg) {
        MsgUtils utils = MsgUtils.getInstance();
        return TextUtils.concat(
                utils.buildUserName(msg.getFromClientName()),
                utils.buildPublicSysMsgContent(tvContent.getContext().getString(R.string.room_live_msg_mylight))
        );
    }

    private CharSequence buildGiftSequence(SendGiftMsg msg) {
        MsgUtils utils = MsgUtils.getInstance();
        CharSequence level = utils.buildLevel(msg.getLevel());
        //if level is not legal
        if (level == null) {
            level = "";
        }
        return TextUtils.concat(
                utils.buildUserName(msg.getFromUserName()),
                utils.buildPublicMsgContent(tvContent.getContext().getString(R.string.room_live_msg_sendone)),
                msg.getGiftName()
        );
    }


    @SuppressLint("NewApi")
    @Override
    public void onCompleteData(RoomPublicMsg data, final HashMap<String, Object> msgData) {
        //Log.i("RayTest"," ----------------------------------");
        this.mData = msgData;
        //final RelativeLayout.LayoutParams vParams = (RelativeLayout.LayoutParams) vSpace.getLayoutParams();
        if (data instanceof UserPublicMsg) {

            // Log.i("RayTest","[UserPublicMsg]"+msgData.get(MsgItem.MsgContent)+" "+msgData.get(MsgItem.MsgType));

            //llmainLayout.addView((View) msgData.get(MsgItem.MsgPublicView),0);
            tvPublicName.setText(msgData.get(MsgItem.MsgName)+"");
            ivPublicLevel.setImageDrawable((Drawable) msgData.get(MsgItem.MsgLevel));
            //tvContent.setText((CharSequence) msgData.get(MsgItem.MsgContent));
            tvPublicContent.setText((CharSequence) msgData.get(MsgItem.MsgContent));
            //tvContent.setLayoutParams(params);
            Drawable drawable = (Drawable) msgData.get(MsgItem.MsgApproeid);
            if(drawable!=null){
                iv_idICon.setVisibility(View.VISIBLE);
                iv_idICon.setImageDrawable((Drawable) msgData.get(MsgItem.MsgApproeid));
            }else{
                iv_idICon.setVisibility(View.GONE);
            }
        } else if (data instanceof LightHeartMsg ||data instanceof SendGiftMsg ||data instanceof SystemWelcome) {
            // Log.i("RayTest","[Default]"+msgData.get(MsgItem.MsgContent)+" "+msgData.get(MsgItem.MsgType));
            Log.i("RayTest","[Default]"+msgData.get(MsgItem.MsgContent)+" "+msgData.get(MsgItem.MsgType));
            tvMsgMode.setText((String) msgData.get(MsgItem.MsgType));
            ivLevel.setImageDrawable((Drawable) msgData.get(MsgItem.MsgLevel));
            tvContent.setText((CharSequence) msgData.get(MsgItem.MsgContent));
            Drawable drawable = (Drawable) msgData.get(MsgItem.MsgApproeid);
            if(drawable!=null){
                iv_idICon.setVisibility(View.VISIBLE);
                iv_idICon.setImageDrawable((Drawable) msgData.get(MsgItem.MsgApproeid));
            }else{
                iv_idICon.setVisibility(View.GONE);
            }

        } else if (data instanceof SystemMsg) {
            CharSequence sContent =(CharSequence) msgData.get(MsgItem.MsgContent);
            if(sContent.toString().contains("將主播加入了最愛") ||sContent.toString().contains("将主播加入了最爱")|| sContent.toString().contains("已被管理員禁言") ){
                tvMsgMode.setText((String) msgData.get(MsgItem.MsgType));
                tvMsgMode.setVisibility(View.VISIBLE);
                iv_idICon.setVisibility(View.GONE);
            }
            if(tvMsgMode.getVisibility()==View.VISIBLE)
                iv_idICon.setVisibility(View.GONE);
            else
                iv_idICon.setVisibility(View.GONE);
            tvContent.setText(sContent);
        }

        // Log.i("RayTest"," ----------------------------------");
    }

    public void onCancel (){
        Log.i("RayTest","onCancel");
        mData.clear();
        iv_idICon.setImageDrawable(null);
    }


}
