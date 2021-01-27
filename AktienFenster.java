package aktien;

import java.awt.BasicStroke;
import java.awt.Color;
import java.time.LocalDate;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class AktienFenster {
	
	JFrame jframe;
	ChartPanel chartpanel;
	
	public AktienFenster(String fenstername) {
		this.jframe = new JFrame(fenstername);
		this.jframe.setSize(700, 500);
		this.chartpanel = new ChartPanel(chartErstellen(fenstername));
		this.jframe.setVisible(true);
	}
	
	public JFreeChart chartErstellen(String fenstername) {
		TimeSeries werte = new TimeSeries("closeWerte");
		for(int i = 0; i < AktienAusfuehrung.daten.size(); i++) {
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
        
        if(AktienAusfuehrung.closeWerte.get(AktienAusfuehrung.closeWerte.size() -1) > AktienAusfuehrung.d200Schnitt.get(AktienAusfuehrung.d200Schnitt.size() -1) ){
        	chart.getPlot().setBackgroundPaint(Color.GREEN);
        }else {
        	chart.getPlot().setBackgroundPaint(Color.pink);
        }
        
        return chart;
	}
}
