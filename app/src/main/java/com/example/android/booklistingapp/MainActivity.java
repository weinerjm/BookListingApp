package com.example.android.booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>>, View.OnClickListener
        {

    private static final String LOG_TAG = MainActivity.class.getName();

    private static final String GOOGLE_BOOKS_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";

    private String mUserQuery = "";

    private static final int BOOK_LOADER_ID = 0;

    private BookAdapter mAdapter;

    private TextView mEmptyStateTextView;

    private View mBookResultsLayout;

    private LoaderManager loaderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.search_btn);
        button.setOnClickListener(this);

        EditText userQueryEditText = (EditText) findViewById(R.id.edit_text);
        if (userQueryEditText != null) {
            mUserQuery = userQueryEditText.getText().toString();
        } else {
            Toast.makeText(this, "Please enter a search term", Toast.LENGTH_LONG).show();
        }

    }

    public void onClick(View v) {

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        mBookResultsLayout = inflater.inflate(R.layout.activity_book_results, null);

        ListView bookListView = (ListView) mBookResultsLayout.findViewById(R.id.list);

        mEmptyStateTextView = (TextView) mBookResultsLayout.findViewById(R.id.empty_view);

        bookListView.setEmptyView(mEmptyStateTextView);

        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        bookListView.setAdapter(mAdapter);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            loaderManager = getLoaderManager();
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
            System.out.println("Okay, created loader manager!");
        } else {
            View loadingIndicator = mBookResultsLayout.findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText("No internet connection");
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        return new BookLoader(this, GOOGLE_BOOKS_REQUEST_URL + mUserQuery + "&maxResults=15");
    }

    @Override
    public void onLoadFinished
            (Loader<List<Book>> loader, List<Book> books) {
        View loadingIndicator = mBookResultsLayout.findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mEmptyStateTextView.setText("No results found. Please type your query into the search bar.");

        mAdapter.clear();

        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }
}

