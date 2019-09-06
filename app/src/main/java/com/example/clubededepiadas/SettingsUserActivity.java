package com.example.clubededepiadas;

import android.app.ProgressDialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    String categoria_a_listar, ip = "192.168.1.5";              Button btnAddFoto, btnUpdateName, btnUpdateEmail;
    TextView name_user, name_email;                             CircleImageView user_image;
    private static final int COD_SELECIONA = 10;                Bitmap bitmap;


    RequestQueue request;
    RequestQueue requestQueue;
    StringRequest stringRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_user);

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
        btnAddFoto = (Button) findViewById(R.id.btnAddFoto);        btnUpdateEmail = (Button)findViewById(R.id.btnUpdateEmail);

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
        if(data != null){
            listarPiadas(data);
        }else{
            listarPiadas("1");
        }

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
        btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmail();
            }
        });
    }

    public void updateEmail(){

    }
    public void updateName(){

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
                   // carregarWEBService(tabPost);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

        }
    }



    /*
    ----------------------------------------------------------------------------------------------------------
     */

    private void carregarWEBService(Uri tabPost) {

        progresso = new ProgressDialog(this);
        progresso.setMessage("Carregando...");
        progresso.show();

        String url = "http://"+ip+"/ApiLaravelForAndroidTeste/public/api/updateAvatar";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progresso.hide();
                    Toast.makeText(SettingsUserActivity.this, "Foto Subiu :"+response, Toast.LENGTH_SHORT).show();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(SettingsUserActivity.this, "Erro ao Registrar erro: "+ error, Toast.LENGTH_SHORT).show();
                progresso.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams()  throws AuthFailureError {

                String imagem = converterImgString(bitmap);
                Map<String,String> parametros = new HashMap<>();
                parametros.put("avatar", imagem);
                return parametros;
            }

        };

         requestQueue.add(stringRequest);
       // VolleySingleton.getIntanciaVolley(this).addToRequestQueue(stringRequest);

    }

    private String converterImgString(Bitmap bitmap) {

        ByteArrayOutputStream array=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagemByte=array.toByteArray();
        String imagemString= Base64.encodeToString(imagemByte,Base64.DEFAULT);

        return imagemString;
    }

    /*
    ----------------------------------------------------------------------------------------------------------
     */
    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {

    }
    /*
    ----------------------------------------------------------------------------------------------------------
     */

    /*
    //  Metodos de teste
    private class Encode_image extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            bitmap = BitmapFactory.decodeFile(file_uri.getPath());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            bitmap.recycle();

            byte[] array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array, 0);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            makeRequest();
        }
    }

    private void makeRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.1.70:89/tutorial3/connection.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("encoded_string",encoded_string);
                map.put("image_name",image_name);

                return map;
            }
        };
        requestQueue.add(request);
    }

    private void uploadImage(Bitmap bitmap){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        try {
            jsonObject = new JSONObject();
            String imgname = String.valueOf(Calendar.getInstance().getTimeInMillis());
            jsonObject.put("name", imgname);
            //  Log.e("Image name", etxtUpload.getText().toString().trim());
            jsonObject.put("image", encodedImage);
            // jsonObject.put("aa", "aa");
        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, upload_URL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e("aaaaaaa", jsonObject.toString());
                        rQueue.getCache().clear();
                        Toast.makeText(getApplication(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("aaaaaaa", volleyError.toString());

            }
        });

        rQueue = Volley.newRequestQueue(SettingsUserActivity    .this);
        rQueue.add(jsonObjectRequest);

    }
    */

    /*
    ----------------------------------------------------------------------------------------------------------
     */

    //  Metodos prontos nao apagar
    public  void listarPiadas(final String categoria_A_listar) {
        progresso = new ProgressDialog(SettingsUserActivity.this);
        progresso.setMessage("Carregando...");
        progresso.show();

        Ion.with(SettingsUserActivity.this)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/piadas
                .load("http://"+ip+"/ApiLaravelForAndroidTeste/public/api/piadas")
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
                            Toast.makeText(SettingsUserActivity.this, "Erro no listar", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private  void getUser(  final String id, final ImageView imageView) {
        Ion.with(SettingsUserActivity.this)
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
                                }else{
                                    //  Eventualmente esse erro ocorrera varias vezes

                                }
                            }
                        }catch (Exception erro){
                            Toast.makeText(SettingsUserActivity.this, "Erro na Requisição", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    public void getImage(User user, final ImageView imageView){

               Ion.with(SettingsUserActivity.this)
                .load("http://"+ip+"/ApiLaravelForAndroidTeste/public/api/getImage/"+user.getAvatar())
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
