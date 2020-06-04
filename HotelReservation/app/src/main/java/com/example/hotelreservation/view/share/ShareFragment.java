package com.example.hotelreservation.view.share;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hotelreservation.R;
import com.example.hotelreservation.controller.Connector;
import com.example.hotelreservation.controller.DataPackager;
import com.example.hotelreservation.controller.SessionManagement;
import com.example.hotelreservation.model.Constant;
import com.example.hotelreservation.model.Data;
import com.example.hotelreservation.model.Myroom;
import com.example.hotelreservation.view.myroom.MyroomActivity;
import com.example.hotelreservation.view.myroom.MyroomListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class ShareFragment extends Fragment {
    String urladdress= Constant.BASE_URL + "Share/getSharedRoom/";
    ListView listView;
    ArrayList<Myroom> arrayList;
    BufferedInputStream is;
    String line=null;
    String result=null;
    String id;
    Data[] postdata = new Data[1];
    int getposition;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);
        listView = (ListView)view.findViewById(R.id.lviewshare);
        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        arrayList = new ArrayList<Myroom>();
        SessionManagement sessionManagement = new SessionManagement(getContext());
        this.id = String.valueOf(sessionManagement.getSession());
        postdata[0]= new Data("id",id);
        String result = send(urladdress);
        collectData(getContext(),result);
        ShareListView shareListView =new ShareListView(getContext(),R.layout.room_row,arrayList);
        listView.setAdapter(shareListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(),"test123",Toast.LENGTH_LONG).show();
                Myroom myroom = arrayList.get(position);
                getposition = position;
                Intent intent = new Intent(getContext(), MyroomActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", myroom.getIdkmr());
                intent.putExtras(bundle);
                intent.putExtra("idtr", myroom.getIdtr());
                intent.putExtra("nomor", myroom.getNomor());
                intent.putExtra("nama", myroom.getNama());
                intent.putExtra("image", myroom.getImagepath());
                intent.putExtra("status", "shared");
                startActivity(intent);
            }
        });
        return view;
    }

    private void collectData(Context c, String response){
        if(response != null)
        {
            try{
                JSONObject jo=new JSONObject(response);
                JSONArray ja=jo.getJSONArray("share");

                for(int i=0;i<ja.length();i++){
                    JSONObject listmyroom = ja.getJSONObject(i);
                    arrayList.add(new Myroom(
                            listmyroom.getString("idtransaksi"),
                            listmyroom.getString("iduser"),
                            listmyroom.getString("idhotel"),
                            listmyroom.getString("idkamar"),
                            listmyroom.getString("tanggal"),
                            listmyroom.getString("checkin"),
                            listmyroom.getString("checkout"),
                            listmyroom.getString("checkinstats"),
                            listmyroom.getString("currentstatus"),
                            listmyroom.getString("nama"),
                            listmyroom.getString("alamat"),
                            listmyroom.getString("image"),
                            listmyroom.getString("nomor")
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

    private String send(String urlAddress)
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
            bw.write(new DataPackager(postdata).packData());

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
