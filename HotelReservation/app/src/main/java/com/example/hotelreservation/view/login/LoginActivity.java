package com.example.hotelreservation.view.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotelreservation.R;
import com.example.hotelreservation.controller.Connector;
import com.example.hotelreservation.controller.DataPackager;
import com.example.hotelreservation.controller.SessionManagement;
import com.example.hotelreservation.model.Constant;
import com.example.hotelreservation.model.Data;
import com.example.hotelreservation.model.User;
import com.example.hotelreservation.view.MainActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class LoginActivity extends AppCompatActivity {
    String urlAddress = Constant.BASE_URL + "login";
    EditText userTxt,passTxt;

    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        TextView register = (TextView) findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkSession();
    }

    public void checkSession(){
        SessionManagement sessionManagement = new SessionManagement(LoginActivity.this);
        int userId = sessionManagement.getSession();
        if(userId != -1){
            moveToMainActivity();
        }
    }

    public void login() {
        userTxt= (EditText) findViewById(R.id.etUsername);
        passTxt= (EditText) findViewById(R.id.etPassword);
        Login s=new Login(LoginActivity.this,urlAddress,userTxt,passTxt);
        s.execute();
    }

    public void register(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void moveToMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }






    public class Login extends AsyncTask<Void,Void,String> {

        Context c;
        String urlAddress;
        EditText userTxt,passTxt;

        String username,password;
        String userUsername, userNama, userEmail, userPhonenumber;
        int userid;
        Data[] data = new Data[2];
        ProgressDialog pd;

        /*
                1.OUR CONSTRUCTOR
        2.RECEIVE CONTEXT,URL ADDRESS AND EDITTEXTS FROM OUR MAINACTIVITY
        */
        public Login(Context c, String urlAddress,EditText...editTexts) {
            this.c = c;
            this.urlAddress = urlAddress;

            //INPUT EDITTEXTS
            this.userTxt=editTexts[0];
            this.passTxt=editTexts[1];

            //GET TEXTS FROM EDITEXTS
            username=userTxt.getText().toString();
            password=passTxt.getText().toString();
            data[0] = new Data("username", username);
            data[1] = new Data("password", password);

        }
        /*
       1.SHOW PROGRESS DIALOG WHILE DOWNLOADING DATA
        */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd=new ProgressDialog(c);
            pd.setTitle("Send");
            pd.setMessage("Sending..Please wait");
            pd.show();
        }

        /*
        1.WHERE WE SEND DATA TO NETWORK
        2.RETURNS FOR US A STRING
         */
        @Override
        protected String doInBackground(Void... params) {
            Connector con = new Connector();
            return con.send(urlAddress, data);
        }

        /*
      1. CALLED WHEN JOB IS OVER
      2. WE DISMISS OUR PD
      3.RECEIVE A STRING FROM DOINBACKGROUND
       */
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            pd.dismiss();

            if(response != null)
            {
                //SUCCESS
                if(response.equals("gagal")){
                    Toast.makeText(c,"Login "+response+": username: " +data[0].getPostData()+"password: " + data[1].getPostData(),Toast.LENGTH_LONG).show();
                }else {
                    try {
                        collectData(LoginActivity.this, response);
                    } catch(NumberFormatException nfe) {
                        System.out.println("Could not parse " + nfe);
                    }
                    User user = new User(userid,userTxt.getText().toString(),passTxt.getText().toString(),userNama,userEmail,userPhonenumber);
                    SessionManagement sessionManagement = new SessionManagement(LoginActivity.this);
                    sessionManagement.saveSession(user);
                    moveToMainActivity();
                    Toast.makeText(c,"Login Sukses",Toast.LENGTH_LONG).show();
                }
            }else
            {
                //NO SUCCESS
                Toast.makeText(c,"Error ",Toast.LENGTH_LONG).show();
            }
        }

        private void collectData(Context c, String response){
            if(response != null)
            {
                try{
                    JSONObject jo=new JSONObject(response);
                    JSONObject userdata = jo.getJSONObject("userdata");
                    userid = Integer.parseInt(userdata.getString("id"));
                    userUsername = userdata.getString("username");
                    userNama = userdata.getString("nama");
                    userEmail = userdata.getString("email");
                    userPhonenumber= userdata.getString("phone");
                }
                catch (Exception ex)
                {

                    ex.printStackTrace();
                }
            }else
            {
                //NO SUCCESS
                Toast.makeText(c,response,Toast.LENGTH_LONG).show();
            }
        }

        /*
        SEND DATA OVER THE NETWORK
        RECEIVE AND RETURN A RESPONSE
         */
    }
}
