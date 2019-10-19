package com.example.clubededepiadas;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clubededepiadas.Adapters.PiadaAdapter;
import com.example.clubededepiadas.Classes.Piada;
import com.example.clubededepiadas.Classes.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowUsersActivity extends AppCompatActivity {

    Intent intent;                          Piada piada;
    String idRecebida, ip;                  List<Piada> listPiada;
    boolean jaLogou;                        PiadaAdapter piadaAdapter;
    CircleImageView circleImageView;        RecyclerView recyclerView;
    TextView userName;                      User userSelecionado;
    ProgressDialog  progresso;              Boolean btn;
    
        
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);

        listPiada = new ArrayList<>();  userSelecionado = new User();

        setContentView(R.layout.activity_show_users);
        ip =  getString(R.string.ipServidor);
        intent = getIntent();
        idRecebida = intent.getStringExtra("id");

        circleImageView = (CircleImageView) findViewById(R.id.fotoUserShow);
        userName = (TextView)findViewById(R.id.nmUserShow);
        recyclerView = (RecyclerView) findViewById(R.id.mRecShow);

        // verificacao do usuario logado
        SharedPreferences prefs = getSharedPreferences("meu_arquivo_de_preferencias", MODE_PRIVATE);
        jaLogou = prefs.getBoolean("estaLogado", false);
        
        if(jaLogou) {
           getUser(idRecebida, circleImageView);
        }else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        listarPiadas(userSelecionado.getId());
    }

    private  void getUser(  final String id, final ImageView imageView) {
        Ion.with(ShowUsersActivity.this)
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
                                if (user.getId().equals(id)) {
                                    getImage(user, imageView);
                                    userName.setText(user.getNome());
                                    //Toast.makeText(ShowUsersActivity.this, "enviei o id: "+user.getId(), Toast.LENGTH_LONG).show();

                                    listarPiadas(user.getId());
                                    userSelecionado.setId(user.getId());
                                }else{
                                    //  Eventualmente esse erro ocorrera varias vezes

                                }
                            }
                        }catch (Exception erro){
                            //  Toast.makeText(ShowUsersActivity.this, "Erro na Requisição", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    public void getImage(User user, final ImageView imageView){

        Ion.with(ShowUsersActivity.this)
                .load("http://"+ip+"/ApiLaravelForAndroidTeste/public/api/getImage/"+user.getAvatar())
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result)  {

                        imageView.setImageBitmap(result);
                    }
                });
    }

    public  void listarPiadas(final String id_A_listar) {

        progresso = new ProgressDialog(ShowUsersActivity.this);
        progresso.setMessage("Carregando...");
        progresso.show();

        piadaAdapter = new PiadaAdapter(listPiada, ShowUsersActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShowUsersActivity.this));
        recyclerView.setHasFixedSize(true);



        Ion.with(ShowUsersActivity.this)
                .load("http://"+ip+"/ApiLaravelForAndroidTeste/public/api/piadas")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        try{
                            for(int i = 0; i < result.size(); i++){

                               // Toast.makeText(ShowUsersActivity.this, "Pesquisa ok", Toast.LENGTH_LONG).show();

                                JsonObject  jsonObject = result.get(i).getAsJsonObject();
                                piada = new Piada();
                                piada.setId(jsonObject.get("id").getAsString());
                                piada.setDescriscao(jsonObject.get("descricao").getAsString());
                                piada.setCategoria_id(jsonObject.get("categoria_id").getAsString());
                                piada.setUser_id(jsonObject.get("user_id").getAsString());
                                piada.setLikes(jsonObject.get("curtidas").getAsString());
                                piada.setDslikes(jsonObject.get("deslikes").getAsString());

                                if (piada.getUser_id().equals(id_A_listar)){
                                    listPiada.add(piada);
                                    //Toast.makeText(ShowUsersActivity.this, "adicionou", Toast.LENGTH_LONG).show();
                                }

                            }

                            recyclerView.setAdapter(piadaAdapter);


                        }catch (Exception erro){
                            progresso.hide();
                            Toast.makeText(ShowUsersActivity.this, "Erro no listar", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        progresso.hide();

    }
}
