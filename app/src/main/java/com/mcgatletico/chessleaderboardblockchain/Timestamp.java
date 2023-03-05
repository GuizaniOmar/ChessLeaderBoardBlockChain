package com.mcgatletico.chessleaderboardblockchain;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Timestamp {

    // Récupérer le timestamp actuel
    public static String getCurrentTimestamp() {
        return Long.toString(System.currentTimeMillis());
    }

    // Convertir une date et heure en timestamp
    public static String convertToTimestamp(String dateString) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = dateFormat.parse(dateString);
        return Long.toString(date.getTime());
    }
}

