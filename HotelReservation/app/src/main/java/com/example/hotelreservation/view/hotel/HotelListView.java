package com.example.hotelreservation.view.hotel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hotelreservation.R;
import com.example.hotelreservation.controller.DownloadImageTask;
import com.example.hotelreservation.model.Hotel;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HotelListView extends ArrayAdapter<Hotel>{
    private Context context;
    int resource;
    ArrayList<Hotel> hotels;

    public HotelListView(Context context, int resource, ArrayList<Hotel> hotels) {
        super(context, resource , hotels);
        this.context=context;
        this.hotels = hotels;
        this.resource=resource;
    }

    @NonNull
    @Override

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View r=convertView;
        ViewHolder viewHolder=null;
        if(r==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            r=inflater.inflate(R.layout.book_row,parent,false);
            viewHolder=new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else {
            viewHolder=(ViewHolder)r.getTag();

        }
        Hotel hotel = getItem(position);
        String harga="Rp " + currencyFormat(hotel.getHarga());
        viewHolder.tvw1.setText(hotel.getNama());
        viewHolder.tvw2.setText(hotel.getAlamat());
        viewHolder.tvw3.setText(harga);
        new DownloadImageTask(viewHolder.ivw).execute(hotel.getImagepath());


        return r;
    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }


    class ViewHolder{

        TextView tvw1;
        TextView tvw2;
        TextView tvw3;
        ImageView ivw;

        ViewHolder(View v){
            tvw1=(TextView)v.findViewById(R.id.tvprofilename);
            tvw2=(TextView)v.findViewById(R.id.tvemail);
            tvw3=(TextView)v.findViewById(R.id.tvprice);
            ivw=(ImageView)v.findViewById(R.id.imageView);
        }

    }
}