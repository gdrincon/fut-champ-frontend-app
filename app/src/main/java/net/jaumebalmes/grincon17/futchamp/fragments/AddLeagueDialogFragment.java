package net.jaumebalmes.grincon17.futchamp.fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import net.jaumebalmes.grincon17.futchamp.R;
import net.jaumebalmes.grincon17.futchamp.interfaces.OnAddLeagueDialogListener;
import static android.app.Activity.RESULT_OK;

public class AddLeagueDialogFragment extends DialogFragment {

    private static final int GALLERY_CODE = 1;
    private OnAddLeagueDialogListener mListener;
    private ImageView leagueImg;
    private EditText leagueName;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_league_dialog, null);
        leagueImg = view.findViewById(R.id.imageViewNewLeague);
        leagueName = view.findViewById(R.id.textEditLeagueName);
        leagueImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);
        builder.setPositiveButton(R.string.add_league, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String name = String.valueOf(leagueName.getText());
                Drawable path = leagueImg.getDrawable();

                if (!TextUtils.isEmpty(name)) {
                    mListener.onAddLeagueClickListener(name, path); // Muestra mensaje
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
            mListener = (OnAddLeagueDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            leagueImg.setImageURI(selectedImage);
            leagueImg.setTag(String.valueOf(selectedImage));
        }
    }
}
