package com.lymenglong.laptop.audiobookapp1verion2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import com.lymenglong.laptop.audiobookapp1verion2.adapter.MenuAdapter;
import com.lymenglong.laptop.audiobookapp1verion2.adapter.FavoriteAdapter;
import com.lymenglong.laptop.audiobookapp1verion2.adapter.HistoryAdapter;
import com.lymenglong.laptop.audiobookapp1verion2.customize.CustomActionBar;
import com.lymenglong.laptop.audiobookapp1verion2.databases.DBHelper;
import com.lymenglong.laptop.audiobookapp1verion2.databases.DatabaseHelper;
import com.lymenglong.laptop.audiobookapp1verion2.http.HttpParse;
import com.lymenglong.laptop.audiobookapp1verion2.http.HttpServicesClass;
import com.lymenglong.laptop.audiobookapp1verion2.model.Book;
import com.lymenglong.laptop.audiobookapp1verion2.model.Chapter;
import com.lymenglong.laptop.audiobookapp1verion2.model.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ListMenu extends AppCompatActivity{
    private RecyclerView listChapter;
    private View imRefresh;
    private ArrayList<Chapter> chapters;
    private MenuAdapter adapter;
    private HistoryAdapter historyAdapter;
    private FavoriteAdapter favoriteAdapter;
    private CustomActionBar actionBar;
    private DatabaseHelper databaseHelper;
    private String titleHome;
    private int idMenu;
    private TextView tvStory;
    private Chapter chapterModel;
    private Activity activity = ListMenu.this;
    private Session session;
    private StringRequest stringRequest;
    private RequestQueue requestQueue;

    //    private static final String getHistoryURL = "http://20121969.tk/audiobook/books/getAllBooks.php";
    private static final String getHistoryURL = "http://20121969.tk/audiobook/books/getHistory.php";
    private static final String getFavoriteURL = "http://20121969.tk/audiobook/books/getFavorite.php";
    private ProgressBar progressBar;
    private DBHelper dbHelper;
    private static ArrayList <Chapter> list = new ArrayList<>();

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
        String CREATE_TABLE_BOOKTYPE = "CREATE TABLE IF NOT EXISTS booktype(Id INTEGER PRIMARY KEY, Name VARCHAR(255));";
//        String CREATE_TABLE_HISTORY = "CREATE TABLE IF NOT EXISTS history(IdUser INTEGER, IdBook INTEGER, InsertTime INTEGER, PauseTime INTEGER);";
        String CREATE_TABLE_HISTORY = "CREATE TABLE IF NOT EXISTS history(" +
                "IdUser INTEGER, " +
                "IdBook INTEGER, " +
                "InsertTime INTEGER, " +
                "PauseTime INTEGER, " +
                "BookName VARCHAR(255), " +
                "FileUrl VARCHAR(255), " +
                "TextContent LONGTEXT);";
        String CREATE_TABLE_BOOK = "CREATE TABLE IF NOT EXISTS book " +
                "(Id INTEGER PRIMARY KEY, " +
                "Name VARCHAR(255), " +
                "CategoryID INTEGER, " +
                "FileUrl VARCHAR(255), " +
                "TextContent LONGTEXT);";
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
        //create database
        dbHelper.QueryData(CREATE_TABLE_BOOKTYPE);
        dbHelper.QueryData(CREATE_TABLE_HISTORY);
        dbHelper.QueryData(CREATE_TABLE_BOOK);

    }

    private void GetCursorData(String tableName) {
        list.clear();
        Cursor cursor;
        switch (tableName){
            case "booktype":
                cursor = dbHelper.GetData("SELECT * FROM '"+tableName+"'");
                while (cursor.moveToNext()){
                    String name = cursor.getString(1);
                    int id = cursor.getInt(0);
                    list.add(new Chapter(id,name));
                }
                cursor.close();
                adapter.notifyDataSetChanged();
                break;
            case "history":
                cursor = dbHelper.GetData("SELECT * FROM history");
                while (cursor.moveToNext()){
                    /*int userId = cursor.getInt(0);
                    int historybookId = cursor.getInt(1);
                    int insertTime = cursor.getInt(2);
                    int pauseTime = cursor.getInt(3);
                    int bookId = cursor.getInt(4);
                    String bookName = cursor.getString(5);
                    int categoryId = cursor.getInt(6);
                    String content = cursor.getString(7);
                    String fileURL = cursor.getString(8);*/

                    int bookId = cursor.getInt(1);
                    String bookName = cursor.getString(4);
                    String content = cursor.getString(6);
                    String fileURL = cursor.getString(5);
                    int pauseTime = cursor.getInt(3);

                    list.add(new Chapter(bookId,bookName,content,fileURL,pauseTime));
                }
                cursor.close();
                historyAdapter.notifyDataSetChanged();
                break;
        }
        dbHelper.close();
    }

    private void initObject() {
        imRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(activity, "Refresh", Toast.LENGTH_SHORT).show();
            switch (idMenu){
                case 1:
                    new GetHttpResponse(activity).execute();
                    break;
                case 2:
                    HttpWebCall(String.valueOf(session.getUserIdLoggedIn()));
                    break;
            }
            }
        });
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        Intent intent;
        switch (idMenu){
            case 1 : //book type
                adapter = new MenuAdapter(ListMenu.this, list);
                listChapter.setAdapter(adapter);
                GetCursorData(TableSwitched(idMenu));
                //get data from json parsing
                if(list.isEmpty()){
                    new GetHttpResponse(this).execute();
                } else {
                    progressBar.setVisibility(View.GONE);
                }
                break;
            case 2: //history
                historyAdapter = new HistoryAdapter(activity, list);
                listChapter.setAdapter(historyAdapter);
                GetCursorData(TableSwitched(idMenu));
                //get data from json parsing
                if(list.isEmpty()) {
                    HttpWebCall(String.valueOf(session.getUserIdLoggedIn()));
                } else {
                    progressBar.setVisibility(View.GONE);
                }
                break;
            case 3: // favorite
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
                break;
            case 4: //account info
                intent = new Intent(this, UserInfoActivity.class);
                this.finish();
                this.startActivity(intent);
                break;
            case 5: //help
                intent  = new Intent(this, HelpActivity.class);
                this.finish();
                this.startActivity(intent);
                break;
            case 6: //exit
                ActivityCompat.finishAffinity(this);
                System.exit(0);
                break;

        }
    }

    private static final String HttpUrl_AllBookTypeData = "http://20121969.tk/SachNoiBKIC/AllBookTypeData.php";
    private static final String HttpUrl_FilterHistoryData = "http://20121969.tk/SachNoiBKIC/FilterHistoryData.php";

    private String HttpUrlSwitched(int id){
        String pathUrl = null;
        switch (id){
            case 1: // list book
                pathUrl = HttpUrl_AllBookTypeData;
                break;
            case 2: // list book
                pathUrl = HttpUrl_FilterHistoryData;
                break;

        }
        return pathUrl;
    }
    private String TableSwitched(int id){
        String tableName = null;
        switch (id){
            case 1: // list book
                tableName = "booktype";
                break;
            case 2: // list history
                tableName = "history";
                break;

        }
        return tableName;
    }


    //region JSON parse class started from here.
    private class GetHttpResponse extends AsyncTask<Void, Void, Void>
    {
        public Context context;

        String JSonResult;

        ArrayList<Chapter> tempArray;

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
            HttpServicesClass httpServicesClass = new HttpServicesClass(
                    HttpUrlSwitched(idMenu));
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

                            Chapter tempModel;
//                            studentList = new ArrayList<Student>();
                            tempArray = new ArrayList<>();

                            for(int i=0; i<jsonArray.length(); i++)
                            {
//                                student = new Student();
                                tempModel = new Chapter();

                                jsonObject = jsonArray.getJSONObject(i);

                                tempModel.setId(Integer.parseInt(jsonObject.getString("Id")));
                                tempModel.setTitle(jsonObject.getString("Name"));
                                tempArray.add(tempModel);

                                int Id = tempModel.getId();
                                String Name = tempModel.getTitle();

                                if (list.size()>= tempArray.size()) {
                                    SetUpdateTableData(i, tempModel, TableSwitched(idMenu));
                                } else {
                                    SetInsertTableData(tempModel,TableSwitched(idMenu));

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
            GetCursorData(TableSwitched(idMenu));
            Log.d("MyTagView", "onPostExecute: "+titleHome);
        }
    }

    private void SetInsertTableData(Chapter arrayModel, String tableName) {
        String INSERT_DATA = null;
        switch (tableName){
            case "booktype":
                INSERT_DATA = "INSERT INTO '"+tableName+"' VALUES('"+arrayModel.getId()+"','"+arrayModel.getTitle()+"')";
                dbHelper.QueryData(INSERT_DATA);
                break;
            case "history":
//                INSERT_DATA = "INSERT INTO '"+tableName+"' VALUES('"+session.getUserIdLoggedIn()+"','"+arrayModel.getId()+"','"+arrayModel.getInsertTime()+"','"+arrayModel.getPauseTime()+"')";
                INSERT_DATA = "INSERT INTO '"+tableName+"' VALUES(" +
                        "'"+session.getUserIdLoggedIn()+"'," +
                        "'"+arrayModel.getId()+"'," +
                        "'"+arrayModel.getInsertTime()+"'," +
                        "'"+arrayModel.getPauseTime()+"'," +
                        "'"+arrayModel.getTitle()+"'," +
                        "'"+arrayModel.getFileUrl()+"'," +
                        "'"+arrayModel.getContent()+"')";
                dbHelper.QueryData(INSERT_DATA);
                /*INSERT_DATA = "INSERT INTO book VALUES (" +
                        "'"+arrayModel.getId()+"'," +
                        "'"+arrayModel.getTitle()+"," +
                        "'"+arrayModel.getCategoryId()+"'," +
                        "'"+arrayModel.getFileUrl()+"'," +
                        "'"+arrayModel.getContent()+"');";*/
//                dbHelper.QueryData(INSERT_DATA);
                break;
        }

    }

    private void SetUpdateTableData(int i, Chapter arrayModel, String tableName) {
        switch (tableName){
            case "booktype":
                if (!list.get(i).getTitle().equals(arrayModel.getTitle())) {
                    String UPDATE_DATA = "UPDATE '"+tableName+"' SET Name = '"+arrayModel.getTitle()+"' WHERE Id = '"+arrayModel.getId()+"';";
                    dbHelper.QueryData(UPDATE_DATA);
                }
                break;
            case "history":
                //todo update history table
                break;
        }

    }
    //endregion


    //region initObject Before changed old code
   /* private void initObject() {

        if(idMenu == 1){ //the loai sach
            databaseHelper = new DatabaseHelper(this);
            chapters = databaseHelper.getListChapter(idMenu);
            adapter = new MenuAdapter(ListMenu.this, chapters);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
            listChapter.setLayoutManager(mLinearLayoutManager);
            listChapter.setAdapter(adapter);
        }

        if(idMenu == 2){ //lich su
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
        if(idMenu== 3) { // yeu thich
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
        if(idMenu == 4){ // tai khoan
            Intent intent  = new Intent(this, UserInfoActivity.class);
            this.finish();
            this.startActivity(intent);
        }
        if(idMenu == 5){ // huong dan
            Intent intent  = new Intent(this, HelpActivity.class);
            this.finish();
            this.startActivity(intent);
        }
        if(idMenu == 0){ // thoát
//            activity.finish();
        }
        else return;


    }*/
    //endregion  ()

    //region Method to show current record Current Selected Record
    private ProgressDialog pDialog;
    private String FinalJSonObject;
    private HashMap<String, String> ResultHash = new HashMap<>();
    private String ParseResult;
    private HttpParse httpParse = new HttpParse();
    public void HttpWebCall(final String PreviousListViewClickedItem){

        class HttpWebCallFunction extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = ProgressDialog.show(activity,"Loading Data","Please wait",true,true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                pDialog.dismiss();

                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;

                //Parsing the Stored JSOn String to GetHttpResponse Method.
                new GetHttpResponseFromHttpWebCall(activity).execute();

            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put("UserID",params[0]);

                ParseResult = httpParse.postRequest(ResultHash, HttpUrlSwitched(idMenu));

                return ParseResult;
            }
        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute(PreviousListViewClickedItem);
    }
    //endregion

    //region Parsing Complete JSON Object.
    private class GetHttpResponseFromHttpWebCall extends AsyncTask<Void, Void, Void>
    {
        public Context context;

        ArrayList<Chapter> tempArray;

        public GetHttpResponseFromHttpWebCall(Context context)
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
            try
            {
                if(FinalJSonObject != null && !FinalJSonObject.equals("No Results Found.")) //When no data, it will return "No Results Found." Value to String JSONObject.
                {
                    JSONArray jsonArray = null;

                    try {
                        jsonArray = new JSONArray(FinalJSonObject);

                        JSONObject jsonObject;

                        Chapter tempModel;
                        tempArray = new ArrayList<>();

                        for(int i=0; i<jsonArray.length(); i++)
                        {
//                                student = new Student();
                            tempModel = new Chapter();

                            jsonObject = jsonArray.getJSONObject(i);

                            tempModel.setId(Integer.parseInt(jsonObject.getString("Id")));
                            tempModel.setTitle(jsonObject.getString("Name"));
                            tempModel.setCategoryId(Integer.parseInt(jsonObject.getString("CategoryId")));
                            tempModel.setPauseTime(Integer.parseInt(jsonObject.getString("PauseTime")));
                            tempModel.setInsertTime(Integer.parseInt(jsonObject.getString("InsertTime")));
                            tempModel.setContent(jsonObject.getString("TextContent"));
                            tempModel.setFileUrl(jsonObject.getString("FileUrl"));
                            tempArray.add(tempModel);

                            if (list.size()>= tempArray.size()) {
                                SetUpdateTableData(i, tempModel, TableSwitched(idMenu));
                            } else {
                                SetInsertTableData(tempModel,TableSwitched(idMenu));

                            }
                        }
                    }
                    catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
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
            pDialog.dismiss();
            progressBar.setVisibility(View.GONE);
            GetCursorData(TableSwitched(idMenu));
            Log.d("MyTagView", "onPostExecute: "+ titleHome);

        }
    }
    //endregion
    /**
     * Lấy dữ liệu thông qua intent
     */
    private void getDataFromIntent() {
        titleHome = getIntent().getStringExtra("titleHome");
        idMenu = getIntent().getIntExtra("idHome", -1);
    }

    /**
     * Khai báo các view và khởi tạo giá trị
     */
    private void initView() {
        session = new Session(activity);
        requestQueue = Volley.newRequestQueue(activity);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        imRefresh = (View) findViewById(R.id.imRefresh);
        actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, titleHome, true);
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
                    progressBar.setVisibility(View.GONE);
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
                    obj.getInt("pauseTime"));
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
                    progressBar.setVisibility(View.GONE);
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
