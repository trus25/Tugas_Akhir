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
import java.util.StringTokenizer;

public class RegisterActivity extends AppCompatActivity {

    String urlAddress = Constant.BASE_URL + "Login/register";
    EditText userTxt,passTxt,namaTxt,emailTxt,phoneTxt,passcfrmTxt;

    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        TextView login = (TextView) findViewById(R.id.regcancel);
        userTxt = (EditText) findViewById(R.id.rgUsername);
        passTxt = (EditText) findViewById(R.id.rgPassword);
        passcfrmTxt = (EditText) findViewById(R.id.rgPasswordCfrm);
        namaTxt = (EditText) findViewById(R.id.rgNama);
        emailTxt = (EditText) findViewById(R.id.rgEmail);
        phoneTxt = (EditText) findViewById(R.id.rgPhone);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validate()){
                    Toast.makeText(getBaseContext(), "Register failed", Toast.LENGTH_LONG).show();
                }else{
                    register();
                }

            }
        });
    }

    public void login(){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void register() {
        Register s=new Register(RegisterActivity.this,urlAddress,userTxt,passTxt,namaTxt, emailTxt, phoneTxt);
        s.execute();
    }

    public boolean validate() {
        boolean valid = true;

        String username = userTxt.getText().toString();
        String password = passTxt.getText().toString();
        String passcfrm = passcfrmTxt.getText().toString();
        String nama = namaTxt.getText().toString();
        String email = emailTxt.getText().toString();
        String phone = phoneTxt.getText().toString();
        if(username.isEmpty()){
            userTxt.setError("Username harus diisi");
            valid = false;
        }else{
            userTxt.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passTxt.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        }else if(!password.equals(passcfrm)){
            passTxt.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        }else{
            passTxt.setError(null);
        }

        if(nama.isEmpty()){
            namaTxt.setError("Username harus diisi");
            valid = false;
        }else{
            namaTxt.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTxt.setError("enter a valid email address");
            valid = false;
        } else {
            emailTxt.setError(null);
        }

        if(phone.isEmpty()){
            phoneTxt.setError("Username harus diisi");
            valid = false;
        }else{
            phoneTxt.setError(null);
        }
        return valid;
    }

    public class Register extends AsyncTask<Void,Void,String> {

        Context c;
        String urlAddress;
        EditText userTxt,passTxt,namaTxt,emailTxt,phoneTxt;

        String username,password,nama,email,phonenumber;
        Data[] data = new Data[5];
        ProgressDialog pd;

        /*
                1.OUR CONSTRUCTOR
        2.RECEIVE CONTEXT,URL ADDRESS AND EDITTEXTS FROM OUR MAINACTIVITY
        */
        public Register(Context c, String urlAddress,EditText...editTexts) {
            this.c = c;
            this.urlAddress = urlAddress;

            //INPUT EDITTEXTS
            this.userTxt=editTexts[0];
            this.passTxt=editTexts[1];
            this.namaTxt=editTexts[2];
            this.emailTxt=editTexts[3];
            this.phoneTxt=editTexts[4];

            //GET TEXTS FROM EDITEXTS
            username=userTxt.getText().toString();
            password=passTxt.getText().toString();
            nama=namaTxt.getText().toString();
            email=emailTxt.getText().toString();
            phonenumber= phoneTxt.getText().toString();
            data[0] = new Data("username", username);
            data[1] = new Data("password", password);
            data[2] = new Data("nama", nama);
            data[3] = new Data("email", email);
            data[4] = new Data("phone", phonenumber);
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
                if(response.equals("failed")) {
                    Toast.makeText(c,"Register Gagal" + email,Toast.LENGTH_LONG).show();
                }else if(response.equals("success")){
                    Toast.makeText(c,"Register Sukses" + email,Toast.LENGTH_LONG).show();
                }else{
                    StringTokenizer st = new StringTokenizer(response, "|");
                    String stusername = st.nextToken();
                    String stemail = st.nextToken();
                    String stphonenumber = st.nextToken();
                    if (stusername.equals("1")) {
                        userTxt.setError("Username sudah ada");
                    }
                    if (stemail.equals("1")){
                        emailTxt.setError("Email sudah ada");
                    }
                    if(stphonenumber.equals("1")){
                        phoneTxt.setError("Nomor HP sudah ada");
                    }
                    Toast.makeText(c,response+"=>"+username+" "+email+" "+phonenumber,Toast.LENGTH_LONG).show();
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
