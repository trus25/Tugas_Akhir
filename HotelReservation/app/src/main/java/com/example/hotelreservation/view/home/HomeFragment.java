package com.example.hotelreservation.view.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.hotelreservation.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class HomeFragment extends Fragment {
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static final Integer[] img = {R.drawable.img_header_background_1,R.drawable.i_hotel_1,R.drawable.i_hotel_2,
            R.drawable.i_hotel_3,R.drawable.i_hotel_4,R.drawable.i_hotel_5,
            R.drawable.i_hotel_6, R.drawable.i_hotel_7,R.drawable.i_hotel_8};
    private ArrayList<Integer> ImgArray = new ArrayList<Integer>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home,container,false);
        init(view);
        return view;
    }
    private void init (View view){
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
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 2500);
    }
}
