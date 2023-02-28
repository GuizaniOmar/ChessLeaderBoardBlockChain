package com.example.chessleaderboardblockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {
    TextView txtTitre2;
    TextView txtTitre2petit;
    Button btnAjouterPartie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btnAjouterPartie = findViewById(R.id.btnAjouterPartie);
        txtTitre2 = findViewById(R.id.txtTitre2);
        txtTitre2petit = findViewById(R.id.txtTitre2petit);
        Intent x = getIntent();
        txtTitre2.setText("Tu es désormais connecté ! Bienvenue " + x.getStringExtra("id"));
        if (x.getStringExtra("ClefPrivee").length() > 10) {
            txtTitre2petit.setText("Tu as une clé privée, tu peux jouer de parties privées");
            btnAjouterPartie.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity2.this, AjouterPartieActivity.class);
                intent.putExtra("id", x.getStringExtra("id"));
                intent.putExtra("ClefPrivee", x.getStringExtra("ClefPrivee"));
                intent.putExtra("ClefPublique", x.getStringExtra("ClefPublique"));
                startActivity(intent);
            });
        }
        else{
            txtTitre2petit.setText("Tu n'as pas de clé privée, tu ne peux pas jouer de parties privées");
        }
    }
}