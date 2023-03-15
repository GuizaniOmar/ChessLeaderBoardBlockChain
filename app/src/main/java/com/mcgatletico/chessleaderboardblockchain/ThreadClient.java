package com.mcgatletico.chessleaderboardblockchain;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.widget.SimpleCursorAdapter;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class ThreadClient {

    private static String[] voteActeurs =  {"voteJ1", "voteJ2", "voteArbitre"};
    private static String[] clefActeurs =  {"clefPubliqueJ1", "clefPubliqueJ2", "clefPubliqueArbitre"};
    private static String IP_Serveur = "93.115.97.128"; //IP du serveur distant

        public static String actualiseBaseDeDonnees(DatabaseHelper db) {
            final  DatabaseHelper madb = db;
            new Thread(new Runnable() {
                DatabaseHelper dbfinale = madb;
                @Override

                public void run() {
                    try {
                        test.liste_ips();
                    } catch (UnknownHostException e) {
                        throw new RuntimeException(e);
                    }
                        Client client = null;
                        try {
                            client = new Client(IP_Serveur, 52000);
                         //   Server server = new Server(52000);
                            client.getComptes(dbfinale);
                          //  client.json(dbfinale);

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
                    client = new Client(IP_Serveur, 52000);
                   // client = new Client("localhost", 52000);
                    client.ajouterCompte(dbfinale, username, clefPublique, clefPriveeCryptee,true);
                    //client.login(madb, username, clefPublique, clefPriveeCryptee);





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
                try {
                    test.liste_ips();
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
                Client client = null;
                try {
                    client = new Client(IP_Serveur, 52000);
                    //   Server server = new Server(52000);
                    client.sendComptes(dbfinale);
                    //  client.json(dbfinale);

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
            database.execSQL("INSERT INTO compte ('pseudo','clefPublique','clefPrivee') VALUES ('"+values[0]+"', '"+values[1]+"', '"+values[2]+"')");
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
                return "Partie déjà présente dans les parties ! ";
            }

            String[] values = {timestamp, hashPartie, clefPubliqueJ1, clefPubliqueJ2, clefPubliqueArbitre, voteJ1, voteJ2, voteArbitre, signatureJ1, signatureJ2, signatureArbitre, hashVote, signatureArbitreHashVote};
try {
    database.execSQL("INSERT INTO partie ('timestamp','hashPartie','clefPubliqueJ1','clefPubliqueJ2','clefPubliqueArbitre', 'voteJ1', 'voteJ2', 'voteArbitre', 'signatureJ1', 'signatureJ2', 'signatureArbitre', 'hashVote', 'signatureArbitreHashVote' ) VALUES ('" + values[0] + "', '" + values[1] + "', '" + values[2] + "'" + ", '" + values[3] + "'" + ", '" + values[4] + "'" + ", '" + values[5] + "'" + ", '" + values[6] + "'" + ", '" + values[7] + "'" + ", '" + values[8] + "'" + ", '" + values[9] + "'" + ", '" + values[10] + "'" + ", '" + values[11] + "'" + ", '" + values[12] + "')");
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
                    client = new Client(IP_Serveur, 52000);
                    // client = new Client("localhost", 52000);

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
                    client = new Client(IP_Serveur, 52000);
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
                    client = new Client(IP_Serveur, 52000);
                    // client = new Client("localhost", 52000);

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
