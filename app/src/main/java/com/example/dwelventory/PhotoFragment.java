package com.example.dwelventory;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    private ActivityResultLauncher<Intent> cameraFragmentResultLauncher;
    private ImageView selectedGalleryImage;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ArrayList<Uri> selectedImages;
    private ArrayAdapter<Uri> photoAdapter;
    private Uri currentUri;
    private ListView photoListView;
    private Button confirmButton;
    private onPhotoFragmentInteractionListener listener;

    /***
     * Interface listener for PhotoFragment
     * Implemented in AddEditActivity and initialized in PhotoFragment
     */
    public interface onPhotoFragmentInteractionListener{
        void onPhotoConfirmPressed(ArrayList<String> photosToAppend);
    }

    /***
     * Overrides the default onAttach method to create onPhotoFragmentInteractionListener
     * Defines result launchers for getting images from device gallery and camera
     * @param context
     *      Context to create a listener for
     * @throws
     *      RuntimeException when listener is not implemented
     */
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PhotoFragment.onPhotoFragmentInteractionListener){
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

        cameraFragmentResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK){
                        Intent camIntent = result.getData();
                        if (camIntent != null){
                            Bitmap photo = (Bitmap) camIntent.getExtras().get("data");
                            String remotePath = "images/" + UUID.randomUUID().toString();
                            StorageReference ref = storageRef.child(remotePath);
                            String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                                    photo, "camera image", null);
                            photos.add(Uri.parse(path));
                            photoAdapter.notifyDataSetChanged();
                            Log.d("CAMERA", "photo taken and working on saving");
                            Log.d("PHOTOPATH", path);
                            ref.putFile(Uri.parse(path)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(getActivity().getBaseContext(),
                                            "Camera photo uploaded!", Toast.LENGTH_SHORT).show();
                                    photoPaths.add(remotePath);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity().getBaseContext(),
                                            "Camera photo upload failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
        );


    }

    /***
     * Overloaded method that creates instance of PhotoFragment and allows for
     * existing images to be displayed
     * @param userId
     *      ID of user to access information from Firebase
     * @param images
     *      Array of images of associated item (if applicable) to display on PhotoFragment
     * @return
     *      PhotoFragment to display
     */
    static PhotoFragment newInstance(String userId, ArrayList<String> images){
        // load in the user ID to get the query path for storing and retrieving current user defined
        Bundle args = new Bundle();
        args.putString("user_id",userId);

        args.putStringArrayList("images",images);

        PhotoFragment photoFragment = new PhotoFragment();
        photoFragment.setArguments(args);
        return photoFragment;
    }

    /***
     * Overloaded method that creates instance of PhotoFragment
     * @param userId
     *      ID of user to access information from Firebase
     * @return
     *      PhotoFragment to display
     */
    static PhotoFragment newInstance(String userId){
        Bundle args = new Bundle();
        args.putString("userId", userId);

        PhotoFragment photoFrag = new PhotoFragment();
        photoFrag.setArguments(args);
        return photoFrag;
    }

    /***
     * Creates new PhotoFragment to be displayed
     * Makes UI that will allow for adding and deleting images
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return
     *      AlertDialog.Builder for the built new PhotoFragment
     */
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_photo,null);
        camera = view.findViewById(R.id.camera_button);
        gallery = view.findViewById(R.id.gallery_button);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        selectedImages = new ArrayList<>();
        photos = new ArrayList<>();
        photoListView = view.findViewById(R.id.photo_list_view);
        confirmButton = view.findViewById(R.id.confirmButton);


        photoAdapter = new PhotoCustomList(this.getContext(), photos);
        photoListView.setAdapter(photoAdapter);

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

        // deleting images
        photoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder askDelete = new AlertDialog.Builder(getActivity());
                askDelete.setMessage("Delete this image?");
                askDelete.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        photos.remove(position);
                        photoPaths.remove(position);
                        photoAdapter.notifyDataSetChanged();
                    }
                });
                askDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
                AlertDialog popup = askDelete.create();
                popup.show();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
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
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraFragmentResultLauncher.launch(intent);
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

    /***
     * Loads URIs of images from their paths in Firestore Cloud Storage and displays them in fragment
     * @param stringQueries
     *      List of paths to each image in Firestore Storage
     */
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


