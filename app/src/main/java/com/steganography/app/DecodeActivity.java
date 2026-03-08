package com.steganography.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.io.InputStream;

public class DecodeActivity extends AppCompatActivity {

    private ImageView ivSelectedImage;
    private LinearLayout layoutImagePlaceholder;
    private Button btnSelectImage, btnDecode;
    private ProgressBar progressBar;

    private Bitmap selectedBitmap;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    loadImage(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Decode Message");
        }

        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        layoutImagePlaceholder = findViewById(R.id.layoutImagePlaceholder);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnDecode = findViewById(R.id.btnDecode);
        progressBar = findViewById(R.id.progressBar);

        btnSelectImage.setOnClickListener(v -> openImagePicker());
        btnDecode.setOnClickListener(v -> startDecoding());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void loadImage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            selectedBitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) inputStream.close();

            ivSelectedImage.setImageBitmap(selectedBitmap);
            ivSelectedImage.setVisibility(View.VISIBLE);
            layoutImagePlaceholder.setVisibility(View.GONE);
            btnDecode.setEnabled(true);

        } catch (Exception e) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private void startDecoding() {
        if (selectedBitmap == null) {
            Toast.makeText(this, "Please select a stego image first", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnDecode.setEnabled(false);
        btnDecode.setText("Decoding...");

        new Thread(() -> {
            try {
                String extractedMessage = SteganographyEngine.decodeMessage(selectedBitmap);

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnDecode.setEnabled(true);
                    btnDecode.setText("Extract Message");

                    Intent intent = new Intent(DecodeActivity.this, ResultActivity.class);
                    intent.putExtra("mode", "decode");
                    intent.putExtra("message", extractedMessage);
                    ResultActivity.resultBitmap = selectedBitmap;
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnDecode.setEnabled(true);
                    btnDecode.setText("Extract Message");
                    Toast.makeText(DecodeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
