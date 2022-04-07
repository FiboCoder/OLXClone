package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.olx.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import Helper.FirebaseConfig;

public class LoginAndRegister extends AppCompatActivity {

    private AppCompatEditText etEmail, etPass;
    private SwitchCompat stAccess;
    private AppCompatButton btAccess;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_register);

        initAndConfigComponents();
    }

    private void initAndConfigComponents(){

        etEmail = findViewById(R.id.etEmailLAR);
        etPass = findViewById(R.id.etPassLAR);
        stAccess = findViewById(R.id.stLAR);

        auth = FirebaseConfig.getAuth();

        btAccess = findViewById(R.id.btLAR);
        btAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString();
                String pass = etPass.getText().toString();

                if(!email.isEmpty()){

                    if(!pass.isEmpty()){

                        if(stAccess.isChecked()){

                            //Register
                            btAccess.setText("Cadastrar");
                            auth.createUserWithEmailAndPassword(
                                    email,
                                    pass
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){

                                        Toast.makeText(LoginAndRegister.this, "Cadastro realizado com sucesso.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), Ads.class);
                                        startActivity(intent);

                                    }else{

                                        String exception = "";
                                        try {

                                            throw task.getException();
                                        }catch (FirebaseAuthWeakPasswordException e){

                                            exception = "Digite uma senha mais forte!";
                                        }catch (FirebaseAuthInvalidCredentialsException e){

                                            exception = "Digite uma e-mail válido!";
                                        }catch (FirebaseAuthUserCollisionException e){

                                            exception = "Está conta já foi cadastrada!";
                                        }catch (Exception e){

                                            exception = "Erro ao cadastrar usuário: " + e.getMessage();
                                            e.printStackTrace();
                                        }

                                        Toast.makeText(LoginAndRegister.this, exception, Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });

                        }else{

                            //Login
                            btAccess.setText("Entrar");
                            auth.signInWithEmailAndPassword(
                                    email, 
                                    pass
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    
                                    if(task.isSuccessful()){

                                        Intent intent = new Intent(getApplicationContext(), Ads.class);
                                        startActivity(intent);
                                        
                                    }else{

                                        Toast.makeText(LoginAndRegister.this, "Erro ao fazer login: " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }

                    }else{

                        Toast.makeText(LoginAndRegister.this, "Digite o e-mail por favor!", Toast.LENGTH_SHORT).show();
                    }
                }else{

                    Toast.makeText(LoginAndRegister.this, "Digite a senha por favor!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}