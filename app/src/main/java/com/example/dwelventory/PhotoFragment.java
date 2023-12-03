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
    private PhotoFragmentListener listener;
    private ImageView selectedGalleryImage;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ArrayList<Uri> selectedImages;
    private ArrayAdapter<Bitmap> photoAdapter;
    private Uri currentUri;
    private ListView photoListView;
    private Button confirmButton;

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
                                    String remotePath = "images/" + UUID.randomUUID().toString();
                                    StorageReference ref = storageRef.child(remotePath);
                                    String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),imageBitmap,"newpic",null);
                                    Log.d("PHOTOPATH", String.valueOf(Uri.parse(remotePath)));
                                    ref.putFile(Uri.parse(path))
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Toast.makeText(getActivity().getBaseContext(),"Upload successful",Toast.LENGTH_SHORT);
                                                    photoPaths.add(path);
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
                            photoAdapter.notifyDataSetChanged();
                            String remotePath = "images/" + UUID.randomUUID().toString();
                            StorageReference ref = storageRef.child(remotePath);
                            String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                                    photo, "camera image", null);
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

        if (context instanceof PhotoFragment.PhotoFragmentListener){
            listener = (PhotoFragment.PhotoFragmentListener) context;
        }else{
            throw new RuntimeException();
        }

    }

    public interface PhotoFragmentListener {
        void addPhotos(ArrayList<String> paths);
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
        confirmButton = view.findViewById(R.id.confirm_photos);

        photoPaths = new ArrayList<>();

        photoAdapter = new PhotoCustomList(this.getContext(), photos);
        photoListView.setAdapter(photoAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

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

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.addPhotos(photoPaths);
            }
        });

        return builder.create();
    }
}