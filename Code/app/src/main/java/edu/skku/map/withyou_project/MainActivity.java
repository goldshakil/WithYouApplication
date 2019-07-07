package edu.skku.map.withyou_project;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    int RC_SIGN_IN = 1000;
    String TAG = "TAG";
    //ArrayList<String> data;
    private DatabaseReference mPostReference;
    EditText userId,userPwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,  this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_google);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

        Button btn = (Button)findViewById(R.id.login);
        Button btnsignup = (Button)findViewById(R.id.signup);
        userId = (EditText)findViewById(R.id.idE);
        userPwd = (EditText)findViewById(R.id.pwE);
        userPwd.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
        userPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mPostReference = FirebaseDatabase.getInstance().getReference();
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                getFirebaseDatabase();


            }

        });
        btnsignup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this,SignUp.class);
                startActivity(intent);
                updateUI(true);

            }

        });


        findViewById(R.id.sign_in_google).setOnClickListener(this);
    }
    public void getFirebaseDatabase()
    {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange", "Data is Updated");
                //data.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    memberInfo get = postSnapshot.getValue(memberInfo.class);
                    String[] info = {get.id, get.name, get.passwd,get.gmail,get.ReOrVol};
                    //String result = info[0] + " : " + info[1] + "(" + info[3] + ", " + info[2] + ")";
                    //data.add(result);
                    if(info[0].equals(userId.getText().toString()) && info[2].equals(userPwd.getText().toString()))
                    {
                        Intent intent = new Intent(MainActivity.this,Arduino.class);
                        intent.putExtra("myName",info[1]);
                        intent.putExtra("myGmail",info[3]);
                        intent.putExtra("ReOrVol",info[4]);
                        startActivity(intent);
                    }
                    Log.d("getFirebaseDatabase", "key: " + key);
                    Log.d("getFirebaseDatabase", "info: " + info[0] + info[1] + info[2] + info[3]+info[4]);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mPostReference.child("member").addValueEventListener(postListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_in_google:
                signIn();
                break;
        }
    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else{

        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
             //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName())); // 로그인한 사람의이름
             //mStatusTextView.append(getString(R.string.signed_in_fmt,acct.getEmail())); // 로그인한 사람의 이메일
            //Intent intent = new Intent(MainActivity.this,NoticeBoard.class);
            Intent intent = new Intent(MainActivity.this,SignUp.class);
            intent.putExtra("myGmail",getString(R.string.signed_in_fmt,acct.getEmail()));
            intent.putExtra("myName",getString(R.string.signed_in_fmt, acct.getDisplayName()));
            startActivity(intent);
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }
    private void updateUI(boolean signedIn) {
        if (signedIn) {
           // findViewById(R.id.sign_in_google).setVisibility(View.GONE);

            //mStatusTextView.setText("로그인 성공!!");
        } else {
           // findViewById(R.id.sign_in_google).setVisibility(View.VISIBLE);
            //mStatusTextView.setText("로그인 실패!!");
        }
    }





    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), ""+connectionResult, Toast.LENGTH_SHORT).show();
    }
}