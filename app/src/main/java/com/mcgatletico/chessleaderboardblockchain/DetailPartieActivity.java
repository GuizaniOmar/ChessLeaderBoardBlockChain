package com.mcgatletico.chessleaderboardblockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class DetailPartieActivity extends AppCompatActivity {
    Switch switchDetailPartie;
    Button btnDetailPartieConfirmerPartie;
    Button btnDetailPartiePlaintePartie;
    Button btnDetailPartieRetour;
    TextView textViewDetailPartieDate;
    TextView textViewDetailPartiePseudoJ1;
    TextView textViewDetailPartieEloJ1;
    TextView textViewDetailPartiePseudoJ2;
    TextView textViewDetailPartieEloJ2;
    TextView textViewDetailPartiePseudoArbitre;
    TextView textViewDetailPartieFiabiliteArbitre;
    TextView textViewDetailPartieResultat;

    @Override
    @SuppressLint("Range")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_partie);
        switchDetailPartie = findViewById(R.id.switchDetailPartie);
        btnDetailPartieConfirmerPartie = findViewById(R.id.btnDetailPartieConfirmerPartie);
        btnDetailPartiePlaintePartie = findViewById(R.id.btnDetailPartiePlaintePartie);
        btnDetailPartieRetour = findViewById(R.id.btnDetailPartieRetour);
        textViewDetailPartieDate = findViewById(R.id.textViewDetailPartieDate);
        textViewDetailPartiePseudoJ1 = findViewById(R.id.textViewDetailPartiePseudoJ1);
        textViewDetailPartieEloJ1 = findViewById(R.id.textViewDetailPartieEloJ1);
        textViewDetailPartiePseudoJ2 = findViewById(R.id.textViewDetailPartiePseudoJ2);
        textViewDetailPartieEloJ2 = findViewById(R.id.textViewDetailPartieEloJ2);
        textViewDetailPartiePseudoArbitre = findViewById(R.id.textViewDetailPartiePseudoArbitre);
        textViewDetailPartieFiabiliteArbitre = findViewById(R.id.textViewDetailPartieFiabiliteArbitre);
        textViewDetailPartieResultat = findViewById(R.id.textViewDetailPartieResultat);
        DatabaseHelper maBaseDeDonnees = DatabaseHelper.getInstance(DetailPartieActivity.this);
        SQLiteDatabase database = maBaseDeDonnees.getDatabase();

        Intent x = getIntent();
        String id = x.getStringExtra("id");
        String serveur = x.getStringExtra("serveur");
        String ClefPrivee = x.getStringExtra("ClefPrivee");
        String ClefPublique = x.getStringExtra("ClefPublique");
        String hashPartie = x.getStringExtra("hashPartie");

        System.out.println("hashPartie " + hashPartie);
        Toast.makeText(DetailPartieActivity.this, "hashPartie " + hashPartie, Toast.LENGTH_LONG).show();

        Cursor c = database.rawQuery("SELECT partie._id,partie.clefPubliqueJ1,partie.clefPubliqueJ2,partie.clefPubliqueArbitre,partie.timestamp, strftime('%d-%m-%Y %H:%M', datetime(partie.timestamp/1000, 'unixepoch')) as dateDuMatch, partie.hashPartie ," +
                "compte1.pseudo AS joueur1, " +
                "compte2.pseudo AS joueur2, " +
                "compte3.pseudo AS arbitre, " +
                "partieDetail1.hashPartie AS hashPartie2, " +
                "partieDetail1.resultat AS resultat, " +
                "partieDetail1.eloJ1 AS eloJ1, " +
                "partieDetail1.eloJ2 AS eloJ2, " +
                "partieDetail1.fiabiliteArbitre AS fiabiliteArbitre " +
                "FROM partie " +
                "LEFT JOIN partieDetail as partieDetail1 ON partie.hashPartie = partieDetail1.hashPartie " +
                "LEFT JOIN compte AS compte1 ON partie.clefPubliqueJ1 = compte1.clefPublique " +
                "LEFT JOIN compte AS compte2 ON partie.clefPubliqueJ2 = compte2.clefPublique " +
                "LEFT JOIN compte AS compte3 ON partie.clefPubliqueArbitre = compte3.clefPublique WHERE partie.hashPartie = '" + hashPartie + "'" ,null);
      //WHERE partie.hashPartie = partieDetail.hashPartie
        if (c.moveToNext()) {
String resultat = c.getString(c.getColumnIndex("resultat"));
            String eloJ1 = c.getString(c.getColumnIndex("eloJ1"));
            String eloJ2 = c.getString(c.getColumnIndex("eloJ2"));
            String fiabiliteArbitre = c.getString(c.getColumnIndex("fiabiliteArbitre"));
            String dateDuMatch = c.getString(c.getColumnIndex("dateDuMatch"));
            String joueur1 = c.getString(c.getColumnIndex("joueur1"));
            String joueur2 = c.getString(c.getColumnIndex("joueur2"));
            String arbitre = c.getString(c.getColumnIndex("arbitre"));
            String hashPartieDetail = c.getString(c.getColumnIndex("hashPartie2"));
            String hashPartieOriginal = c.getString(c.getColumnIndex("hashPartie"));
            textViewDetailPartieDate.setText(dateDuMatch);
            textViewDetailPartiePseudoJ1.setText(joueur1);
            textViewDetailPartieEloJ1.setText(eloJ1);
            textViewDetailPartiePseudoJ2.setText(joueur2);
            textViewDetailPartieEloJ2.setText(eloJ2);
            textViewDetailPartiePseudoArbitre.setText(arbitre);
            textViewDetailPartieFiabiliteArbitre.setText(fiabiliteArbitre);
            switch (resultat) {
                case "V":
                    textViewDetailPartieResultat.setText("Victoire de " + joueur1 );
                    break;
                case "N":
                    textViewDetailPartieResultat.setText("Match nul");
                    break;
                case "D":
                    textViewDetailPartieResultat.setText("Victoire de " + joueur2);
                    break;
                default:
                   textViewDetailPartieResultat.setText("Erreur de résultat");
                    break;
            }
        }
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