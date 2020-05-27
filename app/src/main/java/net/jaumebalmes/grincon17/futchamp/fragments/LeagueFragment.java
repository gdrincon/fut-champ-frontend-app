package net.jaumebalmes.grincon17.futchamp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.jaumebalmes.grincon17.futchamp.R;
import net.jaumebalmes.grincon17.futchamp.adapters.MyLeagueRecyclerViewAdapter;
import net.jaumebalmes.grincon17.futchamp.conexion.Api;
import net.jaumebalmes.grincon17.futchamp.conexion.Enlace;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnListLeagueInteractionListener;
import net.jaumebalmes.grincon17.futchamp.models.League;
import net.jaumebalmes.grincon17.futchamp.repositoryApi.LeagueRepositoryApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Contenedor de la vista de ligas
 *
 * @author guillermo
 */
public class LeagueFragment extends Fragment {

    private static final String TAG = "LEAGUE"; //  Para mostrar mensajes por consola
    private static final int COLUMNS = 2;

    private List<League> leagueList;
    private OnListLeagueInteractionListener mListener;

    private Retrofit retrofitLeague;

    public LeagueFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Enlace enlace = new Enlace(); // para obtener los enlaces de conexion a la api
        Api api = new Api(); // para obtener la conexion a la API
        retrofitLeague = api.getConexion(enlace.getLink(enlace.LIGA));

        leagueList = new ArrayList<>(); // Para almacenar los datos de las leagues
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_league_list, container, false);
        if (view instanceof RecyclerView) {
            obtenerDatosLigas(view); // Llama a la API para obtener los datos de la league

        }
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnListLeagueInteractionListener) {
            mListener = (OnListLeagueInteractionListener) context;
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

    //    private void obtenerDatosLigas(final View view, Call<ArrayList<League>> leagueAnswerCall) {
    private void obtenerDatosLigas(final View view) {
        LeagueRepositoryApi leagueRepositoryApi = retrofitLeague.create(LeagueRepositoryApi.class);
        Call<ArrayList<League>> leagueAnswerCall = leagueRepositoryApi.obtenerListaLeagues();

        // Aqui se realiza la solicitud al servidor de forma asincrónicamente y se obtiene 2 respuestas.
        leagueAnswerCall.enqueue(new Callback<ArrayList<League>>() {
            // Aqui nos indicara si se realiza una conexion, y esta puede tener 2 tipos de ella
            @Override
            public void onResponse(Call<ArrayList<League>> call, Response<ArrayList<League>> response) {
                // Si la conexion es exitosa esta mostrara la informacion obtenida de la base de datos
                if (response.isSuccessful()) {
                    // Obtiene todos los datos de la BBDD por medio de la api y se almacena en el arrayList
                    leagueList = response.body();
                    // Aqui se aplica a la vista los datos obtenidos de la API que estan almacenados en el ArrayList
                    Context context = view.getContext();
                    RecyclerView recyclerView = (RecyclerView) view;
                    recyclerView.setLayoutManager(new GridLayoutManager(context, COLUMNS));
                    recyclerView.setAdapter(new MyLeagueRecyclerViewAdapter(getActivity(), leagueList, mListener)); // Pasa los datos a la vista de leagues
                    // Muestra los datos que llegan en la consola
                    for (int i = 0; i < leagueList.size(); i++) {
                        Log.e(TAG, "Liga: " + leagueList.get(i).getName());
                    }

                } else {
                    Toast toast = Toast.makeText(getContext(), "Error en la descarga.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 500);
                    toast.show();
                    Log.e(TAG, " ERROR AL CARGAR LEAGUES: onResponse" + response.errorBody());
                }
            }

            // Aqui, se mostrara si la conexion a la API falla.
            @Override
            public void onFailure(Call<ArrayList<League>> call, Throwable t) {
                Toast toast = Toast.makeText(getContext(), "Error en la conexion a la red: " + t.getMessage(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
                Log.e(TAG, " => ERROR LISTA LEAGUES => onFailure: " + t.getMessage());
            }
        });
    }
}

