package com.example.sjvirtual;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.SJVIRTUAL.tour.UnityPlayerActivity;

public class Tour extends AppCompatActivity {
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

                Intent i = new Intent(Tour.this, UnityPlayerActivity.class);

                startActivity(i);

    }
}


