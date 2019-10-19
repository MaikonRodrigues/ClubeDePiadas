package com.example.clubededepiadas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.clubededepiadas.Adapters.PiadaAdapter;
import com.example.clubededepiadas.Classes.Piada;
import com.example.clubededepiadas.Classes.User;
import com.example.clubededepiadas.Classes.VolleySingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.android.volley.Response;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsUserActivity extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject>{

    Piada piada, piada1;                                        RecyclerView myrecycleView;
    List<Piada> listPiada;                                      PiadaAdapter piadaAdapter;  User user;
    ProgressDialog progresso;             boolean jaLogou;      Intent intent;      String data;
    String categoria_a_listar, ip ;               Button btnAddFoto, btnUpdateName, btnUpdateEmail;
    TextView name_user, name_email;                             CircleImageView user_image;
    private static final int COD_SELECIONA = 10;                Bitmap bitmap;


    RequestQueue request;
    RequestQueue requestQueue;
    StringRequest stringRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);

        setContentView(R.layout.activity_settings_user);
        ip =  getString(R.string.ipServidor);
        listPiada = new ArrayList<>();

        user = new User();  piada1 = new Piada();
        // verificacao do usuario logado
        SharedPreferences prefs = getSharedPreferences("meu_arquivo_de_preferencias", MODE_PRIVATE);
        jaLogou = prefs.getBoolean("estaLogado", false);
        categoria_a_listar = "1";

        name_user = (TextView) findViewById(R.id.txtNomeUser);
        name_email = (TextView)findViewById(R.id.txtEmailUser);
        myrecycleView = (RecyclerView)findViewById(R.id.mRecyclerViewSet);
        myrecycleView.setLayoutManager(new LinearLayoutManager(SettingsUserActivity.this));
        user_image =  findViewById(R.id.imageUserSet);              btnUpdateName = (Button)findViewById(R.id.btnUpdateNome);
        btnAddFoto = (Button) findViewById(R.id.btnAddFoto);

        request = Volley.newRequestQueue(SettingsUserActivity.this);
        requestQueue = Volley.newRequestQueue(SettingsUserActivity.this);

        if(jaLogou) {
            // chama a tela inicial
            user.setId(prefs.getString("id", "0"));
            user.setNome(prefs.getString("nome", "sem nome"));
            user.setemail(prefs.getString("email", "sem nome"));
            user.setAvatar(prefs.getString("avatar", "1566265043.png"));

            name_user.setText(user.getNome());
            name_email.setText(user.getemail());
        }else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        // Pegando valor do menu selecionado e selecionando tipo para listar
        intent = getIntent();
        data = intent.getStringExtra("keyName");
        /*if(data != null){
            listarPiadas(data);
        }else{
            listarPiadas("1");
        }*/
        listarPiadas();

        getUser(user.getId(), user_image);

        btnAddFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, COD_SELECIONA);
            }
        });
        btnUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateName();
            }
        });

    }

    @Override   // Funcao botao voltar
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

                // Funcao editar nome usuario
    public void updateName(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Digite um novo nome");

        final EditText input = new EditText(SettingsUserActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.TEXT_ALIGNMENT_GRAVITY,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setGravity(View.TEXT_ALIGNMENT_CENTER);
        input.setText(name_user.getText());
        builder.setView(input);
        builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               // Toast.makeText(getApplicationContext(), "Text entered is " + input.getText().toString(), Toast.LENGTH_SHORT).show();

                progresso = new ProgressDialog(SettingsUserActivity.this);
                progresso.setMessage("Carregando...");
                progresso.show();

                Ion.with(SettingsUserActivity.this)
                        //  "http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/piadas"
                        .load("PUT", "http://www.ellego.com.br/webservice/ApiLaravelForAndroidTeste/public/api/updateNome")
                        .setBodyParameter("id", user.getId())
                        .setBodyParameter("name", input.getText().toString())
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                try{
                                    if (result.equals("ok")){
                                        Toast.makeText(SettingsUserActivity.this, "Nome atualizado com sucesso", Toast.LENGTH_LONG).show();

                                        SharedPreferences prefs = getSharedPreferences("meu_arquivo_de_preferencias", CadastroActivity.MODE_PRIVATE );
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("nome",input.getText().toString());
                                        editor.commit();
                                        progresso.hide();
                                        Intent intent = new Intent(SettingsUserActivity.this, SettingsUserActivity.class);
                                        startActivity(intent);
                                    }
                                }catch (Exception erro){
                                   // Toast.makeText(SettingsUserActivity.this, "Erro ao atualizar nome", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case COD_SELECIONA:
                Uri tabPost = data.getData();
                user_image.setImageURI(tabPost);
                try {
                    bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),tabPost);
                    user_image.setImageBitmap(bitmap);
                    // Depois de carregar a imagem
                    carregarWEBService(tabPost);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    private void carregarWEBService(Uri tabPost) {

        progresso = new ProgressDialog(this);
        progresso.setMessage("Carregando...");
        progresso.show();

        String url1 = "http://"+ip+"/ApiLaravelForAndroidTeste/public/uplaodImage.php",
                url = "http://"+ip+"/public/uplaodImage.php";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progresso.hide();
                    Toast.makeText(SettingsUserActivity.this, "Foto Alterada com Sucesso", Toast.LENGTH_SHORT).show();
               /* Intent refresh = new Intent(SettingsUserActivity.this, SettingsUserActivity.class);
                startActivity(refresh);*/
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(SettingsUserActivity.this, "Erro ao atualizar verifique sua conexão ", Toast.LENGTH_SHORT).show();
                progresso.hide();

            }
        }) {
            @Override
            protected Map<String, String> getParams()  throws AuthFailureError {

                String imagem = converterImgString(bitmap);
                Map<String,String> parametros = new HashMap<>();
                parametros.put("imagem", imagem);
                parametros.put("user_id", user.getId());
                return parametros;
            }

        };

         //requestQueue.add(stringRequest);
         VolleySingleton.getIntanciaVolley(this).addToRequestQueue(stringRequest);

    }

    private String converterImgString(Bitmap bitmap) {

        ByteArrayOutputStream array=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagemByte=array.toByteArray();
        String imagemString= Base64.encodeToString(imagemByte,Base64.DEFAULT);

        return imagemString;
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {

    }

    public  void listarPiadas() {
        progresso = new ProgressDialog(SettingsUserActivity.this);
        progresso.setMessage("Carregando...");
        progresso.show();

        Ion.with(SettingsUserActivity.this)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/piadas
                .load("http://"+ip+"/public/api/piadas")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        try{
                            for(int i = 0; i < result.size(); i++){
                                JsonObject jsonObject = result.get(i).getAsJsonObject();
                                piada = new Piada();
                                piada.setId(jsonObject.get("id").getAsString());
                                piada.setDescriscao(jsonObject.get("descricao").getAsString());
                                piada.setCategoria_id(jsonObject.get("categoria_id").getAsString());
                                piada.setUser_id(jsonObject.get("user_id").getAsString());
                                piada.setLikes(jsonObject.get("curtidas").getAsString());
                                piada.setDslikes(jsonObject.get("deslikes").getAsString());
                                if (piada.getUser_id().equals(user.getId())){
                                    listPiada.add(piada);
                                }


                            }
                            progresso.hide();
                            piadaAdapter = new PiadaAdapter(listPiada, SettingsUserActivity.this);
                            myrecycleView.setAdapter(piadaAdapter);


                        }catch (Exception erro){
                            progresso.hide();
                           // Toast.makeText(SettingsUserActivity.this, "Erro no listar", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private  void getUser(  final String id, final ImageView imageView) {
        Ion.with(SettingsUserActivity.this)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/piadas
                .load("http://"+ip+"/public/api/user/"+id)
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
                          //  Toast.makeText(SettingsUserActivity.this, "Erro na Requisição", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    public void getImage(User user, final ImageView imageView){

               Ion.with(SettingsUserActivity.this)
                .load("http://"+ip+"/public/api/getImage/"+user.getAvatar())
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result)  {
                        Bitmap bitmap = BitmapFactory.decodeFile(result.toString());
                        imageView.setImageBitmap(result);
                    }
                });
    }

}
