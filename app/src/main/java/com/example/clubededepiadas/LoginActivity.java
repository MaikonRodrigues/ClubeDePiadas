package com.example.clubededepiadas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clubededepiadas.Classes.User;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class LoginActivity extends AppCompatActivity {

    EditText email, senha;  Button btnLogar;    User user;  TextView naoTconta;
    String  ip = "192.168.56.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.email_login);  senha = (EditText) findViewById(R.id.password_login);
        btnLogar = (Button) findViewById(R.id.btnLogar);    naoTconta = (TextView) findViewById(R.id.btnNaotenhoConta);
        user = new User();

        naoTconta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
                startActivity(intent);
            }
        });

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( email.getText().toString().isEmpty() || senha.getText().toString().isEmpty() ) {
                    Toast.makeText(v.getContext(), "Todos os campos são obrigatorios", Toast.LENGTH_SHORT).show();
                } else {

                    if (senha.getText().length() < 8) {
                         Toast.makeText(v.getContext(), "Senha curta, minimo 8 caracters", Toast.LENGTH_SHORT).show();
                    } else {

                         user.setemail(email.getText().toString());
                         user.setSenha(senha.getText().toString());

                         login();
                    }
                }
            }
        });

    }
    public void login() {

        Ion.with(LoginActivity.this)
                //  "http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/piadas"
                .load("POST","http://"+ip+"/ApiLaravelForAndroidTeste/public/api/login")
                .setBodyParameter("email", ""+user.getemail())
                .setBodyParameter("password", ""+user.getSenha())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                                user.setId(result.get("id").getAsString());
                                user.setNome(result.get("name").getAsString());
                                user.setemail(result.get("email").getAsString());
                                user.setAvatar(result.get("avatar").getAsString());

                                SharedPreferences prefs = getSharedPreferences("meu_arquivo_de_preferencias", CadastroActivity.MODE_PRIVATE );
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("estaLogado", true);
                                editor.putString("id",user.getId());
                                editor.putString("nome",user.getNome());
                                editor.putString("email",user.getemail());
                                editor.putString("avatar",user.getAvatar());
                                editor.commit();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);

                        } catch (Exception erro) {
                            Toast.makeText(LoginActivity.this, "Email ou senha incorretas" + erro, Toast.LENGTH_LONG).show();

                        }
                    }
                });

    }
}
