package com.example.learner.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.learner.R;

public class UploadPagerAdapter extends PagerAdapter {
    private Context mContext;
    private String[] imageList;

    public UploadPagerAdapter(Context context, String[] imageList)
    {
        this.mContext = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_train, null);

        ImageView imageView = view.findViewById(R.id.viewpager);

        Bitmap capture_img = BitmapFactory.decodeFile(imageList[position]);
        imageView.setImageBitmap(capture_img);

        return view;
    }

    @Override
    public int getCount() {
        return imageList.length;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == (View)o);
    }
}