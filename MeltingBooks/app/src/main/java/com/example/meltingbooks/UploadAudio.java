package com.example.meltingbooks;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;  // 추가된 import
import android.widget.Toast;

import android.speech.SpeechRecognizer;
import android.speech.RecognizerIntent;
import android.speech.RecognitionListener;

import java.util.ArrayList;
import java.util.Locale;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.view.Gravity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UploadAudio extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CODE = 1001;
    private ImageButton btnRecord, btnAddFile, btnLike, btnHashtag, btnUpload;
    private EditText etInput;
    private LinearLayout middleLayout, bottomLayout;
    private boolean isKeyboardVisible = false;
    private ImageView imageView;
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private boolean isRecording = false;
    private StorageReference storageReference;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        storageReference = FirebaseStorage.getInstance().getReference("audio");

        btnUpload = findViewById(R.id.btnUpload);
        btnAddFile = findViewById(R.id.btnAddFile);
        btnLike = findViewById(R.id.btnLike);
        btnHashtag = findViewById(R.id.btnHashtag);
        imageView = findViewById(R.id.imageView);
        etInput = findViewById(R.id.etInput);
        middleLayout = findViewById(R.id.middleLayout);
        bottomLayout = findViewById(R.id.bottomLayout);
        btnRecord = findViewById(R.id.btnRecord);

        // editText 기능
        etInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isKeyboardVisible) {
                    // 키보드가 보이면 숨김
                    hideKeyboard();
                } else {
                    // 키보드가 숨겨져 있으면 보임
                    etInput.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(etInput, InputMethodManager.SHOW_IMPLICIT);
                }
                isKeyboardVisible = !isKeyboardVisible; // 키보드 상태 반전
            }
        });

        // 첨부 파일 추가
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        imageView.setImageURI(imageUri);
                    }
                });

        btnAddFile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            imagePickerLauncher.launch(intent);
        });

        checkPermissions();

        btnLike.setOnClickListener(v -> {
            // 좋아요 기능 구현
        });

        btnHashtag.setOnClickListener(v -> {
            // 해시태그 기능 구현
        });

        // btnRecord 클릭 리스너에서 micON 이미지 뷰를 표시
        btnRecord.setOnClickListener(v -> {
            // micON 뷰를 찾고 visibility를 VISIBLE로 변경
            ImageView micImageView = findViewById(R.id.micON);
            if (micImageView != null) {
                micImageView.setVisibility(View.VISIBLE);  // micON 이미지 뷰를 보이도록 설정
            }
            // EditText의 hint 숨기기
            etInput.setHint("");  // hint를 빈 문자열로 설정

            // 음성 인식 시작
            speechRecognizer.startListening(speechRecognizerIntent);
        });

        // 음성 인식 초기화
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                // 음성 인식 시작 전
                showSpeechRecognitionUI();  // UI 표시
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float v) {
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
            }

            @Override
            public void onEndOfSpeech() {
                // 음성 인식 종료 후 hint 복원
            }

            @Override
            public void onError(int error) {
                Toast.makeText(UploadAudio.this, "음성 인식 오류 발생", Toast.LENGTH_SHORT).show();
                hideSpeechRecognitionUI();  // UI 숨김
                // 음성 인식 종료 후 hint 복원
                etInput.setHint("독서 후 느낌을 공유해 보세요...");  // 기본 hint로 설정
            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    etInput.setText(matches.get(0)); // 인식된 첫 번째 텍스트를 etInput에 설정
                }
                hideSpeechRecognitionUI();  // 음성 인식 후 UI 숨김

            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
            }
        });

        checkPermissions();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etInput.getWindowToken(), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "권한 허용 완료", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "권한을 허용해야 음성 인식이 가능합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showSpeechRecognitionUI() {
        // 음성 인식 중 이미지 보이기
        ImageView micImageView = findViewById(R.id.micON);
        if (micImageView != null) {
            micImageView.setVisibility(View.VISIBLE);  // micON 이미지를 보이도록 설정
        }
    }

    // 음성 인식 종료 후 UI 숨김
    private void hideSpeechRecognitionUI() {
        // micON을 찾아서 숨김
        ImageView micImageView = findViewById(R.id.micON);
        if (micImageView != null) {
            micImageView.setVisibility(View.GONE);  // micON 이미지 뷰를 숨김
        }
    }
}
