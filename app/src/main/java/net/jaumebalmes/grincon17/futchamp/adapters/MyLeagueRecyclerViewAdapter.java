package net.jaumebalmes.grincon17.futchamp.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.jaumebalmes.grincon17.futchamp.R;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnListLeagueInteractionListener;
import net.jaumebalmes.grincon17.futchamp.models.League;

import java.util.List;


/**
 * Adaptador para la vista de la lista de ligas
 * @author guillermo
 */
public class MyLeagueRecyclerViewAdapter extends RecyclerView.Adapter<MyLeagueRecyclerViewAdapter.ViewHolder> {

    private final List<League> mValues;
    private final OnListLeagueInteractionListener mListener;
    private final Context mContent;

    public MyLeagueRecyclerViewAdapter(Context context, List<League> items, OnListLeagueInteractionListener listener) {
        mContent = context;
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_league, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(holder.mItem.getName()); // Asigna el nombre de la liga

        loadImg(holder.mItem.getLogo(), holder.mLogoView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onLeagueClickListener(holder.mItem);
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != mListener) {
                    mListener.onLeagueLongClickListener(holder.mItem);

                    if(holder.mCheckedItem.getVisibility() != View.VISIBLE) {
                        holder.mCheckedItem.setVisibility(View.VISIBLE);
                    } else {
                        holder.mCheckedItem.setVisibility(View.INVISIBLE);
                    }
                }
                return true;
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
        final TextView mNameView;
        final ImageView mLogoView;
        final ImageView mCheckedItem;
        League mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = view.findViewById(R.id.textViewLeagueName);
            mLogoView = view.findViewById(R.id.imageViewLogo);
            mCheckedItem = view.findViewById(R.id.imageViewChecked);
        }

    }
}
