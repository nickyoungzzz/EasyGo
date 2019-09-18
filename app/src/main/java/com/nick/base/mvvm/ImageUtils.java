package com.nick.base.mvvm;

import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageUtils
{
    @BindingAdapter(value = {"android:imageUrl", "android:error"})
    public static void loadImage(ImageView imageView, String url, int error)
    {
        Glide.with(imageView.getContext()).load(url).centerCrop().error(error).into(imageView);
    }

    @BindingConversion
    public static Drawable toDrawable(int id)
    {
        return new ColorDrawable(id);
    }
}
