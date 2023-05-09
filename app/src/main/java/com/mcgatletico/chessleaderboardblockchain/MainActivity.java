package com.mcgatletico.chessleaderboardblockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView textView;

    EditText editTextPseudo;
    ListView listview1;

    SQLiteDatabase database;

    EditText editPassword;
    Button btnLogin;
    Button btnConnexion;
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
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!wifiInfo.isConnected()) {
            Toast.makeText(getApplicationContext(), "Pas de connexion Wi-Fi", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Pas de connexion Wi-Fi - Veuillez vous connecter au Wifi et relancer l'application", Toast.LENGTH_LONG).show();
      //return;
        }


        setContentView(R.layout.activity_main);


        DatabaseHelper maBaseDeDonnees = DatabaseHelper.getInstance(MainActivity.this);

        textView = (TextView) findViewById(R.id.textView);
        editPassword = (EditText) findViewById(R.id.editPassword);
        btnLogin2 = (Button) findViewById(R.id.btnRegister1);
        btnLogin = (Button) findViewById(R.id.btnRegisterServer);
        btnConnexion = (Button) findViewById(R.id.btnConnexion);
        listview1 = (ListView) findViewById(R.id.listview1);
        editTextPseudo = (EditText) findViewById(R.id.editTextPseudo);
        switch1 = (Switch) findViewById(R.id.switch1);
        editTextPseudo.setFocusable(false);
        editPassword.setFocusable(false);
        DiscordMessage.EnvoieMessage("Un client \\nse connecte à l'application");
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInformation = wifiMgr.getConnectionInfo();
        MultiClientSocket.db = maBaseDeDonnees;
        Server.db = maBaseDeDonnees;
        int ip = ( wifiInformation).getIpAddress();

        if (!MultiClientSocket.isRunning)
        {
            Toast.makeText(MainActivity.this,"Connexion au P2P", Toast.LENGTH_LONG).show();
            ThreadClient.lancerServeur();
            ThreadClient.lancerClient(maBaseDeDonnees);
        }
        ThreadClient.monIP = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
        ThreadClient.IP = String.format("%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));



        editTextPseudo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextPseudo.setFocusableInTouchMode(true);
                editTextPseudo.requestFocus();
               }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                Toast.makeText(MainActivity.this, "Adresse IP locale: " + ThreadClient.monIP, Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this, "Mon  IP locale: " + ThreadClient.IP, Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this, "Nombre de Connexions: " + MultiClientSocket.count, Toast.LENGTH_LONG).show();

            }
        });
        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPassword.setFocusableInTouchMode(true);
                editPassword.requestFocus();
            }
        });



        // On test de récupèrer le socket du serveur


        // adapter = new ArrayAdapter(MainActivity.this,R.layout.element,R.id.textView2,vecteur);
        // listview1.setAdapter(adapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                database = maBaseDeDonnees.getDatabase();
                Cursor c = database.rawQuery("SELECT * FROM COMPTE", null);

                String[] from = {"pseudo", "clefPublique", "clefPrivee"};
                int[] to = {R.id.affichagePseudo, R.id.affichageClefPublique, R.id.affichageClefPrivee};
                adapter2 = new SimpleCursorAdapter(MainActivity.this, R.layout.element, c, from, to, 0);
                listview1.setAdapter(adapter2);
                System.out.println("Actualisation2");

            }
        }, 1000);

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
                            Cursor c = database.rawQuery("SELECT * FROM COMPTE", null);

                            String[] from = {"pseudo", "clefPublique", "clefPrivee"};
                            int[] to = {R.id.affichagePseudo, R.id.affichageClefPublique, R.id.affichageClefPrivee};
                            adapter2 = new SimpleCursorAdapter(MainActivity.this, R.layout.element, c, from, to, 0);
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

        btnConnexion.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        database = maBaseDeDonnees.getDatabase();
                        Cursor c = database.rawQuery("SELECT * FROM COMPTE", null);

                        String[] from = {"pseudo", "clefPublique", "clefPrivee"};
                        int[] to = {R.id.affichagePseudo, R.id.affichageClefPublique, R.id.affichageClefPrivee};
                        adapter2 = new SimpleCursorAdapter(MainActivity.this, R.layout.element, c, from, to, 0);
                        listview1.setAdapter(adapter2);
                        System.out.println("Actualisation2");
                    }
                }, 1000);

                database = maBaseDeDonnees.getDatabase();
                Cursor c = database.rawQuery("SELECT * FROM COMPTE WHERE pseudo='" + editTextPseudo.getText().toString() + "'", null);
                if (c.getCount() > 0){
                    c.moveToFirst();
                    String clefPublique = c.getString(c.getColumnIndex("clefPublique"));
                    String clefPrivee = c.getString(c.getColumnIndex("clefPrivee"));
                    String pseudo = c.getString(c.getColumnIndex("pseudo"));
                    Intent autreFenetre = new Intent(MainActivity.this, MainActivity2.class);
                    String ClefDecryptee = "";
                    try {
                        ClefDecryptee = CryptageClef.decrypt(clefPrivee, editPassword.getText().toString());
                        if (ClefDecryptee.length() < 10){
                            Toast.makeText(MainActivity.this, "Mot de passe incorrect !", Toast.LENGTH_LONG).show();
                        }else {
                            autreFenetre.putExtra("id", pseudo);
                            autreFenetre.putExtra("ClefPublique", clefPublique);

                            autreFenetre.putExtra("ClefPrivee", ClefDecryptee);
                            String serveur = Boolean.toString(switch1.isChecked());
                            autreFenetre.putExtra("serveur", serveur);
                            startActivity(autreFenetre);
                        }
                    } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Mot de passe incorrect !", Toast.LENGTH_LONG).show();
                    }



                }else{
                    Toast.makeText(MainActivity.this, "Ce compte n'existe pas !", Toast.LENGTH_LONG).show();
                }


            }
        });
        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                    Cursor d = (Cursor) adapterView.getItemAtPosition(i);
                    editTextPseudo.setText(d.getString(1));
                    Toast.makeText(MainActivity.this,"Vous avez selectionné le pseudo " + d.getString(1) + " Veuillez écrire le mot de passe et vous connecter", Toast.LENGTH_LONG).show();


            }
        });

        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    database = maBaseDeDonnees.getDatabase();
                    Cursor c = database.rawQuery("SELECT * FROM COMPTE", null);

                    String[] from = {"pseudo", "clefPublique", "clefPrivee"};
                    int[] to = {R.id.affichagePseudo, R.id.affichageClefPublique, R.id.affichageClefPrivee};
                    adapter2 = new SimpleCursorAdapter(MainActivity.this, R.layout.element, c, from, to, 0);
                    listview1.setAdapter(adapter2);

                }
            }, 10000);

        } catch (Exception e) {
        }

        //btnLogin = via Serveur distant
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextPseudo.getText().toString().equals("") || editPassword.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Veuillez remplir tous les champs !", Toast.LENGTH_LONG).show();
                } else {
                    try {

                        // ON vérifie dans la base de données si y'a un pseudo == sinon on génère une clef publique et une clef privée
                        Cursor c = database.rawQuery("SELECT * FROM compte WHERE pseudo='" + editTextPseudo.getText().toString() + "'", null);
                        boolean pseudoExiste = c.getCount() > 0? true : false;
                        String ClefPublique = "";
                        String ClefPriveeCryptee = "";
                        try {


                            if (pseudoExiste) {
                                System.out.println("Pseudo déjà existant ! ");
                                Toast.makeText(MainActivity.this, "Pseudo déjà existant veuillez en choisir un autre pour vous inscrire ! ", Toast.LENGTH_SHORT).show();
                            }
                                if (!pseudoExiste) {
                                Toast.makeText(MainActivity.this, "Création d'un nouveau compte ! ", Toast.LENGTH_SHORT).show();
                                // On génère une clef publique et une clef privée


                                RSAPSS rsapss = new RSAPSS(); // On initialise la classe pour générer les clefs
                                ClefPublique = RSAPSS.publicKeyToString();

                                System.out.println("Clef publique : " + ClefPublique);

                                System.out.println("Encodage " + RSAPSS.encode("Salut", RSAPSS.privateKey));
                                System.out.println("Decodage réussie ? " + RSAPSS.decode("Salut", RSAPSS.encode("Salut", RSAPSS.privateKey), RSAPSS.publicKey));
                                ClefPriveeCryptee = RSAPSS.privateKeyToString();


                                ClefPriveeCryptee = CryptageClef.encrypt(ClefPriveeCryptee, editPassword.getText().toString());

                                ThreadClient.login(maBaseDeDonnees, editTextPseudo.getText().toString(), ClefPublique, ClefPriveeCryptee);
                                System.out.println("Actualisation");
                                ThreadClient.actualiseBaseDeDonnees(maBaseDeDonnees);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        database = maBaseDeDonnees.getDatabase();
                                        Cursor c = database.rawQuery("SELECT * FROM COMPTE", null);

                                        String[] from = {"pseudo", "clefPublique", "clefPrivee"};
                                        int[] to = {R.id.affichagePseudo, R.id.affichageClefPublique, R.id.affichageClefPrivee};
                                        adapter2 = new SimpleCursorAdapter(MainActivity.this, R.layout.element, c, from, to, 0);
                                        listview1.setAdapter(adapter2);
                                        System.out.println("Actualisation2");
                                    }
                                }, 2000);

                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Erreur lors de la vérification du pseudo ! ", Toast.LENGTH_LONG).show();
                        }
                        // début de l'actualisation du pseudo

                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Erreur lors de l'ajout ! ", Toast.LENGTH_LONG).show();

                    }
                }
            }

        });

        //btnLogin2 = via local + p2p
        btnLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextPseudo.getText().toString().equals("") || editPassword.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Veuillez remplir tous les champs !", Toast.LENGTH_LONG).show();
                } else {
                    try {


                        // ON vérifie dans la base de données si y'a un pseudo == sinon on génère une clef publique et une clef privée
                        Cursor c = database.rawQuery("SELECT * FROM compte where pseudo='" + editTextPseudo.getText().toString() +  "'", null);
                        boolean pseudoExiste = c.getCount() > 0? true:false;
                        String ClefPublique = "";
                        String ClefPriveeCryptee = "";
                        try {

                            if (pseudoExiste)
                                Toast.makeText(MainActivity.this, "Pseudo déjà existant veuillez en choisir un autre pour vous inscrire ! ", Toast.LENGTH_LONG).show();


                            else {
                                Toast.makeText(MainActivity.this, "Pseudo inexistant, création d'un nouveau compte ! ", Toast.LENGTH_LONG).show();


                                RSAPSS rsapss = new RSAPSS(); // permet d'initialiser la clef RSAPSS
                                ClefPublique = RSAPSS.publicKeyToString();

                                ClefPriveeCryptee = RSAPSS.privateKeyToString();


                                ClefPriveeCryptee = CryptageClef.encrypt(ClefPriveeCryptee, editPassword.getText().toString());
                                ThreadClient.ajouterCompte(maBaseDeDonnees, editTextPseudo.getText().toString(), ClefPublique, ClefPriveeCryptee);
                                System.out.println("Actualisation");

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        database = maBaseDeDonnees.getDatabase();
                                        Cursor c = database.rawQuery("SELECT * FROM COMPTE", null);

                                        String[] from = {"pseudo", "clefPublique", "clefPrivee"};
                                        int[] to = {R.id.affichagePseudo, R.id.affichageClefPublique, R.id.affichageClefPrivee};
                                        adapter2 = new SimpleCursorAdapter(MainActivity.this, R.layout.element, c, from, to, 0);
                                        listview1.setAdapter(adapter2);
                                        System.out.println("Actualisation2");
                                    }
                                }, 1000);
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Erreur lors de la vérification du pseudo ! ", Toast.LENGTH_LONG).show();
                        }
                        // début de l'actualisation du pseudo



                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Erreur lors de l'ajout ! ", Toast.LENGTH_LONG).show();

                    }
                }
            }
        });

    }


}