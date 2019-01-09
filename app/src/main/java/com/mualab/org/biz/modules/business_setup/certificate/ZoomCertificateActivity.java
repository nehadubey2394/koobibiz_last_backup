package com.mualab.org.biz.modules.business_setup.certificate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.mualab.org.biz.R;
import com.squareup.picasso.Picasso;

public class ZoomCertificateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_certificate);

        String certificateImage = getIntent().getStringExtra("certificateImage");

        ImageView ivCertificate = findViewById(R.id.ivCertificate);
        ImageView btnBack = findViewById(R.id.btnBack);
        Picasso.with(ZoomCertificateActivity.this).load(certificateImage).
                priority(Picasso.Priority.HIGH).noPlaceholder().into(ivCertificate);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
