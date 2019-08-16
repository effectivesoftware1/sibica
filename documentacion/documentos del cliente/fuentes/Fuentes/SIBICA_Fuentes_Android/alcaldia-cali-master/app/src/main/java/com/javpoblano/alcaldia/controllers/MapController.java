package com.javpoblano.alcaldia.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.javpoblano.alcaldia.R;
import com.javpoblano.alcaldia.models.AllPrediosResponse;
import com.javpoblano.alcaldia.models.AmoblamientoPunto;
import com.javpoblano.alcaldia.models.ConstruccionParsed;
import com.javpoblano.alcaldia.models.GeneralData;
import com.javpoblano.alcaldia.models.InformacionPredio;
import com.javpoblano.alcaldia.models.LineaAmoblamiento;
import com.javpoblano.alcaldia.models.PredioParse;
import com.javpoblano.alcaldia.interfaces.MapInterface;
import com.javpoblano.alcaldia.api.PredioServices;
import com.javpoblano.alcaldia.util.AmoblamientoPuntoRenderer;
import com.javpoblano.alcaldia.util.SharedPrefs;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by javpoblano on 12/21/16.
 */

public class MapController {
    GoogleMap map;
    Context context;
    AllPrediosResponse data;
    HashMap<Integer,Polygon> poligonos = new HashMap<>();
    HashMap<Integer,Polygon> construccionesHash = new HashMap<>();
    HashMap<String,Integer> polygonIDS = new HashMap<>();
    HashMap<Integer,Marker> markers = new HashMap<>();
    Polygon polygon=null;
    MapInterface mapInterface;
    PredioServices predioServices;
    ProgressDialog pd;
    ProgressBar pb;
    Handler handler = new Handler();
    SharedPrefs sharedPrefs;
    List<ConstruccionParsed> construcciones = new ArrayList<>();
    List<AmoblamientoPunto> amoblamientoPuntos = new ArrayList<>();
    ClusterManager<AmoblamientoPunto> clusterManager;
    List<LineaAmoblamiento> amoblamientosLinea = new ArrayList<>();
    List<Polyline> polylineas = new ArrayList<>();

    public MapController(GoogleMap map, Context context, AllPrediosResponse data, MapInterface mapInterface, PredioServices predioServices, ProgressDialog pd,ProgressBar pb) {
        this.map = map;
        this.context = context;
        this.data = data;
        this.mapInterface = mapInterface;
        this.predioServices = predioServices;
        this.pd=pd;
        this.pb=pb;
        sharedPrefs = new SharedPrefs(context);
    }

    public void setConstrucciones(List<ConstruccionParsed> construcciones)
    {
        this.construcciones=construcciones;
        createAllConstructions();
    }

    public void setAmoblamientoPuntos(List<AmoblamientoPunto> amoblamientoPuntos)
    {
        this.amoblamientoPuntos=amoblamientoPuntos;
        createAmoblamientosPuntos();
    }

    public void setAmoblamientoLinea(List<LineaAmoblamiento> amoblamientoLineas)
    {
        this.amoblamientosLinea=amoblamientoLineas;
        createAmoblamientosLinea();
    }



    public void drawAllZones()
    {
        pb.setVisibility(View.VISIBLE);
        HashMap<String,Integer> colors = new HashMap<>();
        for(int i = 0;i<data.getData().size();i++)
        {
            PredioParse aux = data.getData().get(i);
            Polygon x = map.addPolygon(new PolygonOptions()
                    .strokeColor(Color.TRANSPARENT)
                    .fillColor(Color.parseColor(aux.getPredio().getColor()))
                    .clickable(true)
                    .addAll(aux.getPredio().getLatLng()));
            int icon = (aux.getPredio().fraude.equals("Invasion")?R.drawable.ladron:R.drawable.alcaldia);
            Marker y = map.addMarker(new MarkerOptions()
                .position(aux.getPredio().getContenido().getParsedCoordenate())
                .icon(BitmapDescriptorFactory.fromResource(icon))
                .title(aux.getPredio().fraude)
                .visible(false)
            );
            if(!map.getProjection().getVisibleRegion().latLngBounds.contains(aux.getPredio().getContenido().getParsedCoordenate()))
            {
                x.setVisible(false);
            }
            //Log.d("WOW",x.getId());
            colors.put(aux.getPredio().getColor(),1);
            poligonos.put(i,x);
            markers.put(i,y);
            polygonIDS.put(x.getId(),i);
        }

        for (String x:colors.keySet()) {
            Log.d("COLORS", x);
        }

        initDrawerCheck();
        initPoligonListener();
        /*for(int i = 0;i<data.getData().size();i++)
        {
            PredioParse aux = data.getData().get(i);
            Marker x = map.addMarker(new MarkerOptions()
            .position(aux.getPredio().getContenido().getParsedCoordenate())
            );
            markers.put(x,i);
        }*/
        pd.dismiss();
        pb.setVisibility(View.GONE);
    }

    public void createAllConstructions()
    {
        construccionesHash = new HashMap<>();
        for(int j = 0;j<construcciones.size();j++)
        {
            if(construcciones.get(j).getColor().equals("#000"))
            {
                construcciones.get(j).setColor("#000000");
            }
            Polygon x = map.addPolygon(new PolygonOptions()
                    .strokeColor(Color.TRANSPARENT)
                    .fillColor(Color.parseColor(construcciones.get(j).getColor()))
                    .addAll(construcciones.get(j).getLatLngs()));
            construccionesHash.put(j,x);
        }
        Log.d("SIBICA", "LLAMADO" + construccionesHash.size());
    }

    public void createAmoblamientosPuntos()
    {
        clusterManager = new ClusterManager<AmoblamientoPunto>(context,map);
        map.setOnCameraIdleListener(clusterManager);
        //clusterManager.addItems(amoblamientoPuntos);
        AmoblamientoPuntoRenderer renderer = new AmoblamientoPuntoRenderer(context,map,clusterManager);
        clusterManager.setRenderer(renderer);
        //clusterManager.cluster();
    }

    public void createAmoblamientosLinea()
    {
        polylineas = new ArrayList<>();
        for(int i = 0;i<amoblamientosLinea.size();i++)
        {
            try
            {
                Polyline x = map.addPolyline(new PolylineOptions()
                        .addAll(amoblamientosLinea.get(i).getLine())
                        .color(Color.parseColor(amoblamientosLinea.get(i).getColor()))
                        .width(2)
                        );
                //x.setClickable(false);
                //x.setVisible(false);
                polylineas.add(x);
                //Log.d("SIBICA",amoblamientosLinea.get(i).getLine().toString());
            }
            catch (Exception e)
            {
                Log.d("SIBICA - Lineas", amoblamientosLinea.get(i).getColor());
            }
        }
    }



    public void reDrawAllZones()
    {
        pb.setVisibility(View.VISIBLE);
        pb.setIndeterminate(true);
        pb.setMax(data.getData().size());
        pb.setProgress(0);
        handler.removeCallbacks(refreshPolygons);
        handler.postDelayed(refreshPolygons,2000);
    }

    private void initPoligonListener()
    {
        map.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                //Log.d("WOW","doing some shit");
                PredioParse auxData = data.getData().get(polygonIDS.get(polygon.getId()));
                mapInterface.onPolygonClick(auxData);
            }
        });
    }



    private InformacionPredio parsePredialInfo(String data)
    {
        InformacionPredio info = new InformacionPredio();
        try
        {
            JSONObject root = new JSONObject(data);
            info.success=root.getInt("success");
            info.msg=root.getString("msg");
            GeneralData general = new GeneralData();
            JSONObject gen = root.getJSONObject("data");
            JSONObject dat = gen.getJSONObject("general");
            general.area_cedida = dat.getString("area_cedida");
            general.area_edificada = dat.getString("area_edificada");
            general.direccion_construccion = dat.getString("direccion_construccion");
            general.direccion_oficial = dat.getString("direccion_oficial");
            general.matricula=dat.getString("matricula");
            general.tipo_bien=dat.getString("tipo_bien");
            general.nombre_comun=dat.getString("nombre_comun");
            info.data= general;
        }
        catch (Exception e)
        {
            Log.e("Damn",e.toString());
        }
        return info;
    }

    private void initDrawerCheck()
    {
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                pb.setVisibility(View.VISIBLE);
                pb.setIndeterminate(true);
                pb.setMax(data.getData().size());
                pb.setProgress(0);
                handler.removeCallbacks(refreshPolygons);
                handler.postDelayed(refreshPolygons,2000);

            }
        });


    }

    public boolean drawOrNot(String color)
    {
        boolean result = false;
        if(color.equals(context.getResources().getString(R.string.predioPropiedad)))
        {
            result = sharedPrefs.readSharedSetting("predioPropiedad",true);
        }
        else if(color.equals(context.getResources().getString(R.string.predioPropiedadVenta)))
        {
            result = sharedPrefs.readSharedSetting("predioPropiedadVenta",true);
        }
        else if(color.equals(context.getResources().getString(R.string.predioParcialPropiedad)))
        {
            result = sharedPrefs.readSharedSetting("predioParcial",true);
        }
        else if(color.equals(context.getResources().getString(R.string.predioParcialVenta)))
        {
            result = sharedPrefs.readSharedSetting("predioParcialVenta",true);
        }
        return result;
    }

    public boolean drawOrNotConstruction(String capa)
    {
        boolean result = false;
        if(capa.equals("Construccion Propiedad del Mpio"))
        {
            result = sharedPrefs.readSharedSetting("predioPropiedad",true);
        }
        else if(capa.equals(context.getResources().getString(R.string.predioPropiedadVenta)))
        {
            result = sharedPrefs.readSharedSetting("predioPropiedadVenta",true);
        }
        else if(capa.equals(context.getResources().getString(R.string.predioParcialPropiedad)))
        {
            result = sharedPrefs.readSharedSetting("predioParcial",true);
        }
        else if(capa.equals(context.getResources().getString(R.string.predioParcialVenta)))
        {
            result = sharedPrefs.readSharedSetting("predioParcialVenta",true);
        }
        //Construccion Propiedad del Mpio
        //Via Publica
        //Zona Verde
        return false;
    }

    private final Runnable refreshPolygons = new Runnable(){
        public void run(){
            try {
                for(int i = 0;i<data.getData().size();i++)
                {
                    if(!map.getProjection().getVisibleRegion().latLngBounds.contains(data.getData().get(i).getPredio().getContenido().getParsedCoordenate())
                            ||!drawOrNot(data.getData().get(i).getPredio().getColor()))
                    {
                        poligonos.get(i).setVisible(false);
                        poligonos.get(i).setClickable(false);
                        markers.get(i).setVisible(false);
                    }
                    else
                    {
                        if(drawOrNot(data.getData().get(i).getPredio().getColor()))
                        {
                            poligonos.get(i).setVisible(true);
                            poligonos.get(i).setClickable(true);
                            poligonos.get(i).setZIndex(1);
                            if(data.getData().get(i).getPredio().fraude.equals("Invasion"))
                            {
                                markers.get(i).setVisible(true);
                            }
                            else
                            {
                                markers.get(i).setVisible(false);
                            }
                        }
                    }
                }
                if(sharedPrefs.readSharedSetting("construcciones",false))
                {
                    for(int j = 0;j<construcciones.size();j++)
                    {
                        if(!map.getProjection().getVisibleRegion().latLngBounds.contains(construcciones.get(j).getCoordenadas()))
                        {
                            construccionesHash.get(j).setVisible(false);
                            construccionesHash.get(j).setClickable(false);
                        }
                        else
                        {
                            construccionesHash.get(j).setVisible(true);
                            construccionesHash.get(j).setZIndex(2);
                            //construccionesHash.get(j).setClickable(true);
                        }
                    }
                }
                else
                {
                    for(int x = 0;x<construcciones.size();x++)
                    {
                        construccionesHash.get(x).setVisible(false);
                        construccionesHash.get(x).setClickable(false);
                    }
                }

                if(sharedPrefs.readSharedSetting("mobiliario",false))
                {
                    try
                    {
                        clusterManager.addItems(amoblamientoPuntos);
                        clusterManager.cluster();
                    }
                    catch (Exception e)
                    {
                        Log.d("SIBICA", e.toString());
                    }
                }
                else
                {
                    try
                    {
                        clusterManager.clearItems();
                        clusterManager.cluster();
                        /*float zoom = map.getCameraPosition().zoom;
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(map.getCameraPosition().target, zoom-1);
                        map.moveCamera(cameraUpdate);
                        CameraUpdate cameraUpdate2 = CameraUpdateFactory.newLatLngZoom(map.getCameraPosition().target, zoom);
                        map.moveCamera(cameraUpdate2);
                        handler.removeCallbacks(refreshPolygons);
                        pb.setVisibility(View.GONE);*/
                    }
                    catch (Exception e)
                    {
                        Log.e("SIBICA", e.toString());
                    }
                }
                if(sharedPrefs.readSharedSetting("mobiliario2",false))
                {
                    Log.d("SIBICA", "ENTRE!!!"+polylineas.size());
                    //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(polylineas.get(0).getPoints().get(0), 18);
                    //map.moveCamera(cameraUpdate);
                    for (int i = 0;i<polylineas.size();i++)
                    {
                        try
                        {
                            if (!map.getProjection().getVisibleRegion().latLngBounds.contains(polylineas.get(i).getPoints().get(0)))
                            {
                                //Log.d("SIBICA", "TRUE");
                                polylineas.get(i).setVisible(false);
                            }
                            else
                            {
                                //Log.d("SIBICA", "false");
                                polylineas.get(i).setVisible(true);
                                polylineas.get(i).setZIndex(2);
                            }
                        }
                        catch (Exception e)
                        {
                            Log.e("SIBICA", "run: ",e );
                        }
                    }

                }
                else
                {
                    for (int i = 0;i<polylineas.size();i++)
                    {
                        polylineas.get(i).setVisible(false);
                        polylineas.get(i).setClickable(false);
                    }
                }

                pb.setVisibility(View.GONE);
                handler.removeCallbacks(refreshPolygons);
            }
            catch (Exception e) {
                Log.d("SIBICA", ""+e.toString());
                pb.setVisibility(View.GONE);
                handler.removeCallbacks(refreshPolygons);
                e.printStackTrace();
            }
        }
    };

}
