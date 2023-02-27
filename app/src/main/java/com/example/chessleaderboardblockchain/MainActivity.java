package com.example.chessleaderboardblockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    TextView textView;

    EditText editTextPseudo;
    ListView listview1;
    SQLiteOpenHelper helper;
    SQLiteDatabase database;
    EditText editTextClefPublique;
    EditText editTextClefPrivee;
    EditText editPassword;
    Button btnLogin;
    Button btnSupprimerDb;
    String [] vecteur = {"Atletico","JoueurDeux","JoueurTrois3","Joueur4","Joueur5","Joueur6","Joueur7","Joueur8","Joueur9","Joueur10"};
    ArrayAdapter<String> adapter;
    SimpleCursorAdapter adapter2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseHelper maBaseDeDonnees = DatabaseHelper.getInstance(MainActivity.this);
        textView = (TextView) findViewById(R.id.textView);
        editPassword = (EditText) findViewById(R.id.editPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        listview1 = (ListView) findViewById(R.id.listview1);
        editTextPseudo = (EditText) findViewById(R.id.editTextPseudo);

        DiscordMessage.EnvoieMessage("ROLANDO EST LE \\n MEILLEUR JOUEUR DU MONDE");

        // On test de récupèrer le socket du serveur


            ThreadClient.actualiseBaseDeDonnees(maBaseDeDonnees);



        // adapter = new ArrayAdapter(MainActivity.this,R.layout.element,R.id.textView2,vecteur);
       // listview1.setAdapter(adapter);



       listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              if (editPassword.getText().toString().equals("")) {
                  Toast.makeText(MainActivity.this, "Veuillez remplir le mot de passe pour vous connecter !", Toast.LENGTH_LONG).show();
              }else {
                  Cursor d = (Cursor) adapterView.getItemAtPosition(i);
                  textView.setText("Vous avez cliqué sur l'élément " + d.getString(1));
                  Toast.makeText(MainActivity.this, "Vous avez cliqué sur l'élément " + d.getString(1).toString(), Toast.LENGTH_LONG).show();
                  Intent autreFenetre = new Intent(MainActivity.this, MainActivity2.class);
                  String ClefDecryptee = "";
                  try {
                      ClefDecryptee = CryptageClef.decrypt(d.getString(3).toString(), editPassword.getText().toString());
                  } catch (Exception e) {

                  }
                  autreFenetre.putExtra("id", d.getString(1).toString());
                  autreFenetre.putExtra("ClefPrivee", ClefDecryptee);

                  startActivity(autreFenetre);
              }
           }
       });

       try{
           new Handler().postDelayed(new Runnable() {
               @Override
               public void run() {
                   database = maBaseDeDonnees.getDatabase();
                   Cursor c = database.rawQuery("SELECT * FROM COMPTES",null);

                   String [] from = {"Pseudo","ClefPublique","ClefPrivee"};
                   int [] to = {R.id.affichagePseudo,R.id.affichageClefPublique,R.id.affichageClefPrivee};
                   adapter2 = new SimpleCursorAdapter(MainActivity.this,R.layout.element,c,from,to,0);
                   listview1.setAdapter(adapter2);

               }
           }, 2000);

       }catch(Exception e){}




        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextPseudo.getText().toString().equals("") || editPassword.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Veuillez remplir tous les champs !", Toast.LENGTH_LONG).show();}else{
                 try{


                     // ON vérifie dans la base de données si y'a un pseudo == sinon on génère une clef publique et une clef privée
                     Cursor c = database.rawQuery("SELECT * FROM COMPTES",null);
                        boolean pseudoExiste = false;
                        String ClefPublique = "";
                        String ClefPriveeCryptee = "";
                        try{
                            while(c.moveToNext()){
                                if(c.getString(1).toString().equals(editTextPseudo.getText().toString())){
                                    pseudoExiste = true;
                                    ClefPublique= c.getString(2).toString();
                                    ClefPriveeCryptee = c.getString(3).toString();
                                }
                            }


                            if (pseudoExiste == false) {
                                // On génère une clef publique et une clef privée
                                RSA rsa = new RSA();
                                ClefPublique = rsa.publicKeyToString();
                                ClefPriveeCryptee = rsa.privateKeyToString();


                                ClefPriveeCryptee = CryptageClef.encrypt(ClefPriveeCryptee, editPassword.getText().toString());
                            }
                        }catch(Exception e){
                            Toast.makeText(MainActivity.this, "Erreur lors de la vérification du pseudo ! ", Toast.LENGTH_LONG).show();
                        }
                        // début de l'actualisation du pseudo
                     ThreadClient.login(maBaseDeDonnees,editTextPseudo.getText().toString(),ClefPublique,ClefPriveeCryptee);
                     System.out.println("Actualisation");
                     new Handler().postDelayed(new Runnable() {
                         @Override
                         public void run() {
                             database = maBaseDeDonnees.getDatabase();
                             Cursor c = database.rawQuery("SELECT * FROM COMPTES",null);

                             String [] from = {"Pseudo","ClefPublique","ClefPrivee"};
                             int [] to = {R.id.affichagePseudo,R.id.affichageClefPublique,R.id.affichageClefPrivee};
                             adapter2 = new SimpleCursorAdapter(MainActivity.this,R.layout.element,c,from,to,0);
                             listview1.setAdapter(adapter2);

                         }
                     }, 5000);

                 }catch(Exception e){
                     Toast.makeText(MainActivity.this, "Erreur lors de l'ajout ! ", Toast.LENGTH_LONG).show();

                 }
            }}
        });



    }
}