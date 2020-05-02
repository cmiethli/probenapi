package de.proben.dbtest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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
public class InsertBooksIntoTableDemo {

	/*
	 * unter verwendung eines PreparedStatement fügen wir einige records in die
	 * tabelle buch ein.
	 */

	public static void main(String[] args) throws IOException {

		String url = "jdbc:mysql://localhost:3306/jdbc?useUnicode=yes&characterEncoding=UTF-8&serverTimezone=UTC";
		String user = "root";
		String password = "mysql";

		String[] tokens;
//																	1			2				3			4
		String sql = "INSERT INTO buch(isbn, autor, titel, preis)"
			+ " VALUES(?, ?, ?, ?)";
//							(1, 2, 3, 4)  >> JDBC beginnt NICHT bei 0!

		// alle zeilen einlesen
		List<String> lines;
		lines = Files.readAllLines(Paths.get("resources/sql/books.txt"));

		try (Connection conn = DriverManager.getConnection(url, user, password);
//A SQL statement is precompiled and stored in a PreparedStatement object. 
//This object can then be used to efficiently execute this statement multiple times. 
			PreparedStatement pstmt = conn.prepareStatement(sql);) {

			conn.setAutoCommit(true); // default
//			bei false muss man es selber committen (pstmt.commit())
//			wenn fehler 

			for (String line : lines) {

				try {
					// regex: als field separator MINDESTENS ein tabstop...
					tokens = line.split("\t+");
//					System.out.println(Arrays.toString(tokens));
					// achtung: spaltenzählung in jdbc beginnt bei 1!

					pstmt.setString(1, tokens[0]);
					pstmt.setString(2, tokens[1]);
					pstmt.setString(3, tokens[2]);
					pstmt.setDouble(4, Double.parseDouble(tokens[3])); // MySQL: Double
					pstmt.execute();

				} catch (RuntimeException e) {
					System.out.println(e);
				}
			}
		} catch (SQLException e) {
			System.out.println(e);
			e.getMessage();
			e.getSQLState();
		}

	}
}
