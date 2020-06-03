package net.jaumebalmes.grincon17.futchamp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import net.jaumebalmes.grincon17.futchamp.R;
import net.jaumebalmes.grincon17.futchamp.adapters.MyJornadaRecyclerViewAdapter;
import net.jaumebalmes.grincon17.futchamp.adapters.MyJugadorRecyclerViewAdapter;
import net.jaumebalmes.grincon17.futchamp.conexion.Api;
import net.jaumebalmes.grincon17.futchamp.conexion.Enlace;
import net.jaumebalmes.grincon17.futchamp.conexion.Firebase;
import net.jaumebalmes.grincon17.futchamp.fragments.AddEquipoDialogFragment;
import net.jaumebalmes.grincon17.futchamp.fragments.AddLeagueDialogFragment;
import net.jaumebalmes.grincon17.futchamp.fragments.LeagueFragment;
import net.jaumebalmes.grincon17.futchamp.fragments.LoginDialogFragment;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnAddEquipoDialogListener;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnAddLeagueDialogListener;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnListJornadaInteractionListener;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnListJugadorInteractionListener;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnListPartidoInteractionListener;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnLoginDialogListener;
import net.jaumebalmes.grincon17.futchamp.models.Equipo;
import net.jaumebalmes.grincon17.futchamp.models.Jugador;
import net.jaumebalmes.grincon17.futchamp.models.League;
import net.jaumebalmes.grincon17.futchamp.models.Partido;
import net.jaumebalmes.grincon17.futchamp.repositoryApi.CoordinadorRepositoryApi;
import net.jaumebalmes.grincon17.futchamp.repositoryApi.JugadorRepositoryApi;
import net.jaumebalmes.grincon17.futchamp.repositoryApi.LeagueRepositoryApi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Esta activity muestra la vista del detalle de un equipo.
 * @author guillermo
 */
public class EquipoDetailActivity extends AppCompatActivity implements OnLoginDialogListener {

    private SharedPreferences preferences;
    private Equipo equipo;
    private boolean longClick;
    private Api api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipo_detail);
        new Firebase().authFirebaseUser();
        api = new Api(); // para obtener la conexion a la API
        Gson gson = new Gson();
        equipo = gson.fromJson(getIntent().getStringExtra(getString(R.string.equipo_json)), Equipo.class);
        String nombreEquipo = equipo.getName();
        toolbarConf();
        OnListJugadorInteractionListener mListener = new OnListJugadorInteractionListener() {
            @Override
            public void onJugadorClickListener(Jugador jugador) {
                String json = new Gson().toJson(jugador);
                Intent sendJugador = new Intent(EquipoDetailActivity.this, JugadorDetailActivity.class);
                sendJugador.putExtra(getString(R.string.jugador_json), json);
                startActivity(sendJugador);
            }

            @Override
            public void onJugadorLongClickListener(Jugador jugador) {
                longClick = true;
                invalidateOptionsMenu();
            }
        };
        OnListPartidoInteractionListener partidoInteractionListener = new OnListPartidoInteractionListener() {
            @Override
            public void onPartidoClickListener(Partido partido) {

            }
        };
        api.obtenerDatosPartidosPorEquipo(getApplicationContext(), this, partidoInteractionListener);
        ImageView imageViewLogo = findViewById(R.id.imageViewEquipoLogo);
        loadImg(equipo.getLogo(), imageViewLogo);
        api.obtenerDatosJugadoresPorEquipo(nombreEquipo, getApplicationContext(), this, mListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        preferences = getSharedPreferences(getString(R.string.my_pref), Context.MODE_PRIVATE);
        invalidateOptionsMenu();
    }

    /**
     * Configuración del toolbar
     */
    private void toolbarConf() {
        Toolbar toolbar = findViewById(R.id.toolbar_detail_view);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView toolbarImg = findViewById(R.id.toolbar_image);
        TextView toolbarTittle = findViewById(R.id.textViewTitle);
        TextView toolbarSubTitle = findViewById(R.id.textViewSubTitle);
        toolbarTittle.setText(equipo.getName());
        toolbarSubTitle.setText(equipo.getLeague().getName());
        loadImg(equipo.getLogo(), toolbarImg);
    }

    /**
     * @param url       de la imagen
     * @param imageView la vista para poner la imagen
     */
    private void loadImg(String url, ImageView imageView) {
        Glide.with(getApplicationContext())
                .load(url)
                .error(R.mipmap.ic_launcher)
                .centerInside()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (preferences.contains(getString(R.string.my_username)) && preferences.contains(getString(R.string.my_username))) {
            menu.clear();
            inflater.inflate(R.menu.toolbar_coordinator_menu, menu);
            menu.removeItem(R.id.add_calendar);
            menu.removeItem(R.id.add_league);
            menu.removeItem(R.id.add_team);
            if (longClick) {
                menu.findItem(R.id.search_icon).setVisible(false);
                menu.findItem(R.id.trash_icon).setVisible(true);
                menu.findItem(R.id.edit_icon).setVisible(true);

                menu.findItem(R.id.add_player).setVisible(false);
                menu.findItem(R.id.logout).setVisible(false);
            } else {
                menu.findItem(R.id.search_icon).setVisible(true);
                menu.findItem(R.id.trash_icon).setVisible(false);
                menu.findItem(R.id.edit_icon).setVisible(false);
                menu.findItem(R.id.add_player).setVisible(true);
                menu.findItem(R.id.logout).setVisible(true);
            }
        } else {
            inflater.inflate(R.menu.toolbar_login_menu, menu);
        }
        return true;
    }

    /**
     * Este método sirve para elegir un elemento del menú
     *
     * @param item los elementos del menú
     * @return el padre
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_icon:
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.trash_icon:
                // TODO: implementar borrar
                longClick = false;
                invalidateOptionsMenu();
                return true;
            case R.id.edit_icon:
                // TODO: implementar editar
                longClick = false;
                invalidateOptionsMenu();
                return true;
            case R.id.account_login:
                LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
                loginDialogFragment.show(getSupportFragmentManager(), getString(R.string.login_txt));
                return true;
            case R.id.add_league:
                AddLeagueDialogFragment addLeagueDialogFragment = new AddLeagueDialogFragment();
                addLeagueDialogFragment.show(getSupportFragmentManager(), getString(R.string.add_new_league));
                return true;
            case R.id.add_team:
                AddEquipoDialogFragment addEquipoDialogFragment = new AddEquipoDialogFragment();
                addEquipoDialogFragment.show(getSupportFragmentManager(), getString(R.string.add_new_team));
                return true;

            case R.id.add_player:
                startActivity(new Intent(this, AddJugadorActivity.class));
                return true;
            case R.id.logout:
                preferences.edit().remove(getString(R.string.my_username)).apply();
                preferences.edit().remove(getString(R.string.my_pwd)).apply();
                invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onLoginClickListener(String userName, String pwd) {
        api.requestLogin(userName, pwd, getApplicationContext(), this, preferences);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
