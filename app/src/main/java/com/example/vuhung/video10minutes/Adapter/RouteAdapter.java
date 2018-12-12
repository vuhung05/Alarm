package com.example.vuhung.video10minutes.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vuhung.video10minutes.Database.DBChild;
import com.example.vuhung.video10minutes.Database.DBRoutes;
import com.example.vuhung.video10minutes.Model.Child;
import com.example.vuhung.video10minutes.Model.Route;
import com.example.vuhung.video10minutes.R;

import java.util.ArrayList;

import static com.example.vuhung.video10minutes.NewRouteActivity.iDRunRoute;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ItemViewHolder> {
    private ArrayList<Route> routesList;
    private Context mContext;
    IClickListenerRouteAdapter listener;
    boolean isRunAlarm = false;

    public void updateTimeCurrent(int iD,long time){
        isRunAlarm = true;
        int i=0;
        while (i<routesList.size()){
            if (routesList.get(i).getId()==iD){
                routesList.get(i).setTimeCurrent(time);
                i=routesList.size();
                notifyDataSetChanged();
            }
            i++;
        }
    }
    public RouteAdapter(ArrayList<Route> routesList,IClickListenerRouteAdapter listener, Context mContext) {
        this.routesList = routesList;
        this.listener = listener;
        this.mContext = mContext;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
       public ImageView imgIcon, imgStatus, imgClock;
       public TextView tvRouteName, tvListChildren, tvTime;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvListChildren = itemView.findViewById(R.id.tv_list_chidren_name);
            tvRouteName = itemView.findViewById(R.id.tv_route_name_item);
            tvTime = itemView.findViewById(R.id.tv_time_item);
           // imgIcon = itemView.findViewById(R.id.img_route_icon);
            imgStatus = itemView.findViewById(R.id.img_route_status);
            imgClock = itemView.findViewById(R.id.img_clock);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.gotoUpdateRoute(routesList.get(getAdapterPosition()));
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.itemLongClick(getAdapterPosition());
                    return  true;
                }
            });
            imgClock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.gotoTimeRemaining(routesList.get(getAdapterPosition()).getId(),routesList.get(getAdapterPosition()).getTimeCurrent());
                }
            });
            tvTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.gotoTimeRemaining(routesList.get(getAdapterPosition()).getId(),routesList.get(getAdapterPosition()).getTimeCurrent());
                }
            });
        }
    }

    @NonNull
    @Override
    public RouteAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(v);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RouteAdapter.ItemViewHolder holder, int position) {
        DBChild dbChild = new DBChild(mContext);
        DBRoutes dbRoutes = new DBRoutes(mContext);
        ArrayList<Child> allChildren = new ArrayList<Child>();
        ArrayList<Child> childrenSelectUpdate = new ArrayList<Child>();
        allChildren =dbChild.getAllChild();

        ArrayList<Child> childrenSelect = new ArrayList<Child>();
        Route rt = routesList.get(position);
        childrenSelect = rt.getListChildren();
        for (int j =0;j<allChildren.size();j++) {
            for (int i = 0; i < childrenSelect.size(); i++) {
                if (childrenSelect.get(i).getId()==allChildren.get(j).getId()){
                    childrenSelectUpdate.add(allChildren.get(j));
                    Log.d("abcde","children select " + String.valueOf(childrenSelectUpdate.size()));
                }
            }
        }
        dbRoutes.update(rt.getId(),new Route(rt.getName(),childrenSelectUpdate,rt.getIcon(),rt.getTimeCurrent(),rt.getTime()));
        Route route = routesList.get(position);
        holder.tvRouteName.setText(route.getName());
        long time = route.getTimeCurrent();
        long h = time / 3600000;
        long m = time % 3600000 / 60000;
        long s = time % 60000 / 1000;
        String hms = String.format("%02d:%02d:%02d", h, m, s);
        holder.tvTime.setText(hms);
        String str="";
        if (route.getListChildren().size()>0){
            String st = "";
            for(int i=0;i<route.getListChildren().size();i++){
                st = st +route.getListChildren().get(i).getName() +", ";
            }
            str = st.substring(0,st.length()-2);
        }
        holder.tvListChildren.setText(str);
        if (route.getId()==iDRunRoute) {
            holder.imgStatus.setImageResource(R.drawable.status_green);
            holder.tvTime.setTextColor(mContext.getResources().getColor(R.color.green));
        }
        else{
            holder.imgStatus.setImageResource(R.drawable.status_orange);
            holder.tvTime.setTextColor(mContext.getResources().getColor(R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return routesList.size();
    }

}
