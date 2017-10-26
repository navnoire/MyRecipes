package com.navnoire.myrecipes;

import android.support.v4.app.Fragment;

/**
 * Created by navnoire on 25/10/17 in MyRecipes project
 */

public class RecipeListActivity extends SingleFragmentActivity
implements RecipeListFragment.Callbacks {
    @Override
    protected Fragment createFragment() {
        return RecipeListFragment.newInstance("https://gotovim-doma.ru/category/17-salaty");
    }

    @Override
    public void onItemClicked(Recipe recipe) {

    }
}
