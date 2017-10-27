package com.navnoire.myrecipes;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by navnoire on 27/10/17 in MyRecipes project
 */

public class MenuListFragment extends Fragment {
    private static final String TAG = "MenuListFragment";
    private static final String ARGS_URL = "argsUrl";

    private String mUrl;
    private ListView mMenuList;
    private List<MenuItem> mMenuItems;
    private Callbacks mCallbacks;

    public static MenuListFragment newInstance(String url) {

        Bundle args = new Bundle();
        args.putString(ARGS_URL, url);

        MenuListFragment fragment = new MenuListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface Callbacks {
        void onItemClicked(MenuItem item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mUrl = getArguments().getString(ARGS_URL);
        mMenuItems = new ArrayList<>();

        new FetchListTask(mUrl).execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: in");
        View v = inflater.inflate(R.layout.fragment_menu_list, container, false);
        mMenuList = v.findViewById(R.id.menu_list_view);

        setupAdapter(mMenuItems);
        return v;
    }

    private void setupAdapter(List<MenuItem> items) {
        if (mMenuList.getAdapter() == null) {
            mMenuList.setAdapter(new MenuAdapter(items));
            Log.d(TAG, "setupAdapter: new adapter created");
        } else {
            ((MenuAdapter)mMenuList.getAdapter()).updateItems(items);
        }
    }

    private class MenuAdapter extends BaseAdapter {
        List<MenuItem> mMenuItemList;

        public MenuAdapter(List<MenuItem> menuItemList) {
            mMenuItemList = menuItemList;
        }

        public void updateItems (List<MenuItem> items) {
            mMenuItemList = items;
            notifyDataSetChanged();
            Log.d(TAG, "updateItems: items change notified");
        }

        @Override
        public int getCount() {
            return mMenuItemList.size();
        }

        @Override
        public MenuItem getItem(int position) {
            return mMenuItemList.get(position);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(
                        android.R.layout.simple_list_item_1, parent, false);

                ((TextView) convertView.findViewById(android.R.id.text1))
                        .setText(mMenuItemList.get(position).getTitle());

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallbacks.onItemClicked(mMenuItemList.get(position));
                    }
                });
            }
            return convertView;
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
            setupAdapter(mMenuItems);


        }
    }


}
