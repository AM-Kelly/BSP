package com.example.amkelly.tasks.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import com.example.amkelly.tasks.activity.TaskEditActivity;
import com.example.amkelly.tasks.R;
import com.example.amkelly.tasks.adapter.TaskListAdapter;
import com.example.amkelly.tasks.interfaces.OnEditFinished;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by Adam on 04/03/2018.
 */

public class TaskEditFragment extends Fragment
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
            taskId = arguments.getLong(TaskEditActivity.EXTRA_TASKID, 0L);//Same here with arguments
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

        //Set the thumbnail image
        Picasso.with(getActivity())
                .load(TaskListAdapter.getImageUrlForTask(taskId))
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

        return v;
    }
    public static TaskEditFragment newInstance(long id)
    {
        TaskEditFragment fragment = new TaskEditFragment();
        Bundle args = new Bundle();
        args.putLong(TaskEditActivity.EXTRA_TASKID, id);
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
                //save();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                DialogFragment newFragment = new AlertDialogFragment();
                newFragment.show(ft, "alertDialog");
                //((OnEditFinished) getActivity()).finishEditingTask();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
