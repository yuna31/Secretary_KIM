package com.example.home.secretary_kim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import java.security.MessageDigest;

/**
 * Created by YUNA on 2018-07-13.
 */

public class LoginActivity extends Activity {

    private static final String TAG = "OAuthSampleActivity";

    private static String OAUTH_CLIENT_ID = "OEJ_xBOx60FqccYhV_AQ";
    private static String OAUTH_CLIENT_SECRET = "SEAA5rBGq8";
    private static String OAUTH_CLIENT_NAME = "네이버 아이디로 로그인";

    private static OAuthLogin mOAuthLoginInstance;
    private static Context mContext;

    /**
     * UI 요소들
     */
    private TextView mApiResultText;

    private static TextView mOAuthState;

    private OAuthLoginButton mOAuthLoginButton;

    private SessionCallback mKakaocallback;
    // view
    //private Button login_button;
    private LoginButton loginButton;
    private TextView tv_user_id;
    private TextView tv_user_name;
    private ImageView iv_user_profile;
    private String userName;
    private String userId;
    private String profileUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        * 로그인 정보가 있으면 바로 UserActivity로 넘어가야됨 <- 사용자 정보도 같이 넘어가야되나?
        */

        setContentView(R.layout.activity_login);

        mContext = this;

        initData();
        initView();

        /*카카오 로그인*/
        // 헤쉬키를 가져온다
        getAppKeyHash();

        /*
        tv_user_id = (TextView) findViewById(R.id.tv_user_id);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        iv_user_profile = (ImageView) findViewById(R.id.iv_user_profile);
        */
        loginButton = (LoginButton) findViewById(R.id.btnKakaoLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 카카오 로그인 요청
                isKakaoLogin();
            }
        });
    }

    private void initData() {
        mOAuthLoginInstance = OAuthLogin.getInstance();

        mOAuthLoginInstance.showDevelopersLog(true);
        mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);

		/*
         * 2015년 8월 이전에 등록하고 앱 정보 갱신을 안한 경우 기존에 설정해준 callback intent url 을 넣어줘야 로그인하는데 문제가 안생긴다.
		 * 2015년 8월 이후에 등록했거나 그 뒤에 앱 정보 갱신을 하면서 package name 을 넣어준 경우 callback intent url 을 생략해도 된다.
		 */
        //mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME, OAUTH_callback_intent_url);
    }

    private void initView() {
        mApiResultText = (TextView) findViewById(R.id.api_result_text);

        mOAuthState = (TextView) findViewById(R.id.oauth_state);

        mOAuthLoginButton = (OAuthLoginButton) findViewById(R.id.buttonOAuthLoginImg);
        mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);

        updateView();
    }

    //로그아웃할때 표시됨
    private void updateView() {
        Log.d(TAG, "updateView : mOauthAT" + mOAuthLoginInstance.getAccessToken(mContext));
        Log.d(TAG, "updateView : mOauthRT" + mOAuthLoginInstance.getRefreshToken(mContext));
        Log.d(TAG, "updateView : mOauthExpiresString" + String.valueOf(mOAuthLoginInstance.getExpiresAt(mContext)));
        Log.d(TAG, "updateView : mOauthTokenType" + mOAuthLoginInstance.getTokenType(mContext));
        mOAuthState.setText(mOAuthLoginInstance.getState(mContext).toString());
    }

    @Override
    protected void onResume() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();
    }

    /**
     * startOAuthLoginActivity() 호출시 인자로 넘기거나, OAuthLoginButton 에 등록해주면 인증이 종료되는 걸 알 수 있다.
     */
    static private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {

                String accessToken = mOAuthLoginInstance.getAccessToken(mContext);
                String refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
                long expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
                String tokenType = mOAuthLoginInstance.getTokenType(mContext);

                Log.d(TAG, "OAuthLoginHandler : mOauthAT" + accessToken);
                Log.d(TAG, "OAuthLoginHandler : mOauthRT" + refreshToken);
                Log.d(TAG, "OAuthLoginHandler : mOauthExpires" + String.valueOf(expiresAt));
                Log.d(TAG, "OAuthLoginHandler : mOauthTokenType" + tokenType);

                mOAuthState.setText(mOAuthLoginInstance.getState(mContext).toString());

            } else {
                String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext);
                Toast.makeText(mContext, "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        }

    };

    public void onButtonClick(View v) throws Throwable {

        switch (v.getId()) {
            case R.id.buttonOAuth: {
                mOAuthLoginInstance.startOauthLoginActivity(LoginActivity.this, mOAuthLoginHandler);
                break;
            }
            /*
            case R.id.buttonVerifier: {
                new RequestApiTask().execute();
                break;
            }
            case R.id.buttonRefresh: {
                new RefreshTokenTask().execute();
                break;
            }
            */
            case R.id.buttonOAuthLogout: {
                mOAuthLoginInstance.logout(mContext);
                updateView();
                break;
            }
            case R.id.buttonOAuthDeleteToken: {
                new DeleteTokenTask().execute();
                break;
            }
            default:
                break;
        }
    }

    private class DeleteTokenTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            boolean isSuccessDeleteToken = mOAuthLoginInstance.logoutAndDeleteToken(mContext);

            if (!isSuccessDeleteToken) {
                // 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태이다
                // 실패했어도 클라이언트 상에 token 정보가 없기 때문에 추가적으로 해줄 수 있는 것은 없음
                Log.d(TAG, "errorCode:" + mOAuthLoginInstance.getLastErrorCode(mContext));
                Log.d(TAG, "errorDesc:" + mOAuthLoginInstance.getLastErrorDesc(mContext));
            }

            return null;
        }

        protected void onPostExecute(Void v) {
            updateView();
        }
    }
/*
    private class RequestApiTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            mApiResultText.setText((String) "");
        }

        @Override
        protected String doInBackground(Void... params) {
            String url = "https://openapi.naver.com/v1/nid/me";
            String at = mOAuthLoginInstance.getAccessToken(mContext);
            return mOAuthLoginInstance.requestApi(mContext, at, url);
        }

        protected void onPostExecute(String content) {
            mApiResultText.setText((String) content);
        }
    }

    private class RefreshTokenTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return mOAuthLoginInstance.refreshAccessToken(mContext);
        }

        protected void onPostExecute(String res) {
            updateView();
        }
    }
    */

    /*카카오 로그인*/
    private void isKakaoLogin() {
        // 카카오 세션을 오픈한다
        mKakaocallback = new SessionCallback();
        com.kakao.auth.Session.getCurrentSession().addCallback(mKakaocallback);
        com.kakao.auth.Session.getCurrentSession().checkAndImplicitOpen();
        com.kakao.auth.Session.getCurrentSession().open(AuthType.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN, LoginActivity.this);
    }

    protected void KakaorequestMe() {

        UserManagement.getInstance().requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                int ErrorCode = errorResult.getErrorCode();
                int ClientErrorCode = -777;

                if (ErrorCode == ClientErrorCode) {
                    Toast.makeText(getApplicationContext(), "카카오톡 서버의 네트워크가 불안정합니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("TAG" , "오류로 카카오로그인 실패 ");
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d("TAG" , "오류로 카카오로그인 실패 ");
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                profileUrl = userProfile.getProfileImagePath();
                userId = String.valueOf(userProfile.getId());
                userName = userProfile.getNickname();

                /*
                Log.i(TAG, "prifileUrl:" + profileUrl);
                Log.i(TAG, "userId:" + userId);
                Log.i(TAG, "userName:" + userName);
*/
                setLayoutText();
            }

            @Override
            public void onNotSignedUp() {
                // 자동가입이 아닐경우 동의창
            }
        });
    }

    private void setLayoutText(){
        tv_user_id.setText(userId);
        tv_user_name.setText(userName);

        /*
        Picasso.with(this)
                .load(profileUrl)
                .fit()
                .into(iv_user_profile);*/
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //Log.d(TAG, "getAppKeyHash > Hash key:" + something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //og.e(TAG, "getAppKeyHash > name not found : " + e.toString());
        }
    }
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            // 사용자 정보를 가져옴, 회원가입 미가입시 자동가입 시킴
            KakaorequestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                //Log.d(TAG, "Session CallBack Error > " + exception.getMessage());
            }
        }
    }
}
