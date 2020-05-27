package net.jaumebalmes.grincon17.futchamp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.jaumebalmes.grincon17.futchamp.R;
import net.jaumebalmes.grincon17.futchamp.adapters.MyJugadorRecyclerViewAdapter;
import net.jaumebalmes.grincon17.futchamp.conexion.Api;
import net.jaumebalmes.grincon17.futchamp.conexion.Enlace;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnListJugadorInteractionListener;
import net.jaumebalmes.grincon17.futchamp.models.Jugador;
import net.jaumebalmes.grincon17.futchamp.repositoryApi.JugadorRepositoryApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Contenedor de la vista de Jugadores
 *
 * @author guillermo
 */
public class JugadorFragment extends Fragment {

    private static final String TAG = "JUGADOR"; //  Para mostrar mensajes por consola
    private static final int COLUMNS = 3;

    private List<Jugador> jugadorList;
    private OnListJugadorInteractionListener mListener;
    private String leagueName;
    private Retrofit retrofitJugador;

    public JugadorFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            leagueName = getArguments().getString("LEAGUE");
            Log.d("LEAGUE_NAME", leagueName);
        }
        Enlace enlace = new Enlace(); // para obtener los enlaces de conexion a la api
        Api api = new Api(); // para obtener la conexion a la API
        retrofitJugador = api.getConexion(enlace.getLink(enlace.JUGADOR));

        jugadorList = new ArrayList<>(); // Para almacenar los datos de los jugadores
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jugador_list, container, false);
        if (view instanceof RecyclerView) {
            obtenerDatosJugadores(view); // Llama a la API para obtener los datos de los juagdores
        }
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnListJugadorInteractionListener) {
            mListener = (OnListJugadorInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // =============================================================================================
    // =============================================================================================
    // CONEXION A LA API
    private void obtenerDatosJugadores(final View view) {
        JugadorRepositoryApi jugadorRepositoryApi = retrofitJugador.create(JugadorRepositoryApi.class);
        Call<ArrayList<Jugador>> jugadorAnswerCall = jugadorRepositoryApi.obtenerListaJugadores();

        // Aqui se realiza la solicitud al servidor de forma asincrónicamente y se obtiene 2 respuestas.
        jugadorAnswerCall.enqueue(new Callback<ArrayList<Jugador>>() {
            @Override
            public void onResponse(Call<ArrayList<Jugador>> call, Response<ArrayList<Jugador>> response) {

                if (response.isSuccessful()) {
                    // Aqui se aplica a la vista los datos obtenidos de la API que estan almacenados en el ArrayList
                    jugadorList = response.body();
                    List<Jugador> jugadorListLiga = new ArrayList<>();
                    for (Jugador jugador : jugadorList) {
                        if(jugador.getEquipo().getLeague().getName().equals(leagueName)) {
                            jugadorListLiga.add(jugador);
                        }
                    }
                    Log.d("EQUIPO", String.valueOf(jugadorList.get(0)));
                    Context context = view.getContext();
                    RecyclerView recyclerView = (RecyclerView) view;
                    recyclerView.setLayoutManager(new GridLayoutManager(context, COLUMNS));
                    recyclerView.setAdapter(new MyJugadorRecyclerViewAdapter(getActivity(), jugadorListLiga, mListener));
                    // Muestra los datos que llegan en la consola
                    for (int i = 0; i < jugadorList.size(); i++) {
                        Log.e(TAG, "Liga: " + jugadorList.get(i).getNombre());
                    }

                } else {
                    Toast toast = Toast.makeText(getContext(), "Error en la descarga.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 500);
                    toast.show();
                    Log.e(TAG, " ERROR AL CARGAR JUGADORES: onResponse" + response.errorBody());
                }
            }

            // Aqui, se mostrara si la conexion a la API falla.
            @Override
            public void onFailure(Call<ArrayList<Jugador>> call, Throwable t) {
                Toast toast = Toast.makeText(getContext(), "Error en la conexion a la red.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
                Log.e(TAG, " => ERROR LISTA JUGADORES => onFailure: " + t.getMessage());
            }
        });
    }
}
