package probenapi.de.proben.dbtest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * In welcher Reihenfolge close:
 * Umgekehrte Reihenfolge: ResultSet, Statement, Connection
 * 
 * Sind ALLE Resourcen AutoCloseable?
 * Ja: ResultSet, Statement, Connection
 * 
 * koennte hier der autocommitModus der Connection auf false gesetzt werden?
 * Ja>> commit() nur wenn DB veraendert wird Transaktion! (add, modify, delete)
 * 
 * Was ist sicherer: Meth wie rs.getString() mit Spaltennamen oder mit Index?
 * getString() mit Spaltenname! aber auch nicht immer sicher, wenn Name
 * geaendert wird
 * Nicht fuer grosse Projekte geeignet >> Frameworks werden genutzt
 * 
 * Sind Spaltennamen in jdbc Case Sensitiv?
 * NEIN
 * 
 * Kommandozeile?
 * Zuhause (>> andere Festplatte):
 * 1)e:
 * 2)cd \workspace\java\eclipse\OCP
 * 3)java -cp "bin;resources/lib/mysql-connector-java-5.1.39-bin.jar"
 * wbs.jdbc.ResultSetDemo
 */
public class ResultSetDemo {

	// wir geben alle records der tabelle buch jeweils mit allen spalten aus
	// unter verwendung eines ResultSet
	public static void main(String[] args) {
		String attributes = "?useUnicode=yes&characterEncoding=UTF-8&serverTimezone=GMT%2B02:00";
		String url = "jdbc:mysql://localhost:3306/jdbc" + attributes;
		String user = "root";
		String password = "mysql";
		String sql = "SELECT * FROM buch";

		String isbn;
		String autor;
		String titel;
		double preis;

		try (
			Connection connection = DriverManager.getConnection(url, user, password);

//		By default, only one ResultSet object per Statement object can be open at the same time
			Statement statement = connection.createStatement();
//	executeQuery(): Executes the given SQL statement, which returns a single ResultSet object. 
			ResultSet rs = statement.executeQuery(sql)) {
//	A ResultSet object is automatically closed when the Statement object that generated it is closed,
//	Statement: All execution methods in the Statement interface implicitly close a 
//			current ResultSet object of the statement if an open one exists.

			connection.setAutoCommit(false);

			while (rs.next()) {
				// getXXX()- methoden haben auch eine überladung, der die
				// spaltennummer übergeben werden kann...
				// auch hier beginnt die zählung bei 1...
				isbn = rs.getString("Isbn");
				autor = rs.getString("autor");
				titel = rs.getString("titel");
				preis = rs.getDouble("preis");
				System.out.printf("%-20s%-30s%-40s%8.2f%n", isbn, autor, titel, preis);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
}
