package com.android.artem.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.android.artem.myapp.activities.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AlertDialog;


public class isNetwork extends Activity {

    private static boolean bool;



    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static boolean isLogIn(){

        if(FirebaseAuth.getInstance().getCurrentUser()==null)
            bool = false;
        else bool = true;
        return bool;

    }



}
