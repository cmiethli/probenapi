package de.proben.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import de.proben.db.MyDataSourceFactory;
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

	private DataSource ds;

	ProbenVerwaltenDb() {
		ds = MyDataSourceFactory.getMySQLDataSource();
	}

	@Override
	public List<Probe> getAll() {
		String sqlSelect = "SELECT * FROM probe";
		return selectQuery(sqlSelect);
	}

	@Override
	public List<Probe> timeSorted(boolean isAeltesteZuerst) {
		List<Probe> proben = getAll();
		return ProbenVerwaltenInMem.timeSortedPackageScope(isAeltesteZuerst,
				proben);
	}

	@Override
	public List<Probe> filtered(Ergebnis erg) {
		String sqlSelect = "SELECT * FROM probe WHERE probe.ergebnis='" + erg + "'";
		return selectQuery(sqlSelect);
	}

	@Override
	public void addProbe(Probe p) {
		try (Connection conn = MyDataSourceFactory.getMySQLDataSource()
				.getConnection()) {

			String sqlInsert;
			if (p.getMw() != null) {
//mit Messwert
				sqlInsert = "INSERT INTO probe(id, zeitpunkt, messwert, ergebnis)"
						+ " VALUES(?, ?, ?, ?)";
//											(1, 2, 3, 4)  >> JDBC beginnt NICHT bei 0!
				try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
					pstmt.setLong(1, p.getId());
					pstmt.setTimestamp(2, Timestamp.valueOf(p.getTime()));
					pstmt.setInt(3, p.getMw());
					pstmt.setString(4, p.getErg()
							.toString());
					pstmt.executeUpdate();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			} else {
//ohne Messwert
				sqlInsert = "INSERT INTO probe(id, zeitpunkt)" + " VALUES(?, ?)";
				try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
					pstmt.setLong(1, p.getId());
					pstmt.setTimestamp(2, Timestamp.valueOf(p.getTime()));
					pstmt.executeUpdate();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}

		} // end-Try Connection
		catch (SQLException e1) {
			throw new RuntimeException(e1);
		}
	}

	@Override
	public void addProbe(LocalDateTime zeitpunkt, int messwert) {
		addProbe(new Probe(zeitpunkt, messwert));
	}

	@Override
	public void addProbe(LocalDateTime zeit) {
		addProbe(new Probe(zeit));
	}

	@Override
	public boolean removeProbe(long probeId) {
		String sqlDelete = "DELETE FROM `probe` WHERE `probe`.`id` =?";
		int updatedRows = 0;
		try (Connection conn = MyDataSourceFactory.getMySQLDataSource()
				.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
			pstmt.setLong(1, probeId);
			updatedRows = pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return updatedRows != 0 ? true : false;
	}

	@Override
	public boolean addMesswert(long probeId, Integer messwert) {
		String sqlSelect = "SELECT * FROM `probe` WHERE `id` = " + probeId;
		List<Probe> listEinerProbe = selectQuery(sqlSelect);

		boolean isMesswertSet = false;
		if (listEinerProbe.size() == 1) {
			Probe pr = listEinerProbe.get(0);
			if (pr.getMw() != null) {
// Messwert schon vorhanden
				isMesswertSet = false;
			} else {
// Messwert noch nicht vorhanden				
				String sqlUpdate = "UPDATE probe SET messwert = ?, ergebnis = ?"
						+ " WHERE probe.id =?";
				int updatedRows = 0;
				try (Connection conn = MyDataSourceFactory.getMySQLDataSource()
						.getConnection();
						PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
					pr.setMesswert(messwert);
					pstmt.setInt(1, messwert);
					pstmt.setString(2, pr.getErg()
							.toString());
					pstmt.setLong(3, probeId);
					updatedRows = pstmt.executeUpdate();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				isMesswertSet = updatedRows != 0 ? true : false;
			}
		}
		return isMesswertSet;
	}
//	######################################################
//	################## Helper Meths ######################

	private List<Probe> selectQuery(String sqlSelect) {
		List<Probe> proben = new ArrayList<Probe>();
		try (Connection conn = ds.getConnection();
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
			throw new RuntimeException(e);
		}
		return proben;
	}
}
