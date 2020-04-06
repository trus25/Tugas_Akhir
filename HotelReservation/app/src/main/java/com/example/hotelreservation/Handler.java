package com.example.hotelreservation;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Handler {
    public static Handler mInstance;
    private RequestQueue requestQueue;
    private static Context mCtx;

    private Handler(Context context){
        mCtx = context;
        requestQueue = getRequestQueue();
    }
    public RequestQueue getRequestQueue(){
        if (requestQueue == null) {
            requestQueue= Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized Handler getInstance(Context context){
        if(mInstance==null){
            mInstance = new Handler(context);
        }
        return mInstance;
    }
    public <T>void addToRequestQue(Request<T> request){
        requestQueue.add(request);
    }
}
