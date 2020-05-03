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

public class ProbenVerwaltenDb implements ProbenVerwalten {

	DataSource ds = MyDataSourceFactory.getMySQLDataSource();

	@Override
	public List<Probe> getAll() {
		List<Probe> proben = new ArrayList<Probe>();
		String sqlSelect = "SELECT * FROM probe";
		return selectQuery(proben, sqlSelect);
	}

	@Override
	public List<Probe> timeSorted(boolean isAeltesteZuerst) {
		List<Probe> proben = getAll();
		return ProbenVerwaltenInMem.timeSortedPackageScope(isAeltesteZuerst,
				proben);
	}

	@Override
	public List<Probe> filtered(Ergebnis erg) {
		List<Probe> proben = new ArrayList<Probe>();
		String sqlSelect = "SELECT * FROM probe WHERE probe.ergebnis='" + erg + "'";
		return selectQuery(proben, sqlSelect);
	}

	@Override
	public void addProbe(Probe p) {
//		TODO
		String sqlInsert = "INSERT INTO probe(id, zeitpunkt, messwert, ergebnis)"
				+ " VALUES(?, ?, ?, ?)";
//								(1, 2, 3, 4)  >> JDBC beginnt NICHT bei 0!
		try (Connection conn = MyDataSourceFactory.getMySQLDataSource()
				.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {

			pstmt.setLong(1, p.getId());
			pstmt.setTimestamp(2, Timestamp.valueOf(p.getTime()));
			pstmt.setInt(3, p.getMw());
			pstmt.setString(4, p.getErg()
					.toString());
			pstmt.execute();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void addProbe(LocalDateTime zeit, int messwert) {
		addProbe(new Probe(zeit, messwert));
	}

	@Override
	public boolean removeProbe(long id) {
		String sqlDelete = "DELETE FROM `probe` WHERE `probe`.`id` =?";
		int updatedRows = 0;
		try (Connection conn = MyDataSourceFactory.getMySQLDataSource()
				.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
			pstmt.setLong(1, id);
			updatedRows = pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return updatedRows != 0 ? true : false;
	}

//	######################################################
//	################## Helper Meths ######################

	private List<Probe> selectQuery(List<Probe> proben, String sqlSelect) {
		try (Connection conn = ds.getConnection();
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery(sqlSelect);) {
			while (rs.next()) {
				proben.add(new Probe(rs.getLong(1), rs.getTimestamp(2)
						.toLocalDateTime(), rs.getInt(3),
						Ergebnis.valueOf(rs.getString(4))));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return proben;
	}
}
