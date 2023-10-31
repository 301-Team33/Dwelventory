package com.example.dwelventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;

import java.util.Date;

public class AddEditActivity extends AppCompatActivity {

    EditText nameButton;
    EditText dateButton;
    MaterialButton makeButton;
    MaterialButton modelButton;
    MaterialButton serialNumButton;
    EditText estValButton;
    MaterialButton commentButton;
    MaterialButton photoButton;
    MaterialButton confirmButton;
    MaterialButton editTagButton;
    MaterialButton tagDisplay1Button;
    MaterialButton tagDisplay2Button;
    MaterialButton tagDisplay3Button;

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
            // take info and make item object
            String itemName = nameButton.getText().toString();
            String estValue = estValButton.getText().toString(

            );
            int itemEstValue = Integer.parseInt(estValue);

//            Item item = new Item(itemName, itemDate, "Pygmy Goat", "Caramel w/ Black Markings", itemEstValue);

            // put it in intent
            Intent addIntent = new Intent();
            addIntent.putExtra("test", "i love you king julian");
            // go back to main activity
            finish();
        });



    }
}