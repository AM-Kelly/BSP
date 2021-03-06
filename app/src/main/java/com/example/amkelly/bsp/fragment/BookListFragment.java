package com.example.amkelly.bsp.fragment;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.amkelly.bsp.R;
import com.example.amkelly.bsp.adapter.BookListAdapter;
import com.example.amkelly.bsp.interfaces.OnEditBook;
import com.example.amkelly.bsp.login.LoginAuth;
import com.example.amkelly.bsp.provider.BookProvider;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    RecyclerView recyclerView;
    BookListAdapter adapter;
    boolean admin = LoginAuth.adminCheck();

    public BookListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter = new BookListAdapter();

        getLoaderManager().initLoader(0, null, this);
    }

    //Creating a new recycler view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_book_list, container, false);
        recyclerView = v.findViewById(R.id.recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Inflate the layout for this fragment
        return v;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    //Menu inflater function will only work if the user is admin
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        if (admin)
        {
            inflater.inflate(R.menu.menu_list, menu);
        }else
        {
            //Nothing to see here
        }
    }
    //Activity creation for edit book
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_insert:
                ((OnEditBook) getActivity()).editBook(0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //Cursor loader to get content URI
    @Override
    public Loader<Cursor> onCreateLoader(int ignored, Bundle args)
    {
        return new CursorLoader(getActivity(), BookProvider.CONTENT_URI, null, null, null, null);
    }
    //Once the Nth column has been loaded swap the cursor to the next column
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        adapter.swapCursor(cursor);
    }
    //Once a whole record has been loaded reset, with a different ID
    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        adapter.swapCursor(null);
    }
}
