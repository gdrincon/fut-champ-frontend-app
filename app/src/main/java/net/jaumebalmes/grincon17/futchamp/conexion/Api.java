package net.jaumebalmes.grincon17.futchamp.conexion;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.jaumebalmes.grincon17.futchamp.R;
import net.jaumebalmes.grincon17.futchamp.adapters.MyEquipoRecyclerViewAdapter;
import net.jaumebalmes.grincon17.futchamp.adapters.MyJornadaRecyclerViewAdapter;
import net.jaumebalmes.grincon17.futchamp.adapters.MyJugadorRecyclerViewAdapter;
import net.jaumebalmes.grincon17.futchamp.adapters.MyLeagueRecyclerViewAdapter;
import net.jaumebalmes.grincon17.futchamp.adapters.MyNextPartidoRecyclerViewAdapter;
import net.jaumebalmes.grincon17.futchamp.adapters.MyPrevPartidoRecyclerViewAdapter;
import net.jaumebalmes.grincon17.futchamp.fragments.EquipoFragment;
import net.jaumebalmes.grincon17.futchamp.fragments.JugadorFragment;
import net.jaumebalmes.grincon17.futchamp.fragments.LeagueFragment;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnListEquipoInteractionListener;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnListJornadaInteractionListener;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnListJugadorInteractionListener;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnListLeagueInteractionListener;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnListNextPartidoInteractionListener;
import net.jaumebalmes.grincon17.futchamp.models.Calendario;
import net.jaumebalmes.grincon17.futchamp.models.Equipo;
import net.jaumebalmes.grincon17.futchamp.models.Jugador;
import net.jaumebalmes.grincon17.futchamp.models.League;
import net.jaumebalmes.grincon17.futchamp.models.Partido;
import net.jaumebalmes.grincon17.futchamp.repositoryApi.CalendarioReposirotyApi;
import net.jaumebalmes.grincon17.futchamp.repositoryApi.CoordinadorRepositoryApi;
import net.jaumebalmes.grincon17.futchamp.repositoryApi.EquipoRepositoryApi;
import net.jaumebalmes.grincon17.futchamp.repositoryApi.JugadorRepositoryApi;
import net.jaumebalmes.grincon17.futchamp.repositoryApi.LeagueRepositoryApi;
import net.jaumebalmes.grincon17.futchamp.repositoryApi.PartidosRepositoryApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Esta clase Se encargara de realizar la conexion a la API usando la clase Retrofit.
 */
public class Api {

    private Retrofit retrofit;
    private Enlace enlace = new Enlace();
    private LeagueRepositoryApi leagueRepositoryApi;
    private EquipoRepositoryApi equipoRepositoryApi;
    private List<Jugador> jugadorList;

    // Builder
    public Api() {
    }

    // Method

    /**
     * Este metodo devolvera un objeto de tipo Retrofit con los datos de conexion a la API usando
     * un enlace.
     *
     * @param urlEnlace Sera la direccion o enlace ejm => http://192.168.0.23:8080/api/futchamp/jugador/
     * @return Devuelve el objeto Retrofit con la conexion.
     */
    private Retrofit getConexion(String urlEnlace) {
        this.retrofit = new Retrofit.Builder()
                .baseUrl(urlEnlace)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;  // Devuelve un objeto con la conexion a la api

    }

    /**
     * llamada a la API para hacer login
     * @param user nombre de usuario
     * @param pwd contraseña
     */
    public void requestLogin(final String user, final String pwd, final Context context, final Activity activity, final SharedPreferences preferences) {
        final String TAG = "LOGIN";
        retrofit = getConexion(enlace.getLink(enlace.COORDINADOR));
        CoordinadorRepositoryApi coordinadorRepositoryApi = retrofit.create(CoordinadorRepositoryApi.class);
        Call<Boolean> loginSuccess = coordinadorRepositoryApi.verificarAutorizacion(user, pwd);

        // Aqui se realiza la solicitud al servidor de forma asincrónicamente y se obtiene 2 respuestas.
        loginSuccess.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {

                if (response.isSuccessful()) {
                    // Aqui se aplica a la vista los datos obtenidos de la API que estan almacenados en el ArrayList
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(activity.getString(R.string.my_username), user);
                    editor.putString(activity.getString(R.string.my_pwd), pwd);
                    editor.apply();
                    Log.d(TAG, " RESPUESTA DE SEGURIDAD: " + response.body());
                    activity.invalidateOptionsMenu();
                } else {
                    Toast toast = Toast.makeText(context, activity.getString(R.string.login_failed), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 500);
                    toast.show();
                    Log.e(TAG, " NO TIENE AUTORIZACION: onResponse: " + response.errorBody());
                }
            }
            // Aqui, se mostrara si la conexion a la API falla.
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast toast = Toast.makeText(context, "Error en la conexion a la red.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
                Log.e(TAG, " => ERROR VERIFICAR LA CONEXION => onFailure: " + t.getMessage());
            }
        });
    }

    public void obtenerDatosLigas(final View view, final FragmentActivity fragmentActivity,final OnListLeagueInteractionListener mListener) {
        final int COLUMNS = 2;
        final String TAG = "LIGAS";
        retrofit = getConexion(enlace.getLink(enlace.LIGA));
        leagueRepositoryApi = retrofit.create(LeagueRepositoryApi.class);
        Call<ArrayList<League>> leagueAnswerCall = leagueRepositoryApi.obtenerListaLeagues();

        // Aqui se realiza la solicitud al servidor de forma asincrónicamente y se obtiene 2 respuestas.
        leagueAnswerCall.enqueue(new Callback<ArrayList<League>>() {
            // Aqui nos indicara si se realiza una conexion, y esta puede tener 2 tipos de ella
            @Override
            public void onResponse(Call<ArrayList<League>> call, Response<ArrayList<League>> response) {
                // Si la conexion es exitosa esta mostrara la informacion obtenida de la base de datos
                if (response.isSuccessful()) {
                    // Obtiene todos los datos de la BBDD por medio de la api y se almacena en el arrayList
                    List<League> leagueList = response.body();
                    // Aqui se aplica a la vista los datos obtenidos de la API que estan almacenados en el ArrayList

                    RecyclerView recyclerView = (RecyclerView) view;
                    recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), COLUMNS));
                    recyclerView.setAdapter(new MyLeagueRecyclerViewAdapter(fragmentActivity, leagueList, mListener)); // Pasa los datos a la vista de leagues
                    // Muestra los datos que llegan en la consola
                    for (int i = 0; i < leagueList.size(); i++) {
                        Log.e(TAG, "Liga: " + leagueList.get(i).getName());
                    }

                } else {
                    Toast toast = Toast.makeText(view.getContext(), "Error en la descarga.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 500);
                    toast.show();
                    Log.e(TAG, " ERROR AL CARGAR LEAGUES: onResponse" + response.errorBody());
                }
            }

            // Aqui, se mostrara si la conexion a la API falla.
            @Override
            public void onFailure(Call<ArrayList<League>> call, Throwable t) {
                Toast toast = Toast.makeText(view.getContext(), "Error en la conexion a la red: " + t.getMessage(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
                Log.e(TAG, " => ERROR LISTA LEAGUES => onFailure: " + t.getMessage());
            }
        });
    }

    public void obtenerDatosEquipos(final View view, String leagueName, final FragmentActivity fragmentActivity,
                                    final OnListEquipoInteractionListener mListener) {
        final String TAG = "EQUIPO";
        retrofit = getConexion(enlace.getLink(enlace.EQUIPO));
        EquipoRepositoryApi equipoRepositoryApi = retrofit.create(EquipoRepositoryApi.class);
        Call<ArrayList<Equipo>> equipoAnswerCall = equipoRepositoryApi.obtenerListaEquiposPorLiga(leagueName);

        equipoAnswerCall.enqueue(new Callback<ArrayList<Equipo>>() {
            // Aqui nos indicara si se realiza una conexion, y esta puede tener 2 tipos de ella
            @Override
            public void onResponse(Call<ArrayList<Equipo>> call, Response<ArrayList<Equipo>> response) {
                if (response.isSuccessful()) {
                    List<Equipo> equipoList = response.body();
                    // Aqui se aplica a la vista los datos obtenidos de la API que estan almacenados en el ArrayList
                    Context context = view.getContext();
                    RecyclerView recyclerView = (RecyclerView) view;
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new MyEquipoRecyclerViewAdapter(fragmentActivity, equipoList, mListener));
                    // Muestra los datos que llegan en la consola
                    for (int i = 0; i < equipoList.size(); i++) {
                        Log.d(TAG, "Equipo: " + equipoList.get(i).getName());
                    }
                } else {
                    Toast toast = Toast.makeText(view.getContext(), "Error en la descarga.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 500);
                    toast.show();
                    Log.e(TAG, " ERROR AL CARGAR EQUIPOS: onResponse" + response.errorBody());
                }
            }

            // Aqui, se mostrara si la conexion a la API falla.
            @Override
            public void onFailure(Call<ArrayList<Equipo>> call, Throwable t) {
                Toast toast = Toast.makeText(view.getContext(), "Error en la conexion a la red.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
                Log.e(TAG, " => ERROR LISTA EQUIPOS => onFailure: " + t.getMessage());
            }
        });
    }

    public void obtenerDatosJugadores(final View view, final String leagueName, final FragmentActivity fragmentActivity,
                                       final OnListJugadorInteractionListener mListener) {
        final int COLUMNS = 3;
        final String TAG = "JUGADORES";

        retrofit = getConexion(enlace.getLink(enlace.JUGADOR));
        JugadorRepositoryApi jugadorRepositoryApi = retrofit.create(JugadorRepositoryApi.class);
        Call<ArrayList<Jugador>> jugadorAnswerCall = jugadorRepositoryApi.obtenerListaJugadores();

        // Aqui se realiza la solicitud al servidor de forma asincrónicamente y se obtiene 2 respuestas.
        jugadorAnswerCall.enqueue(new Callback<ArrayList<Jugador>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Jugador>> call, @NonNull Response<ArrayList<Jugador>> response) {

                if (response.isSuccessful()) {
                    // Aqui se aplica a la vista los datos obtenidos de la API que estan almacenados en el ArrayList
                    List<Jugador> jugadorList = response.body();
                    List<Jugador> jugadorListLiga = new ArrayList<>();
                    assert jugadorList != null;
                    for (Jugador jugador : jugadorList) {
                        if(jugador.getEquipo().getLeague().getName().equals(leagueName)) {
                            jugadorListLiga.add(jugador);
                        }
                    }
                    Log.d("EQUIPO", String.valueOf(jugadorList.get(0)));
                    Context context = view.getContext();
                    RecyclerView recyclerView = (RecyclerView) view;
                    recyclerView.setLayoutManager(new GridLayoutManager(context, COLUMNS));
                    recyclerView.setAdapter(new MyJugadorRecyclerViewAdapter(fragmentActivity, jugadorListLiga, mListener));
                    // Muestra los datos que llegan en la consola
                    for (int i = 0; i < jugadorList.size(); i++) {
                        Log.e(TAG, "Liga: " + jugadorList.get(i).getNombre());
                    }

                } else {
                    Toast toast = Toast.makeText(view.getContext(), "Error en la descarga.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 500);
                    toast.show();
                    Log.e(TAG, " ERROR AL CARGAR JUGADORES: onResponse" + response.errorBody());
                }
            }

            // Aqui, se mostrara si la conexion a la API falla.
            @Override
            public void onFailure(Call<ArrayList<Jugador>> call, Throwable t) {
                Toast toast = Toast.makeText(view.getContext(), "Error en la conexion a la red.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
                Log.e(TAG, " => ERROR LISTA JUGADORES => onFailure: " + t.getMessage());
            }
        });
    }

    public void obtenerDatosJugadoresPorEquipo(String nombreEquipo, final Context context, final Activity activity,
                                                final OnListJugadorInteractionListener mListener) {
        final String TAG_JUGADOR = "JUGADORES";
        final int COLUMNS = 3;
        retrofit = getConexion(enlace.getLink(enlace.JUGADOR));
        JugadorRepositoryApi jugadorRepositoryApi = retrofit.create(JugadorRepositoryApi.class);
        Call<ArrayList<Jugador>> jugadorAnswerCall = jugadorRepositoryApi.obtenerListaJugadoresEquipo(nombreEquipo);
        jugadorList = new ArrayList<>();
        // Aqui se realiza la solicitud al servidor de forma asincrónicamente y se obtiene 2 respuestas.
        jugadorAnswerCall.enqueue(new Callback<ArrayList<Jugador>>() {
            @Override
            public void onResponse(Call<ArrayList<Jugador>> call, Response<ArrayList<Jugador>> response) {

                if (response.isSuccessful()) {
                    // Aqui se aplica a la vista los datos obtenidos de la API que estan almacenados en el ArrayList
                    jugadorList = response.body();

                    RecyclerView recyclerView = activity.findViewById(R.id.recycler_jugadores_equipo);
                    recyclerView.setLayoutManager(new GridLayoutManager(context, COLUMNS));
                    recyclerView.setAdapter(new MyJugadorRecyclerViewAdapter(context, jugadorList, mListener));
                    // Muestra los datos que llegan en la consola
                    for (int i = 0; i < jugadorList.size(); i++) {
                        Log.e(TAG_JUGADOR, "Liga: " + jugadorList.get(i).getNombre());
                    }

                } else {
                    Toast toast = Toast.makeText(context, activity.getString(R.string.login_failed), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 500);
                    toast.show();
                    Log.e(TAG_JUGADOR, " ERROR AL CARGAR JUGADORES: onResponse" + response.errorBody());
                }
            }

            // Aqui, se mostrara si la conexion a la API falla.
            @Override
            public void onFailure(Call<ArrayList<Jugador>> call, Throwable t) {
                Toast toast = Toast.makeText(context, "Error en la conexion a la red.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
                Log.e(TAG_JUGADOR, " => ERROR LISTA JUGADORES => onFailure: " + t.getMessage());
            }
        });
    }

    public void obtenerDatosPartidosPorEquipo(final Context context, final Activity activity,
                                               final OnListNextPartidoInteractionListener mListener) {
        final String TAG_JUGADOR = "Partido";
        retrofit = getConexion(enlace.getLink(enlace.PARTIDO));
        PartidosRepositoryApi partidosRepositoryApi = retrofit.create(PartidosRepositoryApi.class);
        Call<ArrayList<Partido>> partidoAnswerCall = partidosRepositoryApi.obtenerListaPartidos();

        // Aqui se realiza la solicitud al servidor de forma asincrónicamente y se obtiene 2 respuestas.
        partidoAnswerCall.enqueue(new Callback<ArrayList<Partido>>() {
            @Override
            public void onResponse(Call<ArrayList<Partido>> call, Response<ArrayList<Partido>> response) {

                if (response.isSuccessful()) {
                    // Aqui se aplica a la vista los datos obtenidos de la API que estan almacenados en el ArrayList
                    List<Partido> partidoList = new ArrayList<>();
                    partidoList = response.body();

                    RecyclerView recyclerViewPartidos1 = activity.findViewById(R.id.recycler_partidos1_equipo);
                    RecyclerView recyclerViewPartidos2 = activity.findViewById(R.id.recycler_partidos2_equipo);
                    recyclerViewPartidos1.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false));
                    recyclerViewPartidos2.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false));
                    recyclerViewPartidos1.setAdapter(new MyNextPartidoRecyclerViewAdapter(context, partidoList, mListener));
                    recyclerViewPartidos2.setAdapter(new MyPrevPartidoRecyclerViewAdapter(context, partidoList, mListener));
                    // Muestra los datos que llegan en la consola
                    for (int i = 0; i < jugadorList.size(); i++) {
                        Log.e(TAG_JUGADOR, "Liga: " + jugadorList.get(i).getNombre());
                    }

                } else {
                    Toast toast = Toast.makeText(context, activity.getString(R.string.login_failed), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 500);
                    toast.show();
                    Log.e(TAG_JUGADOR, " ERROR AL CARGAR JUGADORES: onResponse" + response.errorBody());
                }
            }

            // Aqui, se mostrara si la conexion a la API falla.
            @Override
            public void onFailure(Call<ArrayList<Partido>> call, Throwable t) {
                Toast toast = Toast.makeText(context, "Error en la conexion a la red.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
                Log.e(TAG_JUGADOR, " => ERROR LISTA JUGADORES => onFailure: " + t.getMessage());
            }
        });
    }

    /**
     * Llamada a la Api para añadir una liga
     * @param leagueName nombre de la liga
     * @param filePath la ruta de la imagen
     */
    public void postLeague(final String leagueName, Uri filePath, final Context context, final Activity activity, final FragmentManager fragmentManager) {
        if (filePath != null) {
            retrofit = getConexion(enlace.getLink(enlace.LIGA));
            leagueRepositoryApi = retrofit.create(LeagueRepositoryApi.class);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            final StorageReference reference = storage.getReference().child("images/" + UUID.randomUUID().toString());
            reference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("STATE", "carga OK");
                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            String url = task.getResult().toString();
                            Log.i("RESULT", url);
                            League league = new League();
                            league.setName(leagueName);
                            league.setLogo(url);
                            Call<League> addNewLeague = leagueRepositoryApi.postLeague(league);
                            addNewLeague.enqueue(new Callback<League>() {
                                @Override
                                public void onResponse(Call<League> call, Response<League> response) {
                                    if(response.isSuccessful()) {
                                        Log.i("LEAGUE", " RESPUESTA: " + response.body());
                                        fragmentManager.beginTransaction().replace(R.id.fragmentLeagueList, new LeagueFragment()).commit();
                                        Toast.makeText(context, activity.getString(R.string.add_league_success), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e("LEAGUE", "ERROR: " + response.errorBody());
                                    }
                                }

                                @Override
                                public void onFailure(Call<League> call, Throwable t) {
                                    Log.e("LEAGUE", " => ERROR  => onFailure: " + t.getMessage());
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("STATE", "Fallo: " + e.getMessage());
                }
            });
        }
    }

    /**
     * Llamada a la Api para añadir una liga
     * @param equipoName nombre de la liga
     * @param filePath la ruta de la imagen
     */
    public void postEquipo(final String equipoName, final League league, Uri filePath, final Context context, final Activity activity) {
        if (filePath != null) {
            retrofit = getConexion(enlace.getLink(enlace.EQUIPO));
            equipoRepositoryApi = retrofit.create(EquipoRepositoryApi.class);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            final StorageReference reference = storage.getReference().child("images/" + UUID.randomUUID().toString());
            reference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("STATE", "carga OK");
                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            String url = task.getResult().toString();
                            Log.i("RESULT", url);
                            Equipo equipo = new Equipo();
                            equipo.setName(equipoName);
                            equipo.setLogo(url);
                            equipo.setLeague(league);
                            Call<Equipo> addNewEquipo = equipoRepositoryApi.postEquipo(equipo);
                            addNewEquipo.enqueue(new Callback<Equipo>() {
                                @Override
                                public void onResponse(Call<Equipo> call, Response<Equipo> response) {
                                    if(response.isSuccessful()) {
                                        Log.i("EQUIPO", " RESPUESTA: " + response.body());

                                        Toast.makeText(context, activity.getString(R.string.add_league_success), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e("LEAGUE", "ERROR: " + response.errorBody());
                                    }
                                }

                                @Override
                                public void onFailure(Call<Equipo> call, Throwable t) {
                                    Log.e("LEAGUE", " => ERROR  => onFailure: " + t.getMessage());
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("STATE", "Fallo: " + e.getMessage());
                }
            });
        }
    }

    /**
     * Llamada a la Api para añadir un Jugador
     *
     */
    private void addJugador(Uri filePath, final Jugador jugador, final Context context, final Activity activity) {
        if (filePath != null) {

            retrofit = getConexion(enlace.getLink(enlace.JUGADOR));
            final JugadorRepositoryApi JugadorRepositoryApi = retrofit.create(JugadorRepositoryApi.class);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            final StorageReference reference = storage.getReference().child("images/" + UUID.randomUUID().toString());
            reference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("STATE", "carga OK");
                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            String url = task.getResult().toString();
                            Log.i("RESULT", url);
                            jugador.setImagen(url);
                            Call<Jugador> addNewJugador = JugadorRepositoryApi.postJugador(jugador);
                            addNewJugador.enqueue(new Callback<Jugador>() {
                                @Override
                                public void onResponse(Call<Jugador> call, Response<Jugador> response) {
                                    if(response.isSuccessful()) {
                                        Log.i("JUGADOR", " RESPUESTA: " + response.body());
                                        Toast.makeText(context, "Jugador añadido", Toast.LENGTH_SHORT).show();
                                        activity.onBackPressed();

                                    } else {
                                        Log.e("JUGADOR", "ERROR: " + response.errorBody());
                                    }
                                }

                                @Override
                                public void onFailure(Call<Jugador> call, Throwable t) {
                                    Log.e("JUGADOR", " => ERROR  => onFailure: " + t.getMessage());
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("STATE", "Fallo: " + e.getMessage());
                }
            });
        }
    }

    public void postJugador(String nombreEquipo, final Uri filePath, final Jugador jugador, final Context context, final Activity activity) {
        retrofit = getConexion(enlace.getLink(enlace.EQUIPO));
        EquipoRepositoryApi equipoRepositoryApi = retrofit.create(EquipoRepositoryApi.class);
        Call<Equipo> equipoAnswerCall = equipoRepositoryApi.obtenerEquipoPorNombre(nombreEquipo);

        equipoAnswerCall.enqueue(new Callback<Equipo>() {
            // Aqui nos indicara si se realiza una conexion, y esta puede tener 2 tipos de ella
            @Override
            public void onResponse(Call<Equipo> call, Response<Equipo> response) {
                if (response.isSuccessful()) {
                    Equipo equipo = response.body();
                    jugador.setEquipo(equipo);
                    addJugador(filePath, jugador, context, activity);
                } else {
                    Toast toast = Toast.makeText(context, "Equipo no encontrado", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 500);
                    toast.show();
                    Log.e("TAG", " ERROR AL CARGAR EQUIPOS: onResponse" + response.errorBody());
                }
            }

            // Aqui, se mostrara si la conexion a la API falla.
            @Override
            public void onFailure(Call<Equipo> call, Throwable t) {
                Toast toast = Toast.makeText(context, "Error en la conexion a la red.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
                Log.e("TAG", " => ERROR LISTA EQUIPOS => onFailure: " + t.getMessage());
            }
        });
    }

    public void postCalendar(Calendario calendario, final Context context) {
        retrofit = getConexion(enlace.getLink(enlace.CALENDARIO));
        CalendarioReposirotyApi calendarioReposirotyApi = retrofit.create(CalendarioReposirotyApi.class);
        Call<Calendario> calendarioCall = calendarioReposirotyApi.postCalendario(calendario);
        calendarioCall.enqueue(new Callback<Calendario>() {
            @Override
            public void onResponse(Call<Calendario> call, Response<Calendario> response) {
                if(response.isSuccessful()) {
                    Log.i("Calendario", " RESPUESTA_OK: " + response.body());
                    Toast toast = Toast.makeText(context, "Calendario añadido", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 500);
                    toast.show();
                } else {
                    try {
                        assert response.errorBody() != null;
                        Log.e("Calendario", " RESPUESTA_FAILED: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast toast = Toast.makeText(context, "error al añadir calendario", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 500);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<Calendario> call, Throwable t) {
                Toast toast = Toast.makeText(context, "Error en la conexion a la red.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
                Log.e("TAG", " => ERROR CALENDARIO => onFailure: " + t.getMessage());
            }
        });
    }

    public void getPartidosByLeagueCalendar(final View view, final FragmentActivity fragmentActivity,
                                            final OnListJornadaInteractionListener mListener, String nombreLeagueCalendario){
        Log.i("NOMBRE", nombreLeagueCalendario);
        retrofit = getConexion(enlace.getLink(enlace.CALENDARIO));
        CalendarioReposirotyApi calendarioReposirotyApi = retrofit.create(CalendarioReposirotyApi.class);
        Call<Calendario> calendarioCall = calendarioReposirotyApi.obtenerCalendario(nombreLeagueCalendario);
        calendarioCall.enqueue(new Callback<Calendario>() {
            @Override
            public void onResponse(Call<Calendario> call, Response<Calendario> response) {
                if(response.isSuccessful()) {
                    Calendario calendario = response.body();
                    //getPartidos(calendario.getLeague(), calendario.getFecha(),view, fragmentActivity, mListener);
                    Log.i("RESPONSE_OK", response.body().toString());
                } else {
                    Log.i("BAD_RESPONSE", String.valueOf(response.errorBody()));
                }
            }
            @Override
            public void onFailure(Call<Calendario> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    public void getPartidos(/*String nombreLeagueCalendario, String fechaInicio,*/ final View view, final FragmentActivity fragmentActivity,
                                    final OnListJornadaInteractionListener mListener) {
        final String TAG = "PARTIDO";
        retrofit = getConexion(enlace.getLink(enlace.PARTIDO));
        PartidosRepositoryApi partidosRepositoryApi = retrofit.create(PartidosRepositoryApi.class);
        Call<ArrayList<Partido>> equipoAnswerCall = partidosRepositoryApi.obtenerListaPartidos();

        equipoAnswerCall.enqueue(new Callback<ArrayList<Partido>>() {
            // Aqui nos indicara si se realiza una conexion, y esta puede tener 2 tipos de ella
            @Override
            public void onResponse(Call<ArrayList<Partido>> call, Response<ArrayList<Partido>> response) {
                if (response.isSuccessful()) {
                    List<Partido> partidoList = response.body();
                    // Aqui se aplica a la vista los datos obtenidos de la API que estan almacenados en el ArrayList
                    Context context = view.getContext();
                    RecyclerView recyclerView = (RecyclerView) view;
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new MyJornadaRecyclerViewAdapter(fragmentActivity, partidoList, mListener));
                    // Muestra los datos que llegan en la consola
                    for (int i = 0; i < partidoList.size(); i++) {
                        Log.d(TAG, "fecha partidos: " + partidoList.get(i));
                    }
                } else {
                    Toast toast = Toast.makeText(view.getContext(), "Error en la descarga.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 500);
                    toast.show();
                    Log.e(TAG, " ERROR AL CARGAR PARTIDOS: onResponse" + response.errorBody());
                }
            }

            // Aqui, se mostrara si la conexion a la API falla.
            @Override
            public void onFailure(Call<ArrayList<Partido>> call, Throwable t) {
                Toast toast = Toast.makeText(view.getContext(), "Error en la conexion a la red.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
                Log.e(TAG, " => ERROR LISTA PARTIDOS => onFailure: " + t.getMessage());
            }
        });
    }

    public void deleteLiga(long id, final Context context, final FragmentManager fragmentManager) {

        retrofit = getConexion(enlace.getLink(enlace.LIGA));
        LeagueRepositoryApi leagueRepositoryApi = retrofit.create(LeagueRepositoryApi.class);
        Call<League> answerDeleteLeague = leagueRepositoryApi.deleteLeague(id);
        answerDeleteLeague.enqueue(new Callback<League>() {
            @Override
            public void onResponse(Call<League> call, Response<League> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(context, "Liga eliminada", Toast.LENGTH_SHORT).show();
                    fragmentManager.beginTransaction().replace(R.id.fragmentLeagueList, new LeagueFragment()).commit();
                }
            }

            @Override
            public void onFailure(Call<League> call, Throwable t) {

            }
        });

    }

    public void deleteEquipo(long id, final Context context, final FragmentManager fragmentManager) {

        retrofit = getConexion(enlace.getLink(enlace.EQUIPO));
        EquipoRepositoryApi equipoRepositoryApi = retrofit.create(EquipoRepositoryApi.class);
        Call<Equipo> answerDeleteEquipo = equipoRepositoryApi.deleteEquipo(id);
        answerDeleteEquipo.enqueue(new Callback<Equipo>() {
            @Override
            public void onResponse(Call<Equipo> call, Response<Equipo> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(context, "Equipo eliminado", Toast.LENGTH_SHORT).show();
                    fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, new EquipoFragment()).commit();
                }
            }

            @Override
            public void onFailure(Call<Equipo> call, Throwable t) {

            }
        });
    }

    public void deleteJugador(long id, final Context context, final FragmentManager fragmentManager, final Activity activity) {
        retrofit = getConexion(enlace.getLink(enlace.JUGADOR));
        JugadorRepositoryApi jugadorRepositoryApi = retrofit.create(JugadorRepositoryApi.class);
        Call<Jugador> answerDeleteJugador = jugadorRepositoryApi.deleteJugador(id);
        answerDeleteJugador.enqueue(new Callback<Jugador>() {
            @Override
            public void onResponse(Call<Jugador> call, Response<Jugador> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(context, "Jugador eliminado", Toast.LENGTH_SHORT).show();
                    if (fragmentManager != null) {
                        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, new JugadorFragment()).commit();
                    } else {
                        activity.onBackPressed();
                    }
                }
            }

            @Override
            public void onFailure(Call<Jugador> call, Throwable t) {

            }
        });
    }
}
