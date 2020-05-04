package de.proben.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import de.proben.api.ProbenVerwalten;
import de.proben.model.Probe;
import de.proben.util.Constants;

public class ClientProgram {

	public static void main(String[] args) {
		ProbenVerwalten inMem = ProbenVerwalten
				.getInstance(ProbenVerwalten.ProbeVerwaltenInstance.IN_MEM);

		testProbenVerwalten(inMem);

		ProbenVerwalten db = ProbenVerwalten.getInstance(ProbenVerwalten.ProbeVerwaltenInstance.DB);
		alleProbenAusDbLoeschen(db);
		testProbenVerwalten(db);

	}

	private static void testProbenVerwalten(ProbenVerwalten proVerwInstance) {
		String name = proVerwInstance.getClass()
				.getSimpleName();
		for (int i = 0; i < 11; i++) {
			proVerwInstance.addProbe(generateRandomProbe());
		}

		System.out.println("#####################################################");
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
		System.out.println("remove id=0:" + proVerwInstance.removeProbe(0));
		proVerwInstance.getAll()
				.stream()
				.findAny()
				.ifPresentOrElse(p -> {
					long id = p.getId();
					System.out.printf("remove id=%d:%s%n", id,
							proVerwInstance.removeProbe(id));
				}, () -> System.out.println("nothing to remove"));
		;

		proVerwInstance.getAll()
				.forEach(System.out::println);
		System.out.println();
		System.out.println();
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
