package com.bitsescrow.app.bitsescrow.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitsescrow.app.bitsescrow.R;
import com.bitsescrow.app.bitsescrow.models.Slide;

import java.util.List;


/**
 * Created by Lekan Adigun on 3/26/2018.
 */

public class SlideAdapter extends PagerAdapter {

    private List<Slide> mSlides;
    private Context mContext;

    public SlideAdapter(Context context, List<Slide> slides) {
        mContext = context;
        mSlides = slides;
    }

    @Override
    public int getCount() {
        return mSlides.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_slide, container, false);

        ImageView icon = view.findViewById(R.id.iv_slide);
        TextView title = view.findViewById(R.id.tv_title_slide);
        TextView subTitle = view.findViewById(R.id.tv_subtitle_entry_activity);
        icon.setColorFilter(ContextCompat.getColor(mContext, R.color.white));

        Slide slide = mSlides.get(position);

        icon.setImageResource(slide.getIcon());
        title.setText(slide.getTitle());
        subTitle.setText(slide.getSubTitle());

        container.addView(view);
        return view;
    }
}
