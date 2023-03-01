package com.example.chessleaderboardblockchain;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.sql.DriverManager;
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

                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS 'COMPTES' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, Pseudo TEXT NOT NULL UNIQUE, ClefPublique TEXT NOT NULL, ClefPrivee TEXT)");
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS 'PARTIES' ( '_id' INTEGER NOT NULL PRIMARY KEY UNIQUE, Timestamp TEXT NOT NULL , HashPartie TEXT NOT NULL, ClefPubliqueJ1 TEXT NOT NULL, ClefPubliqueJ2 TEXT NOT NULL, ClefPubliqueArbitre TEXT NOT NULL CHECK (ClefPubliqueJ1 <> ClefPubliqueJ2 AND ClefPubliqueJ1 <> ClefPubliqueArbitre AND ClefPubliqueJ2 <> ClefPubliqueArbitre), VoteJ1 TEXT, VoteJ2 TEXT, VoteArbitre TEXT )");

            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS 'COMPTES'");
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS 'PARTIES'");

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
