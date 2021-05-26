package aktienSimulation;

import com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Scanner;

public class AktienSimulationAusfuehrung {

    private static float depot = 100000;
    private static ArrayList<String> aktien = new ArrayList<String>();
    private static String datei ="DateiEinlesen.txt";
    private static LocalDate startdate = LocalDate.ofYearDay(2010, 1);
    private static LocalDate enddate = LocalDate.now().minusDays(1);

    final public static String hostname = "localhost";
    final public static String port = "3306";
    final public static String db = "aktien";
    final public static String user = "root";
    final public static String password = "";

    public static void main(String[] args) {
        ladeDatei(datei);
        tabelleErstellen();

        for(int i = 0; i < aktien.size(); i++){
            ausgabe(aktien.get(i));
        }
    }

    public static void ladeDatei(String datName) {
        File file = new File(datName);

        if(!file.canRead() || !file.isFile()) System.exit(0);

        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(datName));
            String zeile = null;
            while((zeile = in.readLine()) != null) {
                aktien.add(zeile);
            }
        }catch(IOException e) {
            e.printStackTrace();
        }finally {
            if(in != null) {
                try {
                    in.close();
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean ueberpruefen(LocalDate d){
        if(d.getDayOfWeek().equals("SATURDAY")) return false;
        if(d.getDayOfWeek().equals("SUNDAY")) return false;
        return true;
    }

    public static void tabelleErstellen() {
        Connection con = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch(Exception e) {
            System.out.println();
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection("jdbc:mysql://"+hostname+"/"+db+"?user="+user+"&password="+password+"&serverTimezone=UTC");
            Statement myStat = con.createStatement();
            String sql = "CREATE TABLE if not exists KaufstatistikBuyAndHold (Datum date not null Primary Key, depotwert double, aktienwert double, aktie varchar(50),gekauft bool)";
            myStat.execute(sql);

            Statement myStat1 = con.createStatement();
            String sql1 = "CREATE TABLE if not exists Kaufstatistik200Schnitt (Datum date not null Primary Key, depotwert double, aktienwert double, aktie varchar(50),gekauft bool)";
            myStat.execute(sql);

            Statement myStat2 = con.createStatement();
            String sql2 = "CREATE TABLE if not exists KaufstatistikProzent (Datum date not null Primary Key, depotwert double, aktienwert double, aktie varchar(50),gekauft bool)";
            myStat.execute(sql);

            con.close();
        }
        catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("Error: " + sqle.getErrorCode());
            sqle.printStackTrace();
        }
    }

    public static void datenbankeingabeBuyAndHold(String aktie, LocalDate d, float depotwert, float aktienwert, boolean gekauft) {
        Connection con = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection("jdbc:mysql://"+hostname+"/"+db+"?user="+user+"&password="+password+"&serverTimezone=UTC");
            Statement myStat = con.createStatement();

            PreparedStatement pstatement = con.prepareStatement("INSERT INTO KaufstatistikBuyAndHold values( ?, ?, ?, ?, ?)");
            pstatement.setDate(1, Date.valueOf(d));
            pstatement.setDouble(2, depotwert);
            pstatement.setDouble(3, aktienwert);
            pstatement.setString(4, aktie);
            pstatement.setBoolean(5, gekauft);
            pstatement.executeUpdate();

            con.close();
        }
        catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("Error: " + sqle.getErrorCode());
            sqle.printStackTrace();
        }
    }

        public static void datenbankeingabe200Schnitt(String aktie, LocalDate d, float depotwert, float aktienwert, boolean gekauft) {
         Connection con = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection("jdbc:mysql://"+hostname+"/"+db+"?user="+user+"&password="+password+"&serverTimezone=UTC");
            Statement myStat = con.createStatement();

            PreparedStatement pstatement = con.prepareStatement("INSERT INTO Kaufstatistik200Schnitt values( ?, ?, ?, ?, ?)");
            pstatement.setDate(1, Date.valueOf(d));
            pstatement.setDouble(2, depotwert);
            pstatement.setDouble(3, aktienwert);
            pstatement.setString(4, aktie);
            pstatement.setBoolean(5, gekauft);
            pstatement.executeUpdate();

            con.close();
        }
        catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("Error: " + sqle.getErrorCode());
            sqle.printStackTrace();
        }
    }

        public static void datenbankeingabeProzent(String aktie, LocalDate d, float depotwert, float aktienwert, boolean gekauft) {
         Connection con = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection("jdbc:mysql://"+hostname+"/"+db+"?user="+user+"&password="+password+"&serverTimezone=UTC");
            Statement myStat = con.createStatement();

            PreparedStatement pstatement = con.prepareStatement("INSERT INTO KaufstatistikProzent values( ?, ?, ?, ?, ?)");
            pstatement.setDate(1, Date.valueOf(d));
            pstatement.setDouble(2, depotwert);
            pstatement.setDouble(3, aktienwert);
            pstatement.setString(4, aktie);
            pstatement.setBoolean(5, gekauft);
            pstatement.executeUpdate();

            con.close();
        }
        catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("Error: " + sqle.getErrorCode());
            sqle.printStackTrace();
        }
    }

    public static int datenbankDatenSplit(LocalDate d, String aktie) {
        if(!ueberpruefen(d)) return 0;
        String temp;
        int splitwert = 0;
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + hostname + "/" + db + "?user=" + user + "&password=" + password + "&serverTimezone=UTC");
            Statement myStat = con.createStatement();
            ResultSet reSe = myStat.executeQuery("Select splitwert from " + aktie + "where Datum = " + d + ";");
            if (reSe.next()) {
                temp = reSe.getString(1);
                splitwert = Integer.parseInt(temp);
                return splitwert;
            } else {
                return 0;
            }

        } catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("Error: " + sqle.getErrorCode());
            sqle.printStackTrace();
        }finally{
            try {
                con.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return splitwert;
    }

    public static float datenbankDaten(LocalDate d, String aktie) {
        if(!ueberpruefen(d)) return 0;
        String temp;
        float closeWert = 0;
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + hostname + "/" + db + "?user=" + user + "&password=" + password + "&serverTimezone=UTC");
            Statement myStat = con.createStatement();
            ResultSet reSe = myStat.executeQuery("Select closeWert from " + aktie + "where Datum = " + d + ";");
            if (reSe.next()) {
                temp = reSe.getString(1);
                closeWert = Float.parseFloat(temp);
                return closeWert;
            } else {
                return 0;
            }

        } catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("Error: " + sqle.getErrorCode());
            sqle.printStackTrace();
        }finally{
            try {
                con.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return closeWert;
    }

    public static float datenbankDaten200(LocalDate d, String aktie) {
        if(!ueberpruefen(d)) return 0;
        String temp;
        float d200Schnitt = 0;
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + hostname + "/" + db + "?user=" + user + "&password=" + password + "&serverTimezone=UTC");
            Statement myStat = con.createStatement();
            ResultSet reSe = myStat.executeQuery("Select 200Schnitt from 200Schnitt" + aktie + "where Datum = " + d + ";");
            if (reSe.next()) {
                temp = reSe.getString(1);
                d200Schnitt = Float.parseFloat(temp);
                return d200Schnitt;
            } else {
                return 0;
            }

        } catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("Error: " + sqle.getErrorCode());
            sqle.printStackTrace();
        }finally{
            try {
                con.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return d200Schnitt;
    }

    public static float ueberpruefendatenbank(LocalDate d, String aktie){
        float closeWert = 0;

        closeWert = datenbankDaten(d, aktie);

        if(closeWert == 0){
            d = d.plusDays(1);
            closeWert = datenbankDaten(d, aktie);
            if(closeWert == 0){
                d = d.plusDays(1);
                closeWert = datenbankDaten(d, aktie);
            }
        }

        return closeWert;
    }

    public static float ueberpruefendatenbank200(LocalDate d, String aktie){
        float d200Schnitt = 0;

        d200Schnitt = datenbankDaten200(d, aktie);

        if(d200Schnitt == 0){
            d = d.plusDays(1);
            d200Schnitt = datenbankDaten200(d, aktie);
            if(d200Schnitt == 0){
                d = d.plusDays(1);
                d200Schnitt = datenbankDaten200(d, aktie);
            }
        }

        return d200Schnitt;
    }

    public static float buyAndHold(String aktie){
        float closeWert = 0;
        float tempdepot = depot;
        float tempaktien = 0;
        Period diffdays = Period.between(startdate, enddate);
        int laenge = diffdays.getDays();
        int splitwert = 0;
        boolean gekauft = false;

        closeWert = ueberpruefendatenbank(startdate, aktie);

        tempaktien = closeWert/tempdepot;
        tempdepot = tempdepot - (closeWert * tempaktien);
        gekauft = true;
        datenbankeingabeBuyAndHold(aktie, startdate, tempdepot, tempaktien, gekauft);

        for(int i = 0; i < laenge; i++){
            splitwert = datenbankDatenSplit(startdate.plusDays(i), aktie);
            if(splitwert != 0){
                tempaktien = tempaktien * splitwert;
            }
        }

        closeWert = ueberpruefendatenbank(enddate, aktie);
        tempdepot = tempdepot + (closeWert * tempaktien);
        tempaktien = 0;
        gekauft = false;
        datenbankeingabeBuyAndHold(aktie, enddate, tempdepot, tempaktien, gekauft);

        return tempdepot;
    }

    public static float d200Schnitt(String aktie){
        float closeWert = 0;
        float d200 = 0;
        float tempdepot = depot;
        float tempaktien = 0;
        int laenge = 0;
        boolean gekauft = false;
        int splitwert = 0;

        Period diffdays = Period.between(startdate, enddate);
        laenge = diffdays.getDays();

        for(int i = 0; i < laenge; i++){
            closeWert = datenbankDaten(startdate.plusDays(i), aktie);
            d200 = datenbankDaten200(startdate.plusDays(i), aktie);
            splitwert = datenbankDatenSplit(startdate.plusDays(i), aktie);

            if(splitwert != 0){
                tempaktien = tempaktien * splitwert;
            }

            if(closeWert != 0) {
                if(gekauft == false) {
                    if(closeWert >= d200) {
                        tempaktien = closeWert / tempdepot;
                        tempdepot = tempdepot - (closeWert * tempaktien);
                        gekauft = true;
                        datenbankeingabe200Schnitt(aktie, startdate.plusDays(i), tempdepot, tempaktien, gekauft);
                    }
                }else{
                    if(closeWert <= d200){
                        tempdepot = tempdepot + (tempaktien * closeWert);
                        tempaktien = 0;
                        gekauft = false;
                        datenbankeingabe200Schnitt(aktie, startdate.plusDays(i), tempdepot, tempaktien, gekauft);
                    }
                }
            }
        }

        if(gekauft == true){
            closeWert = ueberpruefendatenbank(enddate, aktie);

            tempdepot = tempdepot + (tempaktien * closeWert);
            tempaktien = 0;
            gekauft = false;
            datenbankeingabe200Schnitt(aktie, enddate, tempdepot, tempaktien, gekauft);
        }

        return tempdepot;
    }

    public static float prozent(String aktie){
        float closeWert = 0;
        float d200 = 0;
        float tempdepot = depot;
        float tempaktien = 0;
        int laenge = 0;
        boolean gekauft = false;
        float prozente = 0;
        int splitwert = 0;

        Period diffdays = Period.between(startdate, enddate);
        laenge = diffdays.getDays();

        for(int i = 0; i < laenge; i++){
            closeWert = datenbankDaten(startdate.plusDays(i), aktie);
            d200 = datenbankDaten200(startdate.plusDays(i), aktie);
            prozente = (d200/100) * 3;
            splitwert = datenbankDatenSplit(startdate.plusDays(i), aktie);

            if(splitwert != 0){
                tempaktien = tempaktien * splitwert;
            }

            if(closeWert != 0) {
                if(gekauft == false) {
                    if(closeWert >= (d200 + prozente)) {
                        tempaktien = closeWert / tempdepot;
                        tempdepot = tempdepot - (closeWert * tempaktien);
                        gekauft = true;
                        datenbankeingabeProzent(aktie, startdate.plusDays(i), tempdepot, tempaktien, gekauft);
                    }
                }else{
                    if(closeWert <= (d200 - prozente)){
                        tempdepot = tempdepot + (tempaktien * closeWert);
                        tempaktien = 0;
                        gekauft = false;
                        datenbankeingabeProzent(aktie, startdate.plusDays(i), tempdepot, tempaktien, gekauft);
                    }
                }
            }
        }

        if(gekauft == true){
            closeWert = ueberpruefendatenbank(enddate, aktie);

            tempdepot = tempdepot + (tempaktien * closeWert);
            tempaktien = 0;
            gekauft = false;
            datenbankeingabeProzent(aktie, enddate, tempdepot, tempaktien, gekauft);
        }

        return tempdepot;
    }

    public static void ausgabe(String aktie){
        float depotBuyAndHold = buyAndHold(aktie);
        float depot200Schnitt = d200Schnitt(aktie);
        float depotProzent = prozent(aktie);

        System.out.println("Zeit des Versuches: Von " + startdate + " bis " + enddate);
        System.out.println("Mit folgenden Aktien wurden Berechnungen durchgeführt: ");
        for(int i = 0; i < aktien.size(); i++) {
            System.out.println(aktien.get(i));
        }
        System.out.println("Ergebnisse:");
        System.out.println("Buy and Hold: " + depotBuyAndHold);
        System.out.println("Mit 200-Schnitt:" + depot200Schnitt);
        System.out.println("Mit Prozent-Methode: " + depotProzent);
    }
}
