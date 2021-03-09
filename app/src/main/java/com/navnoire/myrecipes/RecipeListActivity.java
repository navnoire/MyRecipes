package com.navnoire.myrecipes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by navnoire on 25/10/17 in MyRecipes project
 */

public class RecipeListActivity extends SingleFragmentActivity
implements RecipeListFragment.Callbacks, MenuListFragment.Callbacks {
    private static final String SAVED_TAG = "savedTag";
    private static final String SAVED_TITLE = "savedTitle";
    String tag;
    String title;

    @Override
    protected Fragment createFragment() {
        return MenuListFragment.newInstance("https://gotovim-doma.ru");
    }

    @Override
    public void onItemClicked(MenuItem menuItem) {
        title = menuItem.getTitle();
        FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().addToBackStack(null)
                    .replace(R.id.fragment_container, MenuListFragment.newInstance(menuItem.getUrl()), menuItem.getUrl())
                    .commit();
    }

    @Override
    public void onEndOfMenuReached(String url) {
        tag = url;
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().addToBackStack(url)
                .add (R.id.fragment_container, RecipeListFragment.newInstance(url,title))
                .commit();
    }

    @Override
    public void onItemClicked(RecipeKt recipe) {
        RecipeLab.get(this).addRecipe(recipe);
        Intent intent = RecipeActivity.newIntent(this, recipe.getUrl());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment badFragment = fm.findFragmentByTag(tag);
        if(badFragment != null) {
            fm.popBackStack();
        }
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVED_TAG, tag);
        outState.putString(SAVED_TITLE, title);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tag = savedInstanceState.getString(SAVED_TAG);
        title = savedInstanceState.getString(SAVED_TITLE);
    }
}
