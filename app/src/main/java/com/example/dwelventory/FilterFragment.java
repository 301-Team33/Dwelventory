package com.example.dwelventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FilterFragment extends DialogFragment {
    private String[] filterInput;
    private String filterOption;
    private FilterFragmentListener listener;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof FilterFragmentListener){
            listener = (FilterFragmentListener) context;
        }
        else{
            throw new RuntimeException(context.toString()
                    + "must implement OnFragmentInteractionListener");
        }
    }

    public interface FilterFragmentListener {
        void onMakeFilterApplied(String[] filterInput);
        void onDateFilterApplied(Date start, Date end);
        void onKeywordFilterApplied(String[] keywords);
        void onTagFilterApplied(String[] tags);
    }
    public static FilterFragment newInstance(String filterOption) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putString("filter_option", filterOption);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            filterOption = getArguments().getString("filter_option");
        }
        else {
            // Handle case where no arguments were set
            throw new IllegalStateException("No filter option provided to FilterFragment");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if("Make".equals(filterOption)){
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_select_make, null);

            EditText makeInput = view.findViewById(R.id.filter_make_etext);
            Button doneButton = view.findViewById(R.id.filter_make_donebtn);

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    String makeText = makeInput.getText().toString();
                    filterInput = makeText.split("\\s+");
                    if (listener != null) {
                        listener.onMakeFilterApplied(filterInput);
                    }

                    dismiss();
                }
            });
            builder.setView(view);
            return builder.create();
        }
        else if("Date".equals(filterOption)){   // For date filter
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_select_drange, null);

            EditText dateStart = view.findViewById(R.id.filter_date_cal1);
            String dateStartText = dateStart.getText().toString();

            EditText dateEnd = view.findViewById(R.id.filter_date_cal2);
            String dateEndText = dateEnd.getText().toString();

            Button doneButton = view.findViewById(R.id.filter_date_donebtn);

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    if (listener != null) {
                        Date start;
                        Date end;
                        try {
                            start =new SimpleDateFormat("dd/MM/yyyy").parse(dateStartText);
                            end =new SimpleDateFormat("dd/MM/yyyy").parse(dateEndText);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                        listener.onDateFilterApplied(start, end);
                    }

                    dismiss();
                }
            });
            builder.setView(view);
            return builder.create();
        }
        else if("Description Words".equals(filterOption)) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_select_keywords, null);

            EditText keywordInput = view.findViewById(R.id.filter_kwords_etext);


            Button doneButton = view.findViewById(R.id.filter_kwords_donebtn);

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    String keywordText = keywordInput.getText().toString();
                    filterInput = keywordText.split("\\s+");
                    if (listener != null) {
                        listener.onKeywordFilterApplied(filterInput);
                    }

                    dismiss();
                }
            });
            builder.setView(view);
            return builder.create();
        }
        else if("Tags".equals(filterOption)) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_select_tags,null);

            EditText tagsInput = view.findViewById(R.id.filter_tags_etext);


            Button doneButton = view.findViewById(R.id.filter_tags_donebtn);

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    String tagsText = tagsInput.getText().toString();
                    filterInput = tagsText.split("\\s+");
                    if (listener != null) {
                        listener.onTagFilterApplied(filterInput);
                    }

                    dismiss();
                }
            });
            builder.setView(view);
            return builder.create();
        }
        else {
            // Use some default dialog or log an error
            builder.setTitle("Error")
                    .setMessage("No filter option was provided or an unknown filter option was used.")
                    .setPositiveButton("OK", null);
            return builder.create();
        }
    }

    public void setFilterListener(FilterFragmentListener listener) {
        this.listener = listener;
    }
}
