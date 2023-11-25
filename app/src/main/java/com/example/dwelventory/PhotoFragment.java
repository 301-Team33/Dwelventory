package com.example.dwelventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PhotoFragment extends DialogFragment {
    FloatingActionButton camera;
    FloatingActionButton gallery;

    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public interface PhotoFragmentListener {
        // functions executed when actions are taken on fragment in AddEditActivity
            // from fragment to AddEditActvity
    }

    static PhotoFragment newInstance(String userId){
        // load in the user ID to get the query path for storing and retrieving current user defined
        // tags
        Bundle args = new Bundle();
        args.putString("user_id",userId);

        PhotoFragment photoFragment = new PhotoFragment();
        photoFragment.setArguments(args);
        return photoFragment;
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_photo,null);
        camera = view.findViewById(R.id.camera_button);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return builder.create();
    }
}
