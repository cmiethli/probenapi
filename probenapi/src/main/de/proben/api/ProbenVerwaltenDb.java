package de.proben.api;

import java.time.LocalDateTime;
import java.util.List;

import de.proben.db.DbFactory;
import de.proben.db.IDao;
import de.proben.model.Probe;
import de.proben.model.Probe.Ergebnis;

/**
 * Database Implementierung von {@linkplain ProbenVerwalten}. Die Speicherung
 * der Proben laeuft ueber eine Datenbank in MySQL.
 * 
 * @author cmiethli
 *
 */
public class ProbenVerwaltenDb implements ProbenVerwalten {

	private IDao dao;

	ProbenVerwaltenDb() {
		this.dao = DbFactory.getDao();
	}

	@Override
	public List<Probe> getAll() {
		return dao.getAll();
	}

	@Override
	public List<Probe> timeSorted(boolean isAeltesteZuerst) {
		List<Probe> proben = dao.getAll();
		return ProbenVerwaltenInMem.timeSortedPackageScope(isAeltesteZuerst,
				proben);
	}

	@Override
	public List<Probe> filtered(Ergebnis ergebnis) {
		return dao.filtered(ergebnis);
	}

	@Override
	public void addProbe(Probe probe) {
		dao.addProbe(probe);
	}

	@Override
	public void addProbe(LocalDateTime zeitpunkt, int messwert) {
		dao.addProbe(new Probe(zeitpunkt, messwert));
	}

	@Override
	public void addProbe(LocalDateTime zeitpunkt) {
		dao.addProbe(new Probe(zeitpunkt));
	}

	@Override
	public boolean removeProbe(long probeId) {
		return dao.removeProbe(probeId);
	}

	@Override
	public boolean addMesswert(long probeId, Integer messwert) {
		return dao.addMesswert(probeId, messwert);
	}
}
