package com.example.chessleaderboardblockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ListePartiesASignerActivity extends AppCompatActivity {
    SQLiteDatabase database;
    ListView listViewListePartiesASigner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_parties_asigner);
        listViewListePartiesASigner = findViewById(R.id.listViewListePartiesASigner);


        DatabaseHelper maBaseDeDonnees = DatabaseHelper.getInstance(ListePartiesASignerActivity.this);
        database = maBaseDeDonnees.getDatabase();

        database.execSQL("DROP TABLE IF EXISTS 'PARTIES'");
        database.execSQL("CREATE TABLE IF NOT EXISTS 'PARTIES' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, Timestamp TEXT NOT NULL , HashPartie TEXT NOT NULL, ClefPubliqueJ1 TEXT NOT NULL, ClefPubliqueJ2 TEXT NOT NULL, ClefPubliqueArbitre TEXT NOT NULL CHECK (ClefPubliqueJ1 <> ClefPubliqueJ2 AND ClefPubliqueJ1 <> ClefPubliqueArbitre AND ClefPubliqueJ2 <> ClefPubliqueArbitre), VoteJ1 TEXT, VoteJ2 TEXT, VoteArbitre TEXT )");

        // On actualise ici !!!! Avec un thread avec le temps
        System.out.println("On actualise la Bdd avec Liste Parties ");
        ThreadClient.recevoirListeParties(maBaseDeDonnees);

        try{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Cursor c = database.rawQuery("SELECT * FROM PARTIES",null);

                    String [] from = {"Timestamp","HashPartie","ClefPubliqueJ1"};
                    //   String [] from = {"Timestamp","HashPartie","ClefPubliqueJ1","ClefPubliqueJ2","ClefPubliqueArbitre","VoteJ1","VoteJ2","VoteArbitre"};

                    int [] to = {R.id.affichagePseudo,R.id.affichageClefPublique,R.id.affichageClefPrivee};
                    SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(ListePartiesASignerActivity.this, R.layout.element, c, from, to, 0);
                    listViewListePartiesASigner.setAdapter(adapter2);

                }
            }, 4000);

        }catch(Exception e){}







    }
}