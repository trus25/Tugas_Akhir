package com.example.hotelreservation.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotelreservation.R;
import com.example.hotelreservation.controller.SessionManagement;
import com.example.hotelreservation.model.Share;
import com.example.hotelreservation.model.User;
import com.example.hotelreservation.view.home.HomeFragment;
import com.example.hotelreservation.view.hotel.BookFragment;
import com.example.hotelreservation.view.login.LoginActivity;
import com.example.hotelreservation.view.myroom.RoomFragment;
import com.example.hotelreservation.view.share.ShareFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

//import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
//    private static final String LAST_OPENED_FRAGMENT_REF = "LAST_OPENED_FRAGMENT_REF";
    private DrawerLayout drawer;
    TextView textView;
    private User user;

//    private static final int HOME_FRAGMENT = 0;
//    private static final int BOOK_FRAGMENT = 1;
//    private static final int ROOM_FRAGMENT= 2;

//    private int currentOpenedFragment = HOME_FRAGMENT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =  findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        //header view
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.username);
        SessionManagement sessionManagement = new SessionManagement(MainActivity.this);
        String username = "hello, " + sessionManagement.getUsername();
        navUsername.setText(username);


        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.open,R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if(savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putInt(LAST_OPENED_FRAGMENT_REF, currentOpenedFragment);
//    }
//    private Fragment initFragmentByType(int type) {
//        switch(type) {
//            case HOME_FRAGMENT: return new HomeFragment();
//            case BOOK_FRAGMENT: return new BookFragment();
//            default: throw new IllegalArgumentException("There is no type: " + type);
//        }
//    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch(item.getItemId()){
            case R.id.book:
                fragment = new BookFragment();
                break;
            case R.id.room:
                fragment = new RoomFragment();
                break;
            case R.id.shared:
                fragment = new ShareFragment();
                break;
            case R.id.logout:

                SessionManagement sessionManagement = new SessionManagement(MainActivity.this);
                sessionManagement.removeSession();
                moveToLogin();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return loadFragment(fragment);
    }

    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else{
            super.onBackPressed();
        }

    }

    public void moveToLogin(){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        System.out.println("the code is catch");
    }
}
