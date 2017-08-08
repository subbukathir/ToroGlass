package com.toroapp.toro.listeners;

/**
 * Created by daemonsoft on 4/12/15.
 */
public interface ImagePickListener
{
    public void onSingleImagePicked(String Str_Path);
    public void onMultipleImagePicked(String[] Str_Path);
}
