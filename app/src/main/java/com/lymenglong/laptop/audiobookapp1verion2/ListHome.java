package com.lymenglong.laptop.audiobookapp1verion2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lymenglong.laptop.audiobookapp1verion2.adapter.HomeAdapter;
import com.lymenglong.laptop.audiobookapp1verion2.adapter.FavoriteAdapter;
import com.lymenglong.laptop.audiobookapp1verion2.adapter.HistoryAdapter;
import com.lymenglong.laptop.audiobookapp1verion2.customize.CustomActionBar;
import com.lymenglong.laptop.audiobookapp1verion2.databases.DBHelper;
import com.lymenglong.laptop.audiobookapp1verion2.databases.DatabaseHelper;
import com.lymenglong.laptop.audiobookapp1verion2.http.HttpServicesClass;
import com.lymenglong.laptop.audiobookapp1verion2.model.BookType;
import com.lymenglong.laptop.audiobookapp1verion2.model.Chapter;
import com.lymenglong.laptop.audiobookapp1verion2.model.Home;
import com.lymenglong.laptop.audiobookapp1verion2.model.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ListHome extends AppCompatActivity{
    private RecyclerView listChapter;
    private ArrayList<Chapter> chapters;
    private HomeAdapter adapter;
    private HistoryAdapter historyAdapter;
    private FavoriteAdapter favoriteAdapter;
    private CustomActionBar actionBar;
    private DatabaseHelper databaseHelper;
    private String titleHome;
    private int idHome;
    private TextView tvStory;
    private Chapter chapterModel;
    private Activity activity = ListHome.this;
    private Session session;
    private StringRequest stringRequest;
    private RequestQueue requestQueue;



//    private static final String getHistoryURL = "http://20121969.tk/audiobook/books/getAllBooks.php";
    private static final String getHistoryURL = "http://20121969.tk/audiobook/books/getHistory.php";
    private static final String getFavoriteURL = "http://20121969.tk/audiobook/books/getFavorite.php";
    private ProgressBar progressBar;
    private DBHelper dbHelper;
    private ArrayList <Chapter> list;
    private String HttpUrl = "http://20121969.tk/SachNoiBKIC/AllBookTypeData.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chapter);
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        getDataFromIntent();
        setTitle(titleHome);
        initView();
        initDatabase();
        initObject();
    }


    private void initDatabase() {
        String DB_NAME = "menu.sqlite";
        int DB_VERSION = 1;
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS booktype(Id INTEGER PRIMARY KEY, Name VARCHAR(255));";
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
        //create database
        dbHelper.QueryData(CREATE_TABLE);

    }

    private void GetCursorData() {
        Cursor cursor = dbHelper.GetData("SELECT * FROM booktype");
        while (cursor.moveToNext()){
            String name = cursor.getString(1);
            int id = cursor.getInt(0);
            list.add(new Chapter(id,name));
        }
        cursor.close();
        adapter.notifyDataSetChanged();

    }

    private void initObject() {
        if (idHome == 1) {
            list = new ArrayList<>();
            adapter = new HomeAdapter(ListHome.this, list);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
            listChapter.setLayoutManager(mLinearLayoutManager);
            listChapter.setAdapter(adapter);
            //get data from json parsing
            new GetHttpResponse(this).execute();
        }
        if(idHome == 2){ //lich su
            stringRequest = new StringRequest(Request.Method.POST, getHistoryURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            getJSONHistory(getHistoryURL);

                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(activity, "Not Response", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(stringRequest);
        }
        if(idHome== 3) { // yeu thich
            stringRequest = new StringRequest(Request.Method.POST, getFavoriteURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            getJSONFavorite(getFavoriteURL);
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(activity, "Not Response", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(stringRequest);
        }
        if(idHome == 4){ // tai khoan
            Intent intent  = new Intent(this, UserInfoActivity.class);
            this.finish();
            this.startActivity(intent);
        }
        if(idHome == 5){ // huong dan
            Intent intent  = new Intent(this, HelpActivity.class);
            this.finish();
            this.startActivity(intent);
        }
        if(idHome == 0){ // thoát
            activity.finish();
        }
        else return;
    }

    //region JSON parse class started from here.
    private class GetHttpResponse extends AsyncTask<Void, Void, Void>
    {
        public Context context;

        String JSonResult;

        ArrayList<BookType> bookTypes;

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

                            BookType bookTypeModel;
//                            studentList = new ArrayList<Student>();
                            bookTypes = new ArrayList<BookType>();

                            for(int i=0; i<jsonArray.length(); i++)
                            {
//                                student = new Student();
                                bookTypeModel = new BookType();

                                jsonObject = jsonArray.getJSONObject(i);

                                // Adding Student Id TO IdList Array.
//                                IdList.add(jsonObject.getString("Id").toString());
                                bookTypeModel.setId(Integer.parseInt(jsonObject.getString("Id")));

                                //Adding Student Name.
//                                student.StudentName = jsonObject.getString("Name").toString();
                                bookTypeModel.setTitle(jsonObject.getString("Name").toString());
                                bookTypes.add(bookTypeModel);
                                int Id = bookTypeModel.getId();
                                String Name = bookTypeModel.getTitle();
                                if (list.size()>= bookTypes.size()) {
                                    if (!bookTypeModel.getTitle().equals(list.get(i).getTitle())) {
                                        String UPDATE_DATA = "UPDATE booktype SET Name = '"+Name+"' WHERE Id = '"+Id+"'";
                                        dbHelper.QueryData(UPDATE_DATA);
                                    }
                                } else {
                                    String INSERT_DATA = "INSERT INTO booktype VALUES('"+Id+"','"+Name+"')";
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
            Log.d("MyTagView", "onPostExecute: "+titleHome);
        }
    }
    //endregion


    //region initObject Before changed
   /* private void initObject() {

        if(idHome == 1){ //the loai sach
            databaseHelper = new DatabaseHelper(this);
            chapters = databaseHelper.getListChapter(idHome);
            adapter = new HomeAdapter(ListHome.this, chapters);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
            listChapter.setLayoutManager(mLinearLayoutManager);
            listChapter.setAdapter(adapter);
        }

        if(idHome == 2){ //lich su
            stringRequest = new StringRequest(Request.Method.POST, getHistoryURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            getJSONHistory(getHistoryURL);

                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(activity, "Not Response", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(stringRequest);
        }
        if(idHome== 3) { // yeu thich
            stringRequest = new StringRequest(Request.Method.POST, getFavoriteURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            getJSONFavorite(getFavoriteURL);
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(activity, "Not Response", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(stringRequest);
        }
        if(idHome == 4){ // tai khoan
            Intent intent  = new Intent(this, UserInfoActivity.class);
            this.finish();
            this.startActivity(intent);
        }
        if(idHome == 5){ // huong dan
            Intent intent  = new Intent(this, HelpActivity.class);
            this.finish();
            this.startActivity(intent);
        }
        if(idHome == 0){ // thoát
//            activity.finish();
        }
        else return;


    }*/
    //endregion

    /**
     * Lấy dữ liệu thông qua intent
     */
    private void getDataFromIntent() {
        titleHome = getIntent().getStringExtra("titleHome");
        idHome = getIntent().getIntExtra("idHome", -1);
    }

    /**
     * Khai báo các view và khởi tạo giá trị
     */
    private void initView() {
        session = new Session(activity);
        requestQueue = Volley.newRequestQueue(activity);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, titleHome, false);
        listChapter = (RecyclerView) findViewById(R.id.listView);
    }


    private void getJSONHistory(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    getListHistoryFromJSON(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    java.net.URL url = new URL(urlWebService);
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


    public void getListHistoryFromJSON(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        ArrayList<Chapter> chapters = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            if(obj.getString("IdUser").equals(session.getUserIdLoggedIn())) {
            chapterModel = new Chapter(obj.getInt("IdBook"),
                    obj.getString("bookName"),
                    obj.getString("bookTextContent"),
                    obj.getString("bookFileUrl"),
                    obj.getInt("InsertTime"));
            chapters.add(chapterModel);
            }
        }
        historyAdapter = new HistoryAdapter(activity, chapters);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        listChapter.setAdapter(historyAdapter);
    }


    private void getJSONFavorite(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    getListFavoriteFromJSON(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    java.net.URL url = new URL(urlWebService);
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


    public void getListFavoriteFromJSON(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        ArrayList<Chapter> chapters = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            if(obj.getString("IdUser").equals(session.getUserIdLoggedIn())) {
                chapterModel = new Chapter(obj.getInt("IdBook"), obj.getString("bookName"),obj.getString("bookTextContent"),obj.getString("bookFileUrl"));
                chapters.add(chapterModel);
            }
        }
        favoriteAdapter = new FavoriteAdapter(activity, chapters);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        listChapter.setAdapter(favoriteAdapter);
    }





}
