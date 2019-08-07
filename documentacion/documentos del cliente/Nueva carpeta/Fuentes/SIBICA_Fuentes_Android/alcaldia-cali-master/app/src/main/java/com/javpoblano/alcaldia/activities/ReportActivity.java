package com.javpoblano.alcaldia.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.javpoblano.alcaldia.R;
import com.javpoblano.alcaldia.api.PredioServices;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ReportActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    Spinner tipoReporte;
    private ArrayList<Image> images = new ArrayList<>();
    TextView direccion,predial;
    EditText nombre,telefono,correo;
    PredioServices predioServices;
    Retrofit retrofit;
    ProgressDialog pd;
    private GoogleApiClient googleApiClient;
    double lat=0,lon=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fillSpinner();
        try
        {
            predial = (TextView)findViewById(R.id.numPredial);
            direccion = (TextView)findViewById(R.id.direccion);
            nombre = (EditText) findViewById(R.id.nombre);
            telefono = (EditText) findViewById(R.id.telefono);
            correo = (EditText)findViewById(R.id.correo);
            Intent i = getIntent();
            Bundle data = i.getExtras();
            predial.setText(data.getString("predial"));
            direccion.setText(data.getString("direccion"));
            predial.setEnabled(false);
            direccion.setEnabled(false);
            googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();



            retrofit = new Retrofit.Builder()
                    .baseUrl(getApplicationContext().getString(R.string.base_url))
                    .client(client)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    // add other factories here, if needed.
                    .build();
            predioServices = retrofit.create(PredioServices.class);

        }
        catch (Exception e)
        {
            Log.d("SIBICA", e.toString());
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }




    public void fillSpinner()
    {
        List<String> spinnerArray =  new ArrayList<>();
        spinnerArray.add("Invasión");
        spinnerArray.add("Fraude");
        spinnerArray.add("Otro");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoReporte = (Spinner) findViewById(R.id.tipoReporte);
        tipoReporte.setAdapter(adapter);
    }

    public void reportar(View view)
    {
        pd = new ProgressDialog(this);
        pd.setTitle("Generando reporte...");
        pd.setMessage("Por favor, espere.");
        pd.setCancelable(true);
        pd.setIndeterminate(true);
        pd.show();
        String ipAddress = "0.0.0.0";
        try
        {
            //WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            //ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        }
        catch (Exception e)
        {
            Log.d("SIBICA", e.toString());
        }
        String tipoRep = "";
        switch (tipoReporte.getSelectedItemPosition())
        {
            case 0:
                tipoRep="invasion";
                break;
            case 1:
                tipoRep="fraude";
                break;
            case 2:
                tipoRep="otro";
                break;
        }

        Call<ResponseBody> rep = predioServices.reportPredio(tipoRep,direccion.getText().toString(),predial.getText().toString(),nombre.getText().toString(),correo.getText().toString(),telefono.getText().toString(),lat+","+lon,ipAddress);
        rep.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject root = new JSONObject(response.body().string());
                    int success = root.getInt("success");
                    if(success==1)
                    {
                        pd.dismiss();
                        Toast.makeText(ReportActivity.this, "Reporte exitoso", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        pd.dismiss();
                        Toast.makeText(ReportActivity.this, "Reporte no exitoso", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    pd.dismiss();
                    Toast.makeText(ReportActivity.this, "Verifique sus datos", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(ReportActivity.this, "Verifique su conexión a internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openPhotoPicker(View view)
    {
        Intent intent = new Intent(this, ImagePickerActivity.class);

        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_FOLDER_MODE, true);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_MODE, ImagePickerActivity.MODE_SINGLE);
        //intent.putExtra(ImagePicker.EXTRA_LIMIT, 10);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, true);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES, images);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_FOLDER_TITLE, "Carpetas");
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_IMAGE_TITLE, "Selecciona una imagen");
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_IMAGE_DIRECTORY, "Camara");
        //intent.putExtra(ImagePickerActivity, true); //default is false

        startActivityForResult(intent, 321);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 321 && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            //Toast.makeText(this, ""+images.size(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            try
            {
                Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                lat = lastLocation.getLatitude();
                lon = lastLocation.getLongitude();
                Log.d("SIBICA", lat+" "+lon);
            }
            catch (Exception e)
            {
                lat = 3.452139;
                lon = -76.531002;
                Log.d("SIBICA -  ERROR", lat+" "+lon);
            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("SIBICA", "SUSPENDED");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("SIBICA", connectionResult.toString());
    }
}
