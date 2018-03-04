package com.example.amkelly.tasks.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amkelly.tasks.R;
import com.example.amkelly.tasks.interfaces.OnEditTask;
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

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i)
    {
        final Context context = viewHolder.titleView.getContext();
        viewHolder.titleView.setText(fakeData[i]);

        //Checking data is assigned correctly
        Log.e("Vars: ",fakeData[i]);

        //Set image thubmnail
        Picasso.with(context)
                .load(getImageUrlForTask(i))
                .into(viewHolder.imageView);

        //Setting the click action
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OnEditTask) context).editTask(i);
            }
        });
        //Set the long press action
        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.delete_q)
                        .setMessage(viewHolder.titleView.getText())
                        .setCancelable(true)
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(
                                R.string.delete, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {

                                        deleteTask(context, i);
                                    }
                                })
                        .show();
                return true;
            }
        });
    }
    void deleteTask(Context context, long id)
    {
        Log.d("TaskListAdapter", "Deleted!");
    }
}
