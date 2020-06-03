package net.jaumebalmes.grincon17.futchamp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import net.jaumebalmes.grincon17.futchamp.R;
import net.jaumebalmes.grincon17.futchamp.conexion.Api;
import net.jaumebalmes.grincon17.futchamp.models.Jugador;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddJugadorActivity extends AppCompatActivity {

    private static final int GALLERY_CODE = 0;
    private static final int CAMERA_CODE = 1;
    private Uri filePath;
    private ImageView playerImg;
    private EditText nombre;
    private EditText apellido;
    private EditText dorsal;
    private EditText posicion;
    private EditText email;
    private EditText dni;
    private EditText equipoNombre;
    private String imageFilePath;
    private Jugador jugador;
    private Api api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_jugador);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.new_player);
        setSupportActionBar(toolbar);

        api = new Api();

        jugador = new Jugador();
        playerImg = findViewById(R.id.imageViewAddPlayer);
        nombre = findViewById(R.id.textEditPlayerName);
        apellido = findViewById(R.id.textEditPlayerSurname);
        dorsal = findViewById(R.id.textEditDorsal);
        posicion = findViewById(R.id.textEditPosicion);
        email = findViewById(R.id.textEditMail);
        dni = findViewById(R.id.textEditDni);
        equipoNombre = findViewById(R.id.textEditPlayerTeamName);

        playerImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCameraOrGallery();
            }
        });
        // TODO: falta crear un botón en la toolbar para subir el jugador

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_add_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String name = String.valueOf(nombre.getText());
        String surname = String.valueOf(nombre.getText());
        String num = String.valueOf(nombre.getText());
        String pos = String.valueOf(nombre.getText());
        String mail = String.valueOf(nombre.getText());
        String id = String.valueOf(nombre.getText());
        String team = String.valueOf(equipoNombre.getText());
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(surname) && !TextUtils.isEmpty(num) &&
                !TextUtils.isEmpty(pos) && !TextUtils.isEmpty(mail) && !TextUtils.isEmpty(id) &&
                !TextUtils.isEmpty(team) && filePath != null) {
            jugador.setNombre(String.valueOf(nombre.getText()));
            jugador.setApellidos(String.valueOf(apellido.getText()));
            jugador.setPosicion(String.valueOf(posicion.getText()));
            jugador.setDorsal(String.valueOf(dorsal.getText()));
            jugador.setDni(String.valueOf(dni.getText()));
            jugador.setEmail(String.valueOf(email.getText()));
            Log.i("CAMPOS", String.valueOf(jugador));
            api.postJugador(String.valueOf(equipoNombre.getText()), filePath, jugador, getApplicationContext(), this);
        } else {
            Log.i("CAMPOS", jugador + " " + filePath);
            Toast.makeText(this, getString(R.string.add_new_playe_empty_field_msg), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Escoge entre tomar foto o acceder a las imágenes.
     */
    private void chooseCameraOrGallery() {
        final String[] chooseDialogItems = getResources().getStringArray(R.array.chooseImageDialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(AddJugadorActivity.this);
        builder.setTitle(R.string.chooseImageDialogTittle);
        builder.setItems(chooseDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (chooseDialogItems[which].equals(getResources().getString(R.string.chooseImageDialogCamera))) {
                    openCamera();
                } else if (chooseDialogItems[which].equals(getResources().getString(R.string.chooseImageDialogImage))) {
                    openGallery();
                } else if (chooseDialogItems[which].equals(getResources().getString(R.string.chooseImageDialogCancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * Crea una imagen con la foto tomada
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    /**
     * Accede a la cámara para tomar una foto.
     */
    private void openCamera() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager()) != null){

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("ERROR", Objects.requireNonNull(ex.getMessage()));

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "net.jaumebalmes.grincon17.futchamp.provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                startActivityForResult(pictureIntent,
                        CAMERA_CODE);
            }
        }
    }

    /**
     * Accede al almacenamiento del dispositivo para escoger una imagen
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_CODE:
                    filePath = Uri.fromFile(new File(imageFilePath));
                    playerImg.setImageURI(filePath);
                    break;
                case GALLERY_CODE:
                    if (data != null) {
                        filePath = data.getData();
                        playerImg.setImageURI(filePath);
                        playerImg.setTag(String.valueOf(filePath));
                    }
                    break;
            }
        }
    }
}
