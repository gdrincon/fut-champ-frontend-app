package net.jaumebalmes.grincon17.futchamp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import net.jaumebalmes.grincon17.futchamp.R;
import net.jaumebalmes.grincon17.futchamp.conexion.Api;
import net.jaumebalmes.grincon17.futchamp.fragments.AddCalendarioDialogFragment;
import net.jaumebalmes.grincon17.futchamp.fragments.AddEquipoDialogFragment;
import net.jaumebalmes.grincon17.futchamp.fragments.AddResultadoDialogFragment;
import net.jaumebalmes.grincon17.futchamp.fragments.EquipoFragment;
import net.jaumebalmes.grincon17.futchamp.fragments.JornadaFragment;
import net.jaumebalmes.grincon17.futchamp.fragments.JugadorFragment;
import net.jaumebalmes.grincon17.futchamp.fragments.LoginDialogFragment;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnAddCalendarioDialogListener;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnAddEquipoDialogListener;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnAddResultadoDialogListener;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnListEquipoInteractionListener;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnListJornadaInteractionListener;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnListJugadorInteractionListener;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnLoginDialogListener;
import net.jaumebalmes.grincon17.futchamp.models.Calendario;
import net.jaumebalmes.grincon17.futchamp.models.Equipo;
import net.jaumebalmes.grincon17.futchamp.models.Jornada;
import net.jaumebalmes.grincon17.futchamp.models.Jugador;
import net.jaumebalmes.grincon17.futchamp.models.League;
import net.jaumebalmes.grincon17.futchamp.models.Marcador;
import net.jaumebalmes.grincon17.futchamp.models.Partido;

/**
 * Esta activity carga la vista principal que consiste en un menu inferior de navegación de tres pestañas
 *
 * @author guillermo
 */
public class LeagueDetailActivity extends AppCompatActivity implements OnLoginDialogListener,
        OnListJornadaInteractionListener, OnListEquipoInteractionListener, OnListJugadorInteractionListener,
        BottomNavigationView.OnNavigationItemSelectedListener,  OnAddEquipoDialogListener, OnAddCalendarioDialogListener,
        OnAddResultadoDialogListener {

    private SharedPreferences preferences;
    private League league;
    private Bundle bundle;
    private boolean longClick;
    private Api api;
    private Equipo equipoClicked;
    private Jugador jugadorClicked;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_detail);
        api = new Api(); // para obtener la conexion a la API
        Gson gson = new Gson();
        league = gson.fromJson(getIntent().getStringExtra(getString(R.string.league_json)), League.class);
        if(league != null) {
            bundle = new Bundle();
            bundle.putString("LEAGUE", league.getName());
        }
        toolbarConf();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        currentFragment = new JornadaFragment();
        currentFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.nav_host_fragment, currentFragment, "LEAGUE").commit();
        navView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferences = getSharedPreferences(getString(R.string.my_pref), Context.MODE_PRIVATE);
        equipoClicked = new Equipo();
        invalidateOptionsMenu();
    }

    /**
     * Método para navegar entre los fragments.
     * @param item el item seleccionado del menú.
     * @return true
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_equipos :
                currentFragment = new EquipoFragment();
                currentFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, currentFragment, "LEAGUE").commit();
                break;
            case R.id.navigation_jornada :
                currentFragment = new JornadaFragment();
                currentFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, currentFragment, "LEAGUE").commit();
                break;
            case R.id.navigation_jugadores :
                currentFragment = new JugadorFragment();
                currentFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, currentFragment, "LEAGUE").commit();
                break;
        }
        return true;
    }

    /**
     * Configuración del toolbar
     */
    private void toolbarConf() {
        Toolbar toolbar = findViewById(R.id.toolbar_detail_view);
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ImageView toolbarImg = findViewById(R.id.toolbar_image);
        TextView toolbarTittle = findViewById(R.id.textViewTitle);
        getResources().getDimension(R.dimen.toolbar_title_center);
        toolbarTittle.setY(getResources().getDimension(R.dimen.toolbar_title_center));
        toolbarTittle.setText(league.getName());
        loadImg(league.getLogo(), toolbarImg);
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
                .centerInside()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    /**
     * Crea y sobreescribe el menú del toolbar.
     * @param menu a usar
     * @return true
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (preferences.contains(getString(R.string.my_username)) && preferences.contains(getString(R.string.my_username))) {
            menu.clear();
            inflater.inflate(R.menu.toolbar_coordinator_menu, menu);
            menu.removeItem(R.id.add_league);
            if(longClick) {
                menu.findItem(R.id.search_icon).setVisible(false);
                menu.findItem(R.id.trash_icon).setVisible(true);
                menu.findItem(R.id.edit_icon).setVisible(true);
                menu.findItem(R.id.add_team).setVisible(false);
                menu.findItem(R.id.add_player).setVisible(false);
                menu.findItem(R.id.add_calendar).setVisible(false);
                menu.findItem(R.id.logout).setVisible(false);
            } else {
                menu.findItem(R.id.search_icon).setVisible(true);
                menu.findItem(R.id.trash_icon).setVisible(false);
                menu.findItem(R.id.edit_icon).setVisible(false);
                menu.findItem(R.id.add_team).setVisible(true);
                menu.findItem(R.id.add_player).setVisible(true);
                menu.findItem(R.id.add_calendar).setVisible(true);
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

                return true;
            case R.id.trash_icon:
                if(equipoClicked != null) {
                    api.deleteEquipo(equipoClicked.getId(), getApplicationContext(), getSupportFragmentManager());
                }
                if(jugadorClicked != null) {
                    api.deleteJugador(jugadorClicked.getId(), getApplicationContext(), getSupportFragmentManager(), null);
                }
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
            case R.id.add_team:
                AddEquipoDialogFragment addEquipoDialogFragment = new AddEquipoDialogFragment();
                addEquipoDialogFragment.show(getSupportFragmentManager(), getString(R.string.add_new_team));
                return true;
            case R.id.add_player:
                startActivity(new Intent(this, AddJugadorActivity.class));
                return true;
            case R.id.add_calendar:
                AddCalendarioDialogFragment addCalendarioDialogFragment = new AddCalendarioDialogFragment();
                addCalendarioDialogFragment.show(getSupportFragmentManager(), getString(R.string.add_calendar));
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

    /**
     * Es la implementación del click del login de la ventana flotante
     *
     * @param userName el nombre de usuario introducido en el campo de TextEdit
     * @param pwd      la contraseña introducida en el campo de TextEdit
     */
    @Override
    public void onLoginClickListener(String userName, String pwd) {
        api.requestLogin(userName, pwd, getApplicationContext(), this, preferences);
    }

    @Override
    public void onJornadaClickListener(Partido partido) {
        if(preferences != null) {
            AddResultadoDialogFragment addResultadoDialogFragment = new AddResultadoDialogFragment(partido);
            addResultadoDialogFragment.show(getSupportFragmentManager(), getString(R.string.add_resultado));
        }
    }

    @Override
    public void onEquipoClickListener(Equipo equipo) {
        String json = new Gson().toJson(equipo);
        Intent sendEquipo = new Intent(LeagueDetailActivity.this, EquipoDetailActivity.class);
        sendEquipo.putExtra(getString(R.string.equipo_json), json);
        if(longClick) {
            longClick = false;
        }
        startActivity(sendEquipo);
    }

    @Override
    public void onEquipoLongClickListener(Equipo equipo) {
        jugadorClicked = null;
        equipoClicked = equipo;
        longClick = !longClick;
        invalidateOptionsMenu();
    }

    @Override
    public void onJugadorClickListener(Jugador jugador) {
        String json = new Gson().toJson(jugador);
        Intent sendJugador = new Intent(LeagueDetailActivity.this, JugadorDetailActivity.class);
        sendJugador.putExtra(getString(R.string.jugador_json), json);
        if(longClick) {
            longClick = false;
        }
        startActivity(sendJugador);
    }

    @Override
    public void onJugadorLongClickListener(Jugador jugador) {
        equipoClicked = null;
        jugadorClicked = jugador;
        longClick = !longClick;
        invalidateOptionsMenu();
    }

    @Override
    public void onAddEquipoClickListener(String name, Uri filePath) {
        api.postEquipo(name, league, filePath, getApplicationContext(), this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onAddCalendarioClickListener(String date, String hour) {
        String parte[] = date.split("/"); // Se separa la fecha para poder darle formato
        int numeroDia = Integer.parseInt(parte[0]);
        int numeroMes = Integer.parseInt(parte[1]);
        // Se aplica formato con ceros
        String dia = String.format("%02d", numeroDia);
        String mes = String.format("%02d", numeroMes);
        String anyo = parte[2];
        String  fecha = anyo + "-" + mes + "-" + dia; // se Concatena con nuevo formato

        Calendario calendario = new Calendario();
        calendario.setLeague(league.getName());
        calendario.setFecha(fecha);
        calendario.setHora(hour);

        api.postCalendar(calendario, getApplicationContext());
        Log.i("OBJETO CALENDARIO: ", calendario.toString());
    }

    @Override
    public void onAddResultadoClickListener(int resultadoLocal, int resultadoVisitante, Partido partido) {
        Marcador marcador = new Marcador();
        marcador.setgLocal(resultadoLocal);
        marcador.setgVisitante(resultadoVisitante);
        marcador.setPartido(partido);
        api.postMarcador(getApplicationContext(), getSupportFragmentManager(), marcador);
    }
}
