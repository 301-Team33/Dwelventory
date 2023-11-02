package com.example.dwelventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;

public class TagFragment extends DialogFragment {

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
                    tagArrayAdapter.notifyDataSetChanged();
                    produceTagToast(R.string.tag_create_toast);
                }

            }
        });


       return builder.create();
    }



    private void produceTagToast(int stringResource){
        Toast toast = Toast.makeText(getActivity(),stringResource,Toast.LENGTH_SHORT);
        toast.show();
    }

}




