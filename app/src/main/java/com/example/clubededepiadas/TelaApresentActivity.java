package com.example.clubededepiadas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.clubededepiadas.Adapters.SlidAdapter;

public class TelaApresentActivity extends AppCompatActivity {
    ViewPager mViewPager;
    LinearLayout mDotsLayout;

    TextView[] mDots;

    SlidAdapter slidAdapter;
    Button btnProximo, btnVoltar;
    int paginaAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_apresent);

        mViewPager = (ViewPager) findViewById(R.id.slidViewPager);
        mDotsLayout = (LinearLayout) findViewById(R.id.dotsLayout);

        slidAdapter = new SlidAdapter(TelaApresentActivity.this);
        mViewPager.setAdapter(slidAdapter);

        addDotsIndicator(0);

        mViewPager.addOnPageChangeListener(viewListner);
        btnProximo = (Button) findViewById(R.id.btnProx);
        btnVoltar = (Button) findViewById(R.id.btnVoltar);

        btnProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnProximo.getText().equals("Finish")){
                    Intent intent = new Intent(TelaApresentActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    mViewPager.setCurrentItem(paginaAtual + 1);
                }
            }
        });
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(paginaAtual - 1);
            }
        });
    }

    public void addDotsIndicator(int position){
            mDots = new TextView[3];
            mDotsLayout.removeAllViews();

            for (int i = 0; i < mDots.length; i++){
                mDots[i] = new TextView(this);
                mDots[i].setText(Html.fromHtml("&#8226;"));
                mDots[i].setTextSize(35);
                mDots[i].setTextColor(getResources().getColor(R.color.colorPrimary));

                mDotsLayout.addView(mDots[i]);
            }
            if (mDots.length > 0){
                mDots[position].setTextColor(getResources().getColor(R.color.branco));
            }

    }
    ViewPager.OnPageChangeListener viewListner = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            paginaAtual = position;

            if (position == 0){

                btnProximo.setEnabled(true);
                btnVoltar.setEnabled(false);
                btnVoltar.setVisibility(View.INVISIBLE);
                btnProximo.setText("Proximo");

            }else if (position == mDots.length -1 ){

                btnProximo.setEnabled(true);
                btnVoltar.setEnabled(true);
                btnVoltar.setVisibility(View.VISIBLE);

                btnProximo.setText("Finish");

            }else{

                btnProximo.setEnabled(true);
                btnVoltar.setEnabled(true);
                btnVoltar.setVisibility(View.VISIBLE);

                btnProximo.setText("Proximo");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
