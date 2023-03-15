package com.mcgatletico.chessleaderboardblockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mcgatletico.chessleaderboardblockchain.R;

import java.net.UnknownHostException;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    TextView textView;

    EditText editTextPseudo;
    ListView listview1;

    SQLiteDatabase database;

    EditText editPassword;
    Button btnLogin;
    Button btnLogin2;
    Switch switch1;
    SimpleCursorAdapter adapter2;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // Votre code ici. Laissez vide si vous ne voulez rien faire lors du retour en arrière.
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        DatabaseHelper maBaseDeDonnees = DatabaseHelper.getInstance(MainActivity.this);
        textView = (TextView) findViewById(R.id.textView);
        editPassword = (EditText) findViewById(R.id.editPassword);
        btnLogin2 = (Button) findViewById(R.id.btnLogin2);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        listview1 = (ListView) findViewById(R.id.listview1);
        editTextPseudo = (EditText) findViewById(R.id.editTextPseudo);
        switch1 = (Switch) findViewById(R.id.switch1);

        DiscordMessage.EnvoieMessage("Un client \\nse connecte à l'application");

        // On test de récupèrer le socket du serveur






        // adapter = new ArrayAdapter(MainActivity.this,R.layout.element,R.id.textView2,vecteur);
       // listview1.setAdapter(adapter);

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    switch1.setText("Connexion Serveur");
                    Toast.makeText(MainActivity.this, "Les comptes locales (sans clefs privées) ont été envoyé au serveur ! ", Toast.LENGTH_LONG).show();
                    ThreadClient.envoyerComptes(maBaseDeDonnees);
                    Toast.makeText(MainActivity.this, "Les comptes depuis le serveur vont être récupéres merci de patienter 15 secondes ", Toast.LENGTH_LONG).show();


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ThreadClient.actualiseBaseDeDonnees(maBaseDeDonnees);
                             }
                    }, 10000);


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            database = maBaseDeDonnees.getDatabase();
                            Cursor c = database.rawQuery("SELECT * FROM COMPTE",null);

                            String [] from = {"pseudo","clefPublique","clefPrivee"};
                            int [] to = {R.id.affichagePseudo,R.id.affichageClefPublique,R.id.affichageClefPrivee};
                            adapter2 = new SimpleCursorAdapter(MainActivity.this,R.layout.element,c,from,to,0);
                            listview1.setAdapter(adapter2);
                            System.out.println("Actualisation2");
                        }
                    }, 15000);

                    btnLogin2.setEnabled(false);

                    btnLogin.setEnabled(true);
                } else {
                    switch1.setText("Connexion Peer-to-Peer");


                    btnLogin.setEnabled(false);
                    btnLogin2.setEnabled(true);
                }
            }
        });
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
                  autreFenetre.putExtra("ClefPublique", d.getString(2).toString());

                  autreFenetre.putExtra("ClefPrivee", ClefDecryptee);
                  String serveur = Boolean.toString(switch1.isChecked());
                    autreFenetre.putExtra("serveur", serveur);
                  startActivity(autreFenetre);
              }
           }
       });

       try{
           new Handler().postDelayed(new Runnable() {
               @Override
               public void run() {
                   database = maBaseDeDonnees.getDatabase();
                   Cursor c = database.rawQuery("SELECT * FROM COMPTE",null);

                   String [] from = {"pseudo","clefPublique","clefPrivee"};
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
                     Cursor c = database.rawQuery("SELECT * FROM COMPTE",null);
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


                            if (!pseudoExiste) {
                                Toast.makeText(MainActivity.this, "Pseudo inexistant, création d'un nouveau compte ! ", Toast.LENGTH_LONG).show();
                                // On génère une clef publique et une clef privée
                               // System.out.println(Arrays.toString(new Set[]{Security.getAlgorithms("Signature")}));
                               // System.out.println("gROS GROS GROS");

                                RSAPSS rsapss = new RSAPSS();
                                Toast.makeText(MainActivity.this, "Encodage de message : " + RSAPSS.encode("Salut",RSAPSS.privateKey), Toast.LENGTH_LONG).show();
                                ClefPublique = RSAPSS.publicKeyToString();

                                System.out.println("Clef publique : " + ClefPublique);

                                System.out.println("Encodage " + RSAPSS.encode("Salut",RSAPSS.privateKey));
                               System.out.println("Decodage réussie ? " + RSAPSS.decode("Salut",RSAPSS.encode("Salut",RSAPSS.privateKey),RSAPSS.publicKey));
                                ClefPriveeCryptee = RSAPSS.privateKeyToString();


                                ClefPriveeCryptee = CryptageClef.encrypt(ClefPriveeCryptee, editPassword.getText().toString());
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Pseudo existant Veuillez changer de pseudo ! ", Toast.LENGTH_LONG).show();
                            }
                        }catch(Exception e){
                            Toast.makeText(MainActivity.this, "Erreur lors de la vérification du pseudo ! ", Toast.LENGTH_LONG).show();
                        }
                        // début de l'actualisation du pseudo

                     ThreadClient.login(maBaseDeDonnees,editTextPseudo.getText().toString(),ClefPublique,ClefPriveeCryptee);
                     System.out.println("Actualisation");
                     ThreadClient.actualiseBaseDeDonnees(maBaseDeDonnees);
                     new Handler().postDelayed(new Runnable() {
                         @Override
                         public void run() {

                             database = maBaseDeDonnees.getDatabase();
                             Cursor c = database.rawQuery("SELECT * FROM COMPTE",null);

                             String [] from = {"pseudo","clefPublique","clefPrivee"};
                             int [] to = {R.id.affichagePseudo,R.id.affichageClefPublique,R.id.affichageClefPrivee};
                             adapter2 = new SimpleCursorAdapter(MainActivity.this,R.layout.element,c,from,to,0);
                             listview1.setAdapter(adapter2);
                            System.out.println("Actualisation2");
                         }
                     }, 2000);

                 }catch(Exception e){
                     Toast.makeText(MainActivity.this, "Erreur lors de l'ajout ! ", Toast.LENGTH_LONG).show();

                 }
            }}
        });


        btnLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextPseudo.getText().toString().equals("") || editPassword.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Veuillez remplir tous les champs !", Toast.LENGTH_LONG).show();}else{
                    try{


                        // ON vérifie dans la base de données si y'a un pseudo == sinon on génère une clef publique et une clef privée
                        Cursor c = database.rawQuery("SELECT * FROM COMPTE",null);
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


                            if (!pseudoExiste) {
                                Toast.makeText(MainActivity.this, "Pseudo inexistant, création d'un nouveau compte ! ", Toast.LENGTH_LONG).show();
                                // On génère une clef publique et une clef privée
                                // System.out.println(Arrays.toString(new Set[]{Security.getAlgorithms("Signature")}));
                                // System.out.println("gROS GROS GROS");

                                RSAPSS rsapss = new RSAPSS(); // permet d'initialiser la clef RSAPSS
                                ClefPublique = RSAPSS.publicKeyToString();

                                ClefPriveeCryptee = RSAPSS.privateKeyToString();


                                ClefPriveeCryptee = CryptageClef.encrypt(ClefPriveeCryptee, editPassword.getText().toString());
                            }
                        }catch(Exception e){
                            Toast.makeText(MainActivity.this, "Erreur lors de la vérification du pseudo ! ", Toast.LENGTH_LONG).show();
                        }
                        // début de l'actualisation du pseudo

                        ThreadClient.ajouterCompte(maBaseDeDonnees,editTextPseudo.getText().toString(),ClefPublique,ClefPriveeCryptee);
                        System.out.println("Actualisation");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                database = maBaseDeDonnees.getDatabase();
                                Cursor c = database.rawQuery("SELECT * FROM COMPTE",null);

                                String [] from = {"pseudo","clefPublique","clefPrivee"};
                                int [] to = {R.id.affichagePseudo,R.id.affichageClefPublique,R.id.affichageClefPrivee};
                                adapter2 = new SimpleCursorAdapter(MainActivity.this,R.layout.element,c,from,to,0);
                                listview1.setAdapter(adapter2);
                                System.out.println("Actualisation2");
                            }
                        }, 1000);

                    }catch(Exception e){
                        Toast.makeText(MainActivity.this, "Erreur lors de l'ajout ! ", Toast.LENGTH_LONG).show();

                    }
                }}
        });

    }


}