package com.example.dwelventory;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TagFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final FloatingActionButton addButton = findViewById(R.id.add_item_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TagFragment checkIfExists = (TagFragment) getSupportFragmentManager().findFragmentByTag("TAG_FRAG");
                if (checkIfExists != null){
                    checkIfExists.show(getSupportFragmentManager(),"TAG_FRAG");
                    System.out.println("Hey");
                }
                else {
                    new TagFragment().show(getSupportFragmentManager(), "TAG_FRAG");
                    System.out.println("Hey2");
                }
            }
        });
    }



    @Override
    public void onCloseAction() {
        TagFragment tagFragment = (TagFragment) getSupportFragmentManager().findFragmentByTag("TAG_FRAG");
        tagFragment.dismiss();
    }
}
