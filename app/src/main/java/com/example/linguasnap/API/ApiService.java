package com.example.linguasnap.API;

import com.example.linguasnap.model.Word;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    String Url = "https://api.dictionaryapi.dev/";
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create();
    //Link api: https://api.dictionaryapi.dev/api/v2/entries/en/
    ApiService apiservice = new Retrofit.Builder()
            .baseUrl(Url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);
    @GET("api/v2/entries/en/{word}")
    Call<List<Word>> engDictionary(@Path("word") String word);
}
