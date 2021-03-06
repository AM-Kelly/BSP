package com.example.amkelly.bsp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toolbar;

import com.example.amkelly.bsp.R;
import com.example.amkelly.bsp.interfaces.OnEditBook;

public class BookListActivity extends Activity implements OnEditBook {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        setActionBar((Toolbar) findViewById(R.id.toolbar));
    }

    @Override
    public void editBook(long id) {
        //When a task is opened take the ID to know which task to open and edit
        startActivity(new Intent(this, BookEditActivity.class).putExtra(BookEditActivity.EXTRA_BOOKID, id));
    }
}
