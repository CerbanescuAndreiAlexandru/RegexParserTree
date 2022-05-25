import java.sql.*;
import java.text.SimpleDateFormat;

public class DB
{
    final String sDBName;

    public DB(final String sDBName)
    {
        this.sDBName = "jdbc:sqlite:" + sDBName;
    }

    public void Write(final Expression expression)
    {
        Connection conn = null;
        try {
            // create a connection to the database
            conn = DriverManager.getConnection(sDBName);

            System.out.println("Connection to SQLite has been established.");

            String query = "Insert into Expresie(Data,Regex,nPosError,ErrorDescription,isValid) values(?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(query);

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date getDate = new Date(System.currentTimeMillis());
            String data = formatter.format(getDate);

            ps.setString(1, data);
            ps.setString(2, expression.getsExp());
            ps.setInt(3,expression.getNposError());
            ps.setString(4,expression.getNposErrorDescription());
            ps.setBoolean(5,expression.getIsValid());
            ps.executeUpdate();



        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
