package com.example.clubededepiadas.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clubededepiadas.Classes.Categoria;
import com.example.clubededepiadas.MainActivity;
import com.example.clubededepiadas.R;

import java.util.List;

public class CategoriaAdapterMenu extends RecyclerView.Adapter<CategoriaAdapterMenu.CategoriaHolder> {
    List<Categoria> listCategorias; Context context;
    RecyclerView mRecyclerView;

    public CategoriaAdapterMenu(List<Categoria> listCategorias, Context context,  RecyclerView recyclerView) {
        this.listCategorias = listCategorias;
        this.context = context;
        this.mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public CategoriaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria_menu, parent, false);


        return new CategoriaAdapterMenu.CategoriaHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoriaHolder holder, final int position) {
        holder.btnCatMenu.setText(listCategorias.get(position).getNome());

        holder.btnCatMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("keyName",  listCategorias.get(position).getId());  // pass your values and retrieve them in the other Activity using keyName
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listCategorias.size();
    }

    public class CategoriaHolder extends RecyclerView.ViewHolder {
        Button btnCatMenu;  RecyclerView myrecycleView;

        public CategoriaHolder(@NonNull View itemView) {
            super(itemView);
            btnCatMenu = (Button)itemView.findViewById(R.id.btnCatMenu);
            myrecycleView = itemView.findViewById(R.id.mRecyclerView);
        }
    }


}
