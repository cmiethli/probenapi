package probenapi.de.proben.dbtest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class DataSourceTest {

	public static void main(String[] args) {
		testDataSource("mysql");
	}

	private static void testDataSource(String dbType) {
		DataSource ds = null;
		if ("mysql".equals(dbType)) {
			ds = MyDataSourceFactory.getMySQLDataSource();
		} else {
			System.out.println("invalid db type");
			return;
		}
		try (Connection con = ds.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select id, ergebnis from probe");) {
			while (rs.next()) {
				System.out.println(
					"ID=" + rs.getInt("id") + ", Ergebnis=" + rs.getString("ergebnis"));
			}
		} catch (SQLException e) {
			e.printStackTrace();

		}
	}

}
