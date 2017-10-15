package com.toroapp.toro.listeners;

import android.view.View;

public interface VoiceListener {
    void onVoiceEnd(boolean success, String text);
}
