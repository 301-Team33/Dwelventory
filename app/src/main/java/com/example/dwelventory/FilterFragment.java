package com.example.dwelventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FilterFragment extends DialogFragment {
    private String[] filterInput;
    private String filterOption;
    private FilterFragmentListener listener;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof FilterFragmentListener){
            listener = (FilterFragmentListener) context;
        }
        else{
            throw new RuntimeException(context.toString()
                    + "must implement OnFragmentInteractionListener");
        }
    }

    /**
     * This interface defines call back methods for communicating about filter related actions
     * from the filter fragment to the MainActivity.
     */
    public interface FilterFragmentListener {
        void onMakeFilterApplied(String[] filterInput);
        void onDateFilterApplied(Date start, Date end);
        void onKeywordFilterApplied(ArrayList<String> keywords);
        void onTagFilterApplied(ArrayList<Tag> tags);
        void onClearFilterApplied();
    }
    public static FilterFragment newInstance(String filterOption,String userId) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putString("filter_option", filterOption);
        args.putString("user",userId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Verify arguments
        if (getArguments() != null) {
            filterOption = getArguments().getString("filter_option");
        }
        else {
            throw new IllegalStateException("No filter option provided to FilterFragment");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // If filter option selected was make
        if("Make".equals(filterOption)){
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_select_make, null);
            EditText makeInput = view.findViewById(R.id.filter_make_etext);
            Button doneButton = view.findViewById(R.id.filter_make_donebtn);

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    String makeText = makeInput.getText().toString();
                    filterInput = makeText.split(" ");
                    if (listener != null) {
                        listener.onMakeFilterApplied(filterInput);
                    }

                    dismiss();
                }
            });
            builder.setView(view);
            return builder.create();
        }
        else if("Date".equals(filterOption)){   // For date filter
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_select_drange, null);
            EditText dateStart = view.findViewById(R.id.filter_date_cal1);
            EditText dateEnd = view.findViewById(R.id.filter_date_cal2);
            Button doneButton = view.findViewById(R.id.filter_date_donebtn);

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    String dateStartText = dateStart.getText().toString();
                    String dateEndText = dateEnd.getText().toString();
                    if (listener != null) {
                        Date start;
                        Date end;
                        try {
                            start =new SimpleDateFormat("dd/MM/yyyy").parse(dateStartText);
                            end =new SimpleDateFormat("dd/MM/yyyy").parse(dateEndText);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                        listener.onDateFilterApplied(start, end);
                    }

                    dismiss();
                }
            });
            builder.setView(view);
            return builder.create();
        }
        else if("Description Words".equals(filterOption)) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_select_keywords, null);
            EditText keywordInput = view.findViewById(R.id.filter_kwords_etext);
            Button doneButton = view.findViewById(R.id.filter_kwords_donebtn);
            Button addButton = view.findViewById(R.id.add_keyword);
            ListView keywordListView = view.findViewById(R.id.filter_keyword_lv);
            ArrayAdapter<String> keywordAdapter;
            ArrayList<String> keywordList;

            keywordList = new ArrayList<>();
            keywordAdapter = new ArrayAdapter<>(this.getContext(),R.layout.tag_content,R.id.tag_text,keywordList);
            keywordListView.setAdapter(keywordAdapter);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String keywordText = keywordInput.getText().toString();
                    if (keywordText != null && !keywordText.isBlank()) {
                        keywordList.add(keywordText);
                        keywordAdapter.notifyDataSetChanged();
                        keywordInput.setText(null);
                    }
                    keywordInput.setText(null);
                }
            });

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    if (listener != null) {
                        listener.onKeywordFilterApplied(keywordList);
                    }

                    dismiss();
                }
            });
            builder.setView(view);
            return builder.create();
        }
        else if("Tags".equals(filterOption)) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_select_tags,null);
            Button doneButton = view.findViewById(R.id.filter_tags_donebtn);
            ListView tagListView = view.findViewById(R.id.filter_tag_lv);
            TextView tagCount = view.findViewById(R.id.tag_selected_count);

            // initialize the tag listview nessecities..
             ArrayAdapter<Tag> tagArrayAdapter;
             ArrayList<Tag> tagDataList = new ArrayList<>();

            tagArrayAdapter = new TagCustomList(this.getContext(), tagDataList,new ArrayList<Tag>());
            tagListView.setAdapter(tagArrayAdapter);

            // get the firebase data to fill the list view with the required tags.
             FirebaseAuth mAuth;
             FirebaseFirestore db;
             CollectionReference tagsRef;
             String userId = (String) getArguments().getString("user");

             // Initialize an ArrayLiST FOR THE TAGS that we will be filtering by..
             ArrayList<Tag> selectedTags = new ArrayList<>();

            // get the collection for the current user defined tag names
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            Log.d("database Tag", "/users/"+userId+"/tags");
            tagsRef = db.collection("users").document(userId).collection("tags");

            tagsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null){
                        Log.e("Firestore",error.toString());
                        return;
                    }
                    if (value != null){
                        tagDataList.clear();
                        for(QueryDocumentSnapshot doc: value){
                            String storedTagName = doc.getId();
                            Log.d("Firestore", String.format("Tag(%s) fetched", storedTagName));
                            Tag storedTag = new Tag(storedTagName);
                            tagDataList.add(storedTag);
                        }
                        tagArrayAdapter.notifyDataSetChanged(); // notify the adapter of the changed dataset.
                    }
                }

            });

            tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (selectedTags.contains(tagDataList.get(position)) == false){
                            view.setBackgroundColor(getResources().getColor(R.color.selected,null));
                            selectedTags.add(tagDataList.get(position));
                            int numTags = selectedTags.size();
                            if (numTags >= 10){
                                tagCount.setText("TAGS: 9+");
                            }else{
                                tagCount.setText("TAGS: " + numTags);
                            }
                        }else{
                            view.setBackgroundColor(getResources().getColor(R.color.gray,null));
                            selectedTags.remove(tagDataList.get(position));
                            int numTags = selectedTags.size();
                            if (numTags >= 10){
                                tagCount.setText("TAGS: 9+");
                            }else{
                                tagCount.setText("TAGS: " + numTags);
                            }
                        }
                }




            });

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedTags.size() != 0) {
                        listener.onTagFilterApplied(selectedTags);
                        dismiss();
                    }
                }
            });




            builder.setView(view);
            return builder.create();
        }
        else if("Clear Filter".equals(filterOption)){
            listener.onClearFilterApplied();
            return builder.create();
        }
        else {
            builder.setTitle("Error")
                    .setMessage("No filter option was provided or an unknown filter option was used.")
                    .setPositiveButton("OK", null);
            return builder.create();
        }
    }

    public void setFilterListener(FilterFragmentListener listener) {
        this.listener = listener;
    }
}
