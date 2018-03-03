package com.example.amkelly.tasks.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amkelly.tasks.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Adam on 03/03/2018.
 */

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder>
{
    static String[] fakeData = new String[]
    {
            "One",
            "Two",
            "Three",
            "Four",
            "Five",
            "Ah ... ah ... ah!"
    };
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i)
    {
        //Create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_task, parent, false);

        //Wrap it in a ViewHolder
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        Context context = viewHolder.titleView.getContext();
        viewHolder.titleView.setText(fakeData[i]);

        //Checking data is assigned correctly
        Log.e("Vars: ",fakeData[i]);

        //Set image thubmnail
        Picasso.with(context)
                .load(getImageUrlForTask(i))
                .into(viewHolder.imageView);
    }

    @Override
    public int getItemCount()
    {
        return fakeData.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        TextView titleView;
        ImageView imageView;

        public ViewHolder(CardView card)
        {
            super(card);
            cardView = card;
            Log.d("Passed","test");
            titleView = (TextView)card.findViewById(R.id.text1);
            imageView = (ImageView)card.findViewById(R.id.image);
        }
    }
    public static String getImageUrlForTask(long taskId)
    {
        return "http://lorempixel.com/600/400/cats/?fakeId=" + taskId;
    }
}
