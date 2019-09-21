package com.example.clubededepiadas;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.clubededepiadas.Classes.ZoomOutPageTransformer;
import com.example.clubededepiadas.Fragmentos.ScreenSlidePageFragment;

public class ScreenSlidePagerActivity extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);




        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // Se o usuário estiver atualmente olhando para a primeira etapa, permita que o sistema lide com o
            // Botão "voltar. Isso chama finish () nesta atividade e exibe a pilha de trás.
            super.onBackPressed();
        } else {
            // Caso contrário, selecione a etapa anterior.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * Um  pager adapter simples que representa 3 objetos ScreenSlidePageFragment, em
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                Toast.makeText(ScreenSlidePagerActivity.this, "Tela "+position, Toast.LENGTH_LONG).show();
            }else if (position == 1){
                Toast.makeText(ScreenSlidePagerActivity.this, "Tela "+position, Toast.LENGTH_LONG).show();
            }else if (position == 2){
                Toast.makeText(ScreenSlidePagerActivity.this, "Tela "+position, Toast.LENGTH_LONG).show();
            }
            return new ScreenSlidePageFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}