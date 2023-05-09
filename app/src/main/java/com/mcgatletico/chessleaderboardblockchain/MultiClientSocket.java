package com.mcgatletico.chessleaderboardblockchain;

import android.database.sqlite.SQLiteDatabase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiClientSocket implements Runnable {

    private static List<Socket> sockets;
    private static Map<Socket, ClientInfo> serverInfo;
    public static int count = 0;
    private static String hashClient = null;
    public static boolean isRunning = false;
    public static DatabaseHelper db;

    private static MultiClientSocket instance = null;

    public static synchronized MultiClientSocket getInstance() { // MultiClientSocket is a singleton
        if (instance == null) {
            instance = new MultiClientSocket();
        }
        return instance;
    }

    public static synchronized boolean instanceExistante() {
        return instance != null;
    }

    public void add(String serverAddress, int serverPort) throws IOException, IOException {
        boolean isAlreadyConnected = false;
        for (ClientInfo info : serverInfo.values()) {
            if (info.getIpAddress().equals(serverAddress) && info.getPort() == serverPort) {
                isAlreadyConnected = true;
                break;
            }
        }
        if (!isAlreadyConnected) {
            System.out.println("Connexion au serveur " + serverAddress + ":" + serverPort + "...");
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Connexion établie !");
            sockets.add(socket);
            ClientInfo clientInfo = new ClientInfo(serverAddress, serverPort);
            // Ajouter une méthode pour récuperer le hash du serveur
            serverInfo.put(socket, clientInfo);
            count +=1;
        }
    }

    public void setServerInfo(Socket socket, ClientInfo clientinfo) {
        serverInfo.put(socket, clientinfo);
    }

    MultiClientSocket() {
        sockets = new ArrayList<>();
        serverInfo = new HashMap<>();
        System.out.println("Le multiclient est initialisé ! ");

        // On tourne une boucle...
    }

    @Override
    public void run() {
        if (!isRunning) {
            isRunning = true;
            if (true) {
                System.out.println("hash du client en boucle" + hashClient);


                try {
                    Thread.sleep(5000);
                    try {
                        demanderClefsPubliques();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }




                System.out.println("Nombre de connexions: " + sockets.size());
                // Autres actions à effectuer dans la boucle infinie

            }
        }
    }

    public void demanderClefsPubliques() throws IOException {
        // Etape 1 demander les comptes
        System.out.println("Etape 1 demander les comptes" + count);
        for (Socket socket : sockets) {

          //  Socket socketx = new Socket(socket.getInetAddress().getHostAddress(), socket.getPort());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            outputStream.writeInt(20);
            outputStream.flush();
            System.out.println("On a envoyé la demande de clé publique  à  " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " ! ");

            String message = inputStream.readUTF();
            System.out.println("REPONSE CLEF PUBLIQUE " + message);

            if (message.equals("start")) {
                System.out.println("On a recu START clé publique  à  " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " ! ");

                String msg = inputStream.readUTF();
                while (!msg.equals("stop")) {
                    //JSON parser pour récuperer les clés publiques

                    List<String> list = Json.extraireMots(msg);
                    int c;
                    for (c = 0; c < list.size(); c++) {
                        //     System.out.println("Element " + c + " : " + list.get(c));
                        // On tente d'ajouter l'élement dans une base de données !
                        Map<String, Object> obj = (Map<String, Object>) Json.deserialize(list.get(c));
                        String pseudo = (String) obj.get("pseudo");
                       String clefPrivee = (String) obj.get("clefPrivee");
                        String clefPublique = (String) obj.get("clefPublique");
                        System.out.println("On est bon?!");
                        if ((pseudo != null) && (clefPublique != null)) {
                            ThreadClient.ajouterCompte(db, pseudo, clefPublique,clefPrivee);
                            System.out.println("pseudo: " + pseudo + " clefpublique: " + clefPublique);
                        } else {
                            System.out.println("ERREUR ! " + "pseudo: " + pseudo + " clefpublique: " + clefPublique);
                        }
                        // CODE POUR RAJOUTER A LA BASE DE DONNEES...
                    }

                    msg = inputStream.readUTF();
                    System.out.println("On a recu " + msg + " clé publique  à  " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " ! ");

                }


                System.out.println("Serveur nous envoie : " + message);
            }
            System.out.println("Serveur nous envoie : " + message);
        }
        System.out.println("On va demander la partie maintenant !");
        demanderParties();
    }

    public void demanderConfirmations() throws IOException {
        // Etape 1 demander les confirmations
        for (Socket socket : sockets) {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            outputStream.writeInt(21);//20 = demande de clé publique
            outputStream.flush();
            System.out.println("On a envoyé la demande de liste de confirmation ! ");

            String message = inputStream.readUTF();
            if (message.equals("start")) {
                String msg = inputStream.readUTF();
                while (!msg.equals("stop")) {
                    //JSON parser pour récuperer les clés publiques

                    List<String> list = Json.extraireMots(msg);
                    int c;
                    for (c = 0; c < list.size(); c++) {
                        //     System.out.println("Element " + c + " : " + list.get(c));
                        // On tente d'ajouter l'élement dans une base de données !
                        Map<String, Object> obj = (Map<String, Object>) Json.deserialize(list.get(c));
                        String hashPartie = (String) obj.get("hashPartie");
                        String hashVote = (String) obj.get("hashVote");
                        String clefPublique = (String) obj.get("clefPublique");
                        String signatureHashVote = (String) obj.get("signatureHashVote");


                        System.out.println("Traitement reçues");
                        if ((hashPartie != null) && (clefPublique != null) && (hashVote != null) && (signatureHashVote != null)) {
                           ThreadClient.ajouterConfirmation(db,hashPartie,hashVote,clefPublique,signatureHashVote);
                            System.out.println("hashPartie: " + hashPartie + " clefpublique: " + clefPublique + " hashVote: " + hashVote + " signatureHashVote: " + signatureHashVote);
                        } else {
                            System.out.println("ERREUR ! " + "hashPartie: " + hashPartie + " clefpublique: " + clefPublique + " hashVote: " + hashVote + " signatureHashVote: " + signatureHashVote);
                        }
                        // CODE POUR RAJOUTER A LA BASE DE DONNEES...
                    }

                    msg = inputStream.readUTF();

                }

                System.out.println("Serveur nous envoie : " + message);

                demanderPlaintes();
            }
            System.out.println("Serveur nous envoie : " + message);
        }
    }
    public void demanderPlaintes() throws IOException {
        // Etape 1 demander les confirmations
        for (Socket socket : sockets) {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            outputStream.writeInt(21);//20 = demande de clé publique
            outputStream.flush();
            System.out.println("On a envoyé la demande de liste de confirmation ! ");

            String message = inputStream.readUTF();
            if (message.equals("start")) {
                String msg = inputStream.readUTF();
                while (!msg.equals("stop")) {
                    //JSON parser pour récuperer les clés publiques

                    List<String> list = Json.extraireMots(msg);
                    int c;
                    for (c = 0; c < list.size(); c++) {
                        //     System.out.println("Element " + c + " : " + list.get(c));
                        // On tente d'ajouter l'élement dans une base de données !
                        Map<String, Object> obj = (Map<String, Object>) Json.deserialize(list.get(c));
                        String hashPartie = (String) obj.get("hashPartie");
                        String hashVote = (String) obj.get("hashVote");
                        String clefPublique = (String) obj.get("clefPublique");
                        String signatureHashVote = (String) obj.get("signatureHashVote");


                        System.out.println("Traitement reçues");
                        if ((hashPartie != null) && (clefPublique != null) && (hashVote != null) && (signatureHashVote != null)) {
                            ThreadClient.ajouterPlainte(db,hashPartie,hashVote,clefPublique,signatureHashVote);

                            System.out.println("hashPartie: " + hashPartie + " clefpublique: " + clefPublique + " hashVote: " + hashVote + " signatureHashVote: " + signatureHashVote);
                        } else {
                            System.out.println("ERREUR ! " + "hashPartie: " + hashPartie + " clefpublique: " + clefPublique + " hashVote: " + hashVote + " signatureHashVote: " + signatureHashVote);
                        }
                        // CODE POUR RAJOUTER A LA BASE DE DONNEES...
                    }

                    msg = inputStream.readUTF();

                }
                System.out.println("Serveur nous envoie : " + message);
            }
            System.out.println("Serveur nous envoie : " + message);
        }

        try {
            Thread.sleep(10000); // Attendre 1 seconde entre chaque itération
            demanderClefsPubliques();
        } catch (InterruptedException e) {
            // Gérer l'exception si nécessaire
        }
    }

    public void demanderParties() throws IOException {

        System.out.println("Demande de parties vers Serveur" + count);
        // Etape 1 demander les confirmations
        for (Socket socket : sockets) {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            outputStream.writeInt(22);//22 = demande de parties
            outputStream.flush();
            System.out.println("On a envoyé la demande de liste de confirmation ! ");

            String message = inputStream.readUTF();
            System.out.println("Demande de Parties, réponse  : " + message);
            if (message.equals("start")) {
                String msg = inputStream.readUTF();
                System.out.println("Demande de Parties, réponse#2  : " + msg);
                while (!msg.equals("stop")) {
                    //JSON parser pour récuperer les clés publiques

                    List<String> list = Json.extraireMots(msg);
                    int c;
                    System.out.println("Size:" + list.size());
                    for (c = 0; c < list.size(); c++) {
                        //     System.out.println("Element " + c + " : " + list.get(c));
                        // On tente d'ajouter l'élement dans une base de données !
                        Map<String, Object> obj = (Map<String, Object>) Json.deserialize(list.get(c));
                        String timestamp = (String) obj.get("timestamp");
                        String hashPartie = (String) obj.get("hashPartie");
                        String clefPubliqueJ1 = (String) obj.get("clefPubliqueJ1");
                        String clefPubliqueJ2 = (String) obj.get("clefPubliqueJ2");
                        String clefPubliqueArbitre = (String) obj.get("clefPubliqueArbitre");
                        String voteJ1 = (String) obj.get("voteJ1");
                        String voteJ2 = (String) obj.get("voteJ2");
                        String voteArbitre = (String) obj.get("voteArbitre");
                        String signatureJ1 = (String) obj.get("signatureJ1");
                        String signatureJ2 = (String) obj.get("signatureJ2");
                        String signatureArbitre = (String) obj.get("signatureArbitre");
                        String hashVote = (String) obj.get("hashVote");
                        String signatureArbitreHashVote = (String) obj.get("signatureArbitreHashVote");


                        System.out.println("Traitement reçues");

                        if ((hashPartie != null) && (clefPubliqueJ1 != null) && (clefPubliqueJ2 != null) && (clefPubliqueArbitre != null) && (voteJ1 != null) && (voteJ2 != null) && (voteArbitre != null) && (signatureJ1 != null) && (signatureJ2 != null) && (signatureArbitre != null) && (hashVote != null) && (signatureArbitreHashVote != null)) {
                            ThreadClient.ajouterPartie(db,timestamp,hashPartie,clefPubliqueJ1,clefPubliqueJ2,clefPubliqueArbitre,voteJ1,voteJ2,voteArbitre,signatureJ1,signatureJ2,signatureArbitre,hashVote,signatureArbitreHashVote);
                            System.out.println("hashPartie : " + hashPartie + " clefPubliqueJ1 : " + clefPubliqueJ1 + " clefPubliqueJ2 : " + clefPubliqueJ2 + " clefPubliqueArbitre : " + clefPubliqueArbitre + " voteJ1 : " + voteJ1 + " voteJ2 : " + voteJ2 + " voteArbitre : " + voteArbitre + " signatureJ1 : " + signatureJ1 + " signatureJ2 : " + signatureJ2 + " signatureArbitre : " + signatureArbitre + " hashVote : " + hashVote);
                        } else {
                            System.out.println("ERREUR ! " + "hashPartie : " + hashPartie + " clefPubliqueJ1 : " + clefPubliqueJ1 + " clefPubliqueJ2 : " + clefPubliqueJ2 + " clefPubliqueArbitre : " + clefPubliqueArbitre + " voteJ1 : " + voteJ1 + " voteJ2 : " + voteJ2 + " voteArbitre : " + voteArbitre + " signatureJ1 : " + signatureJ1 + " signatureJ2 : " + signatureJ2 + " signatureArbitre : " + signatureArbitre + " hashVote : " + hashVote);
                        }
                    }
                    // CODE POUR RAJOUTER A LA BASE DE DONNEES...


                    msg = inputStream.readUTF();

                }
                System.out.println("Serveur nous envoie : " + message);
            }
            System.out.println("Serveur nous envoie : " + message);
        }
        demanderPartiesARecevoir();
    }
    public void demanderPartiesAEnvoyer() throws IOException {
        // Etape 1 demander les confirmations
        for (Socket socket : sockets) {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            outputStream.writeInt(24);//22 = demande de parties
            outputStream.flush();
            System.out.println("On a envoyé la demande de liste de confirmation ! ");

            String message = inputStream.readUTF();
            if (message.equals("start")) {
                String msg = inputStream.readUTF();
                while (!msg.equals("stop")) {
                    //JSON parser pour récuperer les clés publiques

                    List<String> list = Json.extraireMots(msg);
                    int c;
                    for (c = 0; c < list.size(); c++) {
                        //     System.out.println("Element " + c + " : " + list.get(c));
                        // On tente d'ajouter l'élement dans une base de données !
                        Map<String, Object> obj = (Map<String, Object>) Json.deserialize(list.get(c));
                        String timestamp = (String) obj.get("timestamp");
                        String hashPartie = (String) obj.get("hashPartie");
                        String clefPubliqueJ1 = (String) obj.get("clefPubliqueJ1");
                        String clefPubliqueJ2 = (String) obj.get("clefPubliqueJ2");
                        String clefPubliqueArbitre = (String) obj.get("clefPubliqueArbitre");
                        String voteJ1 = (String) obj.get("voteJ1");
                        String voteJ2 = (String) obj.get("voteJ2");
                        String voteArbitre = (String) obj.get("voteArbitre");
                        String signatureJ1 = (String) obj.get("signatureJ1");
                        String signatureJ2 = (String) obj.get("signatureJ2");
                        String signatureArbitre = (String) obj.get("signatureArbitre");



                        System.out.println("Traitement reçues");

                        if ((hashPartie != null) && (clefPubliqueJ1 != null) && (clefPubliqueJ2 != null) && (clefPubliqueArbitre != null) && (voteJ1 != null) && (voteJ2 != null) && (voteArbitre != null) && (signatureJ1 != null) && (signatureJ2 != null) && (signatureArbitre != null) ) {
                            ThreadClient.ajouterPartieAEnvoyer(db,timestamp,hashPartie,clefPubliqueArbitre,clefPubliqueJ1,clefPubliqueJ2,voteArbitre,voteJ1,voteJ2,signatureArbitre,signatureJ1,signatureJ2);

                            System.out.println("hashPartie : " + hashPartie + " clefPubliqueJ1 : " + clefPubliqueJ1 + " clefPubliqueJ2 : " + clefPubliqueJ2 + " clefPubliqueArbitre : " + clefPubliqueArbitre + " voteJ1 : " + voteJ1 + " voteJ2 : " + voteJ2 + " voteArbitre : " + voteArbitre + " signatureJ1 : " + signatureJ1 + " signatureJ2 : " + signatureJ2 + " signatureArbitre : " + signatureArbitre + " hashVote : " );
                        } else {
                            System.out.println("ERREUR ! " + "hashPartie : " + hashPartie + " clefPubliqueJ1 : " + clefPubliqueJ1 + " clefPubliqueJ2 : " + clefPubliqueJ2 + " clefPubliqueArbitre : " + clefPubliqueArbitre + " voteJ1 : " + voteJ1 + " voteJ2 : " + voteJ2 + " voteArbitre : " + voteArbitre + " signatureJ1 : " + signatureJ1 + " signatureJ2 : " + signatureJ2 + " signatureArbitre : " + signatureArbitre + " hashVote : " );
                        }
                    }
                    // CODE POUR RAJOUTER A LA BASE DE DONNEES...


                    msg = inputStream.readUTF();

                }
                System.out.println("Serveur nous envoie : " + message);
            }
            System.out.println("Serveur nous envoie : " + message);
        }
    demanderConfirmations();
    }

    public void demanderPartiesARecevoir() throws IOException {
        // Etape 1 demander les confirmations
        for (Socket socket : sockets) {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            outputStream.writeInt(23);//22 = demande de parties
            outputStream.flush();
            System.out.println("On a envoyé la demande de liste de PartieARecevoir ! ");

            String message = inputStream.readUTF();
            if (message.equals("start")) {
                String msg = inputStream.readUTF();
                while (!msg.equals("stop")) {
                    System.out.println("PartieARecevoirxD + " + msg);
                    //JSON parser pour récuperer les clés publiques
                    List<String> list = Json.extraireMots(msg);
                    int c;

                    for (c = 0; c < list.size(); c++) {
                        //     System.out.println("Element " + c + " : " + list.get(c));
                        // On tente d'ajouter l'élement dans une base de données !
                        Map<String, Object> obj = (Map<String, Object>) Json.deserialize(list.get(c));
                        String timestamp = (String) obj.get("timestamp");
                        String hashPartie = (String) obj.get("hashPartie");
                        String clefPubliqueJ1 = (String) obj.get("clefPubliqueJ1");
                        String clefPubliqueJ2 = (String) obj.get("clefPubliqueJ2");
                        String clefPubliqueArbitre = (String) obj.get("clefPubliqueArbitre");
                        String voteJ1 = (String) obj.get("voteJ1");
                        String voteJ2 = (String) obj.get("voteJ2");
                        String voteArbitre = (String) obj.get("voteArbitre");
                        String signatureJ1 = (String) obj.get("signatureJ1");
                        String signatureJ2 = (String) obj.get("signatureJ2");
                        String signatureArbitre = (String) obj.get("signatureArbitre");



                        System.out.println("Traitement reçues");

                        if ((hashPartie != null) && (clefPubliqueJ1 != null) && (clefPubliqueJ2 != null) && (clefPubliqueArbitre != null) && (voteJ1 != null) && (voteJ2 != null) && (voteArbitre != null) && (signatureJ1 != null) && (signatureJ2 != null) && (signatureArbitre != null) ) {
                           ThreadClient.ajouterPartieARecevoir(db,timestamp,hashPartie,clefPubliqueArbitre,clefPubliqueJ1,clefPubliqueJ2,voteArbitre,voteJ1,voteJ2,signatureArbitre,signatureJ1,signatureJ2);
                            System.out.println("hashPartie : " + hashPartie + " clefPubliqueJ1 : " + clefPubliqueJ1 + " clefPubliqueJ2 : " + clefPubliqueJ2 + " clefPubliqueArbitre : " + clefPubliqueArbitre + " voteJ1 : " + voteJ1 + " voteJ2 : " + voteJ2 + " voteArbitre : " + voteArbitre + " signatureJ1 : " + signatureJ1 + " signatureJ2 : " + signatureJ2 + " signatureArbitre : " + signatureArbitre + " hashVote : " );
                        } else {
                            System.out.println("ERREUR ! " + "hashPartie : " + hashPartie + " clefPubliqueJ1 : " + clefPubliqueJ1 + " clefPubliqueJ2 : " + clefPubliqueJ2 + " clefPubliqueArbitre : " + clefPubliqueArbitre + " voteJ1 : " + voteJ1 + " voteJ2 : " + voteJ2 + " voteArbitre : " + voteArbitre + " signatureJ1 : " + signatureJ1 + " signatureJ2 : " + signatureJ2 + " signatureArbitre : " + signatureArbitre + " hashVote : " );
                        }
                    }
                    // CODE POUR RAJOUTER A LA BASE DE DONNEES...


                    msg = inputStream.readUTF();

                }
                System.out.println("Serveur nous envoie : " + message);
            }
            System.out.println("Serveur nous envoie : " + message);
        }
        demanderPartiesAEnvoyer();
    }

    public void ajouterPartie(Partie partie) {

        SQLiteDatabase database = db.getDatabase();
        String[] values = {partie.timestamp, partie.hashPartie, partie.clefPubliqueJ1, partie.clefPubliqueJ2, partie.clefPubliqueArbitre, partie.voteJ1, partie.voteJ2, partie.voteArbitre, partie.signatureJ1, partie.signatureJ2, partie.signatureArbitre, partie.hashVote};
        try {
            database.execSQL("INSERT INTO partie ('timestamp', 'hashPartie', 'clefPubliqueJ1', 'clefPubliqueJ2', 'clefPubliqueArbitre', 'voteJ1', 'voteJ2', 'voteArbitre', 'signatureJ1', 'signatureJ2', 'signatureArbitre', 'hashVote') VALUES ('"+values[0]+"', '"+values[1]+"', '"+values[2]+"', '"+values[3]+"', '"+values[4]+"', '"+values[5]+"', '"+values[6]+"', '"+values[7]+"', '"+values[8]+"', '"+values[9]+"', '"+values[10]+"', '"+values[11]+"')");
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout de la partie");
        }

    }
    public void ajouterPartieAEnvoyer(Partie partie) {
        SQLiteDatabase database = db.getDatabase();
        String[] values = {partie.timestamp, partie.hashPartie, partie.clefPubliqueJ1, partie.clefPubliqueJ2, partie.clefPubliqueArbitre, partie.voteJ1, partie.voteJ2, partie.voteArbitre, partie.signatureJ1, partie.signatureJ2, partie.signatureArbitre};
        try {
            database.execSQL("INSERT INTO partieAEnvoyer ('timestamp', 'hashPartie', 'clefPubliqueJ1', 'clefPubliqueJ2', 'clefPubliqueArbitre', 'voteJ1', 'voteJ2', 'voteArbitre', 'signatureJ1', 'signatureJ2', 'signatureArbitre', 'hashVote') VALUES ('"+values[0]+"', '"+values[1]+"', '"+values[2]+"', '"+values[3]+"', '"+values[4]+"', '"+values[5]+"', '"+values[6]+"', '"+values[7]+"', '"+values[8]+"', '"+values[9]+"', '"+values[10]+"')");
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout de la partie à envoyer");
        }

    }
    public void ajouterPartieARecevoir(Partie partie) {
        SQLiteDatabase database = db.getDatabase();
        String[] values = {partie.timestamp, partie.hashPartie, partie.clefPubliqueJ1, partie.clefPubliqueJ2, partie.clefPubliqueArbitre, partie.voteJ1, partie.voteJ2, partie.voteArbitre, partie.signatureJ1, partie.signatureJ2, partie.signatureArbitre};
        try {
            database.execSQL("INSERT INTO partieARecevoir ('timestamp', 'hashPartie', 'clefPubliqueJ1', 'clefPubliqueJ2', 'clefPubliqueArbitre', 'voteJ1', 'voteJ2', 'voteArbitre', 'signatureJ1', 'signatureJ2', 'signatureArbitre', 'hashVote') VALUES ('"+values[0]+"', '"+values[1]+"', '"+values[2]+"', '"+values[3]+"', '"+values[4]+"', '"+values[5]+"', '"+values[6]+"', '"+values[7]+"', '"+values[8]+"', '"+values[9]+"', '"+values[10]+"')");
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout de la partie à envoyer");
        }
    }

    public void ajouterleaderboard(String clefPublique, String pseudo, String elo, String coefficientArbitrage, String scoreTotal, String nbParties, String nbVictoire, String nbConfirmation, String nbPartieArbitre, String nbNul, String nbDefaite){
        SQLiteDatabase database = db.getDatabase();
        String[] values = {clefPublique, pseudo, elo, coefficientArbitrage, scoreTotal, nbParties, nbVictoire, nbConfirmation, nbPartieArbitre, nbNul, nbDefaite};

        try {
            database.execSQL("INSERT INTO leaderboard ('clefPublique', 'pseudo', 'elo', 'coefficientArbitrage', 'scoreTotal', 'nbParties', 'nbVictoire', 'nbConfirmation', 'nbPartieArbitre', 'nbNul', 'nbDefaite') VALUES ('"+values[0]+"', '"+values[1]+"', '"+values[2]+"', '"+values[3]+"', '"+values[4]+"', '"+values[5]+"', '"+values[6]+"', '"+values[7]+"', '"+values[8]+"', '"+values[9]+"', '"+values[10]+"')");
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout de la partie dans leaderboard");
        }


    }
    public void ajouterCompte(String pseudo, String clefPublique, String clefPrivee) {
        SQLiteDatabase database = db.getDatabase();
        String[] values = {pseudo, clefPublique, clefPrivee};
        try {
            database.execSQL("INSERT INTO compte ('pseudo','clefPublique','clefPrivee') VALUES ('"+values[0]+"', '"+values[1]+"', '"+values[2]+"')");
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout du compte dans leaderboard");
        }

    }

    public void ajouterCompte(String pseudo, String clefPublique) {
        ajouterCompte(pseudo, clefPublique, "");
    }

    public void ajouterConfirmation(String hashPartie, String hashVote, String clefPublique, String signatureHashVote) {
        SQLiteDatabase database = db.getDatabase();
        String[] values = {hashPartie, hashVote, clefPublique, signatureHashVote};

        try {
            database.execSQL("INSERT INTO confirmation ('hashPartie', 'hashVote', 'clefPublique', 'signatureHashVote') VALUES ('"+values[0]+"', '"+values[1]+"', '"+values[2]+"', '"+values[3]+"')");
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout de la partie dans leaderboard");
        }


    }

    public void ajouterPlainte(String hashPartie, String hashVote, String clefPublique, String signatureHashVote) {
        SQLiteDatabase database = db.getDatabase();
        String[] values = {hashPartie, hashVote, clefPublique, signatureHashVote};

        try {
            database.execSQL("INSERT INTO plainte ('hashPartie', 'hashVote', 'clefPublique', 'signatureHashVote') VALUES ('"+values[0]+"', '"+values[1]+"', '"+values[2]+"', '"+values[3]+"')");
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout de la partie dans leaderboard");
        }


    }
    public void ajouterPartieDetail(String hashPartie, String resultat, String eloJ1, String eloJ2, String fiabiliteArbitre) {
        SQLiteDatabase database = db.getDatabase();
        String[] values = {hashPartie, resultat, eloJ1, eloJ2, fiabiliteArbitre};

        try {
            database.execSQL("INSERT INTO partieDetail ('hashPartie', 'resultat', 'eloJ1', 'eloJ2', 'fiabiliteArbitre') VALUES ('"+values[0]+"', '"+values[1]+"', '"+values[2]+"', '"+values[3]+"', '"+values[4]+"')");
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout de la partie dans partieDetail");
        }


    }

    public void send(String message) throws IOException {
        for (Socket socket : sockets) {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeInt(0);
            outputStream.writeUTF(message);


            outputStream.flush();
            System.out.print(" On a envoyé ! ");
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            String message2 = inputStream.readUTF();
            System.out.println("Serveur nous envoie : " + message2);

        }
    }


    // Création de plusieurs méthodes pour gérer les demandes à la base de données !
    public void supprimerConnexionsFermees() {
        List<Socket> socketsAFermer = new ArrayList<>();
        for (Socket socket : serverInfo.keySet()) {
            if (socket.isClosed()) {
                socketsAFermer.add(socket);
                count -= 1;
            }
        }
        for (Socket socketAFermer : socketsAFermer) {
            serverInfo.remove(socketAFermer);
            try {
                socketAFermer.close();
            } catch (IOException e) {
// Gérer l'exception si nécessaire
            }
        }
    }

    public void receive() throws IOException {
        for (Socket socket : sockets) {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            String message = inputStream.readUTF();
            System.out.println(socket.getInetAddress().getHostAddress() + "," + serverInfo.get(socket) + "," + socket);
            System.out.println(message);
        }
    }
}
