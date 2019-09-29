package com.example.clubededepiadas;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.clubededepiadas.Adapters.CategoriaAdapter;
import com.example.clubededepiadas.Adapters.CategoriaAdapterMenu;
import com.example.clubededepiadas.Adapters.PiadaAdapter;
import com.example.clubededepiadas.Classes.Categoria;
import com.example.clubededepiadas.Classes.Piada;

import com.example.clubededepiadas.Classes.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Piada piada, piada1;                                    RecyclerView myrecycleView, mRecicleCat,  myrecycle;
    private static final int COD_SELECIONA = 10;
    List<Piada> listPiada;      PiadaAdapter piadaAdapter;  User user;      List<Categoria> listCat, listCatMenu;
    ProgressDialog progresso;   EditText editDesc;          boolean jaLogou;    Intent intent;      String data;
    String categoria_a_listar, ip;          Categoria categoria, categoriaMenu;     int flagGetCat = 0;
    TextView nav_user, nav_email;                           ImageView nav_image;
    CategoriaAdapter categoriaAdapter;                      CategoriaAdapterMenu  categoriaAdapterMenu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pegando valor do menu selecionado e selecionando tipo para listar
        intent = getIntent();
        data = intent.getStringExtra("keyName");
        ip =  getString(R.string.ip);
        user = new User();  piada1 = new Piada();
        // verificacao do usuario logado
        SharedPreferences prefs = getSharedPreferences("meu_arquivo_de_preferencias", MODE_PRIVATE);
        jaLogou = prefs.getBoolean("estaLogado", false);
        categoria_a_listar = "1";

        if(data != null){
            listarPiadas(data);
        }else{
            listarPiadas("1");
        }

        if(jaLogou) {
            // chama a tela inicial
            user.setId(prefs.getString("id", "0"));
            user.setNome(prefs.getString("nome", "sem nome"));
            user.setemail(prefs.getString("email", "sem nome"));
            user.setAvatar(prefs.getString("avatar", "1566265043.png"));
        }else{
            Intent intent = new Intent(this, TelaApresentActivity.class);
            startActivity(intent);
        }

        //STRINGSERVIDOR = "http://www.ellego.com.br/webservice/apiPiadas/ApiLaravelForAndroidTeste/public/api/";
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.item_insert);
                editDesc = (EditText) dialog.findViewById(R.id.editDescricao);

                dialog.findViewById(R.id.btnCategoria_id).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialogCat = new Dialog(MainActivity.this);
                        dialogCat.setContentView(R.layout.item_recycler_categoria);

                        flagGetCat = 1;

                        //Laco para adicionar categorias

                        listCat = new ArrayList<>();
                        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                        myrecycle =(RecyclerView) dialogCat.findViewById(R.id.listCat);
                        myrecycle.setLayoutManager(layoutManager);

                        listarCategorias(dialogCat, dialog);
                        dialogCat.show();
                    }
                });

                dialog.findViewById(R.id.btnAdicionar).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (flagGetCat == 1) {
                            piada1.setCategoria_id(categoriaAdapter.getCategoria_id());
                        }else{
                            piada1.setCategoria_id("1");
                        }
                        createPiada(editDesc.getText().toString(), piada1);
                    }
                });
                dialog.findViewById(R.id.btnCancelar).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);
        // pegando textveiws do menu lateral
        nav_user = (TextView)hView.findViewById(R.id.nav_nome);
        nav_email = (TextView)hView.findViewById(R.id.nav_email);
        nav_image = (ImageView) hView.findViewById(R.id.nav_image);

        nav_user.setText(user.getNome());
        nav_email.setText(user.getemail());
        getUser(user.getId(),nav_image);

        listPiada = new ArrayList<>();        myrecycleView = findViewById(R.id.mRecyclerView);
        myrecycleView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        mRecicleCat = (RecyclerView)findViewById(R.id.mRecyclerCat);    listCatMenu = new ArrayList<>();
        mRecicleCat.setLayoutManager(new LinearLayoutManager(MainActivity.this ,LinearLayoutManager.HORIZONTAL, false));
        listarCategoriasMenu();


    }

    public  void listarPiadas(final String categoria_A_listar) {
        progresso = new ProgressDialog(MainActivity.this);
        progresso.setMessage("Carregando...");
        progresso.show();

        Ion.with(MainActivity.this)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/piadas
                .load("http://www.ellego.com.br/webservice/ApiLaravelForAndroidTeste/public/api/piadas")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {

                        try{
                            for(int i = 0; i < result.size(); i++){
                                JsonObject  jsonObject = result.get(i).getAsJsonObject();
                                piada = new Piada();
                                piada.setId(jsonObject.get("id").getAsString());
                                piada.setDescriscao(jsonObject.get("descricao").getAsString());
                                piada.setCategoria_id(jsonObject.get("categoria_id").getAsString());
                                piada.setUser_id(jsonObject.get("user_id").getAsString());
                                piada.setLikes(jsonObject.get("curtidas").getAsString());
                                piada.setDslikes(jsonObject.get("deslikes").getAsString());
                                if (piada.getCategoria_id().equals(categoria_A_listar)){
                                    listPiada.add(piada);
                                }

                            }
                            progresso.hide();
                            piadaAdapter = new PiadaAdapter(listPiada, MainActivity.this);
                            myrecycleView.setAdapter(piadaAdapter);

                        }catch (Exception erro){
                            progresso.hide();
                            //  Toast.makeText(MainActivity.this, "Erro no listar", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private  void listarCategorias(final Dialog dialogCat,final Dialog dialog) {

        Ion.with(MainActivity.this)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/piadas
                .load("http://www.ellego.com.br/webservice/ApiLaravelForAndroidTeste/public/api/categorias")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {

                        try{
                            for(int i = 0; i < result.size(); i++){
                                JsonObject jsonObject = result.get(i).getAsJsonObject();
                                categoria = new Categoria();
                                categoria.setId(jsonObject.get("id").getAsString());
                                categoria.setNome(jsonObject.get("nome").getAsString());

                                listCat.add(categoria);
                            }
                            categoriaAdapter = new CategoriaAdapter(listCat, dialogCat, dialog, MainActivity.this);
                            myrecycle.setAdapter(categoriaAdapter);

                        }catch (Exception erro){
                            progresso.hide();
                           // Toast.makeText(MainActivity.this, "Erro no listar", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private  void listarCategoriasMenu() {

        Ion.with(MainActivity.this)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/piadas
                .load("http://www.ellego.com.br/webservice/ApiLaravelForAndroidTeste/public/api/categorias")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {

                        try{
                            for(int i = 0; i < result.size(); i++){
                                JsonObject jsonObject = result.get(i).getAsJsonObject();
                                categoriaMenu = new Categoria();
                                categoriaMenu.setId(jsonObject.get("id").getAsString());
                                categoriaMenu.setNome(jsonObject.get("nome").getAsString());

                                listCatMenu.add(categoriaMenu);
                            }
                            myrecycleView = findViewById(R.id.mRecyclerView);
                            myrecycleView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                            categoriaAdapterMenu = new CategoriaAdapterMenu(listCatMenu, MainActivity.this, myrecycleView);
                            mRecicleCat.setAdapter(categoriaAdapterMenu);


                        }catch (Exception erro){
                            progresso.hide();
                          //  Toast.makeText(MainActivity.this, "Erro no listar", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    public void createPiada(String descricao, Piada piada){
       // Toast.makeText(MainActivity.this, "id da categoria: "+piada.getCategoria_id(), Toast.LENGTH_LONG).show();

        Ion.with(MainActivity.this)
                //  "http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/piadas"
                .load("POST", "http://www.ellego.com.br/webservice/ApiLaravelForAndroidTeste/public/api/piadas")
                .setBodyParameter("descricao_app", descricao)
                .setBodyParameter("user_id_app", user.getId())
                .setBodyParameter("categoria_app", ""+piada.getCategoria_id())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try{
                            if (result.equals("ok")){
                                Toast.makeText(MainActivity.this, "Inserido com sucesso", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }catch (Exception erro){
                          //  Toast.makeText(MainActivity.this, "Erro ao adicionar Piada", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private  void getUser(  final String id, final ImageView imageView) {
        Ion.with(MainActivity.this)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/piadas
                .load("http://www.ellego.com.br/webservice/ApiLaravelForAndroidTeste/public/api/user/"+id)
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
                                }else{
                                    //  Eventualmente esse erro ocorrera varias vezes

                                }
                            }
                        }catch (Exception erro){
                          //  Toast.makeText(MainActivity.this, "Erro na Requisição", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    public void getImage(User user, final ImageView imageView){

        Ion.with(MainActivity.this)
                .load("http://www.ellego.com.br/webservice/ApiLaravelForAndroidTeste/public/api/getImage/"+user.getAvatar())
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result)  {

                        imageView.setImageBitmap(result);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
           /* Intent it = new Intent(this, MainActivity.class);
            startActivity(it);*/
        }
    }



    @Override
    protected void onRestart() {
        super.onRestart();
       /* Intent in = new Intent(MainActivity.this, MainActivity.class);
        startActivity(in);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            SharedPreferences prefs = getSharedPreferences("meu_arquivo_de_preferencias", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("estaLogado", false);
            editor.putString("id",null);
            editor.putString("nome",null);
            editor.putString("email",null);
            editor.commit();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        if (id == R.id.action_settingsUser) {
           Intent intent = new Intent(MainActivity.this, SettingsUserActivity.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(MainActivity.this, SettingsUserActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(MainActivity.this, ListarUserActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_share) {


            Ion.with(MainActivity.this)
                    //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/
                    .load("POST", "http://www.ellego.com.br/webservice/ApiLaravelForAndroidTeste/public/api/getLink")
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            try {
                                if (result.equals("ok")) {
                                    Toast.makeText(MainActivity.this, "Deletado com sucesso", Toast.LENGTH_LONG).show();
                                }else{
                                    setLink(result);
                                }
                            } catch (Exception erro) {
                               // Toast.makeText(MainActivity.this, "link = "+result, Toast.LENGTH_LONG).show();

                            }
                        }
                    });



        } else if (id == R.id.nav_send) {
            SharedPreferences prefs = getSharedPreferences("meu_arquivo_de_preferencias", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("estaLogado", false);
            editor.putString("id",null);
            editor.putString("nome",null);
            editor.putString("email",null);
            editor.commit();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void setLink(String texto){

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, texto);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
