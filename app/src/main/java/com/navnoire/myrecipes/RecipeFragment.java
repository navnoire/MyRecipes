package com.navnoire.myrecipes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by navnoire on 19/10/17 in MyRecipes project
 */

public class RecipeFragment extends Fragment {
    private static final String TAG = "RecipeFragment";

    private String mUrl = "https://gotovim-doma.ru/recipe/2434-tort-vozdushnyy-snikers";
    private Recipe mRecipe = new Recipe();
    private TextView mTitleField;
    private TextView mSummaryField;
    private ImageView mMainImage;
    private TextView mIngredientsTextView;
    private Button mInstructionsButton;

    public static RecipeFragment newInstance() {

        Bundle args = new Bundle();

        RecipeFragment fragment = new RecipeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        new GetRecipeTask(mUrl).execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recipe, container, false);

        mTitleField = v.findViewById(R.id.recipe_title);
        mSummaryField = v.findViewById(R.id.recipe_summary);
        mIngredientsTextView = v.findViewById(R.id.recipe_ingredients_textView);

        mMainImage = v.findViewById(R.id.recipe_main_image);
        Point outSize = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(outSize);
        mMainImage.setMaxWidth(outSize.x / 4);

        mInstructionsButton = v.findViewById(R.id.btn_instructions);
        mInstructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //запустить фрагмент с инструкциями
                Fragment instructions = RecipeInstructionsFragment.newInstance(mRecipe.getInstructions());
                getFragmentManager().beginTransaction()
                        .addToBackStack(TAG)
                        .replace(R.id.fragment_container, instructions)
                        .commit();
            }
        });

        UpdateUI();

        return v;
    }

    private void UpdateUI() {
        mTitleField.setText(mRecipe.getTitle());
        mSummaryField.setText(mRecipe.getSummary());
        mIngredientsTextView.setText(mRecipe.getIngredientsString());
        mMainImage.setImageDrawable(mRecipe.getMainImage());
    }


    private class GetRecipeTask extends AsyncTask<Void, Void, Recipe> {
        String mUrl;

        public GetRecipeTask(String url) {
            mUrl = url;
        }

        @Override
        protected Recipe doInBackground(Void... voids) {
            RecipeFetcher rf = new RecipeFetcher();
            Recipe recipe = rf.fetchRecipe(mUrl);

            try {
                byte[] imageBytes = rf.getUrlBytes(recipe.getMainImageUrl());
                Bitmap b = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), b);
                drawable.setCircular(true);

                recipe.setMainImage(drawable);
            } catch (IOException ioe) {
                Log.e(TAG, "doInBackground: Main Image not loaded", ioe);
            }
            return recipe;
        }

        @Override
        protected void onPostExecute(Recipe recipe) {
            mRecipe = recipe;
            UpdateUI();
        }
    }
}
