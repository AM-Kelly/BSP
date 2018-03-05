package com.example.amkelly.bsp.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.amkelly.bsp.provider.BookProvider;
import com.example.amkelly.bsp.activity.BookEditActivity;
import com.example.amkelly.bsp.R;
import com.example.amkelly.bsp.adapter.BookListAdapter;
import com.example.amkelly.bsp.interfaces.OnEditFinished;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by Adam on 04/03/2018.
 */

public class BookEditFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    static final String BOOK_ID = "bookId";
    public static final String DEFAULT_FRAGMENT_TAG = "bookEditFragment";
    private static final int MENU_SAVE = 1;

    //Views
    View rootView;
    EditText bookTitleText;
    EditText bookAuthorText;
    EditText bookIsbnText;
    EditText bookAbstractText;
    EditText bookPriceText;
    ImageView bookImageView;

    long bookId;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null)//This was just arguments - so might need changing back
        {
            bookId = arguments.getLong(BookEditActivity.EXTRA_BOOKID, 0L);//Same here with arguments
        }
        if (savedInstanceState != null)
        {
            bookId = savedInstanceState.getLong(BOOK_ID);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        //This field may have changed whilst the activity was running so it is saved to the outState bundle so it can be restored later
        outState.putLong(BOOK_ID, bookId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_book_edit, container, false);

        //Declaration has to go first (before picasso)
        rootView = v.getRootView();
        //The redundant (DATA) could be removed completely?
        bookTitleText = (EditText) v.findViewById(R.id.book_title);
        bookAuthorText = (EditText) v.findViewById(R.id.book_author);
        bookIsbnText = (EditText) v.findViewById(R.id.book_isbn);
        bookAbstractText = (EditText) v.findViewById(R.id.book_abstract);
        bookPriceText = (EditText) v.findViewById(R.id.book_price);
        bookImageView = (ImageView) v.findViewById(R.id.book_image);

        if (bookId == 0)
        {

        }else
        {
            getLoaderManager().initLoader(0, null, this);
        }
        return v;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        Uri taskUri = ContentUris.withAppendedId(BookProvider.CONTENT_URI, bookId);

        return new CursorLoader(getActivity(), taskUri, null, null, null, null);
    }
    //The below method is called when the loader has finished loading it's data
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor task)
    {
        if (task.getCount() == 0)
        {
            getActivity().runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            ((OnEditFinished) getActivity()).finishEditingTask();
                        }
                    }
            );
            return;
        }
        bookTitleText.setText(task.getString(task.getColumnIndexOrThrow(BookProvider.COLUMN_BOOKTITLE)));
        bookAuthorText.setText(task.getString(task.getColumnIndexOrThrow(BookProvider.COLUMN_BOOKAUTHOR)));
        bookIsbnText.setText(task.getString(task.getColumnIndexOrThrow(BookProvider.COLUMN_BOOKISBN)));
        bookAbstractText.setText(task.getString(task.getColumnIndexOrThrow(BookProvider.COLUMN_BOOKABSTRACT)));
        bookPriceText.setText(task.getString(task.getColumnIndexOrThrow(BookProvider.COLUMN_BOOKPRICE)));

        //Set the image
        Picasso.with(getActivity())
         .load(BookListAdapter.getImageUrlForTask(bookId))
         .into(
                 bookImageView, new Callback() {
        @Override
        public void onSuccess() {
        Activity activity = getActivity();

        if (activity == null)
        return;

        //Set the colours of the activity based on the colours of the image
        Bitmap bitmap = ((BitmapDrawable) bookImageView
        .getDrawable())
        .getBitmap();
        Palette palette = Palette.generate(bitmap, 32);
        int bgColor = palette.getLightMutedColor(0);

        if (bgColor != 0)
        {
        rootView.setBackgroundColor(bgColor);
        }
        }

        @Override
        public void onError() {
        //do nothing as the defaults will be used

        }
        }
         );
         //.into(bookImageView);

    }
    @Override
    public void onLoaderReset(Loader<Cursor> arg0)
    {
        //Nothing to do here :)
    }
    public static BookEditFragment newInstance(long id)
    {
        BookEditFragment fragment = new BookEditFragment();
        Bundle args = new Bundle();
        args.putLong(BookEditActivity.EXTRA_BOOKID, id);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        menu.add(0, MENU_SAVE, 0, R.string.confirm).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            //save button pressed
            case MENU_SAVE :
                //Implement a toast to tell the user that the data has been saved
                save();
                /**FragmentTransaction ft = getFragmentManager().beginTransaction();
                DialogFragment newFragment = new AlertDialogFragment();
                newFragment.show(ft, "alertDialog");**/
                ((OnEditFinished) getActivity()).finishEditingTask();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void save()
    {
        /**
         *This will be where changes can be made for more fields
         **/
        //put all values the user enter into a ContentValue object
        //Getting the values
        String bookTitle = bookTitleText.getText().toString();
        String bookAuthor = bookAuthorText.getText().toString();
        String bookIsbn = bookIsbnText.getText().toString();
        String bookAbstract = bookAbstractText.getText().toString();
        String bookPrice = bookPriceText.getText().toString();
        ContentValues values = new ContentValues();
        //Setting the values
        values.put(BookProvider.COLUMN_BOOKTITLE, bookTitle);
        values.put(BookProvider.COLUMN_BOOKAUTHOR, bookAuthor);
        values.put(BookProvider.COLUMN_BOOKISBN, bookIsbn);
        values.put(BookProvider.COLUMN_BOOKABSTRACT, bookAbstract);
        values.put(BookProvider.COLUMN_BOOKPRICE, bookPrice);

        //The task ID will be 0 when we create a new task else it will be the ID of the task being edited
        if (bookId == 0)
        {
            //Create a new task and set the bookId to the id of the new task
            Uri itemUri = getActivity().getContentResolver().insert(BookProvider.CONTENT_URI, values);
            bookId = ContentUris.parseId(itemUri);
        }else
        {
            //Update the existing task
            Uri uri = ContentUris.withAppendedId(BookProvider.CONTENT_URI, bookId);
            int count = getActivity().getContentResolver().update(uri, values, null, null);

            //If user doesn't edit exactly one entry throw an error
            if (count != 1)
                throw new IllegalStateException("Unable to update " + bookId);
        }
        Toast.makeText(getActivity(), getString(R.string.book_saved_message), Toast.LENGTH_SHORT).show();
    }
}
