package com.mcgatletico.chessleaderboardblockchain;

public class CONS {
    public static String eloDepart = "600"; // l'élo de départ est fixé à 600
    public static String coefficientArbitre = "0.5"; // l'élo maximum est fixé à 3000

    public static int nbMinConfirmation = 0;

    public static double punirArbitre = -0.10;
    public static double recompenserArbitre = 0.01; // on ajoute 1% à chaque confirmation

    public static int PORT = 52000;

    public static String IP_Serveur = "93.115.97.128";
}
