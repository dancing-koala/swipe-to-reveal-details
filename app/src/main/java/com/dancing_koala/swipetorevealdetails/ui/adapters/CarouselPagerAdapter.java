package com.dancing_koala.swipetorevealdetails.ui.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dancing_koala.swipetorevealdetails.R;

public class CarouselPagerAdapter extends PagerAdapter {

    private Context context;

    public CarouselPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View item = View.inflate(context, R.layout.strd_item, null);

        TextView itemName = (TextView) item.findViewById(R.id.tv_item_name);
        itemName.setText(itemName.getText() + " " + position);

        int imgResID = context.getResources().getIdentifier("landscape_" + position, "drawable", context.getPackageName());
        ((ImageView) item.findViewById(R.id.iv_item_image)).setImageResource(imgResID);

        container.addView(item);
        return item;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object.equals(view);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
