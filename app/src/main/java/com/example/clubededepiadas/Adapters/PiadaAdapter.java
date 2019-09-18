package com.example.clubededepiadas.Adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clubededepiadas.Classes.Categoria;
import com.example.clubededepiadas.Classes.Piada;
import com.example.clubededepiadas.Classes.User;
import com.example.clubededepiadas.MainActivity;
import com.example.clubededepiadas.R;
import com.example.clubededepiadas.SettingsUserActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class PiadaAdapter extends RecyclerView.Adapter<PiadaAdapter.PiadaHolder> {
    List<Piada> listPiadas; Context context; User user;
    String STRINGSERVIDOR = "http://www.ellego.com.br/webservice/apiPiadas/ApiLaravelForAndroidTeste/public/api/", ip;
    List<Categoria> listCat;
    RecyclerView myrecycleView, mRecicleCat,  myrecycle;
    Categoria categoria, categoriaEdit;
    Piada  piada1;
    boolean flag = false;
    ProgressDialog progresso;
    CategoriaAdapter categoriaAdapter;

    public PiadaAdapter(List<Piada> listPiadas, Context context) {
        this.listPiadas = listPiadas;
        this.context = context;
        piada1 = new Piada();

        ip =  context.getString(R.string.ip);

        user = new User();
        // verificacao do usuario logado
        SharedPreferences prefs = context.getSharedPreferences("meu_arquivo_de_preferencias", MODE_PRIVATE);
        // chama a tela inicial
        user.setId(prefs.getString("id", "0"));
        user.setNome(prefs.getString("nome", "sem nome"));
        user.setemail(prefs.getString("email", "sem nome"));
        user.setAvatar(prefs.getString("avatar", "1566265043.png"));

    }

    @NonNull
    @Override
    public PiadaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_piada, parent, false);

        return new PiadaAdapter.PiadaHolder(vista);
    }


    @Override
    public void onBindViewHolder(@NonNull final PiadaHolder holder, final int position) {
        holder.descricao.setText(listPiadas.get(position).getDescriscao());
        getUser(holder.nomeUser, holder.txtDataPost, listPiadas.get(position).getUser_id(), holder.imgUser);
        //categoriaAdapter = new CategoriaAdapter();

        holder.btnShere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compartilharPiada(listPiadas.get(position).getDescriscao());
            }
        });
        holder.btnDlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDesLike(holder.qtdDslike, user, listPiadas.get(position).getId());
            }
        });

        //  Verificando se a piada e do user logado
        if (user.getId().equals(listPiadas.get(position).getUser_id())){

            holder.qtdLike.setVisibility(View.INVISIBLE);
            holder.btnDlike.setVisibility(View.INVISIBLE);
            holder.qtdDslike.setVisibility(View.INVISIBLE);
            holder.btnShere.setVisibility(View.INVISIBLE);
            holder.btnMenu.setVisibility(View.INVISIBLE);


            holder.btnMenuUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, holder.btnMenu);
                    popup.inflate(R.menu.menu_piada);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_editar:

                                    final Dialog dialog = new Dialog(context);
                                    dialog.setContentView(R.layout.item_insert);
                                    EditText editDesc; TextView txtCatNome;
                                    txtCatNome = (TextView)dialog.findViewById(R.id.txtCategoria_id);
                                    editDesc = (EditText) dialog.findViewById(R.id.editDescricao);

                                    dialog.findViewById(R.id.btnCategoria_id).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {       // Clik botao categoria
                                            final Dialog dialogCat = new Dialog(context);
                                            dialogCat.setContentView(R.layout.item_recycler_categoria);
                                            flag = true;

                                            //Laco para adicionar categorias
                                            listCat = new ArrayList<>();
                                            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                                            myrecycle =(RecyclerView) dialogCat.findViewById(R.id.listCat);
                                            myrecycle.setLayoutManager(layoutManager);

                                            listarCategorias(dialogCat, dialog);
                                            dialogCat.show();
                                        }
                                    });

                                    // Chamada de funcao seta os valores dos campos
                                    setCamposPiada(editDesc, txtCatNome, listPiadas.get(position).getId());

                                    Button btn = (Button) dialog.findViewById(R.id.btnAdicionar);
                                    btn.setText("Atualisar");
                                    dialog.findViewById(R.id.btnAdicionar).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (flag){
                                                piada1.setCategoria_id(categoriaAdapter.getCategoria_id());
                                                updatePiada(((EditText) dialog.findViewById(R.id.editDescricao)).getText().toString(), listPiadas.get(position).getId(), piada1);
                                            }else{
                                                Toast.makeText(context, "Selecione a Categoria", Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    });
                                    dialog.show();
                                    break;

                                case R.id.action_deletar:

                                    deletePiada(listPiadas.get(position).getId());
                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });

        }else{
            holder.btnMenu.setBackgroundResource(R.drawable.ic_like);
            getLike(holder.qtdLike, listPiadas.get(position).getId());
            getDesLike(holder.qtdDslike, listPiadas.get(position).getId());
            holder.btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setLike(holder.qtdLike, user, listPiadas.get(position).getId());
                }
            });
            holder.btnMenuUser.setVisibility(View.INVISIBLE);
        }

    }

    private  void getCategorias(final String id, final TextView categoriaId ) {

        Ion.with(context)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/piadas
                .load("http://"+ip+"/ApiLaravelForAndroidTeste/public/api/categorias")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        try{
                            for(int i = 0; i < result.size(); i++){
                                JsonObject jsonObject = result.get(i).getAsJsonObject();
                                categoriaEdit = new Categoria();
                                categoriaEdit.setId(jsonObject.get("id").getAsString());
                                categoriaEdit.setNome(jsonObject.get("nome").getAsString());
                                if (categoria.getId().equals(id)){
                                    categoriaId.setText(categoriaEdit.getNome());
                                    piada1.setCategoria_id(categoriaEdit.getId());
                                }
                            }

                        }catch (Exception erro){

                            Toast.makeText(context, "Erro no listar", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private  void listarCategorias(final Dialog dialogCat,final Dialog dialog) {

        Ion.with(context)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/piadas
                .load("http://"+ip+"/ApiLaravelForAndroidTeste/public/api/categorias")
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
                            categoriaAdapter = new CategoriaAdapter(listCat, dialogCat, dialog, context);
                            piada1.setCategoria_id(categoriaAdapter.getCategoria_id());
                            myrecycle.setAdapter(categoriaAdapter);

                        }catch (Exception erro){
                            progresso.hide();
                            Toast.makeText(context, "Erro no listar", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void setDesLike(final TextView qtdDslike, User user, String piada_id) {
        // Toast.makeText(context, "user_id:" +user.getId(), Toast.LENGTH_LONG).show();
        // Toast.makeText(context, "piada_id:" +piada_id, Toast.LENGTH_LONG).show();

        Ion.with(context)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/piadas
                .load("POST","http://"+ip+"/ApiLaravelForAndroidTeste/public/api/deslike")
                .setBodyParameter("user_id", user.getId())
                .setBodyParameter("piada_id", piada_id)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        try{
                            for(int i = 0; i < result.size(); i++) {
                                JsonObject jsonObject = result.get(i).getAsJsonObject();
                                 qtdDslike.setText(jsonObject.get("deslikes").getAsString());
                                //Toast.makeText(context, "curtiu", Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception erro){
                            Toast.makeText(context, "Erro na Requisição "+erro, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void setLike(final TextView qtdLike, User user, String piada_id) {
       // Toast.makeText(context, "user_id:" +user.getId(), Toast.LENGTH_LONG).show();
       // Toast.makeText(context, "piada_id:" +piada_id, Toast.LENGTH_LONG).show();

        Ion.with(context)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/piadas
                .load("POST","http://"+ip+"/ApiLaravelForAndroidTeste/public/api/like")
                .setBodyParameter("user_id", user.getId())
                .setBodyParameter("piada_id", piada_id)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        try{
                            for(int i = 0; i < result.size(); i++) {
                                JsonObject jsonObject = result.get(i).getAsJsonObject();
                                qtdLike.setText(jsonObject.get("curtidas").getAsString());
                               // Toast.makeText(context, "curtiu", Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception erro){
                            Toast.makeText(context, "Erro na Requisição "+erro, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void getLike(final TextView qtdLike,  String piada_id){
        Ion.with(context)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/piadas
                .load("POST","http://"+ip+"/ApiLaravelForAndroidTeste/public/api/getLike")
                .setBodyParameter("piada_id", piada_id)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        try{
                            for(int i = 0; i < result.size(); i++) {
                                JsonObject jsonObject = result.get(i).getAsJsonObject();
                                qtdLike.setText(jsonObject.get("curtidas").getAsString());
                            }

                        }catch (Exception erro){
                           // Toast.makeText(context, "Erro na Requisição "+erro, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void getDesLike(final TextView qtdDesLike,  String piada_id){
        Ion.with(context)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/piadas
                .load("POST","http://"+ip+"/ApiLaravelForAndroidTeste/public/api/getLike")
                .setBodyParameter("piada_id", piada_id)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        try{
                            for(int i = 0; i < result.size(); i++) {
                                JsonObject jsonObject = result.get(i).getAsJsonObject();
                                qtdDesLike.setText(jsonObject.get("deslikes").getAsString());
                            }

                        }catch (Exception erro){
                            // Toast.makeText(context, "Erro na Requisição "+erro, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private  void getUser(final TextView nomeUser, final TextView dataPost, final String id, final ImageView imageView) {
        Ion.with(context)
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
                                     nomeUser.setText(user.getNome());
                                     dataPost.setText(user.getData());
                                     getImage(user, imageView);
                                 }else{
                                     //  Eventualmente esse erro ocorrera varias vezes

                                 }
                            }
                        }catch (Exception erro){
                           // Toast.makeText(context, "Erro na Requisição", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    public void deletePiada(String id)  {
        Ion.with(context)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/
                .load("DELETE", "http://"+ip+"/ApiLaravelForAndroidTeste/public/api/deletePiada/"+id)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            if (result.equals("ok")) {
                                Toast.makeText(context, "Deletado com sucesso", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(context, MainActivity.class);
                                context.startActivity(intent);
                            }
                        } catch (Exception erro) {
                            Toast.makeText(context, "Erro ao deletar piada ", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void updatePiada(String descricao,  String id, Piada piada1){
        Ion.with(context)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/
                .load("PUT", "http://"+ip+"/ApiLaravelForAndroidTeste/public/api/piadas/"+id)
                .setBodyParameter("descricao_app", descricao)
                .setBodyParameter("categoria_app", ""+piada1.getCategoria_id())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try{
                            if (result.equals("ok")){
                                Toast.makeText(context, "Atualizado com sucesso", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(context, MainActivity.class);
                                context.startActivity(intent);
                            }
                        }catch (Exception erro){
                            Toast.makeText(context, "Erro ao editar ", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void setCamposPiada(final EditText editDesc, final TextView categoriaId, String id){
        Ion.with(context)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/
                .load("http://"+ip+"/ApiLaravelForAndroidTeste/public/api/piadas/"+id)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try{
                            String descricao = result.get("descricao").getAsString();
                            EditText desc = editDesc;
                            desc.setText(descricao);
                            getCategorias(result.get("categoria_id").getAsString(), categoriaId);

                        }catch (Exception erro){
                            Toast.makeText(context, "Erro na requisição", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void getImage(User user, final ImageView imageView){
       // Toast.makeText(context, "avatar"+user.getAvatar(), Toast.LENGTH_LONG).show();
        Ion.with(context)
                .load("http://"+ip+"/ApiLaravelForAndroidTeste/public/api/getImage/"+user.getAvatar())
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result)  {
                        imageView.setImageBitmap(result);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return listPiadas.size();
    }

    public class PiadaHolder extends RecyclerView.ViewHolder {
        TextView descricao, nomeUser, txtDataPost, qtdLike, qtdDslike;
        Button btnMenu, btnDlike, btnShere,  btnMenuUser;
        CircleImageView imgUser;
        public PiadaHolder(@NonNull View itemView) {
            super(itemView);
            descricao = itemView.findViewById(R.id.text_descricao);     nomeUser = itemView.findViewById(R.id.text_nomeUser);            txtDataPost = itemView.findViewById(R.id.text_dataPost);
            imgUser = itemView.findViewById(R.id.fotoUser);             qtdLike = itemView.findViewById(R.id.txtLike);                   btnDlike = itemView.findViewById(R.id.btnDeslike);
            btnMenu = (Button) itemView.findViewById(R.id.btnMenu);     btnShere = itemView.findViewById(R.id.btnShare);                 qtdDslike = itemView.findViewById(R.id.txtDsLike);
            btnMenuUser = (Button) itemView.findViewById(R.id.btnMenuUser);
        }
    }

    public void compartilharPiada(String texto){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, texto);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }
}
