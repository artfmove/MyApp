package com.android.artem.myapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.artem.myapp.R;
import com.android.artem.myapp.model.Cache;
import com.android.artem.myapp.util.Act;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private Fragment selectedFragment;
    private int currentItemId = R.id.search_song;
    private FirebaseAuth auth;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logOrSign("Do you have an account or you want to Sign In?");

        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);


        bottomNav.setOnNavigationItemSelectedListener(navListener);


        selectedFragment = new SearchListActivity();


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (currentItemId == item.getItemId()) {
                        return true;
                    }

                    selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.search_song:
                            currentItemId = R.id.search_song;
                            selectedFragment = new SearchListActivity();
                            break;
                        case R.id.fav_list:
                            currentItemId = R.id.fav_list;
                            selectedFragment = new FavouriteListActivity();
                            break;
                        case R.id.settings:
                            currentItemId = R.id.settings;
                            selectedFragment = new SettingsFragment();
                            break;

                    }



                    /*FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.fragment_container, selectedFragment).commit();*/

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };

    @Override
    protected void onResumeFragments() {


        super.onResumeFragments();
    }

    public void logOrSign(String text) {
        builder = new AlertDialog.Builder(this);
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            break;
                    }
                }
            };

            builder.setMessage(text).setPositiveButton("Go to Log/Sign In, ", dialogClickListener)
                    .setNegativeButton("No, maybe later", dialogClickListener)
                    .setTitle("Choose")
                    .setIcon(R.drawable.ic_done_white_24dp)
                    .show();
        }
    }
}
