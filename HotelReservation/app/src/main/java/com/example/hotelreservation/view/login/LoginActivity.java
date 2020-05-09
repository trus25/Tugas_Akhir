package com.example.hotelreservation.view.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hotelreservation.R;
import com.example.hotelreservation.controller.Connector;
import com.example.hotelreservation.controller.DataPackager;
import com.example.hotelreservation.controller.SessionManagement;
import com.example.hotelreservation.model.Data;
import com.example.hotelreservation.model.User;
import com.example.hotelreservation.view.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class LoginActivity extends AppCompatActivity {
    String urlAddress = "http://192.168.1.102/Hotel/Login/login";
    EditText userTxt,passTxt;

    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkSession();
    }

    public void checkSession(){
        SessionManagement sessionManagement = new SessionManagement(LoginActivity.this);
        int userId = sessionManagement.getSession();
        String userNama = sessionManagement.getUsername();
        String userPass = sessionManagement.getPassword();
        if(userId != -1){
            User user = new User(userId,userNama, userPass);
            moveToMainActivity();
        }else{

        }
    }

    public void login(View view) {
        userTxt= (EditText) findViewById(R.id.etUsername);
        passTxt= (EditText) findViewById(R.id.etPassword);
        Login s=new Login(LoginActivity.this,urlAddress,userTxt,passTxt);
        s.execute();
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
            return this.send();
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
                    int userid = 0;

                    try {
                        userid = Integer.parseInt(response);
                    } catch(NumberFormatException nfe) {
                        System.out.println("Could not parse " + nfe);
                    }
                    User user = new User(userid,userTxt.getText().toString(), passTxt.getText().toString());
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

        /*
        SEND DATA OVER THE NETWORK
        RECEIVE AND RETURN A RESPONSE
         */
        private String send()
        {
            //CONNECT
            HttpURLConnection con= Connector.connect(urlAddress);

            if(con==null)
            {
                return null;
            }

            try
            {
                OutputStream os=con.getOutputStream();

                //WRITE
                BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                bw.write(new DataPackager(data).packData());

                bw.flush();

                //RELEASE RES
                bw.close();
                os.close();

                //HAS IT BEEN SUCCESSFUL?
                int responseCode=con.getResponseCode();

                if(responseCode==con.HTTP_OK)
                {
                    //GET EXACT RESPONSE
                    BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuffer response=new StringBuffer();

                    String line;

                    //READ LINE BY LINE
                    while ((line=br.readLine()) != null)
                    {
                        response.append(line);
                    }

                    //RELEASE RES
                    br.close();

                    return response.toString();

                }else
                {

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }
}
