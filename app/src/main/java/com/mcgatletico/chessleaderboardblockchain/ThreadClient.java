package com.mcgatletico.chessleaderboardblockchain;

import java.io.IOException;
import java.net.UnknownHostException;

public class ThreadClient {
        //private static String IP_Serveur = "93.115.97.128";
    //91.178.73.121
    private static String IP_Serveur = "192.168.1.57";

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
                    client = new Client(IP_Serveur, 52000);
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


    public static String envoyerSignature(String hashPartie,String acteurPartie, String signaturePartie){
        System.out.println("Envoyons la signature ! ");

        final  String hashPartie_final = hashPartie;
        final String acteurPartie_final = acteurPartie;
        final String signaturePartie_final = signaturePartie;



        new Thread(new Runnable() {
            String hashPartie = hashPartie_final;
            String acteurPartie = acteurPartie_final;
            String signaturePartie = signaturePartie_final;

            @Override

            public void run() {

                Client client = null;
                try {
                    client = new Client(IP_Serveur, 52000);
                    // client = new Client("localhost", 52000);

                    client.ajouterSignature(hashPartie, acteurPartie, signaturePartie);
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
