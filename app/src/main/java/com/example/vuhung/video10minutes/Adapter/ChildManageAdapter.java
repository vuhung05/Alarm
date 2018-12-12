package com.example.vuhung.video10minutes.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vuhung.video10minutes.Model.Child;
import com.example.vuhung.video10minutes.R;

import java.io.File;
import java.util.ArrayList;

public class ChildManageAdapter extends RecyclerView.Adapter<ChildManageAdapter.ItemViewHolder>{
    private ArrayList<Child> allChildren;
    private final IClickListenerChildManageAdapter listener;
    private Context mContext;

    public void deleteChild(int position){
        allChildren.remove(position);
        Log.d("position1", String.valueOf(position));

    }
    public ChildManageAdapter(ArrayList<Child> allChildren, IClickListenerChildManageAdapter listener, Context mContext) {
        this.allChildren = allChildren;
        this.listener = listener;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child_manage, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view,viewType);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Child child = allChildren.get(position);
        holder.tvChildName.setText(child.getName());
        if (child.getPhoto().equals("")){
            holder.imgChild.setImageResource(R.drawable.user);
        }else {
            File file = new File(child.getPhoto());
            if (file.exists()) {
                Bitmap bitmap = ThumbnailUtils.extractThumbnail(
                        BitmapFactory.decodeFile(file.getAbsolutePath()),
                        200,
                        200);
                holder.imgChild.setImageBitmap(bitmap);
            }else holder.imgChild.setImageResource(R.drawable.user);
        }
    }

    @Override
    public int getItemCount() {
        return allChildren.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        de.hdodenhof.circleimageview.CircleImageView imgChild;
        TextView tvChildName, tvDelete;

        public ItemViewHolder(final View itemView, int viewType) {
            super(itemView);
            imgChild =(de.hdodenhof.circleimageview.CircleImageView)itemView.findViewById(R.id.img_child_item_manage);
            tvChildName = itemView.findViewById(R.id.tv_child_name_item_manage);
            tvDelete = itemView.findViewById(R.id.tv_delete_child_item);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.gotoChild(getAdapterPosition());
                }
            });
            tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.deleteChild(getAdapterPosition());
                }
            });
        }
    }
}
