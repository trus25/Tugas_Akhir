package com.example.hotelreservation.view.myroom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
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
import com.example.hotelreservation.controller.CaptureActivityAnyOrientation;
import com.example.hotelreservation.controller.Connector;
import com.example.hotelreservation.controller.DataPackager;
import com.example.hotelreservation.controller.SessionManagement;
import com.example.hotelreservation.model.Data;
import com.example.hotelreservation.model.Myroom;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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

public class RoomFragment extends Fragment{
    String urladdress="http://192.168.1.102/Hotel/Pengguna/getMyroom/";
    ListView listView;
    ArrayList<Myroom> arrayList;
    BufferedInputStream is;
    String line=null;
    String result=null;
    String id;
    Data[] postdata = new Data[1];
    String scanContent;
    String scanFormat;
    String QRCODE_STRING;
    int getposition;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);
        listView = (ListView)view.findViewById(R.id.lviewroom);
        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        arrayList = new ArrayList<Myroom>();
        SessionManagement sessionManagement = new SessionManagement(getContext());
        this.id = String.valueOf(sessionManagement.getSession());
        postdata[0]= new Data("id",id);
        String result = send(urladdress);
        collectData(getContext(),result);
        MyroomListView myroomListView =new MyroomListView(getContext(),R.layout.room_row,arrayList);
        listView.setAdapter(myroomListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(),"test123",Toast.LENGTH_LONG).show();
                Myroom myroom = arrayList.get(position);
                Button checkbutton=view.findViewById(R.id.buttonroom);
                getposition = position;

                if(myroom.getCheckinstats().equals("1")){
                    Intent intent = new Intent(getContext(), MyroomActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", myroom.getIdkmr());
                    intent.putExtras(bundle);
                    intent.putExtra("idtr", myroom.getIdtr());
                    intent.putExtra("nomor", myroom.getNomor());
                    intent.putExtra("nama", myroom.getNama());
                    intent.putExtra("image", myroom.getImagepath());
                    intent.putExtra("status", "owner");
                    startActivity(intent);
                }else{
                    //for fragment you need to instantiate integrator in this way
                    IntentIntegrator scanIntegrator = IntentIntegrator.forSupportFragment(RoomFragment.this);
                    scanIntegrator.setPrompt("Scan");
                    scanIntegrator.setBeepEnabled(true);

//                enable the following line if you want QR code
                    scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);

                    scanIntegrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
                    scanIntegrator.setOrientationLocked(true);
                    scanIntegrator.setBarcodeImageEnabled(true);
                    scanIntegrator.initiateScan();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            if (scanningResult.getContents() != null) {
                scanContent = scanningResult.getContents().toString();
                scanFormat = scanningResult.getFormatName().toString();
                Myroom myroom = arrayList.get(getposition);
                QRCODE_STRING = id+"-"+myroom.getIdht()+"-"+myroom.getNomor();
                if(scanContent.equals(QRCODE_STRING)){
                    String urlcheckin="http://192.168.1.102/Hotel/Pengguna/confirm/";
                    postdata[0]= new Data("idkamar", myroom.getIdkmr());
                    String result = send(urlcheckin);
                    if(result != null)
                    {
                        if(result.equals("success")) {
                            Toast.makeText(getContext(),"Check-in Sukses",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getContext(), MyroomActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("id", myroom.getIdkmr());
                            intent.putExtras(bundle);
                            intent.putExtra("idtr", myroom.getIdtr());
                            intent.putExtra("nomor", myroom.getNomor());
                            intent.putExtra("nama", myroom.getNama());
                            intent.putExtra("image", myroom.getImagepath());
                            intent.putExtra("status", "owner");
                            startActivity(intent);

                        }else{
                            Toast.makeText(getContext(),"Check-in Error",Toast.LENGTH_LONG).show();
                        }
                    }else
                    {
                        //NO SUCCESS
                        Toast.makeText(getContext(),"Error",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "QRCode tidak cocok", Toast.LENGTH_SHORT).show();
                }
            }
            Toast.makeText(getActivity(), scanContent + "   type:" + scanFormat, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Nothing scanned", Toast.LENGTH_SHORT).show();
        }
    }
}
