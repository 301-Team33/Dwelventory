package com.example.dwelventory;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements TagFragment.OnFragmentInteractionListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference itemsRef;
    private ArrayList<Item> dataList;
  
    private ArrayAdapter<Item> itemAdapter;
    private ActivityResultLauncher<Intent> addEditActivityResultLauncher;
    private int ADD_ACTIVITY_CODE = 8;
    private int EDIT_ACTIVITY_CODE = 18;
    private int ADD_EDIT_CODE_OK = 818;
    private FloatingActionButton addButton;
    private TextView totalCost;
    public int estTotalCost=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize Firebase authentication
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        checkUsers(user);
        Log.d("itemTag", "after user");
        String path = "/users/"+user.getUid()+"/items";
        Log.d("itemTag", "path:"+path);
        itemsRef = db.collection(path);
        dataList = new ArrayList<>();

        itemsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.e("itemTag",error.toString());
                    return;
                }
                if (value != null){
                    dataList.clear();
                    for(QueryDocumentSnapshot doc: value){
                        Log.d("itemTag", "size of value is "+value.size());
                        Log.d("itemTag", user.getUid());
                        String storedRefID = doc.getId();
                        Log.d("itemTag", String.format("Item(%s) fetched", storedRefID));
                        Log.d("itemTag", "added default");
                        String name = doc.get("description", String.class);
                        Log.d("itemTag", String.format("Itemname(%s) fetched", name));
                        Date date = doc.get("date", Date.class);
                        String make =  doc.get("make", String.class);
                        String model = doc.get("model", String.class);
                        int serial = doc.get("serialNumber", int.class);
                        int estValue = doc.get("estValue", int.class);
                        ArrayList photos = doc.get("photos", ArrayList.class);
                        String comment = doc.get("comment", String.class);
                        boolean selected = doc.get("selected", boolean.class);
                        Item item = new Item(name, date, make, model, serial, estValue, comment, photos);
                        if (!storedRefID.equals("null")){
                            Log.d("itemTag1", String.format("Item(%s) was not null", storedRefID)+storedRefID.getClass());
                            item.setItemRefID(UUID.fromString(storedRefID));
                            dataList.add(item);
                        }
                    }
                    itemAdapter.notifyDataSetChanged();
                    setTotal(dataList);
                }
            }
        });

        totalCost = findViewById(R.id.total_cost);
        String text = getString(R.string.totalcost, estTotalCost);
        totalCost.setText(text);
        addButton = findViewById(R.id.add_item_button);
        itemAdapter = new ItemList(this, dataList);
        ListView itemList = findViewById(R.id.item_list);
        itemList.setAdapter(itemAdapter);

        itemAdapter = new ItemList(this, dataList);
        itemList = findViewById(R.id.item_list);
        itemList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        itemList.setAdapter(itemAdapter);



        // Declare itemList as new final variable
        // (This variable is used only for the longClickListener)
        ListView finalItemList = itemList;
        ArrayAdapter<Item> finalItemAdapter = itemAdapter;

        itemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*View checkBoxLayout = view.findViewById(R.id.checkbox);
                checkBoxLayout.setVisibility(View.VISIBLE);*/

                for (int j = 0; j < itemAdapter.getCount(); j++) {
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

                        for (int j = 0; j < itemAdapter.getCount(); j++) {
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
                        for (int j = 0; j < itemAdapter.getCount(); j++) {
                            View view_temp = finalItemList.getChildAt(j);
                            if (view_temp != null) {
                                CheckBox checkBox = view_temp.findViewById(R.id.checkbox);
                                //checkBox.setVisibility(View.GONE);
                                if(checkBox.isChecked()){
                                    // get item and its id
                                    Item deleteItem = dataList.get(j);
                                    UUID refId = deleteItem.getItemRefID();
                                    // remove from list
                                    finalItemAdapter.remove(dataList.get(j));
                                    finalItemAdapter.notifyDataSetChanged();
                                    checkBox.setChecked(false);
                                    // remove from firebase
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String path = "/users/"+user.getUid()+"/items/"+refId.toString();
                                    DocumentReference itemDocRef = db.document(path);
                                    itemDocRef.delete();
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
                //finalItemAdapter.notifyDataSetChanged();
                return true;

            }
        });

        itemList = finalItemList;
        itemAdapter = finalItemAdapter;
        itemList.setAdapter(itemAdapter);
        //itemAdapter.notifyDataSetChanged();



        addEditActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d("resultTag", "activity result opened");
                    Log.d("resultTag", "result code: " + result.getResultCode());
                    if (result.getResultCode() == ADD_EDIT_CODE_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            // Extract item
                            Item item = data.getParcelableExtra("item");
                            // Get and set date bc its weird
                            Date date = (Date) data.getSerializableExtra("date");
                            item.setDate(date);
                            int requestCode = data.getIntExtra("requestCode", -1);
                            Log.d("resultTag", "request code: " + requestCode);
                            if (requestCode == ADD_ACTIVITY_CODE) {
                                // Handle the result for adding
                                Log.d("resultTag", "i am about to add the item");
                                item.setItemRefID();
                                Log.d("itemTag", "New Item RefID: " + item.getItemRefID());
                                dataList.add(item);
                                estTotalCost += item.getEstValue();
                                itemsRef.document(String.valueOf( item.getItemRefID() )).set(item);
                                itemAdapter.notifyDataSetChanged();
                            } else if (requestCode == EDIT_ACTIVITY_CODE) {
                                // Handle the result for editing
                                Log.d("resultTag", "i am about to edit the item");
                                int position = data.getIntExtra("position", -1);
                                String itemRefId = data.getStringExtra("itemRefID");
                                Log.d("itemTag", "from intent RefID: " + itemRefId);
                                Log.d("itemTag", "from editActivity RefID: " + item.getItemRefID());
                                item.setItemRefID(UUID.fromString(itemRefId));
                                Log.d("itemTag", "after setting RefID: " + item.getItemRefID());
                                itemsRef.document(String.valueOf( item.getItemRefID() )).set(item);
                                itemAdapter.notifyDataSetChanged();
                            }
                            String cost = getString(R.string.totalcost, estTotalCost);
                            totalCost.setText(cost);
                        }
                    }
                }
        );

        // View and/or edit the item when clicked
        itemList.setOnItemClickListener((adapterView, view, i, l)->{
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            intent.putExtra("mode", "edit");
            Log.d("mainTag", "position: " + i);
            Item itemToCopy = dataList.get(i);
            Item copyItem = makeCopy( itemToCopy);
            Log.d("mainTag", "hi copyDate is " + copyItem.getDate());

            intent.putExtra("item", copyItem);
            intent.putExtra("date", copyItem.getDate());
            intent.putExtra("position", i);
            intent.putExtra("requestCode", EDIT_ACTIVITY_CODE);
            String itemRefID = itemToCopy.getItemRefID().toString();
            Log.d("itemTag", "RefID going to edit activity: " + itemRefID);
            intent.putExtra("itemRefID", itemRefID);
            addEditActivityResultLauncher.launch(intent);

        });
        // go to add activity
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            intent.putExtra("mode", "add");
            intent.putExtra("requestCode", ADD_ACTIVITY_CODE);
            addEditActivityResultLauncher.launch(intent);
        });
    }
    /*
    * This makes a copy of the item
    * @param item the item object to be copied
    * @return copyItem an Item object with the same values as the input item
    * */
    public Item makeCopy(Item item){
        Log.d("mainTag", "in copy ");
        assert item != null;
        String itemName = item.getDescription();
        Log.d("mainTag", "name is" + itemName);
        Date itemDate = item.getDate();
        String itemMake = item.getMake();
        String itemModel = item.getModel();
        int itemSerial = item.getSerialNumber();
        int itemValue = item.getEstValue();
        String itemComment = item.getComment();
        List itemPhotos = item.getPhotos();
        Log.d("mainTag", "Date is" + itemDate);
        Log.d("mainTag", "Make is " + itemMake);
        Item copyItem = new Item(itemName, itemDate, itemMake, itemModel, itemSerial, itemValue, itemComment, itemPhotos);
        return copyItem;
    }
    /*
    * This calculates the total cost of all the items and then
    * sets the textview to that cost
    * @param dataList the arraylist containing the items
    * */
    public void setTotal(ArrayList<Item> dataList){
        estTotalCost = 0;
        for (int i=0; i < dataList.size(); i++){
            Item item = dataList.get(i);
            int val = item.getEstValue();
            estTotalCost += val;
            String cost = getString(R.string.totalcost, estTotalCost);
            totalCost.setText(cost);
        }
    }


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
                    // make item collection after verifying user
                    itemsRef = db.collection("users").document(user.getUid()).collection("items");
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
    public void onCloseAction() {
        TagFragment tagFragment = (TagFragment) getSupportFragmentManager().findFragmentByTag("TAG_FRAG");
        tagFragment.dismiss();
    }

    @Override
    public void onTagApplyAction(ArrayList<Tag> applyTags) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        String date22 = "28-Oct-2023";
        Date date2;
        TagFragment tagFragment = (TagFragment) getSupportFragmentManager().findFragmentByTag("TAG_FRAG");
        tagFragment.dismiss();
        try {
            date2 = formatter.parse(date22);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Item item3 = new Item("Jinora", date2, "Pygmy Goat", "Caramel w/ Black Markings", 200);
        item3.setTags(applyTags);
        Log.d("tag", "onTagApplyAction: " + item3.getTags().get(0).getTagName() + item3.getTags().get(1).getTagName());
    }

    @Override
    public void onTagDeletion(Tag deletedTag) {

    }


    public void deleteItems(ArrayList<Item> dataList, ArrayList<Item> toremove){
        if (toremove.size() == 0){
            Toast.makeText(MainActivity.this, "Select items to delete",
                    Toast.LENGTH_SHORT).show();
        } else {
            for (Item item : toremove) {
                dataList.remove(item);
                itemAdapter.notifyDataSetChanged();
            }
        }
    }
}


