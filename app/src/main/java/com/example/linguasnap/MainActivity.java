package com.example.linguasnap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.os.StrictMode;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private ImageView iv_camera_option;
    private EditText EnterText;
    private TextView Translated;
    private String originalText;
    private String translatedText;
    private boolean connected;
    TranslateFrom tf = new TranslateFrom();
    TranslateTo to = new TranslateTo();
    String SelectedLanguage;
    String BaseLanguage;
    String detectedLanguage;
    Translate translate;

    int idLanguage = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getTranslateService();
        iv_camera_option=findViewById(R.id.iv_camera_option);
        iv_camera_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent= new Intent(MainActivity.this,ImageToTextActivity.class);
                startActivity(cameraIntent);
            }
        });
        String [] languages = getResources().getStringArray(R.array.LanguageFrom);
        TextView TextFrom = findViewById(R.id.TextFrom);
        TextView TextTo = findViewById(R.id.TextTo);
        Spinner SpinnerFrom = (Spinner) findViewById(R.id.SpinnerFrom);
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
                if(checkInternetConnection()){
                    //getTranslateService();
                    translate();
                    String CurrentValue = SpinnerFrom.getItemAtPosition(SpinnerFrom.getSelectedItemPosition()).toString();
                    Toast.makeText(getApplicationContext(),CurrentValue,Toast.LENGTH_SHORT).show();
                    if(CurrentValue.equals("Auto Detect")){
                        TextFrom.setText("Auto detected : "+ LanguageDetect());
                    }
                    LanguageDetect();
                } else{
                    Translated.setText("No internet connection");
                }
                EnterText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                /*idLanguage = Arrays.asList(languages).indexOf(LanguageDetect());
                SpinnerFrom.setSelection(idLanguage);*/
                idLanguage = 0;

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
        Translation translation = translate.translate(originalText, Translate.TranslateOption.targetLanguage(to.selectCountry(SelectedLanguage)), Translate.TranslateOption.model(tf.selectCountry(BaseLanguage)));
        translatedText = translation.getTranslatedText();
        //Translated text and original text are set to TextViews:
        Translated.setText(translatedText);
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


}