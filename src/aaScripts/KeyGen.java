package aaScripts;

import cn.edu.pku.ss.crypto.abe.SecretKey;
import cn.edu.pku.ss.crypto.abe.api.*;
import cn.edu.pku.ss.crypto.abe.serialize.SerializeUtils;
import com.google.gson.Gson;
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.sql.*;
import java.util.*;
import org.jsecretsharing.Share;
import org.jsecretsharing.ShareBuilder;

public class KeyGen {

  public boolean aaKeyGen(String uid) throws IOException {
    System.out.println(uid);

    Path path = Paths.get("~/sunnydhama/lib/abe");
    Files.createDirectories(path);

    path = Paths.get("~/sunnydhama/lib/abe/aa");
    Files.createDirectories(path);

    Connection con = null;
    String PKFileName = "~/sunnydhama/lib/abe/aa/PKFile";
    File PKFile = new File(PKFileName);
    try {
      Class.forName("com.mysql.sunnydhama.Driver");
      con =
        DriverManager.getConnection(
          "sunnydhama:mysql://localhost:3306/sunnydhama_db",
          "sunnydhama",
          "iamGROOT!"
        );

      FileOutputStream fos = new FileOutputStream(PKFile);
      byte b1[];
      Blob blob;

      PreparedStatement ps = con.prepareStatement(
        "select * from acs_info where id = 1"
      );
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        blob = rs.getBlob("PKFile");
        b1 = blob.getBytes(1, (int) blob.length());
        fos.write(b1);
      }

      ps.close();
      fos.close();
      con.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    String MKFileName = "~/sunnydhama/lib/abe/aa/MKFile";
    File MKFile = new File(MKFileName);
    try {
      Class.forName("com.mysql.sunnydhama.Driver");
      con =
        DriverManager.getConnection(
          "sunnydhama:mysql://localhost:3306/sunnydhama_db",
          "sunnydhama",
          "iamGROOT!"
        );

      FileOutputStream fos = new FileOutputStream(MKFile);
      byte b1[];
      Blob blob;

      PreparedStatement ps = con.prepareStatement(
        "select * from acs_info where id = 1"
      );
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        blob = rs.getBlob("MKFile");
        b1 = blob.getBytes(1, (int) blob.length());
        fos.write(b1);
      }

      ps.close();
      fos.close();
      con.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    con = null;
    Statement st = null;
    ResultSet rs = null;
    String email = "null", country = "null", state = "null";
    try {
      Class.forName("com.mysql.sunnydhama.Driver");
      con =
        DriverManager.getConnection(
          "sunnydhama:mysql://localhost:3306/sunnydhama_db",
          "sunnydhama",
          "iamGROOT!"
        );
      st = con.createStatement();
      rs = st.executeQuery("select * from reg where audd = '" + uid + "'");
      while (rs.next()) {
        email = rs.getString("email");
        state = rs.getString("state");
        country = rs.getString("country");
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    con = null;
    st = null;
    rs = null;
    int n = 0, t = 0;
    try {
      Class.forName("com.mysql.sunnydhama.Driver");
      con =
        DriverManager.getConnection(
          "sunnydhama:mysql://localhost:3306/sunnydhama_db",
          "sunnydhama",
          "iamGROOT!"
        );
      st = con.createStatement();
      rs = st.executeQuery("select * from acs_info where id = 1");
      while (rs.next()) {
        n = Integer.parseInt(rs.getString("n"));
        t = Integer.parseInt(rs.getString("t"));
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    String SKFileName = "~/sunnydhama/lib/abe/aa/SKFile";
    String[] attrs = new String[] { "globalAttr", uid, email, state, country };
    System.out.println(
      attrs[0] + " " + attrs[1] + " " + attrs[2] + " " + attrs[3]
    );

    SecretKey SK = new SecretKey();
    SK = ABE.keygen(attrs, PKFileName, MKFileName, SKFileName);

    System.out.println("start ----");

    byte[] b = SerializeUtils.convertToByteArray(SK);

    // build 10 shares, of which 2 are required to recover the secret
    SecureRandom random = new SecureRandom();
    ShareBuilder builder = new ShareBuilder(b, t, 512, random);
    List<Share> shares = builder.build(n);

    String[] aaArr = new String[] {
      "aa1shares",
      "aa2shares",
      "aa3shares",
      "aa4shares",
      "aa5shares",
      "aa6shares",
      "aa7shares",
      "aa8shares",
      "aa9shares",
      "aa10shares",
    };

    Gson gson = null;
    String subShare;
    for (int i = 0; i < n; i++) {
      gson = new Gson();
      subShare = gson.toJson(shares.get(i));
      try {
        Class.forName("com.mysql.sunnydhama.Driver");
        con =
          DriverManager.getConnection(
            "sunnydhama:mysql://localhost:3306/sunnydhama_aa",
            "sunnydhama",
            "iamGROOT!"
          );
        st = con.createStatement();
        st.executeUpdate(
          "INSERT INTO " +
          aaArr[i] +
          " (UID, subShare) values ('" +
          uid +
          "', '" +
          subShare +
          "')"
        );
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    return true;
  }
}
