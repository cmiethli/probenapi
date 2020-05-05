package de.proben.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import de.proben.api.ProbenVerwalten;
import de.proben.api.ProbenVerwaltenFactory;
import de.proben.model.Probe;
import de.proben.util.Constants;

public class ClientProgram {
	private static Probe probeOhneMw;

	public static void main(String[] args) {
		ProbenVerwalten inMem = ProbenVerwaltenFactory
				.getInstance(ProbenVerwaltenFactory.Instance.IN_MEM);
		testProbenVerwalten(inMem);

		System.out.println();
		System.out.println("###############################################");
		System.out.println("###############################################");
		ProbenVerwalten db = ProbenVerwaltenFactory
				.getInstance(ProbenVerwaltenFactory.Instance.DB);
		alleProbenAusDbLoeschen(db);
		testProbenVerwalten(db);

	}

	private static void testProbenVerwalten(ProbenVerwalten proVerwInstance) {
		generateProben(proVerwInstance);

		String name = proVerwInstance.getClass()
				.getSimpleName();
		System.out.println("##### " + name + ": getAll() ##########");
		proVerwInstance.getAll()
				.forEach(System.out::println);

		System.out.println();
		System.out.println(
				"##### " + name + ": timeSorted(AeltesteZuerst) #############");
		boolean isAeltesteZuerst = true;
		proVerwInstance.timeSorted(isAeltesteZuerst)
				.forEach(System.out::println);

		System.out.println();
		System.out
				.println("#### " + name + ": filtered(Ergebnis.xxx) #############");
		proVerwInstance.filtered(Probe.Ergebnis.FRAGLICH)
				.forEach(System.out::println);
		proVerwInstance.filtered(Probe.Ergebnis.POSITIV)
				.forEach(System.out::println);
		proVerwInstance.filtered(Probe.Ergebnis.NEGATIV)
				.forEach(System.out::println);

		System.out.println();
		System.out.println("##### " + name + ": removeProbe(id) #############");
		System.out.println("remove id=0: " + proVerwInstance.removeProbe(0));
		proVerwInstance.getAll()
				.stream()
				.findAny()
				.ifPresentOrElse(p -> {
					long id = p.getId();
					System.out.printf("remove id=%d: %s%n", id,
							proVerwInstance.removeProbe(id));
				}, () -> System.out.println("nothing to remove"));
		proVerwInstance.getAll()
				.forEach(System.out::println);

		System.out.println();
		int mw = 88;
//		int mw = -88; // IllegalArgExc
		System.out
				.println("##### " + name + ": addMesswert(" + mw + ") #############");
		System.out.println("ProbeId=" + probeOhneMw.getId() + ": "
				+ proVerwInstance.addMesswert(probeOhneMw.getId(), mw));

		Probe keineMwAenderung = proVerwInstance.getAll()
				.get(0);
		System.out.println("ProbeId=" + keineMwAenderung.getId() + ": "
				+ proVerwInstance.addMesswert(keineMwAenderung.getId(), mw));

		proVerwInstance.getAll()
				.forEach(System.out::println);

	}

//	###################### Helper Meths #################################
	private static void generateProben(ProbenVerwalten proVerwInstance) {
		for (int i = 0; i < 10; i++) {
			proVerwInstance.addProbe(generateRandomProbe());
		}
		probeOhneMw = new Probe(LocalDateTime.now());
		proVerwInstance.addProbe(probeOhneMw);
	}

	private static Probe generateRandomProbe() {
		LocalTime t = LocalTime.MIN;
		int thisYear = LocalDate.now()
				.getYear();
		LocalDate d = LocalDate.ofEpochDay(ThreadLocalRandom.current()
				.nextInt(365))
				.withYear(thisYear);
		return new Probe(LocalDateTime.of(d, t), ThreadLocalRandom.current()
				.nextInt(Constants.MW_UPPER_BOUND + 1));
	}

	private static void alleProbenAusDbLoeschen(ProbenVerwalten db) {
		List<Probe> proben = db.getAll();
		proben.forEach(p -> db.removeProbe(p.getId()));
	}
}
