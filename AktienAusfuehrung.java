package aktien;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.DayOfWeek;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class AktienAusfuehrung {
	
	private static String aktie;
	private static String URL = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + aktie + "&outputsize=full&apikey=GV8OZAQLF4YSTSGD";
	private static int tage = 100;
	private static ArrayList<LocalDate> daten = new ArrayList<LocalDate>(); 
	private static ArrayList<Double> closeWerte = new ArrayList<Double>();
	private static double [] d200Schnitt = new double[tage + 200];
	
	final static String hostname = "localhost";
	final static String port = "3306";
	final static String db = "Aktien";
	final static String user = "root";
	final static String password = "1234";

	public static void main(String[] args){
		eingabe();
		
		try {
			getWert(URL);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tabelleErstellen();
		datenbankeingabe();
		datenbankausgabe();
	}
	
	public static void eingabe() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Welche Aktie wollen Sie dargestellt?");
		aktie = sc.next();
	}
	
	public static void getWert(String URL) throws JSONException, IOException {
		try {
			JSONObject json = new JSONObject(IOUtils.toString(new URL(URL), Charset.forName("UTF-8")));
			json = json.getJSONObject("Time Series (Daily)");
			for(int i = 0; i < tage; i++){
				daten.add(LocalDate.parse((CharSequence)json.names().get(i)));
				closeWerte.add(json.getJSONObject(LocalDate.parse((CharSequence)json.names().get(i)).toString()).getDouble("4. close"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void d200SchnittBerechnen() {
		for(int i = 0; i < d200Schnitt.length; i++) {
			int sum = 0;
			for(int j = 0; j < 200; j++) {
				sum = (int) (closeWerte.get(i + j) + sum);
			}
			d200Schnitt[i+200] = sum/200;
		}
		datenbankUpdate200Schnitt();
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
            String sql = "CREATE TABLE "+ aktie +" if not exists(Datum dateTime, closeWert doubble, 200-Schnitt doubble))";
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
	
	public static void datenbankeingabe() {
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
            for(int i = 0; i < tage; i++) {
            	String sql = "INSERT INTO "+ aktie +" values("+ daten.get(i) +","+ closeWerte.get(i) +","+ d200Schnitt[i] +")";
            	myStat.execute(sql);
            }
            ordnen(con);
            con.close();
        }
        catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("Error: " + sqle.getErrorCode());
            sqle.printStackTrace();
        }
	}
	
	public static void ordnen(Connection connection) {
        String selectFrom = "SELECT * FROM "+aktie+" ORDER BY DATUM ASC";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(selectFrom);
            
            daten.clear();
            closeWerte.clear();

            while (rs.next()) {
                daten.add(LocalDate.parse(rs.getString("Datum")));
                closeWerte.add(rs.getDouble("closeWert"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        d200SchnittBerechnen();
    }
	
	public static void datenbankUpdate200Schnitt() {
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
            for(int i = 0; i < tage; i++) {
            	String sql = "UPDATE "+ aktie +" SET 200-Schnitt="+ d200Schnitt[i] +" WHERE Datum="+ daten.get(i);
            	myStat.execute(sql);
            }
            con.close();
        }
        catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("Error: " + sqle.getErrorCode());
            sqle.printStackTrace();
        }
	}
	
	private static void datenbankausgabe(){
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
            ResultSet reSe=myStat.executeQuery("Select * from " + aktie + "order by date");
            System.out.println(aktie +"datum                                 Close-Wert					200-Schnitt");
            while(reSe.next()){
            	String datum = reSe.getString("Datum");
                String closeWert = reSe.getString("closeWert");
                String string200Schnitt = reSe.getString("200-Schnitt");
                
                System.out.printf("%1s",datum);
                System.out.printf("%20s", closeWert);
                System.out.printf("%11s", string200Schnitt);
            }

            con.close();
        }
        catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("Error: " + sqle.getErrorCode());
            sqle.printStackTrace();
        }
    }
}

