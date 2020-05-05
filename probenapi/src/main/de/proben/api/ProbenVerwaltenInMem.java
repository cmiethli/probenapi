package de.proben.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.proben.model.Probe;

/**
 * InMemory Implementierung von {@linkplain ProbenVerwalten}. Die Speicherung
 * der Proben
 * laeuft InMemory ueber eine java.util.list
 * 
 * @author cmiethli
 *
 */
public class ProbenVerwaltenInMem implements ProbenVerwalten {
	private List<Probe> proben;

	ProbenVerwaltenInMem() {
//Concurrent weil: siehe ProVerwInMemTEST.java >> removeAllProben()
		this.proben = new CopyOnWriteArrayList<Probe>();
	}

	@Override
	public List<Probe> getAll() {
		return proben;
	}

	@Override
	public List<Probe> timeSorted(boolean isAeltesteZuerst) {
		return timeSortedPackageScope(isAeltesteZuerst, proben);
	}

	@Override
	public List<Probe> filtered(Probe.Ergebnis erg) {
		return proben.stream()
				.filter(p -> p.getErg() == erg)
				.collect(Collectors.toList());
	}

	@Override
	public void addProbe(Probe p) {
		proben.add(p);
	}

	@Override
	public void addProbe(LocalDateTime zeitpunkt, int messwert) {
		proben.add(new Probe(zeitpunkt, messwert));
	}

	@Override
	public void addProbe(LocalDateTime zeitpunkt) {
		proben.add(new Probe(zeitpunkt));
	}

	@Override
	public boolean removeProbe(long id) {
		Optional<Probe> probeToRemove = getProbe(id);

		return probeToRemove.isPresent() ? proben.remove(probeToRemove.get())
				: false;
	}

	@Override
	public boolean addMesswert(long probeId, Integer messwert) {
		Optional<Probe> pr = getProbe(probeId);
		boolean isMesswertSet = false;
		if (pr.isPresent()) {
			if (pr.get()
					.getMw() == null) {
// Messwert noch nicht vorhanden
				pr.get()
						.setMesswert(messwert);
				isMesswertSet = true;
			} else {
// Messwert schon vorhanden
				isMesswertSet = false;
			}
		}
		return isMesswertSet;
	}

// ############# Helper Meths ####################
	private Optional<Probe> getProbe(long id) {
		return proben.parallelStream()
				.filter(p -> p.getId() == id)
				.findAny();
	}

	static List<Probe> timeSortedPackageScope(boolean isAeltesteZuerst,
			List<Probe> proben) {
		Stream<Probe> probenSorted;
		if (isAeltesteZuerst) {
			probenSorted = proben.stream()
					.sorted((p1, p2) -> p1.getTime()
							.compareTo(p2.getTime()));
		} else {
			probenSorted = proben.stream()
					.sorted((p1, p2) -> p2.getTime()
							.compareTo(p1.getTime()));
		}
		return probenSorted.collect(Collectors.toList());
	}
}
