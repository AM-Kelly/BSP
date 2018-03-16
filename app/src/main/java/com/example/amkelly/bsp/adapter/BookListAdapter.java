package com.example.amkelly.bsp.adapter;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amkelly.bsp.fragment.BookEditFragment;
import com.example.amkelly.bsp.login.LoginAuth;
import com.example.amkelly.bsp.provider.BookProvider;
import com.example.amkelly.bsp.R;
import com.example.amkelly.bsp.interfaces.OnEditTask;
import com.squareup.picasso.Picasso;

/**
 * Created by Adam on 03/03/2018.
 */

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder>
{
    Cursor cursor;
    int bookPriceColumnIndex;
    int bookAbstractColumnIndex;
    int bookIsbnColumnIndex;
    int bookAuthorColumnIndex;
    int bookTitleColumnIndex;
    int bookIdColumnIndex;

    public void swapCursor(Cursor c)
    {
        cursor = c;
        if (cursor!=null)
        {
            cursor.moveToFirst();
            bookPriceColumnIndex = cursor.getColumnIndex(BookProvider.COLUMN_BOOKPRICE);
            bookAbstractColumnIndex = cursor.getColumnIndex(BookProvider.COLUMN_BOOKABSTRACT);
            bookIsbnColumnIndex = cursor.getColumnIndex(BookProvider.COLUMN_BOOKISBN);
            bookAuthorColumnIndex = cursor.getColumnIndex(BookProvider.COLUMN_BOOKAUTHOR);
            bookTitleColumnIndex = cursor.getColumnIndex(BookProvider.COLUMN_BOOKTITLE);
            bookIdColumnIndex = cursor.getColumnIndex(BookProvider.COLUMN_BOOKID);
        }
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i)
    {
        //Create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_book, parent, false);

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
        TextView bookTitleView;
        TextView bookAbstractView;
        ImageView bookImageView;

        public ViewHolder(CardView card)
        {
            super(card);
            cardView = card;
            /**The code below is for the card view -> requires modifying*/
            bookTitleView = (TextView)card.findViewById(R.id.text1);
            bookAbstractView = (TextView)card.findViewById(R.id.text2);
            bookImageView = (ImageView)card.findViewById(R.id.image);
        }
    }

    public static String getImageUrlForTask(long taskId)
    {
        //return "http://lorempixel.com/600/400/cats/?fakeId=" + taskId;
        return "http://covers.openlibrary.org/b/isbn/";
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i)
    {
        final Context context = viewHolder.bookTitleView.getContext();
        final long id = getItemId(i);

        //set the text of the card
        /** This code is also for the card -> requires modifying */
        cursor.moveToPosition(i);
        viewHolder.bookTitleView.setText(cursor.getString(bookTitleColumnIndex));
        viewHolder.bookAbstractView.setText(cursor.getString(bookAbstractColumnIndex));

        //Create string components
        String baseurl = BookListAdapter.getImageUrlForTask(id);
        String ISBN = cursor.getString(bookIsbnColumnIndex);
        String endurlsize = "-L.jpg";

        //Set image thubmnail
        Picasso.with(context)
                .load(baseurl + ISBN + endurlsize)
                .into(viewHolder.bookImageView);

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
                /** The below function will check if the user is an admin or not**/
                boolean admin = LoginAuth.adminCheck();
                if (admin)
                {
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.delete_q)
                            .setMessage(viewHolder.bookTitleView.getText())
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
                }
                return true;
            }
        });
    }
    @Override
    public long getItemId(int position)
    {
        cursor.moveToPosition(position);
        return cursor.getLong(bookIdColumnIndex);
    }
    void deleteTask(Context context, long id)
    {
        context.getContentResolver()
                .delete(
                        ContentUris.withAppendedId(
                                BookProvider.CONTENT_URI, id),
                                null,null);
    }
}
