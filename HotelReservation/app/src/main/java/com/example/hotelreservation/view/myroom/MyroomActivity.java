package com.example.hotelreservation.view.myroom;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotelreservation.R;
import com.example.hotelreservation.controller.CaptureActivityAnyOrientation;
import com.example.hotelreservation.controller.Connector;
import com.example.hotelreservation.controller.DataPackager;
import com.example.hotelreservation.controller.DownloadImageTask;
import com.example.hotelreservation.controller.SessionManagement;
import com.example.hotelreservation.model.Constant;
import com.example.hotelreservation.model.Data;
import com.example.hotelreservation.model.Myroom;
import com.example.hotelreservation.model.Share;
import com.example.hotelreservation.view.MainActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
import java.util.StringTokenizer;

public class MyroomActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "personal";
    private static final int NOTIFICATION_ID = 001;
    private boolean NOTIFICATION;
    TextView nomor,nama;
    ImageView picture;
    RecyclerView recyclerView;
    Switch sw, sw2;
    private RecyclerView.LayoutManager layoutManager;
    EditText username;
    RelativeLayout tv;
    Data[] postdata = new Data[1];
    Data[] postsharer = new Data[1];
    Data[] addshare = new Data[2];
    Data[] checkout = new Data[1];
    Button btn,btnshare,btncheckout, btnlift;
    String currentuser, idtr, idht;
    String pintu, listrik, kipas;
    String urladdress=Constant.BASE_URL + "Myroom/roomcond";
    String urlgetsharer=Constant.BASE_URL + "Myroom/getRoomSharer/";
    String urladdshare =Constant.BASE_URL + "Myroom/addShare/";
    String checkinstats;
    Data[] chkhandler = new Data[1];
    String scanContent;
    String scanFormat;
    String QRCODE_STRING;
    ArrayList<Share> arrayList = new ArrayList<Share>();
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10*1000; //Delay for 15 seconds.  One second = 1000 milliseconds.
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
        btnlift = (Button)findViewById(R.id.btnlift);
        username = findViewById(R.id.tambah_sharer);
        sw = (Switch) findViewById(R.id.switch1);
        tv = (RelativeLayout) findViewById(R.id.hiddenlayout);
        sw2 = (Switch) findViewById(R.id.switch2);
        btncheckout = (Button) findViewById(R.id.btncheckout);
        SessionManagement sessionManagement = new SessionManagement(this);
        this.currentuser = String.valueOf(sessionManagement.getUsername());
        chkhandler[0] = new Data("idtransaksi", idtr);


        Intent intent = getIntent();

        Bundle bundle = this.getIntent().getExtras();
        String aid = bundle.getString("id");
        String anama= intent.getStringExtra("nama");
        idtr = intent.getStringExtra("idtr");
        idht = intent.getStringExtra("idht");
        String nomorkamar = intent.getStringExtra("nomor");
        String aimage = intent.getStringExtra("image");
        String astatus = intent.getStringExtra("status");
        checkinstats = intent.getStringExtra("checkinstats");

        if(astatus.equals("shared")){
            setLayoutInvisible();
        }else{
            setLayoutVisible();
        }

        postdata[0]= new Data("idkamar",aid);
        postsharer[0]= new Data("tid", idtr);


        String result = send(urladdress, postdata);
        collectData(this,result);


        if(checkinstats.equals("2")){
            btncheckout.setText("Checkout dalam progres");
            btncheckout.setOnClickListener(null);
        }
//        btn.setText("TERBUKA");
        if(pintu.equals("0")) {
            btn.setText("BUKA");
            btn.setBackgroundColor(Color.parseColor("#4CAF50"));
        }else{
            btn.setText("TUTUP");
            btn.setBackgroundColor(Color.parseColor("#f44336"));
        }

        if(checkinstats.equals("1")){
            if(listrik.equals("0")){
                sw.setChecked(false);
            }else{
                sw.setChecked(true);
            }

            if(listrik.equals("0")){
                sw2.setChecked(false);
            }else{
                sw.setChecked(false);
            }
        }else{
            sw.setClickable(false);
            sw2.setClickable(false);
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
                if(isChecked){
                    String url = Constant.BASE_URL + "Myroom/setLampu/1";
                    send(url, postdata);
                }else{
                    String url = Constant.BASE_URL + "Myroom/setLampu/0";
                    send(url, postdata);
                }

            }
        });

        sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String url = Constant.BASE_URL + "Myroom/setKipas/1";
                    send(url, postdata);
                }else {
                    String url = Constant.BASE_URL + "Myroom/setKipas/0";
                    send(url, postdata);
                }
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(btn.getText().toString().equals("BUKA")) {
                        String url = Constant.BASE_URL + "Myroom/setPintu/1";
                        send(url,postdata);
                        btn.setText("TUTUP");
                        sw.setChecked(true);
                        sw2.setChecked(true);
                        btn.setBackgroundColor(Color.parseColor("#f44336"));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                String url = Constant.BASE_URL + "Myroom/setPintu/0";
                                send(url, postdata);
                                btn.setText("BUKA");
                                btn.setBackgroundColor(Color.parseColor("#4CAF50"));
                            }
                        }, 2000);
                    }else if(btn.getText().toString().equals("TUTUP")){
                        String url = Constant.BASE_URL + "Myroom/setPintu/0";
                        send(url, postdata);
                        btn.setText("BUKA");
                        btn.setBackgroundColor(Color.parseColor("#4CAF50"));
                    }
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

        btncheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkinstats.equals("1")){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyroomActivity.this);
                    alertDialog.setTitle("Konfirmasi Checkout");
                    alertDialog.setMessage("Apakah yakin ingin checkout dari kamar hotel?");
                    alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // DO SOMETHING HERE
                            String url = Constant.BASE_URL + "Myroom/checkout/";
                            checkout[0] = new Data("idtransaksi", idtr);
                            String result = send(url, checkout);
                            if(result.equals("success")){
                                Toast.makeText(getApplicationContext(), "Checkout berhasil", Toast.LENGTH_SHORT).show();
                                checkinstats="2";
                                btncheckout.setText("Checkout In Progress");
                                sw.setChecked(false);
                                sw2.setChecked(false);
                                sw.setClickable(false);
                                sw2.setClickable(false);
                                btncheckout.setOnClickListener(null);
                            }else{
                                Toast.makeText(getApplicationContext(), "Checkout gagal", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
                }else{
                    Toast.makeText(getApplicationContext(), "Tunggu konfirmasi checkout dari petugas hotel", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnlift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //for fragment you need to instantiate integrator in this way
                IntentIntegrator integrator = new IntentIntegrator(MyroomActivity.this);
                integrator.setPrompt("Scan");
                integrator.setBeepEnabled(true);

//                enable the following line if you want QR code
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);

                integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
                integrator.setOrientationLocked(true);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
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
                kipas = listmyroom.getString("kipaskamar");
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


    @Override
    protected void onResume() {
        //start handler as activity become visible
        NOTIFICATION = false;
        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                //do something
                String url = Constant.BASE_URL + "Myroom/chkhandler/";
                chkhandler[0] = new Data("idtransaksi", idtr);
                String result = send(url,chkhandler);
                if(result.equals("wait")){
                    if(!checkinstats.equals("2")){
                        sw.setChecked(false);
                        sw2.setChecked(false);
                        sw.setClickable(false);
                        sw2.setClickable(false);
                        btncheckout.setText("Checkout dalam progres");
                        btncheckout.setOnClickListener(null);
                        checkinstats = "2";
                    }
                    if(!NOTIFICATION){
                        displayNotification();
                        NOTIFICATION = true;
                    }
                }else if(result.equals("true")){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyroomActivity.this);
                    alertDialog.setTitle("Announcement");
                    alertDialog.setMessage("Masa singgah anda telah berakhir!");
//                    alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
                    alertDialog.setNegativeButton("OKE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // DO SOMETHING HERE
                            Intent intent = new Intent(MyroomActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
                }
                handler.postDelayed(runnable, delay);

            }
        }, delay);

        super.onResume();
    }

// If onPause() is not included the threads will double up when you
// reload the activity

    @Override
    protected void onPause() {
//        handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

    public void displayNotification(){
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Checkout Notification")
                .setContentText("Pengunjung yang terhormat. Masa singgah anda telah habis, mohon untuk segera mengurus checkout dengan petugas")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Personal Notification";
            String description = "include all the personal notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

//    @Override
//    protected void onDestroy() {
//        handler.removeCallbacks(runnable); //stop handler when activity not visible
//        super.onDestroy();
//    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            if (scanningResult.getContents() != null) {
                scanContent = scanningResult.getContents().toString();
                scanFormat = scanningResult.getFormatName().toString();
                StringTokenizer st = new StringTokenizer(scanContent, "|");
                String idhotel = st.nextToken();
                String idlift = st.nextToken();
                String lokasilift = st.nextToken();
                QRCODE_STRING = idht;
                if(idhotel.equals(QRCODE_STRING)){
                    String urlcheckin= Constant.BASE_URL + "Hotel/setLift/1";
                    postdata[0]= new Data("idlift", idlift);
                    new Thread(new Runnable() {
                        @Override public void run() {
                            // background code
                           send(urlcheckin, postdata);
                        } }).start();
                    Toast.makeText(this, "Lokasi Lift: " + lokasilift + "  Status: Aktif", Toast.LENGTH_SHORT).show();

                }else if(!idhotel.equals(QRCODE_STRING)){
                    Toast.makeText(this, "Anda tidak memiliki akses lift", Toast.LENGTH_SHORT).show();
                }
            }
            Toast.makeText(this, scanContent + "   type:" + scanFormat, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Nothing scanned", Toast.LENGTH_SHORT).show();
        }
    }
}
