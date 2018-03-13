package com.lymenglong.laptop.audiobookapp1verion2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lymenglong.laptop.audiobookapp1verion2.adapter.MainAdapter;
import com.lymenglong.laptop.audiobookapp1verion2.databases.DBHelper;
import com.lymenglong.laptop.audiobookapp1verion2.databases.DatabaseHelper;
import com.lymenglong.laptop.audiobookapp1verion2.http.HttpServicesClass;
import com.lymenglong.laptop.audiobookapp1verion2.model.Home;

import org.apache.http.client.methods.HttpOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private RecyclerView homeList;
    private ArrayList<Home> homes;
    private Home homeModel;
    private MainAdapter mainAdapter;
    private DatabaseHelper databaseHelper;
    private static final String URL = "http://20121969.tk/audiobook/books/getAllBooks.php";


    String HttpUrl = "http://20121969.tk/SachNoiBKIC/AllMenuData.php";

    DBHelper dbHelper;

    ArrayList<Home> menuList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getDataFromIntent();
        initView();
        initDatabase();
        initObject();
        //get data from json parsing
        new GetHttpResponse(this).execute();
    }

    // to make application remember pass LoginActivity in to MainActivity
    private void getDataFromIntent() {
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            Intent intent = new Intent(this, LoginActivity2.class);
            startActivity(intent);
        }
    }

    private void initView() {
        homeList = (RecyclerView) findViewById(R.id.listView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        databaseHelper = new DatabaseHelper(this);
    }


    private void initDatabase() {
        String DB_NAME = "menu.sqlite";
        int DB_VERSION = 1;
//        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS menu(Id INTEGER PRIMARY KEY AUTOINCREMENT, MenuName VARCHAR(255));";
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS menu(Id INTEGER PRIMARY KEY, Name VARCHAR(255));";
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
        //create database
        dbHelper.QueryData(CREATE_TABLE);

    }

    private void GetCursorData() {
        menuList.clear();
        Cursor cursor = dbHelper.GetData("SELECT * FROM menu");
        while (cursor.moveToNext()){
            String name = cursor.getString(1);
            int id = cursor.getInt(0);
            menuList.add(new Home(id,name));
        }
        cursor.close();
        mainAdapter.notifyDataSetChanged();

    }

    private void initObject() {
//        homes = databaseHelper.getHomeList();
        menuList = new ArrayList<>();
        mainAdapter = new MainAdapter(MainActivity.this, menuList);
//        mainAdapter = new MainAdapter(MainActivity.this, homes);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        homeList.setLayoutManager(mLinearLayoutManager);
        homeList.setAdapter(mainAdapter);
    }

    //region JSON parse class started from here.
    private class GetHttpResponse extends AsyncTask<Void, Void, Void>
    {
        public Context context;

        String JSonResult;

//        List<Student> studentList;
        ArrayList<Home> home;

        public GetHttpResponse(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            // Passing HTTP URL to HttpServicesClass Class.
            HttpServicesClass httpServicesClass = new HttpServicesClass(HttpUrl);
            try
            {
                httpServicesClass.ExecutePostRequest();

                if(httpServicesClass.getResponseCode() == 200)
                {
                    JSonResult = httpServicesClass.getResponse();

                    if(JSonResult != null)
                    {
                        JSONArray jsonArray = null;

                        try {
                            jsonArray = new JSONArray(JSonResult);

                            JSONObject jsonObject;

                            Home homeModel;
//                            Student student;

//                            studentList = new ArrayList<Student>();
                            home = new ArrayList<Home>();

                            for(int i=0; i<jsonArray.length(); i++)
                            {
//                                student = new Student();
                                homeModel = new Home();

                                jsonObject = jsonArray.getJSONObject(i);

                                // Adding Student Id TO IdList Array.
//                                IdList.add(jsonObject.getString("Id").toString());
                                homeModel.setId(Integer.parseInt(jsonObject.getString("Id")));

                                //Adding Student Name.
//                                student.StudentName = jsonObject.getString("Name").toString();
                                homeModel.setTitle(jsonObject.getString("Name").toString());
                                home.add(homeModel);
                                int Id = homeModel.getId();
                                String Name = homeModel.getTitle();
                                if (menuList.size()>=home.size()) {
                                    if (!homeModel.getTitle().equals(menuList.get(i).getTitle())) {
                                        String UPDATE_DATA = "UPDATE menu SET Name = '"+Name+"' WHERE Id = '"+Id+"'";
                                        dbHelper.QueryData(UPDATE_DATA);
                                    }
                                } else {
                                    String INSERT_DATA = "INSERT INTO menu VALUES('"+Id+"','"+Name+"')";
                                    dbHelper.QueryData(INSERT_DATA);
                                }
                            }
                        }
                        catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                else
                {
                    Toast.makeText(context, httpServicesClass.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            progressBar.setVisibility(View.GONE);
            GetCursorData();
            Log.d("MyTagView", "onPostExecute");
        }
    }
    //endregion





    //region GetJSON start from here
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
    //endregion

}
