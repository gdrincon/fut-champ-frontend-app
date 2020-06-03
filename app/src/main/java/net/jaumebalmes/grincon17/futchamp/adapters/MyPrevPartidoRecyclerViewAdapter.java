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

public class MyPrevPartidoRecyclerViewAdapter extends RecyclerView.Adapter<MyPrevPartidoRecyclerViewAdapter.ViewHolder> {

    private final List<Partido> mValues;
    private final OnListNextPartidoInteractionListener mListener;
    private final Context mContent;

    public MyPrevPartidoRecyclerViewAdapter(Context context, List<Partido> items, OnListNextPartidoInteractionListener listener){
        mContent = context;
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_partido_jugado, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        int randLocal = (int) (Math.random() * 4);
        int randVisit = (int) (Math.random() * 4);
        holder.resultLocal.setText(String.valueOf(randLocal));
        holder.resultVisitante.setText(String.valueOf(randVisit));
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
        TextView resultLocal;
        TextView resultVisitante;
        ImageView logoLocal;
        ImageView logoVisitante;
        Partido mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            resultLocal = view.findViewById(R.id.text_partido_resultado_local);
            resultVisitante = view.findViewById(R.id.text_partido_Resultado_visitante);
            logoLocal = view .findViewById(R.id.imageViewLogoLocal);
            logoVisitante = view .findViewById(R.id.imageViewLogoVisitante);
        }
    }
}
