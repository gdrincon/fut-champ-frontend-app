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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.jaumebalmes.grincon17.futchamp.R;
import net.jaumebalmes.grincon17.futchamp.adapters.MyEquipoRecyclerViewAdapter;
import net.jaumebalmes.grincon17.futchamp.conexion.Api;
import net.jaumebalmes.grincon17.futchamp.conexion.Enlace;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnListEquipoInteractionListener;
import net.jaumebalmes.grincon17.futchamp.models.Equipo;
import net.jaumebalmes.grincon17.futchamp.repositoryApi.EquipoRepositoryApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Contenedor de la vista de equipos
 *
 * @author guillermo
 */
public class EquipoFragment extends Fragment {

    private static final String TAG = "EQUIPO"; //  Para mostrar mensajes por consola

    private List<Equipo> equipoList;
    private OnListEquipoInteractionListener mListener;
    private String leagueName;
    private Retrofit retrofitEquipo;

    public EquipoFragment() {
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
        retrofitEquipo = api.getConexion(enlace.getLink(enlace.EQUIPO));

        equipoList = new ArrayList<>(); // Para almacenar los datos de los equipos
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equipo_list, container, false);
        if (view instanceof RecyclerView) {
            obtenerDatosEquipos(view);

        }
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnListEquipoInteractionListener) {
            mListener = (OnListEquipoInteractionListener) context;
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

    private void obtenerDatosEquipos(final View view) {

        EquipoRepositoryApi equipoRepositoryApi = retrofitEquipo.create(EquipoRepositoryApi.class);
        Call<ArrayList<Equipo>> equipoAnswerCall = equipoRepositoryApi.obtenerListaEquiposPorLiga(leagueName);

        equipoAnswerCall.enqueue(new Callback<ArrayList<Equipo>>() {
            // Aqui nos indicara si se realiza una conexion, y esta puede tener 2 tipos de ella
            @Override
            public void onResponse(Call<ArrayList<Equipo>> call, Response<ArrayList<Equipo>> response) {
                if (response.isSuccessful()) {
                    equipoList = response.body();
                    // Aqui se aplica a la vista los datos obtenidos de la API que estan almacenados en el ArrayList
                    Context context = view.getContext();
                    RecyclerView recyclerView = (RecyclerView) view;
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new MyEquipoRecyclerViewAdapter(getActivity(), equipoList, mListener));
                    // Muestra los datos que llegan en la consola
                    for (int i = 0; i < equipoList.size(); i++) {
                        Log.d(TAG, "Equipo: " + equipoList.get(i).getName());
                    }
                } else {
                    Toast toast = Toast.makeText(getContext(), "Error en la descarga.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 500);
                    toast.show();
                    Log.e(TAG, " ERROR AL CARGAR EQUIPOS: onResponse" + response.errorBody());
                }
            }

            // Aqui, se mostrara si la conexion a la API falla.
            @Override
            public void onFailure(Call<ArrayList<Equipo>> call, Throwable t) {
                Toast toast = Toast.makeText(getContext(), "Error en la conexion a la red.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 500);
                toast.show();
                Log.e(TAG, " => ERROR LISTA EQUIPOS => onFailure: " + t.getMessage());
            }
        });
    }


}
