package com.example.mushroom_inspector;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * this saved my life:
 * @link https://code.tutsplus.com/tutorials/getting-started-with-recyclerview-and-cardview-on-android--cms-23465
 */
public class ShroomResultsAdapter extends RecyclerView.Adapter<ShroomResultsAdapter.OneResult> {
    List<String> mylist;

    //create constructor to initialize context and data sent from main activity.
    public ShroomResultsAdapter(List<String> mylist) {
        this.mylist = mylist;
    }

    @Override
    public OneResult onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_result, parent, false);
        OneResult holder = new OneResult(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(OneResult result, int position) {
        result.title.setText(mylist.get(position));
        result.desc.setText("Edible: unknown");
    }

    @Override
    public int getItemCount() {
        return mylist.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class OneResult extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView desc;

        //contructor for getting reference to the widget
        public OneResult(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            title = (TextView) itemView.findViewById(R.id.title);
            desc = (TextView) itemView.findViewById(R.id.desc);
        }
    }
}