package com.example.amkelly.tasks.adapter;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amkelly.tasks.R;
import com.example.amkelly.tasks.interfaces.OnEditTask;
import com.example.amkelly.tasks.provider.TaskProvider;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by Adam on 03/03/2018.
 */

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder>
{
    /**static String[] fakeData = new String[]
    {
            "One",
            "Two",
            "Three",            this is the old data that was populating the view - redundant now
            "Four",
            "Five",
            "Ah ... ah ... ah!"
    };*/

    Cursor cursor;
    int titleColumnIndex;
    int notesColumnIndex;
    int idColumnIndex;

    public void swapCursor(Cursor c)
    {
        cursor = c;
        if (cursor!=null)
        {
            cursor.moveToFirst();
            titleColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_TITLE);
            notesColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_NOTES);
            idColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_TASKID);
        }
        notifyDataSetChanged();
    }
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
        return cursor!=null ? cursor.getCount() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        TextView titleView;
        TextView notesView;
        ImageView imageView;

        public ViewHolder(CardView card)
        {
            super(card);
            cardView = card;
            Log.d("Passed","test");
            titleView = (TextView)card.findViewById(R.id.text1);
            notesView = (TextView)card.findViewById(R.id.text2);
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
        final long id = getItemId(i);

        //set the text
        cursor.moveToPosition(i);
        viewHolder.titleView.setText(cursor.getString(titleColumnIndex));
        viewHolder.notesView.setText(cursor.getString(notesColumnIndex));

        //Set image thubmnail
        Picasso.with(context)
                .load(getImageUrlForTask(id))
                .into(viewHolder.imageView);

        //Setting the click action
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OnEditTask) context).editTask(id);
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

                                        deleteTask(context, id);
                                    }
                                })
                        .show();
                return true;
            }
        });
    }
    @Override
    public long getItemId(int position)
    {
        cursor.moveToPosition(position);
        return cursor.getLong(idColumnIndex);
    }
    void deleteTask(Context context, long id)
    {
        context.getContentResolver()
                .delete(
                        ContentUris.withAppendedId(
                                TaskProvider.CONTENT_URI, id),
                                null,null);
    }
}
