package com.example.sjvirtual;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class recuperarPass extends AppCompatActivity {

    private EditText Email;
    private Button Aceptar;
    private Button Cancelar;

    private FirebaseAuth mAuth;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_pass);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(recuperarPass.this);

        Email = (EditText) findViewById(R.id.Text_Field_Correo);
        Aceptar = (Button) findViewById(R.id.Button_Aceptar);
        Cancelar = (Button) findViewById(R.id.Button_Cancelar);

        AlertDialog.Builder Aviso = new AlertDialog.Builder(recuperarPass.this);

        Aviso.setTitle("Digite su correo electrónico");
        Aviso.setMessage("Luego pulse aceptar, después recibira en su correo un mensaje para cambio de contraseña nueva.");
        Aviso.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }

        });
        Aviso.show();
        //BOTON ACEPTAR
        Aceptar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(Email.getText().toString().trim().isEmpty()){
                    Email.setError("Por favor digite su correo.");
                    Email.requestFocus();
                }else if(!Validar_Email(Email.getText().toString())){
                    Email.setError("Correo no válido.");
                    Email.requestFocus();
                }else{
                    mDialog.setMessage("Espera un momento...");
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    Formatear_Contraseña();
                }
            }

        });
        //BOTON CANCELAR
        Cancelar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(recuperarPass.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

        });
    }
    //VALIDAR EMAIL
    private boolean Validar_Email(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
    //FORMATEAR CONTRASEÑA
    private void Formatear_Contraseña() {
        //Envía el mensaje para establecer contraseña nueva al email
        mAuth.sendPasswordResetEmail(Email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //Si lo envía
                    AlertDialog.Builder Aviso_2 = new AlertDialog.Builder(recuperarPass.this);
                    Aviso_2.setTitle("Mira tu correo");
                    Aviso_2.setMessage("Allí encontraras un mensaje para restaurar tu contraseña nueva.");
                    Aviso_2.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(recuperarPass.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    });
                    Aviso_2.show();
                }else{
                    //Si no lo envía
                    Email.setError("Correo no existente.");
                    Email.requestFocus();
                }
                mDialog.dismiss();
            }
        });
    }
}