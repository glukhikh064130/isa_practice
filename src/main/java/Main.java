import exceptions.GenericException;
import repos.ProductRepo;

import java.sql.SQLException;

// Usage example: <bin> jdbc:postgresql://127.0.0.1:5432/postgres postgres postgrespw
public class Main {
    public static void main(String[] args) {
        try {
            Config cfg = Config.parse(args);

            Pool pool = new Pool(cfg.getDbUrl(), cfg.getDbUser(), cfg.getDbPass());
            ProductRepo productRepo = new ProductRepo(pool);

            Utils.createTable(pool);
        } catch (GenericException e) {
            System.out.println(e.getFullMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
