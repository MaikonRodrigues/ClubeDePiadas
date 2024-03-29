package com.example.clubededepiadas.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.clubededepiadas.R;
import com.example.clubededepiadas.R.layout;

import de.hdodenhof.circleimageview.CircleImageView;

public class SlidAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SlidAdapter(Context context) {
        this.context = context;
    }

    public int[] slidImage = {
            R.mipmap.ic_edit_jokes,
            R.mipmap.ic_like_circle,
            R.mipmap.ic_riso

    };
    public String[] slidTitulos = {
            "Crie Piadas",
            "Curta as Melhores",
            "Se divirta com Amigos"
    };
    public String[] slidDesc = {
            "O Jokes permite que você crie piadas " +
                    "listando por categorias",
            "O Jokes Rankea as piadas por quantidade " +
                    "de Likes, deixando você sempre atualizado" +
                    " com as piadas mais engraçadas",
            "Curta e compartilhe as " + "\n" +
                    "melhores piadas " +
                    "nas principais redes sociais"
    };

    @Override
    public int getCount() {
        return slidTitulos.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slid_layout, container, false);

        CircleImageView imageSlid = (CircleImageView) view.findViewById(R.id.imageSlid);
        TextView        txtTitulo = (TextView) view.findViewById(R.id.txtTitulo);
        TextView        txtDesc   = (TextView) view.findViewById(R.id.txtDesc);

        imageSlid.setImageResource(slidImage[position]);
        txtTitulo.setText(slidTitulos[position]);
        txtDesc.setText(slidDesc[position]);

        container.addView(view);
        return  view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
