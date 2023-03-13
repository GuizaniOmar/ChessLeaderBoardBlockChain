package com.mcgatletico.chessleaderboardblockchain;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Client  {
    // Classe permettant de se connecter au serveur distant (en + du peer-peer)
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

    @SuppressLint("Range")
    public void sendComptes(DatabaseHelper db){

        // Le serveur reçoit une liste de comptes, pour les ajouter à sa base de données
        SQLiteDatabase database = db.getDatabase();
        try {
            outputStream.writeInt(6); //On envoie 6 pour dire que c'est un envoie de comptes
            outputStream.flush();
            Cursor result = database.rawQuery("SELECT _id,pseudo,clefPublique FROM compte",null);
            String contenue = "";   // Contenue du paquet
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


    }

    public void getComptes(DatabaseHelper db) throws IOException {
        // Le serveur reçoit une liste de comptes, pour les ajouter à sa base de données
        SQLiteDatabase database = db.getDatabase();
        outputStream.writeInt(20);//20 = demande la liste de compte
        outputStream.flush();
        System.out.println("On a envoyé la demande de clé publique ! ");

        String message = inputStream.readUTF();
        if (message.equals("start")) {
            String msg = inputStream.readUTF();
            while (!msg.equals("end")) {
                //JSON parser pour récuperer les clés publiques

                List<String> list = Json.extraireMots(msg);
                int c;
                for (c = 0; c < list.size(); c++) {
                    //     System.out.println("Element " + c + " : " + list.get(c));
                    // On tente d'ajouter l'élement dans une base de données !
                    Map<String, Object> obj = (Map<String, Object>) Json.deserialize(list.get(c));
                    String pseudo = (String) obj.get("pseudo");
                    // On récupère pas la clef privée String clefPriveeCryptee = (String) obj.get("clefPrivee");
                    String clefPublique = (String) obj.get("clefPublique");

                    if ((pseudo != null) && (clefPublique != null)) {
                       ajouterCompte(db, pseudo, clefPublique);
                        } else {
                        System.out.println("ERREUR ! " + "pseudo: " + pseudo + " clefpublique: " + clefPublique);
                    }
                    // CODE POUR RAJOUTER A LA BASE DE DONNEES...
                }

                msg = inputStream.readUTF();

            }
            System.out.println("Serveur nous envoie : " + message);
        }
        System.out.println("Serveur nous envoie : " + message);

    }

    public void ajouterCompte(DatabaseHelper db,String pseudo, String clefPublique, String clefPrivee) {
        SQLiteDatabase database = db.getDatabase();
        String[] values = {pseudo, clefPublique, clefPrivee};
        try {
            database.execSQL("INSERT INTO compte ('pseudo','clefPublique','clefPrivee') VALUES ('"+values[0]+"', '"+values[1]+"', '"+values[2]+"')");
        }catch(Exception e){
            System.out.println("Erreur lors de l'ajout du compte dans leaderboard");
        }

    }

    public void ajouterCompte(DatabaseHelper db, String pseudo, String clefPublique) {
        ajouterCompte(db,pseudo, clefPublique, "");
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

    public String recupererPartiesASigner(DatabaseHelper db) throws IOException {
        String msg = "Message vide";
        PaquetPartiesASigner paquetx = new PaquetPartiesASigner(msg);

        outputStream.writeInt(paquetx.getType());
        System.out.println("Type du paquet : " + paquetx.getType());
        outputStream.writeUTF(paquetx.getMsg());
        outputStream.flush();
        int idresponse = inputStream.readInt();
        String response = inputStream.readUTF();
        System.out.println("Reponse serveur parties à signer" + response);
        List<String> list = Json.extraireMots(response);
        int c;
        SQLiteDatabase database = db.getDatabase();
    //    database.execSQL("DROP TABLE IF EXISTS PARTIES");
        for (c = 0;c<list.size();c++){
            //     System.out.println("Element " + c + " : " + list.get(c));
            // On tente d'ajouter l'élement dans une base de données !
            Map<String, Object> obj = (Map<String, Object>) Json.deserialize(list.get(c));
            String TimestampPartie = (String) obj.get("Timestamp");
            String HashPartie = (String) obj.get("HashPartie");
            String ClefPubliqueJ1 = (String) obj.get("ClefPubliqueJ1");
            String ClefPubliqueJ2 = (String) obj.get("ClefPubliqueJ2");
            String ClefPubliqueArbitre = (String) obj.get("ClefPubliqueArbitre");
            String VoteJ1 = (String) obj.get("VoteJ1");
            String VoteJ2 = (String) obj.get("VoteJ2");
            String VoteArbitre = (String) obj.get("VoteArbitre");
            String SignatureJ1 = (String) obj.get("SignatureJ1");
            String SignatureJ2 = (String) obj.get("SignatureJ2");
            String SignatureArbitre = (String) obj.get("SignatureArbitre");

            if ((TimestampPartie != null) && (HashPartie != null) && (ClefPubliqueJ1 != null) && (ClefPubliqueJ2 != null) && (ClefPubliqueArbitre != null)) {
                // ON VA RAJOUTER A LA BASE DE DONNEES
                //MaBaseDeDonnees maBaseDeDonnees = MaBaseDeDonnees.getInstance(this);


                try{
                    database.execSQL("INSERT INTO PARTIES (Timestamp,HashPartie,ClefPubliqueJ1,ClefPubliqueJ2,ClefPubliqueArbitre,VoteJ1,VoteJ2,VoteArbitre,SignatureJ1,SignatureJ2,SignatureArbitre) VALUES('" + TimestampPartie + "','"+ HashPartie + "','" + ClefPubliqueJ1 + "','" + ClefPubliqueJ2 + "','" + ClefPubliqueArbitre + "','" + VoteJ1 + "','" + VoteJ2 + "','" + VoteArbitre + "','" + SignatureJ1 + "','" + SignatureJ2 + "','" + SignatureArbitre +"')");
                }
                catch (Exception e){
                    System.out.println("BUG de base de données!");
                }


                System.out.println("TimestampPartie xD: " + TimestampPartie + " HashPartie: " + HashPartie + " ClefPubliqueJ1: " + ClefPubliqueJ1 + " ClefPubliqueJ2: " + ClefPubliqueJ2 + " ClefPubliqueArbitre: " + ClefPubliqueArbitre + " VoteJ1: " + VoteJ1 + " VoteJ2: " + VoteJ2 + " VoteArbitre: " + VoteArbitre);
            }else {
                System.out.println("ERREUR ! " + "TimestampPartie: " + TimestampPartie + " HashPartie: " + HashPartie + " ClefPubliqueJ1: " + ClefPubliqueJ1 + " ClefPubliqueJ2: " + ClefPubliqueJ2 + " ClefPubliqueArbitre: " + ClefPubliqueArbitre + " VoteJ1: " + VoteJ1 + " VoteJ2: " + VoteJ2 + " VoteArbitre: " + VoteArbitre);
                  }
            // CODE POUR RAJOUTER A LA BASE DE DONNEES...
        }
        return response;
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
//
    public void ajouterSignature(String hashPartie,String acteurPartie,String vote, String signaturePartie) throws IOException{
        String msg = "Message vide";
        PaquetAjouterSignature paquet = new PaquetAjouterSignature(hashPartie,acteurPartie,vote,signaturePartie);
        outputStream.writeInt(PaquetEnvoyerPartie.SIGNATURE);

        outputStream.writeUTF(paquet.getHashPartie());
        outputStream.writeUTF(paquet.getVoteActeur());
        outputStream.writeUTF(paquet.getVote());

        outputStream.writeUTF(paquet.getSignature());

        outputStream.flush();
        int idresponse = inputStream.readInt();
        for (int cd = 0; cd < 1; cd++){
            System.out.println("On test de recevoir la confirmation de signature");
            String response = inputStream.readUTF();
            System.out.println("Réponse du serveur pour la signature: " + response);
        }

    }


}

