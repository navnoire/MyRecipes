package com.navnoire.myrecipes;

import android.support.v4.app.Fragment;

public class RecipeActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return RecipeFragment.newInstance();
    }
}
