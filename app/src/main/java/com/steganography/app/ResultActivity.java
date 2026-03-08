package com.steganography.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

public class ResultActivity extends AppCompatActivity {

    // Static bitmap to avoid Parcelable size limits
    public static Bitmap resultBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        String mode = getIntent().getStringExtra("mode");
        String message = getIntent().getStringExtra("message");
        String filename = getIntent().getStringExtra("filename");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mode.equals("encode") ? "Encoding Complete" : "Message Extracted");
        }

        ImageView ivResultImage = findViewById(R.id.ivResultImage);
        TextView tvResultTitle = findViewById(R.id.tvResultTitle);
        TextView tvResultSubtitle = findViewById(R.id.tvResultSubtitle);
        TextView tvMessage = findViewById(R.id.tvMessage);
        TextView tvMessageLabel = findViewById(R.id.tvMessageLabel);
        LinearLayout layoutFilename = findViewById(R.id.layoutFilename);
        TextView tvFilename = findViewById(R.id.tvFilename);
        MaterialButton btnCopy = findViewById(R.id.btnCopy);
        MaterialButton btnDone = findViewById(R.id.btnDone);
        Chip chipStatus = findViewById(R.id.chipStatus);

        if (resultBitmap != null) {
            ivResultImage.setImageBitmap(resultBitmap);
        }

        if ("encode".equals(mode)) {
            tvResultTitle.setText("Message Hidden Successfully!");
            tvResultSubtitle.setText("Your secret message has been embedded into the image using LSB steganography.");
            tvMessageLabel.setText("Hidden Message:");
            chipStatus.setText("ENCODED");
            chipStatus.setChipBackgroundColorResource(R.color.chip_encode_bg);

            if (filename != null) {
                layoutFilename.setVisibility(View.VISIBLE);
                tvFilename.setText(filename);
            }
        } else {
            tvResultTitle.setText("Message Extracted!");
            tvResultSubtitle.setText("The hidden message has been successfully decoded from the stego image.");
            tvMessageLabel.setText("Secret Message:");
            chipStatus.setText("DECODED");
            chipStatus.setChipBackgroundColorResource(R.color.chip_decode_bg);
            layoutFilename.setVisibility(View.GONE);
        }

        tvMessage.setText(message);

        btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Secret Message", message);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Message copied to clipboard!", Toast.LENGTH_SHORT).show();
        });

        btnDone.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resultBitmap = null;
    }
}
