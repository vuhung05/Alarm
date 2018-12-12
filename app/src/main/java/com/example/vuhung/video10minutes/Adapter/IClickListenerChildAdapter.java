package com.example.vuhung.video10minutes.Adapter;

import android.view.View;

import com.example.vuhung.video10minutes.Model.Child;

public interface IClickListenerChildAdapter {
    void onPositionClickedItem(View v, int position);

    void onLongClickedItem(View v, int position);

    void onSwitchItem(int position, boolean isChecked);

    void gotoAddChild();
    void gotoUpdateChild(Child child);
    }


