package com.example.hotelreservation.view.myroom;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hotelreservation.R;
import com.example.hotelreservation.controller.Connector;
import com.example.hotelreservation.controller.DataPackager;
import com.example.hotelreservation.controller.SessionManagement;
import com.example.hotelreservation.model.Data;
import com.example.hotelreservation.model.Myroom;
import com.google.zxing.Result;

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

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class RoomFragment extends Fragment {
    String urladdress="http://192.168.1.102/Hotel/Pengguna/getMyroom/";
    ListView listView;
    ArrayList<Myroom> arrayList;
    BufferedInputStream is;
    String line=null;
    String result=null;
    String id;
    Data[] data = new Data[1];
    private ZXingScannerView mScannerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);
//        mScannerView = new ZXingScannerView(getContext());   // Programmatically initialize the scanner view
//        setContentView(mScannerView);                // Set the scanner view as the content view
        listView = (ListView)view.findViewById(R.id.lviewroom);
        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        arrayList = new ArrayList<Myroom>();
        SessionManagement sessionManagement = new SessionManagement(getContext());
        this.id = String.valueOf(sessionManagement.getSession());
        data[0]= new Data("id",id);
        String result = send(urladdress);
        collectData(getContext(),result);
        MyroomListView myroomListView =new MyroomListView(getContext(),R.layout.room_row,arrayList);
        listView.setAdapter(myroomListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(),"test123",Toast.LENGTH_LONG).show();
                Myroom myroom = arrayList.get(position);
                Button checkbutton=view.findViewById(R.id.buttonroom);

                if(myroom.getCheckinstats().equals("1")){
                    Toast.makeText(getContext(),"1",Toast.LENGTH_SHORT).show();
                }else{
                    String urlcheckin="http://192.168.1.102/Hotel/Pengguna/confirm/";
                    data[0]= new Data("idkamar", myroom.getIdkmr());
                    String result = send(urlcheckin);
                    if(result != null)
                    {
                        if(result.equals("success")) {
                            checkbutton.setText("Masuk");
                            myroom.setCheckinstats("1");
                            Toast.makeText(getContext(),result,Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getContext(),"Error",Toast.LENGTH_LONG).show();
                        }
                    }else
                    {
                        //NO SUCCESS
                        Toast.makeText(getContext(),"Error",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        return view;
    }

    private void collectData(Context c, String response){
        if(response != null)
        {
            try{
                JSONObject jo=new JSONObject(response);
                JSONArray ja=jo.getJSONArray("myroom");

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
                            listmyroom.getString("nama"),
                            listmyroom.getString("alamat"),
                            listmyroom.getString("image")
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

//    @Override
//    public void onResume() {
//        super.onResume();
//        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
//        mScannerView.startCamera();          // Start camera on resume
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mScannerView.stopCamera();           // Stop camera on pause
//    }
//
//    @Override
//    public void handleResult(Result rawResult) {
//        // Do something with the result here
//
//        // If you would like to resume scanning, call this method below:
//        mScannerView.resumeCameraPreview(this);
//    }
}
