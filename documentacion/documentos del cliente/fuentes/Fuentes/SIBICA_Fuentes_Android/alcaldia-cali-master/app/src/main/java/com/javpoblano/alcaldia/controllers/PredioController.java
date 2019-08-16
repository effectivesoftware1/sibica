package com.javpoblano.alcaldia.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.javpoblano.alcaldia.R;
import com.javpoblano.alcaldia.models.AllPrediosResponse;
import com.javpoblano.alcaldia.models.AmoblamientoPunto;
import com.javpoblano.alcaldia.models.ConstruccionParsed;
import com.javpoblano.alcaldia.models.Contenido;
import com.javpoblano.alcaldia.models.LineaAmoblamiento;
import com.javpoblano.alcaldia.models.Predio;
import com.javpoblano.alcaldia.models.PredioParse;
import com.javpoblano.alcaldia.interfaces.MapInterface;
import com.javpoblano.alcaldia.api.PredioServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by javpoblano on 12/21/16.
 */

public class PredioController {
    Context context;
    Retrofit retrofit;
    PredioServices predioServices;
    MapController mapController;
    AllPrediosResponse output;
    GoogleMap map;
    MapInterface mapInterface;
    ProgressDialog pd;
    ProgressBar pb;
    Activity parent;

    public void setParent(Activity parent)
    {
        this.parent=parent;
    }

    public GoogleMap getMap() {
        return map;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    public PredioController(Context context, Retrofit retrofit, MapInterface mapInterface,ProgressDialog pd,ProgressBar pb) {
        this.context = context;
        this.retrofit = retrofit;
        this.mapInterface = mapInterface;
        this.pd=pd;
        this.pb=pb;
        predioServices = retrofit.create(PredioServices.class);
    }

    public void reDrawZones()
    {
        mapController.reDrawAllZones();
    }

    public AllPrediosResponse getAllPredios()
    {
        //output = new AllPrediosResponse();
        Call<ResponseBody> result = predioServices.listPredios();
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try
                {
                    Log.d("LOL","YAY");
                    parseAllPrediosResponse(response.body().string());
                }
                catch (Exception x)
                {
                    Log.e("LOL","MÑEH = " + x.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
        return output;
    }

    public void parseAllPrediosResponse(String data) throws JSONException
    {
        AllPrediosResponse result = new AllPrediosResponse();
        JSONObject root = new JSONObject(data);
        result.setSuccess(root.getInt("success"));
        result.setMsg(root.getString("msg"));
        JSONObject container = root.getJSONObject("data");
        for(Iterator iterator = container.keys();iterator.hasNext();)
        {
            PredioParse aux = new PredioParse();
            aux.setId((String)iterator.next());
            Predio predio = new Predio();
            //Color
            JSONObject containerAux = container.getJSONObject(aux.getId());
            predio.setColor(containerAux.getString("color"));
            //Contenido
            JSONObject contenidoAux = containerAux.getJSONObject("contenido");
            Contenido contenido = new Contenido();
            contenido.setTipo(contenidoAux.getString("tipo"));
            contenido.setCoordenadas(contenidoAux.getString("coordenadas"));
            contenido.setMatricula(contenidoAux.getString("matricula"));
            contenido.setDireccion(contenidoAux.getString("direccion"));
            contenido.setNombre(contenidoAux.getString("nombre"));
            contenido.setPredial(contenidoAux.getString("predial"));
            predio.setContenido(contenido);
            //Construcciones
            predio.setConstrucciones(new ArrayList<String>());
            //LatLng
            JSONArray latlng = containerAux.getJSONArray("LatLng");
            List<LatLng> auxCoordenadas = new ArrayList<>();
            for (int i = 0 ; i < latlng.length() ; i++)
            {
                JSONObject singleCoor = latlng.getJSONObject(i);
                LatLng parsedCoor = new LatLng(singleCoor.getDouble("lat"),singleCoor.getDouble("lng"));
                auxCoordenadas.add(parsedCoor);
            }
            //fraude
            JSONObject fraude = containerAux.getJSONObject("fraude");
            predio.fraude = fraude.getString("tipo");
            predio.setLatLng(auxCoordenadas);
            aux.setPredio(predio);
            result.addSingleData(aux);
        }
        Log.d("LOL","FINISH HIM!");
        output = result;
        mapController = new MapController(map,context,output,mapInterface,predioServices,pd,pb);
        mapController.drawAllZones();
        //showAlert();
        getAllConstrucciones();
        getAllAmoblamientosPunto();
        getAllAmoblamientosLinea();
    }

    public void getAllConstrucciones()
    {
        final List<ConstruccionParsed> construcciones = new ArrayList<>();
        Call<ResponseBody> result = predioServices.listConstrucciones();
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try
                {
                    //parseAllPrediosResponse();
                    JSONObject root = new JSONObject(response.body().string());
                    if(root.getInt("success")==1)
                    {
                        JSONObject container = root.getJSONObject("data");
                        for(Iterator iterator = container.keys();iterator.hasNext();)
                        {
                            try
                            {
                                ConstruccionParsed aux = new ConstruccionParsed();
                                aux.setId((String)iterator.next());
                                JSONObject dataAux = container.getJSONObject(aux.getId());
                                aux.setColor(dataAux.getString("color"));
                                String coord = dataAux.getString("coordenadas");
                                String[] separatedCoor = coord.split(",");
                                LatLng coordenateParsed = new LatLng(Double.parseDouble(separatedCoor[0].trim()),Double.parseDouble(separatedCoor[1].trim()));
                                aux.setCoordenadas(coordenateParsed);
                                JSONArray latlng = dataAux.getJSONArray("LatLng");
                                List<LatLng> auxCoordenadas = new ArrayList<>();
                                for (int i = 0 ; i < latlng.length() ; i++)
                                {
                                    JSONObject singleCoor = latlng.getJSONObject(i);
                                    LatLng parsedCoor = new LatLng(singleCoor.getDouble("lat"),singleCoor.getDouble("lng"));
                                    auxCoordenadas.add(parsedCoor);
                                }
                                aux.setLatLngs(auxCoordenadas);
                                aux.setCapa(dataAux.getString("capa"));
                                construcciones.add(aux);
                            }
                            catch (Exception e)
                            {
                                Log.e("SIBICA INNER", e.toString());
                            }

                        }
                    }
                }
                catch (Exception x)
                {
                    Log.e("SIBICA OUT", x.toString());
                }
                Log.d("SIBICA", "" + construcciones.size());
                mapController.setConstrucciones(construcciones);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    public void showAlert()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(parent);
        alert.setTitle("Apreciado usuario");
        alert.setMessage(context.getResources().getString(R.string.leyenda));
        alert.setPositiveButton("OK",null);
        alert.show();
    }

    public void getAllAmoblamientosPunto()
    {
        Call<ResponseBody> result = predioServices.listAmoblamientos("punto");
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try
                {
                    Log.d("LOL","YAY");
                    parseAmoblamientosPunto(response.body().string());
                }
                catch (Exception x)
                {
                    Log.e("LOL","MÑEH = " + x.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void parseAmoblamientosPunto(String dataSource) throws JSONException
    {
        //Sumidero
        //Camara
        //Post
        //Arbol
        //Palma
        //Luminaria
        //Hidrante
        //Cesta Basura
        HashMap<String,Integer> icons;
        icons=new HashMap<>();
        icons.put("Sumidero",R.drawable.sumidero);
        icons.put("Camara",R.drawable.camara);
        icons.put("Poste",R.drawable.poste);
        icons.put("Arbol",R.drawable.arbol);
        icons.put("Palma",R.drawable.palma);
        icons.put("Luminaria",R.drawable.luminaria);
        icons.put("Hidrante",R.drawable.hidrante);
        icons.put("Cesta Basura",R.drawable.cesta_basura);
        icons.put("Pino",R.drawable.pino);
        icons.put("Reflector",R.drawable.reflector);

        JSONObject root = new JSONObject(dataSource);
        JSONObject data = root.getJSONObject("data");
        List<AmoblamientoPunto> puntos = new ArrayList<>();
        for(Iterator iterator = data.keys();iterator.hasNext();)
        {
            AmoblamientoPunto punto = new AmoblamientoPunto();
            JSONObject aux = data.getJSONObject((String)iterator.next());
            try
            {
                punto.setId(icons.get(aux.getString("id")));
            }
            catch (Exception e)
            {
                Log.d("ERROR ID", aux.getString("id"));
                punto.setId(R.drawable.sumidero);
            }
            JSONArray lats = aux.getJSONArray("LatLng");
            double lat = lats.getJSONObject(0).getDouble("lat");
            double lng = lats.getJSONObject(0).getDouble("lng");
            punto.setPosition(lat,lng);
            puntos.add(punto);
        }
        mapController.setAmoblamientoPuntos(puntos);
        Log.d("SIBICA", "YEIIIHHH");

    }


    public void getAllAmoblamientosLinea()
    {
        Call<ResponseBody> result = predioServices.listAmoblamientos("linea");
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try
                {
                    Log.d("LOL","YAY");
                    parseAmoblamientosLinea(response.body().string());
                }
                catch (Exception x)
                {
                    Log.e("LOL","MÑEH = " + x.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    public void parseAmoblamientosLinea(String dataSource) throws JSONException
    {
        JSONObject root = new JSONObject(dataSource);
        JSONObject data = root.getJSONObject("data");
        List<LineaAmoblamiento> puntos = new ArrayList<>();
        for(Iterator iterator = data.keys();iterator.hasNext();)
        {
            LineaAmoblamiento punto = new LineaAmoblamiento();
            JSONObject aux = data.getJSONObject((String)iterator.next());

            JSONArray lats = aux.getJSONArray("LatLng");
            List<LatLng> dots = new ArrayList<>();
            for (int i = 0;i<lats.length();i++)
            {
                double lat = lats.getJSONObject(i).getDouble("lat");
                double lng = lats.getJSONObject(i).getDouble("lng");
                dots.add(new LatLng(lat,lng));
            }
            punto.setColor(aux.getString("impresion"));
            punto.setLine(dots);
            puntos.add(punto);
        }
        mapController.setAmoblamientoLinea(puntos);
        Log.d("SIBICA", "YEIIIHHH -- Lineas");

    }


}
