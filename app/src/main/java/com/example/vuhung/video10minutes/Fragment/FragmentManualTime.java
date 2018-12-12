package com.example.vuhung.video10minutes.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.example.vuhung.video10minutes.R;

public class FragmentManualTime extends Fragment {
   static NumberPicker npMinutes, npHours;

    public FragmentManualTime() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         View view= inflater.inflate(R.layout.fragment_manual_time,container,false);
         npMinutes = view.findViewById(R.id.np_minutes);
         npHours = view.findViewById(R.id.np_hour);
         npMinutes.setMaxValue(59);
         npMinutes.setMinValue(0);
         npHours.setMaxValue(10);
         npHours.setMinValue(0);
         return view;
    }
    public int getManualTime(){
        return npHours.getValue()*60*60*1000+npMinutes.getValue()*60*1000;

    }



}
