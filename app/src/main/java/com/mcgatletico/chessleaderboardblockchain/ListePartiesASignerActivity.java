package com.mcgatletico.chessleaderboardblockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.mcgatletico.chessleaderboardblockchain.R;

public class ListePartiesASignerActivity extends AppCompatActivity {
    SQLiteDatabase database;
    Button btnActualiserListePartiesASigner;
    ListView listViewListePartiesASigner;
    RadioButton radioButtonTout;

    Button btnRetourListePartiesASigner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_parties_asigner);
        radioButtonTout = findViewById(R.id.radioButtonTout);
        btnRetourListePartiesASigner = findViewById(R.id.btnRetourListePartiesASigner);
        listViewListePartiesASigner = findViewById(R.id.listViewListePartiesASigner);
        btnActualiserListePartiesASigner= findViewById(R.id.btnActualiserListePartiesASigner);
        Intent x = getIntent();
       String id = x.getStringExtra("id");
       String serveur = x.getStringExtra("serveur");
        String ClefPrivee = x.getStringExtra("ClefPrivee");
        String ClefPublique = x.getStringExtra("ClefPublique");

btnRetourListePartiesASigner.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent newintent = new Intent(ListePartiesASignerActivity.this,MainActivity2.class);
        newintent.putExtra("id",id);
        newintent.putExtra("ClefPrivee",ClefPrivee);
        newintent.putExtra("ClefPublique",ClefPublique);
        newintent.putExtra("serveur",serveur);
        startActivity(newintent);
    }
});
       if (serveur.equals("true")) {
            // méthode à modifier, peut-être créer une autre Table (temporaire pour la partie)
           btnActualiserListePartiesASigner.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   DatabaseHelper maBaseDeDonnees = DatabaseHelper.getInstance(ListePartiesASignerActivity.this);
                   database = maBaseDeDonnees.getDatabase();


                   database.execSQL("DROP TABLE IF EXISTS 'PARTIES'");
                   database.execSQL("CREATE TABLE IF NOT EXISTS 'PARTIES' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, Timestamp TEXT NOT NULL , HashPartie TEXT NOT NULL, ClefPubliqueJ1 TEXT NOT NULL, ClefPubliqueJ2 TEXT NOT NULL, ClefPubliqueArbitre TEXT NOT NULL CHECK (ClefPubliqueJ1 <> ClefPubliqueJ2 AND ClefPubliqueJ1 <> ClefPubliqueArbitre AND ClefPubliqueJ2 <> ClefPubliqueArbitre), VoteJ1 TEXT, VoteJ2 TEXT, VoteArbitre TEXT, SignatureJ1 TEXT,SignatureJ2 TEXT,SignatureArbitre TEXT )");

                   // On actualise ici !!!! Avec un thread avec le temps
                   System.out.println("On actualise la Bdd avec Liste Parties ");
                   ThreadClient.recevoirListeParties(maBaseDeDonnees);

                   try {
                       new Handler().postDelayed(new Runnable() {
                           @Override
                           public void run() {
                               String contenueFiltreSql = "";

                               if (radioButtonTout.isChecked()) {
                                   contenueFiltreSql = " WHERE ClefPubliqueJ1 = '" + ClefPublique + "' OR ClefPubliqueJ2 = '" + ClefPublique + "' OR ClefPubliqueArbitre = '" + ClefPublique + "'";
                               }
                               //Cursor c = database.rawQuery("SELECT * FROM PARTIES" + contenueFiltreSql,null);
                               Cursor c = database.rawQuery("SELECT PARTIES._id, strftime('%d-%m-%Y %H:%M', datetime(PARTIES.Timestamp/1000, 'unixepoch')) as dateDuMatch, PARTIES.HashPartie, \n" +
                                       "COMPTES1.Pseudo AS Joueur1, " +
                                       "COMPTES2.Pseudo AS Joueur2, " +
                                       "COMPTES3.Pseudo AS Arbitre " +
                                       "FROM PARTIES " +
                                       "LEFT JOIN COMPTES AS COMPTES1 ON PARTIES.ClefPubliqueJ1 = COMPTES1.ClefPublique " +
                                       "LEFT JOIN COMPTES AS COMPTES2 ON PARTIES.ClefPubliqueJ2 = COMPTES2.ClefPublique " +
                                       "LEFT JOIN COMPTES AS COMPTES3 ON PARTIES.ClefPubliqueArbitre = COMPTES3.ClefPublique ", null);

                               String[] from = {"HashPartie", "Joueur1", "Joueur2", "dateDuMatch"};
                               //   String [] from = {"Timestamp","HashPartie","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"};

                               int[] to = {R.id.textView18, R.id.textView19, R.id.textView20, R.id.textView21};
                               SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(ListePartiesASignerActivity.this, R.layout.elementlistepartiesasigner, c, from, to, 0);
                               listViewListePartiesASigner.setAdapter(adapter2);

                           }
                       }, 3000);

                   } catch (Exception e) {
                   }

                   listViewListePartiesASigner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                       @Override
                       public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                           Cursor c = (Cursor) listViewListePartiesASigner.getItemAtPosition(i);
                           int id = c.getColumnIndex("HashPartie");
                           String HashPartie = c.getString(id);
                           Intent intent = new Intent(ListePartiesASignerActivity.this, SignerPartieActivity.class);
                           intent.putExtra("HashPartie", HashPartie);
                           intent.putExtra("ClefPrivee", ClefPrivee);
                           intent.putExtra("ClefPublique", ClefPublique);
                           intent.putExtra("serveur", serveur);
                           startActivity(intent);
                       }
                   });

               }
           });

       }else{
           btnActualiserListePartiesASigner.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   DatabaseHelper maBaseDeDonnees = DatabaseHelper.getInstance(ListePartiesASignerActivity.this);
                   database = maBaseDeDonnees.getDatabase();



                   // On actualise ici !!!! Avec un thread avec le temps
                   Toast.makeText(ListePartiesASignerActivity.this, "Peer-to-Peer Affichage des parties A RECEVOIR pour le moment", Toast.LENGTH_SHORT).show();
                // Pas besoin car présence en local !    ThreadClient.recevoirListeParties(maBaseDeDonnees);

                   try {
                       new Handler().postDelayed(new Runnable() {
                           @Override
                           public void run() {
                               String contenueFiltreSql = "";

                               if (radioButtonTout.isChecked()) {
                                   contenueFiltreSql = " WHERE clefPubliqueJ1 = '" + ClefPublique + "' OR clefPubliqueJ2 = '" + ClefPublique + "' OR clefPubliqueArbitre = '" + ClefPublique + "'";
                               }

                               Cursor c = database.rawQuery("SELECT partieARecevoir._id, strftime('%d-%m-%Y %H:%M', datetime(partieARecevoir.timestamp/1000, 'unixepoch')) as dateDuMatch, partieARecevoir.hashPartie, " +
                                       "compte1.pseudo AS joueur1, " +
                                       "compte2.pseudo AS joueur2, " +
                                       "compte3.pseudo AS arbitre " +
                                       "FROM partieARecevoir " +
                                       "LEFT JOIN compte AS compte1 ON partieARecevoir.clefPubliqueJ1 = compte1.clefPublique " +
                                       "LEFT JOIN compte AS compte2 ON partieARecevoir.clefPubliqueJ2 = compte2.clefPublique " +
                                       "LEFT JOIN compte AS compte3 ON partieARecevoir.clefPubliqueArbitre = compte3.clefPublique ORDER BY CAST(partieARecevoir.timestamp AS DATETIME) ASC", null);







                               String[] from = {"hashPartie", "joueur1", "joueur2", "dateDuMatch","arbitre"};

                               int[] to = {R.id.textViewListePartieHash, R.id.textViewListePartieJoueur1, R.id.textViewListePartieJoueur2, R.id.textViewListePartieDate,R.id.textViewListePartieArbitre};
                               SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(ListePartiesASignerActivity.this, R.layout.listepartie, c, from, to, 0);
                               listViewListePartiesASigner.setAdapter(adapter2);
                               System.out.println(listViewListePartiesASigner.getCount());
                               System.out.println("PartieARecevoir - 667#2");

                           }
                       }, 3000);

                   } catch (Exception e) {
                   }

                   listViewListePartiesASigner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                       @Override
                       public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                           Cursor c = (Cursor) listViewListePartiesASigner.getItemAtPosition(i);
                           int id = c.getColumnIndex("hashPartie");
                           String HashPartie = c.getString(id);
                           Toast.makeText(ListePartiesASignerActivity.this, "Peer-to-Peer hashPartie " + HashPartie, Toast.LENGTH_SHORT).show();
                           Intent intent = new Intent(ListePartiesASignerActivity.this, SignerPartieActivity.class);
                           intent.putExtra("HashPartie", HashPartie);
                           intent.putExtra("ClefPrivee", ClefPrivee);
                           intent.putExtra("ClefPublique", ClefPublique);
                           intent.putExtra("serveur", serveur);
                           startActivity(intent);
                       }
                   });

               }
           });


       }





    }
}