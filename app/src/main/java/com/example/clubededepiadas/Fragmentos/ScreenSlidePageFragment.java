package com.example.clubededepiadas.Fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.clubededepiadas.R;

public class ScreenSlidePageFragment extends Fragment {
    TextView txtTexto;
    int numTela;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        txtTexto = (TextView) rootView.findViewById(R.id.texto1);
        return rootView;
    }

    public int getNumTela() {
        return numTela;
    }

    public void setNumTela(int numTela) {
        this.numTela = numTela;
    }
}