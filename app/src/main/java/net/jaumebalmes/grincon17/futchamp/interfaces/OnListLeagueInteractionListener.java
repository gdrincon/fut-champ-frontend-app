package net.jaumebalmes.grincon17.futchamp.interfaces;

import android.widget.ImageView;

import net.jaumebalmes.grincon17.futchamp.models.League;

public interface OnListLeagueInteractionListener {
    void onLeagueClickListener(League league);

    void onLeagueLongClickListener(League league);
}
