package probenapi.de.proben.dbtest;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Properties;

/*
 * was kann alles Schiefgehen, wenn Code ausgefuehrt wird?
 * - txt-Datei falsch formatiert (ie keine Tabstops zwischen Attributes >>
 * ArrayIndexOutOfBoundsExc, nicht in double formatierbar...)
 * - Code zweites Mal Ausfuehren >>
 * com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException:
 * Duplicate entry '978-3446200791' for key 'PRIMARY'
 * - Falsche IP, PW, Username
 * - SyntaxFehler im SQL Statement (bei Komplexen SQL Statements schwer zu
 * beheben)
 * - NoSuchFileExc >> falscher Pfad
 * 
 * Warum kann dieser Code nur einmal ausgefuehrt werden?
 * - beim ersten Mal wird PrimaryKey erstellt + Datensatz eingefuegt (PKey=ISBN)
 * - bei naechsten Malen duerfen PKeys nicht ueberschrieben werden >>
 * Duplicate entry (MySQLIntegrityConstraintViolationException)
 * 
 * Spielen hier Transaktionen eine Rolle?
 * ja >> nach jedem pstmt.execute(); wird der Eintrag gleich commitet (darf
 * eigentlich gar nicht sein, siehe naechste Frage)
 * 
 * 
 * Was aendert sich, wenn autocommit-modus auf false gesetzt wird?
 * dann muss man manuell commiten >> hier besser, weil dann kann man sagen, dass
 * erst nach ALLEN executes() commit() aufgerufen wird (falls Exc waehrend
 * abarbeitung von Eintraegen auftritt gibt es erst gar keinen commit, bei
 * autocommit schon!!! >> darf eigentlich nicht sein (Transaction: entweder
 * success oder rollback)
 */
public class SelectAndInsertProbeIntoTableDemo {

	/*
	 * unter verwendung eines PreparedStatement fügen wir einige records in die
	 * tabelle buch ein.
	 */

	public static void main(String[] args) throws IOException {
		Properties props = new Properties();
		String idStr = null;
		try (FileInputStream fis = new FileInputStream(
				"resources/db/db.properties");) {
			props.load(fis);
			idStr = props.getProperty("id");
		} catch (IOException e) {
			e.printStackTrace();
		}
//																				1				2					3					4
		String sqlInsert = "INSERT INTO probe(id, zeitpunkt, messwert, ergebnis)"
//							(1, 2, 3, 4)  >> JDBC beginnt NICHT bei 0!
				+ " VALUES(?, ?, ?, ?)";
		String sqlSelect = "SELECT * FROM probe";

		try (Connection conn = MyDataSourceFactory.getMySQLDataSource()
				.getConnection();
//				Statement statement = conn.createStatement();
//				ResultSet rs = statement.executeQuery(sqlSelect);

				PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {

////		Ausgabe
//			long id;
//			LocalDateTime zeitpunkt;
//			int messwert;
//			String ergebnis;
//			rs.beforeFirst();
//			while (rs.next()) {
//				id = rs.getLong(idStr);
//				zeitpunkt = rs.getTimestamp(2)
//						.toLocalDateTime();
//				messwert = rs.getInt(3);
//				ergebnis = rs.getString(4);
//				System.out.printf("%-6s%-26s%-6s%2s%n", id, zeitpunkt, messwert,
//						ergebnis);
//			}

//			rs.last();
//			int size = rs.getRow();
			try {
				pstmt.setLong(1, 18);
				pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
				pstmt.setInt(3, 100);
				pstmt.setString(4, "FRAGLICH");
				pstmt.execute();

			} catch (RuntimeException e) {
				System.out.println(e);
			}

		} catch (SQLException e) {
			System.out.println(e);
			e.getMessage();
			e.getSQLState();
		}

	}
}
