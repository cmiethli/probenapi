package de.proben.api;

import java.time.LocalDateTime;
import java.util.List;

import de.proben.model.Probe;

/**
 * API zur Verwaltung von Proben. Es gibt zwei Implementierungen fuer die
 * Speicherung der Proben: <br>
 * 1) InMemory oder <br>
 * 2) in einer MySQL Database
 * 
 * @author cmiethli
 *
 */
public interface ProbenVerwalten {
	/**
	 * Gibt alle abgespeicherten Proben zurueck.
	 * 
	 * @return List mit Elementtyp Probe
	 */
	List<Probe> getAll();

	/**
	 * Gibt alle abgespeicherten Proben nach dem Zeitpunkt sortiert zurueck.
	 * 
	 * @param isAeltesteZuerst true wenn erstes Element die aelteste Probe sein
	 *                         soll, <br>
	 *                         false wenn erstes Element die neuste Probe sein
	 *                         soll,
	 * @return List mit Elementtyp Probe
	 */
	List<Probe> timeSorted(boolean isAeltesteZuerst);

	/**
	 * Gibt alle abgespeicherten Proben nach dem Ergebnis gefiltert zurueck.
	 * 
	 * @param ergebnis enum Ergebnis mit POSITIV, FRAGLICH, NEGATIV
	 * @return List mit Elementtyp Probe
	 */
	List<Probe> filtered(Probe.Ergebnis ergebnis);

	/**
	 * Fuegt eine Probe dem Speicher hinzu.
	 * 
	 * @param probe Probe die hinzugefuegt wird
	 */
	void addProbe(Probe probe);

	/**
	 * Fuegt eine Probe dem Speicher hinzu.
	 * 
	 * @param zeitpunkt Zeitpunkt der Probennahme
	 * @param messwert  Messwert der Probennahme
	 */
	void addProbe(LocalDateTime zeitpunkt, int messwert);

	/**
	 * Loescht die angegebene Probe aus dem Speicher.
	 * 
	 * @param id Eindeutige id der zu loeschenden Probe
	 * @return true falls diese Probe im Speicher war
	 */
	boolean removeProbe(long id);

	/**
	 * Gibt eine Instanz vom Typ ProbenVerwalten zurueck.
	 * 
	 * @param instance enum ProbeVerwaltenInstance: IN_MEM, DB
	 * @return IN_MEM gibt eine Instanz von ProbenVerwaltenInMem zurueck,<br>
	 *         DB gibt eine Instanz von ProbenVerwaltenDb zurueck
	 */
	static ProbenVerwalten getInstance(ProbeVerwaltenInstance instance) {
		ProbenVerwalten inst;
		switch (instance) {
		case IN_MEM:
			inst = new ProbenVerwaltenInMem();
			break;
		case DB:
			inst = new ProbenVerwaltenDb();
			break;
		default:
			throw new AssertionError("invalid instance...");
//		case IN_MEM -> instance = new ProbenVerwaltenInMem();
//		case DB -> instance = null;
//		default -> throw new AssertionError();
		}
		return inst;
	}

	/**
	 * Hilfsenum fuer {@link ProbenVerwalten#getInstance(ProbeVerwaltenInstance)
	 * getInstance(Instance)}. Sie bestimmt die zurueck gegebene Instanz.
	 * 
	 * @author cmiethli
	 *
	 */
	static enum ProbeVerwaltenInstance {
		/**
		 * Gibt Instanz vom Typ ProbenVerwaltenInMem zurueck
		 */
		IN_MEM,
		/**
		 * Gibt Instanz vom Typ ProbenVerwaltenDb zurueck
		 */
		DB
	}
}
