package com.example.linguasnap.main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.os.StrictMode;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linguasnap.API.ApiService;
import com.example.linguasnap.imageToText.ImageToTextActivity;
import com.example.linguasnap.R;
import com.example.linguasnap.client.GrammarBotClient;
import com.example.linguasnap.model.GrammarBotResponse;
import com.example.linguasnap.model.Word;
import com.example.linguasnap.utils.TextCorrection;
import com.example.linguasnap.utils.TranslateFrom;
import com.example.linguasnap.utils.TranslateTo;
import com.example.linguasnap.utils.utils;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    String [] languages;
    private ImageView iv_camera_option;
    private EditText EnterText;
    private TextView Translated;
    private String originalText;
    private String translatedText;
    private boolean connected;
    private Spinner SpinnerFrom;
    private TextView tv_suggestedText;
    private LinearLayout llSuggestion;
    private  String suggestionText;
    TranslateFrom tf = new TranslateFrom();
    TranslateTo to = new TranslateTo();
    String SelectedLanguage;
    String BaseLanguage;
    String detectedLanguage;
    Translate translate;
    private TextView WordDefinition;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_camera_option=findViewById(R.id.iv_camera_option);
        iv_camera_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent= new Intent(MainActivity.this, ImageToTextActivity.class);
                startActivityForResult(cameraIntent, utils.IMAGE_ACTIVITY_CODE);
            }
        });
        llSuggestion=findViewById(R.id.ll_suggestion);
        WordDefinition = findViewById(R.id.WordDefinition);
        tv_suggestedText = findViewById(R.id.tv_suggestedText);
        languages = getResources().getStringArray(R.array.LanguageFrom);
        TextView TextFrom = findViewById(R.id.TextFrom);
        TextView TextTo = findViewById(R.id.TextTo);
        SpinnerFrom = (Spinner) findViewById(R.id.SpinnerFrom);
        Spinner SpinnerTo = (Spinner) findViewById(R.id.SpinnerTo);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.LanguageFrom, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.LanguageTo, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerFrom.setAdapter(adapter1);
        SpinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                BaseLanguage = adapterView.getItemAtPosition(i).toString();
                String Language = adapterView.getItemAtPosition(i).toString();
                TextFrom.setText(Language);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        SpinnerTo.setAdapter(adapter2);
        SpinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedLanguage = adapterView.getItemAtPosition(i).toString();
                String LanguageTemp = adapterView.getItemAtPosition(i).toString();
                TextTo.setText(LanguageTemp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        EnterText = findViewById(R.id.EnterText);
        Translated = findViewById(R.id.Translated);
        Button btnTranslate = findViewById(R.id.btnTranslate);
        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EnterText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                if (!BaseLanguage.equals(SelectedLanguage)) {
                    if (checkInternetConnection()) {
                        getTranslateService();
                        translate();
                        //LanguageDetect();
//                    sendGrammarBotRequest();
                        clickCallApi();

                    } else {
                        Translated.setText("No internet connection");
                    }
//                TextFrom.setText(LanguageDetect());
//                int idLanguage = Arrays.asList(languages).indexOf(LanguageDetect());
//                SpinnerFrom.setSelection(idLanguage);
                }
                else{
                    Translated.setText(originalText);
                }
            }
        });

        llSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(suggestionText!=null) {
                    EnterText.setText(suggestionText);
                    llSuggestion.setVisibility(View.GONE);
                }
            }
        });
    }
    public void getTranslateService() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (InputStream is = getResources().openRawResource(R.raw.credentials)) {

            //Get credentials:
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //Set credentials and get translate service:
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();

        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }
    public void translate() {
        //Get input text to be translated:
        originalText = EnterText.getText().toString();
        Detection detect = translate.detect(originalText);
        String detectedText = detect.getLanguage();
        if(BaseLanguage.equals("Auto Detect")) {
            Translation translation = translate.translate(originalText, Translate.TranslateOption.targetLanguage(to.selectCountry(SelectedLanguage)), Translate.TranslateOption.sourceLanguage(detectedText));
            translatedText = translation.getTranslatedText();
        }
        else {
            Translation translation = translate.translate(originalText, Translate.TranslateOption.targetLanguage(to.selectCountry(SelectedLanguage)), Translate.TranslateOption.sourceLanguage(tf.selectCountry(BaseLanguage)));
            translatedText = translation.getTranslatedText();
        }
        //Translated text and original text are set to TextViews:
        Translated.setText(translatedText);
    }

    public void sendGrammarBotRequest(){
        RequestBody body = new FormBody.Builder()
                .add("text", EnterText.getText().toString())
                .add("language", "en-US")
                .build();

        try {
            GrammarBotClient.sendRequest("https://grammarbot.p.rapidapi.com/check", body, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful()){
                        Gson gson = new Gson();
                        GrammarBotResponse grammarBotResponse = gson.fromJson(response.body().charStream(), GrammarBotResponse.class);
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(grammarBotResponse.getMatches().size() != 0){
                                    llSuggestion.setVisibility(View.VISIBLE);
                                    suggestionText = TextCorrection.getSuggestedText(grammarBotResponse.getMatches());
                                    MainActivity.this.tv_suggestedText.setText(suggestionText);
                                }
                                else {
                                    llSuggestion.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public String LanguageDetect(){
        //int duration = Toast.LENGTH_SHORT;
        //Context context = getApplicationContext();
        originalText = EnterText.getText().toString();
        Detection detection = translate.detect(originalText);
        detectedLanguage = detection.getLanguage();
        /*Toast toast = Toast.makeText(context,detectedLanguage,duration);
        toast.show();*/
        return tf.detect(detectedLanguage);
    }
    public boolean checkInternetConnection() {

        //Check internet connection:
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Means that we are connected to a network (mobile or wi-fi)
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        return connected;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK){
            return;
        }

        if (requestCode == utils.IMAGE_ACTIVITY_CODE) {
            String value = data.getStringExtra("value");
            String fromLanguage = data.getStringExtra("fromLanguage");
            int idLanguage = Arrays.asList(languages).indexOf(fromLanguage);
            Toast.makeText(MainActivity.this,fromLanguage,Toast.LENGTH_LONG).show();
            SpinnerFrom.setSelection(idLanguage);
            EnterText.setText(value);
            originalText=value;
        }
    }
    private void clickCallApi(){
        ApiService.apiservice.engDictionary(translatedText).enqueue(new retrofit2.Callback<List<Word>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Word>> call, retrofit2.Response<List<Word>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Get the first word object in the list
                    Word word = response.body().get(0);

                    // Get the first meaning object in the list
                    Word.Meaning meaning = word.getMeanings().get(0);

                    // Get the first definition object in the list
                    Word.Meaning.Definition definition = meaning.getDefinitions().get(0);

                    // Use the getter and setter methods to access the "definition" field
                    String definitionText = definition.getDefinition();
                    WordDefinition.setText(definitionText);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Word>> call, Throwable t) {

            }
        });
    }
}