package de.proben.model;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import de.proben.Constants;

public class Probe {

	private static long idCounter = 1;
	private long id;
	private LocalDateTime time;
	private int mw;
	private Ergebnis erg;

	public Probe(long id, LocalDateTime time, int mw, Ergebnis erg) {
		this.id = id;
		idCounter = id;
		this.time = time;
		this.mw = mw;
		this.erg = erg;
	}

	public Probe(LocalDateTime time, int mw) {
		if (mw < Constants.MW_LOWER_BOUND || mw > Constants.MW_UPPER_BOUND) {
			throw new IllegalArgumentException("invalid messwert:" + mw);
		}
		id = idCounter++;
		this.time = time;
		this.mw = mw;
		berechneErgebnis();
	}

	@Override
	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
		NumberFormat formatKilos = NumberFormat.getCompactNumberInstance(
				new Locale("en", "US"), NumberFormat.Style.SHORT);
		formatKilos.setMaximumFractionDigits(1);
		return String.format("id=%3d, zeit=%8s, mw=%5s, erg=%s", id,
				time.format(formatter), formatKilos.format(mw), erg);
//		"id=" + id + ", zeit=" + time.truncatedTo(ChronoUnit.MINUTES)
//			.toLocalDate() + ", mw=" + mw + ", erg=" + erg;
	}

	private void berechneErgebnis() {
		if (mw > Constants.MW_UPPER_BOUND_FRAGLICH) {
			erg = Ergebnis.POS;
		} else if (mw >= Constants.MW_LOWER_BOUND_FRAGLICH
				&& mw <= Constants.MW_UPPER_BOUND_FRAGLICH) {
			erg = Ergebnis.FRAGLICH;
		} else {
			erg = Ergebnis.NEG;
		}
//		double mean = Stream.of(1, 2, 3, 4, 5, 6)
//			.collect(Collectors.teeing(Collectors.summingDouble(i -> i),
//				Collectors.counting(), (sum, n) -> sum / n));
//
//		System.out.println(mean);
	}

//	Getter
	public long getId() {
		return id;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public int getMw() {
		return mw;
	}

	public Ergebnis getErg() {
		return erg;
	}

	public static enum Ergebnis {
		POS, NEG, FRAGLICH
	}

}
