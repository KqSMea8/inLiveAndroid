package tw.chiae.inlive.presentation.ui.room;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.room.RoomAdminInfo;
import tw.chiae.inlive.data.bean.websocket.WsRoomManageRequest;
import tw.chiae.inlive.data.websocket.WebSocketService;
import tw.chiae.inlive.data.websocket.WsObjectPool;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.widget.CustomToast;
import tw.chiae.inlive.presentation.ui.widget.IUserInfoDialog;
import tw.chiae.inlive.presentation.ui.widget.UserInfoDialogPresenter;
import tw.chiae.inlive.util.Const;

import java.util.List;

/**
 * Created by Administrator on 2016/7/8 0008.
 */
public class RoomManageDialog extends Dialog implements View.OnClickListener, IUserInfoDialog {
    private Context activity;
    //    weService
    private WebSocketService webService;

    //    传入显示的用户的info
    private UserInfo info;
    //管理    禁言    踢人         取消
    private Button btnManager, btnGag, btnKickPlayer, btnCancel, btnReport, btnShowManager,btnShowMember;

    private List<RoomAdminInfo> adminInfoList;

    //    当前用户token
    String token;
    //    房间主人的id
    String roomuid;
    //    展开的用户的id
    String adminuid;

    //    房间主人是否是当前登录的用户
    boolean isRoomuser = false;
    //     当前用户是否是管理员
    boolean isRoomAdmin = false;
    //    被查看的用户是否是管理员
    boolean showRoomAmin = false;

    //    当前用户是否是
    boolean isRoomuserid = false;
    private UserInfoDialogPresenter mPresenter;

    public RoomManageDialog(Context context, WebSocketService webService, UserInfo info, String token, String roomuid) {
        super(context, R.style.DialogStyle);
        this.activity = context;
        this.webService = webService;
        this.info = info;
        this.roomuid = roomuid;
        this.token = token;
        this.adminuid = info.getId();
        if (roomuid.equals(adminuid)) isRoomuserid = true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_room_publish_manage);
        mPresenter = new UserInfoDialogPresenter(this);
        mPresenter.getAdminList(token, roomuid);
        initEvent();

    }

    private void initEvent() {
        setCancelable(false);
        btnReport = (Button) findViewById(R.id.dialog_room_manage_btn_report);
        btnCancel = (Button) findViewById(R.id.dialog_room_manage_btn_cancel);
        btnCancel.setOnClickListener(this);
        btnReport.setOnClickListener(this);
    }

    private void initView() {
//       当前用户是该房间的房主
        btnShowMember = (Button) findViewById(R.id.dialog_room_manage_btn_member);
        btnManager = (Button) findViewById(R.id.dialog_room_manage_btn_manage);
        btnShowManager = (Button) findViewById(R.id.dialog_room_manage_btn_managelist);
        btnGag = (Button) findViewById(R.id.dialog_room_manage_btn_gag);
        btnKickPlayer = (Button) findViewById(R.id.dialog_room_manage_btn_kickplaye);


        if (isRoomuserid == showRoomAmin) {
            showRoomAmin = isRoomuserid;
        }
        if (isRoomuser) {

            btnManager.setOnClickListener(this);
            btnGag.setOnClickListener(this);
            btnKickPlayer.setOnClickListener(this);
            btnShowManager.setOnClickListener(this);
        } else if (isRoomAdmin && !showRoomAmin && !isRoomuserid) {//当前登录的用户是该房间的管理员，并且展示的用户不是管理员,也不是房主

            btnGag.setOnClickListener(this);
            btnKickPlayer.setOnClickListener(this);
        }

        if(Const.isDebugMode){
            btnShowMember.setOnClickListener(this);
            btnManager.setOnClickListener(this);
            btnShowManager.setOnClickListener(this);
            btnGag.setOnClickListener(this);
            btnKickPlayer.setOnClickListener(this);


        }
//        控件得显示与隐藏
        isvisible();
    }

    private void isvisible() {
//       当前用户是该房间的房主
        if (isRoomuser) {
            btnManager.setVisibility(View.VISIBLE);
            btnGag.setVisibility(View.VISIBLE);
            btnKickPlayer.setVisibility(View.VISIBLE);
            btnShowManager.setVisibility(View.VISIBLE);

            if (showRoomAmin) {
                btnManager.setText(getContext().getString(R.string.room_live_manage_manage_cancel));
            }
        } else if (isRoomAdmin && !showRoomAmin && !isRoomuserid) {//当前登录的用户是该房间的管理员，并且展示的用户不是管理员,也不是房主
            btnGag.setVisibility(View.VISIBLE);
            btnKickPlayer.setVisibility(View.VISIBLE);
            btnManager.setVisibility(View.INVISIBLE);
            btnShowManager.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            设为管理员
            case R.id.dialog_room_manage_btn_manage:

                if (!showRoomAmin) {
                    webService.sendRequest(WsObjectPool.newRoomManageRequest(WsRoomManageRequest.MANAGE,
                            info.getId(),
                            info.getNickname(),
                            WsRoomManageRequest.ADMINER));
                } else {
                    webService.sendRequest(WsObjectPool.newRoomManageRequest(WsRoomManageRequest.MANAGE,
                            info.getId(),
                            info.getNickname(),
                            WsRoomManageRequest.REMOVERADMINER));
                }
                dismiss();
                break;
//            管理员列表
            case R.id.dialog_room_manage_btn_managelist:

                activity.startActivity(ShowManageListActivity.createIntent(activity, adminInfoList, roomuid));
                dismiss();
                break;
//            禁言
            case R.id.dialog_room_manage_btn_gag:
                webService.sendRequest(WsObjectPool.newRoomManageRequest(WsRoomManageRequest.MANAGE,
                        info.getId(),
                        info.getNickname(),
                        WsRoomManageRequest.DISABLEMSG));
                dismiss();
                break;
//            踢人
            case R.id.dialog_room_manage_btn_kickplaye:

                webService.sendRequest(WsObjectPool.newRoomManageRequest(WsRoomManageRequest.MANAGE,
                        info.getId(),
                        info.getNickname(),
                        WsRoomManageRequest.TYPE_KICK));
                dismiss();
                break;
//            取消
            case R.id.dialog_room_manage_btn_cancel:
                dismiss();
                break;
            case R.id.dialog_room_manage_btn_report:
                CustomToast.makeCustomText(this.getContext(), getContext().getString(R.string.room_live_manage_report_compelet), CustomToast.LENGTH_SHORT).show();
                dismiss();
                break;
        }
    }

    @Override
    public void showUserInfo(UserInfo info) {

    }

    @Override
    public void getAdminLists(List<RoomAdminInfo> adminList) {
        this.adminInfoList = adminList;
        //        判断当前登录用户是否是房主
        if (roomuid.equals(LocalDataManager.getInstance().getLoginInfo().getUserId())) {
            isRoomuser = true;
        }
        if (adminList != null) {
            for (int i = 0; i < adminList.size(); i++) {
//                判断展开的用户的是否是管理员
                if (adminuid.equals(adminList.get(i).getId())) {
                    showRoomAmin = true;
                }
//                判断登录的用户是否是管理员
                if (LocalDataManager.getInstance().getLoginInfo().getUserId().equals(adminList.get(i).getId())) {
                    isRoomAdmin = true;
                }
            }
        } else {

        }
    }

    @Override
    public void adminnullgoinit() {
        //        判断当前登录用户是否是房主
        if (roomuid.equals(LocalDataManager.getInstance().getLoginInfo().getUserId())) {
            isRoomuser = true;
        }
        initView();
    }

    @Override
    public void getHitCode(int code, String hitid) {

    }

    @Override
    public void getRemoveHitCode(int code, String hitid) {

    }

    @Override
    public void getStartCode(int code, String uid) {

    }

    @Override
    public void getRemoveStartCode(int code, String uid) {

    }

    @Override
    public void CompleteDelBlackList(List<BlackList> blackLists, int code,String uid) {

    }

    @Override
    public void CompleteAddBlackList(List<BlackList> blackLists,String uid) {

    }


    @Override
    public void showNetworkException() {

    }

    @Override
    public void showUnknownException() {

    }

    @Override
    public void showDataException(String msg) {

    }

    @Override
    public void showLoadingComplete() {

    }

    @Override
    public Dialog showLoadingDialog() {
        return null;
    }

    @Override
    public void dismissLoadingDialog() {

    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }
}
