package com.example.vuhung.video10minutes.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.vuhung.video10minutes.Model.FileModel;
import com.example.vuhung.video10minutes.R;

import java.util.ArrayList;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private ArrayList<FileModel> files;
    private final IClickListenerAlarmAdapter listener;
    private RadioButton lastCheckedRB = null;;
    private Context mContext;
    // Pass in the contact array into the constructor


    public AlarmAdapter(ArrayList<FileModel> files, IClickListenerAlarmAdapter listener, Context mContext) {
        this.files = files;
        this.listener = listener;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AlarmAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_alarm, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull AlarmAdapter.ViewHolder viewHolder, int i) {
        FileModel file = files.get(i);
        viewHolder.tvName.setText(file.getName());
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MY_ALARM",Context.MODE_PRIVATE);
        if(sharedPreferences.getString("path_alarm","").equals(file.getPath())){
            viewHolder.rbCheck.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {

        public TextView tvName;
        public RadioButton rbCheck;
        public ViewHolder(final View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            rbCheck = itemView.findViewById(R.id.rb_check);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            rbCheck.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onPositionClickedItem(v,getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onLongClickedItem(v,getAdapterPosition());
            return false;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (lastCheckedRB != null) {
                lastCheckedRB.setChecked(false);
            }
            lastCheckedRB = rbCheck;
            if (isChecked) {
                listener.onCheckedRadioButton(getAdapterPosition());
                Log.d("abc","check path: "+ getAdapterPosition());
            }
        }

    }
}
