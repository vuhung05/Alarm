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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.vuhung.video10minutes.Model.Child;
import com.example.vuhung.video10minutes.R;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ItemViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private ArrayList<Child> allChildren;
    private ArrayList<Child> children;
    private  final IClickListenerChildAdapter listener;
    private Context mContext;

    public ChildAdapter(ArrayList<Child> allChildren,ArrayList<Child> children, IClickListenerChildAdapter listener, Context mContext) {
        this.allChildren = allChildren;
        this.children = children;
        this.listener = listener;
        this.mContext = mContext;
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= allChildren.size()) {
            // This is where we'll add footer.
            return TYPE_FOOTER;
        }else return TYPE_ITEM;

    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        ItemViewHolder itemViewHolder;
        if (viewType == TYPE_ITEM){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child, parent, false);
            itemViewHolder = new ItemViewHolder(view,viewType);
            return itemViewHolder;
        }else  if (viewType == TYPE_FOOTER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_add_child, parent, false);

            itemViewHolder = new ItemViewHolder(view, viewType);
            return itemViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        if (holder.view_type == TYPE_ITEM) {
            Child child = allChildren.get(position);
            holder.tvChildName.setText(child.getName());

            if (child.getPhoto().equals("")){
                holder.imgChild.setImageResource(R.drawable.user);
            }else {
                File file = new File(child.getPhoto());
                if (file.exists()) {
                    Bitmap bitmap = ThumbnailUtils.extractThumbnail(
                            BitmapFactory.decodeFile(file.getAbsolutePath()),
                            150,
                            150);
                    holder.imgChild.setImageBitmap(bitmap);
                }else holder.imgChild.setImageResource(R.drawable.user);
            }
            int i=0;
            while (i<children.size()){
                if (children.get(i).getId()==child.getId()){
                    holder.swCheckItem.setChecked(true);
                    i=children.size();
                }else holder.swCheckItem.setChecked(false);
                i++;
            }
        }else if ((holder.view_type == TYPE_HEADER)){
            holder.tvFooterAddChild.setText("Add child");
        }
    }


    @Override
    public int getItemCount() {
        return allChildren.size()+1;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton
            .OnCheckedChangeListener{
        int view_type;
        CircleImageView imgChild;
         TextView tvChildName;
         Switch swCheckItem;

        ImageView imgFooterAddChild;
        TextView tvFooterAddChild;
        public ItemViewHolder(final View itemView, int viewType) {
            super(itemView);
            if (viewType == TYPE_ITEM)
            {
                imgChild = itemView.findViewById(R.id.imgChildItem);
                tvChildName = itemView.findViewById(R.id.tv_child_name_item);
                swCheckItem = itemView.findViewById(R.id.sw_check_item);
                view_type = 1;
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.gotoUpdateChild(allChildren.get(getAdapterPosition()));
                        Log.d("abcde", String.valueOf(getAdapterPosition()));
                    }
                });
                swCheckItem.setOnCheckedChangeListener(this);

            }else if (viewType ==TYPE_FOOTER ){
                imgFooterAddChild = itemView.findViewById(R.id.img_footer_add_child);
                tvFooterAddChild = itemView.findViewById(R.id.tv_footer_add_child);
                view_type = 2;
                imgFooterAddChild.setOnClickListener(this);
                tvFooterAddChild.setOnClickListener(this);
            }
        }
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.img_footer_add_child || v.getId()==R.id.tv_footer_add_child){
                listener.gotoAddChild();
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            listener.onSwitchItem(getAdapterPosition(),isChecked);
        }
    }
}
