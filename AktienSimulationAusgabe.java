package aktienSimulation;

import java.time.LocalDate;
import java.util.ArrayList;

public class AktienSimulationAusgabe {

    private static LocalDate startdate;
    private static LocalDate enddate;
    private static ArrayList<String> aktien = new ArrayList<String>();

    public AktienSimulationAusgabe(ArrayList aktien, LocalDate startdate, LocalDate enddate){
        this.aktien = aktien;
        this.startdate = startdate;
        this.enddate = enddate;
    }

    public static void ausgabe(float buyAndHold, float d200Schnitt, float prozent){
        System.out.println("Zeit des Versuches: Von " + startdate + " bis " + enddate);
        System.out.println("Mit folgenden Aktien wurden Berechnungen durchgeführt: ");
        for(int i = 0; i < aktien.size(); i++) {
            System.out.println(aktien.get(i));
        }
        System.out.println("Ergebnisse:");
        System.out.println("Buy and Hold: " + buyAndHold);
        System.out.println("Mit 200-Schnitt:" + d200Schnitt);
        System.out.println("Mit Prozent-Methode: " + prozent);
    }
}
