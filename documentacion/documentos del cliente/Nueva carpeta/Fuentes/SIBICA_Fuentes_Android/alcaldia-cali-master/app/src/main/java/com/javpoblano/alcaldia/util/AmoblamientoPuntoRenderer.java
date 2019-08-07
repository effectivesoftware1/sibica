package com.javpoblano.alcaldia.util;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.javpoblano.alcaldia.models.AmoblamientoPunto;


/**
 * Created by javpoblano on 10/01/2017.
 */


public class AmoblamientoPuntoRenderer extends DefaultClusterRenderer<AmoblamientoPunto> {



    public AmoblamientoPuntoRenderer(Context context, GoogleMap map, ClusterManager clusterManager) {
        super(context, map, clusterManager);
    }


    @Override
    protected void onBeforeClusterItemRendered(AmoblamientoPunto item, MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromResource(item.getId()));
        //markerOptions.
        super.onBeforeClusterItemRendered(item,markerOptions);
    }
}
