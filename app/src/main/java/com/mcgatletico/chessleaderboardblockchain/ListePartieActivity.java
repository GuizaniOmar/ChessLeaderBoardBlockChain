package com.mcgatletico.chessleaderboardblockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ListePartieActivity extends AppCompatActivity {
Button btnRetour;
Button btnAfficherListePartie;
CheckBox checkBoxFiltrePseudoPartie;
EditText editTextFiltrePseudoPartie;
ListView listViewPartie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_partie);
        Intent x = getIntent();
        String id = x.getStringExtra("id");
        String serveur = x.getStringExtra("serveur");
        String ClefPrivee = x.getStringExtra("ClefPrivee");
        String ClefPublique = x.getStringExtra("ClefPublique");
        btnRetour = findViewById(R.id.btnRetourListePartie);
        btnAfficherListePartie = findViewById(R.id.btnAfficherListePartie);
        checkBoxFiltrePseudoPartie = findViewById(R.id.checkBoxFiltrePseudoPartie);
        editTextFiltrePseudoPartie = findViewById(R.id.editTextFiltrePseudoPartie);
        listViewPartie = findViewById(R.id.listViewPartie);
        DatabaseHelper maBaseDeDonnees = DatabaseHelper.getInstance(ListePartieActivity.this);
        SQLiteDatabase database = maBaseDeDonnees.getDatabase();




        btnAfficherListePartie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contenueFiltreSql = "";

                if (checkBoxFiltrePseudoPartie.isChecked()) {
                    String filtrePseudo = editTextFiltrePseudoPartie.getText().toString();
                    contenueFiltreSql = " WHERE joueur1 = '" + filtrePseudo + "' OR joueur2 = '" + filtrePseudo + "' OR arbitre = '" + filtrePseudo + "'";
                }
                //Cursor c = database.rawQuery("SELECT * FROM PARTIES" + contenueFiltreSql,null);
                Cursor c = database.rawQuery("SELECT partie._id, strftime('%d-%m-%Y %H:%M', datetime(partie.timestamp/1000, 'unixepoch')) as dateDuMatch, partie.hashPartie, " +
                        "compte1.Pseudo AS joueur1, " +
                        "compte2.Pseudo AS joueur2, " +
                        "compte3.Pseudo AS arbitre " +
                        "FROM partie " +
                        "LEFT JOIN compte AS compte1 ON partie.ClefPubliqueJ1 = compte1.clefPublique " +
                        "LEFT JOIN compte AS compte2 ON partie.ClefPubliqueJ2 = compte2.clefPublique " +
                        "LEFT JOIN compte AS compte3 ON partie.ClefPubliqueArbitre = compte3.clefPublique " + contenueFiltreSql, null);

                String[] from = {"dateDuMatch", "joueur1", "joueur2","arbitre","hashPartie"};

                int[] to = {R.id.textViewListePartieDate, R.id.textViewListePartieJoueur1, R.id.textViewListePartieJoueur2,R.id.textViewListePartieArbitre,R.id.textViewListePartieHash};
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(ListePartieActivity.this, R.layout.listepartie, c, from, to, 0);
                listViewPartie.setAdapter(adapter);

            }
        });

        listViewPartie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor c = (Cursor) listViewPartie.getItemAtPosition(i);
                @SuppressLint("Range") String hashPartie = c.getString(c.getColumnIndex("hashPartie"));
                Intent newintent = new Intent(ListePartieActivity.this,DetailPartieActivity.class);
                newintent.putExtra("id",id);
                newintent.putExtra("ClefPrivee",ClefPrivee);
                newintent.putExtra("ClefPublique",ClefPublique);
                newintent.putExtra("serveur",serveur);
                newintent.putExtra("hashPartie",hashPartie);
                startActivity(newintent);
            }
        });
        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newintent = new Intent(ListePartieActivity.this,MainActivity2.class);
                newintent.putExtra("id",id);
                newintent.putExtra("ClefPrivee",ClefPrivee);
                newintent.putExtra("ClefPublique",ClefPublique);
                newintent.putExtra("serveur",serveur);
                startActivity(newintent);
            }
        });
    }
}