package com.example.dwelventory;

import static android.app.Activity.RESULT_OK;

import static androidx.camera.core.impl.utils.ContextUtil.getBaseContext;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.internal.StorageReferenceUri;

import org.checkerframework.checker.units.qual.A;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.UUID;

public class PhotoFragment extends DialogFragment {
    private FloatingActionButton camera;
    private FloatingActionButton gallery;
    private ArrayList<Uri> photos;
    private ArrayList<String> photoPaths;
    private ImageView imageView;
    private String userId;
    private ActivityResultLauncher<Intent> photoFragmentResultLauncher;
    private ImageView selectedGalleryImage;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ArrayList<Uri> selectedImages;
    private ArrayAdapter<Uri> photoAdapter;
    private Uri currentUri;
    private ListView photoListView;
    private Button photoApply;
    private onPhotoFragmentInteractionListener listener;

    public interface onPhotoFragmentInteractionListener{
        void onPhotoConfirmPressed(ArrayList<String> photosToAppend);
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TagFragment.OnFragmentInteractionListener){
            listener = (PhotoFragment.onPhotoFragmentInteractionListener) context;
        }else{
            throw new RuntimeException();
        }

        photoFragmentResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // SOURCE: https://www.geeksforgeeks.org/how-to-select-multiple-images-from-gallery-in-android/
                    // Utilized: "HOW TO SELECT MULTIPLE IMAGES FROM GALLERY IN ANDROID?" to select many images
                    // From the open gallery in Android Studio.
                    // Author: User: anniaanni
                    // Usage: Learned code snippets required to open the gallery and select images from the gallery.
                    // Adapted the Source code and tutorial to utilize the activity result launcher.
                    if (result.getResultCode() == RESULT_OK){
                        Intent intent = result.getData();
                        if (intent.getClipData() != null) {
                            for (int i = 0; i < intent.getClipData().getItemCount(); i++){
                                currentUri = intent.getClipData().getItemAt(i).getUri();
                                // Process the URI by adding it not only to the ListView but also the
                                // Firestore.
                                try {
                                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), currentUri);
                                    photos.add(currentUri);
                                    photoAdapter.notifyDataSetChanged();

                                    // Save the photo to the specified firestore.
                                    String path = "images/" + UUID.randomUUID().toString();
                                    StorageReference ref = storageRef.child(path);
                                    String path2 = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),imageBitmap,"newpic",null);
                                    photoPaths.add(path);
                                    ref.putFile(Uri.parse(path2))
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Toast.makeText(getActivity().getBaseContext(),"Upload successful",Toast.LENGTH_SHORT);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getActivity().getBaseContext(),"Upload failed",Toast.LENGTH_SHORT);
                                                }
                                            });

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

    static PhotoFragment newInstance(String userId, ArrayList<String> images){
        // load in the user ID to get the query path for storing and retrieving current user defined
        Bundle args = new Bundle();
        args.putString("user_id",userId);

        args.putStringArrayList("images",images);

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
        photoApply = view.findViewById(R.id.photo_apply_button);


        photoAdapter = new PhotoCustomList(this.getContext(), photos);
        photoListView.setAdapter(photoAdapter);

        //imageView = view.findViewById(R.id.imageView);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

        Bundle bundle = getArguments();

        if (bundle.containsKey("images")){
            photoPaths = bundle.getStringArrayList("images");
            if(photoPaths == null){ // photos first added when editing it, not on create.
                photoPaths = new ArrayList<>();
            }
        }else{ // Item isn't created yet so the photo paths cant be set yet.
            photoPaths = new ArrayList<>();
        }

        loadPhotos(photoPaths);

        /*photos = bundle.getParcelableArrayList("images");
        if (photos.size() != 0 && photos != null){
            imageView.setImageBitmap(photos.get(0));
        }*/

        photoApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPhotoConfirmPressed(photoPaths);
                dismiss();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start camera activity
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                startActivity(intent);
            }
        });


        // SOURCE: https://www.geeksforgeeks.org/how-to-select-multiple-images-from-gallery-in-android/
        // Utilized: "HOW TO SELECT MULTIPLE IMAGES FROM GALLERY IN ANDROID?" to select many images
        // From the open gallery in Android Studio.
        // Author: User: anniaanni
        // Usage: Learned code snippets required to open the gallery and select images from the gallery.
        // Adapted the Source code and tutorial to utilize the activity result launcher.
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



    public void loadPhotos(ArrayList<String> stringQueries) {


        for (String currentSearch : stringQueries) {
            // https://firebase.google.com/docs/storage/android/list-files#java API research
            // Last updated 2023-11-22 UTC.
            // Title: List files with Cloud Storage on Android
            // API REFERENCE: learning how to query the files in the database storage and checking the
            // file names since the queries were not returning anything with our path string.
            // Realization: gs://dwelventory etc is appended to the paths file...
            StorageReference listReference = storageRef.child("images/");

            listReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    for (StorageReference item: listResult.getItems()){
                        String fullPath = "gs://dwelventory.appspot.com/" + currentSearch;
                        Log.d("OUR TAG", "This is the item..." + item.toString());
                        if(fullPath.equals(item.toString())){

                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    try {
                                        Log.d("uritag", "onSuccess: uri:" + uri.toString());
                                        photos.add(uri);
                                        photoAdapter.notifyDataSetChanged();
                                    }catch (Exception e){
                                        Log.d("error tag", "Error occured fetching bitmap");
                                    }
                                }
                            });

                        }
                    }
                }
            });
        }

    }

}


