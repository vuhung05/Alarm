package com.example.vuhung.video10minutes.Adapter;

import android.view.View;

public interface IClickListenerAlarmAdapter {
    void onPositionClickedItem(View v, int position);

    void onLongClickedItem(View v, int position);

    void onCheckedRadioButton(int position);
}
