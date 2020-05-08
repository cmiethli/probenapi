package de.proben.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import de.proben.db.DbFactory;
import de.proben.db.IDao;
import de.proben.db.PersistenceException;
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
		String sqlSelect = "SELECT * FROM probe WHERE probe.ergebnis='" + ergebnis
				+ "'";
		return selectQuery(sqlSelect);
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

//	################### Helper Meths #################
	private List<Probe> selectQuery(String sqlSelect) {
		List<Probe> proben = new ArrayList<Probe>();
		try (Connection conn = DbFactory.getMySQLDataSource()
				.getConnection();
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery(sqlSelect);) {
			while (rs.next()) {
				Ergebnis erg;
				Integer mw;
				if (rs.getString(4) == null) {
					erg = null;
					mw = null;
				} else {
					erg = Ergebnis.valueOf(rs.getString(4));
					mw = rs.getInt(3);
				}

				proben.add(new Probe(rs.getLong(1), rs.getTimestamp(2)
						.toLocalDateTime(), mw, erg));
			}
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
		return proben;
	}
}
