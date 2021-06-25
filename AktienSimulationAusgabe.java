package aktienSimulation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class AktienSimulationAusgabe {

    private static LocalDate startdate;
    private static LocalDate enddate;
    private static ArrayList<String> aktien = new ArrayList<String>();

    public AktienSimulationAusgabe(ArrayList aktien, LocalDate startdate, LocalDate enddate){
        this.aktien = aktien;
        this.startdate = startdate;
        this.enddate = enddate;
    }

    public static void ausgabe(float buyAndHold, float d200Schnitt, float prozent, float buyAndHoldprozent, float d200Schnittprozent, float prozentProzent){
        System.out.println("Zeit des Versuches: Von " + startdate + " bis " + enddate);
        System.out.println("Mit folgenden Aktien wurden Berechnungen durchgeführt: ");
        for(int i = 0; i < aktien.size(); i++) {
            System.out.println(aktien.get(i));
        }
        System.out.println("Ergebnisse:");
        System.out.println("Buy and Hold: " + buyAndHold + " (+" + buyAndHoldprozent + ")");
        System.out.println("Mit 200-Schnitt:" + d200Schnitt + " (+" + d200Schnittprozent + ")");
        System.out.println("Mit Prozent-Methode: " + prozent + " (+" + prozentProzent + ")");
    }
    /*
    JFrame jframe;
    ChartPanel chartpanel;

    public File file = null;

    public AktienSimulationAusgabe() {
        this.jframe = new JFrame("Strategien");
        this.jframe.setSize(700, 500);
        this.chartpanel = new ChartPanel(chartErstellen("Strategien"));
        this.jframe.add(this.chartpanel);
        this.jframe.setVisible(true);
    }

    public JFreeChart chartErstellen(String fenstername) {
        TimeSeries werte = new TimeSeries("closeWerte");
        for(int i = 0; i < DatenbankAktienSimulation.datenbankDaten; i++) {
            LocalDate datum = AktienAusfuehrung.daten.get(i);
            double wert = AktienAusfuehrung.closeWerte.get(i);
            int tag = datum.getDayOfMonth();
            int monat = datum.getMonthValue();
            int jahr = datum.getYear();
            werte.add(new Day(tag,monat,jahr), wert);
        }

        TimeSeries d200werte = new TimeSeries("200Schnitt");
        for(int i = 0; i < AktienAusfuehrung.d200Schnitt.size(); i++) {
            LocalDate datum = AktienAusfuehrung.daten.get(i);
            double d200wert = AktienAusfuehrung.d200Schnitt.get(i);
            int tag = datum.getDayOfMonth();
            int monat = datum.getMonthValue();
            int jahr = datum.getYear();
            d200werte.add(new Day(tag,monat,jahr), d200wert);
        }

        TimeSeriesCollection collection = new TimeSeriesCollection();
        collection.addSeries(werte);
        collection.addSeries(d200werte);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(fenstername, "Datum", "CloseWert", collection);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.getRenderer().setDefaultStroke(new BasicStroke(2.0f));
        ((AbstractRenderer) plot.getRenderer()).setAutoPopulateSeriesStroke(false);
        ((AbstractRenderer) plot.getRenderer()).setSeriesPaint(0,  new Color(0, 0, 0));

        return chart;
    }*/
}
