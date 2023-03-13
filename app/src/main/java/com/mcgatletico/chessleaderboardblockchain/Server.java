package com.mcgatletico.chessleaderboardblockchain;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    private Map<String, DataOutputStream> clients;
    private ServerSocket serverSocket;
    private static String hashServer = null;
    public static DatabaseHelper db;

    public Server(int port) throws IOException {
        clients = new HashMap<>();

        int backlog = 200;
        ServerSocket serverSocket = new ServerSocket(port, backlog);

        // serverSocket = new ServerSocket(port);



        System.out.println("Le serveur est en écoute sur le port " + port + "...");
        new Thread(() -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Ip : " + clientSocket.getInetAddress().getHostAddress() + " a rejoint le serveur");

                    new Thread(new ClientHandler(clientSocket)).start();
                } catch (Exception e) {
                    System.out.println("Serveur: La connexion n'a pas pu être accepté");
                    //  e.printStackTrace();
                }
            }
        }).start();
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private DataInputStream inputStream;
        private DataOutputStream outputStream;

        private String hashClient = null; //Hash du client

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            try {
                while (true) {
                    int type = inputStream.readInt();
                    switch (type) {
                        case 0:
                            handleMessage();
                            break;
                        case 1:
                            handleLogin();
                            break;

                        case 20:
                            handleComptes();
                            break;

                        case 21:
                            handleConfirmations();
                            break;
                        case 22:
                            handleParties();
                            break;
                        case 23:
                            handlePartiesARecevoir();
                            break;
                        case 24:
                            handlePartiesAEnvoyer();
                            break;
                        case 25:
                            handlePlaintes();
                            break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Erreur de connexion #2");
                // e.printStackTrace();
            }
        }
        private void handleMessage() throws IOException {
            // On va tenter de rajouter le compte dans la base de données...


            String username = inputStream.readUTF();
            System.out.println(" ---- précédent hash du client  :  " + hashClient);
            System.out.println("Le hash du client a été enregistré à  :  " + username);
            hashClient = username;

            System.out.println("Le hash du client est  :  " + hashClient);

            outputStream.writeUTF("Le message a bien été recue " + username);

            outputStream.flush();
        }
        private void handleLogin() throws IOException {
            // On va tenter de rajouter le compte dans la base de données...


            String username = inputStream.readUTF();
            String clefPublique = inputStream.readUTF();
            String clefPriveeCryptee = inputStream.readUTF();
            System.out.println("Connexion de l'utilisateur " + username);
            System.out.println("ClefPublique: " + clefPublique);
            SQLiteDatabase database = db.getDatabase();

            String[] values = {username, clefPublique,clefPriveeCryptee};
            try {
                database.execSQL("INSERT INTO compte ('pseudo','clefPublique','clefPrivee') VALUES ('"+values[0]+"', '"+values[1]+"', '"+values[2]+"')");
                outputStream.writeUTF("Ajout du compte réussi ! ");
            }catch(Exception e){

                outputStream.writeUTF("Ajout du compte raté ! " + e.getMessage());
            }
            // ON VERIFIE SI DANS LA BASE DE DONNEES y'a un compte avec le même pseudo
            Cursor result =database.rawQuery("SELECT * FROM compte WHERE pseudo='" + username + "'",null);
            try {
                if (result.moveToNext()) {
                    outputStream.writeUTF("Connexion réussie");
                }
            } catch (Exception e) {
                outputStream.writeUTF("Le compte '" + username + "' n'existe pas");

            }

            outputStream.flush();

        }

        @SuppressLint("Range")
        private void handleComptes() throws IOException {

            // On créer une base de données et on renvoit la liste des données !
            SQLiteDatabase database = db.getDatabase();


            System.out.println("Json reçu de quelqu'un");

            //  outputStream.writeInt(20);



            // On prépare le hash
            String contenue = "";
            try {
                Cursor result = database.rawQuery("SELECT _id,pseudo,clefPublique FROM compte",null);

                try {
                    int i=0;
                    outputStream.writeUTF("start");
                    outputStream.flush();
                    while (result.moveToNext()) {

                        Map<String, Object> map2 = new HashMap<>();

                        map2.put("id", result.getInt(result.getColumnIndex("_id")));
                        map2.put("pseudo", result.getString(result.getColumnIndex("pseudo")));;
                        map2.put("clefPublique", result.getString(result.getColumnIndex("clefPublique")));
                        System.out.println("i:"+i+ " " + Json.serialize(map2));
                        if ((Json.serialize(map2).length() + contenue.length()) > 32000) // Si le paquet est + gros que la capacité maximale par envoie de paquet
                        {
                            outputStream.writeUTF(contenue);

                            outputStream.flush();
                            contenue = "";
                        }
                        contenue += "|" + Json.serialize(map2) + "|";

                        i++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.out.println("Erreur PseudoClefs");
            }

            if (contenue.length() > 0)
                outputStream.writeUTF(contenue);


            outputStream.writeUTF("stop");
            outputStream.flush();


            // Provisoire après ça sera amélioré


        }
        @SuppressLint("Range")
        private void handleConfirmations() throws IOException {

            // On créer une base de données et on renvoit la liste des données !
            SQLiteDatabase database = db.getDatabase();

            System.out.println("Demande de liste de confirmation reçues");

            //  outputStream.writeInt(20);



            // On prépare le hash
            String contenue = "";
            try {
                Cursor result = database.rawQuery("SELECT _id,hashPartie,hashVote,clefPublique,signatureHashVote FROM confirmation",null);

                try {
                    int i=0;
                    outputStream.writeUTF("start");
                    outputStream.flush();
                    while (result.moveToNext()) {

                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("id", result.getInt(result.getColumnIndex("_id")));
                        map2.put("hashPartie", result.getString(result.getColumnIndex("hashPartie")));
                        map2.put("hashVote", result.getString(result.getColumnIndex("hashVote")));
                        map2.put("clefPublique", result.getString(result.getColumnIndex("clefPublique")));
                        map2.put("signatureHashVote", result.getString(result.getColumnIndex("signatureHashVote")));

                        System.out.println("i:"+i+ " " + Json.serialize(map2));
                        if ((Json.serialize(map2).length() + contenue.length()) > 32000) // Si le paquet est + gros que la capacité maximale par envoie de paquet
                        {
                            outputStream.writeUTF(contenue);

                            outputStream.flush();
                            contenue = "";
                        }
                        contenue += "|" + Json.serialize(map2) + "|";

                        i++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.out.println("Erreur Confirmation");
            }

            if (contenue.length() > 0)
                outputStream.writeUTF(contenue);


            outputStream.writeUTF("stop");
            outputStream.flush();


        }
        @SuppressLint("Range")
        private void handlePlaintes() throws IOException {

            // On créer une base de données et on renvoit la liste des données !
            SQLiteDatabase database = db.getDatabase();

            System.out.println("Demande de liste de confirmation reçues");

            //  outputStream.writeInt(20);



            // On prépare le hash
            String contenue = "";
            try {
                Cursor result = database.rawQuery("SELECT _id,hashPartie,hashVote,clefPublique,signatureHashVote FROM plainte",null);

                try {
                    int i=0;
                    outputStream.writeUTF("start");
                    outputStream.flush();
                    while (result.moveToNext()) {

                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("id", result.getInt(result.getColumnIndex("_id")));
                        map2.put("hashPartie", result.getString(result.getColumnIndex("hashPartie")));
                        map2.put("hashVote", result.getString(result.getColumnIndex("hashVote")));
                        map2.put("clefPublique", result.getString(result.getColumnIndex("clefPublique")));
                        map2.put("signatureHashVote", result.getString(result.getColumnIndex("signatureHashVote")));

                        System.out.println("i:"+i+ " " + Json.serialize(map2));
                        if ((Json.serialize(map2).length() + contenue.length()) > 32000) // Si le paquet est + gros que la capacité maximale par envoie de paquet
                        {
                            outputStream.writeUTF(contenue);

                            outputStream.flush();
                            contenue = "";
                        }
                        contenue += "|" + Json.serialize(map2) + "|";

                        i++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.out.println("Erreur Plainte");
            }

            if (contenue.length() > 0)
                outputStream.writeUTF(contenue);


            outputStream.writeUTF("stop");
            outputStream.flush();

        }

        @SuppressLint("Range")
        private void handleParties() throws IOException {

            // On créer une base de données et on renvoit la liste des données !
            SQLiteDatabase database = db.getDatabase();

            System.out.println("Demande de liste de parties reçues");

            //  outputStream.writeInt(20);



            // On prépare le hash
            String contenue = "";
            try {
                Cursor result = database.rawQuery("SELECT _id,timestamp,hashPartie,clefPubliqueJ1,clefPubliqueJ2,clefPubliqueArbitre,voteJ1,voteJ2,voteArbitre,signatureJ1,signatureJ2,signatureArbitre,hashVote FROM partie",null);
                try {
                    int i=0;
                    outputStream.writeUTF("start");
                    outputStream.flush();
                    while (result.moveToNext()) {

                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("id", result.getInt(result.getColumnIndex("_id")));
                        map2.put("timestamp", result.getString(result.getColumnIndex("timestamp")));
                        map2.put("hashPartie", result.getString(result.getColumnIndex("hashPartie")));
                        map2.put("clefPubliqueJ1", result.getString(result.getColumnIndex("clefPubliqueJ1")));
                        map2.put("clefPubliqueJ2", result.getString(result.getColumnIndex("clefPubliqueJ2")));
                        map2.put("clefPubliqueArbitre", result.getString(result.getColumnIndex("clefPubliqueArbitre")));
                        map2.put("voteJ1", result.getString(result.getColumnIndex("voteJ1")));
                        map2.put("voteJ2", result.getString(result.getColumnIndex("voteJ2")));
                        map2.put("voteArbitre", result.getString(result.getColumnIndex("voteArbitre")));
                        map2.put("signatureJ1", result.getString(result.getColumnIndex("signatureJ1")));
                        map2.put("signatureJ2", result.getString(result.getColumnIndex("signatureJ2")));
                        map2.put("signatureArbitre", result.getString(result.getColumnIndex("signatureArbitre")));
                        map2.put("hashVote", result.getString(result.getColumnIndex("hashVote")));


                        // On ne se partage pas les comptes !      map2.put("ClefPriveeCryptee", result.getString("ClefPriveeCryptee"));
                        System.out.println("i:"+i+ " " + Json.serialize(map2));
                        if ((Json.serialize(map2).length() + contenue.length()) > 32000) // Si le paquet est + gros que la capacité maximale par envoie de paquet
                        {
                            outputStream.writeUTF(contenue);

                            outputStream.flush();
                            contenue = "";
                        }
                        contenue += "|" + Json.serialize(map2) + "|";

                        i++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.out.println("Erreur Parties");
            }

            if (contenue.length() > 0)
                outputStream.writeUTF(contenue);


            outputStream.writeUTF("stop");
            outputStream.flush();


            // Provisoire après ça sera amélioré


        }
        @SuppressLint("Range")
        private void handlePartiesAEnvoyer() throws IOException {

            // On créer une base de données et on renvoit la liste des données !
            SQLiteDatabase database = db.getDatabase();

            System.out.println("Demande de liste de parties reçues");

            //  outputStream.writeInt(20);



            // On prépare le hash
            String contenue = "";
            try {
                Cursor result = database.rawQuery("SELECT _id,timestamp,hashPartie,clefPubliqueJ1,clefPubliqueJ2,clefPubliqueArbitre,voteJ1,voteJ2,voteArbitre,signatureJ1,signatureJ2,signatureArbitre FROM partieAEnvoyer",null);
                try {
                    int i=0;
                    outputStream.writeUTF("start");
                    outputStream.flush();
                    while (result.moveToNext()) {

                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("id", result.getInt(result.getColumnIndex("_id")));
                        map2.put("timestamp", result.getString(result.getColumnIndex("timestamp")));
                        map2.put("hashPartie", result.getString(result.getColumnIndex("hashPartie")));
                        map2.put("clefPubliqueJ1", result.getString(result.getColumnIndex("clefPubliqueJ1")));
                        map2.put("clefPubliqueJ2", result.getString(result.getColumnIndex("clefPubliqueJ2")));
                        map2.put("clefPubliqueArbitre", result.getString(result.getColumnIndex("clefPubliqueArbitre")));
                        map2.put("voteJ1", result.getString(result.getColumnIndex("voteJ1")));
                        map2.put("voteJ2", result.getString(result.getColumnIndex("voteJ2")));
                        map2.put("voteArbitre", result.getString(result.getColumnIndex("voteArbitre")));
                        map2.put("signatureJ1", result.getString(result.getColumnIndex("signatureJ1")));
                        map2.put("signatureJ2", result.getString(result.getColumnIndex("signatureJ2")));
                        map2.put("signatureArbitre", result.getString(result.getColumnIndex("signatureArbitre")));


                        // On ne se partage pas les comptes !      map2.put("ClefPriveeCryptee", result.getString("ClefPriveeCryptee"));
                        System.out.println("i:"+i+ " " + Json.serialize(map2));
                        if ((Json.serialize(map2).length() + contenue.length()) > 32000) // Si le paquet est + gros que la capacité maximale par envoie de paquet
                        {
                            outputStream.writeUTF(contenue);

                            outputStream.flush();
                            contenue = "";
                        }
                        contenue += "|" + Json.serialize(map2) + "|";

                        i++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.out.println("Erreur PartiesAEnvoyer");
            }

            if (contenue.length() > 0)
                outputStream.writeUTF(contenue);


            outputStream.writeUTF("stop");
            outputStream.flush();


            // Provisoire après ça sera amélioré

        }
        @SuppressLint("Range")
        private void handlePartiesARecevoir() throws IOException {

            // On créer une base de données et on renvoit la liste des données !
            SQLiteDatabase database = db.getDatabase();

            System.out.println("Demande de liste de parties reçues");

            //  outputStream.writeInt(20);



            // On prépare le hash
            String contenue = "";
            try {
                Cursor result = database.rawQuery("SELECT _id,timestamp,hashPartie,clefPubliqueJ1,clefPubliqueJ2,clefPubliqueArbitre,voteJ1,voteJ2,voteArbitre,signatureJ1,signatureJ2,signatureArbitre FROM partieARecevoir",null);
                try {
                    int i=0;
                    outputStream.writeUTF("start");
                    outputStream.flush();
                    while (result.moveToNext()) {

                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("id", result.getInt(result.getColumnIndex("_id")));
                        map2.put("timestamp", result.getString(result.getColumnIndex("timestamp")));
                        map2.put("hashPartie", result.getString(result.getColumnIndex("hashPartie")));
                        map2.put("clefPubliqueJ1", result.getString(result.getColumnIndex("clefPubliqueJ1")));
                        map2.put("clefPubliqueJ2", result.getString(result.getColumnIndex("clefPubliqueJ2")));
                        map2.put("clefPubliqueArbitre", result.getString(result.getColumnIndex("clefPubliqueArbitre")));
                        map2.put("voteJ1", result.getString(result.getColumnIndex("voteJ1")));
                        map2.put("voteJ2", result.getString(result.getColumnIndex("voteJ2")));
                        map2.put("voteArbitre", result.getString(result.getColumnIndex("voteArbitre")));
                        map2.put("signatureJ1", result.getString(result.getColumnIndex("signatureJ1")));
                        map2.put("signatureJ2", result.getString(result.getColumnIndex("signatureJ2")));
                        map2.put("signatureArbitre", result.getString(result.getColumnIndex("signatureArbitre")));


                        // On ne se partage pas les comptes !      map2.put("ClefPriveeCryptee", result.getString("ClefPriveeCryptee"));
                        System.out.println("i:"+i+ " " + Json.serialize(map2));
                        if ((Json.serialize(map2).length() + contenue.length()) > 32000) // Si le paquet est + gros que la capacité maximale par envoie de paquet
                        {
                            outputStream.writeUTF(contenue);

                            outputStream.flush();
                            contenue = "";
                        }
                        contenue += "|" + Json.serialize(map2) + "|";

                        i++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.out.println("Erreur PartiesARecevoir");
            }

            if (contenue.length() > 0)
                outputStream.writeUTF(contenue);


            outputStream.writeUTF("stop");
            outputStream.flush();



        }



    }
}