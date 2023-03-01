package com.example.chessleaderboardblockchain;


import android.database.sqlite.SQLiteDatabase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Client  {
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;



    public Client(String serverAddress, int serverPort) throws IOException {
        socket = new Socket(serverAddress, serverPort);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void login(DatabaseHelper db, String username, String clefPublique, String clefPriveeCryptee) throws IOException {
        PaquetLogin paquet = new PaquetLogin(username,clefPublique,clefPriveeCryptee);
        outputStream.writeInt(paquet.getType());
        outputStream.writeUTF(paquet.getUsername());
        outputStream.writeUTF(paquet.getClefPublique());
        outputStream.writeUTF(paquet.getClefPriveeCryptee());
        outputStream.flush();
        String response1 = inputStream.readUTF();
        String response2 = inputStream.readUTF();
        System.out.println("Réponse du serveur pour l'inscription : " + response1 );
        System.out.println("Réponse du serveur pour la présence du pseudo dans la base de données : " + response2 );

    }
    public String json(DatabaseHelper db) throws IOException {
        String msg = "Message vide";
        PaquetJson paquet = new PaquetJson(msg);
        outputStream.writeInt(paquet.getType());
        outputStream.writeUTF(paquet.getMsg());
        outputStream.flush();
        int idresponse = inputStream.readInt();
        String response = inputStream.readUTF();
        List<String> list = Json.extraireMots(response);
        int c;
        for (c = 0;c<list.size();c++){
            //     System.out.println("Element " + c + " : " + list.get(c));
            // On tente d'ajouter l'élement dans une base de données !
            Map<String, Object> obj = (Map<String, Object>) Json.deserialize(list.get(c));
            String pseudo = (String) obj.get("Pseudo");
            String clefPriveeCryptee = (String) obj.get("ClefPriveeCryptee");
            String clefPublique = (String) obj.get("ClefPublique");
            if ((pseudo != null) && (clefPriveeCryptee != null) && (clefPublique != null)) {
            // ON VA RAJOUTER A LA BASE DE DONNEES
                //MaBaseDeDonnees maBaseDeDonnees = MaBaseDeDonnees.getInstance(this);

             SQLiteDatabase database = db.getDatabase();

             try{
                 database.execSQL("INSERT INTO COMPTES (Pseudo,ClefPublique,ClefPrivee) VALUES('" + pseudo + "','"+ clefPublique + "','" + clefPriveeCryptee + "')");
             }
                catch (Exception e){
                    System.out.println("BUG de base de données!");
                }


                System.out.println("pseudo: " + pseudo + " clefpriveecryptee: " + clefPriveeCryptee + " clefpublique: " + clefPublique );
            }else {
                System.out.println("ERREUR ! " + "pseudo: " + pseudo + "clefpriveecryptee: " + clefPriveeCryptee + "clefpublique: " + clefPublique );
            }
            // CODE POUR RAJOUTER A LA BASE DE DONNEES...
        }
        return response;
    }
    public void deleteAccount(String username) throws IOException {
        PaquetDeleteAccount paquet = new PaquetDeleteAccount(username);
        outputStream.writeInt(paquet.getType());
        outputStream.writeUTF(paquet.getUsername());
        outputStream.flush();
        String response = inputStream.readUTF();
        System.out.println("Réponse du serveur pour la suppression: " + response);
    }

    public void close() throws IOException {
        socket.close();
    }

    public void envoyerPartie(String timestampPartie, String hashPartie, String clefPubliqueJ1, String clefPubliqueJ2, String clefPubliqueArbitre) throws IOException{
        String msg = "Message vide";
        PaquetEnvoyerPartie paquet = new PaquetEnvoyerPartie(timestampPartie,hashPartie,clefPubliqueJ1,clefPubliqueJ2,clefPubliqueArbitre);
        outputStream.writeInt(PaquetEnvoyerPartie.SEND_GAME);
        outputStream.writeUTF(paquet.getTimestampPartie());
        outputStream.writeUTF(paquet.getHashPartie());
        outputStream.writeUTF(paquet.getClefPubliqueJ1());
        outputStream.writeUTF(paquet.getClefPubliqueJ2());
        outputStream.writeUTF(paquet.getClefPubliqueArbitre());
        outputStream.flush();
        int idresponse = inputStream.readInt();
        for (int cd = 0; cd < 2; cd++){
            System.out.println("On test de recevoir");
            String response = inputStream.readUTF();
            System.out.println("Réponse du serveur pour l'envoi de la partie: " + response);
        }

    }
}

