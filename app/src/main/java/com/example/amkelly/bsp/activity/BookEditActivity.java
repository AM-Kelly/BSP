package com.example.amkelly.bsp.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Toolbar;

import com.example.amkelly.bsp.fragment.BookEditFragment;
import com.example.amkelly.bsp.R;
import com.example.amkelly.bsp.interfaces.OnEditFinished;

public class BookEditActivity extends Activity implements OnEditFinished
{
    public static final String EXTRA_TASKID = "taskId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        setActionBar((Toolbar) findViewById(R.id.toolbar));

        long id = getIntent().getLongExtra(BookEditActivity.EXTRA_TASKID,0L);

        Fragment fragment = BookEditFragment.newInstance(id);

        String fragmentTag = BookEditFragment.DEFAULT_FRAGMENT_TAG;

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
