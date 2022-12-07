package DBcon;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbConnection {

  public static Connection getConnection() {
    Connection con = null;
    try {
      Class.forName("com.mysql.sunnydhama.Driver");
      con =
        DriverManager.getConnection(
          "sunnydhama:mysql://localhost:3306/sunnydhama_db",
          "sunnydhama",
          "iamGROOT!"
        );
    } catch (Exception e) {
      e.printStackTrace();
    }
    return con;
  }
}
