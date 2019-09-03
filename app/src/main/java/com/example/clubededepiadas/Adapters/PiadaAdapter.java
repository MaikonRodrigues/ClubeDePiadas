package com.example.clubededepiadas.Adapters;

import android.app.Dialog;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.clubededepiadas.Classes.Piada;
import com.example.clubededepiadas.Classes.User;
import com.example.clubededepiadas.MainActivity;
import com.example.clubededepiadas.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class PiadaAdapter extends RecyclerView.Adapter<PiadaAdapter.PiadaHolder> {
    List<Piada> listPiadas; Context context; User user;
    String STRINGSERVIDOR = "http://www.ellego.com.br/webservice/apiPiadas/ApiLaravelForAndroidTeste/public/api/", ip = "192.168.56.1";

    public PiadaAdapter(List<Piada> listPiadas, Context context) {
        this.listPiadas = listPiadas;
        this.context = context;

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

        //  Verificando se a piada e do user logado
        if (user.getId().equals(listPiadas.get(position).getUser_id())){
            holder.qtdLike.setVisibility(View.INVISIBLE);
            holder.btnMenu.setOnClickListener(new View.OnClickListener() {
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
                                    EditText editDesc;
                                    editDesc = (EditText) dialog.findViewById(R.id.editDescricao);
                                    // Chamada de funcao para editar piada
                                    editPiada(editDesc, listPiadas.get(position).getId() );
                                    Button btn = (Button) dialog.findViewById(R.id.btnAdicionar);
                                    btn.setText("Atualisar");
                                    dialog.findViewById(R.id.btnAdicionar).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            updatePiada(((EditText) dialog.findViewById(R.id.editDescricao)).getText().toString(), listPiadas.get(position).getId());
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
            holder.btnMenu.setBackgroundResource(R.drawable.ic_action_heart);
            holder.btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getLike(holder.qtdLike, user, listPiadas.get(position).getId());
                }
            });


        }

    }

    private void getLike(final TextView qtdLike, User user, String piada_id) {
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
                                Toast.makeText(context, "curtiu", Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception erro){
                            Toast.makeText(context, "Erro na Requisição "+erro, Toast.LENGTH_LONG).show();
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
                            Toast.makeText(context, "Erro na Requisição", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    public void deletePiada(String id)  {
        Ion.with(context)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/
                .load("DELETE", "http://"+ip+"/ApiLaravelForAndroidTeste/public/api/piadas/deletePiada/"+id)
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

    public void updatePiada(String descricao, String id){
        Ion.with(context)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/
                .load("PUT", "http://"+ip+"/ApiLaravelForAndroidTeste/public/api/piadas/"+id)
                .setBodyParameter("descricao_app", descricao)
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

    public void editPiada(final EditText editDesc, String id){
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
                            Toast.makeText(context, descricao, Toast.LENGTH_LONG).show();

                        }catch (Exception erro){
                            Toast.makeText(context, "Erro na requisição", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void getImage(User user, final ImageView imageView){

        Ion.with(context)
                .load("http://"+ip+"/ApiLaravelForAndroidTeste/public/api/image/"+user.getAvatar())
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
        TextView descricao, nomeUser, txtDataPost, qtdLike;
        Button btnMenu;
        CircleImageView imgUser;
        public PiadaHolder(@NonNull View itemView) {
            super(itemView);
            descricao = itemView.findViewById(R.id.text_descricao);     nomeUser = itemView.findViewById(R.id.text_nomeUser);            txtDataPost = itemView.findViewById(R.id.text_dataPost);
            imgUser = itemView.findViewById(R.id.fotoUser);             qtdLike = itemView.findViewById(R.id.txtLike);
            btnMenu = (Button) itemView.findViewById(R.id.btnMenu);
        }
    }
}
