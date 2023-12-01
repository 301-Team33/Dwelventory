package com.example.dwelventory;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PhotoFragment extends DialogFragment {
    private FloatingActionButton camera;
    private FloatingActionButton gallery;
    private ArrayList<Bitmap> photos;
    private ImageView imageView;
    private String userId;
    private ActivityResultLauncher<Intent> photoFragmentResultLauncher;
    private Uri photoChosen;
    private ImageView selectedGalleryImage;

    public void onAttach(Context context) {
        super.onAttach(context);

        photoFragmentResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK){
                        photoChosen = result.getData().getData();
                        selectedGalleryImage.setImageURI(photoChosen);
                        Log.d("HERE WE GO", "onAttach: " + photoChosen.toString());
                    }

                });


    }

    /*public interface PhotoFragmentListener {
        void addPhoto(String path);
        // functions executed when actions are taken on fragment in AddEditActivity
    }*/

    static PhotoFragment newInstance(String userId, ArrayList<Bitmap> images){
        // load in the user ID to get the query path for storing and retrieving current user defined
        Bundle args = new Bundle();
        args.putString("user_id",userId);
        args.putParcelableArrayList("images", images);

        PhotoFragment photoFragment = new PhotoFragment();
        photoFragment.setArguments(args);
        return photoFragment;
    }

    static PhotoFragment newInstance(String userId){
        Bundle args = new Bundle();
        args.putString("userId", userId);

        PhotoFragment photoFrag = new PhotoFragment();
        photoFrag.setArguments(args);
        return photoFrag;
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_photo,null);
        camera = view.findViewById(R.id.camera_button);
        gallery = view.findViewById(R.id.gallery_button);
        selectedGalleryImage = view.findViewById(R.id.return_image);
        //imageView = view.findViewById(R.id.imageView);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

        Bundle bundle = getArguments();

        /*photos = bundle.getParcelableArrayList("images");
        if (photos.size() != 0 && photos != null){
            imageView.setImageBitmap(photos.get(0));
        }*/

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start camera activity
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                startActivity(intent);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                photoFragmentResultLauncher.launch(intent);
            }
        });

        return builder.create();
    }
}
