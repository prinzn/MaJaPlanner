package de.ghse.projects.janap.shibaplanner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import yuku.ambilwarna.AmbilWarnaDialog;

public class TerminBearbeitung_Steuerung {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Attribute
    private TerminBearbeitung_GUI dieGui;
    private Calendar start, ende;
    private DatumAuswaehlen datumAuswaehlerDialogFragment;
    private ZeitAuswaehlen zeitAuswaehlenFragment;

    private SimpleDateFormat datumFormat = new SimpleDateFormat("dd.MM.yyyy");
    private SimpleDateFormat zeitFormat = new SimpleDateFormat("HH:mm");

    private final String logTag = "LogTag_TerminErstellung";
    private final String datumTag = "DatumEingeben";
    private final String zeitTag = "ZeitEingeben";

    private Boolean titelVorhanden = false;
    private Boolean ganztaegig;
    private int tempFarbe;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Konstruktor
    public TerminBearbeitung_Steuerung(TerminBearbeitung_GUI pGui) {
        dieGui = pGui;
        initialisieren();
        setzenDerDaten();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Methoden

    /*-------------------------Die On-Click Methoden----------------------------------------------*/
    public void abbruchGeklickt() {
        dieGui.oeffneActivityKalenderMonatsUebersicht();
    }

    public void speichernGeklickt() {
        Boolean etwasFehlt = terminDatenVollstaendigUeberpruefen();
        Boolean irgendwasIstFalsch = terminDatenAufFehlerUeberpruefen();
        if (!etwasFehlt) {
            if (!irgendwasIstFalsch) {
                terminDatenSpeichern();
            }
        }
    }

    public void switchGanztaegigGeklickt() {
        if (ganztaegig) {
            ganztaegig = false;
            dieGui.lasseUnwichtigesVerschwinden();
        } else if (!ganztaegig) {
            ganztaegig = true;
            dieGui.allesSichtbarMachen();
        }
    }

    public void titelEingegeben(Editable s) {
        if (s.length() > 0) {
            titelVorhanden = true;
        }

    }

    public void datumZeitAngabeGeklickt(View v) {
        try {
            int id = v.getId();
            v.setEnabled(false);
            if (id == R.id.txt_Terminerstellung_Eingabe_Datum_Start) {
                datumAuswaehlerDialogFragment.setKennzeichnung(DatumAuswaehlen.KENNZEICHNUNG_START_DATUM);
                datumAuswaehlerDialogFragment.show(dieGui.getSupportFragmentManager(), datumTag);   //Datepicker wird geöffnet
            } else if (id == R.id.txt_Terminerstellung_Eingabe_Datum_Ende) {
                datumAuswaehlerDialogFragment.setKennzeichnung(DatumAuswaehlen.KENNZEICHNUNG_ENDE_DATUM);
                datumAuswaehlerDialogFragment.show(dieGui.getSupportFragmentManager(), datumTag);   //Datepicker wird geöffnet
            } else if (id == R.id.txt_Terminerstellung_Eingabe_Zeit_Start) {
                zeitAuswaehlenFragment.setKennzeichnung(ZeitAuswaehlen.KENNZEICHNUNG_START_ZEIT);
                zeitAuswaehlenFragment.show(dieGui.getSupportFragmentManager(), zeitTag);   //Timepicker wird geöffnet
            } else if (id == R.id.txt_Terminerstellung_Eingabe_Zeit_Ende) {
                zeitAuswaehlenFragment.setKennzeichnung(ZeitAuswaehlen.KENNZEICHNUNG_ENDE_ZEIT);
                zeitAuswaehlenFragment.show(dieGui.getSupportFragmentManager(), zeitTag);   //Timepicker wird geöffnet
            }
            v.setEnabled(true);
        } catch (IllegalStateException e) {
            Log.e(logTag, "Error bei DatumZeitAngabe: " + e.getMessage());
        }
    }

    public void datumGeklickt(int jahr, int monat, int tag) {
        if (datumAuswaehlerDialogFragment.getKennzeichnung() == DatumAuswaehlen.KENNZEICHNUNG_START_DATUM) {
            start.set(jahr, monat, tag);    //Startangaben setzten
            dieGui.getStartDatum().setText(datumFormat.format(start.getTime()));    //eingegebene Startangaben anzeigen
        } else if (datumAuswaehlerDialogFragment.getKennzeichnung() == DatumAuswaehlen.KENNZEICHNUNG_ENDE_DATUM) {
            ende.set(jahr, monat, tag);     //Endangaben setzen
            dieGui.getEndDatum().setText(datumFormat.format(ende.getTime()));   //eingegebene Endangaben anzeigen
        }
    }

    public void zeitGeklickt(int stunde, int minute) {
        if (zeitAuswaehlenFragment.getKennzeichnung() == ZeitAuswaehlen.KENNZEICHNUNG_START_ZEIT) {

            //Startangaben setzen
            start.set(Calendar.HOUR_OF_DAY, stunde);
            start.set(Calendar.MINUTE, minute);

            dieGui.getStartZeit().setText(zeitFormat.format(start.getTime()));  //eingegebene Startangaben anzeigen

        } else if (zeitAuswaehlenFragment.getKennzeichnung() == ZeitAuswaehlen.KENNZEICHNUNG_ENDE_ZEIT) {

            //Endangaben setzten
            ende.set(Calendar.HOUR_OF_DAY, stunde);
            ende.set(Calendar.MINUTE, minute);

            dieGui.getEndZeit().setText(zeitFormat.format(ende.getTime())); //eingegebene Endangaben anzeigen
        }
    }

    public void oeffneColorPicker() {
        tempFarbe = Color.parseColor("#394046");
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(dieGui, tempFarbe, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                tempFarbe = color;
                dieGui.getOeffneColorPicker().setBackgroundColor(tempFarbe);
            }
        });
        colorPicker.show();
    }

    /*-------------------------public Methoden----------------------------------------------------*/


    /*-------------------------private Methoden---------------------------------------------------*/
    private void setzenDerDaten() {
        Termin termin = dieGui.getTermin();
        dieGui.getTitel().setText(termin.getTitel());
        dieGui.getStartZeit().setText(zeitFormat.format(termin.getStart().getTime()));
        dieGui.getStartDatum().setText(datumFormat.format(termin.getStart().getTime()));
        ganztaegig = termin.getGanztaegig();
        switchGanztaegigGeklickt();
        dieGui.getEndZeit().setText(zeitFormat.format(termin.getEnde().getTime()));
        dieGui.getEndDatum().setText(datumFormat.format(termin.getEnde().getTime()));

        dieGui.getOeffneColorPicker().setBackgroundColor(termin.getFarbe());
    }

    private void initialisieren() {
        start = dieGui.getTermin().getStart();
        ende = dieGui.getTermin().getEnde();

        ganztaegig = dieGui.getTermin().getGanztaegig();
        datumAuswaehlerDialogFragment = new DatumAuswaehlen();
        zeitAuswaehlenFragment = new ZeitAuswaehlen();
    }

    private Boolean terminDatenAufFehlerUeberpruefen() {
        Boolean fehlerVorhanden;

        if (ganztaegig) {
            fehlerVorhanden = false;

        } else {
            //Start muss vor Ende sein
            long zeitInMiliStart = start.getTimeInMillis();
            long zeitInMiliEnde = ende.getTimeInMillis();

            if (zeitInMiliStart > zeitInMiliEnde) {
                fehlerVorhanden = true;
                Toast toast = Toast.makeText(dieGui, R.string.toastDatenFalsch, Toast.LENGTH_SHORT);
                toast.show();
            } else {
                fehlerVorhanden = false;
            }
        }
        return fehlerVorhanden;
    }

    private Boolean terminDatenVollstaendigUeberpruefen() {
        Boolean titelFehlt = false;
        if (dieGui.getTitel().getText() == null) {

            //Wenn kein Titel vorhanden Titel -> unterschiedliche Fehlermeldungen:
            dieGui.getTitel().setHintTextColor(Color.parseColor("#FFB60003"));  //Rot färben

            Vibrator v = (Vibrator) dieGui.getSystemService(Context.VIBRATOR_SERVICE);  // Eine Instanz von Vibrator im momentanen Context
            v.vibrate(300); // viebriere für 300 ms

            Toast toast = Toast.makeText(dieGui, R.string.toastTitelFehlt, Toast.LENGTH_SHORT);
            toast.show();

            titelFehlt = true;
        }
        return titelFehlt;

    }

    private void terminDatenSpeichern() {
        dieGui.getDataSource().open();  //Datenbank öffnen

        if (ganztaegig) {
            //StartUhrzeit = 0:00 Uhr
            start.set(Calendar.HOUR_OF_DAY, 0);
            start.set(Calendar.MINUTE, 0);

            //Ende = Start, da Ganztägig; Endzeit = 23:59 Uhr
            ende.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH), 23, 59);

            //Farbe des Termins festlegen
            int farbe = ((ColorDrawable) dieGui.getOeffneColorPicker().getBackground()).getColor();

            //Neuer Termin in Datenbank
            dieGui.getDataSource().aendereTermin(dieGui.getId(), dieGui.getTitel().getText().toString(), start, ende, "true", farbe);

        } else if (!ganztaegig) {

            //Farbe des Termins festlegen
            int farbe = ((ColorDrawable) dieGui.getOeffneColorPicker().getBackground()).getColor();

            //Neuer Termin in Datenbank
            dieGui.getDataSource().aendereTermin(dieGui.getId(), dieGui.getTitel().getText().toString(), start, ende, "false", farbe);
        }
        dieGui.getDataSource().close(); //Datenbank schließen

        Toast toast = Toast.makeText(dieGui, R.string.toastTerminGespeichert, Toast.LENGTH_SHORT);
        toast.show();

        Log.d(logTag, "Neuer Termin gespeichert: " + dieGui.getTitel().getText());

        dieGui.oeffneActivityKalenderMonatsUebersicht();


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Ender der Klasse
}
