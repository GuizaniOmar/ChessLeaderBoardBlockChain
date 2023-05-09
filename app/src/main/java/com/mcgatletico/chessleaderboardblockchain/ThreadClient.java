package com.mcgatletico.chessleaderboardblockchain;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.widget.SimpleCursorAdapter;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;

public class ThreadClient {

    private static String[] voteActeurs =  {"voteJ1", "voteJ2", "voteArbitre"};
    public static String IP = "192.168.1";
    public static String monIP = "192.168.1.1";
    private static String[] clefActeurs =  {"clefPubliqueJ1", "clefPubliqueJ2", "clefPubliqueArbitre"};
     //IP du serveur distant

    public static void lancerServeur(){
        new Thread(new Runnable() {

            @Override

            public void run() {


                try {

          Server server = new Server(CONS.PORT);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
    }
    public static void lancerClient(DatabaseHelper db){
        new Thread(new Runnable() {

            @Override

            public void run() {


                try {

                    MultiClientSocket multiClientSocket2 = MultiClientSocket.getInstance();
                    multiClientSocket2.db = db;
                    Server.db = db;


                    Thread threadClient2 = new Thread(multiClientSocket2);

                    threadClient2.start();
              //      multiClientSocket2.add("192.168.1.5", CONS.PORT);

                    PingIP.main();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
    }

        public static String actualiseBaseDeDonnees(DatabaseHelper db) {
            final  DatabaseHelper madb = db;
            new Thread(new Runnable() {
                DatabaseHelper dbfinale = madb;
                @Override

                public void run() {

                        Client client = null;
                        try {
                            client = new Client(CONS.IP_Serveur, CONS.PORT);

                            client.getComptes(dbfinale);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                }
            }).start();


            return "Base de données reçues";
        }
    public static String login(DatabaseHelper db, String username, String clefPublique, String clefPriveeCryptee) {

        final  DatabaseHelper madb = db;
        final  String username_final = username;
        final  String clefPublique_final = clefPublique;
        final  String clefPriveeCryptee_final = clefPriveeCryptee;


        new Thread(new Runnable() {
            DatabaseHelper dbfinale = madb;
            String username = username_final;
            String clefPublique = clefPublique_final;
           String clefPriveeCryptee = clefPriveeCryptee_final;
            @Override

            public void run() {

                Client client = null;
                try {
                    client = new Client(CONS.IP_Serveur, CONS.PORT);

                    client.ajouterCompte(dbfinale, username, clefPublique, clefPriveeCryptee,true);





                } catch (IOException e) {
                    throw new RuntimeException(e);
                }



            }
        }).start();
        //Handler().postDelayed

        return "Base de données reçues";
    }
    public static String envoyerComptes(DatabaseHelper db) {
        final  DatabaseHelper madb = db;
        new Thread(new Runnable() {
            DatabaseHelper dbfinale = madb;
            @Override

            public void run() {

                Client client = null;
                try {
                    client = new Client(CONS.IP_Serveur, CONS.PORT);

                    client.sendComptes(dbfinale);


                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();


        return "Base de données reçues";
    }

    public static String ajouterCompte(DatabaseHelper db, String username, String clefPublique, String clefPriveeCryptee) {

        SQLiteDatabase database = db.getDatabase();
        String[] values = {username, clefPublique, clefPriveeCryptee};
        try {
            database.execSQL("INSERT OR IGNORE INTO compte ('pseudo','clefPublique','clefPrivee') VALUES ('"+values[0]+"', '"+values[1]+"', '"+values[2]+"')");
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout du compte dans leaderboard");
        }
        return "Base de données reçues";
    }


    public static String ajouterPartieARecevoir(DatabaseHelper db, String timestamp, String hashPartie,String clefPubliqueArbitre, String clefPubliqueJ1, String clefPubliqueJ2) {

        SQLiteDatabase database = db.getDatabase();

        String[] values = {timestamp, hashPartie, clefPubliqueJ1, clefPubliqueJ2, clefPubliqueArbitre};
        try {
            database.execSQL("INSERT INTO partieARecevoir ('timestamp','hashPartie','clefPubliqueJ1','clefPubliqueJ2','clefPubliqueArbitre', 'voteJ1', 'voteJ2', 'voteArbitre', 'signatureJ1', 'signatureJ2', 'signatureArbitre' ) VALUES ('"+values[0]+"', '"+values[1]+"', '"+values[2]+"'" + ", '"+values[3]+"'" + ", '"+values[4]+"'" + ",'','','','','','')");
         }catch(Exception e){
            System.out.println("Erreur lors de l'ajout de la partie à recevoir dans la table partieARecevoir");
        }
        return "Entrée validé";
    }
    public static String ajouterPartieARecevoir(DatabaseHelper db, String timestamp, String hashPartie,String clefPubliqueArbitre, String clefPubliqueJ1, String clefPubliqueJ2,String voteArbitre,String voteJ1,String voteJ2,String signatureArbitre,String signatureJ1,String signatureJ2) {

        SQLiteDatabase database = db.getDatabase();

        try{
            Cursor e = database.rawQuery("SELECT * FROM partieARecevoir WHERE hashPartie = '"+hashPartie+"'", null);
            if (e.getCount() == 0) {
                database.execSQL("INSERT INTO partieARecevoir ('timestamp','hashPartie','clefPubliqueJ1','clefPubliqueJ2','clefPubliqueArbitre', 'voteJ1', 'voteJ2', 'voteArbitre', 'signatureJ1', 'signatureJ2', 'signatureArbitre' ) VALUES ('" + timestamp + "', '" + hashPartie + "', '" + clefPubliqueJ1 + "'" + ", '" + clefPubliqueJ2 + "'" + ", '" + clefPubliqueArbitre + "'" + ",'" + voteJ1 + "','" + voteJ2 + "','" + voteArbitre + "','" + signatureJ1 + "','" + signatureJ2 + "','" + signatureArbitre + "')");
            }else {
                System.out.println("Partie déjà reçue, on met à jour les votes et signatures");
                try {
                    if (voteArbitre != null) {

                        Cursor c = database.rawQuery("SELECT * FROM partieARecevoir WHERE hashPartie = '"+hashPartie+"' AND  voteArbitre=''", null);
                        //set voteArbitre and
                        if (c.getCount() > 0)
                        database.execSQL("UPDATE partieARecevoir SET voteArbitre = '" + voteArbitre + "', signatureArbitre = '" + signatureArbitre + "' WHERE hashPartie = '" + hashPartie + "'");

                           }
                    if (voteJ1 != null) {
                        Cursor c = database.rawQuery("SELECT * FROM partieARecevoir WHERE hashPartie = '"+hashPartie+"' AND  voteJ1=''", null);
                        if (c.getCount() > 0)
                        database.execSQL("UPDATE partieARecevoir SET voteJ1 = '" + voteJ1 + "', signatureJ1 = '" + signatureJ1 + "' WHERE hashPartie = '" + hashPartie + "'");
                    }
                    if (voteJ2 != null) {
                        Cursor c = database.rawQuery("SELECT * FROM partieARecevoir WHERE hashPartie = '"+hashPartie+"' AND  voteJ2=''", null);
                        if (c.getCount() > 0)
                        database.execSQL("UPDATE partieARecevoir SET voteJ2 = '" + voteJ2 + "', signatureJ2 = '" + signatureJ2 + "' WHERE hashPartie = '" + hashPartie + "'");
                    }

                        }catch(Exception e1){
                    System.out.println("Erreur lors de l'ajout de la partie à recevoir dans la table partieARecevoir");
                }

            }
        }catch(Exception e){

        }
        return "Entrée validé";
    }
    public static String ajouterPartieAEnvoyer(DatabaseHelper db, String timestamp, String hashPartie,String clefPubliqueArbitre, String clefPubliqueJ1, String clefPubliqueJ2,String voteArbitre,String voteJ1,String voteJ2,String signatureArbitre,String signatureJ1,String signatureJ2) {

        SQLiteDatabase database = db.getDatabase();

        try {
            database.execSQL("UPDATE partieAEnvoyer SET voteArbitre = COALESCE(voteArbitre, '" + voteArbitre + "'), voteJ1 = COALESCE(voteJ1, '" + voteJ1 + "'), voteJ2 = COALESCE(voteJ2, '" + voteJ2 + "'), signatureArbitre = COALESCE(signatureArbitre, '" + signatureArbitre + "'), signatureJ1 = COALESCE(signatureJ1, '" + signatureJ1 + "'), signatureJ2 = COALESCE(signatureJ2, '" + signatureJ2 + "') WHERE hashPartie = '" + hashPartie + "'");
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout de la partie à recevoir dans la table partieARecevoir");
        }
        return "Entrée validé";
    }

    public static String ajouterConfirmation(DatabaseHelper db, String hashPartie, String hashVote, String clefPublique, String signature)  {
        SQLiteDatabase database = db.getDatabase();
        // On va vérifier toutes les signatures !
        if ((!hashVote.equals(hashPartie + "-N")) && (!hashVote.equals(hashPartie + "-O")))
            return "Vote de la Partie incorrecte! ";
        try{
            boolean isSignatureCorrect = RSAPSS.decode(hashVote, signature, RSAPSS.publicKeyFromString(clefPublique));
            if (isSignatureCorrect) {
                Cursor c = database.rawQuery("SELECT * FROM confirmation WHERE hashPartie = '" + hashPartie + "' and clefPublique = '" + clefPublique + "'", null);
                if (c.getCount() > 0){
                    return "Confirmation déjà présente dans la base de donnée ! ";
                }

                String[] values = {hashPartie, hashVote, clefPublique, signature};
                try {
                    database.execSQL("INSERT INTO confirmation ('hashPartie', 'hashVote','clefPublique','signatureHashVote' ) VALUES ( '" + values[0] + "', '" + values[1] + "', '" + values[2] + "', '" + values[3] + "')");
                    return "Confirmation ajoutée ! ";
                } catch (Exception e) {
                    return "Confirmation non ajoutée - Problème SQL ! ";
                }



            }
            return isSignatureCorrect ? "Confirmation non ajoutée - Signature incorrecte. As-tu une clef privée ?! " : "Problème de signature, la vérification de la validité des signatures a échoué  !";
        }
        catch(Exception e) {
            return "Problème de signature - Confirmation non ajoutée ! ";
        }
    }
    public static String ajouterPlainte(DatabaseHelper db, String hashPartie, String hashVote, String clefPublique, String signature)  {
        SQLiteDatabase database = db.getDatabase();
        // On va vérifier toutes les signatures !
        if ((!hashVote.equals(hashPartie + "-N")) && (!hashVote.equals(hashPartie + "-O")))
            return "Plainte - Vote de la Partie incorrecte! ";
        try{
            boolean isSignatureCorrect = RSAPSS.decode(hashVote, signature, RSAPSS.publicKeyFromString(clefPublique));
            if (isSignatureCorrect) {
                Cursor c = database.rawQuery("SELECT * FROM plainte WHERE hashPartie = '" + hashPartie + "' and clefPublique = '" + clefPublique + "'", null);
                if (c.getCount() > 0){
                    return "Plainte  déjà présente dans la base de donnée ! ";
                }

                String[] values = {hashPartie, hashVote, clefPublique, signature};
                try {
                    database.execSQL("INSERT INTO plainte ('hashPartie', 'hashVote','clefPublique','signatureHashVote' ) VALUES ( '" + values[0] + "', '" + values[1] + "', '" + values[2] + "', '" + values[3] + "')");
                    return "Plainte ajoutée ! ";
                } catch (Exception e) {
                    return "Plainte non ajoutée - Problème SQL ! ";
                }



            }
            return isSignatureCorrect ? "plainte non ajoutée - Signature incorrecte. As-tu une clef privée ?! " : "Problème de signature, la vérification de la validité des signatures a échoué  !";
        }
        catch(Exception e) {
            return "Problème de signature - Plainte non ajoutée ! ";
        }
    }

    public static String ajouterPartie(DatabaseHelper db, String timestamp, String hashPartie, String clefPubliqueJ1, String clefPubliqueJ2,String clefPubliqueArbitre, String voteJ1, String voteJ2, String voteArbitre, String signatureJ1, String signatureJ2, String signatureArbitre, String hashVote, String signatureArbitreHashVote)  {
        SQLiteDatabase database = db.getDatabase();
        // On va vérifier toutes les signatures !
        try{
        boolean isSignatureJ1Correct = RSAPSS.decode(voteJ1, signatureJ1, RSAPSS.publicKeyFromString(clefPubliqueJ1));
        boolean isSignatureJ2Correct = RSAPSS.decode(voteJ2, signatureJ2, RSAPSS.publicKeyFromString(clefPubliqueJ2));
        boolean isSignatureArbitreCorrect = RSAPSS.decode(voteArbitre, signatureArbitre, RSAPSS.publicKeyFromString(clefPubliqueArbitre));
        boolean isSignatureHashVoteCorrect = RSAPSS.decode(hashVote, signatureArbitreHashVote, RSAPSS.publicKeyFromString(clefPubliqueArbitre));
        if (isSignatureJ1Correct && isSignatureJ2Correct && isSignatureArbitreCorrect && isSignatureHashVoteCorrect) {
            Cursor c = database.rawQuery("SELECT * FROM partie WHERE hashPartie = '" + hashPartie + "'", null);
            if (c.getCount() > 0){
                database.execSQL("DELETE FROM partieARecevoir WHERE hashPartie = '" + hashPartie + "'");
                return "Partie déjà présente dans les parties ! ";
            }

            String[] values = {timestamp, hashPartie, clefPubliqueJ1, clefPubliqueJ2, clefPubliqueArbitre, voteJ1, voteJ2, voteArbitre, signatureJ1, signatureJ2, signatureArbitre, hashVote, signatureArbitreHashVote};
try {
    database.execSQL("INSERT OR IGNORE INTO partie ('timestamp','hashPartie','clefPubliqueJ1','clefPubliqueJ2','clefPubliqueArbitre', 'voteJ1', 'voteJ2', 'voteArbitre', 'signatureJ1', 'signatureJ2', 'signatureArbitre', 'hashVote', 'signatureArbitreHashVote' ) VALUES ('" + values[0] + "', '" + values[1] + "', '" + values[2] + "'" + ", '" + values[3] + "'" + ", '" + values[4] + "'" + ", '" + values[5] + "'" + ", '" + values[6] + "'" + ", '" + values[7] + "'" + ", '" + values[8] + "'" + ", '" + values[9] + "'" + ", '" + values[10] + "'" + ", '" + values[11] + "'" + ", '" + values[12] + "')");
    database.execSQL("DELETE FROM partieARecevoir WHERE hashPartie = '" + hashPartie + "'");
    return "Partie ajoutée ! ";
} catch (Exception e) {
    return "Partie non ajoutée - Problème SQL ! ";
}



        }
            return isSignatureHashVoteCorrect ? "Partie non ajoutée - Signature des votes par l'arbitre est incorrecte. Es-tu l'arbitre ?! " : "Problème de signature, la vérification de la validité des signatures a échoué  !";
          }
        catch(Exception e) {
            return "Problème de signature - Partie non ajoutée ! ";
        }
    }

    @SuppressLint("Range")
    public static String updateLeaderboard(DatabaseHelper db)  {
        SQLiteDatabase database = db.getDatabase();
        // On va vérifier toutes les signatures !
        try{
            Cursor cPartieDetail = database.rawQuery("SELECT * FROM partieDetail", null);
             Cursor cPartie = database.rawQuery("SELECT * FROM partie", null);
            if (cPartieDetail.getCount() == cPartie.getCount()){
                return "Leaderboard déjà à jour ! ";
            }
            // On va supprimer toutes les parties du leaderboard
            database.execSQL("DELETE FROM partieDetail");
            database.execSQL("DELETE FROM leaderboard");
            // On va ajouter toutes les parties du leaderboard
            cPartie = database.rawQuery("SELECT partie.*," +   "compte1.Pseudo AS joueur1, " +
                    "compte2.Pseudo AS joueur2, " +
                    "compte3.Pseudo AS arbitre " +
                    "FROM partie " +
                    "LEFT JOIN compte AS compte1 ON partie.ClefPubliqueJ1 = compte1.clefPublique " +
                    "LEFT JOIN compte AS compte2 ON partie.ClefPubliqueJ2 = compte2.clefPublique " +
                    "LEFT JOIN compte AS compte3 ON partie.ClefPubliqueArbitre = compte3.clefPublique ORDER BY CAST(partie.timestamp AS DATETIME) ASC" , null);
            while(cPartie.moveToNext()){
                // On va traiter les données
                // on récupèrer le hashPartie,clefPubliqueJ1,clefPubliqueJ2,clefPubliqueArbitre,voteJ1,voteJ2,voteArbitre

                String hashPartie = cPartie.getString(cPartie.getColumnIndex("hashPartie")),
                        clefPubliqueJ1 = cPartie.getString(cPartie.getColumnIndex("clefPubliqueJ1")),
                        clefPubliqueJ2 = cPartie.getString(cPartie.getColumnIndex("clefPubliqueJ2")),
                        clefPubliqueArbitre = cPartie.getString(cPartie.getColumnIndex("clefPubliqueArbitre")),
                        voteJ1 = cPartie.getString(cPartie.getColumnIndex("voteJ1")),
                        voteJ2 = cPartie.getString(cPartie.getColumnIndex("voteJ2")),
                        voteArbitre = cPartie.getString(cPartie.getColumnIndex("voteArbitre")), joueur1 = cPartie.getString(cPartie.getColumnIndex("joueur1")), joueur2 = cPartie.getString(cPartie.getColumnIndex("joueur2")), arbitre = cPartie.getString(cPartie.getColumnIndex("arbitre"));

                // On ajoute les membres au leaderboard !
                database.execSQL("INSERT OR IGNORE INTO leaderboard ('clefPublique', 'pseudo', 'elo', 'coefficientArbitrage', 'scoreTotal', 'nbParties', 'nbVictoire', 'nbConfirmation', 'nbPartieArbitre', 'nbNul', 'nbDefaite') VALUES ('" + clefPubliqueJ1 + "', '" + joueur1 + "', '" + CONS.eloDepart + "', '" +CONS.coefficientArbitre+ "', '0', '0', '0', '0', '0', '0', '0')");

                database.execSQL("INSERT OR IGNORE INTO leaderboard ('clefPublique', 'pseudo', 'elo', 'coefficientArbitrage', 'scoreTotal', 'nbParties', 'nbVictoire', 'nbConfirmation', 'nbPartieArbitre', 'nbNul', 'nbDefaite') VALUES ('" + clefPubliqueJ2 + "', '" + joueur2 + "', '" + CONS.eloDepart + "', '" +CONS.coefficientArbitre+ "', '0', '0', '0', '0', '0', '0', '0')");
                database.execSQL("INSERT OR IGNORE INTO leaderboard ('clefPublique', 'pseudo', 'elo', 'coefficientArbitrage', 'scoreTotal', 'nbParties', 'nbVictoire', 'nbConfirmation', 'nbPartieArbitre', 'nbNul', 'nbDefaite') VALUES ('" + clefPubliqueArbitre + "', '" + arbitre + "', '" + CONS.eloDepart + "', '" +CONS.coefficientArbitre+ "', '0', '0', '0', '0', '0', '0', '0')");
                // ON récupère
                Cursor cLeaderboardJ1 = database.rawQuery("SELECT * FROM leaderboard WHERE clefPublique = '" + clefPubliqueJ1 + "'", null);
                Cursor cLeaderboardJ2 = database.rawQuery("SELECT * FROM leaderboard WHERE clefPublique = '" + clefPubliqueJ2 + "'", null);
                Cursor cLeaderboardArbitre = database.rawQuery("SELECT * FROM leaderboard WHERE clefPublique = '" + clefPubliqueArbitre + "'", null);

                int eloJ1 = 0, eloJ2 = 0, eloArbitre = 0;
                double arbitreCoefficient = 0,joueur1Coefficient = 0, joueur2Coefficient = 0;
                int nbPartieArbitree = 0;
                int nbPartieJ1 = 0, nbPartieJ2 = 0;
                int nbVictoireJ1 = 0, nbVictoireJ2 = 0;
                int nbNulJ1 = 0, nbNulJ2 = 0;
                int nbDefaiteJ1 = 0, nbDefaiteJ2 = 0;
                int nbConfirmationJ1 = 0, nbConfirmationJ2 = 0, nbConfirmationArbitre = 0;
                double scoreTotalJ1 = 0, scoreTotalJ2 = 0, scoreTotalArbitre = 0;


                if (cLeaderboardJ1.moveToFirst()){
                    eloJ1 = Integer.parseInt(cLeaderboardJ1.getString(cLeaderboardJ1.getColumnIndex("elo")));
                    nbPartieJ1 = Integer.parseInt(cLeaderboardJ1.getString(cLeaderboardJ1.getColumnIndex("nbParties")));
                    nbPartieJ1 += 1;
                    nbVictoireJ1 = Integer.parseInt(cLeaderboardJ1.getString(cLeaderboardJ1.getColumnIndex("nbVictoire")));
                    nbNulJ1 = Integer.parseInt(cLeaderboardJ1.getString(cLeaderboardJ1.getColumnIndex("nbNul")));
                    nbDefaiteJ1 = Integer.parseInt(cLeaderboardJ1.getString(cLeaderboardJ1.getColumnIndex("nbDefaite")));
                    Cursor cConfirmationJ1 = database.rawQuery("SELECT * FROM confirmation WHERE clefPublique = '" + clefPubliqueJ1 + "'", null);
                    nbConfirmationJ1 = cConfirmationJ1.getCount();
                    scoreTotalJ1 = Double.parseDouble(cLeaderboardJ1.getString(cLeaderboardJ1.getColumnIndex("scoreTotal")));
                    joueur1Coefficient = Double.parseDouble(cLeaderboardJ1.getString(cLeaderboardJ1.getColumnIndex("coefficientArbitrage")));

                }
                if (cLeaderboardJ2.moveToFirst()){
                    eloJ2 = Integer.parseInt(cLeaderboardJ2.getString(cLeaderboardJ2.getColumnIndex("elo")));
                    nbPartieJ2 = Integer.parseInt(cLeaderboardJ2.getString(cLeaderboardJ2.getColumnIndex("nbParties")));
                    nbPartieJ2 += 1;
                    nbVictoireJ2 = Integer.parseInt(cLeaderboardJ2.getString(cLeaderboardJ2.getColumnIndex("nbVictoire")));
                    nbNulJ2 = Integer.parseInt(cLeaderboardJ2.getString(cLeaderboardJ2.getColumnIndex("nbNul")));
                    nbDefaiteJ2 = Integer.parseInt(cLeaderboardJ2.getString(cLeaderboardJ2.getColumnIndex("nbDefaite")));
                    Cursor cConfirmationJ2 = database.rawQuery("SELECT * FROM confirmation WHERE clefPublique = '" + clefPubliqueJ2 + "'", null);
                    nbConfirmationJ2 = cConfirmationJ2.getCount();
                    scoreTotalJ2 = Double.parseDouble(cLeaderboardJ2.getString(cLeaderboardJ2.getColumnIndex("scoreTotal")));
                    joueur2Coefficient = Double.parseDouble(cLeaderboardJ2.getString(cLeaderboardJ2.getColumnIndex("coefficientArbitrage")));
                }
                if (cLeaderboardArbitre.moveToFirst()){
                    arbitreCoefficient = Double.parseDouble(cLeaderboardArbitre.getString(cLeaderboardArbitre.getColumnIndex("coefficientArbitrage")));
                    nbPartieArbitree = Integer.parseInt(cLeaderboardArbitre.getString(cLeaderboardArbitre.getColumnIndex("nbPartieArbitre")));
              nbPartieArbitree += 1;
                    Cursor cConfirmationArbitre = database.rawQuery("SELECT * FROM confirmation WHERE clefPublique = '" + clefPubliqueArbitre + "'", null);
                    nbConfirmationArbitre = cConfirmationArbitre.getCount();
                    eloArbitre = Integer.parseInt(cLeaderboardArbitre.getString(cLeaderboardArbitre.getColumnIndex("elo")));
                scoreTotalArbitre = Double.parseDouble(cLeaderboardArbitre.getString(cLeaderboardArbitre.getColumnIndex("scoreTotal")));
                }

                char dernierVoteJ1 = voteJ1.charAt(voteJ1.length() - 1),
                        dernierVoteJ2 = voteJ2.charAt(voteJ2.length() - 1),
                        dernierVoteArbitre = voteArbitre.charAt(voteArbitre.length() - 1);

// Compter les occurrences de chaque type de vote
                int nbVictoires = 0,
                        nbNuls = 0,
                        nbDefaites = 0;
                if (dernierVoteJ1 == 'V') nbVictoires++;
                else if (dernierVoteJ1 == 'N') nbNuls++;
                else nbDefaites++;

                if (dernierVoteJ2 == 'V') nbVictoires++;
                else if (dernierVoteJ2 == 'N') nbNuls++;
                else nbDefaites++;

                if (dernierVoteArbitre == 'V') nbVictoires++;
                else if (dernierVoteArbitre == 'N') nbNuls++;
                else nbDefaites++;

// Déterminer le vote majoritaire
                char voteMajoritaire = dernierVoteArbitre;
                if (nbVictoires > nbNuls && nbVictoires > nbDefaites) {
                    voteMajoritaire = 'V';

                } else if (nbNuls > nbDefaites) {
                    voteMajoritaire = 'N';
                } else if (nbDefaites > nbVictoires && nbDefaites > nbNuls) {
                    voteMajoritaire = 'D';
                }
// Vérifier si l'arbitre a voté contre la majorité
                database.execSQL("INSERT INTO partieDetail ('hashPartie', 'resultat', 'eloJ1', 'eloJ2', 'fiabiliteArbitre') VALUES ('" + hashPartie + "', '" + voteMajoritaire + "', '" + eloJ1 + "', '" + eloJ2 + "', '" + arbitreCoefficient + "')");

                if (voteMajoritaire != dernierVoteArbitre) {
                    arbitreCoefficient += CONS.punirArbitre;

                }else{
                    Cursor cConfirmation = database.rawQuery("SELECT * FROM confirmation WHERE hashPartie = '" + hashPartie + "'", null);
                    Cursor cPlainte = database.rawQuery("SELECT * FROM plainte WHERE hashPartie = '" + hashPartie + "'", null);

                    if (cConfirmation.getCount()>=cPlainte.getCount() && cConfirmation.getCount()>=CONS.nbMinConfirmation){
                        arbitreCoefficient += CONS.recompenserArbitre; // On récompense l'arbitre car la partie a reçues beaucoup de confirmation
                    }else if (cConfirmation.getCount()<cPlainte.getCount()){
                        arbitreCoefficient += CONS.punirArbitre; // On punie l'arbitre car la partie a reçues beaucoup de plaintes
                    }
                }

                /*
                CREATE TABLE "partieDetail" (
	"_id"	INTEGER NOT NULL UNIQUE,
	"hashPartie"	TEXT NOT NULL,
	"resultat"	TEXT NOT NULL,
	"eloJ1"	TEXT NOT NULL,
	"eloJ2"	TEXT NOT NULL,
	"fiabiliteArbitre"	REAL CHECK("fiabiliteArbitre" BETWEEN 0.0 AND 1.0),
	PRIMARY KEY("_id" AUTOINCREMENT)
);

                 */
                // On met à jour le coefficient d'arbitrage de l'arbitre et son nombre de partie arbitré
                database.execSQL("UPDATE leaderboard SET coefficientArbitrage = '" + arbitreCoefficient + "', nbPartieArbitre = '" + nbPartieArbitree + "' WHERE clefPublique = '" + clefPubliqueArbitre + "'");
// On ajoute partieDetail
                // Construire la chaîne de résultat
                String resultatPartie = String.valueOf(voteMajoritaire);
                double voteMajoritaireDecimal = 0.0;
                switch (voteMajoritaire) {
                    case 'V':
                        voteMajoritaireDecimal = 1.0;
                        nbVictoireJ1 += 1;
                        nbDefaiteJ2 += 1;
                        break;
                    case 'N':
                        voteMajoritaireDecimal = 0.5;
                        nbNulJ1 += 1;
                        nbNulJ2 += 1;
                        break;
                    case 'D':
                        voteMajoritaireDecimal = 0.0;
                        nbDefaiteJ1 += 1;
                        nbVictoireJ2 += 1;
                        break;
                    default:
                        break;
                }

                HashMap<String, Integer> nouveauElo = Elo.updateEloForSingleGame(joueur1, joueur2, eloJ1, eloJ2, voteMajoritaireDecimal);

                eloJ1 = nouveauElo.get(joueur1);
                eloJ2 = nouveauElo.get(joueur2);

                scoreTotalArbitre = arbitreCoefficient * eloArbitre;
                scoreTotalJ1 =  joueur1Coefficient* eloJ1;
                scoreTotalJ2 =  joueur2Coefficient* eloJ2;


                // On update le leaderboard avec la confirmation aussi
                System.out.println(" Joueur 1 les données sont : " + scoreTotalJ1 + " " + nbPartieJ1 + " " + nbVictoireJ1 + " " + nbNulJ1 + " " + nbDefaiteJ1 + " " + eloJ1 + " " + nbConfirmationJ1);
                database.execSQL("UPDATE leaderboard SET scoreTotal = '" + scoreTotalJ1 + "', nbParties = '" + nbPartieJ1 + "', nbVictoire = '" + nbVictoireJ1 + "', nbNul = '" + nbNulJ1 + "', nbDefaite = '" + nbDefaiteJ1 + "', elo = '" + eloJ1 + "', nbConfirmation = '" + nbConfirmationJ1 +  "' WHERE clefPublique = '" + clefPubliqueJ1 + "'");
                database.execSQL("UPDATE leaderboard SET scoreTotal = '" + scoreTotalJ2 + "', nbParties = '" + nbPartieJ2 + "', nbVictoire = '" + nbVictoireJ2 + "', nbNul = '" + nbNulJ2 + "', nbDefaite = '" + nbDefaiteJ2 + "', elo = '" + eloJ2 + "', nbConfirmation = '" + nbConfirmationJ2 +  "' WHERE clefPublique = '" + clefPubliqueJ2 + "'");
                database.execSQL("UPDATE leaderboard SET scoreTotal = '" + scoreTotalArbitre + "', nbPartieArbitre = '" + nbPartieArbitree + "', nbConfirmation = '" + nbConfirmationArbitre +  "' WHERE clefPublique = '" + clefPubliqueArbitre + "'");



            }
            return "Leaderboard updated ! ";
    }
        catch(Exception e) {
            e.printStackTrace();
            return "Problème de signature - Partie non ajoutée ! ";
        }
    }
    public static String envoyerPartie(String timestampPartie, String hashPartie,String ClefPubliqueJ1, String ClefPubliqueJ2, String ClefPubliqueArbitre){
            System.out.println("Envoyons la partie ! ");

        final  String timestampPartie_final = timestampPartie;
        final String hashPartie_final = hashPartie;
        final String ClefPubliqueJ1_final = ClefPubliqueJ1;
        final String ClefPubliqueJ2_final = ClefPubliqueJ2;
        final String ClefPubliqueArbitre_final = ClefPubliqueArbitre;



        new Thread(new Runnable() {
            String timestampPartie = timestampPartie_final;
            String hashPartie = hashPartie_final;
            String ClefPubliqueJ1 = ClefPubliqueJ1_final;
            String ClefPubliqueJ2 = ClefPubliqueJ2_final;
            @Override

            public void run() {

                Client client = null;
                try {
                    client = new Client(CONS.IP_Serveur, CONS.PORT);


                    client.envoyerPartie(timestampPartie, hashPartie, ClefPubliqueJ1, ClefPubliqueJ2, ClefPubliqueArbitre);
                    client.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
            return "Coucou";
    }

    public static String recevoirListeParties(DatabaseHelper db) {
        final  DatabaseHelper madb = db;
        new Thread(new Runnable() {
            DatabaseHelper dbfinale = madb;
            @Override

            public void run() {

                Client client = null;
                try {
                    client = new Client(CONS.IP_Serveur, CONS.PORT);
                    client.recupererPartiesASigner(dbfinale);
                    client.close(); // A la fin ça serra peut-être des clients connectés jusqu'à la fin?

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
        return "Base de données reçues";
    }

    public static String ajouterSignature(DatabaseHelper db,String hashPartie,String acteurPartie, String vote, String signaturePartie){
        System.out.println("Envoyons la signature ! ");

        SQLiteDatabase database = db.getDatabase();
        System.out.println(" Données réçues de la méthode " + hashPartie + " " + acteurPartie + " " + vote + " " + signaturePartie);
        if (isVoteActeur("vote"+acteurPartie)){
            System.out.println("L'acteur est correct ! ");
Cursor c = database.rawQuery("SELECT * FROM partieARecevoir WHERE hashPartie = '"+hashPartie+"'", null);
            if (c.moveToNext()) {
                System.out.println("Partie trouvé dans la bdd est correct ! ");
                String colonne_vote = getClefActeur("vote" + acteurPartie);
                if (!(colonne_vote == null)) {
                    System.out.println("Colonne est trouvé dans la bdd est correct ! ");
                    @SuppressLint("Range") String clefPublique = c.getString(c.getColumnIndex(colonne_vote));
                    try {
                        boolean voteisCorrect = RSAPSS.decode(vote,signaturePartie, RSAPSS.publicKeyFromString(clefPublique));
                        if (voteisCorrect) {

                            database.execSQL("UPDATE partieARecevoir SET signature" + acteurPartie + " = '" + signaturePartie + "', vote" + acteurPartie + " = '" + vote + "' WHERE hashPartie = '" + hashPartie + "'");
                        }else{
                            System.out.println("Le vote n'est pas correct ! Qui es-tu ?");
                        }
                    } catch (Exception e) {
                        System.out.println("Erreur lors de l'ajout du vote dans la table partieARecevoir");
                    }
                }
            }
        }

        return "Signature ajoutée !";
    }

    public static String envoyerSignature(String hashPartie,String acteurPartie, String Vote, String signaturePartie){
        System.out.println("Envoyons la signature ! ");

        final  String hashPartie_final = hashPartie;
        final String acteurPartie_final = acteurPartie;
        final String signaturePartie_final = signaturePartie;
        final String Vote_final = Vote;


        new Thread(new Runnable() {
            String hashPartie = hashPartie_final;
            String acteurPartie = acteurPartie_final;
            String signaturePartie = signaturePartie_final;
            String vote = Vote_final;

            @Override

            public void run() {

                Client client = null;
                try {
                    client = new Client(CONS.IP_Serveur, CONS.PORT);

                    client.ajouterSignature(hashPartie, acteurPartie,vote, signaturePartie);
                    client.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
        return "Coucou";
    }

    private static boolean isVoteActeur(String voteActeur) {
        return Arrays.asList(voteActeurs).contains(voteActeur);
    }

    private static String getClefActeur(String voteActeur) {
        if (Arrays.asList(voteActeurs).contains(voteActeur)) // on autorise uniquement les votes de J1, J2 et Arbitre
            return clefActeurs[Arrays.asList(voteActeurs).indexOf(voteActeur)];
        else
            return null;
    }
    public static void main(String[] args) {

        }

}
