package com.mcgatletico.chessleaderboardblockchain;

import java.util.HashMap;
import java.util.List;

public class Elo {

    public class Partie {

        // Attributs de la classe
        private String joueur1; // Nom du premier joueur
        private String joueur2; // Nom du deuxième joueur
        private int eloJoueur1; // Elo du premier joueur avant la partie
        private int eloJoueur2; // Elo du deuxième joueur avant la partie
        private double score; // Score du premier joueur (1 pour une victoire, 0.5 pour un nul, 0 pour une défaite)

        // Constructeur de la classe
        public Partie(String joueur1, String joueur2, int eloJoueur1, int eloJoueur2, double score) {
            this.joueur1 = joueur1;
            this.joueur2 = joueur2;
            this.eloJoueur1 = eloJoueur1;
            this.eloJoueur2 = eloJoueur2;
            this.score = score;
        }

        // Méthodes d'accès aux attributs (getters)
        public String getJoueur1() {
            return joueur1;
        }

        public String getJoueur2() {
            return joueur2;
        }
        public int getEloJoueur1() {
            return eloJoueur1;
        }

        public int getEloJoueur2() {
            return eloJoueur2;
        }

        public double getScore() {
            return score;
        }
    }
    // Méthode qui calcule le coefficient K en fonction du niveau du joueur
    private static int calculateK(int elo) {
        if (elo < 300) {
            return 60;
        } else if (elo < 600) {
            return 50;
        } else if (elo < 1200) {
            return 40;
        } else if (elo < 1800) {
            return 30;
        } else if (elo < 2600) {
            return 20;
        } else {
            return 10;
        }
    }

    // Méthode qui calcule le nouvel Elo d'un joueur après une partie
    private static int calculateNewElo(int oldElo, int opponentElo, double score) {
        // Calculer l'espérance de score du joueur
        double expectedScore = 1 / (1 + Math.pow(10, (opponentElo - oldElo) / 400.0));
        // Calculer le coefficient K adapté au niveau du joueur
        int K = calculateK(oldElo);
        // Calculer le nouvel Elo du joueur
        return (int) Math.round(oldElo + K * (score - expectedScore));
    }

    public static void updateElo(List<Partie> parties) {
        // Créer une map pour stocker le classement des joueurs
        HashMap<String, Integer> eloMap = new HashMap<>();
        // Parcourir les parties par ordre chronologique
        for (Partie partie : parties) {
            // Récupérer les informations de la partie
            String joueur1 = partie.getJoueur1();
            String joueur2 = partie.getJoueur2();
            int eloJoueur1 = partie.getEloJoueur1();
            int eloJoueur2 = partie.getEloJoueur2();
            double score = partie.getScore();
            // Calculer le nouvel Elo des joueurs
            int newEloJoueur1 = calculateNewElo(eloJoueur1, eloJoueur2, score);
            int newEloJoueur2 = calculateNewElo(eloJoueur2, eloJoueur1, 1 - score);
            // Mettre à jour la map avec les nouveaux Elo
            eloMap.put(joueur1, newEloJoueur1);
            eloMap.put(joueur2, newEloJoueur2);
        }
        // Afficher le classement final des joueurs
        for (String joueur : eloMap.keySet()) {
            System.out.println(joueur + " : " + eloMap.get(joueur));
        }

    }
}
