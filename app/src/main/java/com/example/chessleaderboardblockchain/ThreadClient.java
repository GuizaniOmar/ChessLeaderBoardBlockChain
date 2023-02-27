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
                    client.login(madb, username, clefPublique, clefPriveeCryptee);

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
