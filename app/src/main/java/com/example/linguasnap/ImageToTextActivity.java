package com.example.linguasnap;

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
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageToTextActivity extends AppCompatActivity {

    private TextView tvPictureStatus;
    private String currentImagePath=null;
    private Uri imageUri;
    private ImageView ivCamera;
    private ImageView ivImage;
    private ImageView ivImport;
    private TextView tvTranslatedText;
    private ConstraintLayout clConstraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_to_text);

        ivCamera =findViewById(R.id.iv_camera);
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
        /*spinner = findViewById(R.id.spinner);
        tv_text = findViewById(R.id.tv_text);*/

        InputImage image = null;
        try {
            image = InputImage.fromFilePath(this, imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }


//        TextRecognizer recognizer= getTextRecognizer(spinner.getSelectedItem().toString());
        /*
// When using Chinese script library
        TextRecognizer recognizer =
                TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());

// When using Devanagari script library
        TextRecognizer recognizer =
                TextRecognition.getClient(new DevanagariTextRecognizerOptions.Builder().build());

// When using Japanese script library
        TextRecognizer recognizer =
                TextRecognition.getClient(new JapaneseTextRecognizerOptions.Builder().build());

// When using Korean script library
        TextRecognizer recognizer =
                TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());*/

        TextRecognizer recognizer =
                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);


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
        clConstraintLayout=findViewById(R.id.cl_translated_layout);
        clConstraintLayout.setVisibility(View.VISIBLE);

        tvTranslatedText=findViewById(R.id.tv_translated);
        tvTranslatedText.setText(result);
    }

    /*TextRecognizer getTextRecognizer(String languageCode){
        switch (languageCode){
            case "Latin":{
                TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                return recognizer;
            }
            case "Japanese":{
                TextRecognizer recognizer =
                        TextRecognition.getClient(new JapaneseTextRecognizerOptions.Builder().build());
                return recognizer;
            }
            case "Chinese":{
                TextRecognizer recognizer =
                        TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
                return recognizer;
            }
            case "Korean":{
                TextRecognizer recognizer =
                        TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());
                return recognizer;
            }
            case "Devanagari ":{
                TextRecognizer recognizer =
                        TextRecognition.getClient(new DevanagariTextRecognizerOptions.Builder().build());
                return recognizer;
            }
        }
        return null;
    }*/

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