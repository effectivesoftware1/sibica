package com.javpoblano.alcaldia.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.lguipeng.library.animcheckbox.AnimCheckBox;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.javpoblano.alcaldia.R;
import com.javpoblano.alcaldia.adapters.SearchAdapter;
import com.javpoblano.alcaldia.api.PredioServices;
import com.javpoblano.alcaldia.controllers.PredioController;
import com.javpoblano.alcaldia.interfaces.ListInterface;
import com.javpoblano.alcaldia.models.AllPrediosResponse;
import com.javpoblano.alcaldia.models.BusquedaData;
import com.javpoblano.alcaldia.models.BusquedaItem;
import com.javpoblano.alcaldia.models.PredioParse;
import com.javpoblano.alcaldia.interfaces.MapInterface;
import com.javpoblano.alcaldia.util.SharedPrefs;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback,MapInterface,ListInterface {

    MapView mapView;
    GoogleMap map;
    OnMapReadyCallback onMapReadyCallback;
    Dialog search;
    Retrofit retrofit;
    PredioController predioController;
    AllPrediosResponse allPrediosResponse;
    MapInterface mapInterface;
    ProgressDialog pd;
    ProgressBar pb;
    SharedPrefs sharedPrefs;
    PredioServices predioServices;
    int init = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //toolbar.setNavigationIcon(R.drawable.logo);

        //init shared prefs
        checkPerms();
        sharedPrefs = new SharedPrefs(getApplicationContext());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });
        initFilterValues();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar.setLogo(R.mipmap.loguito2);
        //toolbar.setLogo(R.drawable.logo);
        //toolbar.setFrameVisibility();

        //showAlert();

        pd = new ProgressDialog(this);
        pd.setTitle("Cargando...");
        pd.setMessage("Por favor, espere.");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();

        showAlert();

        pb = (ProgressBar)findViewById(R.id.progress);
        //pb.setVisibility(View.VISIBLE);
        //MAP VIEW
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        onMapReadyCallback=this;
        mapInterface = this;
        initRetrofit();
        mapView.getMapAsync(onMapReadyCallback);
    }

    public void initRetrofit()
    {
        retrofit = new Retrofit.Builder()
                .baseUrl(getApplicationContext().getString(R.string.base_url))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                // add other factories here, if needed.
                .build();
        predioServices = retrofit.create(PredioServices.class);
        predioController = new PredioController(getApplicationContext(),retrofit,mapInterface,pd,pb);
    }

    public void initFilterValues()
    {
        /*
        sharedPrefs.saveSharedSetting("predioPropiedad",sharedPrefs.readSharedSetting("predioPropiedad",true));
        sharedPrefs.saveSharedSetting("predioPropiedadVenta",sharedPrefs.readSharedSetting("predioPropiedadVenta",true));
        sharedPrefs.saveSharedSetting("predioParcial",sharedPrefs.readSharedSetting("predioParcial",true));
        sharedPrefs.saveSharedSetting("predioParcialVenta",sharedPrefs.readSharedSetting("predioParcialVenta",true));
        sharedPrefs.saveSharedSetting("construcciones",sharedPrefs.readSharedSetting("construcciones",true));
        sharedPrefs.saveSharedSetting("mobiliario",sharedPrefs.readSharedSetting("mobiliario",true));
        */

        sharedPrefs.saveSharedSetting("predioPropiedad",true);
        sharedPrefs.saveSharedSetting("predioPropiedadVenta",true);
        sharedPrefs.saveSharedSetting("predioParcial",true);
        sharedPrefs.saveSharedSetting("predioParcialVenta",true);
        sharedPrefs.saveSharedSetting("construcciones",false);
        sharedPrefs.saveSharedSetting("mobiliario",false);
        sharedPrefs.saveSharedSetting("mobiliario2",false);
        sharedPrefs.saveSharedSetting("mobiliario3",false);

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        try
        {
            if(init!=0)
            {
                pd.dismiss();
            }
            //
        }
        catch (Exception e)
        {
            Log.d("SIBICA","ERR DIALOG");
        }

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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        mapView.onSaveInstanceState(bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            showDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDialog()
    {
        final Dialog dialog = new Dialog(this);
        search = dialog;
        dialog.setContentView(R.layout.dialog_list);
        dialog.setTitle("Buscar");
        //SearchView searchView = (SearchView)dialog.findViewById(R.id.searchview);
        //searchView.setIconified(false);
        final EditText editText = (EditText)search.findViewById(R.id.search);
        final ProgressBar pbAux = (ProgressBar)search.findViewById(R.id.pb);
        final TextView nores = (TextView)search.findViewById(R.id.nodata);
        final ListView lv = (ListView)search.findViewById(R.id.devlist);
        List<String> spinnerArray =  new ArrayList<>();
        spinnerArray.add("Dirección");
        spinnerArray.add("Matrícula inmobiliaria");
        spinnerArray.add("Número Predial");
        spinnerArray.add("Barrio");
        spinnerArray.add("Comuna");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner tipo = (Spinner) dialog.findViewById(R.id.tipo);
        tipo.setAdapter(adapter);
        final String token = "";
        final ListInterface listinter = this;
        final Activity parent = this;
        tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editText.setText("");
                lv.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                //Log.d("SIBICA", "SEARCH");
                //Log.d("SIBICA", keyEvent.getKeyCode()+"");
                if(keyEvent.getKeyCode() == 66)
                {
                    Log.d("SIBICA", "SEARCH");
                    pbAux.setVisibility(View.VISIBLE);
                    lv.setVisibility(View.GONE);
                    nores.setVisibility(View.GONE);
                    Call<BusquedaData> call = predioServices.search(tipo.getSelectedItemPosition()+1+"",editText.getText().toString(),token);
                    call.enqueue(new Callback<BusquedaData>() {
                        @Override
                        public void onResponse(Call<BusquedaData> call, Response<BusquedaData> response) {
                            if(response.body().getSuccess()==1)
                            {
                                SearchAdapter searchAdapter=new SearchAdapter(parent,response.body().getData(),listinter);
                                lv.setAdapter(searchAdapter);
                                lv.setVisibility(View.VISIBLE);
                                pbAux.setVisibility(View.GONE);
                                nores.setVisibility(View.GONE);
                            }
                            else
                            {
                                pbAux.setVisibility(View.GONE);
                                nores.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onFailure(Call<BusquedaData> call, Throwable t) {

                        }
                    });
                }
                return false;
            }
        });

        dialog.show();
    }

    public void showFilterDialog()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_filter);
        setupFilterDialog(dialog);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //predioController.reDrawZones();
            }
        });

        Button ok = (Button)dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                predioController.reDrawZones();
                dialog.dismiss();
            }
        });
        dialog.show();
        /*final AlertDialog diag = new AlertDialog.Builder(this)
                .setTitle("Enter An Administrative Password")
                .setView(R.layout.dialog_filter)
                .create();

        diag.show();
        */
    }



    public void setupFilterDialog(Dialog root)
    {
        AnimCheckBox predioPropiedad = (AnimCheckBox)root.findViewById(R.id.checkboxPropiedad);
        AnimCheckBox predioPropiedadVenta = (AnimCheckBox)root.findViewById(R.id.checkboxPropiedadVenta);
        AnimCheckBox predioParcialVenta = (AnimCheckBox)root.findViewById(R.id.checkboxParcialVenta);
        AnimCheckBox predioParcial = (AnimCheckBox)root.findViewById(R.id.checkboxParcialPropiedad);
        AnimCheckBox construcciones = (AnimCheckBox)root.findViewById(R.id.checkboxConstrucciones);
        AnimCheckBox mobiliario = (AnimCheckBox)root.findViewById(R.id.checkboxMobiliario);
        AnimCheckBox mobiliario2 = (AnimCheckBox)root.findViewById(R.id.checkboxMobiliario2);
        AnimCheckBox mobiliario3 = (AnimCheckBox)root.findViewById(R.id.checkboxMobiliario3);


        predioPropiedad.setChecked(sharedPrefs.readSharedSetting("predioPropiedad",true),false);
        predioParcial.setChecked(sharedPrefs.readSharedSetting("predioParcial",true),false);
        predioPropiedadVenta.setChecked(sharedPrefs.readSharedSetting("predioPropiedadVenta",true),false);
        predioParcialVenta.setChecked(sharedPrefs.readSharedSetting("predioParcialVenta",true),false);
        construcciones.setChecked(sharedPrefs.readSharedSetting("construcciones",true),false);
        mobiliario.setChecked(sharedPrefs.readSharedSetting("mobiliario",true),false);
        mobiliario2.setChecked(sharedPrefs.readSharedSetting("mobiliario2",true),false);
        mobiliario3.setChecked(sharedPrefs.readSharedSetting("mobiliario3",true),false);

        predioPropiedad.setOnCheckedChangeListener(new AnimCheckBox.OnCheckedChangeListener() {
            @Override
            public void onChange(boolean checked) {
                sharedPrefs.saveSharedSetting("predioPropiedad",checked);
            }
        });

        predioParcialVenta.setOnCheckedChangeListener(new AnimCheckBox.OnCheckedChangeListener() {
            @Override
            public void onChange(boolean checked) {
                sharedPrefs.saveSharedSetting("predioParcialVenta",checked);
            }
        });

        predioPropiedadVenta.setOnCheckedChangeListener(new AnimCheckBox.OnCheckedChangeListener() {
            @Override
            public void onChange(boolean checked) {
                sharedPrefs.saveSharedSetting("predioPropiedadVenta",checked);
            }
        });

        predioParcial.setOnCheckedChangeListener(new AnimCheckBox.OnCheckedChangeListener() {
            @Override
            public void onChange(boolean checked) {
                sharedPrefs.saveSharedSetting("predioParcial",checked);
            }
        });

        construcciones.setOnCheckedChangeListener(new AnimCheckBox.OnCheckedChangeListener() {
            @Override
            public void onChange(boolean checked) {
                sharedPrefs.saveSharedSetting("construcciones",checked);
            }
        });

        mobiliario.setOnCheckedChangeListener(new AnimCheckBox.OnCheckedChangeListener() {
            @Override
            public void onChange(boolean checked) {
                sharedPrefs.saveSharedSetting("mobiliario",checked);
            }
        });

        mobiliario2.setOnCheckedChangeListener(new AnimCheckBox.OnCheckedChangeListener() {
            @Override
            public void onChange(boolean checked) {
                sharedPrefs.saveSharedSetting("mobiliario2",checked);
            }
        });

        mobiliario3.setOnCheckedChangeListener(new AnimCheckBox.OnCheckedChangeListener() {
            @Override
            public void onChange(boolean checked) {
                sharedPrefs.saveSharedSetting("mobiliario3",checked);
            }
        });

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent i = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_slideshow) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.cali.gov.co/bienes/manual/"));
            startActivity(browserIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        MapsInitializer.initialize(getApplicationContext());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(3.452139, -76.531002), 18);
        /*Polyline x = map.addPolyline(new PolylineOptions()
                .add(new LatLng(3.452139, -76.531002))
                .add(new LatLng(3.472139, -76.631002))
                .color(Color.parseColor("#000000"))
                .width(1));
                */
        map.moveCamera(cameraUpdate);
        /*try
        {
            map.setMyLocationEnabled(true);
            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 18);
                    map.moveCamera(cameraUpdate);
                    map.setOnMyLocationChangeListener(null);
                    map.setMyLocationEnabled(false);
                }
            });
        }
        catch (SecurityException e)
        {
            checkPerms();
        }*/
        predioController.setMap(map);
        predioController.setParent(this);
        allPrediosResponse = predioController.getAllPredios();
        init=1;
        //showAlert();
    }

    @Override
    public void onPolygonClick(PredioParse auxData) {
        Intent i = new Intent(MainActivity.this,SingleViewActivity.class);
        double[] lats=new double[auxData.getPredio().getLatLng().size()];
        double[] lngs=new double[auxData.getPredio().getLatLng().size()];
        for (int x = 0 ; x < auxData.getPredio().getLatLng().size() ; x++ )
        {
            lats[x]=auxData.getPredio().getLatLng().get(x).latitude;
            lngs[x]=auxData.getPredio().getLatLng().get(x).longitude;
            //Log.d("SIBICA",auxData.getPredio().getLatLng().get(x).latitude +  " " + auxData.getPredio().getLatLng().get(x).longitude );
        }

        i.putExtra("lats",lats);
        i.putExtra("lngs",lngs);
        i.putExtra("lat",auxData.getPredio().getContenido().getParsedCoordenate().latitude);
        i.putExtra("lng",auxData.getPredio().getContenido().getParsedCoordenate().longitude);
        i.putExtra("predio",auxData.getPredio().getContenido().getPredial());
        i.putExtra("color",auxData.getPredio().getColor());
        startActivity(i);
    }

    public void showAlert()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Apreciado usuario");
        alert.setMessage(this.getResources().getString(R.string.leyenda));
        alert.setPositiveButton("OK",null);
        alert.show();
    }


    public void checkPerms()
    {
        int REQUEST_CODE_ASK_PERMISSIONS = 1234;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasWriteSMS = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            int hasLocation = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int hasWIFI = checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE);
            //int hasPhone = checkSelfPermission(Manifest.permission.CALL_PHONE);
            if (hasWriteSMS != PackageManager.PERMISSION_GRANTED || hasLocation != PackageManager.PERMISSION_GRANTED || hasWIFI != PackageManager.PERMISSION_GRANTED ) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_WIFI_STATE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
        }

    }

    @Override
    public void onListItemClick(BusquedaItem data) {
        search.dismiss();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                new LatLng(Double.parseDouble(data.getLongitud())
                            , Double.parseDouble(data.getLatitud())), 18);
        //MarkerOptions aux = new MarkerOptions();
        //aux.position(new LatLng(Double.parseDouble(data.getLatitud())
        //        , Double.parseDouble(data.getLongitud())));
        //map.addMarker(aux);
        map.moveCamera(cameraUpdate);
    }
}
