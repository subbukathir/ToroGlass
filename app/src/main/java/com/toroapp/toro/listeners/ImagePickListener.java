package com.toroapp.toro.listeners;

public interface ImagePickListener
{
    void onSingleImagePicked(String Str_Path);
    void onMultipleImagePicked(String[] Str_Path);
}
