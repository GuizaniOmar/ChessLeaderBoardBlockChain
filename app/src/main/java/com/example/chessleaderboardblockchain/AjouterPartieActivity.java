package com.example.chessleaderboardblockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AjouterPartieActivity extends AppCompatActivity {
TextView editTextJoueur1;
    TextView editTextArbitre;
    TextView editTextJoueur2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_partie);
        editTextJoueur1 = findViewById(R.id.editTextJoueur1);
        editTextArbitre = findViewById(R.id.editTextArbitre);
        editTextJoueur2 = findViewById(R.id.editTextJoueur2);
        Intent x = getIntent();
        System.out.println(x.getStringExtra("id"));
        System.out.println(x.getStringExtra("ClefPrivee"));
        System.out.println(x.getStringExtra("ClefPublique"));
        System.out.println(x.getStringExtra("id_player"));


        String[] listeNomParticipants = {"","",""};
        if (x.getStringArrayExtra("listeParticipants") != null){
            listeNomParticipants = x.getStringArrayExtra("listeParticipants");
        }
        System.out.println("listeNomParticipants[0] = "+listeNomParticipants[0]);
        editTextArbitre.setText(listeNomParticipants[0]);
        editTextJoueur1.setText(listeNomParticipants[1]);
        editTextJoueur2.setText(listeNomParticipants[2]);

        // On peut recevoir plusieurs types de données


        String[] finalListeNomParticipants = listeNomParticipants;
        editTextJoueur1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newintent = new Intent(AjouterPartieActivity.this,SelectionPseudoActivity.class);
                newintent.putExtra("id_player",1); // 0 = arbitre  1 = joueur 1 ; 2 = joueur 2
                newintent.putExtra("id", x.getStringExtra("id"));
                newintent.putExtra("ClefPrivee", x.getStringExtra("ClefPrivee"));
                newintent.putExtra("ClefPublique", x.getStringExtra("ClefPublique"));
                newintent.putExtra("listeParticipants", finalListeNomParticipants);
                startActivity(newintent);
            }
        });
        editTextJoueur2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newintent = new Intent(AjouterPartieActivity.this,SelectionPseudoActivity.class);
                newintent.putExtra("id_player",2); // 0 = arbitre  1 = joueur 1 ; 2 = joueur 2
                newintent.putExtra("id", x.getStringExtra("id"));
                newintent.putExtra("ClefPrivee", x.getStringExtra("ClefPrivee"));
                newintent.putExtra("ClefPublique", x.getStringExtra("ClefPublique"));
                newintent.putExtra("listeParticipants", finalListeNomParticipants);
                startActivity(newintent);
            }
        });
        editTextArbitre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newintent = new Intent(AjouterPartieActivity.this,SelectionPseudoActivity.class);
                newintent.putExtra("id_player",0); // 0 = arbitre  1 = joueur 1 ; 2 = joueur 2
                newintent.putExtra("id", x.getStringExtra("id"));
                newintent.putExtra("ClefPrivee", x.getStringExtra("ClefPrivee"));
                newintent.putExtra("ClefPublique", x.getStringExtra("ClefPublique"));
                newintent.putExtra("listeParticipants", finalListeNomParticipants);
                startActivity(newintent);
            }
        });

    }


}