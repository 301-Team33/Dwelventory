package com.example.dwelventory;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PhotoCustomList extends ArrayAdapter<Uri> {
    private ArrayList<Uri> photos;
    private Context context;


    public PhotoCustomList(Context context, ArrayList<Uri> photos){
        super(context,0,photos);
        this.photos = photos;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.list_photo_item,parent,false);
        }
        // SOURCE: https://stackoverflow.com/questions/42033748/unable-to-set-firebase-image-uri-in-image-view
        // AUTHOR: Rosário Pereira Fernandes
        // ANSWERED: answered Feb 3, 2017 at 22:12
        // Reasoning for Use: Did not use his code base for a tutorial, but rather his description to
        // Find out that you can't load images from firebase off the top!

        // SOURCE:
        // Tutorial Code: Loading images from the web with Glide
        // Source: https://m.youtube.com/watch?v=xMyfY02Bs_M
        // Author: Future Studio , November 3, 2017
        // Code from the YouTube Author Future Studio was adapted to fit the needs of the assignment
        // Basically the adaptation was used to store photos from the online Firebase
        if (photos.get(position).toString().startsWith("https")){
            Uri photo = photos.get(position);
            ImageView photoView = view.findViewById(R.id.indiv_image);
            Glide.with(context).load(photo).into(photoView);
        }else {
            Uri photo = photos.get(position);
            ImageView photoView = view.findViewById(R.id.indiv_image);
            photoView.setImageURI(photo);
        }
        return view;
    }
}
