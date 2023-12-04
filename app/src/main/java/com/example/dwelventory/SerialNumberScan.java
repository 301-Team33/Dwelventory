package com.example.dwelventory;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SerialNumberScan extends AppCompatActivity {
    // ... (other variables)
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button Snapbtn;
    private Button Scanbtn;
    private Button Usebtn;
    private com.google.android.material.floatingactionbutton.FloatingActionButton Backbtn;
    private TextView Serial_no;
    private EditText Scanned_Edit_txt;
    private ImageView imageView;
    private Bitmap imagebitmap;

    private String detected_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_number_scan);

        imageView = findViewById(R.id.captured_img);
        Snapbtn = findViewById(R.id.snap_photo_btn);
        Scanbtn = findViewById(R.id.scan_txt_btn);
        Serial_no = findViewById(R.id.serialNumber);
        Usebtn = findViewById(R.id.use_serial_no);
        Scanned_Edit_txt = findViewById(R.id.scanned_edit_txt);
        Backbtn = findViewById(R.id.back_btn);

        Snapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        Scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                int rotationdegree = 0;
                InputImage image = InputImage.fromBitmap(imagebitmap,rotationdegree);
                Task<Text> result =
                        recognizer.process(image)
                                .addOnSuccessListener(new OnSuccessListener<Text>() {
                                    @Override
                                    public void onSuccess(Text visionText) {
                                        // Task completed successfully
                                        // ...
                                        StringBuilder recognizedText = new StringBuilder();

                                        // Process each block of text
                                        List<Text.TextBlock> blocks = visionText.getTextBlocks();
                                        for (Text.TextBlock block : blocks) {
                                            List<Text.Line> lines = block.getLines();

                                            // Process each line of text
                                            for (Text.Line line : lines) {
                                                List<Text.Element> elements = line.getElements();

                                                // Process each element of text (word/character)
                                                for (Text.Element element : elements) {
                                                    // Append recognized text to StringBuilder
                                                    recognizedText.append(element.getText()).append(" ");
                                                }

                                                recognizedText.append("\n"); // Append new line for each line of text
                                            }
                                        }

                                        // Now you have the complete recognized text
                                        detected_text = recognizedText.toString();
                                        // Use the recognized text as needed (e.g., set it to a TextView)
                                        //Serial_no.setText(finalRecognizedText);
                                        Toast.makeText(SerialNumberScan.this, "Scanned!", Toast.LENGTH_SHORT).show();
                                        Scanned_Edit_txt.setText(detected_text);


                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                                Log.d("Failed Scan","Failed Scan: "+e.getMessage());
                                                Toast.makeText(SerialNumberScan.this, "Failed Scan: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

            }
        });
        
        Usebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String final_serial_no = Scanned_Edit_txt.getText().toString();
                final_serial_no = stripString(final_serial_no);
                final_serial_no = removeText(final_serial_no);
                Serial_no.setText(final_serial_no);
                Toast.makeText(SerialNumberScan.this, "All numerical values detected from the text has been used to set!", Toast.LENGTH_LONG).show();
            }
        });

        Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_prev = getIntent();
                Intent intent_new = new Intent(SerialNumberScan.this,AddEditActivity.class);
                Log.d("ScanTag EDIT MODE", "back btn clicked");



                if  (intent_prev.getStringExtra("mode").equals("edit")){
                    Log.d("ScanTag EDIT MODE", "MADE IT IN the back button!! intent prev mode: "+intent_prev.getStringExtra("mode"));
                    Item item = intent_prev.getParcelableExtra("item");
                    Log.d("ScanTag EDIT MODE", "0");
                    String serial_as_string = Serial_no.getText().toString();
                    Log.d("ScanTag EDIT MODE", "0.1 " + serial_as_string);
                    item.setSerialNumber(new BigInteger(serial_as_string));
//                    int serial_as_int = -1;
//                    try {
//                        serial_as_int = Integer.parseInt(serial_as_string);
//                        Log.d("ScanTag EDIT MODE", "0.2 " + serial_as_int);
//                        item.setSerialNumber(serial_as_int);
//                        Log.d("ScanTag EDIT MODE", "0.2.1: item.getserial is " + item.getSerialNumber());
//
//
//                    } catch (Exception e){
//                        Log.d("ScanTag EDIT MODE", "fucking failed bc of " + e);
//                    }

                    Date date = (Date) intent_prev.getSerializableExtra("date");
                    Log.d("Date test",date.toString());
                    Log.d("ScanTag EDIT MODE", "1");
                    String mode = intent_prev.getStringExtra("mode");
                    int position = intent_prev.getIntExtra("position",-1);
                    int request_code = intent_prev.getIntExtra("requestCode",-1);
                    String itemRefID = intent_prev.getStringExtra("itemRefID");
                    ArrayList<Tag> tagstoApply = intent_prev.getParcelableArrayListExtra("tags");
                    String prev_name = intent_prev.getStringExtra("previous name");
                    Log.d("ScanTag EDIT MODE", "2");
                    intent_new.putExtra("item",item);
                    intent_new.putExtra("date",date);
                    intent_new.putExtra("mode",mode);
                    intent_new.putExtra("position",position);
                    intent_new.putExtra("requestCode",request_code);
                    intent_new.putExtra("itemRefID",itemRefID);
                    intent_new.putExtra("serialNo", serial_as_string);
                    
                    Log.d("ScanTag EDIT MODE", "3 " + serial_as_string);
                    //intent_new.putExtra("tags",tagstoApply);
                    intent_new.putExtra("previous name",prev_name);
//                    startActivity(intent_new); THIS WAS ABHIS
                    Log.d("ScanTag EDIT MODE", "setting the result");
                    setResult(17, intent_new);
                    Log.d("ScanTag EDIT MODE", "finishing scanActivity...");
                    finish();
                    Log.d("ScanTag EDIT MODE", "right after finish");



                }
                else if(intent_prev.getStringExtra("mode").equals("add")){
                    //To be completed
                }
                Log.d("ScanTag EDIT MODE", "passed through the if statements");

            }
        });

    }

    private String removeText(String finalSerialNo) {
        StringBuilder result = new StringBuilder();

        for (char c : finalSerialNo.toCharArray()) {
            if (Character.isDigit(c)) {
                result.append(c);
            }
        }

        return result.toString();
    }

    private String stripString(String finalSerialNo) {
        // Remove all whitespace and newlines from the string
        return finalSerialNo.replaceAll("\\s", "");
    }


    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try{
            takePictureLauncher.launch(takePictureIntent);
        } catch (ActivityNotFoundException e){
            // Handle the exception
            Toast.makeText(this, "Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            imagebitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imagebitmap);
        }
    }
    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    Bundle extras = data.getExtras();
                    imagebitmap = (Bitmap) extras.get("data");
                    imageView.setImageBitmap(imagebitmap);
                }
            }
    );
}