package com.mcgatletico.chessleaderboardblockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;


public class DetailPartieActivity extends AppCompatActivity {
    Switch switchDetailPartie;
    Button btnDetailPartieConfirmerPartie;
    Button btnDetailPartiePlaintePartie;
    Button btnDetailPartieRetour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_partie);
        switchDetailPartie = findViewById(R.id.switchDetailPartie);
        btnDetailPartieConfirmerPartie = findViewById(R.id.btnDetailPartieConfirmerPartie);
        btnDetailPartiePlaintePartie = findViewById(R.id.btnDetailPartiePlaintePartie);
        btnDetailPartieRetour = findViewById(R.id.btnDetailPartieRetour);
        DatabaseHelper maBaseDeDonnees = DatabaseHelper.getInstance(DetailPartieActivity.this);
        SQLiteDatabase database = maBaseDeDonnees.getDatabase();

        Intent x = getIntent();
        String id = x.getStringExtra("id");
        String serveur = x.getStringExtra("serveur");
        String ClefPrivee = x.getStringExtra("ClefPrivee");
        String ClefPublique = x.getStringExtra("ClefPublique");
        String hashPartie = x.getStringExtra("hashPartie");
     btnDetailPartieRetour.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             Intent newintent = new Intent(DetailPartieActivity.this,ListePartieActivity.class);
             newintent.putExtra("id",id);
             newintent.putExtra("ClefPrivee",ClefPrivee);
             newintent.putExtra("ClefPublique",ClefPublique);
             newintent.putExtra("serveur",serveur);
             startActivity(newintent);
         }
     });
        btnDetailPartieConfirmerPartie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailPartieActivity.this);
                builder.setMessage("Êtes-vous sûr de vouloir confirmer la partie ?")
                        .setTitle("Confirmation de partie ")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String hashVote = hashPartie + "-O";
                                try {
                                    String signaturehashVote = RSAPSS.encode(hashVote,RSAPSS.privateKeyFromString(ClefPrivee));
                                    Toast.makeText(DetailPartieActivity.this, ThreadClient.ajouterConfirmation(maBaseDeDonnees,hashPartie,hashVote,ClefPublique,signaturehashVote), Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(DetailPartieActivity.this, "Erreur lors de la signature ! ClefPrivée incorrecte ???", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(DetailPartieActivity.this, "Vote annulé", Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btnDetailPartiePlaintePartie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
AlertDialog.Builder builder = new AlertDialog.Builder(DetailPartieActivity.this);
                builder.setMessage("Êtes-vous sûr de vouloir déposer une plainte ?")
                        .setTitle("Dépôt de plainte ")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String hashVote = hashPartie + "-N";
                                try {
                                    String signaturehashVote = RSAPSS.encode(hashVote,RSAPSS.privateKeyFromString(ClefPrivee));
                                    Toast.makeText(DetailPartieActivity.this, ThreadClient.ajouterPlainte(maBaseDeDonnees,hashPartie,hashVote,ClefPublique,signaturehashVote), Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(DetailPartieActivity.this, "Erreur lors de la signature ! ClefPrivée incorrecte ???", Toast.LENGTH_SHORT).show();
                                }

                                 }
                        })
                        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(DetailPartieActivity.this, "Plainte annulée", Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        switchDetailPartie.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    btnDetailPartieConfirmerPartie.setEnabled(true);
                    btnDetailPartiePlaintePartie.setEnabled(true);
                } else {
                    btnDetailPartieConfirmerPartie.setEnabled(false);
                    btnDetailPartiePlaintePartie.setEnabled(false);
                }
            }
        });
    }
}