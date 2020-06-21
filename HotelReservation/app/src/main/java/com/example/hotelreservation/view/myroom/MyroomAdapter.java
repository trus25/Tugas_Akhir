package com.example.hotelreservation.view.myroom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelreservation.R;
import com.example.hotelreservation.controller.Connector;
import com.example.hotelreservation.controller.DataPackager;
import com.example.hotelreservation.model.Constant;
import com.example.hotelreservation.model.Data;
import com.example.hotelreservation.model.Share;
import com.example.hotelreservation.view.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class MyroomAdapter extends RecyclerView.Adapter<MyroomAdapter.ViewHolder> {

    //All methods in this adapter are required for a bare minimum recyclerview adapter
    private int listItemLayout;
    private static ArrayList<Share> itemList;
    private static Context context;
    Connector connector = new Connector();
    // Constructor of the class
    public MyroomAdapter(Context context, int layoutId, ArrayList<Share> itemList) {
        this.context = context;
        listItemLayout = layoutId;
        this.itemList = itemList;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }


    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        TextView item = holder.item;
        item.setText(itemList.get(listPosition).toString());
    }

    // Static inner class to initialize the views of rows
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView item;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            item = (TextView) itemView.findViewById(R.id.list_share);
        }
        @Override
        public void onClick(View view) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("Delete user dari Room Sharing");
            alertDialog.setMessage("Apakah yakin ingin menghapus user tersebut ");
            alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // DO SOMETHING HERE
                    Share share = itemList.get(getLayoutPosition());
                    String sid = share.getSid();
                    String urladdress= Constant.BASE_URL + "delete_sharer";
                    Data[] postdata = new Data[1];
                    postdata[0]= new Data("sid",sid);
                    String result = connector.send(urladdress, postdata);
                    Log.d("onclick", "sid " + sid);

                    if(result.equals("success")){
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        removeAt(getLayoutPosition());
                    }else{
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            AlertDialog dialog = alertDialog.create();
            dialog.show();
        }
    }

    public void removeAt(int position) {
        itemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, itemList.size());
    }
}