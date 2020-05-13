package net.jaumebalmes.grincon17.futchamp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import net.jaumebalmes.grincon17.futchamp.R;

public class JornadaFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_jornada, container, false);
        TextView textView = view.findViewById(R.id.text_home);
        textView.setText("Jornada");
        return view;
    }
}
