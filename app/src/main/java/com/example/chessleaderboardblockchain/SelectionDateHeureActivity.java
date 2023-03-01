package com.example.chessleaderboardblockchain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SelectionDateHeureActivity extends AppCompatActivity {
    CalendarView calendarView;
    EditText editTextHeure;
    EditText editTextMinute;
    Button btnValiderDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_date_heure);
        calendarView = findViewById(R.id.calendarView);
        editTextHeure = findViewById(R.id.editTextHeure);
        editTextMinute = findViewById(R.id.editTextMinute);
        btnValiderDate = findViewById(R.id.btnValiderDate);
        Intent x = getIntent();
        System.out.println(x.getStringExtra("id"));
        System.out.println(x.getStringExtra("ClefPrivee"));
        System.out.println(x.getStringExtra("ClefPublique"));
        final String[] DateChoisie = {""};

        String[] listeNomParticipants = {"","",""};
        if (x.getStringArrayExtra("listeParticipants") != null){
            listeNomParticipants = x.getStringArrayExtra("listeParticipants");
        }

        String[] finalListeNomParticipants = listeNomParticipants;

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                DateChoisie[0] = String.valueOf(i2) + "/" + String.valueOf(i1+1) + "/" + String.valueOf(i);
                Toast.makeText(SelectionDateHeureActivity.this, "Date sélectionnée : " + i2 + "/" + (i1+1) + "/" + i, Toast.LENGTH_SHORT).show();
            }
        });
        btnValiderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(editTextHeure.getText().toString()) > 23 ||Integer.parseInt(editTextHeure.getText().toString()) < 0 || Integer.parseInt(editTextMinute.getText().toString()) > 59 || Integer.parseInt(editTextMinute.getText().toString()) < 0 || DateChoisie[0].equals("")) {
                    Toast.makeText(SelectionDateHeureActivity.this, "Veuillez entrer une heure et une minute valide et cliquer sur la date", Toast.LENGTH_SHORT).show();
                }else{

                    String time = "";
                    time = DateChoisie[0] + " " + editTextHeure.getText().toString() + ":" + editTextMinute.getText().toString();
                    System.out.println(time);

                    Intent newintent = new Intent(SelectionDateHeureActivity.this,AjouterPartieActivity.class);
                    newintent.putExtra("id", x.getStringExtra("id"));
                    newintent.putExtra("ClefPrivee", x.getStringExtra("ClefPrivee"));
                    newintent.putExtra("ClefPublique", x.getStringExtra("ClefPublique"));
                    newintent.putExtra("listeParticipants", finalListeNomParticipants);
                    newintent.putExtra("Time", time);
                    startActivity(newintent);

                }
            }
        });

    }
}