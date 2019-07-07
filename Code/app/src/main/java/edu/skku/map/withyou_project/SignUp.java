package edu.skku.map.withyou_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    private String id;
    private String name;
    private String passwd;
    private String gmail;
    private String ReOrVol;
    private String receiveData =null;
    private String receiveData2 =null;
    RadioGroup userRV;
    EditText userID,userName,userPW,userGmail;
    private DatabaseReference mPostReference;
    ArrayList<String> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        userRV = (RadioGroup)findViewById(R.id.group);
        userRV.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGRoup, int checkedId) {
                switch (checkedId){
                    case R.id.chooseR:
                        ReOrVol = "Refugee";
                        break;
                    case R.id.chooseV:
                        ReOrVol = "Volunteer";
                        break;

                }
            }
        });

        Intent intent = getIntent();
        receiveData = intent.getStringExtra("myGmail");
        receiveData2 = intent.getStringExtra("myName");
        data = new ArrayList<>();
        userName=(EditText)findViewById(R.id.nameUserET);
        userID=(EditText)findViewById(R.id.userIDET);
        userPW=(EditText)findViewById(R.id.userPWET);
        userGmail=(EditText)findViewById(R.id.userGmailET);

        Button signUpBtn=(Button)findViewById(R.id.button2);

        if(receiveData != null)
        {
            gmail = receiveData;
            userGmail.setText(receiveData);
        }
        if(receiveData2 !=null)
        {
            name = receiveData2;
            userName.setText(receiveData2);
        }

        //비밀번호 * 로 바꾸기
        userPW.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
        userPW.setTransformationMethod(PasswordTransformationMethod.getInstance());

        mPostReference = FirebaseDatabase.getInstance().getReference();


        signUpBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                id = userID.getText().toString();
                name = userName.getText().toString();
                passwd = userPW.getText().toString();
                gmail = userGmail.getText().toString();


                if(id.length() * name.length() * passwd.length() * gmail.length() * ReOrVol.length() ==0)
                    Toast.makeText(SignUp.this, "Data is missing", Toast.LENGTH_SHORT).show();
                else // 수정
                {
                    postFirebaseDatabase(true);
                    Intent intent;
                    if(ReOrVol.equals("Refugee"))
                    {
                        intent = new Intent(SignUp.this,NoticeBoardRefugee.class);
                    }
                    else
                    {
                        intent = new Intent(SignUp.this,NoticeBoard.class);
                    }
                    intent.putExtra("myName",name);
                    intent.putExtra("myGmail",gmail);
                    startActivity(intent);
                }
            }

        });

        getFirebaseDatabase();
    }

    public void getFirebaseDatabase() {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange", "Data is Updated");
                data.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    memberInfo get = postSnapshot.getValue(memberInfo.class);
                    String[] info = {get.id, get.name, get.passwd,get.gmail,get.ReOrVol};
                    //String result = info[0] + " : " + info[1] + "(" + info[3] + ", " + info[2] + ")";
                    //data.add(result);
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
    public void postFirebaseDatabase(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            memberInfo mem = new memberInfo(id, name, passwd,gmail,ReOrVol);
            postValues = mem.toMap();
        }
        childUpdates.put("/member/" + id, postValues);
        mPostReference.updateChildren(childUpdates);
        clearET();
    }
    public void clearET () {
        userID.setText("");
        userName.setText("");
        userPW.setText("");
        userGmail.setText("");

        id  ="";
        name = "";
        passwd = "";
        gmail ="";
        ReOrVol ="";
    }
}

