package com.example.amkelly.tasks.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.amkelly.tasks.activity.TaskEditActivity;
import com.example.amkelly.tasks.R;
import com.example.amkelly.tasks.adapter.TaskListAdapter;
import com.squareup.picasso.Picasso;

/**
 * Created by Adam on 04/03/2018.
 */

public class TaskEditFragment extends Fragment
{
    static final String TASK_ID = "taskId";
    public static final String DEFAULT_FRAGMENT_TAG = "taskEditFragment";

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

        //Set the thumbnail image
        Picasso.with(getActivity())
                .load(TaskListAdapter.getImageUrlForTask(taskId))
                .into(imageView);

        rootView = v.getRootView();
        //The redundant (DATA) could be removed completely?
        titleText = (EditText) v.findViewById(R.id.title);
        notesText = (EditText) v.findViewById(R.id.notes);
        imageView = (ImageView) v.findViewById(R.id.image);

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
}
