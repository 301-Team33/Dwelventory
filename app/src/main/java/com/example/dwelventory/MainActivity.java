package com.example.dwelventory;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private ArrayList<Item> dataList;
    private ArrayAdapter<Item> itemAdapter;
    private ActivityResultLauncher<Intent> addEditActivityResultLauncher;
    private int ADD_ACTIVITY_CODE = 8;
    private int EDIT_ACTIVITY_CODE = 18;
    private int ADD_EDIT_CODE_OK = 818;



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
        int serial = 12731;
        String comment = "so cute";
        List photos = null;
        Item item1 = new Item("Billy", date1, "Pygmy Goat", "Caramel w/ Black Markings",serial,200, comment, photos);
        Item item2 = new Item("Jinora", date2, "Pygmy Goat", "Caramel w/ Black Markings", 200);
        dataList.add(item1);
        dataList.add(item2);

        itemAdapter = new ItemList(this, dataList);
        ListView itemList = findViewById(R.id.item_list);
        itemList.setAdapter(itemAdapter);

        final FloatingActionButton addButton = findViewById(R.id.add_item_button);
        addEditActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == ADD_EDIT_CODE_OK) {
                        // Handle the result here
                        Intent data = result.getData();
                        if (data != null) {
                            // Extract data from the result Intent and handle it
                            // For example, you can check request codes to distinguish between add and edit
                            int requestCode = data.getIntExtra("requestCode", -1);
                            if (requestCode == ADD_ACTIVITY_CODE) {
                                // Handle the result for adding
                                Item newItem = data.getParcelableExtra("item");
                                // Add the newObject to your list or adapter and update the UI

                            } else if (requestCode == EDIT_ACTIVITY_CODE) {
                                // Handle the result for editing
                                Item updatedItem = data.getParcelableExtra("item");
                                int position = data.getIntExtra("position", -1);
                                // Add the newObject to your list or adapter and update the UI
                            }
                        }
                    }
                }
        );

//        addEditActivityResultLauncher = registerForActivityResult( new ActivityResultContracts.StartActivityForResult(),result->{
//            if (result.getResultCode() == ADD_EDIT_CODE_OK) {
//                Intent data = result.getData();
//                Item newItem = (Item) data.getSerializableExtra("item");
//                dataList.add(newItem);
//                itemAdapter.notifyDataSetChanged();
//            }
//        });
        // View and/or edit the item when clicked
        itemList.setOnItemClickListener((adapterView, view, i, l)->{
            Intent intent = new Intent(getApplicationContext(), AddEditActivity.class);
            intent.putExtra("mode", "edit");
            Item copyItem = makeCopy( dataList.get(i) );
            Log.d("mainTag", "hi copyDate is " + copyItem.getDate());

            intent.putExtra("item", copyItem);
            intent.putExtra("date", copyItem.getDate());
            intent.putExtra("position", i);
            addEditActivityResultLauncher.launch(intent);

//            startActivity(intent);
        });
        // go to add activity
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddEditActivity.class);
            intent.putExtra("mode", "add");
            addEditActivityResultLauncher.launch(intent);
//            addEditActivityResultLauncher.launch(intent);
//            startActivity(intent);
//            new ActivityResultContracts.StartActivityForResult(), result->(intent, ADD_EDIT_CODE);
        });
    }
    public Item makeCopy(Item item){
        String itemName = item.getDescription();
        Date itemDate = item.getDate();
        String itemMake = item.getMake();
        String itemModel = item.getModel();
        int itemSerial = item.getSerialNumber();
        int itemValue = item.getEstValue();
        String itemComment = item.getComment();
        List itemPhotos = item.getPhotos();
        Log.d("mainTag", "Date is" + itemDate);
        Item copyItem = new Item(itemName, itemDate, itemMake, itemModel, itemSerial, itemValue, itemComment, itemPhotos);
        return copyItem;
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == ADD_ACTIVITY_CODE && resultCode == ADD_EDIT_CODE_OK) {
//            if (data != null) {
//                Item newItem = data.getParcelableExtra("item");
//                // Add the newObject to your list or adapter and update the UI
//            }
//        }
//        if (requestCode == EDIT_ACTIVITY_CODE && resultCode == ADD_EDIT_CODE_OK) {
//            if (data != null) {
//                Item updatedItem = data.getParcelableExtra("item");
//                int position = data.getIntExtra("position", -1);
//                // Add the newObject to your list or adapter and update the UI
//            }
//        }
//    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            signOnAnonymously();
        }
        else {
            Toast.makeText(MainActivity.this, "Already signed in",
                    Toast.LENGTH_SHORT).show();
            checkUsers(mAuth.getCurrentUser());
        }
    }


    public void onOKPressed(Item item) {
        dataList.add(item);
        itemAdapter.notifyDataSetChanged();
    }

    /**
     *  This method will attempt to sign on anonymously, if the user is not already signed in
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
     * @param user
     *     This is the given user currently accessing the app/database
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
}

