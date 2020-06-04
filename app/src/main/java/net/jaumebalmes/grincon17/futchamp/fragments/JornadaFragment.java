package net.jaumebalmes.grincon17.futchamp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import net.jaumebalmes.grincon17.futchamp.R;
import net.jaumebalmes.grincon17.futchamp.conexion.Api;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnListJornadaInteractionListener;

/**
 * Contenedor de la vista de Jornadas
 * @author guillermo
 */
public class JornadaFragment extends Fragment {

    private OnListJornadaInteractionListener mListener;
    private String leagueName;
    private Api api;
    private View view;

    public JornadaFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            leagueName = getArguments().getString("LEAGUE");
            Log.d("LEAGUE_NAME", leagueName);
        }

        api = new Api();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (view instanceof RecyclerView) {
            api.getPartidos(view, getActivity(), mListener);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_jornada_list, container, false);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnListJornadaInteractionListener) {
            mListener = (OnListJornadaInteractionListener) context;
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
}
