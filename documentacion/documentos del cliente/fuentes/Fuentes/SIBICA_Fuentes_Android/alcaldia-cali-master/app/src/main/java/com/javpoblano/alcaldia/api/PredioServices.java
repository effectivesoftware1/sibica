package com.javpoblano.alcaldia.api;

import com.javpoblano.alcaldia.models.BusquedaData;
import com.javpoblano.alcaldia.models.PredioQuery;
import com.javpoblano.alcaldia.models.UserResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by javpoblano on 12/21/16.
 */

public interface PredioServices {

    @GET("loader.php?lServicio=Bienes&lFuncion=mostrarPredios")
    Call<ResponseBody> listPredios();

    @GET("loader.php?lServicio=Bienes&lFuncion=mostrarConstrucciones")
    Call<ResponseBody> listConstrucciones();

    @FormUrlEncoded
    @POST("loader.php?lServicio=Bienes&lFuncion=mostrarAmoblamientos")
    Call<ResponseBody> listAmoblamientos(@Field("tipo") String tipo);

    @FormUrlEncoded
    @POST("loader.php?lServicio=Bienes&lFuncion=informacionPredio")
    Call<ResponseBody> getInfoPredio(@Header("Cookie") String tokenheader, @Field(value = "predio", encoded = true) String predio, @Field(value = "token", encoded = true) String token);

    @FormUrlEncoded
    @POST("loader.php?lServicio=Bienes&lFuncion=login")
    Call<UserResponse> login(@Field("usuario") String usuario, @Field("password") String password);

    @FormUrlEncoded
    @POST("loader.php?lServicio=Bienes&lFuncion=reporte")
    Call<ResponseBody> reportPredio (@Field("tipo")String tipo,@Field("direccion")String direccion,@Field("predial")String predial,@Field("nombre")String nombre,@Field("correo")String correo,@Field("telefono")String telefono,@Field("coordenadas")String coordenadas,@Field("equipo")String equipo);

    @FormUrlEncoded
    @POST("loader.php?lServicio=Bienes&lFuncion=buscarPoligonos")
    Call<BusquedaData> search(@Field("opcion") String opcion, @Field("valor") String valor,@Field("token") String token);


}
