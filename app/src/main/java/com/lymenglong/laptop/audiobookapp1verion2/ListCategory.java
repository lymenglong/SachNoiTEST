package com.lymenglong.laptop.audiobookapp1verion2;

import android.content.Context;
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

import com.lymenglong.laptop.audiobookapp1verion2.adapter.CategoryAdapter;
import com.lymenglong.laptop.audiobookapp1verion2.customize.CustomActionBar;
import com.lymenglong.laptop.audiobookapp1verion2.databases.DBHelper;
import com.lymenglong.laptop.audiobookapp1verion2.databases.DatabaseHelper;
import com.lymenglong.laptop.audiobookapp1verion2.http.HttpParse;
import com.lymenglong.laptop.audiobookapp1verion2.http.HttpServicesClass;
import com.lymenglong.laptop.audiobookapp1verion2.model.BookType;
import com.lymenglong.laptop.audiobookapp1verion2.model.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListCategory extends AppCompatActivity{
    private RecyclerView listChapter;
    private ArrayList<Category> chapters;
    private CategoryAdapter adapter;
    private CustomActionBar actionBar;
    private DatabaseHelper databaseHelper;
    private String titleChapter;
    private int idChapter;
    private TextView tvStory;
    private ProgressBar progressBar;
    private DBHelper dbHelper;
    private String HttpUrl = "http://20121969.tk/SachNoiBKIC/AllCategoryData.php";
    private ArrayList<Category> list;






    HttpParse httpParse = new HttpParse();

    // Http Url For Filter Student Data from Id Sent from previous activity.
    String HttpURL = "http://20121969.tk/SachNoiBKIC/FilterCategoryData.php";

    String ParseResult ;
    HashMap<String,String> ResultHash = new HashMap<>();
    String FinalJSonObject ;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chapter);
        getDataFromIntent();
        init();
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        setTitle(titleChapter);
        initDatabase();
        initObject();
    }

    /**
     * Lấy dữ liệu thông qua intent
     */
    private void getDataFromIntent() {
        titleChapter = getIntent().getStringExtra("titleChapter");
        idChapter = getIntent().getIntExtra("idChapter", -1);
    }

    /**
     * Khai báo các view và khởi tạo giá trị
     */
    private void init() {
        actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, titleChapter, false);
        listChapter = (RecyclerView) findViewById(R.id.listView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        /*databaseHelper = new DatabaseHelper(this);
        chapters = databaseHelper.getListSmallChapter(idChapter);
        adapter = new CategoryAdapter(ListCategory.this, chapters);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        listChapter.setAdapter(adapter);*/

    }

    private void initDatabase() {
        String DB_NAME = "menu.sqlite";
        int DB_VERSION = 1;
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS category(Id INTEGER PRIMARY KEY, Name VARCHAR(255), TypeID INTEGER);";
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
        //create database
        dbHelper.QueryData(CREATE_TABLE);

    }

    private void GetCursorData() {
        list.clear();
        Cursor cursor = dbHelper.GetData("SELECT * FROM category");
        while (cursor.moveToNext()){
            if(cursor.getInt(2)== idChapter){
                String name = cursor.getString(1);
                int id = cursor.getInt(0);
                list.add(new Category(id,name));
            }
        }
        cursor.close();
        adapter.notifyDataSetChanged();

    }

    private void initObject() {
            list = new ArrayList<>();
            adapter = new CategoryAdapter(ListCategory.this, list);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
            listChapter.setLayoutManager(mLinearLayoutManager);
            listChapter.setAdapter(adapter);
            /*//get data from json parsing
            new GetHttpResponse(this).execute();*/
            HttpWebCall(String.valueOf(idChapter));
    }

    //region GetHttpResponse all data
    /*private class GetHttpResponse extends AsyncTask<Void, Void, Void>
    {
        public Context context;

        String JSonResult;

        ArrayList<Category> categories;

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

                            Category categoryModel;
//                            studentList = new ArrayList<Student>();
                            categories = new ArrayList<>();

                            for(int i=0; i<jsonArray.length(); i++)
                            {
//                                student = new Student();
                                categoryModel = new Category();

                                jsonObject = jsonArray.getJSONObject(i);

                                // Adding Student Id TO IdList Array.
//                                IdList.add(jsonObject.getString("Id").toString());
                                categoryModel.setId(Integer.parseInt(jsonObject.getString("Id")));

                                //Adding Student Name.
//                                student.StudentName = jsonObject.getString("Name").toString();
                                categoryModel.setTitle(jsonObject.getString("Name").toString());
                                categories.add(categoryModel);
                                int Id = categoryModel.getId();
                                String Name = categoryModel.getTitle();
                                if (list.size()>= categories.size()) {
                                    if (!categoryModel.getTitle().equals(list.get(i).getTitle())) {
                                        String UPDATE_DATA = "UPDATE menu SET Name = '"+Name+"' WHERE Id = '"+Id+"'";
                                        dbHelper.QueryData(UPDATE_DATA);
                                    }
                                } else {
                                    String INSERT_DATA = "INSERT INTO category VALUES('"+Id+"','"+Name+"')";
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
    }*/
    //endregion


    //Method to show current record Current Selected Record
    public void HttpWebCall(final String PreviousListViewClickedItem){

        class HttpWebCallFunction extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

//                pDialog = ProgressDialog.show(ShowCategoryActivity.this,"Loading Data",null,true,true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

//                pDialog.dismiss();

                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;

                //Parsing the Stored JSOn String to GetHttpResponse Method.
                new GetHttpResponse(ListCategory.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put("BookTypeID",params[0]);

                ParseResult = httpParse.postRequest(ResultHash, HttpURL);

                return ParseResult;
            }
        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute(PreviousListViewClickedItem);
    }


    // Parsing Complete JSON Object.
    private class GetHttpResponse extends AsyncTask<Void, Void, Void>
    {
        public Context context;

        ArrayList<Category> categories;

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
            try
            {
                if(FinalJSonObject != null)
                {
                    JSONArray jsonArray = null;

                    try {
                        jsonArray = new JSONArray(FinalJSonObject);

                        JSONObject jsonObject;

                        Category categoryModel;
//                            studentList = new ArrayList<Student>();
                        categories = new ArrayList<>();

                        for(int i=0; i<jsonArray.length(); i++)
                        {
//                                student = new Student();
                            categoryModel = new Category();

                            jsonObject = jsonArray.getJSONObject(i);

                            // Adding Student Id TO IdList Array.
//                                IdList.add(jsonObject.getString("Id").toString());
                            categoryModel.setId(Integer.parseInt(jsonObject.getString("Id")));

                            //Adding Student Name.
//                                student.StudentName = jsonObject.getString("Name").toString();
                            categoryModel.setTitle(jsonObject.getString("Name").toString());
                            categories.add(categoryModel);
                            int Id = categoryModel.getId();
                            String Name = categoryModel.getTitle();
                            if (list.size()>= categories.size()) {
                                if (!categoryModel.getTitle().equals(list.get(i).getTitle())) {
                                    String UPDATE_DATA = "UPDATE category SET Name = '"+Name+"' WHERE Id = '"+Id+"' AND TypeID = '"+idChapter+"'";
                                    dbHelper.QueryData(UPDATE_DATA);
                                }
                            } else {
                                String INSERT_DATA = "INSERT INTO category VALUES('"+Id+"','"+Name+"','"+idChapter+"')";
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
            Log.d("MyTagView", "onPostExecute: "+ titleChapter);

        }
    }
}
