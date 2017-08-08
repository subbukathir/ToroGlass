package com.toroapp.toro.components;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.commonsware.cwac.provider.LegacyCompatCursorWrapper;

/**
 * Created by vikram on 8/8/17.
 */

public class LegacyProvider extends FileProvider {
        @Override
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            return(new LegacyCompatCursorWrapper(super.query(uri, projection, selection, selectionArgs, sortOrder)));
        }
}
