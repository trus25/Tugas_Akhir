package com.example.hotelreservation.view.myroom;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotelreservation.R;
import com.example.hotelreservation.controller.Connector;
import com.example.hotelreservation.controller.DataPackager;
import com.example.hotelreservation.controller.DownloadImageTask;
import com.example.hotelreservation.controller.SessionManagement;
import com.example.hotelreservation.midtrans.ReviewActivity;
import com.example.hotelreservation.model.Data;
import com.example.hotelreservation.model.Kamar;
import com.example.hotelreservation.model.Myroom;
import com.example.hotelreservation.model.Share;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class MyroomActivity extends AppCompatActivity {
    TextView nomor,nama;
    ImageView picture;
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    EditText username;
    RelativeLayout tv;
    Data[] postdata = new Data[1];
    Data[] postsharer = new Data[1];
    Data[] addshare = new Data[2];
    Button btn,btnshare;
    String pintu, currentuser;
    String listrik;
    String urladdress="http://192.168.1.102/Hotel/Pengguna/roomcond";
    String urlgetsharer="http://192.168.1.102/Hotel/Pengguna/getRoomSharer/";
    String urladdshare ="http://192.168.1.102/Hotel/Pengguna/addShare/";
    ArrayList<Share> arrayList = new ArrayList<Share>();

    final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myroom);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        nomor  = findViewById(R.id.nomorkamar);
        nama  = findViewById(R.id.namahotelroom);
        picture = (ImageView)findViewById(R.id.hotelpicroom);
        btn = (Button)findViewById(R.id.buttonpintu);
        btnshare = (Button)findViewById(R.id.button_share);
        username = findViewById(R.id.tambah_sharer);
        Switch sw = (Switch) findViewById(R.id.switch1);
        tv = (RelativeLayout) findViewById(R.id.hiddenlayout);
        SessionManagement sessionManagement = new SessionManagement(this);
        this.currentuser = String.valueOf(sessionManagement.getUsername());


        Intent intent = getIntent();

        Bundle bundle = this.getIntent().getExtras();
        String aid = bundle.getString("id");
        String anama= intent.getStringExtra("nama");
        String idtr = intent.getStringExtra("idtr");
        String nomorkamar = intent.getStringExtra("nomor");
        String aimage = intent.getStringExtra("image");
        String astatus = intent.getStringExtra("status");

        if(astatus.equals("shared")){
            setLayoutInvisible();
        }else{
            setLayoutVisible();
        }

        postdata[0]= new Data("idkamar",aid);
        postsharer[0]= new Data("tid", idtr);


        String result = send(urladdress, postdata);
        collectData(this,result);



//        btn.setText("TERBUKA");
        if(!pintu.equals("0")) {
            String url = "http://192.168.1.102/Hotel/Pengguna/setPintu/";
            send(url, postdata);
        }
        btn.setText("BUKA");
        btn.setBackgroundColor(Color.parseColor("#4CAF50"));
        if(listrik.equals("0")){
            sw.setChecked(false);
        }else{
            sw.setChecked(true);
        }
        nama.setText(anama);
        nomor.setText(nomorkamar);
        new DownloadImageTask(picture).execute(aimage);

        String resultshare = send(urlgetsharer, postsharer);
        collectDatashare(this, resultshare);
        recyclerView = (RecyclerView) findViewById(R.id.listsharer);
        MyroomAdapter myroomAdapter = new MyroomAdapter(this,R.layout.sharelist_row, arrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myroomAdapter);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String url = "http://192.168.1.102/Hotel/Pengguna/setLampu/";
                send(url, postdata);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String url = "http://192.168.1.102/Hotel/Pengguna/setPintu/";
                    send(url,postdata);
                    btn.setText("TUTUP");
                    btn.setBackgroundColor(Color.parseColor("#f44336"));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            send(url,postdata);
                            btn.setText("BUKA");
                            btn.setBackgroundColor(Color.parseColor("#4CAF50"));
                        }
                    }, 2000);
            }
        });

        btnshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!username.getText().toString().equals("")){
                    if(username.getText().toString().equals(currentuser)){
                        Toast.makeText(getApplicationContext(), "Tidak dapat diisi dengan username anda", Toast.LENGTH_SHORT).show();
                    }else{
                        addshare[0]= new Data("tid", idtr);
                        addshare[1]= new Data("username", username.getText().toString());
                        String resultshare = send(urladdshare, addshare);
                        if(resultshare.equals("notexist")){
                            Toast.makeText(getApplicationContext(), "Username tidak ada!", Toast.LENGTH_SHORT).show();
                        }else if (resultshare.equals("exist")){
                            Toast.makeText(getApplicationContext(), "User "+username.getText().toString()+" sudah terdaftar di Roomsharing", Toast.LENGTH_SHORT).show();
                        }else if(resultshare.equals("success")){
                            Toast.makeText(getApplicationContext(), "Sharing kamar berhasil", Toast.LENGTH_SHORT).show();
                            arrayList.clear();
                            resultshare = send(urlgetsharer, postsharer);
                            collectDatashare(getApplicationContext(), resultshare);
                            myroomAdapter.notifyDataSetChanged();
                        }else if(resultshare.equals("failed")){
                            Toast.makeText(getApplicationContext(), "Sharing kamar gagal", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Isi username yang akan diberikan akses!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void setLayoutInvisible() {
        if (tv.getVisibility() == View.VISIBLE) {
            tv.setVisibility(View.GONE);
        }
    }

    public void setLayoutVisible() {
        if (tv.getVisibility() == View.GONE) {
            tv.setVisibility(View.VISIBLE);
        }
    }

    private String send(String urlAddress, Data[] post)
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
            bw.write(new DataPackager(post).packData());

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

    private void collectData(Context c, String response){
        if(response != null)
        {
            try{
                JSONObject jo=new JSONObject(response);
                JSONObject listmyroom = jo.getJSONObject("roomcond");
                pintu = listmyroom.getString("pintukamar");
                listrik = listmyroom.getString("lampukamar");
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

    private void collectDatashare(Context c, String response){
        if(response != null)
        {
            try{
                JSONObject jo=new JSONObject(response);
                JSONArray ja=jo.getJSONArray("roomsharer");

                for(int i=0;i<ja.length();i++){
                    JSONObject listmyroom = ja.getJSONObject(i);
                    arrayList.add(new Share(
                            String.valueOf(i+1),
                            listmyroom.getString("sid"),
                            listmyroom.getString("uid"),
                            listmyroom.getString("username")
                    ));
                }
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
}
