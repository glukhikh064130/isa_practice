package repos;

import entities.Product;
import exceptions.StorageException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductRepo {
    private final DataSource ds;

    public ProductRepo(DataSource dataSource) {
        this.ds = dataSource;
    }

    /**
     * Returns all products.
     * Note: be careful with huge data set :)
     * @return List of all products in the table.
     * @throws StorageException throws when SQL error
     */
    public List<Product> getAll() throws StorageException {
        try (Connection conn = this.ds.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from products");

            List<Product> products = new ArrayList<>();

            while (rs.next()) {
                products.add(mapToProduct(rs));
            }

            return products;
        } catch (SQLException e) {
            throw new StorageException("ProductRepo.getAll()", e);
        }
    }

    /**
     * Returns the product by ID.
     * @param id Product ID.
     * @return The product or null if not exists.
     * @throws StorageException throws when SQL error
     */
    public Product getById(int id) throws StorageException {
        try (Connection conn = this.ds.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("select * from products where id = ?");
            stmt.setObject(1, id);

            ResultSet rs = stmt.executeQuery();

            Product product = null;
            if (rs.next()) {
                product = mapToProduct(rs);
            }
            rs.close();

            return product;
        } catch (SQLException e) {
            throw new StorageException("ProductRepo.getById()", e);
        }
    }

    /**
     * Returns the most expensive products.
     * @return The most expensive products.
     * @throws StorageException throws when SQL error
     */
    public List<Product> getMostExpensive() throws StorageException {
        try (Connection conn = this.ds.getConnection()) {
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("select * from products where price = (" +
                    "select max(price) from products)");

            List<Product> products = new ArrayList<>();

            while (rs.next()) {
                products.add(mapToProduct(rs));
            }

            return products;
        } catch (SQLException e) {
            throw new StorageException("ProductRepo.getMostExpensive()", e);
        }
    }

    /**
     * Returns products with prices between given range
     * @param from From.
     * @param to To.
     * @return Products with prices between given range
     * @throws StorageException throws when SQL error
     */
    public List<Product> getProductsWithPriceRange(double from, double to) throws StorageException {
        try (Connection conn = this.ds.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("select * from products where price between ? and ?");
            stmt.setObject(1, from);
            stmt.setObject(2, to);

            ResultSet rs = stmt.executeQuery();

            List<Product> products = new ArrayList<>();

            while (rs.next()) {
                products.add(mapToProduct(rs));
            }

            return products;
        } catch (SQLException e) {
            throw new StorageException("ProductRepo.getProductsWithPriceRange()", e);
        }
    }

    /**
     * Creates a new product.
     * @param product Product.
     * @throws StorageException throws when SQL error
     */
    public void create(Product product) throws StorageException {
        try (Connection conn = this.ds.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("insert into products(id, good, price, category_name) values (?, ?, ?, ?)");
            stmt.setObject(1, product.getId());
            stmt.setObject(2, product.getGood());
            stmt.setObject(3, product.getPrice());
            stmt.setObject(4, product.getCategoryName());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException("ProductRepo.create()", e);
        }
    }

    /**
     * Creates multiple products (batch insert).
     * @param products Products.
     * @throws StorageException throws when SQL error
     */
    public void createBatch(List<Product> products) throws StorageException {
        try (Connection conn = this.ds.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("insert into products(id, good, price, category_name) values(?, ?, ?, ?)");
            conn.setAutoCommit(false);

            for (Product p : products) {
                stmt.setObject(1, p.getId());
                stmt.setObject(2, p.getGood());
                stmt.setObject(3, p.getPrice());
                stmt.setObject(4, p.getCategoryName());
                stmt.addBatch();
            }

            int[] inserts = stmt.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new StorageException("ProductRepo.createBatch()", e);
        }
    }

    /**
     * Update product by ID.
     * @param id      Product ID.
     * @param product Product.
     * @throws StorageException throws when SQL error
     */
    public void update(int id, Product product) throws StorageException {
        try (Connection conn = this.ds.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("update products set id = ?, good = ?, price = ?, category_name = ? where id = ?");
            stmt.setObject(1, product.getId());
            stmt.setObject(2, product.getGood());
            stmt.setObject(3, product.getPrice());
            stmt.setObject(4, product.getCategoryName());
            stmt.setObject(5, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException("ProductRepo.update()", e);
        }
    }

    public void increaseCategoryPrice(String categoryName, double percent) throws StorageException {
        try (Connection conn = this.ds.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("update products set price = price + price * ? where category_name = ?");
            stmt.setObject(1, percent);
            stmt.setObject(2,categoryName);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException("ProductRepo.increaseCategoryPrice()", e);
        }
    }

    /**
     * Removes the product by ID.
     * @param id Product ID.
     * @throws StorageException throws when SQL error
     */
    public void delete(int id) throws StorageException {
        try (Connection conn = this.ds.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("delete from products where id = ?");
            stmt.setObject(1, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException("ProductRepo.delete()", e);
        }
    }

    /**
     * Removes all products owned by category.
     * @param categoryName Category name.
     * @throws StorageException throws when SQL error
     */
    public void deleteAllCategoryProducts(String categoryName) throws StorageException {
        try (Connection conn = this.ds.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("delete from products where category_name = ?");
            stmt.setObject(1, categoryName);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException("ProductRepo.deleteAllCategoryProducts()", e);
        }
    }

    /**
     * Truncates the products table.
     * @throws StorageException throws when SQL error
     */
    public void truncate() throws StorageException {
        try (Connection conn = this.ds.getConnection()) {
            Statement stmt = conn.createStatement();

            stmt.executeUpdate("truncate products");
        } catch (SQLException e) {
            throw new StorageException("ProductRepo.truncate()", e);
        }
    }

    /**
     * Maps result set to the product object.
     * @param rs Result set.
     * @return Product constructed from result set.
     * @throws SQLException throws when SQL error
     */
    private static Product mapToProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("good"),
                rs.getDouble("price"),
                rs.getString("category_name")
        );
    }
}
