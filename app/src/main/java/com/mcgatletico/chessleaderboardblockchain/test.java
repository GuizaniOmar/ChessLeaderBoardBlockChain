package com.mcgatletico.chessleaderboardblockchain;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class test {
    public static void main(String[] args) throws UnknownHostException {
        System.out.println("Test executé");

        liste_ips();
    }

    public static void liste_ips() throws UnknownHostException {
        try {
            // Obtenir toutes les adresses IP locales de l'ordinateur
            InetAddress[] adresses = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());

            // Afficher toutes les adresses IP locales
            for (int i = 0; i < adresses.length; i++) {
                String ip = adresses[i].getHostAddress();
                if (ip.startsWith("192.")) {
                    System.out.println("Adresse IP locale: " + ip);}
                else{
                    System.out.println("Adresse IP non-local: " + ip);}

            }
        } catch (UnknownHostException e) {
            // Gérer les erreurs
            e.printStackTrace();
        }


    }

}
