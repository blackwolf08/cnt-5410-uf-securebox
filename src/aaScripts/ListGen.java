package aaScripts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ListGen {

  public boolean aaListGen(String aid) {
    try {
      Class.forName("com.mysql.sunnydhama.Driver");
      Connection con = DriverManager.getConnection(
        "sunnydhama:mysql://localhost:3306/sunnydhama_aa",
        "sunnydhama",
        "iamGROOT!"
      );
      Statement st = con.createStatement();
      st.executeUpdate("INSERT INTO aalist (AID) values ('" + aid + "')");
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return true;
  }
}
