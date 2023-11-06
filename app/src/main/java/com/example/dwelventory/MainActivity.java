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
import android.widget.AdapterView;
import android.widget.Spinner;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
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

import org.checkerframework.checker.units.qual.A;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TagFragment.OnFragmentInteractionListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private ArrayList<Item> dataList;
    private ArrayAdapter<Item> itemAdapter;
    boolean reverseOrder = false;
    private ActivityResultLauncher<Intent> addEditActivityResultLauncher;
    private int ADD_ACTIVITY_CODE = 8;
    private int EDIT_ACTIVITY_CODE = 18;
    private int ADD_EDIT_CODE_OK = 818;
    private FloatingActionButton addButton;

    private Spinner sortSpinner;
    private Spinner orderSpinner;
    private float estTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize Firebase authentication
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        dataList = new ArrayList<>();

        addButton = findViewById(R.id.add_item_button);

        //ArrayList<Item> dataList = new ArrayList<>();

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
        ArrayList<Tag> practiceTags = new ArrayList<>();
        practiceTags.add(new Tag("Tag1"));
        practiceTags.add(new Tag("Tag2"));
        item1.setTags(practiceTags);
        dataList.add(item1);
        dataList.add(item2);

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

            public void getSelectedCount(TextView selected_count){
                int count = 0;
                for (int j = 0; j < itemAdapter.getCount(); j++) {
                    View view_temp = finalItemList.getChildAt(j);
                    if (view_temp != null) {
                        CheckBox checkBox = view_temp.findViewById(R.id.checkbox);
                        if (checkBox.isChecked()){
                            count++;
                        }
                    }
                }
                selected_count.setText("Selected Items : "+ count);
            }
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*View checkBoxLayout = view.findViewById(R.id.checkbox);
                checkBoxLayout.setVisibility(View.VISIBLE);*/


                /*for (int j = 0; j < itemAdapter.getCount(); j++) {
                    View view_temp = finalItemList.getChildAt(j);
                    if (view_temp != null) {
                        CheckBox checkBox = view_temp.findViewById(R.id.checkbox);
                        checkBox.setVisibility(View.VISIBLE);
                        checkBox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getSelectedCount(selected_count);
                            }
                        });
                    }
                }      */



                RelativeLayout select_items = findViewById(R.id.selectMultipleitems);
                TextView selected_count = findViewById(R.id.selectedItems);
                select_items.setVisibility(View.VISIBLE);
                CheckBox select_All = findViewById(R.id.selectAll_checkbox);

                for (int j = 0; j < itemAdapter.getCount(); j++) {
                    View view_temp = finalItemList.getChildAt(j);
                    if (view_temp != null) {
                        CheckBox checkBox = view_temp.findViewById(R.id.checkbox);
                        checkBox.setVisibility(View.VISIBLE);
                        checkBox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getSelectedCount(selected_count);
                            }
                        });
                    }
                }
                select_All.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(select_All.isChecked()){
                            for(int j = 0; j<finalItemList.getCount();j++){
                                View view1 = finalItemList.getChildAt(j);
                                CheckBox checkBox = view1.findViewById(R.id.checkbox);
                                checkBox.setChecked(true);
                                getSelectedCount(selected_count);
                            }
                        }
                        if(!select_All.isChecked()){
                            for(int j = 0; j<finalItemList.getCount();j++){
                                View view1 = finalItemList.getChildAt(j);
                                CheckBox checkBox = view1.findViewById(R.id.checkbox);
                                checkBox.setChecked(false);
                                getSelectedCount(selected_count);
                            }
                        }
                    }
                });
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
                         ArrayList<Item> tobeDeleted = getcheckedItems(finalItemList, finalItemAdapter);
                         for(int i = 0; i<tobeDeleted.size();i++){
                             itemAdapter.remove(tobeDeleted.get(i));
                             dataList.remove(tobeDeleted.get(i));
                             itemAdapter.notifyDataSetChanged();
                         }

                         for(int i = 0; i<finalItemList.getCount();i++){
                             View view1 = finalItemList.getChildAt(i);
                             CheckBox checkBox = view1.findViewById(R.id.checkbox);
                             checkBox.setChecked(false);
                         }
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
                itemAdapter.notifyDataSetChanged();
                return true;

            }
        });

        itemList = finalItemList;
        itemAdapter = finalItemAdapter;
        itemList.setAdapter(itemAdapter);
        //itemAdapter.notifyDataSetChanged();


        final FloatingActionButton addButton = findViewById(R.id.add_item_button);

        sortSpinner = findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.sort_array,
                android.R.layout.simple_spinner_item
        );
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sort = parent.getItemAtPosition(position).toString();
                switch(sort) {
                    case "Date":
                        ItemSorter.sortDate(dataList, reverseOrder);
                        break;
                    case "Description":
                        ItemSorter.sortDescription(dataList, reverseOrder);
                        break;
                    case "Make":
                        ItemSorter.sortMake(dataList, reverseOrder);
                        break;
                    case "Estimated Value":
                        ItemSorter.sortEstValue(dataList, reverseOrder);
                        break;
                }
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sortSpinner.setAdapter(sortAdapter);

        orderSpinner = findViewById(R.id.order_spinner);
        ArrayAdapter<CharSequence> orderAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.order_array,
                android.R.layout.simple_spinner_item
        );
        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String order = parent.getItemAtPosition(position).toString();
                if (order.equals("Descending")) {
                    reverseOrder = true;
                }
                else {
                    reverseOrder = false;
                }

                String sort = sortSpinner.getSelectedItem().toString();
                switch(sort) {
                    case "Date":
                        ItemSorter.sortDate(dataList, reverseOrder);
                        break;
                    case "Description":
                        ItemSorter.sortDescription(dataList, reverseOrder);
                        break;
                    case "Make":
                        ItemSorter.sortMake(dataList, reverseOrder);
                        break;
                    case "Estimated Value":
                        ItemSorter.sortEstValue(dataList, reverseOrder);
                        break;
                }
                itemAdapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        orderSpinner.setAdapter(orderAdapter);

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
                                dataList.add(item);
                                itemAdapter.notifyDataSetChanged();
                            } else if (requestCode == EDIT_ACTIVITY_CODE) {
                                // Handle the result for editing
                                Log.d("resultTag", "i am about to edit the item");
                                int position = data.getIntExtra("position", -1);
                                dataList.set(position, item);
                                itemAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
        );

        // View and/or edit the item when clicked
        itemList.setOnItemClickListener((adapterView, view, i, l)->{
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            intent.putExtra("mode", "edit");
            Log.d("mainTag", "position: " + i);
            Log.d("mainitemclickTag", "date from list " + dataList.get(i).getDate());
            Item copyItem = makeCopy( dataList.get(i) );

            Log.d("mainTag", "hi copyDate is " + copyItem.getDate());

            intent.putExtra("item", copyItem);
            intent.putExtra("date", copyItem.getDate());
            intent.putExtra("position", i);
            intent.putExtra("requestCode", EDIT_ACTIVITY_CODE);
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

    private ArrayList<Item> getcheckedItems(ListView L, ArrayAdapter<Item> I) {
        L = findViewById(R.id.item_list);
        ArrayList<Item> A = new ArrayList<>();
        for(int i = 0; i<L.getCount(); i++){
            View view = L.getChildAt(i);
            CheckBox checkBox = view.findViewById(R.id.checkbox);
            Item item = (Item) itemAdapter.getItem(i);
            if(checkBox.isChecked()){
                A.add(item);
            }
        }
        return A;
    }

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


    public void onOKPressed(Item item) {

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


