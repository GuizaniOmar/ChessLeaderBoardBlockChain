package com.mcgatletico.chessleaderboardblockchain;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.widget.SimpleCursorAdapter;

import java.io.IOException;
import java.net.UnknownHostException;

public class ThreadClient {

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
    public static String ajouterPartieARecevoir(DatabaseHelper db, String timestamp, String hashPartie, String clefPubliqueJ1, String clefPubliqueJ2,String clefPubliqueArbitre) {

        SQLiteDatabase database = db.getDatabase();
        String[] values = {timestamp, hashPartie, clefPubliqueJ1, clefPubliqueJ2, clefPubliqueArbitre};
        try {
            database.execSQL("INSERT INTO partieARecevoir ('timestamp','hashPartie','clefPubliqueJ1','clefPubliqueJ2','clefPubliqueArbitre', 'voteJ1', 'voteJ2', 'voteArbitre', 'signatureJ1', 'signatureJ2', 'signatureArbitre' ) VALUES ('"+values[0]+"', '"+values[1]+"', '"+values[2]+"'" + ", '"+values[3]+"'" + ", '"+values[4]+"'" + ",'','','','','','')");
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout de la partie à recevoir dans la table partieARecevoir");
        }
        return "Entrée validé";
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

    public static void main(String[] args) {

        }

}
