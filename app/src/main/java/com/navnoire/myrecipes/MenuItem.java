package com.navnoire.myrecipes;

import android.graphics.drawable.Drawable;

/**
 * Created by navnoire on 26/10/17 in MyRecipes project
 */

public class MenuItem {
    private String mTitle;
    private String mUrl;
    private Drawable mMainImage;

    public MenuItem(String title, String url) {
        mTitle = title;
        mUrl = url;
    }

    public Drawable getMainImage() {
        return mMainImage;
    }

    public void setMainImage(Drawable mainImage) {
        mMainImage = mainImage;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
}
