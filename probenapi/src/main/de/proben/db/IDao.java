package de.proben.db;

import java.util.List;
import java.util.Optional;

import de.proben.model.Probe;

public interface IDao {

	List<Probe> getAll();

	Optional<Probe> getProbe(long probeId);

	void addProbe(Probe probe);

	boolean removeProbe(long probeId);

	boolean addMesswert(long probeId, Integer messwert);

}
