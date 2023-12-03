package com.example.dwelventory;

import static android.app.Activity.RESULT_OK;

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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.UUID;

public class PhotoFragment extends DialogFragment {
    private FloatingActionButton camera;
    private FloatingActionButton gallery;
    private ArrayList<Bitmap> photos;
    private ArrayList<String> photoPaths;
    private ImageView imageView;
    private String userId;
    private ActivityResultLauncher<Intent> photoFragmentResultLauncher;
    private ActivityResultLauncher<Intent> cameraFragmentResultLauncher;
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

                                    // Save the photo to the specified firestore.
                                    StorageReference ref = storageRef.child("images/" + UUID.randomUUID().toString());
                                    String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),imageBitmap,"gallery image",null);
                                    //photoPaths.add(path);
                                    ref.putFile(Uri.parse(path))
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
                                    Log.d("exception handled...", "onAttach: Exception");
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
                            photos.add(photo);
                            StorageReference ref = storageRef.child("images/" +
                                    UUID.randomUUID().toString());
                            String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                                    photo, "camera image", null);
                            //photoPaths.add(path);
                            Log.d("CAMERA", "photo taken and working on saving");
                            ref.putFile(Uri.parse(path)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(getActivity().getBaseContext(),
                                            "Camera photo uploaded!", Toast.LENGTH_SHORT).show();
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

    public interface PhotoFragmentListener {
        void addPhoto(String path);
        // functions executed when actions are taken on fragment in AddEditActivity
    }

    static PhotoFragment newInstance(){
        PhotoFragment photoFrag = new PhotoFragment();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

        /*photos = bundle.getParcelableArrayList("images");
        if (photos.size() != 0 && photos != null){
            imageView.setImageBitmap(photos.get(0));
        }*/

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start camera activity
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraFragmentResultLauncher.launch(intent);
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