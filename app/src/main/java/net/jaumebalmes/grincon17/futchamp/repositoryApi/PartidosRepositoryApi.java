package net.jaumebalmes.grincon17.futchamp.repositoryApi;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

import net.jaumebalmes.grincon17.futchamp.models.Partido;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PartidosRepositoryApi {

    // Devuelve una lista de partidos locales de un equipo
    @GET("mostrar/locales/{nombreEquipoLocal}")
    Call<ArrayList<Partido>> obtenerListaPartidosLocales(@Path("nombreEquipoLocal") String nombreEquipoLocal);


    // Devuelve una lista de partidos vistantes de un equipo
    @GET("mostrar/visitantes/{nombreEquipoVisitante}")
    Call<ArrayList<Partido>> obtenerListaPartidosVisitantes(@Path("nombreEquipoVisitante") String nombreEquipoVisitante);

    // Devuelve una lista de partidos por jornadas
    @GET("mostrar/jornada/{jornada}")
    Call<ArrayList<Partido>> obtenerListaPartidosJornada(@Path("jornada") int jornada);

    @GET("mostrar")
    Call<ArrayList<Partido>> obtenerListaPartidos();

    @GET("mostrar/calendario")
    Call<ArrayList<Partido>> obtenerListaPartidosPorCalendario(@Query("nombreLeagueCalendario") String nombreLeagueCalendario,
                                                               @Query("fechaInicio") String fechaIncio);

}
