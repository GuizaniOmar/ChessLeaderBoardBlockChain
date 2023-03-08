package com.mcgatletico.chessleaderboardblockchain;

import java.util.Arrays;

public class PaquetAjouterSignature extends Paquet{
    private String hashPartie;

    private String acteurSignature;
    private String vote;
    private String signature;
    private String voteActeur;
    public static String[] voteActeurs =  {"VoteJ1", "VoteJ2", "VoteArbitre"};
    public static String[] clefActeurs =  {"ClefPubliqueJ1", "ClefPubliqueJ2", "ClefPubliqueArbitre"};
    public PaquetAjouterSignature(String hashPartie, String voteActeur,String vote, String signature) {
        super(Paquet.SIGNATURE);
        this.hashPartie = hashPartie;
        this.vote = vote;
        if (Arrays.asList(voteActeurs).contains(voteActeur)) // on autorise uniquement les votes de J1, J2 et Arbitre
            this.voteActeur = voteActeur;
        else
            this.voteActeur = null;
        this.voteActeur = voteActeur;
        this.signature = signature;

    }

    public static boolean isVoteActeur(String voteActeur) {
        return Arrays.asList(voteActeurs).contains(voteActeur);
    }

    public static String getClefActeur(String voteActeur) {
        if (Arrays.asList(voteActeurs).contains(voteActeur)) // on autorise uniquement les votes de J1, J2 et Arbitre
            return clefActeurs[Arrays.asList(voteActeurs).indexOf(voteActeur)];
        else
            return null;
    }

    public String getHashPartie() {
        return hashPartie;
    }

    public String getSignature() {
        return signature;
    }

    public String getVoteActeur() {
        return voteActeur;
    }

    public String getVote() {
        return vote;
    }
}

