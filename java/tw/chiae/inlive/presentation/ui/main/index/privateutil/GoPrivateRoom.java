package tw.chiae.inlive.presentation.ui.main.index.privateutil;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.main.me.transaction.RechargeActivity;
import tw.chiae.inlive.presentation.ui.room.create.PrivateSetStringDialog;
import tw.chiae.inlive.presentation.ui.widget.CustomToast;

/**
 * Created by Administrator on 2016/12/14 0014.
 */

public class GoPrivateRoom extends DialogFragment implements View.OnClickListener {
    //退出
    private ImageView pClose;
    //私密类型
    private TextView pType, pName;
    //头像
    private SimpleDraweeView pPhoto,mLayoutBg;
    //密码输入框
    private EditText pPwd;
    //content
    private RelativeLayout pTicket;
    //content titile
    private TextView pTitle;
    //content price
    private TextView pPrice;
    //content balance
    private TextView pBalance;
    //content reagince
    private TextView pRecharge;
    //content commit
    private TextView pCommit;
    //提交信息给fragment
    private GoPrivateRoomInterface goPrivateRoomInterface;

    public static String GO_PLID = "plid";
    public static String GO_PRIVATE_TYPE = "privateType";
    public static String GO_PRIVATE_CONTE = "privatecont";
    public static String GO_NAME = "username";
    public static String GO_PHOTO = "avatar";
    public static String GO_USER_ID="userid";
    public static String GO_LAYOU_BG="snap";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Window window = getDialog().getWindow();
        View view = inflater.inflate(R.layout.goprivate_room_dialog, ((ViewGroup) window.findViewById(android.R.id.content)), false);//需要用android.R.id.content这个view
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
        window.setLayout(-1, -1);//这2行,和上面的一样,注意顺序就行;
        initView(view);
        setViewText();
        initEvent();
        return view;
    }


    private void initView(View view) {
        pClose = (ImageView) view.findViewById(R.id.goprivate_room_close);
        pType = (TextView) view.findViewById(R.id.goprivate_room_dialog_type);
        pPhoto = (SimpleDraweeView) view.findViewById(R.id.goprivate_room_dialog_photo);
        pName = (TextView) view.findViewById(R.id.goprivate_room_dialog_name);
        pPwd = (EditText) view.findViewById(R.id.goprivate_room_dialog_pwdedit);
        pTicket = (RelativeLayout) view.findViewById(R.id.goprivate_room_dialog_ticket);
        pTitle = (TextView) view.findViewById(R.id.goprivate_room_ticket_title);
        pPrice = (TextView) view.findViewById(R.id.goprivate_room_ticket_price);
        pBalance = (TextView) view.findViewById(R.id.goprivate_room_ticket_balance);
        pRecharge = (TextView) view.findViewById(R.id.goprivate_room_ticket_rechargebalance);
        pCommit = (TextView) view.findViewById(R.id.goprivate_room_dialog_commit);
        mLayoutBg= (SimpleDraweeView) view.findViewById(R.id.goprivate_room_dialog_bg);
    }

    private void setViewText() {
        pName.setText(getArguments().getString(GO_NAME));
        pPhoto.setImageURI(SourceFactory.wrapPathToUri(getArguments().getString(GO_PHOTO)));
        mLayoutBg.setImageURI(SourceFactory.wrapPathToUri(getArguments().getString(GO_LAYOU_BG)));
        pPrice.setText(getArguments().getString(GO_PRIVATE_CONTE));
        if (getArguments().getString(GO_PRIVATE_TYPE).equals(PrivateSetStringDialog.PRIVTE_ROOM_PWD)) {
            pTitle.setText(getString(R.string.goprivate_room_dialog_pwd));
            pTicket.setVisibility(View.GONE);
            pType.setText(getString(R.string.goprivate_room_dialog_pwd));
        } else if (getArguments().getString(GO_PRIVATE_TYPE).equals(PrivateSetStringDialog.PRIVTE_ROOM_TICKET)) {
            pPwd.setVisibility(View.GONE);
            pTitle.setText(getString(R.string.goprivate_room_ticket_money));
            pType.setText(getString(R.string.goprivate_room_dialog_ticket));
            pBalance.setText(getString(R.string.goprivate_room_ticket_balance,String.valueOf(LocalDataManager.getInstance().getLoginInfo().getTotalBalance())));
            pRecharge.setText(getString(R.string.goprivate_room_ticket_recharge));
            pCommit.setText(getString(R.string.goprivate_room_ticket_commit));
        } else {
            pPwd.setVisibility(View.GONE);
            pTitle.setText(getString(R.string.goprivate_room_level_min));
            pType.setText(getString(R.string.goprivate_room_dialog_level));
            pCommit.setText(getString(R.string.goprivate_room_level_commit));
            pRecharge.setText(getString(R.string.goprivate_room_level_fastup));
            pBalance.setText(getString(R.string.goprivate_room_my_level,LocalDataManager.getInstance().getLoginInfo().getLevel()));
        }
    }

    private void initEvent() {
        pClose.setOnClickListener(this);
        pRecharge.setOnClickListener(this);
        pCommit.setOnClickListener(this);
        pPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_DONE ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)) {
                    commitPrivatePwd();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goprivate_room_close:
                dismiss();
                break;
            case R.id.goprivate_room_ticket_rechargebalance:
                if (getArguments().getString(GO_PRIVATE_TYPE).equals(PrivateSetStringDialog.PRIVTE_ROOM_TICKET)) {
                    getActivity().startActivity(RechargeActivity.createIntent(getActivity()));
                    dismiss();
                }
                break;
            case R.id.goprivate_room_dialog_commit:
                if (getArguments().getString(GO_PRIVATE_TYPE).equals(PrivateSetStringDialog.PRIVTE_ROOM_LEVEL))
                    getActivity().startActivity(RechargeActivity.createIntent(getActivity()));
                else if (getArguments().getString(GO_PRIVATE_TYPE).equals(PrivateSetStringDialog.PRIVTE_ROOM_TICKET)){
                    //余额不足
                    if (LocalDataManager.getInstance().getLoginInfo().getTotalBalance()<Long.valueOf(getArguments().getString(GO_PRIVATE_CONTE))){
                        CustomToast.makeCustomText(getActivity(), getString(R.string.goprivate_room_ticket_nomoney), Toast.LENGTH_SHORT).show();
                        break;
                    }else {
                        //有钱请求api
                        if (goPrivateRoomInterface!=null)
                            goPrivateRoomInterface.questGoPrivateRoom(getArguments().getString(GO_PRIVATE_TYPE),
                                    getArguments().getInt(GO_PLID),
                                    getArguments().getString(GO_USER_ID),
                                    pPwd.getText().toString());
                    }
                }else {
                    commitPrivatePwd();
                }
                dismiss();
                break;
        }
    }

    public void setGoPrivateRoomInterface(GoPrivateRoomInterface goPrivateRoomInterface) {
        this.goPrivateRoomInterface = goPrivateRoomInterface;
    }

    public void commitPrivatePwd(){
        //提交密码过去
        if (TextUtils.isEmpty(pPwd.getText().toString().trim())){
            CustomToast.makeCustomText(getActivity(), getString(R.string.goprivate_room_pwd_editerror), Toast.LENGTH_SHORT).show();
            return;
        }
        if (goPrivateRoomInterface!=null)
            goPrivateRoomInterface.questGoPrivateRoom(getArguments().getString(GO_PRIVATE_TYPE),
                    getArguments().getInt(GO_PLID),
                    getArguments().getString(GO_USER_ID),
                    pPwd.getText().toString());
        dismiss();
    }
}
