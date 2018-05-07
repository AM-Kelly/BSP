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
    public static final String EXTRA_BOOKID = "taskId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_edit);
        setActionBar((Toolbar) findViewById(R.id.toolbar));

        long id = getIntent().getLongExtra(BookEditActivity.EXTRA_BOOKID,0L);//Gets the value of the pressed ID

        Fragment fragment = BookEditFragment.newInstance(id);//Will start a new fragment using the ID that was pressed.

        String fragmentTag = BookEditFragment.DEFAULT_FRAGMENT_TAG;//Sets the fragment tag of the new instance

        if (savedInstanceState == null)//Checks if the instance of a particular fragement (by ID) has been saved, if so load the fragment
            getFragmentManager().beginTransaction().add(
                    R.id.container,
                    fragment,
                    fragmentTag).commit();
    }

    @Override
    public void finishEditingBook()
    {
        finish();//If editing has finished end the editing book fragment
    }

}
