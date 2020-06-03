package net.jaumebalmes.grincon17.futchamp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import net.jaumebalmes.grincon17.futchamp.conexion.Api;
import net.jaumebalmes.grincon17.futchamp.conexion.Enlace;
import net.jaumebalmes.grincon17.futchamp.conexion.Firebase;
import net.jaumebalmes.grincon17.futchamp.fragments.AddEquipoDialogFragment;
import net.jaumebalmes.grincon17.futchamp.fragments.AddLeagueDialogFragment;
import net.jaumebalmes.grincon17.futchamp.fragments.LeagueFragment;
import net.jaumebalmes.grincon17.futchamp.fragments.LoginDialogFragment;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnAddEquipoDialogListener;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnAddLeagueDialogListener;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnLoginDialogListener;
import net.jaumebalmes.grincon17.futchamp.models.Equipo;
import net.jaumebalmes.grincon17.futchamp.models.Jugador;
import net.jaumebalmes.grincon17.futchamp.models.League;
import net.jaumebalmes.grincon17.futchamp.repositoryApi.CoordinadorRepositoryApi;
import net.jaumebalmes.grincon17.futchamp.repositoryApi.LeagueRepositoryApi;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Esta activity muestra la vista del detalle de un jugador.
 * @author guillermo
 */
public class JugadorDetailActivity extends AppCompatActivity implements OnLoginDialogListener {

    private SharedPreferences preferences;
    private Equipo equipo;
    private Jugador jugador;
    private Api api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugador_detail);
        new Firebase().authFirebaseUser();
        api = new Api(); // para obtener la conexion a la API
        Gson gson = new Gson();
        jugador = gson.fromJson(getIntent().getStringExtra(getString(R.string.jugador_json)), Jugador.class);
        equipo = jugador.getEquipo();
        toolbarConf();

        ImageView jugadorImg = findViewById(R.id.imageViewJugadorImg);
        TextView jugadorName = findViewById(R.id.text_jugador_name);
        TextView jugadorSurName = findViewById(R.id.text_jugador_surname);
        TextView jugadorDorsal = findViewById(R.id.text_jugador_dorsal);
        TextView jugadorPosicion = findViewById(R.id.text_jugador_posicion);
        TextView jugadorEmail = findViewById(R.id.text_jugador_email);

        jugadorName.setText(jugador.getNombre());
        jugadorSurName.setText(jugador.getApellidos());
        jugadorDorsal.setText(jugador.getDorsal());
        jugadorPosicion.setText(jugador.getPosicion());
        jugadorEmail.setText(jugador.getEmail());
        loadImg(jugador.getImagen(), jugadorImg);
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
     *
     * @param url de la imagen
     * @param imageView la vista para poner la imagen
     */
    private void loadImg(String url, ImageView imageView) {
        Glide.with(getApplicationContext())
                .load(url)
                .error(R.mipmap.ic_launcher)
                .centerInside() //
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    /**
     * Este método crea el menú del toolbar
     *
     * @param menu el menú del sistema
     * @return true para que muestre el menú
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (preferences.contains(getString(R.string.my_username)) && preferences.contains(getString(R.string.my_username))) {
            inflater.inflate(R.menu.toolbar_coordinator_menu, menu);
            menu.removeItem(R.id.search_icon);
            menu.findItem(R.id.trash_icon).setVisible(true);
            menu.findItem(R.id.edit_icon).setVisible(true);
            menu.removeItem(R.id.logout);
            menu.removeItem(R.id.add_league);
            menu.removeItem(R.id.add_team);
            menu.removeItem(R.id.add_player);
            menu.removeItem(R.id.add_calendar);
        } else {
            inflater.inflate(R.menu.toolbar_login_menu, menu);
            menu.removeItem(R.id.search_icon);
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
            case R.id.account_login:
                LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
                loginDialogFragment.show(getSupportFragmentManager(), getString(R.string.login_txt));
                return true;
            case R.id.trash_icon:
                api.deleteJugador(jugador.getId(), getApplicationContext(), null, this);
                return true;
            case R.id.edit_icon:
                // TODO: implementar editar

                return true;
            case R.id.logout:
                preferences.edit().remove(getString(R.string.my_username)).apply();
                preferences.edit().remove(getString(R.string.my_pwd)).apply();
                invalidateOptionsMenu();
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
