package freieTage;

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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class FreieTageAusfuehrung extends Application {
	
	@Override
	public void start(Stage primaryStage) {
	    Pane root = new Pane();
	    ObservableList<PieChart.Data> valueList = FXCollections.observableArrayList(
	            new PieChart.Data("Montage "+mo, mo),
	            new PieChart.Data("Dinstage "+di, di),
	            new PieChart.Data("Mittwoch "+mi, mi),
	    		new PieChart.Data("Donnerstage "+don, don),
	    		new PieChart.Data("Freitage "+fr, fr),
	    		new PieChart.Data("Samstage "+sa, sa),
	    		new PieChart.Data("Sonntage "+so, so));
	    PieChart pieChart = new PieChart(valueList);
	    pieChart.setTitle("Freien Wochentage zwischen "+ anfangsjahr +" - " + endjahr);
	    root.getChildren().addAll(pieChart);
	    Scene scene = new Scene(root, 450, 450);

	    primaryStage.setTitle("Freie Wochentage");
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}
	
	static ArrayList<LocalDate> feiertage = new ArrayList<LocalDate>();
	static List<String> dynamischeFeiertage = new ArrayList<>();
	static int mo = 0;
	static int di = 0;
	static int mi = 0;
	static int don = 0;
	static int fr = 0;
	static int sa = 0;
	static int so = 0;
	static int anfangsjahr;
	static int endjahr;
	final static String hostname = "localhost";
	final static String port = "3306";
	final static String db = "FreieTage";
	final static String user = "root";
	final static String password = "1234";
	
	public static void main(String[] args)throws JSONException, MalformedURLException, IOException {
		
		dynamischeFeiertageBestimmen();
		eingabe();		
		launch(args);
		tabelleErstellen();
		datenbankeingabe();

		
		Scanner sc = new Scanner(System.in);
		System.out.println("Wollen Sie die Datenbank ausgeben? [j/n]");
		String text = sc.next();
		if(text.equalsIgnoreCase("j")) datenbankausgabe();
		sc.close();
	}
	
	public static void dynamischeFeiertageBestimmen() {
		dynamischeFeiertage.add("Christi Himmelfahrt");   
		dynamischeFeiertage.add("Ostermontag");  
		dynamischeFeiertage.add("Fronleichnam");     
		dynamischeFeiertage.add("Pfingstmontag");
	}
	
	public static void eingabe() {
		
		Scanner sc = new Scanner (System.in);
		System.out.println("Bitte Anfangs- und Endjahr eingeben.");
		do {
		anfangsjahr = sc.nextInt();
		}while(anfangsjahr < 0 || anfangsjahr > 3000);
		do {
		endjahr = sc.nextInt();
		}while(endjahr < 0 || endjahr > 3000);
		try {
			for(int i = anfangsjahr; i <= endjahr; i++) {
				JSONObject json = new JSONObject(IOUtils.toString(new URL("https://feiertage-api.de/api/?jahr="+ i +"&nur_land=BY"), Charset.forName("UTF-8")));
				getWert(json, dynamischeFeiertage);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		feiertageBestimmen(anfangsjahr, endjahr);
		tagUeberpruefen();
	}
	
	public static List<String> getWert(JSONObject json, List<String> keys) {

        List<String> anzahl = new ArrayList<>();
        for(int i = 0; i< keys.size();i++) {
            JSONObject jsonO = (JSONObject) json.get(keys.get(i));
            anzahl.add( jsonO.getString("datum"));
            LocalDate date = LocalDate.parse(anzahl.get(i));
            feiertage.add(date);
        }
               return anzahl;
    }
	
	public static void feiertageBestimmen(int anfangsjahr, int endjahr) {
		for(int i = anfangsjahr; i <= endjahr; i++) {
			feiertage.add(LocalDate.of(i, 1, 1));
			feiertage.add(LocalDate.of(i, 1, 6));
			feiertage.add(LocalDate.of(i, 5, 1));
			feiertage.add(LocalDate.of(i, 8, 15));
			feiertage.add(LocalDate.of(i, 10, 26));
			feiertage.add(LocalDate.of(i, 11, 1));
			feiertage.add(LocalDate.of(i, 12, 8));
			feiertage.add(LocalDate.of(i, 12, 25));
			feiertage.add(LocalDate.of(i, 12, 26));
		}
	}
	
	public static void tagUeberpruefen() {
		for(int i = 0; i < feiertage.size(); i++) {
			if(feiertage.get(i).getDayOfWeek().equals(DayOfWeek.MONDAY)) mo++;
			if(feiertage.get(i).getDayOfWeek().equals(DayOfWeek.TUESDAY)) di++;
			if(feiertage.get(i).getDayOfWeek().equals(DayOfWeek.WEDNESDAY)) mi++;
			if(feiertage.get(i).getDayOfWeek().equals(DayOfWeek.THURSDAY)) don++;
			if(feiertage.get(i).getDayOfWeek().equals(DayOfWeek.FRIDAY)) fr++;
			if(feiertage.get(i).getDayOfWeek().equals(DayOfWeek.SATURDAY)) sa++;
			if(feiertage.get(i).getDayOfWeek().equals(DayOfWeek.SUNDAY)) so++;
		}
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
            String sql = "CREATE TABLE freieTage if not exists(Datum dateTime, Montag int, Dienstag int, Mittwoch int, Donnerstag int, Freitag int, Samstag int, Sonntag int, Anfangsjahr int, Endjahr int))";
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
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String sql = "INSERT INTO freieTage values(" +"'"+timestamp+"',"+mo+","+di+","+mi+","+don+","+fr+","+sa+","
                    +so+","+anfangsjahr+","+endjahr+")";
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
            ResultSet reSe=myStat.executeQuery("Select * from freieTage");
            System.out.println("Zeit                                 Montag      Dienstag        Mittwoch        Donnerstag      Freitag     Samstag" +
                    "       Sonntag         Startjahr       Endjahr");
            while(reSe.next()){
                String zeit = reSe.getString("Datum");
                String montag = reSe.getString("Montag");
                String dienstag = reSe.getString("Dienstag");
                String mittwoch = reSe.getString("Mittwoch");
                String donnerstag = reSe.getString("Donnerstag");
                String freitag = reSe.getString("Freitag");
                String samstag = reSe.getString("Samstag");
                String sonntag = reSe.getString("Sonntag");
                String anfangsjahr = reSe.getString("Anfangsjahr");
                String endjahr = reSe.getString("Endjahr");


                System.out.printf("%1s",zeit);
                System.out.printf("%20s", montag);
                System.out.printf("%11s", dienstag);
                System.out.printf("%16s", mittwoch);
                System.out.printf("%17s", donnerstag);
                System.out.printf("%15s", freitag);
                System.out.printf("%12s", samstag);
                System.out.printf("%14s", sonntag);
                System.out.printf("%19s", anfangsjahr);
                System.out.printf("%16s", endjahr);
                System.out.println();
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