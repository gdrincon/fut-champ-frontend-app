package net.jaumebalmes.grincon17.futchamp.repositoryApi;

import net.jaumebalmes.grincon17.futchamp.models.Marcador;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MarcadorRepositoryApi {

    // Devuelve una lista de marcadores de todos los equipos
    @GET("mostrar")
    Call<ArrayList<Marcador>> obtenerListaMarcadores();

    @POST("agregar")
    Call<Marcador> postMarcador(@Body Marcador marcador);


    // Devuelve un Marcador de puntuacion de un partido, tambien info de equipos en el
    @GET("mostrar/marcador/{idPartido}")
    Call<Marcador> obtenerMarcadorPartido(@Path("idPartido") Long idPartido);

}
