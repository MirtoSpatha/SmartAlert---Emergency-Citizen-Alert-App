package com.john.smartalert;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class MyTts {
    private TextToSpeech tts;
    private String language;

    public MyTts(Context context, String language) {
        this.language = language;
        tts = new TextToSpeech(context,initListener);
    }

    TextToSpeech.OnInitListener  initListener= new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status==TextToSpeech.SUCCESS){
                tts.setLanguage(new Locale(language));
            }
        }
    };

    public void speak(String message){
        tts.speak(message,TextToSpeech.QUEUE_ADD,null,null);
    }
}
