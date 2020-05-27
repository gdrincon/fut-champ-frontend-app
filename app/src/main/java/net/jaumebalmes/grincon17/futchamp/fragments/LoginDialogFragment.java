package net.jaumebalmes.grincon17.futchamp.fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import net.jaumebalmes.grincon17.futchamp.R;
import net.jaumebalmes.grincon17.futchamp.conexion.Api;
import net.jaumebalmes.grincon17.futchamp.conexion.Enlace;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnLoginDialogListener;
import net.jaumebalmes.grincon17.futchamp.repositoryApi.CoordinadorRepositoryApi;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class LoginDialogFragment extends DialogFragment {

    private static final String TAG = "COORDINADOR"; //  Para mostrar mensajes por consola

    private OnLoginDialogListener mListener;
    private EditText userName;
    private EditText pwd;

    private Retrofit retrofitCoordinador;

    public LoginDialogFragment() {
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_login_dialog, null);
        userName = view.findViewById(R.id.textEditUser);
        pwd = view.findViewById(R.id.textEditPwd);

        Enlace enlace = new Enlace(); // para obtener los enlaces de conexion a la api
        Api api = new Api(); // para obtener la conexion a la API
        retrofitCoordinador = api.getConexion(enlace.getLink(enlace.COORDINADOR));

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);
        builder.setPositiveButton(R.string.login_txt, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Obtiene los datos del usuario para verificar autorizacion
                String name = String.valueOf(userName.getText());
                String pass = String.valueOf(pwd.getText());

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pass)) {
                    // mListener.onLoginClickListener(name, pass); // Muestra mensaj
                    verificarAutorizacionUsuario(name, pass);   // Verifica la respuesta de seguridad

                    mListener.onLoginClickListener(name, pass); // Captura le acceso del coordinador
                } else {
                    Toast.makeText(getContext(), "Must not be empty " + name, Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton(R.string.cancel_txt, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (OnLoginDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    // =============================================================================================
    // CONEXION A LA API

    // Este metodo verifica la respuesta de seguridad obteniendo un valor de tipo Boleano.
    private void verificarAutorizacionUsuario(String name, String pass) {
        CoordinadorRepositoryApi coordinadorRepositoryApi = retrofitCoordinador.create(CoordinadorRepositoryApi.class);
        Call<Boolean> verificandoRespuesta = coordinadorRepositoryApi.verificarAutorizacion(name, pass);

        verificandoRespuesta.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Boolean respuesta; // Para almacenar la respuesta de seguridad para el acceso de un coordinador
                    respuesta = response.body(); // Obtiene la respuesta para saber si el coordinador tiene acceso.

                    // Aqui se puede ver la respuesta y trabajar con ella
                    Log.e(TAG, " RESPUESTA DE SEGURIDAD: " + respuesta);
                } else {
                    try { // Esta respuesta solo se muetra si el valor es false
                        Log.e(TAG, " NO TIENE AUTORIZACION: onResponse: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(TAG, " => ERROR VERIFICAR LA CONEXION => onFailure: " + t.getMessage());
            }
        });
    }

}
