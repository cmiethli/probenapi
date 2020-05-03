package de.proben.model;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import de.proben.util.Constants;

public class Probe {

	private static long idCounter = 1;
	private long probeId;
	private LocalDateTime time;
	private int messwert;
	private Ergebnis ergebnis;

	public Probe(long id, LocalDateTime time, int mw, Ergebnis erg) {
		this.probeId = id;
		idCounter = id;
		this.time = time;
		this.messwert = mw;
		this.ergebnis = erg;
	}

	public Probe(LocalDateTime time, int mw) {
		if (mw < Constants.MW_LOWER_BOUND || mw > Constants.MW_UPPER_BOUND) {
			throw new IllegalArgumentException("invalid messwert:" + mw);
		}
		probeId = idCounter++;
		this.time = time;
		this.messwert = mw;
		berechneErgebnis();
	}

	@Override
	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
		NumberFormat formatKilos = NumberFormat.getCompactNumberInstance(
				new Locale("en", "US"), NumberFormat.Style.SHORT);
		formatKilos.setMaximumFractionDigits(1);
		return String.format("id=%3d, zeit=%8s, mw=%5s, erg=%s", probeId,
				time.format(formatter), formatKilos.format(messwert), ergebnis);
//		"id=" + id + ", zeit=" + time.truncatedTo(ChronoUnit.MINUTES)
//			.toLocalDate() + ", mw=" + mw + ", erg=" + erg;
	}

	private void berechneErgebnis() {
		if (messwert > Constants.MW_UPPER_BOUND_FRAGLICH) {
			ergebnis = Ergebnis.POS;
		} else if (messwert >= Constants.MW_LOWER_BOUND_FRAGLICH
				&& messwert <= Constants.MW_UPPER_BOUND_FRAGLICH) {
			ergebnis = Ergebnis.FRAGLICH;
		} else {
			ergebnis = Ergebnis.NEG;
		}
//		double mean = Stream.of(1, 2, 3, 4, 5, 6)
//			.collect(Collectors.teeing(Collectors.summingDouble(i -> i),
//				Collectors.counting(), (sum, n) -> sum / n));
//
//		System.out.println(mean);
	}

	@Override
	public int hashCode() {
		return Long.valueOf(this.getId())
				.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Probe)) {
			return false;
		}
		Probe other = (Probe) object;

		if (this.getId() == other.getId()) {
			return true;
		} else {
			return false;
		}

	}

//	Getter
	public long getId() {
		return probeId;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public int getMw() {
		return messwert;
	}

	public Ergebnis getErg() {
		return ergebnis;
	}

	public static enum Ergebnis {
		POS, NEG, FRAGLICH
	}

}
