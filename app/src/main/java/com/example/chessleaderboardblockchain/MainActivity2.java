package com.example.chessleaderboardblockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {
    TextView txtTitre2;
    TextView txtTitre2petit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        txtTitre2 = findViewById(R.id.txtTitre2);
        txtTitre2petit = findViewById(R.id.txtTitre2petit);
        Intent x = getIntent();
        txtTitre2.setText("Tu es désormais connecté ! Bienvenue " + x.getStringExtra("id"));
        if (x.getStringExtra("ClefPrivee").length() > 10) {

        }else{
            txtTitre2petit.setText("Tu n'as pas de clé privée, tu ne peux pas jouer de parties privées");
        }
    }
}