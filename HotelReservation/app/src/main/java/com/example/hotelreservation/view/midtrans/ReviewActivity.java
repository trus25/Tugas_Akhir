package com.example.hotelreservation.view.midtrans;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotelreservation.R;
import com.example.hotelreservation.controller.Connector;
import com.example.hotelreservation.controller.DataPackager;
import com.example.hotelreservation.controller.DownloadImageTask;
import com.example.hotelreservation.controller.SessionManagement;
import com.example.hotelreservation.model.Constant;
import com.example.hotelreservation.model.Data;
import com.example.hotelreservation.model.DataCustomer;
import com.example.hotelreservation.model.Kamar;
import com.example.hotelreservation.view.MainActivity;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.PaymentMethod;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;

import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity implements TransactionFinishedCallback, AdapterView.OnItemSelectedListener {
    String urladdress= Constant.BASE_URL + "Transaksi/rentRoom";
    private EditText nama, email, nohp, tglcheckin, durasi;
    private TextView hotelname, alamat, harga, hargatotal;
    private ImageView hotelimage;
    private static final String TAG = "transactionresult";
    private String aid,anama,aharga,aidkamar,anomorkamar,aalamat,aimage, adurasi, atglcheckin;
    int atotalharga;
    Data[] postdata = new Data[6];


    private ArrayList<Kamar> arrayList;
    private Data[] postdatakamar= new Data[3];
    private Spinner spinner;
    private String urladdresskamar = Constant.BASE_URL + "Hotel/getAvailableRoom/";
    private int selectedroom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        init();
        initMid();
    }

    private void init(){
        Intent intent = getIntent();

        Bundle bundle = this.getIntent().getExtras();
         aid = bundle.getString("id");
         anama= intent.getStringExtra("nama");
         aharga= intent.getStringExtra("harga");
         aalamat = intent.getStringExtra("alamat");
         aimage = intent.getStringExtra("image");
         adurasi = intent.getStringExtra("durasi");
         atglcheckin = intent.getStringExtra("tglcheckin");
         atotalharga = Integer.parseInt(aharga) * Integer.parseInt(adurasi);


        hotelimage = findViewById(R.id.product_image);
        hotelname = findViewById(R.id.product_name);
        nama = findViewById(R.id.edit_customer_name);
        email = findViewById(R.id.edit_customer_email);
        nohp = findViewById(R.id.edit_customer_phone);
        alamat = findViewById(R.id.delivery_address);
        hargatotal = findViewById(R.id.text_amount);
        harga = findViewById(R.id.product_price_amount);
        tglcheckin = findViewById(R.id.edit_checkin);
        durasi = findViewById(R.id.edit_checkout);

        spinner = (Spinner)findViewById(R.id.spinner);

        arrayList = new ArrayList<Kamar>();
        postdatakamar[0]= new Data("idhotel",aid);
        postdatakamar[1]= new Data("checkin",atglcheckin);
        postdatakamar[2]= new Data("durasi",adurasi);
        String result = send(urladdresskamar, postdatakamar);
        if(result.equals("null")){
                arrayList.add(new Kamar("0","Kamar Habis"));
        }else{
            collectData(this,result);
        }
        ArrayAdapter<Kamar> adapter = new ArrayAdapter<Kamar>(ReviewActivity.this,
                android.R.layout.simple_spinner_item,arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        //setText

        new DownloadImageTask(hotelimage).execute(aimage);
        hotelname.setText(anama);
        SessionManagement sessionManagement = new SessionManagement(this);
        nama.setText(sessionManagement.getUsername());
        email.setText(sessionManagement.getUserEmail());
        nohp.setText(sessionManagement.getUserPhone());
        alamat.setText(aalamat);
        harga.setText(currencyFormat(aharga));
        hargatotal.setText(currencyFormat(String.valueOf(atotalharga)));
        tglcheckin.setText(atglcheckin);
        durasi.setText(adurasi);

        findViewById(R.id.button_primary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner.getSelectedItem().toString().equals("Kamar Habis")){
                    Toast.makeText(getApplicationContext(), "Kamar habis, mohon pilih tanggal lain", Toast.LENGTH_SHORT).show();
                }else {
//                    if(isConnected()){
                        actionButton();
//                    }
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        Kamar kamar = arrayList.get(position);
        aidkamar = kamar.getId();
        anomorkamar = kamar.getNomor();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

    private void initMid() {
        SdkUIFlowBuilder.init()
                .setClientKey(Constant.CLIENT_KEY) // client_key is mandatory
                .setContext(this) // context is mandatory
                .setTransactionFinishedCallback(this) // set transaction finish callback (sdk callback)
                .setMerchantBaseUrl(Constant.BASE_URL_PAYMENT) //set merchant url (required)
                .enableLog(true) // enable sdk log (optional)
                .setColorTheme(new CustomColorTheme("#FFE51255", "#B61548", "#FFE51255")) // set theme. it will replace theme on snap theme on MAP ( optional)
                .buildSDK();
    }

//    @Override
//    public void onTransactionFinished(TransactionResult transactionResult) {
//        Log.w(TAG, transactionResult.getResponse().getStatusMessage());
//    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    private void actionButton(){
        MidtransSDK.getInstance().setTransactionRequest(DataCustomer.transactionRequest(
                this,
                "1",
                 Integer.parseInt(aharga),
                 Integer.parseInt(adurasi),
                 anama
        ));
        MidtransSDK.getInstance().startPaymentUiFlow(this, PaymentMethod.GO_PAY);
    }

    @Override
    public void onTransactionFinished(TransactionResult result) {
       if(result.getResponse() != null) {
           switch (result.getStatus()) {
               case TransactionResult.STATUS_SUCCESS:
                   Toast.makeText(this, "Transaction Finished ID: " + result.getResponse().getTransactionId(), Toast.LENGTH_SHORT).show();
                   postdata[0] = new Data("idhotel", aid );
                   SessionManagement sessionManagement = new SessionManagement(this);
                   String id = String.valueOf(sessionManagement.getSession());
                   int totalbiaya = Integer.parseInt(aharga)*Integer.parseInt(adurasi);
                   postdata[1] = new Data("iduser", id);
                   postdata[2] = new Data("idkamar",aidkamar);
                   postdata[3] = new Data("checkin", atglcheckin);
                   postdata[4] = new Data("durasi", adurasi);
                   postdata[5] = new Data("total", String.valueOf(totalbiaya));
                   String response = send(urladdress,postdata);
                   if(response.equals("success")){
                       Intent intent = new Intent(this, MainActivity.class);
                       startActivity(intent);
                       this.finish();
                   }else{
                       Toast.makeText(this, "Insert data "+response, Toast.LENGTH_SHORT).show();
                   }
               case TransactionResult.STATUS_PENDING:
                   Toast.makeText(this, "Transaction Pending ID: " + result.getResponse().getTransactionId(), Toast.LENGTH_SHORT).show();
               case TransactionResult.STATUS_FAILED:
                   Toast.makeText(this, "Transaction Failed ID: " + result.getResponse().getTransactionId(), Toast.LENGTH_SHORT).show();
           }
           result.getResponse().getValidationMessages();
       }else if(result.isTransactionCanceled()){
           Toast.makeText(this, "Transaction Canceled ID: ", Toast.LENGTH_SHORT).show();
       }else{
           if(result.getStatus().equalsIgnoreCase(TransactionResult.STATUS_INVALID)){
               Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_SHORT).show();
           }else{
               Toast.makeText(this, "Transaction Finished with failure", Toast.LENGTH_SHORT).show();
           }
       }
    }

    private void collectData(Context c, String response){
        if(response != null)
        {
            try{
                JSONObject jo=new JSONObject(response);
                JSONArray ja=jo.getJSONArray("kamartersedia");

                for(int i=0;i<ja.length();i++){
                    JSONObject listmyroom = ja.getJSONObject(i);
                    arrayList.add(i,new Kamar(
                            listmyroom.getString("idkamar"),
                            listmyroom.getString("nomorkamar")
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

//    public boolean isConnected() {
//        try {
//            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo netInfo = cm.getActiveNetworkInfo();
//
//            if (netInfo != null && netInfo.isConnected()) {
//                // Network is available but check if we can get access from the
//                // network.
//                URL url = new URL("192.168.1.103");
//                HttpURLConnection urlc = (HttpURLConnection) url
//                        .openConnection();
//                urlc.setRequestProperty("Connection", "close");
//                urlc.setConnectTimeout(2000); // Timeout 2 seconds.
//                urlc.connect();
//
//                if (urlc.getResponseCode() == 200) // Successful response.
//                {
//                    return true;
//                } else {
//                    Log.d("NO INTERNET", "NO INTERNET");
//                    Toast.makeText(this, "URL down", Toast.LENGTH_SHORT).show();
//                    return false;
//                }
//            } else {
//                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
}
