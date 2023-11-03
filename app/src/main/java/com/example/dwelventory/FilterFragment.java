package com.example.dwelventory;

import android.app.Dialog;
import android.os.Bundle;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if("make".equals(filterOption)){
            View view = inflater.inflate(R.layout.popup_select_make, container, false);

            EditText makeInput = view.findViewById(R.id.filter_make_etext);
            String makeText = makeInput.getText().toString();
            filterInput = makeText.split(" ");

            Button doneButton = view.findViewById(R.id.filter_make_donebtn);

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    if (listener != null) {
                        listener.onMakeFilterApplied(filterInput);
                    }

                    dismiss();
                }
            });
            return view;
        }
        else if("date".equals(filterOption)){   // For date filter
            View view = inflater.inflate(R.layout.popup_select_drange, container, false);

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
            return view;
        }
        else if("keywords".equals(filterOption)) {
            View view = inflater.inflate(R.layout.popup_select_keywords, container, false);

            EditText keywordInput = view.findViewById(R.id.filter_kwords_etext);
            String keywordText = keywordInput.getText().toString();
            filterInput = keywordText.split(" ");

            Button doneButton = view.findViewById(R.id.filter_kwords_donebtn);

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    if (listener != null) {
                        listener.onKeywordFilterApplied(filterInput);
                    }

                    dismiss();
                }
            });
            return view;
        }
        else if("tags".equals(filterOption)) {
            View view = inflater.inflate(R.layout.popup_select_tags, container, false);

            EditText tagsInput = view.findViewById(R.id.filter_tags_etext);
            String tagsText = tagsInput.getText().toString();
            filterInput = tagsText.split(" ");

            Button doneButton = view.findViewById(R.id.filter_tags_donebtn);

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    if (listener != null) {
                        listener.onTagFilterApplied(filterInput);
                    }

                    dismiss();
                }
            });
            return view;
        }
        return null;
    }

    public void setFilterListener(FilterFragmentListener listener) {
        this.listener = listener;
    }
}
