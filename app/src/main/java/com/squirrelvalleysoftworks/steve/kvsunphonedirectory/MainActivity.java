package com.squirrelvalleysoftworks.steve.kvsunphonedirectory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {
    SearchHandler searchHandler = null;//This should be instant. before onCreate
    int mainViewHeight = 0;
    int mainViewWidth = 0;
    final static long BACKGROUND_TIMEOUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchHandler = new SearchHandler(this);
        searchHandler.sanityCheck();

        //Initialize layout components
        //Why must these be final?
        //  inner classes?
        final SearchView queryView = (SearchView) findViewById(R.id.queryView);
        final RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.layout);
        final Spinner categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        final ListView resultsView = (ListView) findViewById(R.id.resultsView);
        final Context MainActivityContext = this; //A cheat to get the context to inner class


        //Spinner set up
        //spinner items should be factored out as resources
        String[] items = new String[]{"Search By Name", "Search By Category", "Search By Number"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);







        //Used so that queryView collapses when pressing the main layout
        mainLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //which of these are necessary?
                queryView.clearFocus();
                mainLayout.requestFocus();
                System.out.println("Main listener triggered");
            }
        });

        //Set up queryView
        queryView.setIconifiedByDefault(true);
        queryView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Query listener triggered");
                //which of these are necessary?
                mainLayout.clearFocus();
                queryView.setIconified(false);
                queryView.requestFocus();
            }
        });

        queryView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                displayResults(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                displayResults(newText);
                return false;
            }

            private void displayResults(String query) {
                System.out.println("Query submitted: " + query);
                String spinnerText = categorySpinner.getSelectedItem().toString();
                Cursor resultsCursor = null;
                try {
                    if (query.charAt(query.length() - 1) == ' ')//wanting to scrape off trailing space
                        query.substring(0, query.length() - 2);
                } catch (Exception e) {
                    System.err.println("index out of bounds, ignoring");
                }

                switch (spinnerText) {
                    case "Search By Name":
                        resultsCursor = searchHandler.searchByName(query);
                        break;
                    case "Search By Category":
                        resultsCursor = searchHandler.searchByCategory(query);
                        break;
                    case "Search By Number":
                        resultsCursor = searchHandler.searchByNumber(query);
                        break;
                    default:
                        System.err.println("Category was not matched, crashing");
                        throw new RuntimeException("Crashed within spinner category selection");
                }


                //resultsView setup
                CustomCursorAdapter adapter = new CustomCursorAdapter(MainActivityContext, resultsCursor);
                resultsView.setAdapter(adapter);
//                queryView.clearFocus();
//                mainLayout.requestFocus();
            }
        });

        //Going to use this to keep the mainViews height and width updated with the max
        View mainView = findViewById(R.id.layout);
        mainView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (left == 0 && top == 0 && right == 0 && bottom == 0) {
                    return;
                } else {
                    int width = right;
                    int height = bottom;

                    if (width > mainViewWidth)
                        mainViewWidth = width;
                    if (height > mainViewHeight)
                        mainViewHeight = height;

                    System.out.println("Main layout has changed");
                    System.out.println(left);
                    System.out.println(top);
                    System.out.println(right);
                    System.out.println(bottom);

                }
            }
        });

        //Setup background changing timer
        Timer backgroundTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //change background
                //Need to do a fade transition
            }
        };
        backgroundTimer.schedule(task, 1000 * 30, 1000 * 30);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clearSearchFocus() {
        final SearchView queryView = (SearchView) findViewById(R.id.queryView);
        queryView.clearFocus();
    }

    public void setDim(boolean dim) {
        if(dim)
            findViewById(R.id.dimmer).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.dimmer).setVisibility(View.GONE);
    }

    private void fadeOutBackground() {

    }

    private void fadeInBackground() {
        
    }
}