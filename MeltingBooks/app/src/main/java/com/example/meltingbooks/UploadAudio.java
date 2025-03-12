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
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UploadAudio extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CODE = 1001;
    private ImageButton btnRecord, btnAddFile, btnLike, btnHashtag, btnUpload;
    private EditText etInput;
    private LinearLayout bottomLayout;
    private boolean isKeyboardVisible = false;
    private ImageView imageView;
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private boolean isRecording = false;
    private StorageReference storageReference;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

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
        bottomLayout = findViewById(R.id.bottomLayout);
        btnRecord = findViewById(R.id.btnRecord);

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




        // 첨부 파일 선택
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

        btnLike.setOnClickListener(v -> {
            // 좋아요 기능 구현
        });

        btnHashtag.setOnClickListener(v -> {
            // 해시태그 기능 구현
        });

        btnRecord.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        });

        checkPermissions();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        }
    }

    private void startRecording() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }

        File audioFile = new File(getExternalFilesDir(null), generateFileName());
        audioFilePath = audioFile.getAbsolutePath();

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(audioFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            btnRecord.setImageResource(R.drawable.ic_mic);
            Toast.makeText(this, "녹음 시작", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("AudioRecorder", "녹음 시작 오류", e);
        }
    }

    private void stopRecording() {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            btnRecord.setImageResource(R.drawable.ic_mic);
            Toast.makeText(this, "녹음 완료", Toast.LENGTH_SHORT).show();

            uploadAudioToFirebase();
        } catch (Exception e) {
            Log.e("AudioRecorder", "녹음 정지 오류", e);
        }
    }

    private void uploadAudioToFirebase() {
        Uri fileUri = Uri.fromFile(new File(audioFilePath));
        StorageReference audioRef = storageReference.child(fileUri.getLastPathSegment());

        audioRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> audioRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Toast.makeText(UploadAudio.this, "업로드 완료", Toast.LENGTH_SHORT).show();
                    convertSpeechToText(uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(UploadAudio.this, "업로드 실패", Toast.LENGTH_SHORT).show());
    }

    private void convertSpeechToText(String audioUrl) {
        String dummyText = "이 책은 인간과 협력에 대해 다룬다.";
        etInput.setText(dummyText);
    }

    private String generateFileName() {
        return "audio_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".3gp";
    }

    private void toggleKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            // 키보드가 열려 있으면 숨긴다.
            hideKeyboard();
        } else {
            // 키보드가 열려 있지 않으면 키보드를 연다.
            etInput.requestFocus();
            imm.showSoftInput(etInput, InputMethodManager.SHOW_IMPLICIT);
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
                Toast.makeText(this, "권한을 허용해야 녹음이 가능합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
