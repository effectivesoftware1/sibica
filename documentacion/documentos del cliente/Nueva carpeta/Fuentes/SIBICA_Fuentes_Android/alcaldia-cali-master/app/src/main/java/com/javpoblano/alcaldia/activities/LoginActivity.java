package com.javpoblano.alcaldia.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.javpoblano.alcaldia.R;
import com.javpoblano.alcaldia.api.PredioServices;
import com.javpoblano.alcaldia.models.UserResponse;
import com.javpoblano.alcaldia.util.SharedPrefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoginActivity extends AppCompatActivity {

    ProgressBar pb;
    EditText user,pass;
    Button b,b2;
    SharedPrefs sharedPrefs;
    PredioServices predioServices;
    Retrofit retrofit;
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
        initRetrofit();
        sharedPrefs =  new SharedPrefs(getApplicationContext());
        //Toast.makeText(this, sharedPrefs.readSharedSetting("token",""), Toast.LENGTH_SHORT).show();
        if(sharedPrefs.readSharedSetting("token","").equals(""))
        {
            user.setVisibility(View.VISIBLE);
            pass.setVisibility(View.VISIBLE);
            b.setVisibility(View.VISIBLE);
            container.setVisibility(View.VISIBLE);
            b2.setVisibility(View.GONE);
        }
        else
        {
            container.setVisibility(View.GONE);
            user.setVisibility(View.GONE);
            pass.setVisibility(View.GONE);
            b.setVisibility(View.GONE);
            b2.setVisibility(View.VISIBLE);
            b2.setText("Salir\n" + sharedPrefs.readSharedSetting("nombre",""));
        }
    }

    public void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(getApplicationContext().getString(R.string.base_url))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                // add other factories here, if needed.
                .build();
        predioServices = retrofit.create(PredioServices.class);
    }

    public void initViews()
    {
        pb = (ProgressBar)findViewById(R.id.bar);
        user = (EditText) findViewById(R.id.nombre);
        pass = (EditText) findViewById(R.id.pass);
        b = (Button)findViewById(R.id.login);
        b2 = (Button)findViewById(R.id.logut);
        container = (LinearLayout)findViewById(R.id.logincontainer);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    login();
                }
                catch (Exception e)
                {

                }


            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLogoff(v);
            }
        });
    }

    public void setLogin(UserResponse userdata)
    {
        sharedPrefs.saveSharedSetting("token",userdata.getData().getToken());
        sharedPrefs.saveSharedSetting("nombre",userdata.getData().getNombre());
        user.setVisibility(View.GONE);
        pass.setVisibility(View.GONE);
        b.setVisibility(View.GONE);
        b2.setText("Salir\n" + sharedPrefs.readSharedSetting("nombre",""));
        b2.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
    }

    public void setLogoff(View view)
    {
        sharedPrefs.saveSharedSetting("token","");
        sharedPrefs.saveSharedSetting("cookie","");
        sharedPrefs.saveSharedSetting("nombre","");
        user.setVisibility(View.VISIBLE);
        pass.setVisibility(View.VISIBLE);
        b.setVisibility(View.VISIBLE);
        b2.setVisibility(View.GONE);

        container.setVisibility(View.VISIBLE);
    }

    public void login()
    {

        pb.setVisibility(View.VISIBLE);
        Call<UserResponse> test = predioServices.login(user.getText().toString(),pass.getText().toString());
        test.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                try
                {
                    if(response.body().getSuccess()==1)
                    {
                        setLogin(response.body());
                        sharedPrefs.saveSharedSetting("cookie",response.headers().get("Set-Cookie"));
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Verifique sus datos.", Toast.LENGTH_SHORT).show();
                    }
                    pb.setVisibility(View.GONE);
                }
                catch (Exception e)
                {
                    Toast.makeText(LoginActivity.this, "Verifique sus datos.", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Verifique sus conexión a internet.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
