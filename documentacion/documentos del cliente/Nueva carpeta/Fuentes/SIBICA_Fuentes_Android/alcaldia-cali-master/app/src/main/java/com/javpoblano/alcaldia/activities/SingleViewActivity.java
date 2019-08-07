package com.javpoblano.alcaldia.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.javpoblano.alcaldia.R;
import com.javpoblano.alcaldia.models.ExtraData;
import com.javpoblano.alcaldia.models.GeneralData;
import com.javpoblano.alcaldia.models.InformacionPredio;
import com.javpoblano.alcaldia.models.PredioQuery;
import com.javpoblano.alcaldia.api.PredioServices;
import com.javpoblano.alcaldia.util.SharedPrefs;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SingleViewActivity extends AppCompatActivity implements OnMapReadyCallback{

    MapView mapView;
    GoogleMap map;
    OnMapReadyCallback onMapReadyCallback;
    Retrofit retrofit;
    PredioServices predioServices;
    InformacionPredio informacionPredio;
    TextView tipo,dir_of,dir_con,predialTerreno,predialEdificacion
            ,nombreProyecto,nombreAreaCedida,nombreEdificacion
            ,areaEdificacion,matricula,areaCesion,nombreComun;

    TextView codigo_nacional,num_activo_fijo,barrio,comuna,
            estrato,cedente,nombre_cb,orfeo,nombre_tb,nombre_tu,nombre_madq,
            lind_norte_predio,lind_sur_predio,lind_este_predio,lind_oeste_predio,
            lind_adic_predio,estado_predio,nombre_tipo,numero_doc,fecha_documento,
            nombre_not,numero_cont,area_entregada,fecha_inicio_contrato,fecha_fin_contrato,
            estado_contrato,lind_norte_contrato,lind_sur_contrato,lind_este_contrato,lind_oeste_contrato,
            lind_adic_contrato,tercero,numero_pisos_construccion,activo_fijo_construccion,nombre_dependencia,
            path_archivo_digital,path_archivo_fotos,fecha_observacion,observacion,nombre_usuario,apellido_usuario,url1,url2,url3;
    final String TAG = "SIBICA";
    Bundle data;
    LatLng street;
    SharedPrefs sharedPrefs;


    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPrefs = new SharedPrefs(getApplicationContext());
        Intent i = getIntent();
        data = i.getExtras();

        street = new LatLng(data.getDouble("lat"),data.getDouble("lng"));

        pd = new ProgressDialog(this);
        pd.setTitle("Cargando...");
        pd.setMessage("Por favor, espere.");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();

        //MAP VIEW
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        onMapReadyCallback=this;
        mapView.getMapAsync(onMapReadyCallback);
        initRetrofit();
        initViews();
        loadSingle();
    }

    public void initViews()
    {
        tipo = (TextView)findViewById(R.id.tipoBien);
        dir_of = (TextView)findViewById(R.id.direccion);
        dir_con = (TextView)findViewById(R.id.direccionEdificacion);
        nombreAreaCedida = (TextView)findViewById(R.id.nombreAreaCedida);
        nombreComun = (TextView)findViewById(R.id.nombreComun);
        nombreEdificacion = (TextView)findViewById(R.id.nombreEdificacion);
        nombreProyecto = (TextView)findViewById(R.id.direccionEdificacion);
        predialEdificacion = (TextView)findViewById(R.id.predialConstruccion);
        predialTerreno = (TextView)findViewById(R.id.predialTerreno);
        areaCesion = (TextView)findViewById(R.id.areaCesion);
        areaEdificacion = (TextView)findViewById(R.id.areaEdificacion);
        matricula = (TextView)findViewById(R.id.matricula);

        codigo_nacional= (TextView)findViewById(R.id.codigo_nacional);
        num_activo_fijo= (TextView)findViewById(R.id.num_activo_fijo);
        barrio= (TextView)findViewById(R.id.barrio);
        comuna= (TextView)findViewById(R.id.comuna);
        estrato= (TextView)findViewById(R.id.estrato);
        cedente= (TextView)findViewById(R.id.cedente);
        nombre_cb= (TextView)findViewById(R.id.nombre_cb);
        orfeo= (TextView)findViewById(R.id.orfeo);
        nombre_tb= (TextView)findViewById(R.id.nombre_tb);
        nombre_tu= (TextView)findViewById(R.id.nombre_tu);
        nombre_madq= (TextView)findViewById(R.id.nombre_madq);
        lind_norte_predio= (TextView)findViewById(R.id.lind_norte_predio);
        lind_sur_predio= (TextView)findViewById(R.id.lind_sur_predio);
        lind_este_predio= (TextView)findViewById(R.id.lind_este_predio);
        lind_oeste_predio= (TextView)findViewById(R.id.lind_oeste_predio);
        lind_adic_predio= (TextView)findViewById(R.id.lind_adic_predio);
        estado_predio= (TextView)findViewById(R.id.estado_predio);
        nombre_tipo= (TextView)findViewById(R.id.nombre_tipo);
        numero_doc= (TextView)findViewById(R.id.numero_doc);
        fecha_documento= (TextView)findViewById(R.id.fecha_documento);
        nombre_not= (TextView)findViewById(R.id.nombre_not);
        numero_cont= (TextView)findViewById(R.id.numero_cont);
        area_entregada= (TextView)findViewById(R.id.area_entregada);
        fecha_inicio_contrato= (TextView)findViewById(R.id.fecha_inicio_contrato);
        fecha_fin_contrato= (TextView)findViewById(R.id.fecha_fin_contrato);
        estado_contrato= (TextView)findViewById(R.id.estado_contrato);
        lind_norte_contrato= (TextView)findViewById(R.id.lind_norte_contrato);
        lind_sur_contrato= (TextView)findViewById(R.id.lind_sur_contrato);
        lind_este_contrato= (TextView)findViewById(R.id.lind_este_contrato);
        lind_oeste_contrato= (TextView)findViewById(R.id.lind_oeste_contrato);
        lind_adic_contrato= (TextView)findViewById(R.id.lind_adic_contrato);
        tercero= (TextView)findViewById(R.id.tercero);
        numero_pisos_construccion= (TextView)findViewById(R.id.numero_pisos_construccion);
        activo_fijo_construccion= (TextView)findViewById(R.id.activo_fijo_construccion);
        nombre_dependencia= (TextView)findViewById(R.id.nombre_dependencia);
        path_archivo_digital= (TextView)findViewById(R.id.path_archivo_digital);
        path_archivo_fotos= (TextView)findViewById(R.id.path_archivo_fotos);
        fecha_observacion= (TextView)findViewById(R.id.fecha_observacion);
        observacion= (TextView)findViewById(R.id.observacion);
        nombre_usuario= (TextView)findViewById(R.id.nombre_usuario);
        apellido_usuario= (TextView)findViewById(R.id.apellido_usuario);
        url1= (TextView)findViewById(R.id.url1);
        url2= (TextView)findViewById(R.id.url2);
        url3= (TextView)findViewById(R.id.url3);

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        MapsInitializer.initialize(getApplicationContext());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(data.getDouble("lat"),data.getDouble("lng")), 18);
        map.moveCamera(cameraUpdate);
        double[] lats = data.getDoubleArray("lats");
        double[] lngs = data.getDoubleArray("lngs");
        PolygonOptions options = new PolygonOptions();
        options.strokeColor(Color.TRANSPARENT);
        options.fillColor(Color.parseColor(data.getString("color")));
        for(int i = 0 ; i<lats.length ; i++)
        {
            options.add(new LatLng(lats[i],lngs[i]));
        }
        Polygon x = map.addPolygon(options);


        //map.addMarker(new MarkerOptions().title("test").position(new LatLng(19.005036, -98.205104)));
        /*Polygon x = map.addPolygon(new PolygonOptions()
                .add(new LatLng(19.00453001772766,-98.20427656173706))
                .add(new LatLng(19.00519952286137,-98.20396542549133))
                .add(new LatLng(19.005584994291805,-98.20473790168762))
                .add(new LatLng(19.005077794855666,-98.2057249546051))
                .strokeColor(Color.parseColor("#55555555"))
                .fillColor(Color.parseColor("#55555555")));
                */
    }

    public void loadSingle()
    {
        final String predio = data.getString("predio");
        //PredioQuery predioQuery = new PredioQuery(predio,"0");
        Log.d(TAG, "PREDIO: "+predio+" TOKEN : "+sharedPrefs.readSharedSetting("token",""));
        Call<ResponseBody> result = predioServices.getInfoPredio(sharedPrefs.readSharedSetting("cookie",""),predio,sharedPrefs.readSharedSetting("token",""));//,sharedPrefs.readSharedSetting("token",""));

        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try
                {
                    Log.d(TAG, response.headers().get("Set-Cookie"));
                    String aux = response.body().string();

                    Log.d(TAG, aux);
                    parsePredialInfo(aux);
                }
                catch (Exception e)
                {
                    Log.d("MÑEH", "error parsing");
                }
                pd.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
            }
        });
    }

    private InformacionPredio parsePredialInfo(String data)
    {
        LinearLayout container = (LinearLayout)findViewById(R.id.dataExtra);
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
            general.predial_construccion=dat.getString("predial_construccion");
            general.predial_terreno=dat.getString("predial_terreno");
            general.nombre_area_cedida=dat.getString("nombre_area_cedida");
            general.nombre_construccion=dat.getString("nombre_construccion");
            general.nombre_proyecto=dat.getString("nombre_proyecto");
            info.data = general;

            try
            {
                JSONObject ext = gen.getJSONObject("extras");
                ExtraData extra = new ExtraData();
                extra.codigo_nacional=ext.getString("codigo_nacional");
                extra.num_activo_fijo=ext.getString("num_activo_fijo");
                extra.barrio=ext.getString("barrio");
                extra.comuna=ext.getString("comuna");
                extra.estrato=ext.getString("estrato");
                extra.cedente=ext.getString("cedente");
                extra.nombre_cb=ext.getString("nombre_cb");
                extra.orfeo=ext.getString("orfeo");
                extra.nombre_tb=ext.getString("nombre_tb");
                extra.nombre_tu=ext.getString("nombre_tu");
                extra.nombre_madq=ext.getString("nombre_madq");
                extra.lind_norte_predio=ext.getString("lind_norte_predio");
                extra.lind_sur_predio=ext.getString("lind_sur_predio");
                extra.lind_este_predio=ext.getString("lind_este_predio");
                extra.lind_oeste_predio=ext.getString("lind_oeste_predio");
                extra.lind_adic_predio=ext.getString("lind_adic_predio");
                extra.estado_predio=ext.getString("estado_predio");
                extra.nombre_tipo=ext.getString("nombre_tipo");
                extra.numero_doc=ext.getString("numero_doc");
                extra.fecha_documento=ext.getString("fecha_documento");
                extra.nombre_not=ext.getString("nombre_not");
                extra.numero_cont=ext.getString("numero_cont");
                extra.area_entregada=ext.getString("area_entregada");
                extra.fecha_inicio_contrato=ext.getString("fecha_inicio_contrato");
                extra.fecha_fin_contrato=ext.getString("fecha_fin_contrato");
                extra.estado_contrato=ext.getString("estado_contrato");
                extra.lind_norte_contrato=ext.getString("lind_norte_contrato");
                extra.lind_sur_contrato=ext.getString("lind_sur_contrato");
                extra.lind_este_contrato=ext.getString("lind_este_contrato");
                extra.lind_oeste_contrato=ext.getString("lind_oeste_contrato");
                extra.lind_adic_contrato=ext.getString("lind_adic_contrato");
                extra.tercero=ext.getString("tercero");
                extra.numero_pisos_construccion=ext.getString("numero_pisos_construccion");
                extra.activo_fijo_construccion=ext.getString("activo_fijo_construccion");
                extra.nombre_dependencia=ext.getString("nombre_dependencia");
                extra.path_archivo_digital=ext.getString("path_archivo_digital");
                extra.path_archivo_fotos=ext.getString("path_archivo_fotos");
                extra.fecha_observacion=ext.getString("fecha_observacion");
                extra.observacion=ext.getString("observacion");
                extra.nombre_usuario=ext.getString("nombre_usuario");
                extra.apellido_usuario=ext.getString("apellido_usuario");
                extra.url1=ext.getString("url1");
                extra.url2=ext.getString("url2");
                extra.url3=ext.getString("url3");
                info.extra=extra;

                //set text

                codigo_nacional.setText(extra.codigo_nacional);
                num_activo_fijo.setText(extra.num_activo_fijo);
                barrio.setText(extra.barrio);
                comuna.setText(extra.comuna);
                estrato.setText(extra.estrato);
                cedente.setText(extra.cedente);
                nombre_cb.setText(extra.nombre_cb);
                orfeo.setText(extra.orfeo);
                nombre_tb.setText(extra.nombre_tb);
                nombre_tu.setText(extra.nombre_tu);
                nombre_madq.setText(extra.nombre_madq);
                lind_norte_predio.setText(extra.lind_norte_predio);
                lind_sur_predio.setText(extra.lind_sur_predio);
                lind_este_predio.setText(extra.lind_este_predio);
                lind_oeste_predio.setText(extra.lind_oeste_predio);
                lind_adic_predio.setText(extra.lind_adic_predio);
                estado_predio.setText(extra.estado_predio);
                nombre_tipo.setText(extra.nombre_tipo);
                numero_doc.setText(extra.numero_doc);
                fecha_documento.setText(extra.fecha_documento);
                nombre_not.setText(extra.nombre_not);
                numero_cont.setText(extra.numero_cont);
                area_entregada.setText(extra.area_entregada+" m²");
                fecha_inicio_contrato.setText(extra.fecha_inicio_contrato);
                fecha_fin_contrato.setText(extra.fecha_fin_contrato);
                estado_contrato.setText(extra.estado_contrato);
                lind_norte_contrato.setText(extra.lind_norte_contrato);
                lind_sur_contrato.setText(extra.lind_sur_contrato);
                lind_este_contrato.setText(extra.lind_este_contrato);
                lind_oeste_contrato.setText(extra.lind_oeste_contrato);
                lind_adic_contrato.setText(extra.lind_adic_contrato);
                tercero.setText(extra.tercero);
                numero_pisos_construccion.setText(extra.numero_pisos_construccion);
                activo_fijo_construccion.setText(extra.activo_fijo_construccion);
                nombre_dependencia.setText(extra.nombre_dependencia);
                path_archivo_digital.setText(extra.path_archivo_digital);
                path_archivo_fotos.setText(extra.path_archivo_fotos);
                fecha_observacion.setText(extra.fecha_observacion);
                observacion.setText(extra.observacion);
                nombre_usuario.setText(extra.nombre_usuario);
                apellido_usuario.setText(extra.apellido_usuario);
                url1.setText(extra.url1);
                url2.setText(extra.url2);
                url3.setText(extra.url3);
                container.setVisibility(View.VISIBLE);


            }
            catch (Exception e)
            {
                container.setVisibility(View.GONE);
                Log.e(TAG, "parsePredialInfo: "+e.toString() );
            }



            //info.data.nombre_comun=(info.data.nombre_comun.equals(""))?"Sin Nombre":info.data.nombre_comun;

            //SET DATA

            getSupportActionBar().setTitle(info.data.nombre_comun);
            tipo.setText(info.data.tipo_bien);
            dir_con.setText(info.data.direccion_construccion);
            dir_of.setText(info.data.direccion_oficial);
            nombreAreaCedida.setText(info.data.nombre_area_cedida);
            nombreProyecto.setText(info.data.nombre_proyecto);
            nombreEdificacion.setText(info.data.nombre_construccion);
            nombreComun.setText(info.data.nombre_comun);
            matricula.setText(info.data.matricula);
            predialTerreno.setText(info.data.predial_terreno);
            predialEdificacion.setText(info.data.predial_construccion);
            areaEdificacion.setText(info.data.area_edificada+" m²");
            areaCesion.setText(info.data.area_cedida+" m²");



        }
        catch (Exception e)
        {
            Log.e("Damn",e.getMessage()+data);
        }
        return info;
    }

    public void initRetrofit()
    {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        retrofit = new Retrofit.Builder()
                .baseUrl(getApplicationContext().getString(R.string.base_url))
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                //.addConverterFactory(GsonConverterFactory.create())
                // add other factories here, if needed.
                .build();
        predioServices = retrofit.create(PredioServices.class);

    }

    public void openReport(View view)
    {
        Intent i = new Intent(SingleViewActivity.this,ReportActivity.class);
        i.putExtra("direccion",dir_of.getText().toString());
        i.putExtra("predial",predialTerreno.getText().toString());
        startActivity(i);
    }

    public void openStreetView(View view)
    {
        Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+street.latitude+","+street.longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
