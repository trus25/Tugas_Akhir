package com.example.hotelreservation.view.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.hotelreservation.R;
import com.example.hotelreservation.controller.SessionManagement;
import com.example.hotelreservation.view.MainActivity;
import com.example.hotelreservation.view.hotel.BookFragment;
import com.example.hotelreservation.view.login.LoginActivity;
import com.example.hotelreservation.view.myroom.RoomFragment;
import com.example.hotelreservation.view.share.ShareFragment;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static final Integer[] img = {R.drawable.img_header_background_1,R.drawable.i_hotel_1,R.drawable.i_hotel_2,
            R.drawable.i_hotel_3,R.drawable.i_hotel_4,R.drawable.i_hotel_5,
            R.drawable.i_hotel_6, R.drawable.i_hotel_7,R.drawable.i_hotel_8};
    private ArrayList<Integer> ImgArray = new ArrayList<Integer>();
    Timer swipeTimer = new Timer();
    private CardView home, room, share, logout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home,container,false);
        init(view);
        home = (CardView) view.findViewById(R.id.homemenu1);
        room = (CardView) view.findViewById(R.id.homemenu2);
        share = (CardView) view.findViewById(R.id.homemenu3);
        logout = (CardView) view.findViewById(R.id.homemenu4);

        home.setOnClickListener(this);
        room.setOnClickListener(this);
        share.setOnClickListener(this);
        logout.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        switch (v.getId()){
            case R.id.homemenu1:
                fragment = new BookFragment();
                break;
            case R.id.homemenu2:
                fragment = new RoomFragment();
                break;
            case R.id.homemenu3:
                fragment = new ShareFragment();
                break;
            case R.id.homemenu4:
                SessionManagement sessionManagement = new SessionManagement(getContext());
                sessionManagement.removeSession();
                moveToLogin();
                break;
        }
        loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getActivity()
                    .getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            return true;
        }
        return false;
    }

    public void moveToLogin(){
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void init (View view){
        if(!ImgArray.isEmpty()){
            ImgArray.clear();
        }
        for (int i=0; i<img.length; i++)
            ImgArray.add(img[i]);

        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPager.setAdapter(new ImageAdapter(getContext(), ImgArray));
        CircleIndicator indicator = (CircleIndicator)view.findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            @Override
            public void run() {
                if (currentPage == img.length){
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        //Auto start
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 2500);
    }


    @Override
    public void onPause() {
//        handler.removeCallbacks(runnable); //stop handler when activity not visible
        if(swipeTimer != null) {
            swipeTimer.cancel();
            swipeTimer = null;
        }
        super.onPause();
    }
}
