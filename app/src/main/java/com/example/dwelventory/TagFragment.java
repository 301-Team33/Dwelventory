package com.example.dwelventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

public class TagFragment extends DialogFragment {
    // Get the needed firebase stuff.
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference tagsRef;

    private String userId;

    private OnFragmentInteractionListener listener;
    private FloatingActionButton tagBackButton;
    private FloatingActionButton tagCreateButton;
    private EditText tagEditText;
    private ListView tagListView;
    private ArrayAdapter<Tag> tagArrayAdapter;
    private ArrayList<Tag> tagDataList;
    private HashMap<String,String> currentTagNames = new HashMap<String,String>();
    public interface OnFragmentInteractionListener{
        void onCloseAction();
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

        Bundle bundle = getArguments();
        userId = bundle.getString("user_id");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tagsRef = db.collection("users").document(userId).collection("tags");

        tagEditText = view.findViewById(R.id.tag_edittext);

        tagListView = view.findViewById(R.id.tag_listview);

        tagDataList = new ArrayList<>();

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
                tagDataList.remove(position);
                tagArrayAdapter.notifyDataSetChanged();
                return false;
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
                    return;
                }
                else {
                    currentTagNames.put(tagName.toLowerCase(), null);
                    tagDataList.add(new Tag(tagName));
                    tagEditText.setText(null);
                    tagsRef.document(tagName);
                    tagArrayAdapter.notifyDataSetChanged();
                    produceTagToast(R.string.tag_create_toast);
                }

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



    private void produceTagToast(int stringResource){
        Toast toast = Toast.makeText(getActivity(),stringResource,Toast.LENGTH_SHORT);
        toast.show();
    }

}




