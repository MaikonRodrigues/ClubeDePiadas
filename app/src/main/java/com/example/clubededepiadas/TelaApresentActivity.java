package com.example.clubededepiadas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class TelaApresentActivity extends AppCompatActivity {
    ViewPager mViewPager;
    LinearLayout mDotsLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_apresent);

        mViewPager = (ViewPager) findViewById(R.id.slidViewPager);
        mDotsLayout = (LinearLayout) findViewById(R.id.dotsLayout);
    }
}
