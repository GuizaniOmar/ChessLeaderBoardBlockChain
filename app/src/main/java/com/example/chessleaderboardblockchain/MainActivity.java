package com.example.chessleaderboardblockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
        textView = (TextView) findViewById(R.id.textView);
        editPassword = (EditText) findViewById(R.id.editPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        listview1 = (ListView) findViewById(R.id.listview1);
        editTextPseudo = (EditText) findViewById(R.id.editTextPseudo);
        editTextClefPrivee = (EditText) findViewById(R.id.editTextClefPrivee);
        editTextClefPublique = (EditText) findViewById(R.id.editTextClefPublique);
        btnSupprimerDb = findViewById(R.id.btnSupprimerDb);

        DiscordMessage.EnvoieMessage("ROLANDO EST LE \\n MEILLEUR JOUEUR DU MONDE");

        // adapter = new ArrayAdapter(MainActivity.this,R.layout.element,R.id.textView2,vecteur);
       // listview1.setAdapter(adapter);
       helper = new SQLiteOpenHelper(MainActivity.this,"dbchess.db",null,1) {
           @Override
           public void onCreate(SQLiteDatabase sqLiteDatabase) {
               sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS 'COMPTES' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, Pseudo TEXT NOT NULL UNIQUE CHECK (Pseudo = LOWER(Pseudo)), ClefPublique TEXT, ClefPrivee TEXT)");
           }

           @Override
           public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
sqLiteDatabase.execSQL("DROP TABLE IF EXISTS 'COMPTES'");
               onCreate(sqLiteDatabase);
           }
       };


       listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               Cursor d = (Cursor) adapterView.getItemAtPosition(i);
               textView.setText("Vous avez cliqué sur l'élément " + d.getString(1));
                Toast.makeText(MainActivity.this, "Vous avez cliqué sur l'élément " + d.getString(1).toString(), Toast.LENGTH_LONG).show();
                Intent autreFenetre = new Intent(MainActivity.this, MainActivity2.class);
              String ClefDecryptee = "";
              try{
                  ClefDecryptee = CryptageClef.decrypt(d.getString(3).toString(),editPassword.getText().toString());
              }catch(Exception e){

              }
               autreFenetre.putExtra("id", d.getString(1).toString());
               autreFenetre.putExtra("ClefPrivee", ClefDecryptee);

               startActivity(autreFenetre);
           }
       });
       database = helper.getReadableDatabase();
       Cursor c = database.rawQuery("SELECT * FROM COMPTES",null);

       String [] from = {"Pseudo","ClefPublique","ClefPrivee"};
       int [] to = {R.id.affichagePseudo,R.id.affichageClefPublique,R.id.affichageClefPrivee};
        adapter2 = new SimpleCursorAdapter(MainActivity.this,R.layout.element,c,from,to,0);
        listview1.setAdapter(adapter2);

        btnSupprimerDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DiscordMessage.EnvoieMessage(editPassword.getText().toString());

                database = helper.getWritableDatabase();
                database.execSQL("DROP TABLE IF EXISTS 'COMPTES'");
                helper.onCreate(database);
                Toast.makeText(MainActivity.this, "Base de données supprimée avec succès !", Toast.LENGTH_LONG).show();
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 try{
                  //  String ClefPublic = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt5mo5LrPLpe36HIOGYNu+zO98TcsFwtI+AqiIHHV6R2D1n97PyOcgdwc7QroH7fW5szdXjzTEpiJRLJ2/zXMAhBv0o79XOHoP8cdjUp1hdkaOXmQEI8Zzawsbyd0pZhBLmayUodwkZMs7ie0JRDnEpTMcAcz1gFTmTjrcCE79BINXg+W1uuQ6QdEnyADDPnOf2DofuKh40KBHJkoSAwC31L+Xo5L5631+wEcR5Hz1LLN9KUDGUrD2AVfo+r/Y/UAUSQ2n7m4NMVjNaed5K6zMznZbRtadI0w0e12UBx2zBa7UQaapqWu1xmuiHd8BzbJyXwYU8283FlfU3igmtgJWQIDAQAB";
                    //String ClefPrivee = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC3majkus8ul7focg4Zg277M73xNywXC0j4CqIgcdXpHYPWf3s/I5yB3BztCugft9bmzN1ePNMSmIlEsnb/NcwCEG/Sjv1c4eg/xx2NSnWF2Ro5eZAQjxnNrCxvJ3SlmEEuZrJSh3CRkyzuJ7QlEOcSlMxwBzPWAVOZOOtwITv0Eg1eD5bW65DpB0SfIAMM+c5/YOh+4qHjQoEcmShIDALfUv5ejkvnrfX7ARxHkfPUss30pQMZSsPYBV+j6v9j9QBRJDafubg0xWM1p53krrMzOdltG1p0jTDR7XZQHHbMFrtRBpqmpa7XGa6Id3wHNsnJfBhTzbzcWV9TeKCa2AlZAgMBAAECggEAElMOzU1r0j2oSFXh3GAG2qGOap3UE7S3orIE/nH+JJciim0M0v9t5nQemvYEtu21UDHQQlCVzHxW7vlU8SHga7BSSNSmqveNI5hu9aQh2KsTd/sPCugNbj6p7KomdUSd9NChKaWE8bKbwGVMEIsAVRVu1huR6+EUA+3vabCXeEONmgrvdnOoStQGrWcREskivtJOeZKLeBB53aAEDVCKDeTP3cNxcSikq5B+wMqXIkljEkjnwDKzkXU10KKZJDG+pjHoO6hyWBhn3qee2sLz9u/7OKCFOnHPSu9xpnoaELdPEneY64pDdmBR6rtKL9Q6lAJmHuan4p60Vi78DWTYAQKBgQDKIRbwZ78N2YhhgRWqLdztX95HvAW2YpAQQbdcPU4eLvDT1JM/2cVNTc9Kz8H/0Ngx7er9JHZUhvO39WFEstYCoOJV6BRKlNrE7T1y5Hw4sFI0HGXdaOVoFImL/Rb04KetLd2diqaGR6N0DjnBlZVHi05w7LfkzA3vuiLYI6EVAQKBgQDoiF5jdFXZ92nZtR9EaIRqxa/mFDDAWV2hz/ecAcuTvDVS3hNX9T+rNl2t4W6AzGAYJRk0cq3aqk/Libt7ag0meO8VsBDmf/1n4Qhhx1Zq8FYMETLQLOvG1QArX9RKRd3Sxi6H9vcYkTFBmTrp38mR439AQ1J6C0MP6Vv1JWu8WQKBgF+OARApm5JqkiS2J43KHLVDKwvygSjVs5Fb39kFSlbOjh82UV5QDwwairKtQOM00d/Yv8xoXmBbZABFSnR1ruKTOCywiFcxw7JxDSmxhmAuSs5D1owzOLBZdSTuwtmFEv+1vRzrHQpB9623w+oWUvn9i1mrLsxFAxmffzV6sn4BAoGAGrItQ/XDNXb2LAxjPpNRQIDZpOyEfFDGMyGRJ9P870UYSh880UhSuvFO5/uNmDPehGcd8auI0iXja1aws4aFY/lWWYMRLaVcDmUDdVZRUY2uE0yWLNg7aWRi1Jf3418KDHy8Mtfjnmps4T8aSGds2NbpcRNJkMFiPZ1o9UgKimECgYAT4+iFronT0XHC/5FXNMtzheaa+bG971DCeCezrg2ptI7p/AhEpP35zzaH65DJ4cgBGCasr7xyiu1c3rwB/G0YnMC6m8+zx4EB7LgAFLrG2zH5U89fxpqa9UaEFtW+EM4kZjj7G5G7/LdbGRxov3DtFHLocDDLwoeHWrGGOuYifQ==";
                    // Ici on utilise la clef déjà existante
                  //  RSA rsa = new RSA(ClefPrivee,ClefPublic);
                    RSA rsa = new RSA();
                    String ClefPublic = rsa.publicKeyToString();
                    String ClefPrivee = rsa.privateKeyToString();
                    String ClefPriveeCryptee = "";

                 ClefPriveeCryptee = CryptageClef.encrypt(ClefPrivee,editPassword.getText().toString());
                    editTextClefPrivee.setText(ClefPrivee);
                    editTextClefPublique.setText(ClefPublic);
                    SHA2 sha2 = new SHA2();
                    String MessageSha2 = sha2.encrypt("Bonjours");
                    System.out.println("On va encrypter en SHA2: " + MessageSha2);


                    // Ici on génère une clef ! RSA rsa = new RSA();

                    String message = "Bonjour";
                    System.out.println("Message à encoder: " + message);

                    String encoded = rsa.encode(message, rsa.privateKey);
                    // textView.setText(encoded);
                    System.out.println("Message encodé: " + encoded);

                    String decoded = rsa.decode(encoded, rsa.publicKey);
                    System.out.println("Message décodé: " + decoded);

                    String publicKey = rsa.publicKeyToString();
                    System.out.println("Clef Public: " + publicKey);
                    String privateKey = rsa.privateKeyToString();
                    System.out.println("Clef privée: " + privateKey);
                    database = helper.getWritableDatabase();
                    database.execSQL("INSERT INTO COMPTES (Pseudo,ClefPublique,ClefPrivee) VALUES('" + editTextPseudo.getText().toString().toLowerCase() + "','"+ ClefPublic + "','" + ClefPriveeCryptee + "')");
                }catch(Exception e){
                     Toast.makeText(MainActivity.this, "Erreur lors de l'ajout ! ", Toast.LENGTH_LONG).show();

                 }
            }
        });



    }
}