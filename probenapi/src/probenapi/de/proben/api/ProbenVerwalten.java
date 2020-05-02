package probenapi.de.proben.api;

import java.time.LocalDateTime;
import java.util.List;

import probenapi.de.proben.model.Probe;

public interface ProbenVerwalten {
	List<Probe> getAll();

	List<Probe> timeSorted(boolean isAeltesteZuerst);

	List<Probe> filtered(Probe.Ergebnis erg);

	void addProbe(Probe p);

	void addProbe(LocalDateTime zeit, int messwert);

	boolean removeProbe(long id);

//	@SuppressWarnings("preview")
	static ProbenVerwalten getInstance(Inst inst) {
		ProbenVerwalten instance;
		switch (inst) {
		case IN_MEM:
			instance = new ProbenVerwaltenInMem();
			break;
		case DB:
			instance = new ProbenVerwaltenDb();
			break;
		default:
			throw new AssertionError();
//		case IN_MEM -> instance = new ProbenVerwaltenInMem();
//		case DB -> instance = null;
//		default -> throw new AssertionError();
		}
		return instance;
	}

	static enum Inst {
		IN_MEM, DB
	}
}
