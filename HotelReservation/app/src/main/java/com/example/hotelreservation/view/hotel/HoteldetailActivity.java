package com.example.hotelreservation.view.hotel;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotelreservation.R;
import com.example.hotelreservation.controller.Connector;
import com.example.hotelreservation.controller.DataPackager;
import com.example.hotelreservation.controller.DownloadImageTask;
import com.example.hotelreservation.controller.MyEditTextDatePicker;
import com.example.hotelreservation.model.Constant;
import com.example.hotelreservation.view.midtrans.ReviewActivity;
import com.example.hotelreservation.model.Data;
import com.example.hotelreservation.model.Hotel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class HoteldetailActivity extends AppCompatActivity {
    private TextView nama,alamat,harga;
    private ImageView image;
    private EditText tglcheckin, durasi;
    private int position;
    private ArrayList<Hotel> hotels;
    private Data[] postdata= new Data[3];
    private String urladdresskamar = Constant.BASE_URL + "Hotel/getAvailableRoom/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoteldetail);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        nama = findViewById(R.id.hotelname);
        alamat = findViewById(R.id.hotelalamat);
        harga  = findViewById(R.id.hargahotel);
        image = (ImageView)findViewById(R.id.hotelpic);
        tglcheckin = findViewById(R.id.edit_checkin);
        durasi = findViewById(R.id.edit_checkout);
        durasi.setText("1");

        Intent intent = getIntent();

        Bundle bundle = this.getIntent().getExtras();
        String aid = bundle.getString("id");
        String anama= intent.getStringExtra("nama");
        String aalamat= intent.getStringExtra("alamat");
        String aharga = intent.getStringExtra("harga");
        String aimage = intent.getStringExtra("image");



        nama.setText(anama);
        alamat.setText(aalamat);
        harga.setText(currencyFormat(aharga));
        new DownloadImageTask(image).execute(aimage);
        MyEditTextDatePicker myEditTextDatePicker = new MyEditTextDatePicker(this, tglcheckin);
        findViewById(R.id.buttonpay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tglcheckin.getText().toString().equals("")) {
                    postdata[0]= new Data("idhotel",aid);
                    postdata[1]= new Data("checkin",tglcheckin.getText().toString());
                    postdata[2]= new Data("durasi",durasi.getText().toString());
                    String result = send(urladdresskamar, postdata);
                    if(result.equals("null")){
                        Toast.makeText(getApplicationContext(), "Kamar tidak tersedia pada tanggal tersebut", Toast.LENGTH_SHORT).show();
                    }else{
                        Intent intent = new Intent(HoteldetailActivity.this, ReviewActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Bundle bundle = new Bundle();
                        bundle.putString("id", aid);
                        intent.putExtras(bundle);
                        intent.putExtra("nama", anama);
                        intent.putExtra("harga", aharga);
                        intent.putExtra("alamat", aalamat);
                        intent.putExtra("image", aimage);
                        intent.putExtra("tglcheckin", tglcheckin.getText().toString());
                        intent.putExtra("durasi", durasi.getText().toString());
                        startActivity(intent);
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Tanggal wajib diisi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
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
}
