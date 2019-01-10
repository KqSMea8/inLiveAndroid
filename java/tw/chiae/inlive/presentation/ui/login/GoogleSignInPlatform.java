package tw.chiae.inlive.presentation.ui.login;

import android.content.Context;
import android.content.Intent;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.google.GooglePlus;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;

/**
 * Created by rayyeh on 2017/3/17.
 */

public class GoogleSignInPlatform extends GooglePlus {
    private final GoogleSignInPresenter pGoogleSignIn;
    private String[] GooglePermission;

    public GoogleSignInPlatform(Context context) {
        super(context);
        pGoogleSignIn = new GoogleSignInPresenter((LoginUiInterface) context);



    }

    @Override
    protected void doAuthorize(String[] permission) {
       this.GooglePermission = permission;
    }

    public void init() {
       // pGoogleSignIn.init();
    }

    public void onStart() {
        //pGoogleSignIn.onStart();
    }

    public void getUserInfo() {
        //pGoogleSignIn.authorize();
    }

    public void handleSignInResult(Intent data) {
       // pGoogleSignIn.handleSignInResult(data);
    }

    protected void doAuthorize(HashMap<String, Object> res) {
        if(this.isClientValid() && !this.isSSODisable()) {
            GoogleSignInPlatform.this.db.put("nickname", res.get("nickname").toString());
            GoogleSignInPlatform.this.db.put("icon", res.get("icon").toString());
            GoogleSignInPlatform.this.db.put("gender", res.get("gender").toString());
            GoogleSignInPlatform.this.db.put("snsUserUrl", res.get("snsUserUrl").toString());
            GoogleSignInPlatform.this.db.put("birthday", res.get("birthday").toString());
            GoogleSignInPlatform.this.db.put("isSigin", "true");
            if(GoogleSignInPlatform.this.listener != null) {
                GoogleSignInPlatform.this.listener.onComplete(this, 0, res);
            }


        }else{
            this.doWebAuthorize(GooglePermission);
        }
    }

    public void thirdLogin(HashMap<String, Object> res) {
      //  pGoogleSignIn.thirdLogin(res);
    }
}
