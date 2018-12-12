package com.example.vuhung.video10minutes.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vuhung.video10minutes.Model.Child;
import com.example.vuhung.video10minutes.R;

import java.io.File;
import java.util.ArrayList;

public class AvatarChildAdapter extends RecyclerView.Adapter<AvatarChildAdapter.AvatarViewHolder> {
    private ArrayList<Child> children = new ArrayList<Child>();
    private IClickListenerAvatarChildAdapter listener;
    private Context mContext;

    public AvatarChildAdapter(ArrayList<Child> children, IClickListenerAvatarChildAdapter listener, Context mContext) {
        this.children = children;
        this.listener = listener;
        this.mContext = mContext;
    }

    public class AvatarViewHolder extends RecyclerView.ViewHolder{
        ImageView imgAvatar;
        TextView tvName;
        public AvatarViewHolder(View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_avatar_child_time_remain);
            tvName = itemView.findViewById(R.id.tv_child_name_time_remain);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.ClickAvatarChild(children.get(getAdapterPosition()).getId());
                }
            });
        }
    }

    @NonNull
    @Override
    public AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        view = inflater.inflate(R.layout.item_avatar_child,parent,false);
        AvatarViewHolder avatarViewHolder =  new AvatarViewHolder(view);
        return avatarViewHolder;
    }

    @NonNull

    @Override
    public void onBindViewHolder(@NonNull AvatarViewHolder holder, int position) {

        Child child =  children.get(position);
        if (child.getPhoto().equals("")){
            holder.imgAvatar.setImageResource(R.drawable.user);
        }else {
            File file = new File(child.getPhoto());
            if (file.exists()) {
                Bitmap bitmap = ThumbnailUtils.extractThumbnail(
                        BitmapFactory.decodeFile(file.getAbsolutePath()),
                        150,
                        150);
                holder.imgAvatar.setImageBitmap(bitmap);
            }else holder.imgAvatar.setImageResource(R.drawable.user);
        }

        holder.tvName.setText(child.getName());
    }

    @Override
    public int getItemCount() {
        return children.size();
    }
}
