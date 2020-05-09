package com.example.hotelreservation.view.hotel;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hotelreservation.R;
import com.example.hotelreservation.controller.DownloadImageTask;
import com.example.hotelreservation.model.Hotel;

import java.util.ArrayList;

public class HoteldetailActivity extends AppCompatActivity {
    TextView nama,alamat,harga;
    ImageView image;
    int position;
    ArrayList<Hotel> hotels;
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
        Intent intent = getIntent();

        Bundle bundle = this.getIntent().getExtras();
        String aid = bundle.getString("id");
        String anama= intent.getStringExtra("nama");
        String aalamat= intent.getStringExtra("alamat");
        String aharga = intent.getStringExtra("harga");
        String aimage = intent.getStringExtra("image");

        nama.setText(anama);
        alamat.setText(aalamat);
        harga.setText(aharga);
        new DownloadImageTask(image).execute(aimage);
    }

}
