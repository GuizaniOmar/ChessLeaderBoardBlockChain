package com.mcgatletico.chessleaderboardblockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mcgatletico.chessleaderboardblockchain.R;

public class MainActivity2 extends AppCompatActivity {
    TextView txtTitre2;
    TextView txtTitre2petit;
    Button btnAjouterPartie;
    Button btnAfficherParties;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btnAjouterPartie = findViewById(R.id.btnAjouterPartie);
        btnAfficherParties = findViewById(R.id.btnAfficherParties);
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

            btnAfficherParties.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity2.this, ListePartiesASignerActivity.class);
                    intent.putExtra("id", x.getStringExtra("id"));
                    intent.putExtra("ClefPrivee", x.getStringExtra("ClefPrivee"));
                    intent.putExtra("ClefPublique", x.getStringExtra("ClefPublique"));
                    startActivity(intent);
                }
            });
        }
        else{

            btnAjouterPartie.setEnabled(false);
            btnAfficherParties.setEnabled(false);
            txtTitre2petit.setText("Tu n'as pas de clé privée, tu ne peux pas jouer de parties privées");
        }
    }
}