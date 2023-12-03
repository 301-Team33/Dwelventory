package com.example.dwelventory;

import static android.app.Activity.RESULT_OK;

import static androidx.camera.core.impl.utils.ContextUtil.getBaseContext;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.A;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.UUID;

public class PhotoFragment extends DialogFragment {
    private FloatingActionButton camera;
    private FloatingActionButton gallery;
    private ArrayList<Bitmap> photos;
    private ImageView imageView;
    private String userId;
    private ActivityResultLauncher<Intent> photoFragmentResultLauncher;
    private ImageView selectedGalleryImage;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ArrayList<Uri> selectedImages;
    private ArrayAdapter<Bitmap> photoAdapter;
    private Uri currentUri;
    private ListView photoListView;

    public void onAttach(Context context) {
        super.onAttach(context);

        photoFragmentResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK){
                        Intent intent = result.getData();
                        if (intent.getClipData() != null) {
                            for (int i = 0; i < intent.getClipData().getItemCount(); i++){
                                currentUri = intent.getClipData().getItemAt(i).getUri();
                                // Process the URI by adding it not only to the ListView but also the
                                // Firestore.
                                try {
                                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), currentUri);
                                    photos.add(imageBitmap);
                                    photoAdapter.notifyDataSetChanged();
                                }catch(Exception exception){
                                    Log.d("exception handelled...", "onAttach: Exception");
                                }
                            }
                        }

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
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        selectedImages = new ArrayList<>();
        photos = new ArrayList<>();
        photoListView = view.findViewById(R.id.photo_list_view);

        photoAdapter = new PhotoCustomList(this.getContext(), photos);
        photoListView.setAdapter(photoAdapter);

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
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setType("image/*");
                photoFragmentResultLauncher.launch(Intent.createChooser(intent,"picture"));
            }
        });

        return builder.create();
    }
}
