package com.example.dwelventory;

import androidx.appcompat.app.AppCompatActivity;
//import static com.google.firebase.firestore.FirebaseFirestore.getInstance;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
/*import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import com.google.firebase.ml.vision.FirebaseVision;

import java.util.List;
import com.google.firebase.ml.vision.FirebaseVisionText;
import com.google.firebase.ml.vision.FirebaseVisionTextRecognizer;*/

import android.os.Bundle;

public class SerialNumberScan extends AppCompatActivity {
    // ... (other variables)
    private Button Snapbtn;
    private Button Scanbtn;
    private Button Usebtn;
    private TextView Serial_no;
    private ImageView Camera;

    private Bitmap bitmap;

    private String detected_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_number_scan);

        Camera = findViewById(R.id.captured_img);
        Snapbtn = findViewById(R.id.snap_photo_btn);

        //Request Camera Permissions
        if(ContextCompat.checkSelfPermission(SerialNumberScan.this,Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SerialNumberScan.this,new String[]{
                    Manifest.permission.CAMERA
            },100);
        }

        Snapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);

            }
        });

        /*Scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectText();
            }
        });*/

        /*Usebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Serial_no.setText(detected_text);
            }
        });*/

    }
    @Override
    protected void onActivityResult(int requestcode, int resultcode, @Nullable Intent data){
        super.onActivityResult(requestcode, resultcode, data);
        if(requestcode == 100){
            bitmap = (Bitmap) data.getExtras().get("data");
            Camera.setImageBitmap(bitmap);
        }
    }

   /* private String detectText(){
        if(bitmap == null){
            Toast.makeText(this, "Snap a photo first!", Toast.LENGTH_SHORT).show();
        }else{
            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
            firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    displayTextfromImage(firebaseVisionText, detected_text);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SerialNumberScanActivity.this, "Detection Failed!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        return detected_text;
    }

    private void displayTextfromImage(FirebaseVisionText firebaseVisionText, String detected_text) {
        List<FirebaseVisionText.Block> blockList = firebaseVisionText.getBlocks();
        if(blockList.size() == 0){
            Toast.makeText(this, "No Text found in image", Toast.LENGTH_SHORT).show();
        }else{
            for(FirebaseVisionText.Block block : firebaseVisionText.getBlocks()){
                detected_text = block.getText();
            }
        }
    }*/
}