package com.navnoire.myrecipes;

import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by navnoire on 28/10/17 in MyRecipes project
 */

public class RecipeLab {
    private static RecipeLab sRecipeLab;

    private Context mContext;
    private List<RecipeKt> mRecipes;

    public static RecipeLab get(Context context) {
        if (sRecipeLab == null) {
            sRecipeLab = new RecipeLab(context);
        }
        return sRecipeLab;
    }

    public RecipeLab(Context context) {
        mContext = context.getApplicationContext();
        mRecipes = new ArrayList<>();
    }

    public void addRecipe(RecipeKt recipe) {
        mRecipes.add(recipe);
    }

    public RecipeKt getRecipe(String url) {
        Iterator<RecipeKt> iterator = mRecipes.iterator();
        while (iterator.hasNext()) {
            RecipeKt item = iterator.next();
            if (item.getUrl().equals(url)) {
                return item;
            }
        }
        return null;
    }
}
