package com.ljy.musicplayer.biomusicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.loginlibrary.LoginLib;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.Session;
import com.ljy.musicplayer.biomusicplayer.view.AppActivity;

import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;
    private LoginLib loginLib;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        KakaoSDK.init(new KakaoSdkAdapter());
        loginLib = new LoginLib(this, new AppActivity());

        // Google
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);

        // Facebook
        final LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile","email"));
        loginButton.registerCallback(loginLib.facebookLogin.getCallbackManager(),
                loginLib.facebookLogin.getFacebookCallback());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 이미 signed 됬는지 확인
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        loginLib.googleLogin.updateUI(account);

        // 페북 엑세스 토큰 있을시 자동 로그인
        if(AccessToken.getCurrentAccessToken() != null){
            Log.d("ktest","이미 페북 엑세스 토큰있음");

            GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.i("result",object.toString());
                    Intent intent = new Intent(MainActivity.this,AppActivity.class);

                    intent.putExtra("id",object.optString("id"));
                    intent.putExtra("email",object.optString("name"));
                    intent.putExtra("name",object.optString("email"));

                    startActivity(intent);
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,birthday");
            graphRequest.setParameters(parameters);
            graphRequest.executeAsync();

        }
        // 카카오톡 확인 (login 됬는지 확인)
        if(Session.getCurrentSession().isOpened()){
            Log.w("ovlab","kakao session");
            loginLib.kakaoLogin.redirectSignupActivity();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(loginLib.kakaoLogin.sessionCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            loginLib.googleLogin.handleSignInResult(task);

        }
        loginLib.facebookLogin.getCallbackManager().onActivityResult(requestCode, resultCode, data);

        if(Session.getCurrentSession().handleActivityResult(requestCode,resultCode,data)){
            return ;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sign_in_button:
                Intent signInIntent = loginLib.googleLogin.
                        getmGoogleSignInClient().getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
            case R.id.sign_out_button:
                break;
            case R.id.disconnect_button:
                break;
        }

    }
}
