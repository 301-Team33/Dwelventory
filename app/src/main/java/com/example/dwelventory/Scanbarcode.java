package com.example.dwelventory;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.util.List;
/**
 * This is the activity that allows the user to take a picture and scan the image for
 * a barcode or can also take in the manual input of the barcode number. The activity
 * searches the database and finds an associated item name if the barcode exists.
 *
 * @author Abhishek Kumar and Maggie Lacson
 * @see AddEditActivity
 * @see FirebaseFirestore
 */
public class Scanbarcode extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference barcodes;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button Snapbtn;
    private Button Scanbtn;
    private Button Usebtn;
    private FloatingActionButton Backbtn;
    private ImageView imageView;
    private EditText extracted_barcode_title;
    private TextView barcode_description;
    private Uri imageUri;
    private String detected_text;
    private int BARCODE_OK = 25;
    private int BACK_CODE = 11;

    private static final int REQUEST_CAMERA_PERMISSION = 123;
    /**
     * This sets up the blank scanning activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanning);

        imageView = findViewById(R.id.captured_img);
        Snapbtn = findViewById(R.id.snap_photo_btn);
//        Scanbtn = findViewById(R.id.scan_txt_btn);
        Usebtn = findViewById(R.id.use_serial_no);
        Backbtn = findViewById(R.id.back_btn);
        barcode_description = findViewById(R.id.barcode_description);
        extracted_barcode_title = findViewById(R.id.scanned_edit_txt);

        /*BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        imagebitmap = drawable.getBitmap();*/

        Snapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCameraPermission();
            }
        });

        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_UPC_A,
                                Barcode.FORMAT_UPC_E,
                                Barcode.FORMAT_EAN_13,
                                Barcode.FORMAT_EAN_8,
                                Barcode.FORMAT_CODE_39,
                                Barcode.FORMAT_CODE_128,
                                Barcode.FORMAT_QR_CODE,
                                Barcode.FORMAT_AZTEC,
                                Barcode.FORMAT_DATA_MATRIX,
                                Barcode.FORMAT_PDF417)
                        .enableAllPotentialBarcodes()
                        .build();


        Usebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set your desired TextView with the detected text
                barcode_description.setText(detected_text);
                Toast.makeText(Scanbarcode.this, "Description has been set!", Toast.LENGTH_SHORT).show();
                Intent intent_new = new Intent(Scanbarcode.this,AddEditActivity.class);
                setResult(BARCODE_OK, intent_new);
                finish();
            }
        });

        Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_new = new Intent(Scanbarcode.this,AddEditActivity.class);
                setResult(BACK_CODE, intent_new);
                finish();
            }
        });
    }
    /**
     * This notifies the user and requests for camera permission
     */
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            dispatchTakePictureIntent();
        }
    }
    /**
     * This handles the camera permissions of the user. Takes a picture if allowed, otherwise
     * notifies the user they do not have access
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * This launches the camera activity
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        takePictureLauncher.launch(takePictureIntent);
    }
    /**
     * This handles the result from the camera activity
     */
    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Process the captured image Uri
                    try {
                        processCapturedImage();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
    );

    private void processCapturedImage() throws IOException {
        if (imageUri != null) {
            InputImage image = InputImage.fromFilePath(this, imageUri);
            imageView.setImageURI(imageUri);

            BarcodeScanner scanner = BarcodeScanning.getClient();
            Task<List<Barcode>> result = scanner.process(image)
                    .addOnSuccessListener(barcodes -> {
                        Log.d("BARCODES", "onSuccess: " + barcodes.toString());
                        for (Barcode barcode : barcodes) {
                            String barcodeData = barcode.getRawValue();
                            setName(barcodeData);

                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Scanbarcode.this, "Detection Failed! : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(Scanbarcode.this, "No image captured", Toast.LENGTH_SHORT).show();
        }
    }

    private void setName(String barcodeData) {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("barcodes").document(barcodeData); // Replace with actual ID


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve data from the document
                        String itemName = document.getString("item name");
                        Log.d("item name",itemName);
                        detected_text = itemName;
                        extracted_barcode_title.setText(detected_text);
                        Toast.makeText(Scanbarcode.this, "Description Found!", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(Scanbarcode.this, "The Barcode you gave does not exist in our database! \n Sorry about that!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("FirestoreError", "Error retrieving information, try snapping another photo!", task.getException());
                    Toast.makeText(Scanbarcode.this, "Error retrieving information, try snapping another photo!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
