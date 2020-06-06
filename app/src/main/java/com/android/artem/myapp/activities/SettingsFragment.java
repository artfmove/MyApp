package com.android.artem.myapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.artem.myapp.R;
import com.android.artem.myapp.data.CacheAppData;
import com.android.artem.myapp.model.Cache;
import com.android.artem.myapp.model.Song;
import com.android.artem.myapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.sql.Ref;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.room.Room;

public class SettingsFragment extends PreferenceFragmentCompat{

    private FirebaseAuth auth;

    private DatabaseReference usersDatabaseReference;
    private String name;
    private Preference namePreference, backtracks;
    private ValueEventListener valueEventListener;

    private CacheAppData cacheAppData;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        namePreference = findPreference("name");
        backtracks = findPreference("backtracks");
        backtracks.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                deleteAllCaches();
                return true;
            }
        });

        auth = FirebaseAuth.getInstance();

        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    if(dataSnapshot1.child("id").getValue().toString().equals(auth.getCurrentUser().getUid())) {

                        name = dataSnapshot1.getValue(User.class).getName();
                        namePreference.setSummary(name);
                        usersDatabaseReference.removeEventListener(valueEventListener);
                    }



                    //songsDatabaseReference.removeEventListener(songsChildEventListener);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        usersDatabaseReference.addValueEventListener(valueEventListener);


    }
    private void deleteAllCaches(){
        cacheAppData = Room.databaseBuilder(getContext(), CacheAppData.class, "AllCacheDB")
                .allowMainThreadQueries()
                .build();
        int cacheSize = cacheAppData.getCacheDAO().getAllCaches().size();

        new AlertDialog.Builder(getContext())
                .setTitle("Delete cache?")
                .setMessage("You have" + cacheSize + "songs. Are you sure you want to delete this cache")

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cacheAppData.clearAllTables();
                        //File file = new File(cache.getUrl());
                        File file = new File("/data/user/0/com.android.artem.myapp/cache/");

                        if (file.exists()) {
                            String deleteCmd = "rm -r " + "/data/user/0/com.android.artem.myapp/cache/";
                            Runtime runtime = Runtime.getRuntime();
                            try {
                                runtime.exec(deleteCmd);
                            } catch (IOException e) { }
                        }
                    }
                })

                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }



}
