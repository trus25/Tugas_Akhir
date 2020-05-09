package com.example.hotelreservation.view.myroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hotelreservation.R;
import com.example.hotelreservation.controller.Connector;
import com.example.hotelreservation.controller.DataPackager;
import com.example.hotelreservation.controller.DownloadImageTask;
import com.example.hotelreservation.model.Data;
import com.example.hotelreservation.model.Myroom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class MyroomListView extends ArrayAdapter<Myroom> {
    private Context context;
    int resource;
    ArrayList<Myroom> myrooms;
    Data[] data = new Data[1];
    String urladdress = "192.168.1.102/Hotel/Pengguna/confirm/";
    public MyroomListView(Context context, int resource, ArrayList<Myroom> myrooms) {
        super(context, resource , myrooms);
        this.context=context;
        this.myrooms = myrooms;
        this.resource=resource;
    }

    @NonNull
    @Override

    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View r=convertView;
        ViewHolder viewHolder=null;
        if(r==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            r=inflater.inflate(R.layout.room_row,parent,false);
            viewHolder=new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else {
            viewHolder=(ViewHolder)r.getTag();
        }
        Myroom myroom = getItem(position);

        viewHolder.tvw1.setText(myroom.getNama());
        viewHolder.tvw2.setText(myroom.getAlamat());
        viewHolder.tvw3.setText(myroom.getTanggal());
        new DownloadImageTask(viewHolder.ivw).execute(myroom.getImagepath());
        if(myroom.getCheckinstats().equals("1")){
            viewHolder.btn.setText("MASUK");
        }else{
            viewHolder.btn.setText("CHECK IN");
        }
//        viewHolder.btn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                //do something
//                Myroom myroom = getItem(position);
//                Button checkbutton=(Button)v.findViewById(R.id.buttonroom);
//
//                if(myroom.getCheckinstats().equals("1")){
//                    Toast.makeText(getContext(),"1",Toast.LENGTH_SHORT).show();
//                }else{
//                    data[0]= new Data("idkamar", myroom.getIdkmr());
//                    String result = send(urladdress);
//                    if(result != null)
//                    {
//                        if(result.equals("success")) {
//                            checkbutton.setText("Masuk");
//                            myroom.setCheckinstats("1");
//                            Toast.makeText(getContext(),result,Toast.LENGTH_LONG).show();
//                        }else{
//                            Toast.makeText(getContext(),"Error",Toast.LENGTH_LONG).show();
//                        }
//                    }else
//                    {
//                        //NO SUCCESS
//                        Toast.makeText(getContext(),"Error",Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//        });


        return r;
    }

    class ViewHolder{

        TextView tvw1;
        TextView tvw2;
        TextView tvw3;
        ImageView ivw;
        Button btn;

        ViewHolder(View v){
            tvw1=(TextView)v.findViewById(R.id.tvnamahotel);
            tvw2=(TextView)v.findViewById(R.id.tvalamat);
            tvw3=(TextView)v.findViewById(R.id.tvtanggal);
            ivw=(ImageView)v.findViewById(R.id.tvfotohotel);
            btn=(Button)v.findViewById(R.id.buttonroom);
        }
    }
}
