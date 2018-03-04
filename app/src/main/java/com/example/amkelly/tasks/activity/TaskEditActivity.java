package com.example.amkelly.tasks.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Toolbar;

import com.example.amkelly.tasks.R;
import com.example.amkelly.tasks.fragment.TaskEditFragment;
import com.example.amkelly.tasks.interfaces.OnEditFinished;
import com.example.amkelly.tasks.interfaces.OnEditTask;

public class TaskEditActivity extends Activity implements OnEditFinished
{
    public static final String EXTRA_TASKID = "taskId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        setActionBar((Toolbar) findViewById(R.id.toolbar));

        long id = getIntent().getLongExtra(TaskEditActivity.EXTRA_TASKID,0L);

        Fragment fragment = TaskEditFragment.newInstance(id);

        String fragmentTag = TaskEditFragment.DEFAULT_FRAGMENT_TAG;

        if (savedInstanceState == null)
            getFragmentManager().beginTransaction().add(
                    R.id.container,
                    fragment,
                    fragmentTag).commit();
    }

    @Override
    public void finishEditingTask()
    {
        finish();
    }

}
