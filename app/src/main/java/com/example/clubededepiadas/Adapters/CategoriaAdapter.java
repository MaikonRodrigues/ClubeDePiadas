package com.example.clubededepiadas.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clubededepiadas.Classes.Categoria;
import com.example.clubededepiadas.R;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaHolder> {
    List<Categoria> listCategorias; Context context;
    String  ip = "192.168.1.5", categoria_id;
    Categoria categoria = new Categoria("geral");
    Dialog dialogCat, dialog;


    public CategoriaAdapter(List<Categoria> listCategorias, Dialog dialogCat,  Dialog dialog,  Context context) {
        this.listCategorias = listCategorias;
        this.context = context;
        this.dialogCat = dialogCat;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public CategoriaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria, parent, false);

        return new CategoriaAdapter.CategoriaHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoriaHolder holder, final int position) {
        holder.nome.setText(listCategorias.get(position).getNome());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoria.setId(listCategorias.get(position).getId());
                categoria.setNome(listCategorias.get(position).getNome());
                setCategoria_id(categoria.getId());
                TextView  tx = (TextView) dialog.findViewById(R.id.txtCategoria_id);
                tx.setText(categoria.getNome());
                dialogCat.cancel();
            }
        });
    }



    @Override
    public int getItemCount() {
        return listCategorias.size();
    }

    public class CategoriaHolder extends RecyclerView.ViewHolder {
        TextView nome;
        CardView cardView;

        public CategoriaHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.txtNomeCat);
            cardView = (CardView)itemView.findViewById(R.id.card_item_categoria);
        }
    }

    public String getCategoria() {
        return categoria.getNome();
    }

    public void setCategoria(String nome) {
        this.categoria.setNome(nome);
    }

    public String getCategoria_id() {
        return categoria_id;
    }

    public void setCategoria_id(String id) {
        this.categoria_id = id;
    }
}
