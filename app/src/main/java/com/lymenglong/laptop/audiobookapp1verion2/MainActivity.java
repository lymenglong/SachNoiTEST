package com.lymenglong.laptop.audiobookapp1verion2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lymenglong.laptop.audiobookapp1verion2.adapter.MainAdapter;
import com.lymenglong.laptop.audiobookapp1verion2.databases.DatabaseHelper;
import com.lymenglong.laptop.audiobookapp1verion2.model.Home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private RecyclerView homeList;
    private ArrayList<Home> homes;
    private Home homeModel;
    private MainAdapter mainAdapter;
    private DatabaseHelper databaseHelper;
    private static final String URL = "http://20121969.tk/audiobook/books/getAllBooks.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ViewCompat.setImportantForAccessibility(getWindow().getDecorView(),ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
//        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.activity_main),ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
        /*View forename = findViewById(R.id.activity_main);
        forename.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            public boolean performAccessibilityAction (View host, int action, Bundle args){
                return true;
            }
        });*/
        getDataFromIntent();
        initView();
        initObject();
//        getJSON(URL);
    }

    private void initObject() {
        homes = databaseHelper.getHomeList();
        mainAdapter = new MainAdapter(MainActivity.this, homes);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        homeList.setLayoutManager(mLinearLayoutManager);
        homeList.setAdapter(mainAdapter);
    }

    private void getDataFromIntent() {
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            Intent intent = new Intent(this, LoginActivity2.class);
            startActivity(intent);
        }
    }

    private void initView() {
        homeList = (RecyclerView) findViewById(R.id.listView);
        databaseHelper = new DatabaseHelper(this);
    }









    private void getJSON(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    getHomeList(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }


    public void getHomeList(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        ArrayList<Home> homes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            homeModel = new Home(obj.getInt("id"), obj.getString("name"));
            homes.add(homeModel);
        }
        mainAdapter = new MainAdapter(MainActivity.this, homes);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        homeList.setLayoutManager(mLinearLayoutManager);
        homeList.setAdapter(mainAdapter);
    }
}
