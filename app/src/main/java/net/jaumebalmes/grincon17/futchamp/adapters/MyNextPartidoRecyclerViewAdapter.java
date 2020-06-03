package net.jaumebalmes.grincon17.futchamp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.jaumebalmes.grincon17.futchamp.R;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnListNextPartidoInteractionListener;
import net.jaumebalmes.grincon17.futchamp.models.Equipo;
import net.jaumebalmes.grincon17.futchamp.models.Partido;

import java.util.List;

public class MyNextPartidoRecyclerViewAdapter extends RecyclerView.Adapter<MyNextPartidoRecyclerViewAdapter.ViewHolder> {

    private final List<Partido> mValues;
    private final OnListNextPartidoInteractionListener mListener;
    private final Context mContent;

    public MyNextPartidoRecyclerViewAdapter(Context context, List<Partido> items, OnListNextPartidoInteractionListener listener){
        mContent = context;
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_proximo_partido, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        String fechaInvertida = holder.mItem.getFecha();
        String [] splitFecha = fechaInvertida.split("-");
        String fecha = splitFecha[2] + "-" + splitFecha[1] + "-" + splitFecha[0];
        holder.fecha.setText(fecha);
        String horaSegundos = holder.mItem.getHora();
        String [] splitHora = horaSegundos.split(":");
        String hora = splitHora[0] + ":" + splitHora[1];
        holder.hora.setText(hora);
        Equipo equipoLocal = holder.mItem.getLocal();
        Equipo equipoVisitante = holder.mItem.getVisitante();

        loadImg(equipoLocal.getLogo(), holder.logoLocal);
        loadImg(equipoVisitante.getLogo(), holder.logoVisitante);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onNextPartidoClickListener(holder.mItem);
                }
            }
        });
    }

    /**
     *
     * @param url de la imagen
     * @param imageView la vista para poner la imagen
     */
    private void loadImg(String url, ImageView imageView) {
        Glide.with(mContent)
                .load(url)
                .error(R.drawable.ic_jersey_purple)
                .centerInside() //
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        TextView fecha;
        TextView hora;
        ImageView logoLocal;
        ImageView logoVisitante;
        Partido mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            fecha = view.findViewById(R.id.text_partido_fecha);
            hora = view.findViewById(R.id.text_partido_hora);
            logoLocal = view .findViewById(R.id.imageViewLogoLocal);
            logoVisitante = view .findViewById(R.id.imageViewLogoVisitante);
        }
    }
}
