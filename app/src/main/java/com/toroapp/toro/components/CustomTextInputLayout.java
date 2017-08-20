package com.toroapp.toro.components;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.activities.MainActivity;
import com.toroapp.toro.utils.Font;

import java.lang.reflect.Field;

/**
 * Created by daemonsoft on 24/5/16.
 */
public class CustomTextInputLayout extends TextInputLayout
{

    private static final String MODULE = MainActivity.class.getSimpleName();
    private static String TAG = "";

    Font font;
    float mTextSize;
    AppCompatActivity mActivity;

    public CustomTextInputLayout(Context context)
    {
        super(context);
        initFont(context);

    }

    public CustomTextInputLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mActivity = (AppCompatActivity) context;
        initFont(context);
    }

    private void initFont(Context context)
    {

        TAG="initFont";
        Log.d(MODULE,TAG);

        font = MyApplication.getInstance().getFontInstance();
        EditText editText = getEditText();
        if (editText != null)
        {
            editText.setTypeface(font.getRobotoRegular());
            editText.setSingleLine(true);
        }
        try
        {
            // Retrieve the CollapsingTextHelper Field
            final Field cthf = TextInputLayout.class.getDeclaredField("mCollapsingTextHelper");
            cthf.setAccessible(true);

            // Retrieve an instance of CollapsingTextHelper and its TextPaint
            final Object cth = cthf.get(this);
            final Field tpf = cth.getClass().getDeclaredField("mTextPaint");
            tpf.setAccessible(true);

            // Apply your Typeface to the CollapsingTextHelper TextPaint
            ((TextPaint) tpf.get(cth)).setTypeface(font.getRobotoRegular());
            mTextSize = (float) mActivity.getResources().getDimension(R.dimen.text_size_medium);
            int textColor = ContextCompat.getColor(mActivity,R.color.color_white);
            ((TextPaint) tpf.get(cth)).setTextSize(mTextSize);
            ((TextPaint) tpf.get(cth)).setColor(textColor);
            ((TextPaint) tpf.get(cth)).setTypeface(font.getRobotoRegular());
        }
        catch (Exception ignored)
        {
            ignored.printStackTrace();
        }
    }

}
