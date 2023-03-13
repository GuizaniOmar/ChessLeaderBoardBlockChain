package com.mcgatletico.chessleaderboardblockchain;

public class Partie {
    public String timestamp;
    public String hashPartie;
    public String clefPubliqueJ1;
    public String clefPubliqueJ2;
    public String clefPubliqueArbitre;
    public String voteJ1;
    public String voteJ2;
    public String voteArbitre;
    public  String signatureJ1;
    public String signatureJ2;
    public   String signatureArbitre;
    public String hashVote;

    // Attributs de la classe
    Partie(String timestamp, String hashPartie, String clefPubliqueJ1, String clefPubliqueJ2, String clefPubliqueArbitre, String voteJ1, String voteJ2, String voteArbitre, String signatureJ1, String signatureJ2, String signatureArbitre, String hashVote) {
        this.timestamp = timestamp;
        this.hashPartie = hashPartie;
        this.clefPubliqueJ1 = clefPubliqueJ1;
        this.clefPubliqueJ2 = clefPubliqueJ2;
        this.clefPubliqueArbitre = clefPubliqueArbitre;
        this.voteJ1 = voteJ1;
        this.voteJ2 = voteJ2;
        this.voteArbitre = voteArbitre;
        this.signatureJ1 = signatureJ1;
        this.signatureJ2 = signatureJ2;
        this.signatureArbitre = signatureArbitre;
        this.hashVote = hashVote;

    }
    Partie(String timestamp, String hashPartie, String clefPubliqueJ1, String clefPubliqueJ2, String clefPubliqueArbitre) {
        this.timestamp = timestamp;
        this.hashPartie = hashPartie;
        this.clefPubliqueJ1 = clefPubliqueJ1;
        this.clefPubliqueJ2 = clefPubliqueJ2;
        this.clefPubliqueArbitre = clefPubliqueArbitre;
        this.voteJ1 = null;
        this.voteJ2 = null;
        this.voteArbitre = null;
        this.signatureJ1 = null;
        this.signatureJ2 = null;
        this.signatureArbitre = null;
        this.hashVote = null;

    }

}