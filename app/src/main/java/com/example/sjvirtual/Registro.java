package com.example.sjvirtual;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {

    private EditText mNombresMensajero, mApellidosMensajero, mEmailMensajero, mContraseñaMensajero, mNumeroMensajero,
            mDireccionMensajero,mEpsMensajero,mDocumentoMensajero, mContraseñaMensajeroVerificar;

    private Button mRegistroMensajero, mCerrarRegistroMensajero;


    private FirebaseAuth  mAuth;
    private FirebaseUser User;
    private DatabaseReference mBaseDeDatosMensajero;

    private Spinner mSpinnerCiudades;

    private String idMensajero;

    private String mNombreMensajeroDato, mApellidoMensajeroDato, mEmailMensajeroDato, mContraseñaMensajeroDato, mContraseñaMensajeroDatoVerificar,
            mNumeroMensajeroDato, mCiudadSpinnerDato, mDireccionMensajeroDato, mEpsMensajeroDato, mDocumentoMensajeroDato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Set variables
        mNombresMensajero = (EditText) findViewById(R.id.NombreMensajero);
        mApellidosMensajero = (EditText) findViewById(R.id.ApellidosMensajero);
        mEmailMensajero = (EditText) findViewById(R.id.EmailMensajero);
        mContraseñaMensajero = (EditText) findViewById(R.id.ContraseñaMensajero);
        mNumeroMensajero = (EditText) findViewById(R.id.NumeroMensajero);
        mDireccionMensajero = (EditText)findViewById(R.id.direccionMensajeroRegistro);
        mEpsMensajero = (EditText)findViewById(R.id.epsMensajeroRegistro);
        mDocumentoMensajero = (EditText)findViewById(R.id.documentoMensajeroRegistro);
        mContraseñaMensajeroVerificar = (EditText)findViewById(R.id.ContraseñaMensajeroVerificar);

        mRegistroMensajero = (Button) findViewById(R.id.RegistrarMensajero);
        mCerrarRegistroMensajero = (Button) findViewById(R.id.CerrarRegistroMensajero);

        mSpinnerCiudades = (Spinner) findViewById(R.id.ciudadRegistro);

        mAuth = FirebaseAuth.getInstance();

        //Alerta inicial registro
        android.app.AlertDialog.Builder Aviso = new android.app.AlertDialog.Builder(Registro.this);
        Aviso.setTitle("Aviso");
        Aviso.setMessage("Registrate con el correo institucional");
        Aviso.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) { }
        });
        Aviso.show();
        //BOTON REGISTRO
        mRegistroMensajero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creacion de una alerta
                if (mNombresMensajero!=null && mApellidosMensajero!=null && mEmailMensajero!=null &&
                        mContraseñaMensajero!=null && mNumeroMensajero!=null && mDireccionMensajero != null
                        && mEpsMensajero!=null && mDocumentoMensajero!=null && mContraseñaMensajeroVerificar!=null) {

                    if (!mNombresMensajero.equals("") && !mApellidosMensajero.equals("") && !mEmailMensajero.equals("")
                            && !mContraseñaMensajero.equals("") && !mNumeroMensajero.equals("") && !mDireccionMensajero.equals("")
                            && !mEpsMensajero.equals("") && !mDocumentoMensajero.equals("") &&
                            !mContraseñaMensajeroVerificar.equals("")) {

                        mNombreMensajeroDato = mNombresMensajero.getText().toString();
                        mApellidoMensajeroDato = mApellidosMensajero.getText().toString();
                        mEmailMensajeroDato = mEmailMensajero.getText().toString();
                        mContraseñaMensajeroDato = mContraseñaMensajero.getText().toString();
                        mNumeroMensajeroDato = mNumeroMensajero.getText().toString();
                        mCiudadSpinnerDato = mSpinnerCiudades.getSelectedItem().toString();
                        mDireccionMensajeroDato = mDireccionMensajero.getText().toString();
                        mEpsMensajeroDato = mEpsMensajero.getText().toString();
                        mDocumentoMensajeroDato = mDocumentoMensajero.getText().toString();
                        mContraseñaMensajeroDatoVerificar = mContraseñaMensajeroVerificar.getText().toString();

                        if (!Validar_Email(mEmailMensajeroDato)) {
                            Toast.makeText(Registro.this, "Direccion de correo invalida", Toast.LENGTH_SHORT).show();
                            mEmailMensajero.requestFocus();

                        } else {

                            if(!mContraseñaMensajeroDato.equals(mContraseñaMensajeroDatoVerificar)) {

                                Toast.makeText(Registro.this, "La contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                                mContraseñaMensajero.requestFocus();


                            }else {
                                if (mContraseñaMensajeroDato.length() < 5 && !isValidPassword(mContraseñaMensajeroDato)) {
                                    Toast.makeText(Registro.this, "La contraseña debe ser mayor a 5 caracteres", Toast.LENGTH_SHORT).show();
                                    mContraseñaMensajero.requestFocus();
                                } else {
                                    final String email = mEmailMensajero.getText().toString();
                                    final String contraseña = mContraseñaMensajero.getText().toString();
                                    mAuth.createUserWithEmailAndPassword(email, contraseña).addOnCompleteListener(Registro.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(Registro.this, "error de registro", Toast.LENGTH_SHORT).show();
                                            } else {
                                                idMensajero = mAuth.getCurrentUser().getUid();
                                                User = mAuth.getCurrentUser();
                                                User.sendEmailVerification();
                                                mBaseDeDatosMensajero = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(idMensajero);
                                                mBaseDeDatosMensajero.setValue(true);
                                                GuardarInformacionDelMensajero();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }else{
                        Toast.makeText(Registro.this, "Ningun campo debe estar vacio", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(Registro.this, "Por favor llena todos los datos", Toast.LENGTH_LONG).show();

                }
            }
        });

        /*BOTON CERRAR REGISTRO*/
        mCerrarRegistroMensajero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registro.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        /*Spinner con sedes de la universidad*/
        DatabaseReference sedes = FirebaseDatabase.getInstance().getReference().child("sedes");
        sedes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> sede = new ArrayList<String>();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String sedeName = areaSnapshot.child("nombre").getValue(String.class);
                    sede.add(sedeName);
                }

                ArrayAdapter<String> ciudadesAdapter = new ArrayAdapter<String>(Registro.this, android.R.layout.simple_spinner_item, sede);
                ciudadesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnerCiudades.setAdapter(ciudadesAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /*Validar email*/
    private boolean Validar_Email(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
    /*Guardar informacion*/
    private void GuardarInformacionDelMensajero(){
        Map InformacionDelUsuario = new HashMap();
        InformacionDelUsuario.put("Nombre", mNombreMensajeroDato.trim());
        InformacionDelUsuario.put("Apellido", mApellidoMensajeroDato.trim());
        InformacionDelUsuario.put("Email", mEmailMensajeroDato.trim());
        InformacionDelUsuario.put("Contraseña", mContraseñaMensajeroDato.trim());
        InformacionDelUsuario.put("Codigo", mNumeroMensajeroDato.trim());
        InformacionDelUsuario.put("Jornada", mDireccionMensajeroDato.trim());
        InformacionDelUsuario.put("Sede", mCiudadSpinnerDato.trim());
        InformacionDelUsuario.put("Semestre", mDocumentoMensajeroDato.trim());
        InformacionDelUsuario.put("Carrera", mEpsMensajeroDato.trim());
        mBaseDeDatosMensajero.updateChildren(InformacionDelUsuario);

        AlertDialog.Builder Auten = new AlertDialog.Builder(Registro.this);

        Auten.setTitle("Verificar correo electrónico");
        Auten.setMessage("Por favor verifica tu correo para ser registrado satisfactoriamente.");
        Auten.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent(Registro.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }

        });

        Auten.show();

    }
    /*VALIDACION DE LA CONTRASEÑA*/
    public static boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.[0-9])(?=.[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }
}