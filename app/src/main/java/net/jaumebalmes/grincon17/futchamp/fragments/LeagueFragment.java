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

import com.google.gson.Gson;

import net.jaumebalmes.grincon17.futchamp.R;
import net.jaumebalmes.grincon17.futchamp.adapters.MyLeagueRecyclerViewAdapter;
import net.jaumebalmes.grincon17.futchamp.conexion.Api;
import net.jaumebalmes.grincon17.futchamp.conexion.Enlace;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnListLeagueInteractionListener;
import net.jaumebalmes.grincon17.futchamp.models.League;
import net.jaumebalmes.grincon17.futchamp.repositoryApi.LeagueRepositoryApi;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

    private int mColumnCount = 2;
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

        leagueList = new ArrayList<>();


        for (int i = 0; i < leagueList.size(); i++) {
            Log.e(TAG, "AAAAAA: " + leagueList.get(i).getLogo());
        }

        // TODO: esto hay que sustituirlo por retrofit
//        try {
//            // Aqui se obtiene las url para las imagenes
//            InputStream stream = requireActivity().getAssets().open("leagues.json"); // se agrega el archivo que contien las url
//
//            int size = stream.available();
//            byte[] buffer = new byte[size];
//            stream.read(buffer);
//            String json = new String(buffer);
//            leagueList = Arrays.asList(new Gson().fromJson(json, League[].class));
//            stream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_league_list, container, false);
        if (view instanceof RecyclerView) {
            obtenerDatosLigas(); // Trae los datos de la API
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            recyclerView.setAdapter(new MyLeagueRecyclerViewAdapter(getActivity(), leagueList, mListener)); // pasa los datos a la vista de ligas
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

    private void obtenerDatosLigas() {
        // Se instancia la interfaz y se le aplica el objeto(retrofit) con la conexion para obtener los datos.
        LeagueRepositoryApi leagueRepositoryApi = retrofitLeague.create(LeagueRepositoryApi.class);
        // Se realiza la llamada al metodo para obtener los datos y se almacena la respuesta aqui.
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


                    // Muestra los datos que llegan en la consola
                    for (int i = 0; i < leagueList.size(); i++) {
                        Log.e(TAG, "Liga: " + leagueList.get(i).getName());
                    }


                } else {
                    Toast toast = Toast.makeText(getContext(), "Error en la descarga.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 1400);
                    toast.show();
                    Log.e(TAG, " ERROR AL CARGAR LEAGUES: onResponse" + response.errorBody());
                }
            }

            // Aqui, se mostrara si la conexion a la API falla.
            @Override
            public void onFailure(Call<ArrayList<League>> call, Throwable t) {
                Toast toast = Toast.makeText(getContext(), "Error en la conexion a la red.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 1400);
                toast.show();
                Log.e(TAG, " => ERROR LISTA LEAGUES => onFailure: " + t.getMessage());
            }
        });


    }


}
