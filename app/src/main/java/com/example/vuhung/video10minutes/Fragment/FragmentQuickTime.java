package com.example.vuhung.video10minutes.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.vuhung.video10minutes.R;

public class FragmentQuickTime extends Fragment implements View.OnClickListener {
    LinearLayout tv5, tv10, tv15, tv20, tv25, tv45, tv95;
    static int  quickTime;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quick_times,container,false);
        tv5 = view.findViewById(R.id.tv_quick_time_5);
        tv10 = view.findViewById(R.id.tv_quick_time_10);
        tv20 = view.findViewById(R.id.tv_quick_time_20);
        tv15 = view.findViewById(R.id.tv_quick_time_15);
        tv25 = view.findViewById(R.id.tv_quick_time_25);
        tv45 = view.findViewById(R.id.tv_quick_time_45);
        tv95 = view.findViewById(R.id.tv_quick_time_95);
        tv5.setOnClickListener(this);
        tv10.setOnClickListener(this);
        tv15.setOnClickListener(this);
        tv20.setOnClickListener(this);
        tv25.setOnClickListener(this);
        tv45.setOnClickListener(this);
        tv95.setOnClickListener(this);
        resetLayout();
        return view;
    }
    private void resetLayout(){
        tv5.setBackgroundColor(getResources().getColor(R.color.white));
        tv10.setBackgroundColor(getResources().getColor(R.color.white));
        tv15.setBackgroundColor(getResources().getColor(R.color.white));
        tv20.setBackgroundColor(getResources().getColor(R.color.white));
        tv25.setBackgroundColor(getResources().getColor(R.color.white));
        tv45.setBackgroundColor(getResources().getColor(R.color.white));
        tv95.setBackgroundColor(getResources().getColor(R.color.white));
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_quick_time_5:
                resetLayout();
                quickTime = 5*60*1000;
                tv5.setBackgroundResource(R.drawable.choice_quick_time);
                break;
            case R.id.tv_quick_time_10:
                resetLayout();
                quickTime = 10*60*1000;
                tv10.setBackgroundResource(R.drawable.choice_quick_time);
                break;
            case R.id.tv_quick_time_15:
                resetLayout();
                quickTime = 15*60*1000;
                tv15.setBackgroundResource(R.drawable.choice_quick_time);
                break;
            case R.id.tv_quick_time_20:
                resetLayout();
                quickTime = 20*60*1000;
                tv20.setBackgroundResource(R.drawable.choice_quick_time);
                break;
            case R.id.tv_quick_time_25:
                resetLayout();
                quickTime = 25*60*1000;
                tv25.setBackgroundResource(R.drawable.choice_quick_time);
                break;
            case R.id.tv_quick_time_45:
                resetLayout();
                quickTime = 45*60*1000;
                tv45.setBackgroundResource(R.drawable.choice_quick_time);
                break;
            case R.id.tv_quick_time_95:
                resetLayout();
                quickTime = 95*60*1000;
                tv95.setBackgroundResource(R.drawable.choice_quick_time);
                break;
        }
    }
    public int getQuickTime(){
        return quickTime;
    }
}
