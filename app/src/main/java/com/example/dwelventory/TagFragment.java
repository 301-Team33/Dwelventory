package com.example.dwelventory;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;



public class TagFragment extends Fragment {

    private FloatingActionButton backButton;
    private FloatingActionButton addButton;
    private EditText tagEditText;
    private ListView listView;
    private OnFragmentInteractionListener listener;
    public interface OnFragmentInteractionListener{

        void onClosePressed();
        void onAddPressed(Tag newTag);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener){
            listener = (OnFragmentInteractionListener) context;
        }else{
            throw new RuntimeException(context.toString() + "must implement OnFragmentInteractionListener");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tag_edit_fragment_layout,container);

        backButton = view.findViewById(R.id.close_button);
        addButton = view.findViewById(R.id.add_button);
        tagEditText = view.findViewById(R.id.tag_edittext);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClosePressed();
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }

}
