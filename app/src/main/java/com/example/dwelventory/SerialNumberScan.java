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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * This is the activity that allows the user to take a picture and scan the image for
 * a serial number or can also take in the manual input of the serial number.
 *
 * @author Abhishek Kumar and Maggie Lacson
 * @see AddEditActivity
 */
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
    private int BACK_CODE = 11;
    private int SERIAL_OK = 17;
    /**
     * This sets up the blank scanning activity
     */
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
                if (checkIntConvertible(final_serial_no)){
                    Serial_no.setText(final_serial_no);
                    Toast.makeText(SerialNumberScan.this, "All numerical values detected from the text has been used to set!", Toast.LENGTH_LONG).show();
                    Intent intent_new = new Intent(SerialNumberScan.this,AddEditActivity.class);
                    String serial_as_string = Serial_no.getText().toString();
                    intent_new.putExtra("serialNo", serial_as_string);
                    setResult(SERIAL_OK, intent_new);
                    finish();
                }
            }
        });

        Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_new = new Intent(SerialNumberScan.this,AddEditActivity.class);
                String serial_as_string = Serial_no.getText().toString();
                intent_new.putExtra("serialNo", serial_as_string);
                setResult(BACK_CODE, intent_new);
                finish();
            }
        });

    }
    /**
     * This turns the scanned text to be only then number values
     *
     * @param finalSerialNo (String) the string value of the serial number to be processed
     * @return result (String) the string value of the serial number, still needs further processing
     */
    private String removeText(String finalSerialNo) {
        StringBuilder result = new StringBuilder();

        for (char c : finalSerialNo.toCharArray()) {
            if (Character.isDigit(c)) {
                result.append(c);
            }
        }
        return result.toString();
    }
    /**
     * This checks strips the string of all whitespace and newlines
     *
     * @param finalSerialNo (String) the string value of the serial integer containing whitespace
     */
    private String stripString(String finalSerialNo) {
        // Remove all whitespace and newlines from the string
        return finalSerialNo.replaceAll("\\s", "");
    }
    /**
     * This checks that the serial number can be stored in firebase as an int
     *
     * @param serial_string (String) string value of the serial integer
     * @return true or false whether or not the int is valid
     */
    private boolean checkIntConvertible(String serial_string){
        try {
            int intValue = Integer.parseInt(serial_string);
        } catch (NumberFormatException e){
            Scanned_Edit_txt.setError("Serial Number is too large");
            Scanned_Edit_txt.requestFocus();
            return false;
        }
        return true;
    }
    /**
     * This takes the picture from which the serial number is scanned
     */
    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try{
            takePictureLauncher.launch(takePictureIntent);
        } catch (ActivityNotFoundException e){
            // Handle the exception
            Toast.makeText(this, "Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This handles the result from the camera activity and sets the image view
     * to the picture that was taken.
     */
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