package com.navnoire.myrecipes;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by navnoire on 25/10/17 in MyRecipes project
 */

public class RecipeListActivity extends SingleFragmentActivity
implements RecipeListFragment.Callbacks {
    @Override
    protected Fragment createFragment() {
        return RecipeListFragment.newInstance("https://gotovim-doma.ru");
    }

    @Override
    public void onItemClicked(MenuItem menuItem) {
        if(menuItem.getMainImage() == null) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().addToBackStack(menuItem.getTitle())
                    .replace(R.id.fragment_container, RecipeListFragment.newInstance(menuItem.getUrl()))
                    .commit();
        }
    }
}
