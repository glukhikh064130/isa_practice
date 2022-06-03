import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Utils {
    public static void createTable(Pool pool) throws SQLException {
        Connection conn = pool.getConnection();
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("create table if not exists products (" +
                "id int primary key," +
                "good text not null," +
                "price real not null," +
                "category_name text not null)");

    }

    private Utils() {}
}
