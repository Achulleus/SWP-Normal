package aktienSimulation;

import java.sql.*;
import java.time.LocalDate;

public class DatenbankAktienSimulation {

    final public static String hostname = "localhost";
    final public static String port = "3306";
    final public static String db = "aktien";
    final public static String user = "root";
    final public static String password = "";

    public static Connection conAufbau() throws SQLException {
        Connection con = null;
        con = DriverManager.getConnection("jdbc:mysql://"+hostname+"/"+db+"?user="+user+"&password="+password+"&serverTimezone=UTC");
        return con;
    }

    public static void conAbbau(Connection con) throws SQLException {
        con.close();
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
}
