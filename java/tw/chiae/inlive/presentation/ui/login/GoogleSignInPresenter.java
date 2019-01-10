package tw.chiae.inlive.presentation.ui.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/*import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.PeopleScopes;
import com.google.api.services.people.v1.model.Birthday;
import com.google.api.services.people.v1.model.Date;
import com.google.api.services.people.v1.model.Gender;
import com.google.api.services.people.v1.model.Nickname;
import com.google.api.services.people.v1.model.Person;*/
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Subscription;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.bean.ThirdLoginPlatform;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.domain.LoginManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;
import tw.chiae.inlive.presentation.ui.main.MainActivity;

/**
 * Created by rayyeh on 2017/3/17.
 */
public class GoogleSignInPresenter extends BasePresenter<LoginUiInterface>  {
    protected GoogleSignInPresenter(LoginUiInterface uiInterface) {
        super(uiInterface);
    }
//public class GoogleSignInPresenter extends BasePresenter<LoginUiInterface> implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
   /* private final LoginManager loginManager;
    LoginSelectActivity mainActivity;
    private GoogleApiClient mGoogleApiClient;
    public final static int RC_SIGN_IN = 9001;

    public GoogleSignInPresenter(LoginUiInterface uiInterface) {
        super(uiInterface);
        this.mainActivity = (LoginSelectActivity) uiInterface;
        loginManager = new LoginManager();
    }

    public void init() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PROFILE))
                .requestProfile()
                .requestServerAuthCode(mainActivity.getResources().getString(R.string.default_web_client_id),false)
                .requestId()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(mainActivity)
                .enableAutoManage(mainActivity *//* FragmentActivity *//*, this *//* OnConnectionFailedListener *//*)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .build();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void handleSignInResult(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if(result.isSuccess()){
            GoogleSignInAccount acct = result.getSignInAccount();
            new PeoplesAsync(acct.getId()).execute(acct.getServerAuthCode());

        }else{
            mainActivity.onGoogleLoginError();
        }

    }


    public void onStart() {
        mGoogleApiClient.connect();
    }

    public void authorize() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        mainActivity.startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    public void thirdLogin(HashMap<String, Object> res) {
        mainActivity.onGoogleLoginWait();
        String openId = res.get("id").toString();
        Subscription subscription = loginManager.thirdLogin(openId, ThirdLoginPlatform.PLATFORM_FACEBOOK, res)
                .compose(this.<BaseResponse<LoginInfo>>applyAsySchedulers())
                .subscribe(new BaseObserver<BaseResponse<LoginInfo>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<LoginInfo> response) {
                        LocalDataManager.getInstance().saveLoginInfo(response.getData());
                        getUiInterface().startActivityAndFinishOthers();
                    }
                })
                ;
        addSubscription(subscription);
    }

    private class PeoplesAsync extends AsyncTask<String, Void, HashMap<String,Object>>{


        private final String userid;

        public PeoplesAsync(String id) {
            this.userid = id;
        }

        @Override
        protected HashMap<String,Object> doInBackground(String... params) {
            HashMap<String,Object> nameList = new HashMap<>();
            try {

                People peopleService = PeopleHelper.setup(mainActivity, params[0]);
                Person meProfile = peopleService.people().get("people/me").execute();

                String nickname = meProfile.getNames()!=null && meProfile.getNames().size()>0?meProfile.getNames().get(0).getDisplayName():"";
                String icon = meProfile.getCoverPhotos()!=null && meProfile.getCoverPhotos().size()>0?meProfile.getCoverPhotos().get(0).getUrl():"";
                String gender = meProfile.getGenders()!=null && meProfile.getGenders().size()>0?meProfile.getGenders().get(0).getValue():"";
                String snsUserUrl = meProfile.getPhotos()!=null && meProfile.getPhotos().size()>0?meProfile.getPhotos().get(0).getUrl():"";
                Date birthdayDate = meProfile.getBirthdays()!=null && meProfile.getBirthdays().size()>0?meProfile.getBirthdays().get(0).getDate():null;
                String birthday  = birthdayDate!=null?birthdayDate.getYear()+"/"+birthdayDate.getMonth()+"/"+birthdayDate.getDay():"";


                nameList.put("id",userid);
                nameList.put("nickname",nickname);
                nameList.put("icon",icon);
                nameList.put("gender",gender);
                nameList.put("snsUserUrl",snsUserUrl);
                nameList.put("birthday",birthday);
                nameList.put("isSign","true");

            } catch (IOException e) {
                e.printStackTrace();
            }
            return  nameList;
        }

        @Override
        protected void onPostExecute(HashMap<String, Object> res) {
            super.onPostExecute(res);
            mainActivity.onGoogleLoginComplete(res);
        }
    }

*/

}
