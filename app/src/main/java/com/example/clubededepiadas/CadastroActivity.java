package com.example.clubededepiadas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.clubededepiadas.Classes.User;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class CadastroActivity extends AppCompatActivity {

    EditText email, senha, confSenha, nome;
    Button btnCadastrar;
    User user;
    String  ip = "192.168.1.5";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        user = new User();
        nome = (EditText) findViewById(R.id.nome_cadastro);
        email = (EditText) findViewById(R.id.email_cadastro);
        senha = (EditText) findViewById(R.id.password_cadastro);
        confSenha = (EditText) findViewById(R.id.confpassword_cadastro);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nome.getText().toString().isEmpty() || email.getText().toString().isEmpty() ||
                        senha.getText().toString().isEmpty() || confSenha.getText().toString().isEmpty()) {
                    Toast.makeText(v.getContext(), "Todos os campos são obrigatorios", Toast.LENGTH_SHORT).show();
                } else {

                    if (senha.getText().toString().equals(confSenha.getText().toString())) {
                        if (senha.getText().length() < 8) {
                            Toast.makeText(v.getContext(), "Senha curta, minimo 8 caracters", Toast.LENGTH_SHORT).show();
                        } else {
                            user.setNome(nome.getText().toString());
                            user.setemail(email.getText().toString());
                            user.setSenha(senha.getText().toString());

                            createUser();
                        }
                    } else {
                        Toast.makeText(v.getContext(), "As senhas não são iguais", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });
    }

    public void createUser() {

        Ion.with(CadastroActivity.this)
                //  "http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/piadas"
                .load("POST","http://"+ip+"/ApiLaravelForAndroidTeste/public/api/addUser")
                .setBodyParameter("name", "" + user.getNome())
                .setBodyParameter("email", "" + user.getemail())
                .setBodyParameter("password", "" + user.getSenha())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {

                            if (result.equals("erro")){
                                Toast.makeText(CadastroActivity.this, "erro ao cadastrar", Toast.LENGTH_LONG).show();
                            }else {


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

                                Intent intent = new Intent(CadastroActivity.this, MainActivity.class);
                                startActivity(intent);


                            }

                        } catch (Exception erro) {
                            Toast.makeText(CadastroActivity.this, "Erro ao criar user " + erro, Toast.LENGTH_LONG).show();
                            nome.setText("" + erro);
                        }
                    }
                });

    }
}
