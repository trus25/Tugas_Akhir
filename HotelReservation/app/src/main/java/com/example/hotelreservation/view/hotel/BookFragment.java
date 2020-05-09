package com.example.hotelreservation.view.hotel;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hotelreservation.R;
import com.example.hotelreservation.controller.Connector;
import com.example.hotelreservation.model.Hotel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class BookFragment extends Fragment {
    String urladdress="http://192.168.1.102/Hotel/Pengguna/gethotel/";
    String harga;
    ListView listView;
    ArrayList<Hotel> arrayList;
    BufferedInputStream is;
    String line=null;
    String result=null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        listView = (ListView)view.findViewById(R.id.lviewbook);
        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        arrayList = new ArrayList<Hotel>();
        collectData();
        HotelListView hotelListView =new HotelListView(getContext(),R.layout.book_row,arrayList);
        listView.setAdapter(hotelListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(),"test123",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), HoteldetailActivity.class);
                Hotel hotel = arrayList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("id", hotel.getId());
                intent.putExtras(bundle);
                intent.putExtra("nama", hotel.getNama());
                intent.putExtra("alamat",hotel.getAlamat());
                intent.putExtra("image", hotel.getImagepath());
                intent.putExtra("harga", hotel.getHarga());
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        return view;
    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

    private void collectData()
    {
        //Connection
        HttpURLConnection con = Connector.connect(urladdress);
        //content
        try{
            is=new BufferedInputStream(con.getInputStream());
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            StringBuilder sb=new StringBuilder();
            while ((line=br.readLine())!=null){
                sb.append(line+"\n");
            }
            is.close();
            result=sb.toString();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();

        }

//JSON
        try{
            JSONObject jo=new JSONObject(result);
            JSONArray ja=jo.getJSONArray("listhotel");
//            name=new String[ja.length()];
//            email=new String[ja.length()];
//            imagepath=new String[ja.length()];


            for(int i=0;i<ja.length();i++){
                JSONObject listHotel = ja.getJSONObject(i);
                harga="Rp " + currencyFormat(listHotel.getString("harga"));
                arrayList.add(new Hotel(
                        listHotel.getString("idhotel"),
                        listHotel.getString("nama"),
                        listHotel.getString("alamat"),
                        listHotel.getString("image"),
                        harga
                ));
//                name[i]=listHotel.getString("nama");
//                email[i]=listHotel.getString("alamat");
//                imagepath[i]=listHotel.getString("image");
            }
        }
        catch (Exception ex)
        {

            ex.printStackTrace();
        }


    }
}