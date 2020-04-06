package com.example.hotelreservation;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BookFragment extends Fragment {
    String urladdress="http://jaisonjoy.com/android/displayprofile.php";
//    String[] name = {"Favehotel Rungkut Surabaya","Best Western Papilio Hotel","MaxOneHotels at Tidar Surabaya"};
    // String[] email = {"Jl. Raya Kalirungkut No.23-25, Surabaya, Indonesia","Jl. Ahmad Yani 176 - 178, Surabaya","Jl. Tidar No. 5, Surabaya, Indonesia"};
    // String[] imagepath = {"http://192.168.100.12/IoTSuhu/assets/img/i_hotel_1.jpg","http://192.168.100.12/IoTSuhu/assets/img/i_hotel_2.jpg","http://192.168.100.12/IoTSuhu/assets/img/i_hotel_3.jpg"};
    String[] name;
    String[] email;
    String[] imagepath;
    ListView listView;
    BufferedInputStream is;
    String line=null;
    String result=null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        listView = (ListView)view.findViewById(R.id.lview);
        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        collectData();
        CustomListView customListView=new CustomListView(getContext(),name,email,imagepath);
        listView.setAdapter(customListView);

        return view;
    }

    private void collectData()
    {
//Connection
        try{

            URL url=new URL(urladdress);
            HttpURLConnection con=(HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            is=new BufferedInputStream(con.getInputStream());

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        //content
        try{
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
            JSONArray ja=new JSONArray(result);
            JSONObject jo=null;
            name=new String[ja.length()];
            email=new String[ja.length()];
            imagepath=new String[ja.length()];

            for(int i=0;i<=ja.length();i++){
                jo=ja.getJSONObject(i);
                name[i]=jo.getString("name");
                email[i]=jo.getString("email");
                imagepath[i]=jo.getString("photo");
            }
        }
        catch (Exception ex)
        {

            ex.printStackTrace();
        }


    }
}