package tw.chiae.inlive.presentation.ui.login;

import java.util.HashMap;

/**
 * Created by rayyeh on 2017/3/17.
 */

public interface GoogleSignInInterface {
    void onGoogleLoginComplete(HashMap<String, Object> result);

    void onGoogleLoginError();

    void onGoogleLoginWait();
}
