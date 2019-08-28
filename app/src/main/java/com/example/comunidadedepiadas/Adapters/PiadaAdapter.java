package com.example.comunidadedepiadas.Adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comunidadedepiadas.Classes.Piada;
import com.example.comunidadedepiadas.Classes.User;
import com.example.comunidadedepiadas.MainActivity;
import com.example.comunidadedepiadas.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.ion.Response;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PiadaAdapter extends RecyclerView.Adapter<PiadaAdapter.PiadaHolder> {
    List<Piada> listPiadas; Context context;
    String STRINGSERVIDOR = "http://www.ellego.com.br/webservice/apiPiadas/ApiLaravelForAndroidTeste/public/api/", ip = "192.168.1.2";

    public PiadaAdapter(List<Piada> listPiadas, Context context) {
        this.listPiadas = listPiadas;
        this.context = context;
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

        holder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.imgDslike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        TextView descricao, nomeUser, txtDataPost, qtdLike, qtDeslike;
        ImageView imgLike, imgDslike;   Button btnMenu;
        CircleImageView imgUser;
        public PiadaHolder(@NonNull View itemView) {
            super(itemView);
            descricao = itemView.findViewById(R.id.text_descricao);     nomeUser = itemView.findViewById(R.id.text_nomeUser);
            txtDataPost = itemView.findViewById(R.id.text_dataPost);    qtDeslike = itemView.findViewById(R.id.txtQuantLike);
            qtdLike = itemView.findViewById(R.id.txtQuantDslike);       imgLike = itemView.findViewById(R.id.imgLike);
            imgDslike = itemView.findViewById(R.id.imgDslike);          imgUser = itemView.findViewById(R.id.fotoUser);
            btnMenu = (Button) itemView.findViewById(R.id.btnMenu);
        }
    }
}
