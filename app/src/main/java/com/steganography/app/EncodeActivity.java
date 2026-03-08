package com.steganography.app;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.io.InputStream;
import java.io.OutputStream;

public class EncodeActivity extends AppCompatActivity {

    private ImageView ivSelectedImage;
    private LinearLayout layoutImagePlaceholder;
    private TextInputEditText etSecretMessage;
    private TextInputLayout tilMessage;
    private Button btnSelectImage, btnEncode;
    private TextView tvCapacity, tvCharCount;
    private ProgressBar progressBar;

    private Bitmap selectedBitmap;
    private int maxCapacity = 0;

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
        setContentView(R.layout.activity_encode);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Encode Message");
        }

        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        layoutImagePlaceholder = findViewById(R.id.layoutImagePlaceholder);
        etSecretMessage = findViewById(R.id.etSecretMessage);
        tilMessage = findViewById(R.id.tilMessage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnEncode = findViewById(R.id.btnEncode);
        tvCapacity = findViewById(R.id.tvCapacity);
        tvCharCount = findViewById(R.id.tvCharCount);
        progressBar = findViewById(R.id.progressBar);

        btnSelectImage.setOnClickListener(v -> openImagePicker());
        btnEncode.setOnClickListener(v -> startEncoding());

        etSecretMessage.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCharCount(s.length());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
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

            maxCapacity = SteganographyEngine.getMaxCapacity(selectedBitmap);
            tvCapacity.setText("Max capacity: " + maxCapacity + " characters");
            tvCapacity.setVisibility(View.VISIBLE);

            updateCharCount(etSecretMessage.getText() != null ? etSecretMessage.getText().length() : 0);
            btnEncode.setEnabled(true);

        } catch (Exception e) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCharCount(int count) {
        if (maxCapacity > 0) {
            tvCharCount.setText(count + " / " + maxCapacity);
            tvCharCount.setVisibility(View.VISIBLE);
            if (count > maxCapacity) {
                tvCharCount.setTextColor(getResources().getColor(R.color.error_color, null));
            } else {
                tvCharCount.setTextColor(getResources().getColor(R.color.accent_cyan, null));
            }
        }
    }

    private void startEncoding() {
        if (selectedBitmap == null) {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        String message = etSecretMessage.getText() != null ? etSecretMessage.getText().toString().trim() : "";
        if (message.isEmpty()) {
            tilMessage.setError("Please enter a secret message");
            return;
        }
        tilMessage.setError(null);

        if (message.length() > maxCapacity) {
            tilMessage.setError("Message too long! Max: " + maxCapacity + " characters");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnEncode.setEnabled(false);
        btnEncode.setText("Encoding...");

        new Thread(() -> {
            try {
                Bitmap stegoBitmap = SteganographyEngine.encodeMessage(selectedBitmap, message);

                // Save to gallery
                String fileName = "Stego_" + System.currentTimeMillis() + ".png";
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/SteganographyApp");
                }

                Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                OutputStream outputStream = getContentResolver().openOutputStream(imageUri);
                stegoBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                if (outputStream != null) outputStream.close();

                final Bitmap finalBitmap = stegoBitmap;
                final String savedPath = fileName;
                final String savedMessage = message;

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnEncode.setEnabled(true);
                    btnEncode.setText("Encode & Hide");

                    Intent intent = new Intent(EncodeActivity.this, ResultActivity.class);
                    intent.putExtra("mode", "encode");
                    intent.putExtra("message", savedMessage);
                    intent.putExtra("filename", savedPath);
                    ResultActivity.resultBitmap = finalBitmap;
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnEncode.setEnabled(true);
                    btnEncode.setText("Encode & Hide");
                    Toast.makeText(EncodeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
