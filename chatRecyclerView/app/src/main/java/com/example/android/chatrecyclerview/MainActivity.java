package com.example.android.chatrecyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ArrayList<Boolean> sent=new ArrayList<Boolean>();
        final ArrayList<String> messages=new ArrayList<String>();
        messages.add("hi");
        sent.add(false);
        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.recycler_view);
        final RecyclerAdapter adapter=new RecyclerAdapter(sent,messages);
        LinearLayoutManager layoutManager=new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        Button send=(Button)findViewById(R.id.sendButton);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText msg=(EditText)findViewById(R.id.messageEditText);
                messages.add(msg.getText().toString());
                sent.add(true);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
