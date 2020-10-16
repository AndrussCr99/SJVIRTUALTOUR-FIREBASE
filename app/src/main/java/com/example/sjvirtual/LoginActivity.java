package com.example.sjvirtual;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sjvirtual.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText mCorreo, mContraseña;
    private Button mIngresar, mRegistrarse, mPass;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private FirebaseUser User;
    private Task<Void> mBaseDeDatosCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Autenticacion
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser Usuario = FirebaseAuth.getInstance().getCurrentUser();
            }
        };

        //Set variables
        mCorreo= (EditText) findViewById(R.id.Correo);
        mContraseña=(EditText) findViewById(R.id.Contraseña);
        mIngresar=(Button) findViewById(R.id.Ingresar);
        mRegistrarse=(Button)findViewById(R.id.Registrarse);
        mPass =(Button)findViewById(R.id.OlvidoPass);

        //Boton registrar
        mRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent (LoginActivity.this, Registro.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        //Metodo ingreso
        logicaIngreso();

        //Boton olvide contraseña
        mPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, recuperarPass.class);
                startActivity(intent);
                finish();
                return;
            }
        });

    }
    /*LOGICA INGRESO*/
    private void logicaIngreso(){
        mIngresar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(mCorreo.getText().toString().trim().equals("")) {

                    mCorreo.setError("Por favor digite su correo.");
                    mCorreo.requestFocus();

                }else if(mContraseña.getText().toString().trim().equals("")){

                    mContraseña.setError("Por favor digite su contraseña.");
                    mContraseña.requestFocus();

                } else if (!Validar_Email(mCorreo.getText().toString())){

                    mCorreo.setError("Email no válido.");
                    mCorreo.requestFocus();

                }else if(mCorreo.getText().toString().trim().length() > 0 && mContraseña.getText().toString().trim().length()>0) {

                    final String email = mCorreo.getText().toString();

                    final String contraseña = mContraseña.getText().toString();

                    mAuth.signInWithEmailAndPassword(email, contraseña).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {

                                Toast.makeText(LoginActivity.this, "Error de ingreso.", Toast.LENGTH_SHORT).show();

                            }else {

                                //Dar valor al usuario actual

                                User = mAuth.getCurrentUser();

                                //Verifica si el usuario ya reviso su correo

                                if(!User.isEmailVerified()){

                                    //Aviso que no verifico

                                    AlertDialog.Builder Auten = new AlertDialog.Builder(LoginActivity.this);

                                    Auten.setTitle("Correo no verificado");
                                    Auten.setMessage("Por favor verifica tu correo electrónico.");
                                    Auten.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    });
                                    Auten.show();
                                }else{

                                    //Si ya verifico el correo

                                    mBaseDeDatosCliente = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(User.getUid()).child("Contraseña").setValue(mContraseña.getText().toString().trim());
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    return;

                                }

                            }

                        }
                    });

                } else{

                    Toast.makeText(LoginActivity.this,"Por favor llene los datos solicitados.",Toast.LENGTH_SHORT).show();

                }

            }

        });
    }
    /*Validar email*/
    private boolean Validar_Email(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
    /*on start*/
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }
    /*on stop*/
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }
}