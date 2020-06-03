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
import net.jaumebalmes.grincon17.futchamp.interfaces.OnListJornadaInteractionListener;
import net.jaumebalmes.grincon17.futchamp.models.Partido;

import java.util.List;

/**
 * Adaptador para la vista de la lista de jornadas
 * @author guillermo
 */
public class MyJornadaRecyclerViewAdapter extends RecyclerView.Adapter<MyJornadaRecyclerViewAdapter.ViewHolder> {

    private final List<Partido> mValues;
    private final OnListJornadaInteractionListener mListener;
    private final Context mContent;

    public MyJornadaRecyclerViewAdapter(Context context, List<Partido> items, OnListJornadaInteractionListener listener) {
        mContent = context;
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_jornada, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        String fechaInvertida = holder.mItem.getFecha();
        String [] splitFecha = fechaInvertida.split("-");
        String fecha = splitFecha[2] + "-" + splitFecha[1] + "-" + splitFecha[0];
        holder.mFechaView.setText(fecha);
        String horaSegundos = holder.mItem.getHora();
        String [] splitHora = horaSegundos.split(":");
        String hora = splitHora[0] + ":" + splitHora[1];
        holder.mHoraView.setText(hora);
        holder.mLocalView.setText(holder.mItem.getLocal().getName());
        holder.mVisitanteView.setText(holder.mItem.getVisitante().getName());
        holder.mJornadaNumView.setText(String.valueOf(holder.mItem.getJornada()));
        loadImg(holder.mItem.getLocal().getLogo(), holder.mLocalLogoView);
        loadImg(holder.mItem.getVisitante().getLogo(), holder.mVisitanteLogoView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onJornadaClickListener(holder.mItem);
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
                .error(R.mipmap.ic_launcher)
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
        final TextView mFechaView;
        final TextView mHoraView;
        final TextView mLocalView;
        final TextView mVisitanteView;
        final TextView mJornadaNumView;
        final ImageView mLocalLogoView;
        final ImageView mVisitanteLogoView;
        Partido mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mJornadaNumView = view.findViewById(R.id.textViewJornadaNum);
            mFechaView = view.findViewById(R.id.text_partido_fecha);
            mHoraView = view.findViewById(R.id.text_partido_hora);
            mLocalView = view.findViewById(R.id.text_equipo_local);
            mVisitanteView = view.findViewById(R.id.text_equipo_visitante);
            mLocalLogoView = view.findViewById(R.id.imageViewLogoLocal);
            mVisitanteLogoView = view.findViewById(R.id.imageViewLogoVisitante);
        }
    }
}
