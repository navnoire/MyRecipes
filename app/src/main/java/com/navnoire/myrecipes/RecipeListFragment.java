package com.navnoire.myrecipes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by navnoire on 25/10/17 in MyRecipes project
 */

public class RecipeListFragment extends Fragment {
    private static final String TAG = "RecipeListFragment";
    private static final String ARGS_URL_STRING = "argsUrlString";
    private static final String ARGS_MAIN_TITLE = "argsTitle";

    private TextView mListTitle;
    private String mUrl;
    private RecyclerView mRecyclerView;
    List<RecipeKt> mRecipeList = new ArrayList<>();
    private RecipeListAdapter mAdapter;
    private Callbacks mCallbacks;
    private ImageDownloader<RecipeItemHolder> mImageDownloader;

    public interface Callbacks {
        void onItemClicked(RecipeKt recipe);
    }

    public static RecipeListFragment newInstance(String url, String title) {

        Bundle args = new Bundle();
        args.putString(ARGS_URL_STRING, url);
        args.putString(ARGS_MAIN_TITLE, title);

        RecipeListFragment fragment = new RecipeListFragment();
        fragment.setArguments(args);
        Log.d(TAG, "newInstance: Created newInstance of RecipeListFragment");
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof Callbacks)) {
            throw new ClassCastException("Activity must implement callbacks");
        }

        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Handler mResponseHandler = new Handler();
        mImageDownloader = new ImageDownloader<>(mResponseHandler);
        mImageDownloader.setImageDownloadListener(new ImageDownloader.imageDownloadListener<RecipeItemHolder>() {
            @Override
            public void onImageDownloaded(Bitmap image, RecipeItemHolder target) {
                Drawable drawable = new BitmapDrawable(getResources(),image);
                target.bindDrawable(drawable);
            }
        });
        mImageDownloader.start();
        mImageDownloader.getLooper();

        mUrl = getArguments().getString(ARGS_URL_STRING);
        new FetchListTask(mUrl).execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        mListTitle = v.findViewById(R.id.recipe_list_title);
        mListTitle.setText(getArguments().getString(ARGS_MAIN_TITLE));

        mRecyclerView = v.findViewById(R.id.recipe_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        setupAdapter();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mImageDownloader.cleanQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageDownloader.quit();
    }

    private void setupAdapter() {
        mAdapter = new RecipeListAdapter(mRecipeList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private class RecipeItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitleTextView;
        ImageView mRecipeImageView;

        RecipeItemHolder(View itemView) {
            super(itemView);

            mTitleTextView = itemView.findViewById(R.id.instruction_step_text);
            mRecipeImageView = itemView.findViewById(R.id.instruction_step_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            RecipeKt currentItem = mRecipeList.get(getAdapterPosition());
            mCallbacks.onItemClicked(currentItem);
        }

        public void bindItem(String text) {
            mTitleTextView.setText(text);
        }

        public void bindDrawable(Drawable image) {
            mRecipeImageView.setImageDrawable(image);
        }
    }

    private class RecipeListAdapter extends RecyclerView.Adapter<RecipeItemHolder> {
        List<RecipeKt> mRecipeList;

        public RecipeListAdapter(List<RecipeKt> recipeList) {
            mRecipeList = recipeList;
        }

        @Override
        public RecipeItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.recipe_list_item, parent, false);
            return new RecipeItemHolder(v);
        }

        @Override
        public void onBindViewHolder(RecipeItemHolder holder, int position) {
            RecipeKt currentItem = mRecipeList.get(position);
            holder.bindItem(currentItem.getTitle());

            String imageUrl = currentItem.getMainImageUrl();
            if(imageUrl != null) {
                mImageDownloader.requestImageDownload(imageUrl, holder);
            }
        }

        @Override
        public int getItemCount() {
            return mRecipeList.size();
        }
    }

    private class FetchListTask extends AsyncTask<Void, Void, List<RecipeKt>> {
        private String recipeUrl;

        public FetchListTask(String url) {
            Log.d(TAG, "FetchListTask: AsyncTask in");
            recipeUrl = url;
        }

        @Override
        protected List<RecipeKt> doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: in");
            return new RecipeFetcher().fetchRecipeList(recipeUrl);
        }

        @Override
        protected void onPostExecute(List<RecipeKt> recipeItems) {
            super.onPostExecute(recipeItems);
            mRecipeList = recipeItems;
            setupAdapter();


        }
    }
}
