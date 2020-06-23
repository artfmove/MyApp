package com.android.artem.myapp.util;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import java.util.Random;


public class mColor {



        private static String mColors[] = {
                "#39add1", // light blue [0]
                "#3079ab", // dark blue [1]
                "#c25975", // mauve [2]
                "#e15258", // red
                "#f9845b", // orange
                "#838cc7", // lavender
                "#7d669e", // purple
                "#53bbb4", // aqua
                "#51b46d", // green
                "#e0ab18", // mustard
                "#637a91", // dark gray
                "#f092b0", // pink
                "#b7c0c7"  // light gray
        };

        private static String mColors2[] = {
                "#3079ab", //dark blue  [0]
                "#39add1", // light blue [1]
                "#e0ab18", // mustard [2]
                "#637a91", // dark gray
                "#f092b0", // pink
                "#b7c0c7",  // light gray
                "#c25975", // mauve
                "#51b46d", // green
                "#e15258", // red
                "#f9845b", // orange
                "#838cc7", // lavender
                "#7d669e", // purple
                "#53bbb4", // aqua
        };

    public static int[] getGcolors() {
        String color = "#000000";
        String color2 = " ";

        Random randomGenerator = new Random();
        int randomNumber = randomGenerator.nextInt(mColors.length);

        //color = mColors[randomNumber];
        color2 = mColors2[randomNumber];
        int colorAsInt = Color.parseColor(color);
        int colorAsInt2 = Color.parseColor(color2);

        return new int[]{colorAsInt, colorAsInt2};
    }





}

