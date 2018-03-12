package com.lymenglong.laptop.audiobookapp1verion2;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lymenglong.laptop.audiobookapp1verion2.Helpers.InputValidation;
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
import java.util.HashMap;
import java.util.Map;


public class LoginActivity2 extends AppCompatActivity{

    private final AppCompatActivity activity = LoginActivity2.this;

    private AppCompatTextView textViewLinkRegister;
    private RequestQueue requestQueue;
//    private static final String URL = "http://192.168.1.27:80/audiobook/mobile_registration/login.php";
    private static final String URL = "http://20121969.tk/audiobook/mobile_registration/login.php";
    private static final String getDataURL = "http://20121969.tk/audiobook/mobile_registration/get_user.php";
    private StringRequest request;
    private Session session;
    private String textEmail;

    private User userModel;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;

    private AppCompatButton appCompatButtonLogin;

    private InputValidation inputValidation;

    private Intent accountsIntent;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initObjects();
        initListeners();
        // Manually checking internet connection
//        checkConnection();

//        ViewCompat.setImportantForAccessibility(getWindow().getDecorView(),ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);

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
                    getUser(s);
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


    public void getUser(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            if (obj.getString("Email").trim().equals(textEmail)
                    || obj.getString("Username").trim().equals(textEmail) ) {
//                userModel = new User(obj.getInt("Id"), obj.getString("Fullname"),obj.getString("Email"));
                userModel = new User();
                userModel.setId(Integer.parseInt(obj.getString("Id")));
                userModel.setName(obj.getString("Fullname"));
                userModel.setEmail(obj.getString("Email"));
                userModel.setPassword(obj.getString("Password"));
                userModel.setAddress(obj.getString("Address"));
                userModel.setIdentitynumber(obj.getString("IdentityNumber"));
                userModel.setBirthday(obj.getString("Birthday"));
                userModel.setPhonenumber(obj.getString("PhoneNumber"));
                userModel.setUsername(obj.getString("Username"));
                // add to list
                users.add(userModel);
                session.setUserInfo(userModel);
                session.setUserIdLoggedIn(String.valueOf(userModel.getId()));
                session.setNameLoggedIn(userModel.getName());
                session.setLoggedin(true);
                session.getUserInfo();
                break;
            }
        }
    }


    private void initListeners() {
        textViewLinkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity3.class);
                startActivity(intentRegister);
                activity.finish();
            }
        });

        appCompatButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email_or_username))) {
                    return;
                }   else{
                    textEmail = textInputEditTextEmail.getText().toString().trim();
                }
                /*if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_message_email))) {
                    return;
                }*/
                if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
                    return;
                }


                request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.names().get(0).equals("success")){
                                Toast.makeText(getApplicationContext(),"Thành công, "+jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                                getJSON(getDataURL);
//                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                accountsIntent = new Intent(getApplicationContext(), MainActivity.class);
                                accountsIntent.putExtra("EMAIL", textInputEditTextEmail.getText().toString().trim());
                                startActivity(accountsIntent);
//                                session.setNameLoggedIn(textInputEditTextEmail.getText().toString().trim());
                                activity.finish();

                            }else {
                                // Snack Bar to show success message that record is wrong
//                                Snackbar.make(nestedScrollView, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show();
                                Toast.makeText(getApplicationContext(), "Lỗi, " +jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String,String> hashMap = new HashMap<String, String>();
                        hashMap.put("Email",textInputEditTextEmail.getText().toString().trim());
                        hashMap.put("Password",textInputEditTextPassword.getText().toString().trim());

                        return hashMap;
                    }
                };

                requestQueue.add(request);
            }
        });
    }




    private void initObjects() {
        inputValidation = new InputValidation(activity);
        requestQueue = Volley.newRequestQueue(this);
        session = new Session(this);
        if(session.loggedin()){
            startActivity(new Intent(LoginActivity2.this,MainActivity.class));
            finish();
        }
    }

    /**
     * This method is to initialize views
     */
    private void initViews() {

        textViewLinkRegister = findViewById(R.id.textViewLinkRegister);

        nestedScrollView = findViewById(R.id.nestedScrollView);

        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);

        textInputEditTextEmail = findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = findViewById(R.id.textInputEditTextPassword);

        appCompatButtonLogin = findViewById(R.id.appCompatButtonLogin);

        textViewLinkRegister = findViewById(R.id.textViewLinkRegister);

    }
}


