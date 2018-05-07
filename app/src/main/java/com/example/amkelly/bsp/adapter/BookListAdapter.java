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

import com.example.amkelly.bsp.login.LoginAuth;
import com.example.amkelly.bsp.provider.BookProvider;
import com.example.amkelly.bsp.R;
import com.example.amkelly.bsp.interfaces.OnEditBook;
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
        if (cursor!=null)//if the cursor is not empty
        {
            cursor.moveToFirst();// Move to first record
            bookPriceColumnIndex = cursor.getColumnIndex(BookProvider.COLUMN_BOOKPRICE);
            bookAbstractColumnIndex = cursor.getColumnIndex(BookProvider.COLUMN_BOOKABSTRACT);
            bookIsbnColumnIndex = cursor.getColumnIndex(BookProvider.COLUMN_BOOKISBN);
            bookAuthorColumnIndex = cursor.getColumnIndex(BookProvider.COLUMN_BOOKAUTHOR);
            bookTitleColumnIndex = cursor.getColumnIndex(BookProvider.COLUMN_BOOKTITLE);
            bookIdColumnIndex = cursor.getColumnIndex(BookProvider.COLUMN_BOOKID);
        }
        notifyDataSetChanged();//calling the datasetchanged function
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
    }//Get the number of items within the database

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
            /**The code below is for the card view, it defines the card view*/
            bookTitleView = card.findViewById(R.id.text1);
            bookAbstractView = card.findViewById(R.id.text2);
            bookImageView = card.findViewById(R.id.image);
        }
    }

    public static String getImageUrlForTask(long taskId)
    {
        return "http://covers.openlibrary.org/b/isbn/";//This will enable the program to get a image (of a book) from a internet database
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i)
    {
        final Context context = viewHolder.bookTitleView.getContext();//Getting references for all items of the activity
        final long id = getItemId(i);//Get the item ID

        /** This code is also for the card - sets the text for the card (continues from the definitions above)*/
        cursor.moveToPosition(i);//Move to the position gained from getItemId (The ID)
        viewHolder.bookTitleView.setText(cursor.getString(bookTitleColumnIndex));//Set the text to the title of the book
        viewHolder.bookAbstractView.setText(cursor.getString(bookAbstractColumnIndex));//Set the text to the abstract of the book

        //Create string components
        /** The below will allow for the application to concatenate the URL and isbn together to get an image for the ISBN**/
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
                ((OnEditBook) context).editBook(id);
            }
        });
        //Set the long press action
        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                /** The below function will check if the user is an admin or not**/
                boolean admin = LoginAuth.adminCheck();
                if (admin)/** once checks have been performed and the user is known to have admin rights delete will be available*/
                {//The below will build a dialog box to check that user does want to delete the current selected book
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
        cursor.moveToPosition(position);//Move the cursor to the correct position (ID)
        return cursor.getLong(bookIdColumnIndex);
    }
    //The function below enables deleting (a book from the database)
    void deleteTask(Context context, long id)
    {
        context.getContentResolver()
                .delete(
                        ContentUris.withAppendedId(
                                BookProvider.CONTENT_URI, id),//Delete the record of the current ID
                                null,null);
    }
}
