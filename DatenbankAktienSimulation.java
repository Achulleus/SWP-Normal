package aktienSimulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DatenbankAktienSimulation {

    public static String hostname;
    public static String port;
    public static String db;
    public static String user;
    public static String password;

    public static Connection conAufbau() throws SQLException {
        Connection con = null;
        con = DriverManager.getConnection("jdbc:mysql://"+hostname+"/"+db+"?user="+user+"&password="+password+"&serverTimezone=UTC");
        return con;
    }

    public static void conAbbau(Connection con) throws SQLException {
        con.close();
    }

    public static void ladeDateiDat(String datName) {
        File file = new File(datName);

        if(!file.canRead() || !file.isFile()) System.exit(0);

        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(datName));
            String zeile = null;
            zeile = in.readLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            if(zeile != null) hostname = zeile;
            zeile = in.readLine();
            if(zeile != null) port = zeile;
            zeile = in.readLine();
            if(zeile != null) db = zeile;
            zeile = in.readLine();
            if(zeile != null) user = zeile;
            zeile = in.readLine();
            if(zeile != null) password = zeile;
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

    public static void tabelleErstellen(Connection con) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch(Exception e) {
            System.out.println();
            e.printStackTrace();
        }
        try {
            Statement myStat = con.createStatement();
            String sql = "CREATE TABLE if not exists KaufstatistikBuyAndHold (Datum date not null Primary Key, depotwert double, aktienwert double, aktie varchar(50),gekauft bool)";
            myStat.execute(sql);

            Statement myStat1 = con.createStatement();
            String sql1 = "CREATE TABLE if not exists Kaufstatistik200Schnitt (Datum date not null Primary Key, depotwert double, aktienwert double, aktie varchar(50),gekauft bool)";
            myStat.execute(sql);

            Statement myStat2 = con.createStatement();
            String sql2 = "CREATE TABLE if not exists KaufstatistikProzent (Datum date not null Primary Key, depotwert double, aktienwert double, aktie varchar(50),gekauft bool)";
            myStat.execute(sql);
        }
        catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("Error: " + sqle.getErrorCode());
            sqle.printStackTrace();
        }
    }

    public static void datenbankeingabeBuyAndHold(String aktie, LocalDate d, float depotwert, float aktienwert, boolean gekauft, Connection con) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Statement myStat = con.createStatement();

            PreparedStatement pstatement = con.prepareStatement("INSERT INTO KaufstatistikBuyAndHold values( ?, ?, ?, ?, ?)");
            pstatement.setDate(1, Date.valueOf(d));
            pstatement.setDouble(2, depotwert);
            pstatement.setDouble(3, aktienwert);
            pstatement.setString(4, aktie);
            pstatement.setBoolean(5, gekauft);
            pstatement.executeUpdate();
        }
        catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("Error: " + sqle.getErrorCode());
            sqle.printStackTrace();
        }
    }

    public static void datenbankeingabe200Schnitt(String aktie, LocalDate d, float depotwert, float aktienwert, boolean gekauft, Connection con) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Statement myStat = con.createStatement();

            PreparedStatement pstatement = con.prepareStatement("INSERT INTO Kaufstatistik200Schnitt values( ?, ?, ?, ?, ?)");
            pstatement.setDate(1, Date.valueOf(d));
            pstatement.setDouble(2, depotwert);
            pstatement.setDouble(3, aktienwert);
            pstatement.setString(4, aktie);
            pstatement.setBoolean(5, gekauft);
            pstatement.executeUpdate();
        }
        catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("Error: " + sqle.getErrorCode());
            sqle.printStackTrace();
        }
    }

    public static void datenbankeingabeProzent(String aktie, LocalDate d, float depotwert, float aktienwert, boolean gekauft, Connection con) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Statement myStat = con.createStatement();

            PreparedStatement pstatement = con.prepareStatement("INSERT INTO KaufstatistikProzent values( ?, ?, ?, ?, ?)");
            pstatement.setDate(1, Date.valueOf(d));
            pstatement.setDouble(2, depotwert);
            pstatement.setDouble(3, aktienwert);
            pstatement.setString(4, aktie);
            pstatement.setBoolean(5, gekauft);
            pstatement.executeUpdate();
        }
        catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("Error: " + sqle.getErrorCode());
            sqle.printStackTrace();
        }
    }

    public static int datenbankDatenSplit(LocalDate d, String aktie, Connection con) {
        if(!ueberpruefen(d)) return 0;
        String temp;
        int splitwert = 0;

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Statement myStat = con.createStatement();
            ResultSet reSe = myStat.executeQuery("Select splitwert from " + aktie + " where Datum = " + d + ";");
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
        }
        return splitwert;
    }

    public static float datenbankDaten(LocalDate d, String aktie, Connection con) {
        if(!ueberpruefen(d)) return 0;
        String temp;
        float closeWert = 0;

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
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
        }
        return closeWert;
    }

    public static float datenbankDaten200(LocalDate d, String aktie, Connection con) {
        if(!ueberpruefen(d)) return 0;
        String temp;
        float d200Schnitt = 0;

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
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
        }
        return d200Schnitt;
    }

    public static float ueberpruefendatenbank(LocalDate d, String aktie, Connection con){
        float closeWert = 0;

        closeWert = datenbankDaten(d, aktie, con);

        if(closeWert == 0){
            d = d.plusDays(1);
            closeWert = datenbankDaten(d, aktie, con);
            if(closeWert == 0){
                d = d.plusDays(1);
                closeWert = datenbankDaten(d, aktie, con);
            }
        }

        return closeWert;
    }

    public static float ueberpruefendatenbank200(LocalDate d, String aktie, Connection con){
        float d200Schnitt = 0;

        d200Schnitt = datenbankDaten200(d, aktie, con);

        if(d200Schnitt == 0){
            d = d.plusDays(1);
            d200Schnitt = datenbankDaten200(d, aktie, con);
            if(d200Schnitt == 0){
                d = d.plusDays(1);
                d200Schnitt = datenbankDaten200(d, aktie, con);
            }
        }

        return d200Schnitt;
    }

    public static ArrayList<LocalDate> datenbankDatenDatum(LocalDate d, String aktie, Connection con) {
        if(!ueberpruefen(d)) return null;
        String temp;
        LocalDate datum;
        ArrayList<LocalDate> daten = new ArrayList<LocalDate>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Statement myStat = con.createStatement();
            ResultSet reSe = myStat.executeQuery("Select Datum from " + aktie + " order by Datum;");
            if (reSe.next()) {
                temp = reSe.getString(1);
                datum = LocalDate.parse(temp);
                daten.add(datum);
            }
            return daten;

        } catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("Error: " + sqle.getErrorCode());
            sqle.printStackTrace();
        }
        return null;
    }
}
