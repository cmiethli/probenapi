package de.proben.db;

import java.util.List;
import java.util.Optional;

import de.proben.model.Probe;
import de.proben.model.Probe.Ergebnis;

public interface IDao {

	List<Probe> getAll();

	Optional<Probe> getProbe(long probeId);

	List<Probe> filtered(Ergebnis ergebnis);

	void addProbe(Probe probe);

	boolean removeProbe(long probeId);

	boolean addMesswert(long probeId, Integer messwert);

}
