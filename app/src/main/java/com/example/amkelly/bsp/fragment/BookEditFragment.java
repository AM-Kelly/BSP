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

import com.example.amkelly.bsp.login.LoginAuth;
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
    boolean admin = LoginAuth.adminCheck();
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
        if (arguments != null)
        {
            bookId = arguments.getLong(BookEditActivity.EXTRA_BOOKID, 0L);
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
        //Setting the view variables
        bookTitleText = v.findViewById(R.id.book_title);
        bookAuthorText = v.findViewById(R.id.book_author);
        bookIsbnText = v.findViewById(R.id.book_isbn);
        bookAbstractText = v.findViewById(R.id.book_abstract);
        bookPriceText = v.findViewById(R.id.book_price);
        bookImageView = v.findViewById(R.id.book_image);

        //If admin privileges are not attained the edit fields will be disabled
        if (!admin)
        {
            /** Disable the edit fields within the edit fragment for non-admins*/
            bookTitleText.setEnabled(false);
            bookAuthorText.setEnabled(false);
            bookIsbnText.setEnabled(false);
            bookAbstractText.setEnabled(false);
            bookPriceText.setEnabled(false);

        }else
        {
            //Nothing to see here
        }

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
        //set the book uri
        Uri bookUri = ContentUris.withAppendedId(BookProvider.CONTENT_URI, bookId);

        return new CursorLoader(getActivity(), bookUri, null, null, null, null);
    }
    //The below method is called when the loader has finished loading it's data
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor book)
    {
        if (book.getCount() == 0)
        {
            getActivity().runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            ((OnEditFinished) getActivity()).finishEditingBook();
                        }
                    }
            );
            return;
        }
        //the below will set the text for each field within the edit book activity (including the image)
        bookTitleText.setText(book.getString(book.getColumnIndexOrThrow(BookProvider.COLUMN_BOOKTITLE)));
        bookAuthorText.setText(book.getString(book.getColumnIndexOrThrow(BookProvider.COLUMN_BOOKAUTHOR)));
        bookIsbnText.setText(book.getString(book.getColumnIndexOrThrow(BookProvider.COLUMN_BOOKISBN)));
        bookAbstractText.setText(book.getString(book.getColumnIndexOrThrow(BookProvider.COLUMN_BOOKABSTRACT)));
        bookPriceText.setText(book.getString(book.getColumnIndexOrThrow(BookProvider.COLUMN_BOOKPRICE)));
        //The code below should generate an image for each ISBN
        String baseurl = BookListAdapter.getImageUrlForTask(bookId);
        String ISBN = book.getString(book.getColumnIndexOrThrow(BookProvider.COLUMN_BOOKISBN));
        String endurlsize = "-L.jpg";
        //Set the image
        Picasso.with(getActivity())
            .load(baseurl + ISBN + endurlsize)
            .into(
                 bookImageView, new Callback() {
        @Override
        public void onSuccess() {
        Activity activity = getActivity();

        if (activity == null)
        return;

        //Set the colours of the activity based on an assesment of the colours within the image
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

    }
    @Override
    public void onLoaderReset(Loader<Cursor> arg0)
    {
        //Nothing to do here :)
    }
    public static BookEditFragment newInstance(long id)
    {
        //Create a new instance of the book edit fragment
        BookEditFragment fragment = new BookEditFragment();
        Bundle args = new Bundle();
        args.putLong(BookEditActivity.EXTRA_BOOKID, id);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Create the options menu once the activity is created
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        //Check if the user is admin, if not don't show menu
        if (!admin)
        {
            //Nothing to see here
        }
        else//if the user is admin then show the menu
        {
            menu.add(0, MENU_SAVE, 0, R.string.confirm).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            //save button pressed --> start the save function
            case MENU_SAVE :
                save();
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


        //Error checking
        if (bookTitle.isEmpty())
        {
            Toast.makeText(getActivity(), "Please enter the books title", Toast.LENGTH_SHORT).show();
        }
        else if (bookAuthor.isEmpty())
        {
            Toast.makeText(getActivity(), "Please enter the books author", Toast.LENGTH_SHORT).show();
        }
        else if (bookIsbn.isEmpty() || (bookIsbn.length() != 10 && bookIsbn.length() != 13) )
        {
            Toast.makeText(getActivity(), "Please enter the books ISBN ensuring that it is the correct length", Toast.LENGTH_SHORT).show();
        }
        else if (bookAbstract.isEmpty())
        {
            Toast.makeText(getActivity(), "Please enter the books abstract", Toast.LENGTH_SHORT).show();
        }
        else if (bookPrice.isEmpty())
        {
            Toast.makeText(getActivity(), "Please enter the books price", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //The task ID will be 0 when we create a new task else it will be the ID of the task being edited
            if (bookId == 0)
            {
                //Create a new task and set the bookId to the id of the new task
                Uri itemUri = getActivity().getContentResolver().insert(BookProvider.CONTENT_URI, values);
                bookId = ContentUris.parseId(itemUri);
                ((OnEditFinished) getActivity()).finishEditingBook();
            }else
            {
                //Update the existing task
                Uri uri = ContentUris.withAppendedId(BookProvider.CONTENT_URI, bookId);
                int count = getActivity().getContentResolver().update(uri, values, null, null);
                ((OnEditFinished) getActivity()).finishEditingBook();
                //If user doesn't edit exactly one entry throw an error
                if (count != 1)
                    throw new IllegalStateException("Unable to update " + bookId);
            }
            Toast.makeText(getActivity(), getString(R.string.book_saved_message), Toast.LENGTH_SHORT).show();
        }

    }
}
