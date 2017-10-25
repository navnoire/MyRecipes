package com.navnoire.myrecipes;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by navnoire on 19/10/17 in MyRecipes project
 */

public class Recipe {

    private String mUrl;
    private String mTitle;
    private String mSummary;
    private String mIngredientsString;
    private ArrayList<ArrayList<String>> mInstructions = new ArrayList<>();
    private List<Drawable> mStepImages = new ArrayList<>();

    private String mMainImageUrl;
    private Drawable mainImage;

    public ArrayList<ArrayList<String>> getInstructions() {
        return mInstructions;
    }

    public void setInstructions(ArrayList<ArrayList<String>> instructions) {
        mInstructions = instructions;
    }

    public String getIngredientsString() {
        return mIngredientsString;
    }

    public void setIngredientsString(String ingredientsString) {
        mIngredientsString = ingredientsString;
    }

    public String getMainImageUrl() {
        return mMainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        mMainImageUrl = mainImageUrl;
    }

    public List<Drawable> getStepImages() {
        return mStepImages;
    }

    public void setStepImages(List<Drawable> stepImages) {
        mStepImages = stepImages;
    }

    public Drawable getMainImage() {
        return mainImage;
    }

    public void setMainImage(Drawable mainImage) {
        this.mainImage = mainImage;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }


    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
