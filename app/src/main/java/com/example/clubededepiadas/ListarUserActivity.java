package com.example.clubededepiadas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clubededepiadas.Adapters.CategoriaAdapter;
import com.example.clubededepiadas.Adapters.ListUserAdapter;
import com.example.clubededepiadas.Classes.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class ListarUserActivity extends AppCompatActivity {

    String  ip ;                          Boolean jaLogou;
    RecyclerView recyclerView;                           ProgressDialog progresso;
    EditText textPesquisa;                               User user;
    List<User> userList, userListFilter;
    ListUserAdapter listUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_user);
        ip =  getString(R.string.ip);
        user = new User();
        // verificacao do usuario logado
        SharedPreferences prefs = getSharedPreferences("meu_arquivo_de_preferencias", MODE_PRIVATE);
        jaLogou = prefs.getBoolean("estaLogado", false);

        if(jaLogou) {
            // chama a tela inicial
            user.setId(prefs.getString("id", "0"));
            user.setNome(prefs.getString("nome", "sem nome"));
            user.setemail(prefs.getString("email", "sem nome"));
            user.setAvatar(prefs.getString("avatar", "1566265043.png"));
        }else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }


        // Declarando os objetos do main.xml
        recyclerView = (RecyclerView) findViewById(R.id.mRecGetUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(ListarUserActivity.this));
        textPesquisa = (EditText) findViewById(R.id.TextPesquisa);

        userList = new ArrayList<>();
        progresso = new ProgressDialog(ListarUserActivity.this);
        progresso.setMessage("Carregando...");
        progresso.show();

        getUser(user.getId());



        // Função responsável pela pesquisa
        //  Pesquisar();

        textPesquisa.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }

            public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                Pesquisar();

                //recyclerView.setAdapter(new ArrayAdapter<String>(ListarUserActivity.this, android.R.layout.simple_list_item_1, pesquisa));
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        progresso = new ProgressDialog(ListarUserActivity.this);
        progresso.setMessage("Carregando...");
        progresso.show();

        getUser(user.getId());
    }

    public void Pesquisar() {
        int textlength = textPesquisa.getText().length();
        userListFilter = new ArrayList<>();

        for (int i = 0; i < userList.size(); i++ ) {
            if (textlength <= userList.get(i).toString().length()) {
                if (textPesquisa.getText().toString().equalsIgnoreCase((String)userList.get(i).getNome().subSequence(0, textlength))) {
                    userListFilter.add(userList.get(i));
                    listUserAdapter = new ListUserAdapter(userListFilter,ListarUserActivity.this);
                    recyclerView.setAdapter(listUserAdapter);
                }else{
                   // Toast.makeText(ListarUserActivity.this, "erro no if 2", Toast.LENGTH_LONG).show();
                }
            }else{
               // Toast.makeText(ListarUserActivity.this, "erro no if 1", Toast.LENGTH_LONG).show();
            }
        }
    }
    private  void getUser(String id) {
        userList = new ArrayList<>();
        Ion.with(ListarUserActivity.this)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/piadas
                .load("http://"+ip+"/ApiLaravelForAndroidTeste/public/api/user/"+id)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        try{
                            for(int i = 0; i < result.size(); i++) {
                                JsonObject jsonObject = result.get(i).getAsJsonObject();
                                User user;
                                user = new User();
                                user.setId(jsonObject.get("id").getAsString());
                                user.setNome(jsonObject.get("name").getAsString());
                                user.setemail(jsonObject.get("email").getAsString());
                                user.setAvatar(jsonObject.get("avatar").getAsString());
                                user.setData(jsonObject.get("created_at").getAsString());

                               userList.add(user);

                            }

                            listUserAdapter = new ListUserAdapter(userList,ListarUserActivity.this);
                            recyclerView.setAdapter(listUserAdapter);
                            progresso.cancel();

                        }catch (Exception erro){
                             Toast.makeText(ListarUserActivity.this, "Erro na Requisição", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }
}
