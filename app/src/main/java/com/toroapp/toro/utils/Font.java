package com.toroapp.toro.utils;

import android.content.Context;
import android.graphics.Typeface;

public class Font {

    private Typeface type;
    private Context context;

    public Font(Context context) {
        this.context = context;
    }

    public Typeface getHelveticaRegular() {
        type = Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaRegular.otf");
        return type;
    }

    public Typeface getTrajanMedium() {
        type = Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaMedium.otf");
        return type;
    }

    public Typeface getHelveticaBold() {
        type = Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaBold.otf");
        return type;
    }

    public Typeface getRobotoBold() {
        type = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
        return type;
    }

    public Typeface getRobotoMedium() {
        type = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
        return type;
    }

    public Typeface getRobotoRegular() {
        type = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        return type;
    }

}
