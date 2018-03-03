package com.example.amkelly.tasks.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toolbar;

import com.example.amkelly.tasks.R;

public class TaskListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        setActionBar((Toolbar) findViewById(R.id.toolbar));
    }
}
