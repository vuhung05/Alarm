package com.example.vuhung.video10minutes.Adapter;

import com.example.vuhung.video10minutes.Model.Route;

public interface IClickListenerRouteAdapter {
    void gotoUpdateRoute(Route route);
    void itemLongClick(int position);
    void gotoTimeRemaining(int iD,long timeCurrent);
}
