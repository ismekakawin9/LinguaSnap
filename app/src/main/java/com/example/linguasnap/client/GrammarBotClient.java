package com.example.linguasnap.client;

import com.example.linguasnap.model.Match;
import com.example.linguasnap.model.Replacement;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class GrammarBotClient {
    public static void sendRequest(String url, RequestBody requestBody, Callback callback) throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("X-RapidAPI-Key", "7aae8fcf14msh6cfe2b693635677p1042c3jsn396fea93bbe4")
                .addHeader("X-RapidAPI-Host", "grammarbot.p.rapidapi.com")
                .build();

        client.newCall(request).enqueue(callback);
    }
}
