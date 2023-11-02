package com.example.dwelventory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private ArrayList<Item> dataList;

    //private ArrayAdapter<Item> itemAdapter;
    //private ListView itemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize Firebase authentication
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        dataList = new ArrayList<>();

        ArrayList<Item> dataList = new ArrayList<>();
        // fake data
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        String date11 = "7-Jun-2013";
        String date22 = "28-Oct-2023";
        Date date1;
        try {
            date1 = formatter.parse(date11);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Date date2;
        try {
            date2 = formatter.parse(date22);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Item item1 = new Item("Billy", date1, "Pygmy Goat", "Caramel w/ Black Markings", 200);
        Item item2 = new Item("Jinora", date2, "Pygmy Goat", "Caramel w/ Black Markings", 200);
        dataList.add(item1);
        dataList.add(item2);


        ArrayAdapter<Item> itemAdapter = new ItemList(this, dataList);
        ListView itemList = findViewById(R.id.item_list);
        itemList.setAdapter(itemAdapter);

        itemAdapter = new ItemList(this, dataList);
        itemList = findViewById(R.id.item_list);
        itemList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        itemList.setAdapter(itemAdapter);

        // Declare itemList as new final variable
        // (This variable is used only for the longClickListener)
        final ListView finalItemList = itemList;
        final ArrayAdapter<Item> finalItemAdapter = itemAdapter;

        itemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*View checkBoxLayout = view.findViewById(R.id.checkbox);
                checkBoxLayout.setVisibility(View.VISIBLE);*/

                for (int j = 0; j < adapterView.getCount(); j++) {
                    View view_temp = finalItemList.getChildAt(j);
                    if (view_temp != null) {
                        CheckBox checkBox = view_temp.findViewById(R.id.checkbox);
                        checkBox.setVisibility(View.VISIBLE);
                    }
                }

                RelativeLayout select_items = findViewById(R.id.selectMultipleitems);
                select_items.setVisibility(View.VISIBLE);
                //changeListViewHeight(Boolean.TRUE);

                ImageButton closebtn = findViewById(R.id.closebtn);
                ImageButton deletebtn = findViewById(R.id.deletebtn);
                closebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        select_items.setVisibility(View.GONE);

                        for (int j = 0; j < adapterView.getCount(); j++) {
                            View view_temp = finalItemList.getChildAt(j);
                            if (view_temp != null) {
                                CheckBox checkBox = view_temp.findViewById(R.id.checkbox);
                                checkBox.setVisibility(View.GONE);
                            }
                        }
                    }
                });

                deletebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int j = 0; j < adapterView.getCount(); j++) {
                            View view_temp = finalItemList.getChildAt(j);
                            if (view_temp != null) {
                                CheckBox checkBox = view_temp.findViewById(R.id.checkbox);
                                //checkBox.setVisibility(View.GONE);
                                if(checkBox.isChecked()){
                                    finalItemAdapter.remove(dataList.get(j));
                                    finalItemAdapter.notifyDataSetChanged();
                                    checkBox.setChecked(false);
                                    dataList.remove(j);
                                }
                            }
                        }
                        finalItemAdapter.notifyDataSetChanged();
                    }
                });
                /*deletebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int itemRemovedCount = 0;

                        for (int j = dataList.size() - 1; j >= 0; j--) {
                            Item currentItem = dataList.get(j);
                            if (currentItem.isSelected()) {
                                finalItemAdapter.remove(currentItem);
                                dataList.remove(j);
                                itemRemovedCount++;
                            }
                        }

                        if (itemRemovedCount > 0) {
                            Toast.makeText(MainActivity.this, "Deleted " + itemRemovedCount + " Items", Toast.LENGTH_LONG).show();
                        }
                    }
                });*/
                finalItemAdapter.notifyDataSetChanged();
                return true;

            }
        });
        itemList = finalItemList;
        itemAdapter = finalItemAdapter;
        itemList.setAdapter(itemAdapter);
        //itemAdapter.notifyDataSetChanged();

        final FloatingActionButton addButton = findViewById(R.id.add_item_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

   /* private void changeListViewHeight(Boolean b, ListView itemList) {
        if (b) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) itemList.getLayoutParams();
            layoutParams.topMargin = 75;
            itemList.setLayoutParams(layoutParams);
            itemList.requestLayout();
        } else {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) itemList.getLayoutParams();
            layoutParams.topMargin = 0;
            itemList.setLayoutParams(layoutParams);
            itemList.requestLayout();
        }
    }*/

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            signOnAnonymously();
        } else {
            Toast.makeText(MainActivity.this, "Already signed in",
                    Toast.LENGTH_SHORT).show();
            checkUsers(mAuth.getCurrentUser());
        }
    }

    //@Override
    /*public void onOKPressed(Item item) {
        dataList.add(item);
        itemAdapter.notifyDataSetChanged();
    }*/

    /**
     * This method will attempt to sign on anonymously, if the user is not already signed in
     */
    private void signOnAnonymously() {
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in succeeds
                    Log.d("AnonymousAuth", "signInAnonymously:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(MainActivity.this, "Authentication Success",
                            Toast.LENGTH_SHORT).show();
                    checkUsers(user);
                } else {
                    // Sign in fails
                    Log.w("AnonymousAuth", "signInAnonymously:failure", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication Failed",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * This method checks the Firestore database to see if a corresponding 'users' document exists
     *
     * @param user This is the given user currently accessing the app/database
     */
    private void checkUsers(FirebaseUser user) {
        DocumentReference doc = db.collection("users").document(user.getUid());
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("userCheck", "User document exists");
                    } else {
                        Log.d("userCheck", "No such document");
                        // create a new document for the anonymous user if they do not already have one
                        // the new HashMap is to just create an empty document
                        // as we need this document in place to serve as a path for sub-collections
                        usersRef.document(user.getUid()).set(new HashMap<String, Object>());
                    }
                } else {
                    Log.d("userCheck", "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.multiple_items_selected, menu);
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.multiple_items_selected, menu);
    }

}
    /*@Override
    public void onOKPressed(Item item) {
        dataList.add(item);
        itemAdapter.notifyDataSetChanged();
    }*/

