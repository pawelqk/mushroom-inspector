package com.example.mushroom_inspector;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.WeakHashMap;

/**
 * this saved my life:
 * @link https://code.tutsplus.com/tutorials/getting-started-with-recyclerview-and-cardview-on-android--cms-23465
 */
public class ShroomResultsAdapter extends RecyclerView.Adapter<ShroomResultsAdapter.ResultViewHolder> {
    private final List<ResultsActivity.Result> mylist;
    private final View.OnClickListener onClickListener;

    //create constructor to initialize context and data sent from main activity.
    public ShroomResultsAdapter(List<ResultsActivity.Result> mylist, final View.OnClickListener onClickListener) {
        this.mylist = mylist;
        this.onClickListener = onClickListener;
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_result, parent, false);
        v.setOnClickListener(onClickListener);
        ResultViewHolder holder = new ResultViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ResultViewHolder resultViewHolder, int position) {
        ResultsActivity.Result result = mylist.get(position);
        resultViewHolder.title.setText(result.speciesWithConfidence);
        resultViewHolder.desc.setText(result.data.edibility);
    }

    @Override
    public int getItemCount() {
        return mylist.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView desc;

        //contructor for getting reference to the widget
        public ResultViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            title = (TextView) itemView.findViewById(R.id.title);
            desc = (TextView) itemView.findViewById(R.id.desc);
        }
    }
}