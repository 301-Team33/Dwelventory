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

import java.util.List;

public class FilterFragment extends DialogFragment {
    private String[] filterInput;
    private String filterOption;
    private FilterFragmentListener listener;

    public interface FilterFragmentListener {
        void onMakeFilterApplied(String[] filterInput);
    }
    public static FilterFragment newInstance(String filterOption) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putString("filter_option", filterOption);
        fragment.setArguments(args);
        return fragment;
    }
    public FilterFragment() { }

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
            View view = inflater.inflate(R.layout.)
        }
        return null;
    }

    public void setFilterListener(FilterFragmentListener listener) {
        this.listener = listener;
    }
}
