package net.jaumebalmes.grincon17.futchamp.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.jaumebalmes.grincon17.futchamp.R;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnAddResultadoDialogListener;
import net.jaumebalmes.grincon17.futchamp.models.Partido;

public class AddResultadoDialogFragment extends DialogFragment {

    private OnAddResultadoDialogListener mListener;
    private Partido mPartido;
    private Context mContext;
   public AddResultadoDialogFragment(Partido partido) {
       mPartido = partido;
   }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_resultado_dialog, null);
        final EditText resultadoLocal = view.findViewById(R.id.textViewResultadoLocal);
        final EditText resultadoVisitante = view.findViewById(R.id.textViewResultadoVisitante);
        ImageView logoLocal = view.findViewById(R.id.imageViewLocal);
        ImageView logoVisitante = view.findViewById(R.id.imageViewVisitante);
        loadImg(mPartido.getLocal().getLogo(), logoLocal);
        loadImg(mPartido.getVisitante().getLogo(), logoVisitante);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);
        builder.setPositiveButton(R.string.add_league, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String local =  String.valueOf(resultadoLocal.getText());
                String visitante = String.valueOf(resultadoVisitante.getText());
                if(!TextUtils.isEmpty(local) && !TextUtils.isEmpty(visitante)) {
                    mListener.onAddResultadoClickListener(Integer.parseInt(local), Integer.parseInt(visitante), mPartido);
                }
            }
        }) .setNegativeButton(R.string.cancel_txt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
       mContext = context;
        super.onAttach(context);
        try {
            mListener = (OnAddResultadoDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    private void loadImg(String url, ImageView imageView) {
        Glide.with(mContext)
                .load(url)
                .error(R.mipmap.ic_launcher)
                .centerInside()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
}
