package com.example.clubededepiadas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.clubededepiadas.Adapters.SlidAdapter;

public class TelaApresentActivity extends AppCompatActivity {
    ViewPager mViewPager;
    LinearLayout mDotsLayout;

    TextView[] mDots;

    SlidAdapter slidAdapter;
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
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
