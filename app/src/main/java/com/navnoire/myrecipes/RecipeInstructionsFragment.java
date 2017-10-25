package com.navnoire.myrecipes;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
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
 * Created by navnoire on 23/10/17 in MyRecipes project
 */

public class RecipeInstructionsFragment extends Fragment {
    private static final String TAG = "RecipeInstructionsFr";
    public static final String ARG_INSTRUCTIONS = "argText";

    private ArrayList<ArrayList<String>> mInstructions = new ArrayList<>();
    private RecyclerView mInstructionsRecyclerView;
    private InstructionsAdapter mAdapter;
    private ImageDownloader<InstructionsViewHolder> mImageDownloader;

    public static RecipeInstructionsFragment newInstance(ArrayList<ArrayList<String>> instructions) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_INSTRUCTIONS, instructions);
        RecipeInstructionsFragment fragment = new RecipeInstructionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mInstructions = (ArrayList<ArrayList<String>>) getArguments().getSerializable(ARG_INSTRUCTIONS);

        Handler mResponseHandler = new Handler();
        mImageDownloader = new ImageDownloader<>(mResponseHandler);
        mImageDownloader.setImageDownloadListener(new ImageDownloader.imageDownloadListener<InstructionsViewHolder>() {
            @Override
            public void onImageDownloaded(Bitmap image, InstructionsViewHolder targetHolder) {
                targetHolder.bindDrawable(new BitmapDrawable(getResources(),image));

            }
        });
        mImageDownloader.start();
        mImageDownloader.getLooper();
        Log.d(TAG, "onCreate: downloader thread started");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        mInstructionsRecyclerView = v.findViewById(R.id.recipe_list);
        mInstructionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration divider = new DividerItemDecoration(mInstructionsRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mInstructionsRecyclerView.addItemDecoration(divider);

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
            mAdapter = new InstructionsAdapter(mInstructions);
            mInstructionsRecyclerView.setAdapter(mAdapter);
    }
    private class InstructionsAdapter extends RecyclerView.Adapter<InstructionsViewHolder> {
        private List<ArrayList<String>> mInstructions;

        public InstructionsAdapter(List<ArrayList<String>> instructions) {
            mInstructions = instructions;

        }

        @Override
        public InstructionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.instructions_list_item, parent, false);
            return new InstructionsViewHolder(v);
        }

        @Override
        public void onBindViewHolder(InstructionsViewHolder holder, int position) {
            String currentStepText = mInstructions.get(position).get(0);
            holder.bind(currentStepText);

            String imageUrl = mInstructions.get(position).get(1);
            if (imageUrl != null) {
                mImageDownloader.requestImageDownload(imageUrl, holder);
            } else {
                holder.bindDrawable(null);
            }

        }

        @Override
        public int getItemCount() {
            return mInstructions.size();
        }

        @Override
        public int getItemViewType(int position) {
            return R.layout.instructions_list_item;
        }
    }

    private class InstructionsViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        private TextView mTextView;
        private ImageView mImageView;

        public InstructionsViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            mTextView = itemView.findViewById(R.id.instruction_step_text);
            mImageView = itemView.findViewById(R.id.instruction_step_image);
        }

        @Override
        public void onClick(View view) {
            view.setBackgroundColor(getResources().getColor(R.color.item_clicked_color));
        }

        public void bind(String stepText) {

            mTextView.setText(stepText);
        }

        public void bindDrawable (Drawable stepImage) {
            mImageView.setImageDrawable(stepImage);
        }
    }
}
