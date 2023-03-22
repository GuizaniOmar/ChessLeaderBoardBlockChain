package com.mcgatletico.chessleaderboardblockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class LeaderboardActivity extends AppCompatActivity {
Button btnLeaderboardRetour;

Button btnLeaderboardActualiserListe;
ListView listViewLeaderboard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Intent x = getIntent();
        String id = x.getStringExtra("id");
        String serveur = x.getStringExtra("serveur");
        String ClefPrivee = x.getStringExtra("ClefPrivee");
        String ClefPublique = x.getStringExtra("ClefPublique");
        DatabaseHelper maBaseDeDonnees = DatabaseHelper.getInstance(LeaderboardActivity.this);
        SQLiteDatabase database = maBaseDeDonnees.getDatabase();
        listViewLeaderboard = findViewById(R.id.listViewLeaderboard);
        btnLeaderboardRetour = findViewById(R.id.btnLeaderboardRetour);

        btnLeaderboardActualiserListe = findViewById(R.id.btnLeaderboardActualiserListe);

        btnLeaderboardActualiserListe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               database.execSQL("DELETE FROM partieDetail");
                //Toast.makeText(LeaderboardActivity.this, "Actualisation en cours...", Toast.LENGTH_SHORT).show();
                Toast.makeText(LeaderboardActivity.this, ThreadClient.updateLeaderboard(maBaseDeDonnees), Toast.LENGTH_SHORT).show();
                //database.execSQL("INSERT INTO leaderboard ('clefPublique', 'pseudo', 'elo', 'coefficientArbitrage', 'scoreTotal', 'nbParties', 'nbVictoire', 'nbConfirmation', 'nbPartieArbitre', 'nbNul', 'nbDefaite') VALUES ('clefPublique2', 'pseudonyme2', '700', '0.5', '300', '5', '3', '10', '3', '1', '1');");
                Cursor c = database.rawQuery("SELECT * FROM leaderboard", null);
//("pseudo", "elo", "coefficientArbitrage", "scoreTotal", "nbParties", "nbVictoire", "nbConfirmation", "nbPartieArbitre", "nbNul", "nbDefaite")
                String[] from = {"pseudo","elo","coefficientArbitrage","scoreTotal","nbParties","nbVictoire","nbConfirmation","nbPartieArbitre","nbNul","nbDefaite"};

                int[] to = {R.id.textViewElementLeaderboardPseudo, R.id.textViewElementLeaderboardElo, R.id.textViewElementLeaderboardArbitrage, R.id.textViewElementLeaderboardScoreTotal, R.id.textViewElementLeaderboardNbParties, R.id.textViewElementLeaderboardNbVictoire, R.id.textViewElementLeaderboardNbConfirmation, R.id.textViewElementLeaderboardNbPartieArbitre, R.id.textViewElementLeaderboardNbNul, R.id.textViewElementLeaderboardNbDefaite};
                SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(LeaderboardActivity.this, R.layout.elementleaderboard, c, from, to, 0);
                listViewLeaderboard.setAdapter(adapter2);


            }
        });

        btnLeaderboardRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newintent = new Intent(LeaderboardActivity.this,MainActivity2.class);
                newintent.putExtra("id",id);
                newintent.putExtra("ClefPrivee",ClefPrivee);
                newintent.putExtra("ClefPublique",ClefPublique);
                newintent.putExtra("serveur",serveur);
                startActivity(newintent);
            }
        });

    }
}