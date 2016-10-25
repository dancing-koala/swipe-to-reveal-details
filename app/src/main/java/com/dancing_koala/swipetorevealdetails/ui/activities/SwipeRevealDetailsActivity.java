package com.dancing_koala.swipetorevealdetails.ui.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.dancing_koala.swipetorevealdetails.R;
import com.dancing_koala.swipetorevealdetails.ui.adapters.CarouselPagerAdapter;

public class SwipeRevealDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_reveal_details);

        ViewPager carousel = (ViewPager) findViewById(R.id.vp_carousel);
        carousel.setAdapter(new CarouselPagerAdapter(this));
        carousel.setOffscreenPageLimit(3);
    }
}
