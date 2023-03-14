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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mcgatletico.chessleaderboardblockchain.R;

public class SelectionPseudoActivity extends AppCompatActivity {
    SimpleCursorAdapter adapter2;
    ListView listViewSelectionPseudo;
    TextView textViewTitreSelectionPseudo;
    EditText editTextSelectionPseudo;
    Button btnSelectionPseudo;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_pseudo);
        listViewSelectionPseudo = findViewById(R.id.listViewSelectionPseudo);
        textViewTitreSelectionPseudo = findViewById(R.id.textViewTitreSelectionPseudo);
        editTextSelectionPseudo = findViewById(R.id.editTextSelectionPseudo);
        btnSelectionPseudo = findViewById(R.id.btnSelectionPseudo);
        Intent x = getIntent();
        System.out.println(x.getStringExtra("id"));
        System.out.println(x.getStringExtra("ClefPrivee"));
        System.out.println(x.getStringExtra("ClefPublique"));
        System.out.println(x.getStringExtra("Time"));
        String time  = "";
        if( x.getStringExtra("Time") != null){
            System.out.println("Le time n'est pas null : " + x.getStringExtra("Time"));
            time = (x.getStringExtra("Time"));
        }

        String[] listeNomParticipants = {"","",""};
        if (x.getStringArrayExtra("listeParticipants") != null){
            listeNomParticipants = x.getStringArrayExtra("listeParticipants");
        }



        DatabaseHelper maBaseDeDonnees = DatabaseHelper.getInstance(SelectionPseudoActivity.this);

        int id_player = x.getIntExtra("id_player",1);
        String[] listeTitreParticipants = {"Arbitre","Joueur 1","Joueur 2"};
        textViewTitreSelectionPseudo.setText("Selectionnez le pseudo du "+ listeTitreParticipants[id_player]+" :");
       SQLiteDatabase database = maBaseDeDonnees.getDatabase();
        Cursor c = database.rawQuery("SELECT * FROM compte",null);

        String [] from = {"pseudo"};
        int [] to = {android.R.id.text1};
        adapter2 = new SimpleCursorAdapter(SelectionPseudoActivity.this,android.R.layout.simple_list_item_1,c,from,to,0);
        listViewSelectionPseudo.setAdapter(adapter2);


        listViewSelectionPseudo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor c = (Cursor) adapterView.getItemAtPosition(i);
                String pseudo = c.getString(1);
                editTextSelectionPseudo.setText(pseudo);
            }
        });

        String[] finalListeNomParticipants = listeNomParticipants;
        String finalTime = time;
        btnSelectionPseudo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pseudo = editTextSelectionPseudo.getText().toString();

                if (!pseudo.equals("")){
                    finalListeNomParticipants[id_player] = pseudo; // 0 = arbitre  1 = joueur 1 ; 2 = joueur 2
                    Intent newintent = new Intent(SelectionPseudoActivity.this,AjouterPartieActivity.class);
                    newintent.putExtra("id_player",id_player);
                    newintent.putExtra("id", x.getStringExtra("id"));
                    newintent.putExtra("ClefPrivee", x.getStringExtra("ClefPrivee"));
                    newintent.putExtra("ClefPublique", x.getStringExtra("ClefPublique"));
                    newintent.putExtra("listeParticipants", finalListeNomParticipants);
                    newintent.putExtra("Time", finalTime);
                    startActivity(newintent);
                } else{
                    Toast.makeText(SelectionPseudoActivity.this, "Veuillez selectionner un pseudo ! ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}