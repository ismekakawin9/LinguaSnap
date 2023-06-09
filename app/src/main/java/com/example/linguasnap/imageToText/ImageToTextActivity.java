package com.example.linguasnap.imageToText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linguasnap.R;
import com.example.linguasnap.utils.utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageToTextActivity extends AppCompatActivity {
    private String textResult;
    private String currentImagePath=null;
    private Uri imageUri;
    private TextView tvPictureStatus;
    private ImageView ivCamera;
    private ImageView ivImage;
    private ImageView ivImport;
    private TextView tvTranslatedText;
    private ConstraintLayout clConstraintLayout;
    private ImageView ivTranslate;
    private ImageView ivSearchText;
    private ProgressBar pbLoading;
    private Spinner spFrom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_to_text);

        tvPictureStatus=findViewById(R.id.tv_picture_status);
        ivTranslate = findViewById(R.id.iv_translate);
        ivCamera =findViewById(R.id.iv_camera);
        ivImport = findViewById(R.id.iv_import);
        ivSearchText=findViewById(R.id.iv_search_text);
        pbLoading=findViewById(R.id.pb_load_scanning);
        spFrom=findViewById(R.id.sp_from);

        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ImageToTextActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ImageToTextActivity.this, new String[]{android.Manifest.permission.CAMERA}, utils.CAMERA_PICK_CODE);
                } else {
                    // Permission already granted, start the camera
                    captureImage();
                }
            }
        });

        ivImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGalleryActivity();
            }
        });

        ivTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();

                resultIntent.putExtra("value", textResult);
                resultIntent.putExtra("fromLanguage",spFrom.getSelectedItem().toString());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        ivSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText();
            }
        });
    }

    public void searchText(){
        if(textResult!=null){
            String searchString = textResult;
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

    public void loading(){
        pbLoading.setVisibility(View.VISIBLE);
    }

    public void loadingFinished(){
        pbLoading.setVisibility(View.INVISIBLE);
    }

    public void startGalleryActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Choose your apps"), utils.GALLERY_PICK_CODE);
    }

    public void captureImage(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(cameraIntent.resolveActivity( getPackageManager() ) != null){
            File imageFile = null;

            try {
                imageFile = getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(imageFile!=null){
                imageUri = FileProvider.getUriForFile(this,"com.example.linguasnap.fileprovider",imageFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(cameraIntent,utils.CAMERA_PICK_CODE);
            }
        }
    }

    private File getImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName="jpg_"+timestamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(imageName,".jpg",storageDir);
        currentImagePath=imageFile.getAbsolutePath();
        return imageFile;
    }

    public void recognizeText() {
        InputImage image = null;
        try {
            image = InputImage.fromFilePath(this, imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TextRecognizer recognizer = getTextRecognizer(spFrom.getSelectedItem().toString());

        if(recognizer!=null){
            assert image != null;
            Task<Text> result = recognizer.process(image)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text visionText) {
                            StringBuilder result = new StringBuilder();
                            for (Text.TextBlock block : visionText.getTextBlocks()) {
                                for (Text.Line line : block.getLines()) {
                                    String lineText = line.getText();
                                    result.append(lineText+"\n");
                                }
                            }
                            textResult= String.valueOf(result);
                            displayText(String.valueOf(result));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ImageToTextActivity.this,"Text recognition failed",Toast.LENGTH_LONG).show();
                        }
                    });
        }
        else{
            Log.d("MainActivity","TextRecognizer is null");
        }
    }

    public void displayText(String result){
        loadingFinished();
        clConstraintLayout=findViewById(R.id.cl_translated_layout);
        clConstraintLayout.setVisibility(View.VISIBLE);
        tvTranslatedText=findViewById(R.id.tv_translated);
        tvTranslatedText.setText(result);
    }

    TextRecognizer getTextRecognizer(String languageCode){
        switch (languageCode){
            case "Japanese":{
                TextRecognizer recognizer =
                        TextRecognition.getClient(new JapaneseTextRecognizerOptions.Builder().build());
                return recognizer;
            }
            case "Chinese (Simplified)":
            case "Chinese (Traditional)":{
                TextRecognizer recognizer =
                        TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
                return recognizer;
            }

            case "Korean":{
                TextRecognizer recognizer =
                        TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());
                return recognizer;
            }

            case "Hindi":
            case "Nepali":
            case "Marathi": {
                TextRecognizer recognizer =
                        TextRecognition.getClient(new DevanagariTextRecognizerOptions.Builder().build());
                return recognizer;
            }

            default:{
                TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                return recognizer;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK){
            return;
        }

        if (requestCode == utils.GALLERY_PICK_CODE) {
            CropImage.activity(data.getData())
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == utils.CAMERA_PICK_CODE) {
            CropImage.activity(imageUri).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            tvPictureStatus.setVisibility(View.INVISIBLE);
            loading();
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                ivImage =findViewById(R.id.iv_image);
                ivImage.setImageURI(imageUri);
                ivImage.setVisibility(View.VISIBLE);
                recognizeText();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}