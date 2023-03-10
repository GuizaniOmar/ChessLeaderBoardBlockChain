package com.mcgatletico.chessleaderboardblockchain;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class DatabaseHelper {
    private static DatabaseHelper instance;
    private SQLiteOpenHelper helper;
    private SQLiteDatabase database;
    private String nomBaseDeDonnees = "dbchess.db";
    private DatabaseHelper(Context context) {
        System.out.println("#1 - Execution de la création de la base de données ! ");
        supprimerBaseDeDonnees(context);
         helper = new SQLiteOpenHelper(context,nomBaseDeDonnees,null,1) {
            @Override

            public void onCreate(SQLiteDatabase sqLiteDatabase) {

                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS 'compte' ( '_id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, Pseudo TEXT NOT NULL UNIQUE, ClefPublique TEXT NOT NULL, ClefPrivee TEXT)");
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS 'partieARecevoir' ('_id'INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,'timestamp' INTEGER NOT NULL,'hashPartie' TEXT NOT NULL,'clefPubliqueJ1' TEXT NOT NULL,'clefPubliqueJ2' TEXT NOT NULL,'clefPubliqueArbitre' TEXT NOT NULL CHECK('clefPubliqueJ1' <> 'clefPubliqueJ2' AND 'clefPubliqueJ1' <> 'clefPubliqueArbitre' AND 'clefPubliqueJ2' <> 'clefPubliqueArbitre'),'voteJ1' TEXT,'voteJ2' TEXT,'voteArbitre' TEXT,'signatureJ1' TEXT,'signatureJ2' TEXT,'signatureArbitre' TEXT)");
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS 'partieAEnvoyer' ('_id'INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,'timestamp' INTEGER NOT NULL,'hashPartie' TEXT NOT NULL,'clefPubliqueJ1' TEXT NOT NULL,'clefPubliqueJ2' TEXT NOT NULL,'clefPubliqueArbitre' TEXT NOT NULL CHECK('clefPubliqueJ1' <> 'clefPubliqueJ2' AND 'clefPubliqueJ1' <> 'clefPubliqueArbitre' AND 'clefPubliqueJ2' <> 'clefPubliqueArbitre'),'voteJ1' TEXT,'voteJ2' TEXT,'voteArbitre' TEXT,'signatureJ1' TEXT,'signatureJ2' TEXT,'signatureArbitre' TEXT)");
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS 'partie' ('_id'INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,'timestamp' INTEGER NOT NULL,'hashPartie' TEXT NOT NULL,'clefPubliqueJ1' TEXT NOT NULL,'clefPubliqueJ2' TEXT NOT NULL,'clefPubliqueArbitre' TEXT NOT NULL CHECK('clefPubliqueJ1' <> 'clefPubliqueJ2' AND 'clefPubliqueJ1' <> 'clefPubliqueArbitre' AND 'clefPubliqueJ2' <> 'clefPubliqueArbitre'),'voteJ1' TEXT,'voteJ2' TEXT,'voteArbitre' TEXT,'signatureJ1' TEXT,'signatureJ2' TEXT,'signatureArbitre' TEXT,'hashVote' TEXT)");
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS 'confirmation' ('_id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,'hashPartie' TEXT NOT NULL,'hashVote' TEXT NOT NULL,'clefPublique' TEXT NOT NULL,'signatureHashVote' TEXT NOT NULL)");
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS 'plainte' ('_id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,'hashPartie' TEXT NOT NULL,'hashVote' TEXT NOT NULL,'clefPublique' TEXT NOT NULL,'signatureHashVote' TEXT NOT NULL)");
                sqLiteDatabase.execSQL("CREATE TABLE 'partieDetail' ('_id' INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,'hashPartie' TEXT NOT NULL,'resultat' TEXT NOT NULL,'eloJ1' INTEGER NOT NULL,'eloJ2' INTEGER NOT NULL,'fiabiliteArbitre' REAL CHECK (fiabiliteArbitre BETWEEN 0.0 AND 1.0), 'eloGainJ1' INTEGER, 'eloGainJ2' INTEGER, 'arbitreGain' REAL, 'satisfactionArbitrage' INTEGER)");
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS 'compte'");
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS 'partieARecevoir'");
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS 'partieAEnvoyer'");
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS 'partie'");
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS 'confirmation'");
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS 'plainte'");

                onCreate(sqLiteDatabase);
            }
        };
        database = helper.getWritableDatabase();
        System.out.println("#2 - Execution de la création de la base de données ! ");

    }

    public void supprimerBaseDeDonnees(Context context) {
        System.out.println("On va supprimer siuuu");
         File dbFile = context.getDatabasePath(nomBaseDeDonnees);
        database = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

        database.execSQL("DROP TABLE IF EXISTS 'COMPTES'");
        database.execSQL("CREATE TABLE IF NOT EXISTS 'COMPTES' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, Pseudo TEXT NOT NULL UNIQUE, ClefPublique TEXT NOT NULL, ClefPrivee TEXT)");

    }
    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }
}
