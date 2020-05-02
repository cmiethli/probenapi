
package de.proben.dbtest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import com.mysql.cj.jdbc.MysqlDataSource;

public class MyDataSourceFactory {

	public static DataSource getMySQLDataSource() {
		Properties props = new Properties();
		MysqlDataSource mysqlDS = new MysqlDataSource();

		try (FileInputStream fis = new FileInputStream(
			"resources/db/db.properties");) {
			props.load(fis);
			mysqlDS.setURL(props.getProperty("MYSQL_DB_URL"));
			mysqlDS.setUser(props.getProperty("MYSQL_DB_USERNAME"));
			mysqlDS.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mysqlDS;
	}
}
