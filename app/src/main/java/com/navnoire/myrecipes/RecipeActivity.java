package com.navnoire.myrecipes;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class RecipeActivity extends SingleFragmentActivity {
    public static final String EXTRA_RECIPE_URL = "com.navnoire.myrecipes.recipe_url";

    @Override
    protected Fragment createFragment() {
        String url = getIntent().getStringExtra(EXTRA_RECIPE_URL);
        return RecipeFragment.newInstance(url);
    }

    public static Intent newIntent(Context packageContext, String url) {
        Intent intent = new Intent(packageContext, RecipeActivity.class);
        intent.putExtra(EXTRA_RECIPE_URL, url);
        return intent;
    }
}
