package edu.skku.map.withyou_project;



import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;

public class board_list extends AppCompatActivity {
    private DatabaseReference mPostReference;



    ListView text_list ;  //listview
    ArrayList<String> msgs = new ArrayList<>(); //some vector for sotring the msgs
    EditText editText; //for getting the input
    Button button; // for using the onclick listener
    ArrayAdapter<String> adapter;
    String input;
    String ID,chatroom_name;
    String name=null;
    String user_msg_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        Intent getIntent;
        getIntent = getIntent();
        name=getIntent.getStringExtra("myName");


//move to next activity on cl
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                Intent getIntent;

                switch (item.getItemId()){


                    case R.id.arduino:
                        intent = new Intent(board_list.this, Arduino.class);
                        getIntent = getIntent();
                        intent.putExtra("myName",getIntent.getStringExtra("myName"));
                        intent.putExtra("myGmail",getIntent.getStringExtra("myGmail"));
                        intent.putExtra("ReOrVol",getIntent.getStringExtra("ReOrVol"));
                        startActivity(intent);
                        break;


                    case R.id.noticeboard:

                        break;


                    case R.id.donation:
                        intent = new Intent(board_list.this, donate.class);
                        getIntent = getIntent();
                        intent.putExtra("myName",getIntent.getStringExtra("myName"));
                        intent.putExtra("myGmail",getIntent.getStringExtra("myGmail"));
                        intent.putExtra("ReOrVol",getIntent.getStringExtra("ReOrVol"));
                        startActivity(intent);
                        break;


                }


                return false;
            }
        });



        //gtting the text_list
        text_list=findViewById(R.id.List_view);
        //making the the list
        msgs=new ArrayList<String>();
        adapter = new ArrayAdapter<String>(board_list.this, android.R.layout.simple_list_item_1, msgs);

        ////accesssiing initialization for database
        mPostReference = FirebaseDatabase.getInstance().getReference().child("notices_list");//this will navigate the database to the specific chat_roomnname

        text_list.setAdapter(adapter);
        //intent based on each item clicking in the list view
        text_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent;
                Intent getIntent;
                getIntent = getIntent();

                if(getIntent.getStringExtra("ReOrVol").equals("Refugee"))//refugee notice board
                    intent = new Intent(board_list.this, NoticeBoardRefugee.class);
                else
                    intent = new Intent(board_list.this, NoticeBoard.class);
                intent.putExtra("myName",getIntent.getStringExtra("myName"));
                intent.putExtra("myGmail",getIntent.getStringExtra("myGmail"));
                intent.putExtra("ReOrVol",getIntent.getStringExtra("ReOrVol"));
                startActivity(intent);

            }
        });



        mPostReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                updateConversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateConversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    public void updateConversation(DataSnapshot dataSnapshot){
        String msg;
        Iterator i = dataSnapshot.getChildren().iterator();
        while(i.hasNext()){

            msg = (String) ((DataSnapshot)i.next()).getValue();

            adapter.insert(msg, adapter.getCount());
            adapter.notifyDataSetChanged();
            text_list.setAdapter(adapter);
        }


    }
}