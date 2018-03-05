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
    static final String TASK_ID = "taskId";
    public static final String DEFAULT_FRAGMENT_TAG = "taskEditFragment";
    private static final int MENU_SAVE = 1;

    //Views
    View rootView;
    EditText titleText;
    EditText notesText;
    ImageView imageView;

    long taskId;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null)//This was just arguments - so might need changing back
        {
            taskId = arguments.getLong(BookEditActivity.EXTRA_TASKID, 0L);//Same here with arguments
        }
        if (savedInstanceState != null)
        {
            taskId = savedInstanceState.getLong(TASK_ID);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        //This field may have changed whilst the activity was running so it is saved to the outState bundle so it can be restored later
        outState.putLong(TASK_ID, taskId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_task_edit, container, false);

        //Declaration has to go first (before picasso)
        rootView = v.getRootView();
        //The redundant (DATA) could be removed completely?
        titleText = (EditText) v.findViewById(R.id.title);
        notesText = (EditText) v.findViewById(R.id.notes);
        imageView = (ImageView) v.findViewById(R.id.image);

        if (taskId == 0)
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
        Uri taskUri = ContentUris.withAppendedId(BookProvider.CONTENT_URI, taskId);

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
        titleText.setText(task.getString(task.getColumnIndexOrThrow(BookProvider.COLUMN_TITLE)));
        notesText.setText(task.getString(task.getColumnIndexOrThrow(BookProvider.COLUMN_NOTES)));

        //Set the image
        Picasso.with(getActivity())
         .load(BookListAdapter.getImageUrlForTask(taskId))
         .into(
         imageView, new Callback() {
        @Override
        public void onSuccess() {
        Activity activity = getActivity();

        if (activity == null)
        return;

        //Set the colours of the activity based on the colours of the image
        Bitmap bitmap = ((BitmapDrawable) imageView
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
         //.into(imageView);

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
        args.putLong(BookEditActivity.EXTRA_TASKID, id);
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
         *This will be where changed can be made for more fields
         **/
        //put all values the user enter into a ContentValue object
        //Getting the values
        String title = titleText.getText().toString();
        String notes = notesText.getText().toString();
        ContentValues values = new ContentValues();
        //Setting the values
        values.put(BookProvider.COLUMN_TITLE, title);
        values.put(BookProvider.COLUMN_NOTES, notes);

        //The task ID will be 0 when we create a new task else it will be the ID of the task being edited
        if (taskId == 0)
        {
            //Create a new task and set the taskId to the id of the new task
            Uri itemUri = getActivity().getContentResolver().insert(BookProvider.CONTENT_URI, values);
            taskId = ContentUris.parseId(itemUri);
        }else
        {
            //Update the existing task
            Uri uri = ContentUris.withAppendedId(BookProvider.CONTENT_URI, taskId);
            int count = getActivity().getContentResolver().update(uri, values, null, null);

            //If user doesn't edit exactly one entry throw an error
            if (count != 1)
                throw new IllegalStateException("Unable to update " + taskId);
        }
        Toast.makeText(getActivity(), getString(R.string.task_saved_message), Toast.LENGTH_SHORT).show();
    }
}
