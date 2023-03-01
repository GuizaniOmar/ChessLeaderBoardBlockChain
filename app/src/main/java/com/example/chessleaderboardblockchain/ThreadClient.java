package com.example.chessleaderboardblockchain;

import java.io.IOException;

public class ThreadClient {


        public static String actualiseBaseDeDonnees(DatabaseHelper db) {
            final  DatabaseHelper madb = db;
            new Thread(new Runnable() {
                DatabaseHelper dbfinale = madb;
                @Override

                public void run() {

                        Client client = null;
                        try {
                            client = new Client("93.115.97.128", 52000);
                            client.json(dbfinale);

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
                    client = new Client("93.115.97.128", 52000);
                   // client = new Client("localhost", 52000);

                    client.login(madb, username, clefPublique, clefPriveeCryptee);
                    client.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
        return "Base de données reçues";
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
                    client = new Client("93.115.97.128", 52000);
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
                    client = new Client("93.115.97.128", 52000);
                    client.recupererPartiesASigner(dbfinale);
                    client.close(); // A la fin ça serra peut-être des clients connectés jusqu'à la fin?

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
        return "Base de données reçues";
    }

    public static void main(String[] args) {

        }

}
