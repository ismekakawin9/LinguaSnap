package com.example.linguasnap.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.os.StrictMode;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linguasnap.API.ApiService;
import com.example.linguasnap.BookmarkActivity;
import com.example.linguasnap.HistoryActivity;
import com.example.linguasnap.model.User;
import com.example.linguasnap.activity_loginds;
import com.example.linguasnap.changepass;
import com.example.linguasnap.imageToText.ImageToTextActivity;
import com.example.linguasnap.R;
import com.example.linguasnap.client.GrammarBotClient;
import com.example.linguasnap.model.GrammarBotResponse;
import com.example.linguasnap.model.Word;
import com.example.linguasnap.utils.TextCorrection;
import com.example.linguasnap.utils.TranslateFrom;
import com.example.linguasnap.utils.TranslateTo;
import com.example.linguasnap.utils.utils;
import com.google.android.material.navigation.NavigationView;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    String [] fromLanguages;
    String [] toLanguages;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private ImageView ivSearchText;
    private ImageView ivSpeakerFrom;
    private ImageView ivSpeakerTo;
    private TextToSpeech textToSpeech;
    private ImageView ivSwitchLanguage;
    private ProgressBar pbTranslation;
    private ImageView ivCopyText;
    private ImageView iv_camera_option;
    private EditText EnterText;
    private TextView Translated, TextFrom, TextTo,email_user1;
    private String originalText;
    private String translatedText;
    private boolean connected;
    private Spinner SpinnerFrom;
    private Spinner SpinnerTo;
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
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ImageView iv_microphone;
    private static final int RESULT_SPECCH = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EnterText = findViewById(R.id.EnterText);
        Translated = findViewById(R.id.Translated);
        Button btnTranslate = findViewById(R.id.btnTranslate);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String key = mAuth.getUid();
        //menu
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar2);
        //
        setSupportActionBar(toolbar);
        //
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_tran);
        navigationView.setCheckedItem(R.id.nav_logout);
        navigationView.setCheckedItem(R.id.nav_his);
        updateNavHeader();

        iv_microphone = findViewById(R.id.iv_microphone);


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
        fromLanguages = getResources().getStringArray(R.array.LanguageFrom);
        toLanguages = getResources().getStringArray(R.array.LanguageTo);
        TextFrom = findViewById(R.id.TextFrom);
        TextTo = findViewById(R.id.TextTo);
        SpinnerFrom = (Spinner) findViewById(R.id.SpinnerFrom);
        SpinnerTo = (Spinner) findViewById(R.id.SpinnerTo);
        pbTranslation = findViewById(R.id.pb_load_translation);
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

        if(getSavedFromLanguage()!= null){
            int idLanguage = Arrays.asList(fromLanguages).indexOf(getSavedFromLanguage());
            SpinnerFrom.setSelection(idLanguage);
        }

        if(getSavedToLanguage()!= null){
            int idLanguage = Arrays.asList(toLanguages).indexOf(getSavedToLanguage());
            SpinnerTo.setSelection(idLanguage);
        }

        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EnterText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                if (!BaseLanguage.equals(SelectedLanguage)) {
                    if (checkInternetConnection()) {
                        getTranslateService();
                        translate();
                        DatabaseReference usersRef = database.getReference("User").child(key).child("History").push();
                        String keyID = usersRef.getKey();
                        String from = TextFrom.getText().toString().trim();
                        String to = TextTo.getText().toString().trim();
                        String inputtext = EnterText.getText().toString().trim();
                        String translatetext= Translated.getText().toString().trim();
                        int like = 0;
                        User user = new User(keyID,from,to,inputtext,translatetext,like);
                        usersRef.setValue(user);
//                              LanguageDetect();
                        if(SpinnerFrom.getSelectedItem().toString().equals("English")){
                            sendGrammarBotRequest();
                        }
                        if(wordCount(inputtext)<3) {
                            clickCallApi();
                        }
                    } else {
                        Translated.setText("No internet connection");
                    }
                } else {
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

        ivSearchText = findViewById(R.id.iv_text_search);
        ivSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText();
            }
        });

        ivCopyText = findViewById(R.id.iv_copy_content);
        ivCopyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToClipboard();
            }
        });

        ivSwitchLanguage = findViewById(R.id.iv_swap_language);
        ivSwitchLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchLanguages();
            }
        });

        textToSpeech= new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    Set<Locale> supportedLanguages = textToSpeech.getAvailableLanguages();
                    for (Locale locale : supportedLanguages) {
                        Log.d("Supported Language", locale.getDisplayLanguage());
                    }
                }
                else {
                    Log.d("MainActivity","TTS not initialized");
                }
            }
        });

        ivSpeakerFrom = findViewById(R.id.iv_speaker_from);
        ivSpeakerFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak(SpinnerFrom.getSelectedItem().toString(),EnterText.getText().toString());
            }
        });

        ivSpeakerTo = findViewById(R.id.iv_speaker_to);
        ivSpeakerTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak(SpinnerTo.getSelectedItem().toString(),Translated.getText().toString());
            }
        });
    }

    public void speak(String language,String text){
        String languageCode = getLanguageCode(language);

        if (languageCode != null) {
            textToSpeech.setLanguage(new Locale(languageCode));
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
        else {
            Log.e("TTS", "Language code not found");
        }
    }
    public String getLanguageCode(String languageName) {
        Locale[] availableLocales = Locale.getAvailableLocales();
        for (Locale locale : availableLocales) {
            if (languageName.equalsIgnoreCase(locale.getDisplayName())) {
                return locale.getLanguage();
            }
        }
        if(Objects.equals(languageName, "Chinese (Simplified)")){
            String languageCode = "zh-CN";
            return languageCode;
        }
        if(Objects.equals(languageName, "Chinese (Traditional)"))
        {
            String languageCode = "zh-TW";
            return languageCode;
        }
        return null; // Language code not found
    }

    private void updateNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        email_user1 = headerView.findViewById(R.id.email_user1);
        email_user1.setText(mUser.getEmail());
    }

    public void showTranslation(){
        Translated.setVisibility(View.VISIBLE);
        WordDefinition.setVisibility(View.VISIBLE);
    }

    public void hideTranslation(){
        Translated.setVisibility(View.INVISIBLE);
        WordDefinition.setVisibility(View.INVISIBLE);
    }

    public void switchLanguages(){
        String fromLanguage= SpinnerFrom.getSelectedItem().toString();
        String toLanguage = SpinnerTo.getSelectedItem().toString();
        int changedFromLanguageId = Arrays.asList(fromLanguages).indexOf(toLanguage);
        int changedToLanguageId = Arrays.asList(toLanguages).indexOf(fromLanguage);
        SpinnerFrom.setSelection(changedFromLanguageId);
        SpinnerTo.setSelection(changedToLanguageId);
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
        showTranslation();
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

    public void searchText(){
        if(translatedText!=null){
            String searchString = translatedText;
            String url = null;
            try {
                url = "https://www.google.com/search?q=" + URLEncoder.encode(searchString, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }
    public void saveToClipboard(){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clip = ClipData.newPlainText("translatedText", translatedText);

        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
    }

    public String getSavedFromLanguage(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        String fromLanguage = sharedPreferences.getString("fromLanguage", null);
        return fromLanguage;
    }
    public String getSavedToLanguage(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        String toLanguage = sharedPreferences.getString("toLanguage", null);
        return toLanguage;
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
        if (requestCode == utils.MYREQUEST_CODE) {
            String strinputtext = data.getStringExtra("getinputfromhistory");
            String strtranslatetext = data.getStringExtra("getoutputfromhistory");
            String strgetto = data.getStringExtra("getto");
            String strgetfrom = data.getStringExtra("getfrom");
            TextFrom.setText(strgetfrom);
            TextTo.setText(strgetto);
            EnterText.setText(strinputtext);
            Translated.setText(strtranslatetext);
            int strfrom = Arrays.asList(fromLanguages).indexOf(strgetfrom);
            int strto = Arrays.asList(toLanguages).indexOf(strgetto);
            SpinnerTo.setSelection(strto);
            SpinnerFrom.setSelection(strfrom);
        }
        if (requestCode == utils.BOOKMARK_CODE){
            String strinputtext = data.getStringExtra("getinputfrombookmark");
            String strtranslatetext = data.getStringExtra("getoutputfrombookmark");
            String strgetto = data.getStringExtra("getto");
            String strgetfrom = data.getStringExtra("getfrom");
            TextFrom.setText(strgetfrom);
            TextTo.setText(strgetto);
            EnterText.setText(strinputtext);
            Translated.setText(strtranslatetext);
            int strfrom = Arrays.asList(fromLanguages).indexOf(strgetfrom);
            int strto = Arrays.asList(toLanguages).indexOf(strgetto);
            SpinnerTo.setSelection(strto);
            SpinnerFrom.setSelection(strfrom);
        }
        if (requestCode == utils.IMAGE_ACTIVITY_CODE) {
            String value = data.getStringExtra("value");
            String fromLanguage = data.getStringExtra("fromLanguage");
            int idLanguage = Arrays.asList(fromLanguages).indexOf(fromLanguage);
            Toast.makeText(MainActivity.this, fromLanguage, Toast.LENGTH_LONG).show();
            SpinnerFrom.setSelection(idLanguage);
            EnterText.setText(value);
            originalText = value;
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_tran:
                break;
            case R.id.nav_his:
                Intent intenthis = new Intent(MainActivity.this,HistoryActivity.class);
                startActivityForResult(intenthis,utils.MYREQUEST_CODE);
                break;
            case R.id.nav_changpass:
                Intent intenthis1 = new Intent(MainActivity.this, changepass.class);
                startActivity(intenthis1);
                break;
            case R.id.nav_bookmark:
                Intent intentbookmark = new Intent(MainActivity.this, BookmarkActivity.class);
                startActivityForResult(intentbookmark,utils.BOOKMARK_CODE);
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent1 = new Intent(MainActivity.this, activity_loginds.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fromLanguage", SpinnerFrom.getSelectedItem().toString());
        editor.putString("toLanguage", SpinnerTo.getSelectedItem().toString());
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the Text-to-Speech resources
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
    public int wordCount(@NonNull String Word){
        return Word.split("\\s").length;
    }
}
