package com.mcgatletico.chessleaderboardblockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mcgatletico.chessleaderboardblockchain.R;

import java.util.Objects;

public class AjouterPartieActivity extends AppCompatActivity {
TextView editTextJoueur1;
    TextView editTextArbitre;
    TextView editTextJoueur2;
    TextView editTextTime;
    Button btnEnvoyerPartie;
    Button btnRetourAjouterPartie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_partie);
        editTextJoueur1 = findViewById(R.id.editTextJoueur1);
        editTextArbitre = findViewById(R.id.editTextArbitre);
        editTextJoueur2 = findViewById(R.id.editTextJoueur2);
        btnRetourAjouterPartie = findViewById(R.id.btnRetourAjouterPartie);
        editTextTime = findViewById(R.id.editTextTime);
        Button btnEnvoyerPartie = findViewById(R.id.btnEnvoyerPartie);
        Intent x = getIntent();
        System.out.println(x.getStringExtra("id"));
        System.out.println(x.getStringExtra("ClefPrivee"));
        System.out.println(x.getStringExtra("ClefPublique"));
        System.out.println(x.getStringExtra("id_player"));
        String serveur = "false";
        if (x.getStringExtra("serveur") != null){
            serveur = x.getStringExtra("serveur");
        }
        if (x.getStringExtra("Time") != null){
            System.out.println("Le time n'est pas null");
            editTextTime.setText(x.getStringExtra("Time"));
        }else{System.out.println("Le time est null");}


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
        btnRetourAjouterPartie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newintent = new Intent(AjouterPartieActivity.this,MainActivity2.class);
                newintent.putExtra("id", x.getStringExtra("id"));
                newintent.putExtra("ClefPrivee", x.getStringExtra("ClefPrivee"));
                newintent.putExtra("ClefPublique", x.getStringExtra("ClefPublique"));
                newintent.putExtra("serveur", x.getStringExtra("serveur"));
                startActivity(newintent);
            }
        });
        String finalServeur = serveur;
        btnEnvoyerPartie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // On envoie les données à la page suivante
                if (editTextJoueur1.getText().equals("") || editTextJoueur2.getText().equals("") || editTextArbitre.getText().equals("") || editTextTime.getText().equals(""))
                {
                    Toast.makeText(AjouterPartieActivity.this, "Veuillez remplir tous les champs (J1,J2,Arbitre)", Toast.LENGTH_SHORT).show();
                } else if (editTextJoueur1.getText().equals(editTextJoueur2.getText()) || editTextJoueur1.getText().equals(editTextArbitre.getText()) || editTextJoueur2.getText().equals(editTextArbitre.getText())) {
                    Toast.makeText(AjouterPartieActivity.this, "Les 3 champs (J1,J2,Arbitre) doivent être différent ! ", Toast.LENGTH_SHORT).show();
                } else{
                    try {
                        DatabaseHelper maBaseDeDonnees = DatabaseHelper.getInstance(AjouterPartieActivity.this);

                        SQLiteDatabase database = maBaseDeDonnees.getDatabase();
                        String [][] listeJoueurs = new String[3][3];


                        for (int c = 0;c<3;c++){
                            Cursor cursor = database.rawQuery("SELECT * FROM compte WHERE pseudo = '" + finalListeNomParticipants[c] + "'",null);
                            cursor.moveToFirst();
                            listeJoueurs[c][0] = cursor.getString(1);
                            listeJoueurs[c][1] = cursor.getString(2);

                        }
                       // Aura levé une exception avant si ça ne fonctionne pas :D
                        System.out.println("Timestamp: " + Timestamp.convertToTimestamp(editTextTime.getText().toString()));
                        System.out.println("Joueur 1 : "+listeJoueurs[0][0].toString()+" "+listeJoueurs[0][1].toString() + " Joueur 2 : "+listeJoueurs[1][0].toString()+" "+listeJoueurs[1][1].toString() +  " Arbitre : " + listeJoueurs[2][0].toString()+" "+listeJoueurs[2][1].toString());
                       String timestampPartie = Timestamp.convertToTimestamp(editTextTime.getText().toString());
                       String hashPartie = SHA2.encrypt(timestampPartie + "-" + listeJoueurs[0][1].toString() + "-" + listeJoueurs[1][1].toString() + "-" + listeJoueurs[2][1].toString());
                       if (finalServeur == "true" )
                       ThreadClient.envoyerPartie(timestampPartie,hashPartie,listeJoueurs[0][1].toString(),listeJoueurs[1][1].toString(),listeJoueurs[2][1].toString());
                        else if (finalServeur == "false")
                            ThreadClient.ajouterPartieARecevoir(maBaseDeDonnees,timestampPartie,hashPartie,listeJoueurs[0][1].toString(),listeJoueurs[1][1].toString(),listeJoueurs[2][1].toString());
                        Toast.makeText(AjouterPartieActivity.this, "Partie ajoutée" + (Objects.equals(finalServeur, "true") ?"Au serveur VPS":"En local, les demandes aux autres participants seront envoyés!  "), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(AjouterPartieActivity.this, "Erreur lors de l'ajout de la partie - Base de données inaccessible", Toast.LENGTH_SHORT).show();
                    }
                    }
            }
        });
        editTextJoueur1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newintent = new Intent(AjouterPartieActivity.this,SelectionPseudoActivity.class);
                newintent.putExtra("id_player",1); // 0 = arbitre  1 = joueur 1 ; 2 = joueur 2
                newintent.putExtra("id", x.getStringExtra("id"));
                newintent.putExtra("ClefPrivee", x.getStringExtra("ClefPrivee"));
                newintent.putExtra("ClefPublique", x.getStringExtra("ClefPublique"));
                newintent.putExtra("listeParticipants", finalListeNomParticipants);
                newintent.putExtra("Time", editTextTime.getText().toString());
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
                newintent.putExtra("Time", editTextTime.getText().toString());
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
                newintent.putExtra("Time", editTextTime.getText().toString());
                startActivity(newintent);
            }
        });
        editTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newintent = new Intent(AjouterPartieActivity.this,SelectionDateHeureActivity.class);
                newintent.putExtra("id", x.getStringExtra("id"));
                newintent.putExtra("ClefPrivee", x.getStringExtra("ClefPrivee"));
                newintent.putExtra("ClefPublique", x.getStringExtra("ClefPublique"));
                newintent.putExtra("listeParticipants", finalListeNomParticipants);
                newintent.putExtra("Time", editTextTime.getText().toString());
                startActivity(newintent);
            }
        });
    }


}