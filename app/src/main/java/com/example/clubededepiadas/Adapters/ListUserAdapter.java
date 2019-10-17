package com.example.clubededepiadas.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clubededepiadas.Classes.User;
import com.example.clubededepiadas.ListarUserActivity;
import com.example.clubededepiadas.MainActivity;
import com.example.clubededepiadas.R;
import com.example.clubededepiadas.SettingsUserActivity;
import com.example.clubededepiadas.ShowUsersActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.PiadaHolder> {
    List<User> listUser; Context context; User user;

    public ListUserAdapter(List<User> listUser, Context context) {
        this.listUser = listUser;
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
        View vista =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ListUserAdapter.PiadaHolder(vista);
    }


    @Override
    public void onBindViewHolder(@NonNull final PiadaHolder holder, final int position) {
        holder.nomeUser.setText(listUser.get(position).getNome());
        getUser(holder.nomeUser, holder.txtDataPost, listUser.get(position).getId(), holder.imgUser);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowUsersActivity.class).putExtra("id", listUser.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    private  void getUser(final TextView nomeUser, final TextView dataPost, final String id, final ImageView imageView) {
        Ion.with(context)
                //  http://192.168.1.4/ApiLaravelForAndroidTeste/public/api/piadas
                .load("http://"+R.string.ipServidor+"/public/api/user/"+id)
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
                                }

                            }
                        }catch (Exception erro){
                           // Toast.makeText(context, "Erro na Requisição", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    public void getImage(User user, final ImageView imageView){
       // Toast.makeText(context, "avatar"+user.getAvatar(), Toast.LENGTH_LONG).show();
        Ion.with(context)
                .load("http://"+R.string.ipServidor+"/public/api/getImage/"+user.getAvatar())
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
        return listUser.size();
    }

    public class PiadaHolder extends RecyclerView.ViewHolder {
        TextView  nomeUser, txtDataPost;
        CircleImageView imgUser;
        CardView cardView;
        public PiadaHolder(@NonNull View itemView) {
            super(itemView);
            nomeUser = itemView.findViewById(R.id.nmUserBusca);
            txtDataPost = itemView.findViewById(R.id.txtDataCria);
            imgUser = itemView.findViewById(R.id.fotoUserBusc);
            cardView = itemView.findViewById(R.id.card_view_user_item);
        }
    }

}
