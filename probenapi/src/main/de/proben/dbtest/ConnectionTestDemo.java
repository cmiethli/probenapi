package de.proben.dbtest;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

// wir diskutieren vor- und nachteile der verschiedenen arten
// eine connection zu beziehen

/*
 * Treiberklasse muss bekannt sein bei:
 * 1,2
 * Runtime-Class von Connection:
 * 1,2,3: class com.mysql.jdbc.JDBC4Connection
 * 
 * Runtime-Class von DatabaseMetaData:
 * 1,2,3: class com.mysql.jdbc.JDBC4DatabaseMetaData
 * 
 * Wo liegt ByteCode der KLassen:
 * in com.mysql.jdbc vom importierten MySQL jar
 * 
 * Warum tryWithResources?
 * Connection impl AutoCloseable
 * 
 * Wie funktioniert Class.forName(...).getConstructor()... ?
 * forName() holt sich ueber String(!) Class >> getConstr...
 * 
 * DataSource Var4:
 * - welche vorteile ergeben sich, wenn connections von einer DataSource bezogen
 * werden?
 * -lose Kopplung (
 * -ConnectionPool: eine Connection - ein Klient, Connection wird
 * wiederverwendet
 * -Entwickler braucht nur noch JNDI kennen
 * -Admin definiert alle Parameter an einer Stelle >> ueber naming Service
 * (JNDI) kann
 * Client auf alle Parameter zugreifen + muss von Parametern nichts wissen
 * >> Parameter koennen einfach (an dieser einen Stelle) geaendert werden ohne
 * dass Clients irgendwas an ihrem Code aendern muessen
 * 
 * 
 * - muss in diesem fall ein client noch den url, username, password kennen?
 * nein
 * 
 * - falls nein: muss er überhaupt noch etwas wissen?
 * nicht ueber Driver von MySQL, nur noch JNDI Name!
 * 
 * - w e r ist der client?
 * Klassen die interagieren = Componenten:
 * Client einer DataSource = SoftwareComponente >> Instanzen der Klassen
 * 
 * 
 * - w a s ist JNDI?
 * Java Naming and Directory API.
 * An object that implements the DataSource interface will typically be
 * registered with a naming service based on JNDI
 * 
 * - DataSource ist ein interface. wer liefert eine implementierung dieses
 * interfaces?
 * DataSource interface is implemented by a driver vendor >> Treiber Hersteller
 * NICHT JDK
 * 
 * 
 * - wo ist uns das pooling von objekten schon mal begegnet...?
 * Threadpool von ExecutorService
 * 
 * 
 * 
 */

public class ConnectionTestDemo {

	public static void main(String[] args) // viele Exceptions >> in OCP beachten
		throws SQLException, ClassNotFoundException, InstantiationException,
		IllegalAccessException, IllegalArgumentException, InvocationTargetException,
		NoSuchMethodException, SecurityException {

//		aus Treiberdoku
// Protokoll (jdbc:Hersteller)://LocalHost(IP-Addresse):Port von MySQL
// ?Parameter fuer Treiber (unterschiedlich von Treiberherstellern)
		String attributes = "?useUnicode=yes&characterEncoding=UTF-8&serverTimezone=GMT%2B02:00";
		String url = "jdbc:mysql://localhost:3306" + attributes;
//		String url = "jdbc:mysql://localhost:3306?useUnicode=yes&characterEncoding=UTF-8&serverTimezone=UTC";
		String user = "root";
		String password = "mysql"; // ggf ändern

		// variante 3 mit DriverManager
//		in jar MUSS es geben: META-INF/services/... hier java.sql.Driver
//		DriverManager ruft fuer jede DriverDatei .acceptUrl() auf (oder delegiert??), 
//		diese geben zurueck ob sie der richtige Treiber sind. 
//		DriverManager delegiert Arbeit
		try (Connection conn = DriverManager.getConnection(url, user, password)) {
			DatabaseMetaData meta = conn.getMetaData();
			System.out.println(meta.supportsTransactions());
			System.out.println(meta.getClass());
		}

		// variante 4
		// DataSource
		DataSource ds = MyDataSourceFactory.getMySQLDataSource();
		try (Connection conn = ds.getConnection()) {
			DatabaseMetaData meta = conn.getMetaData();
			System.out.println(meta.supportsTransactions());
			System.out.println(meta.getClass());
		}
	}
}
