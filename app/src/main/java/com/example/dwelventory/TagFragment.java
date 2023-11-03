package com.example.dwelventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TagFragment extends DialogFragment{
    // Get the needed firebase stuff.
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference tagsRef;

    private String userId;

    private OnFragmentInteractionListener listener;
    private Button tagApplyButton;
    private Button deleteCancelButton;
    private Button deleteConfirmButton;
    private TextView deletePrompt;
    private LinearLayout deleteLayout;
    private FloatingActionButton tagBackButton;
    private FloatingActionButton tagCreateButton;
    private EditText tagEditText;
    private ListView tagListView;
    private ArrayAdapter<Tag> tagArrayAdapter;
    private ArrayList<Tag> tagDataList;
    private ArrayList<Tag> tagsToApply;
    private HashMap<String,String> currentTagNames = new HashMap<String,String>();
    private int itemIndex;



    public interface OnFragmentInteractionListener{
        void onCloseAction();
        void onTagApplyAction(ArrayList<Tag> applyTags);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener){
            listener = (OnFragmentInteractionListener) context;
        }else{
            throw new RuntimeException();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.tag_edit_fragment_layout,null);
        tagBackButton = view.findViewById(R.id.tag_back_button);
        tagCreateButton = view.findViewById(R.id.tag_create_button);
        tagApplyButton = view.findViewById(R.id.tag_apply_button);

        deleteLayout = view.findViewById(R.id.tag_deletion_views);
        deleteCancelButton = view.findViewById(R.id.no_action_button);
        deleteConfirmButton = view.findViewById(R.id.delete_button);
        deletePrompt = view.findViewById(R.id.delete_prompt_text);

        deleteLayout.setVisibility(View.GONE);
        deletePrompt.setVisibility(View.GONE);




        Bundle bundle = getArguments();
        userId = bundle.getString("user_id");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tagsRef = db.collection("users").document(userId).collection("tags");

        tagEditText = view.findViewById(R.id.tag_edittext);

        tagListView = view.findViewById(R.id.tag_listview);

        tagDataList = new ArrayList<>();
        tagsToApply = new ArrayList<>();

        tagArrayAdapter = new TagCustomList(this.getContext(), tagDataList);
        tagListView.setAdapter(tagArrayAdapter);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);


        tagBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCloseAction();
            }
        });

        tagListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                itemIndex = position;
                deletePrompt.setText("Delete: " + tagDataList.get(position).getTagName() + "?" );
                deleteLayout.setVisibility(View.VISIBLE);
                deletePrompt.setVisibility(View.VISIBLE);
                return true;
            }
        });

        tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(tagsToApply.contains(tagDataList.get(position))){
                    // This is the case where the tag was already selected and now they want to
                    // deselect it, removing it from the set of tags that need to be applied!
                    view.setBackgroundColor(Color.GRAY);
                    tagsToApply.remove(tagDataList.get(position));
                }else{
                    // Else Tag is Selected and now added to the Tag Data List that needs to be
                    // applied.
                    view.setBackgroundColor(Color.DKGRAY);
                    tagsToApply.add(tagDataList.get(position));
                }
            }
        });

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
                        currentTagNames.put(storedTagName.toLowerCase(),"1");
                        tagDataList.add(new Tag(storedTagName));
                    }
                    tagArrayAdapter.notifyDataSetChanged();
                }
            }


        });

        if (bundle.containsKey("current_item")){
            Item item = (Item) bundle.getSerializable("current_item");
            ArrayList<Tag> currentAppliedTags = item.getTags();
            String testString = "";
            for (Tag appliedTag : currentAppliedTags){
                String appliedTagName = appliedTag.getTagName();
                tagsToApply.add(appliedTag);

                // Set the background color of the listview to show that the Tag was selected!
                int i = 0;
                for (Tag userDefinedTags: tagDataList){
                    String testTagName = userDefinedTags.getTagName();
                    testString = testString + testTagName;
                    if (1 == 1){
                        View updateView = tagListView.getChildAt(i);
                        updateView.setBackgroundColor(Color.BLUE);
                        tagApplyButton.setBackgroundColor(Color.BLUE);
                        break;
                    }
                    i++;

                }



            }
            testString = Integer.toString(tagDataList.size());
            tagApplyButton.setText(testString);


        }

        deleteCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLayout.setVisibility(View.GONE);
                deletePrompt.setVisibility(View.GONE);
            }
        });

        deleteConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tag toDelete = tagDataList.get(itemIndex);
                deleteLayout.setVisibility(View.GONE);
                deletePrompt.setVisibility(View.GONE);
                currentTagNames.remove(toDelete.getTagName().toLowerCase());
                tagsRef.document(toDelete.getTagName()).delete();
                tagDataList.remove(itemIndex);
                tagArrayAdapter.notifyDataSetChanged();
            }
        });

        tagCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if the editText is null or just a space.
                String tagName = tagEditText.getText().toString();
                if (tagName == null || tagName.trim().length() == 0){
                    produceTagToast(R.string.invalid_tag_toast);
                    tagEditText.setText(null);
                }
                // check if tag already exists in list with same name (case insensitive)
                else if (currentTagNames.containsKey(tagName.toLowerCase())) {
                    tagEditText.setText(null);
                    produceTagToast(R.string.tag_already_exists_toast);
                }
                else {
                    currentTagNames.put(tagName.toLowerCase(), null);
                    tagDataList.add(new Tag(tagName));
                    tagEditText.setText(null);
                    tagsRef.document(tagName).set(new HashMap<Object,Object>());
                    tagArrayAdapter.notifyDataSetChanged();
                    produceTagToast(R.string.tag_create_toast);
                }

            }
        });

        tagApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTagApplyAction(tagsToApply);
            }
        });




       return builder.create();
    }

    static TagFragment newInstance(String userId){
        Bundle args = new Bundle();
        args.putString("user_id",userId);

        TagFragment tagFragment = new TagFragment();
        tagFragment.setArguments(args);
        return tagFragment;
    }

    static TagFragment newInstance(String userId,Item item){
        Bundle args = new Bundle();
        args.putString("user_id",userId);
        args.putSerializable("current_item",item);

        TagFragment tagFragment = new TagFragment();
        tagFragment.setArguments(args);

        return tagFragment;
    }



    private void produceTagToast(int stringResource){
        Toast toast = Toast.makeText(getActivity(),stringResource,Toast.LENGTH_SHORT);
        toast.show();
    }

}




