package edu.skku.map.withyou_project;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class donate extends AppCompatActivity {
    EditText name,amount,email,tel;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                Intent getIntent;
                switch (item.getItemId()){


                    case R.id.arduino:
                        intent = new Intent(donate.this, Arduino.class);
                        getIntent = getIntent();
                        intent.putExtra("myName",getIntent.getStringExtra("myName"));
                        intent.putExtra("myGmail",getIntent.getStringExtra("myGmail"));
                        intent.putExtra("ReOrVol",getIntent.getStringExtra("ReOrVol"));
                        startActivity(intent);
                        break;

                    case R.id.noticeboard:

                        intent = new Intent(donate.this, board_list.class);
                        getIntent = getIntent();
                        intent.putExtra("myName",getIntent.getStringExtra("myName"));
                        intent.putExtra("myGmail",getIntent.getStringExtra("myGmail"));
                        intent.putExtra("ReOrVol",getIntent.getStringExtra("ReOrVol"));
                        startActivity(intent);                        break;


                    case R.id.donation:
                        break;


                }


                return false;
            }
        });

        name =  (EditText)findViewById(R.id.editName);
        amount =  (EditText)findViewById(R.id.editAmount);
        email =  (EditText)findViewById(R.id.editEmail);
        tel =  (EditText)findViewById(R.id.editTel);
        Intent intent =getIntent();
        String myName =intent.getStringExtra("myName");
        String myGmail =intent.getStringExtra("myGmail");
        name.setText(myName);
        email.setText(myGmail);
        btn = (Button)findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(donate.this,donate_webview.class);
                intent.putExtra("myName",intent.getStringExtra("myName"));
                intent.putExtra("myGmail",intent.getStringExtra("myGmail"));
                intent.putExtra("myAmount",amount.getText().toString());
                intent.putExtra("myTel",tel.getText().toString());
                startActivity(intent);

            }

        });
    }
}
