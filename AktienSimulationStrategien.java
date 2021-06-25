package aktienSimulation;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.Period;

public class AktienSimulationStrategien {

    private static float depot;
    private static LocalDate startdate;
    private static LocalDate enddate;

    public AktienSimulationStrategien(float depot, LocalDate startdate, LocalDate enddate){
        this.depot = depot;
        this.startdate = startdate;
        this.enddate = enddate;
    }

    public static float buyAndHold(String aktie, Connection con){
        float closeWert = 0;
        float tempdepot = depot;
        int tempaktien = 0;
        Period diffdays = Period.between(startdate, enddate);
        int laenge = diffdays.getDays();
        int splitwert = 0;
        boolean gekauft = false;

        closeWert = DatenbankAktienSimulation.ueberpruefendatenbank(startdate, aktie, con);

        tempaktien = (int) (tempdepot / closeWert);
        tempdepot = tempdepot - (closeWert * tempaktien);
        gekauft = true;
        DatenbankAktienSimulation.datenbankeingabeBuyAndHold(aktie, startdate, tempdepot, tempaktien, gekauft, con);

        for(int i = 0; i < laenge; i++){
            splitwert = DatenbankAktienSimulation.datenbankDatenSplit(startdate.plusDays(i), aktie, con);
            if(splitwert != 0){
                tempaktien = tempaktien * splitwert;
            }
        }

        closeWert = DatenbankAktienSimulation.ueberpruefendatenbank(enddate, aktie, con);
        tempdepot = tempdepot + (closeWert * tempaktien);
        tempaktien = 0;
        gekauft = false;
        DatenbankAktienSimulation.datenbankeingabeBuyAndHold(aktie, enddate, tempdepot, tempaktien, gekauft, con);

        return tempdepot;
    }

    public static float d200Schnitt(String aktie, Connection con){
        float closeWert = 0;
        float d200 = 0;
        float tempdepot = depot;
        int tempaktien = 0;
        int laenge = 0;
        boolean gekauft = false;
        int splitwert = 0;

        Period diffdays = Period.between(startdate, enddate);
        laenge = diffdays.getDays();

        for(int i = 0; i < laenge; i++){
            closeWert = DatenbankAktienSimulation.datenbankDaten(startdate.plusDays(i), aktie, con);
            d200 = DatenbankAktienSimulation.datenbankDaten200(startdate.plusDays(i), aktie, con);
            splitwert = DatenbankAktienSimulation.datenbankDatenSplit(startdate.plusDays(i), aktie, con);

            if(splitwert != 0){
                tempaktien = tempaktien * splitwert;
            }

            if(closeWert != 0) {
                if(gekauft == false) {
                    if(closeWert >= d200) {
                        tempaktien = (int) (tempdepot / closeWert);
                        tempdepot = tempdepot - (closeWert * tempaktien);
                        gekauft = true;
                        DatenbankAktienSimulation.datenbankeingabe200Schnitt(aktie, startdate.plusDays(i), tempdepot, tempaktien, gekauft, con);
                    }
                }else{
                    if(closeWert <= d200){
                        tempdepot = tempdepot + (tempaktien * closeWert);
                        tempaktien = 0;
                        gekauft = false;
                        DatenbankAktienSimulation.datenbankeingabe200Schnitt(aktie, startdate.plusDays(i), tempdepot, tempaktien, gekauft, con);
                    }
                }
            }
        }

        if(gekauft == true){
            closeWert = DatenbankAktienSimulation.ueberpruefendatenbank(enddate, aktie,con);

            tempdepot = tempdepot + (tempaktien * closeWert);
            tempaktien = 0;
            gekauft = false;
            DatenbankAktienSimulation.datenbankeingabe200Schnitt(aktie, enddate, tempdepot, tempaktien, gekauft, con);
        }

        return tempdepot;
    }

    public static float prozent(String aktie, Connection con){
        float closeWert = 0;
        float d200 = 0;
        float tempdepot = depot;
        int tempaktien = 0;
        int laenge = 0;
        boolean gekauft = false;
        float prozente = 0;
        int splitwert = 0;

        Period diffdays = Period.between(startdate, enddate);
        laenge = diffdays.getDays();

        for(int i = 0; i < laenge; i++){
            closeWert = DatenbankAktienSimulation.datenbankDaten(startdate.plusDays(i), aktie, con);
            d200 = DatenbankAktienSimulation.datenbankDaten200(startdate.plusDays(i), aktie, con);
            prozente = (d200/100) * 3;
            splitwert = DatenbankAktienSimulation.datenbankDatenSplit(startdate.plusDays(i), aktie, con);

            if(splitwert != 0){
                tempaktien = tempaktien * splitwert;
            }

            if(closeWert != 0) {
                if(gekauft == false) {
                    if(closeWert >= (d200 + prozente)) {
                        tempaktien = (int) (tempdepot / closeWert);
                        tempdepot = tempdepot - (closeWert * tempaktien);
                        gekauft = true;
                        DatenbankAktienSimulation.datenbankeingabeProzent(aktie, startdate.plusDays(i), tempdepot, tempaktien, gekauft, con);
                    }
                }else{
                    if(closeWert <= (d200 - prozente)){
                        tempdepot = tempdepot + (tempaktien * closeWert);
                        tempaktien = 0;
                        gekauft = false;
                        DatenbankAktienSimulation.datenbankeingabeProzent(aktie, startdate.plusDays(i), tempdepot, tempaktien, gekauft, con);
                    }
                }
            }
        }

        if(gekauft == true){
            closeWert = DatenbankAktienSimulation.ueberpruefendatenbank(enddate, aktie, con);

            tempdepot = tempdepot + (tempaktien * closeWert);
            tempaktien = 0;
            gekauft = false;
            DatenbankAktienSimulation.datenbankeingabeProzent(aktie, enddate, tempdepot, tempaktien, gekauft, con);
        }

        return tempdepot;
    }
}
