package com.navnoire.myrecipes;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by navnoire on 25/10/17 in MyRecipes project
 */

public class RecipeListFragment extends Fragment{
    private static final String TAG = "RecipeListFragment";
    private static final String ARGS_URL_STRING = "argsUrlString";

    private String mUrl;
    private RecyclerView mRecyclerView;
    List<MenuItem> mMenuItems = new ArrayList<>();
    private MenuAdapter mAdapter;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onItemClicked(MenuItem menuItem);
    }

    public static RecipeListFragment newInstance(String url) {

        Bundle args = new Bundle();
        args.putString(ARGS_URL_STRING, url);

        RecipeListFragment fragment = new RecipeListFragment();
        fragment.setArguments(args);
        Log.d(TAG, "newInstance: Created newInstance of RecipeListFragment");
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: in");
        mUrl = getArguments().getString(ARGS_URL_STRING);
        new FetchListTask(mUrl).execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        mRecyclerView = v.findViewById(R.id.recipe_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL );
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        setupAdapter();
        return v;
    }

    private void setupAdapter() {
        mAdapter = new MenuAdapter(mMenuItems);
        mRecyclerView.setAdapter(mAdapter);
    }

    private class MenuItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mNameTextView;

        public MenuItemHolder(View itemView) {
            super(itemView);

            mNameTextView = itemView.findViewById(android.R.id.text1);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            MenuItem currentItem = mMenuItems.get(getAdapterPosition());
            mCallbacks.onItemClicked(currentItem);
        }

        public void bindItem(String text) {
            mNameTextView.setText(text);
        }
    }

    private class MenuAdapter extends RecyclerView.Adapter<MenuItemHolder> {
        List<MenuItem> mMenuItemList;

        public MenuAdapter(List<MenuItem> menuItemList) {
            mMenuItemList = menuItemList;
        }

        @Override
        public MenuItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            return new MenuItemHolder(v);
        }

        @Override
        public void onBindViewHolder(MenuItemHolder holder, int position) {
            MenuItem currentItem = mMenuItemList.get(position);
            holder.bindItem(currentItem.getTitle());
        }

        @Override
        public int getItemCount() {
            return mMenuItemList.size();
        }
    }

    private class FetchListTask extends AsyncTask<Void, Void, List<MenuItem>> {
        private String menuUrl;

        public FetchListTask(String url) {
            Log.d(TAG, "FetchListTask: AsyncTask in");
            menuUrl = url;
        }

        @Override
        protected List<MenuItem> doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: in");
            return new RecipeFetcher().fetchList(menuUrl);
        }

        @Override
        protected void onPostExecute(List<MenuItem> menuItems) {
            super.onPostExecute(menuItems);
            mMenuItems = menuItems;
            setupAdapter();


        }
    }
}
