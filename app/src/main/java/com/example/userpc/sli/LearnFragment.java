package com.example.userpc.sli;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import javax.security.auth.callback.Callback;

public class LearnFragment extends Fragment {
    private  Callback callback;
    public LearnFragment() {
        // Required empty public constructor
    }

 public interface Callback{
        void FillWithNumbers();
        void FillWithAlphabets();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try{
            callback = (Callback)getActivity();
        }
        catch (ClassCastException cce){
            throw new ClassCastException(getActivity().toString()+"must Implement methods");
        }
        final View view = inflater.inflate(R.layout.fragment_learn, container, false);
        Button Numbers = (Button) view.findViewById(R.id.NumberBtn);
        Numbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.FillWithNumbers();
            }
        });
        Button Alphabets = (Button)view.findViewById(R.id.AlphabetButton);
        Alphabets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.FillWithAlphabets();
            }
        });
        return view;
    }

}
