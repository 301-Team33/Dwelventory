package com.example.dwelventory;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * This sets up the item list adapter for the list view
 * @author Maggie Lacson
 * */
public class ItemList extends ArrayAdapter<Item> {
    private final Context context;
    private ArrayList<Item> items;
    /**
     * Constructor
     * @param context (Context)
     * @param items (ArrayList<Item>) an array list of items that are going to be displayed
     * @see Item
     * @see MainActivity
     * */
    public ItemList(Context context, ArrayList<Item> items){
        super(context,android.R.layout.simple_list_item_multiple_choice, items);
        this.context = context;
        this.items = items;
    }
    /**
     * This sets up the list view and displays the item name, cost, and picture
     * @param position (int) the position inside the list
     * @param convertView (View) the view in which the item information is being displayed
     * @param parent (ViewGroup) the view parent in which this view sits
     * @return view (View) the view after being populated with the item's information
     * */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_content,parent,false);
        }

        Item item = items.get(position);
        TextView description = view.findViewById(R.id.description); // Updated ID here
        TextView estValue = view.findViewById(R.id.estValue); // Updated ID here
        TextView date = view.findViewById(R.id.date_item);
        TextView make = view.findViewById(R.id.make_item);
        TextView model = view.findViewById(R.id.model_item);

        description.setText(item.getDescription());
        estValue.setText("$"+ String.valueOf(item.getEstValue()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        // Format the Date object as a string
        String strDate = dateFormat.format(item.getDate());
        date.setText(strDate);
        make.setText(item.getMake());
        model.setText(item.getModel());

        ImageView image = view.findViewById(R.id.itemImage);

        if (item.getPhotos() == null || item.getPhotos().size() == 0){
            image.setImageResource(R.drawable.goat);
        }else{
            String firstPhotoPath = item.getPhotos().get(0);

            FirebaseStorage storage;
            StorageReference storageRef;


            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
            StorageReference listReference = storageRef.child("images/");

            listReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    for (StorageReference item:listResult.getItems()){
                        String fullPath = "gs://dwelventory.appspot.com/" + firstPhotoPath;
                        Log.d("OUR TAG", "This is the item..." + item.toString());
                        if(fullPath.equals(item.toString())){
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(context).load(uri).into(image);
                                }
                            });
                        }
                    }
                }
            });
        }

        // Get the checkbox and set its state based on the item's selection status
        CheckBox checkBox = view.findViewById(R.id.checkbox);
//        checkBox.setChecked(item.isSelected());

        return view;
    }
}
