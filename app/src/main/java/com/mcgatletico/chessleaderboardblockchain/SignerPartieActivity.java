package com.mcgatletico.chessleaderboardblockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SignerPartieActivity extends AppCompatActivity {
    TextView textTitreSignerPartie;
TextView textPseudoJ1Signature;
TextView textPseudoJ2Signature;
TextView textPseudoArbitreSignature;
RadioButton choixJ1;
RadioButton choixJ2;
RadioButton choixArbitre;
RadioButton choixVictoire;
RadioButton choixDefaite;
RadioButton choixNul;
Button btnRetourSignerPartie;
Button btnVoter;
String[] Couleurs = {"#A6EB8F","#E6AF2E","#632B2F"};
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
        setContentView(R.layout.activity_signer_partie);
        btnRetourSignerPartie = (Button) findViewById(R.id.btnRetourSignerPartie);
        textTitreSignerPartie = (TextView) findViewById(R.id.textTitreSignerPartie);
        textPseudoJ1Signature = (TextView) findViewById(R.id.textPseudoJ1Signature);
        textPseudoJ2Signature = (TextView) findViewById(R.id.textPseudoJ2Signature);
        textPseudoArbitreSignature = (TextView) findViewById(R.id.textPseudoArbitreSignature);
        choixJ1 = (RadioButton) findViewById(R.id.choixJ1);
        choixJ2 = (RadioButton) findViewById(R.id.choixJ2);
        choixArbitre = (RadioButton) findViewById(R.id.choixArbitre);
        choixVictoire = (RadioButton) findViewById(R.id.choixVictoire);
        choixDefaite = (RadioButton) findViewById(R.id.choixDefaite);
        choixNul = (RadioButton) findViewById(R.id.choixNul);
        btnVoter = (Button) findViewById(R.id.btnVoter);
        Intent intent = getIntent();
        String hashPartie = intent.getStringExtra("HashPartie");
        String clefPrivee = intent.getStringExtra("ClefPrivee");
        String clefPublique = intent.getStringExtra("ClefPublique");
        String serveur = intent.getStringExtra("serveur");
        DatabaseHelper maBaseDeDonnees = DatabaseHelper.getInstance(SignerPartieActivity.this);
        SQLiteDatabase database = maBaseDeDonnees.getDatabase();
        try{
            new Handler().postDelayed(new Runnable() {
                @SuppressLint("Range")
                @Override
                public void run() {
                   // String contenueFiltreSql = "WHERE ClefPubliqueJ1 = '" + clefPublique + "' OR ClefPubliqueJ2 = '" + clefPublique + "' OR lefPubliqueArbitre = '" + clefPublique + "'";
                    String contenueFiltreSql = "WHERE hashPartie = '" + hashPartie + "'";

                    //Cursor c = database.rawQuery("SELECT * FROM PARTIES" + contenueFiltreSql,null);
                    Cursor c = database.rawQuery("SELECT partieARecevoir._id,partieARecevoir.clefPubliqueJ1,partieARecevoir.clefPubliqueJ2,partieARecevoir.clefPubliqueArbitre, partieARecevoir.voteJ1,partieARecevoir.voteJ2,partieARecevoir.voteArbitre,partieARecevoir.signatureJ1,partieARecevoir.signatureJ2,partieARecevoir.signatureArbitre, strftime('%d-%m-%Y %H:%M', datetime(partieARecevoir.timestamp/1000, 'unixepoch')) as dateDuMatch, partieARecevoir.hashPartie, \n" +
                            "compte1.pseudo AS joueur1, " +
                            "compte2.pseudo AS joueur2, " +
                            "compte3.pseudo AS arbitre " +
                            "FROM partieARecevoir " +
                            "LEFT JOIN compte AS compte1 ON partieARecevoir.clefPubliqueJ1 = compte1.clefPublique " +
                            "LEFT JOIN compte AS compte2 ON partieARecevoir.clefPubliqueJ2 = compte2.clefPublique " +
                            "LEFT JOIN compte AS compte3 ON partieARecevoir.clefPubliqueArbitre = compte3.clefPublique " + contenueFiltreSql,null);

                   if (c.moveToNext()) {
                     Toast.makeText(SignerPartieActivity.this, "Partie trouvée ! ", Toast.LENGTH_SHORT).show() ;
                       String hashPartie = c.getString(c.getColumnIndex("hashPartie"));
                       String voteJ1 = c.getString(c.getColumnIndex("voteJ1"));
                       String voteJ2 = c.getString(c.getColumnIndex("voteJ2"));
                       String voteArbitre = c.getString(c.getColumnIndex("voteArbitre"));
                       String dateDuMatch = c.getString(c.getColumnIndex("dateDuMatch"));
                       String joueur1 = c.getString(c.getColumnIndex("joueur1"));
                       String joueur2 = c.getString(c.getColumnIndex("joueur2"));
                        String arbitre = c.getString(c.getColumnIndex("arbitre"));
                        String ClefPubliqueJ1 = c.getString(c.getColumnIndex("clefPubliqueJ1"));
                        String ClefPubliqueJ2 = c.getString(c.getColumnIndex("clefPubliqueJ2"));
                        String ClefPubliqueArbitre = c.getString(c.getColumnIndex("clefPubliqueArbitre"));
                       String signatureJ1 = c.getString(c.getColumnIndex("signatureJ1"));
                          String signatureJ2 = c.getString(c.getColumnIndex("signatureJ2"));
                            String signatureArbitre = c.getString(c.getColumnIndex("signatureArbitre"));

                        textTitreSignerPartie.setText("Signer la partie " + hashPartie + " du " + dateDuMatch);
                       textPseudoJ1Signature.setText(joueur1);
                       textPseudoJ2Signature.setText(joueur2);
                       textPseudoArbitreSignature.setText(arbitre);


                       if (clefPublique.equals(c.getString(c.getColumnIndex("clefPubliqueJ1")))){
                           choixJ1.setChecked(true);}
                       else if (clefPublique.equals(c.getString(c.getColumnIndex("clefPubliqueJ2")))){
                           choixJ2.setChecked(true);}

                    else if (clefPublique.equals(c.getString(c.getColumnIndex("clefPubliqueArbitre")))){
                        choixArbitre.setChecked(true);}
                    else {
                        Toast.makeText(SignerPartieActivity.this, "Vous n'êtes pas concerné par cette partie ! Tu peux tenter de voter, les autres peer refuseront la donnée et tu perdras des points ! ", Toast.LENGTH_SHORT).show() ;
                    }

                    if (!voteJ1.equals("")){
                        textPseudoJ1Signature.setTextColor(Color.parseColor(Couleurs[0]));
                        // On essaie de déchiffrer le vote
                        try{
                            boolean signatureEstValide = RSAPSS.decode(voteJ1,signatureJ1, RSAPSS.publicKeyFromString(ClefPubliqueJ1));

                            String[] parties = voteJ1.split("-");
                            String hashduVote = parties[0];
                            String Vote = parties[1];
                            if (Vote.length() == 1 && signatureEstValide)
                                textPseudoJ1Signature.setText(joueur1 + " a voté " + Vote);
                            else
                                throw new Exception("Vote invalide");
                        }catch(Exception e){

                            textPseudoJ1Signature.setTextColor(Color.parseColor(Couleurs[2]));
                        }
                    }else{
                        textPseudoJ1Signature.setTextColor(Color.parseColor(Couleurs[1]));
                    }

                    if (!voteJ2.equals("")) {
                        textPseudoJ2Signature.setTextColor(Color.parseColor(Couleurs[0]));
                        try{
                            boolean signatureEstValide = RSAPSS.decode(voteJ2,signatureJ2, RSAPSS.publicKeyFromString(ClefPubliqueJ2));

                            String[] parties = voteJ2.split("-");
                            String hashduVote = parties[0];
                            String Vote = parties[1];
                            if (Vote.length() == 1 && signatureEstValide)
                                textPseudoJ2Signature.setText(joueur2 + " a voté " + Vote);

                            else
                                throw new Exception("Vote invalide");


                        }catch(Exception e){

                            textPseudoJ2Signature.setTextColor(Color.parseColor(Couleurs[2]));
                        }
                    }else{
                        textPseudoJ2Signature.setTextColor(Color.parseColor(Couleurs[1]));
                    }
                    if (!voteArbitre.equals("")) {
                        textPseudoArbitreSignature.setTextColor(Color.parseColor(Couleurs[0]));
                        try{
                            boolean signatureEstValide = RSAPSS.decode(voteArbitre,signatureArbitre, RSAPSS.publicKeyFromString(ClefPubliqueArbitre));

                            String[] parties = voteArbitre.split("-");
                            String hashduVote = parties[0];
                            String Vote = parties[1];
                            if (Vote.length() == 1 && signatureEstValide)
                                textPseudoArbitreSignature.setText(arbitre + " a voté " + Vote);

                            else
                                throw new Exception("Vote invalide");


                        }catch(Exception e){

                            textPseudoArbitreSignature.setTextColor(Color.parseColor(Couleurs[2]));
                        }
                    }else{
                        textPseudoArbitreSignature.setTextColor(Color.parseColor(Couleurs[1]));
                    }

                   }

                   else
                   {
                       Toast.makeText(SignerPartieActivity.this, "Partie non trouvée ! ", Toast.LENGTH_LONG).show() ;
                   }
                }
            }, 1000);

        }catch(Exception e){}

        btnRetourSignerPartie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignerPartieActivity.this,ListePartiesASignerActivity.class);

                intent.putExtra("ClefPrivee", clefPrivee);
                intent.putExtra("ClefPublique", clefPublique);
                intent.putExtra("serveur",serveur);
                startActivity(intent);
            }
        });
        btnVoter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vote = "";
                String voteLettre = "";
                if (choixJ1.isChecked()){
                    vote = "J1";
                }
                else if (choixJ2.isChecked()){
                    vote = "J2";
                }
                else if (choixArbitre.isChecked()){
                    vote = "Arbitre";
                }
                else{
                    Toast.makeText(SignerPartieActivity.this, "Veuillez choisir un vote ! ", Toast.LENGTH_SHORT).show() ;
                }

                if (choixVictoire.isChecked()){
                    voteLettre = "V";
                }
                else if (choixDefaite.isChecked()){
                    voteLettre = "D";
                }
                else if (choixNul.isChecked()){
                    voteLettre = "N";
                }
                else{
                    Toast.makeText(SignerPartieActivity.this, "Veuillez choisir un Vote (Gagnant/Perdant) ! ", Toast.LENGTH_SHORT).show() ;
                }

                if (!vote.equals("")){
                    try {
                        String hashVote = hashPartie + "-" + voteLettre;
                        System.out.println("Hash du vote : " + hashVote);
                        System.out.println("Clef Privée : " + clefPrivee);
                        String signatureVote = RSAPSS.encode(hashVote, RSAPSS.privateKeyFromString(clefPrivee));
                        System.out.println("Clef Privée : " + clefPrivee);
                        // On va tenter de décder
                        System.out.println("Clef Publique: " + clefPublique);
                        System.out.println("Signature du message: " + signatureVote);

                        try{

                           boolean signatureEstValide = RSAPSS.decode(hashVote,signatureVote, RSAPSS.publicKeyFromString(clefPublique));
                            System.out.println("Signature est-t-elle valide ? : " + signatureEstValide);
                        }
                        catch(Exception e){
                            System.out.println("Erreur lors de la vérification de la signature");
                        }


                        System.out.println("On test de voter" + hashPartie);
                        ThreadClient.envoyerSignature(hashPartie, vote ,hashVote, signatureVote);
                     //   database.update("PARTIES", values, "HashPartie='" + hashPartie + "'",null);
                        //On try de déchiffrer le vote
                        Toast.makeText(SignerPartieActivity.this, "Vote enregistré ! ", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignerPartieActivity.this, ListePartiesASignerActivity.class);
                        intent.putExtra("HashPartie", hashPartie);
                        intent.putExtra("ClefPrivee", clefPrivee);
                        intent.putExtra("ClefPublique", clefPublique);
                        intent.putExtra("serveur",serveur);


                        startActivity(intent);
                    }
                    catch(Exception e){
                        Toast.makeText(SignerPartieActivity.this, "Erreur lors du vote ! " + e.getMessage(), Toast.LENGTH_SHORT).show() ;
                    }
                }
            }
        });
    }
}