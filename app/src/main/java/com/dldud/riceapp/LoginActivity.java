package com.dldud.riceapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static com.kakao.util.maps.helper.Utility.getPackageInfo;


public class LoginActivity extends BaseActivity {

    final int APP_PERMISSION = 1111;
    private Button skipbtn;
    private com.kakao.usermgmt.LoginButton btnKakao;
    private SessionCallback callback; // kakao
    private CallbackManager callbackManager; // facebook
    LoginButton facebook_login; //facebook


    //Naver
    OAuthLogin mOAuthLoginModule;
    OAuthLoginButton authLoginButton;
    Context mContext;
    //Naver

    String TAG;

    private String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageView fakeKakao;

        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= 23){
            checkPermissions();
        }
/*
        skipbtn = (Button)findViewById(R.id.skipBtn);
        skipbtn.setOnClickListener(skipListener);
*/
        fakeKakao =(ImageView)findViewById(R.id.fake_kakao);
        fakeKakao.setOnClickListener(loginListener);
        btnKakao = (com.kakao.usermgmt.LoginButton)findViewById(R.id.loginButton);


        //kakao login callback
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        //로그인이력확인

        Session.getCurrentSession().checkAndImplicitOpen();

    }

    View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fake_kakao:
                    if (callback == null) {
                        callback = new SessionCallback();
                        Session.getCurrentSession().addCallback(callback);
                    }
                    Session.getCurrentSession().checkAndImplicitOpen();
                    btnKakao.performClick();
                    break;
            }
        }
    };
    private boolean checkPermissions(){
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions){
            result = ContextCompat.checkSelfPermission(this,pm);
            if(result != PackageManager.PERMISSION_GRANTED){
                permissionList.add(pm);
            }
        }
        if(!permissionList.isEmpty()){
            ActivityCompat.requestPermissions(this,
                    permissionList.toArray(new String[permissionList.size()]),
                    APP_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            redirectSignupActivity();  // 세션 연결성공 시 redirectSignupActivity() 호출
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
            setContentView(R.layout.activity_login); // 세션 연결이 실패했을때
        }                                            // 로그인화면을 다시 불러옴
    }
/*
    protected void redirectSignupActivity() {       //세션 연결 성공 시 SignupActivity로 넘김
        final Intent intent = new Intent(this, KaKaoSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
*/

}
