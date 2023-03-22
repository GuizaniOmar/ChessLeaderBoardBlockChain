package com.mcgatletico.chessleaderboardblockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LeaderboardActivity extends AppCompatActivity {
Button btnLeaderboardRetour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Intent x = getIntent();
        String id = x.getStringExtra("id");
        String serveur = x.getStringExtra("serveur");
        String ClefPrivee = x.getStringExtra("ClefPrivee");
        String ClefPublique = x.getStringExtra("ClefPublique");
        btnLeaderboardRetour = findViewById(R.id.btnLeaderboardRetour);


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