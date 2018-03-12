package com.lymenglong.laptop.audiobookapp1verion2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lymenglong.laptop.audiobookapp1verion2.adapter.UserInfoRecyclerAdapter;
import com.lymenglong.laptop.audiobookapp1verion2.customize.CustomActionBar;
import com.lymenglong.laptop.audiobookapp1verion2.model.Session;
import com.lymenglong.laptop.audiobookapp1verion2.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {

    private AppCompatActivity activity = UserInfoActivity.this;
    private AppCompatTextView textViewName;
    private AppCompatButton btnLogout;
    private RecyclerView recyclerViewUsers;
    private List<User> listUsers;
    private User user;
    private UserInfoRecyclerAdapter userInfoRecyclerAdapter;
    private Session session;


    private Dialog dialog;

//    private static final String URL = "http://192.168.1.27/audiobook/mobile_registration/get_user.php";
    private static final String URL = "http://20121969.tk/audiobook/mobile_registration/get_user.php";
    private CustomActionBar actionBar;
    private StringRequest stringRequest;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
//        ViewCompat.setImportantForAccessibility(getWindow().getDecorView(),ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
//        getSupportActionBar().setTitle("");
        initToolbar();
        initViews();
        initObjects();
//        getDataFromSQLite();
//        getJSON(URL);
//        postServerRequest();
        getDataFromPrefs();


    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    /**
     * This method is to initialize views
     */
    private void initViews() {
        textViewName = findViewById(R.id.textViewName);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        btnLogout = findViewById(R.id.btn_logout);
    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        session = new Session(this);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        listUsers = new ArrayList<User>();
        requestQueue = Volley.newRequestQueue(activity);
        userInfoRecyclerAdapter = new UserInfoRecyclerAdapter(listUsers);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewUsers.setLayoutManager(mLayoutManager);
        recyclerViewUsers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setAdapter(userInfoRecyclerAdapter);

//        String emailFromIntent = getIntent().getStringExtra("EMAIL");
        String emailFromIntent = session.getNameLoggedIn();
        textViewName.setText("Chào bạn, "+emailFromIntent.toUpperCase());

    }

    private void postServerRequest() {
        stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getJSON(URL);

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


    private void initToolbar() {
        actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, "Tải Khoản", false);
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
                    getDataFromServer(s);
                    userInfoRecyclerAdapter.notifyDataSetChanged();
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
                    BufferedReader bufferedReader = new BufferedReader(new
                            InputStreamReader(con.getInputStream()));
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

    /**
     * This method is to fetch all user records from Server
     */

    private void getDataFromServer(String json) throws JSONException  {
        JSONArray jsonArray = new JSONArray(json);
        listUsers.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            user = new User();
            user.setId(Integer.parseInt(obj.getString("Id")));
            user.setName(obj.getString("Fullname"));
            user.setEmail(obj.getString("Email"));
            user.setPassword(obj.getString("Password"));
            user.setAddress(obj.getString("Address"));
            user.setIdentitynumber(obj.getString("IdentityNumber"));
            user.setBirthday(obj.getString("Birthday"));
            user.setPhonenumber(obj.getString("PhoneNumber"));
            user.setUsername(obj.getString("Username"));
            // Adding user record to list
            listUsers.add(user);
        }
    }


    private void getDataFromPrefs() {
        // AsyncTask is used that SQLite operation not blocks the UI Thread.
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                listUsers.clear();
                listUsers.addAll(session.getUserInfo());

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                userInfoRecyclerAdapter.notifyDataSetChanged();
            }
        }.execute();
    }


    public void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đãng Xuất Tài Khoản");
        builder.setMessage("Bạn có muốn đăng xuất không?");
        builder.setCancelable(false);
        builder.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            Toast.makeText(activity, "Tài khoản chưa được đăng xuất", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Đăng xuất", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            session.getClearSession();
            session.setLoggedin(false);
            Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// This flag ensures all activities on top of the MainActivity are cleared.
            intent.putExtra("EXIT", true);
            Toast.makeText(activity, "Đăng Xuất Thành Công", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
