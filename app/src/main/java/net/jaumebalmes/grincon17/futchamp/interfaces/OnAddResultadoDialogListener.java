package net.jaumebalmes.grincon17.futchamp.interfaces;

import net.jaumebalmes.grincon17.futchamp.models.Partido;

public interface OnAddResultadoDialogListener {
    void onAddResultadoClickListener(int resultadoLocal, int resultadoVisitante, Partido partido);
}
