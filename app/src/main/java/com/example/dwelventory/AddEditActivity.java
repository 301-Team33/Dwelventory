package com.example.dwelventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditActivity extends AppCompatActivity {
    // All views
    EditText nameButton;
    EditText dateButton;
    EditText makeButton;
    EditText modelButton;
    MaterialButton serialNumButton;
    EditText estValButton;
    EditText commentButton;
    MaterialButton photoButton;
    MaterialButton confirmButton;
    MaterialButton editTagButton;
    MaterialButton tagDisplay1Button;
    MaterialButton tagDisplay2Button;
    MaterialButton tagDisplay3Button;
    // Required inputs
    private String name;
    private Date date;
    private String make;
    private String model;
    private int estValue;
//    private String comment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_item_fragment);
        // set up
        editTagButton = findViewById(R.id.edit_tag_button);
        tagDisplay1Button = findViewById(R.id.tag_display_1);
        tagDisplay2Button = findViewById(R.id.tag_display_2);
        tagDisplay3Button  =findViewById(R.id.tag_display_3);

        nameButton = findViewById(R.id.item_name_button);
        dateButton = findViewById(R.id.date_button);
        makeButton = findViewById(R.id.make_button);
        modelButton = findViewById(R.id.model_button);
        serialNumButton = findViewById(R.id.serial_number_button);
        estValButton = findViewById(R.id.estimated_val_button);
        commentButton = findViewById(R.id.comment_button);
        photoButton = findViewById(R.id.photo_button);
        confirmButton = findViewById(R.id.confirm_button);

        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");

        confirmButton.setOnClickListener(v -> {
            // check for valid inputs
            if (reqInputsValid()){
                // take info and make item object
                Item item = new Item(name, date, make, model, estValue);
                // put it in intent
                Intent updatedIntent = new Intent();
                // go back to main activity
                finish();
            }
        });
    }
    /**
     * This checks all the required inputs are filled out properly
     * @return true or false whether or not inputs are valid
     */
    private boolean reqInputsValid(){
        boolean valid = true;
        // check name
        name = nameButton.getText().toString().trim();
        if (name.isEmpty()) {
            // handle empty field
            nameButton.setError("Field cannot be empty");
            nameButton.requestFocus();
            valid = false;
        }
        // check date
        String strDate = dateButton.getText().toString();
        if (strDate.isEmpty()) {
            // handle empty field
            dateButton.setError("Field cannot be empty");
            dateButton.requestFocus();
            valid = false;
        }
        if (!isDateValid(strDate)){
            // handle date format
            dateButton.setError("Date format must be (mm-dd-yyyy)");
            dateButton.requestFocus();
            valid = false;
        }
        // check make
        make = makeButton.getText().toString().trim();
        if (make.isEmpty()) {
            // handle empty field
            makeButton.setError("Field cannot be empty");
            makeButton.requestFocus();
            valid = false;
        }
        // check model
        model = modelButton.getText().toString();
        if (model.isEmpty()) {
            // handle empty field
            modelButton.setError("Field cannot be empty");
            modelButton.requestFocus();
            valid = false;
        }
        // check estimated value
        String ev = estValButton.getText().toString();
        if (ev.isEmpty()){
            estValButton.setError("Field cannot be empmty");
            estValButton.requestFocus();
            valid = false;
        }
        estValue = Integer.parseInt(ev);
        // All inputs valid!!!
        return valid;
    }

    private boolean isDateValid(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        dateFormat.setLenient(false); // Disallow lenient date parsing
        try {
            date = dateFormat.parse(dateStr);
            return true; // Date is valid
        } catch (ParseException e) {
            return false; // Date is invalid
        }
    }

}

//        if (!isDateValid(date)) {
//        //
//        Toast.makeText(getApplicationContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
//        }
//
//        String ev = estValButton.getText().toString();
//        estValue = Integer.parseInt(ev);