package probenapi.de.proben.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import probenapi.de.proben.api.ProbenVerwalten;

public class ClientProgram {

	public static void main(String[] args) {
//		ProbenVerwalten inMem = new ProbenVerwaltenInMem(Proben.getProben());
		ProbenVerwalten inMem = ProbenVerwalten
				.getInstance(ProbenVerwalten.Inst.IN_MEM);

		testProbenVerwalten(inMem);

		ProbenVerwalten db = ProbenVerwalten.getInstance(ProbenVerwalten.Inst.DB);
		alleProbenAusDbLoeschen(db);
		testProbenVerwalten(db);

	}

	private static void testProbenVerwalten(ProbenVerwalten proVerwInstance) {
		System.out.println("#####################################################");
		System.out.println("##### Test fuer: " + proVerwInstance.getClass()
				.getSimpleName() + " ##########");

		for (int i = 0; i < 10; i++) {
			proVerwInstance.addProbe(generateRandomProbe());
		}
		proVerwInstance.addProbe(LocalDateTime.now(), 88);

		proVerwInstance.getAll()
				.forEach(System.out::println);

		System.out.println();
		System.out
				.println("############### timeSorted(AeltesteZuerst) #############");
		boolean isAeltesteZuerst = true;
		proVerwInstance.timeSorted(isAeltesteZuerst)
				.forEach(System.out::println);

		System.out.println();
		System.out.println("############### filtered(Ergebnis.xxx) #############");
		proVerwInstance.filtered(Probe.Ergebnis.FRAGLICH)
				.forEach(System.out::println);
		proVerwInstance.filtered(Probe.Ergebnis.POS)
				.forEach(System.out::println);
		proVerwInstance.filtered(Probe.Ergebnis.NEG)
				.forEach(System.out::println);

		System.out.println();
		System.out.println("############### removeProbe(id) #############");
		System.out.println("remove id=0:" + proVerwInstance.removeProbe(0));
		System.out.println("remove id=2:" + proVerwInstance.removeProbe(2));
		proVerwInstance.getAll()
				.forEach(System.out::println);
		System.out.println();
		System.out.println();
	}

	// addProbe() in JUnit >> 3 FRAGLICH, TODO
	public static Probe generateRandomProbe() {
		LocalTime t = LocalTime.MIN;
		int thisYear = LocalDate.now()
				.getYear();
		LocalDate d = LocalDate.ofEpochDay(ThreadLocalRandom.current()
				.nextInt(365))
				.withYear(thisYear);
		return new Probe(LocalDateTime.of(d, t), ThreadLocalRandom.current()
				.nextInt(10_001));
	}

	private static void alleProbenAusDbLoeschen(ProbenVerwalten db) {
		List<Probe> proben = db.getAll();
		proben.forEach(p -> db.removeProbe(p.getId()));
	}
}
