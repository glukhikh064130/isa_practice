import entities.Product;
import exceptions.StorageException;
import org.jetbrains.annotations.NotNull;
import org.junit.*;
import repos.ProductRepo;

import java.util.*;

public class ProductRepoTest {
    private static ProductRepo repo;

    @BeforeClass
    public static void before() throws Exception {
        String[] args = new String[] {"jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "pass"};
        Config cfg = Config.parse(args);
        Pool pool = new Pool(cfg.getDbUrl(), cfg.getDbUser(), cfg.getDbPass());
        Utils.createTable(pool);
        repo = new ProductRepo(pool);
    }

    @After
    public void afterEach() throws Exception {
        repo.truncate();
    }

    @Test
    public void getAll_FilledTable() throws StorageException {
        List<Product> expectedProducts = this.fillProductsTable(5);

        // getAll does not guarantee the ordering, we sort it manually
        List<Product> products = repo.getAll().stream()
                .sorted(Comparator.comparing(Product::getId))
                .toList();

        Assert.assertEquals(expectedProducts.size(), products.size());
        for (int i = 0; i < expectedProducts.size(); i++) {
            Assert.assertEquals(expectedProducts.get(i), products.get(i));
        }
    }

    @Test
    public void getAll_EmptyTable() throws StorageException {
        List<Product> products = repo.getAll();
        Assert.assertEquals(0, products.size());
    }

    @Test
    public void getById_ExistedProduct() throws StorageException {
        Product expectedProduct = this.fillProductsTable(1).get(0);
        Product product = repo.getById(1);
        Assert.assertEquals(expectedProduct, product);
    }

    @Test
    public void getById_NonExistedProduct() throws StorageException {
        Product product = repo.getById(999);
        Assert.assertNull(product);
    }

    @Test
    public void getMostExpensive_Single() throws StorageException {
        List<Product> createdProducts = this.fillProductsTable(5);
        List<Product> expectedProducts = Collections.singletonList(createdProducts.get(4));

        List<Product> products = repo.getMostExpensive();

        Assert.assertEquals(expectedProducts.size(), products.size());
        for (int i = 0; i < expectedProducts.size(); i++) {
            Assert.assertEquals(expectedProducts.get(i), products.get(i));
        }
    }

    @Test
    public void getMostExpensive_Multiple() throws StorageException {
        List<Product> createdProducts = this.fillProductsTable(5);
        Product firstExpensiveProduct = createdProducts.get(4);
        Product secondExpensiveProduct = new Product(6, "6", firstExpensiveProduct.getPrice(), "all");
        repo.create(secondExpensiveProduct);
        List<Product> expectedProducts = Arrays.asList(firstExpensiveProduct, secondExpensiveProduct);

        List<Product> products = repo.getMostExpensive().stream()
                .sorted(Comparator.comparing(Product::getId))
                .toList();

        Assert.assertEquals(expectedProducts.size(), products.size());
        for (int i = 0; i < expectedProducts.size(); i++) {
            Assert.assertEquals(expectedProducts.get(i), products.get(i));
        }
    }

    @Test
    public void getMostExpensive_EmptyTable() throws StorageException {
        List<Product> products = repo.getMostExpensive();
        Assert.assertEquals(0, products.size());
    }

    @Test
    public void getProductsWithPriceRange() throws StorageException {
        final double from = 10.0;
        final double to = 20.0;

        List<Product> createdProducts = this.fillProductsTable(5);
        List<Product> expectedProducts = createdProducts.stream()
                .filter(p -> p.getPrice() >= from && p.getPrice() <= to)
                .sorted(Comparator.comparing(Product::getId))
                .toList();

        List<Product> products = repo.getProductsWithPriceRange(from, to).stream()
                .sorted(Comparator.comparing(Product::getId))
                .toList();

        Assert.assertEquals(expectedProducts.size(), products.size());
        for (int i = 0; i < expectedProducts.size(); i++) {
            Assert.assertEquals(expectedProducts.get(i), products.get(i));
        }
    }

    @Test
    public void getProductsWithPriceRange_FilledTableWithoutFoundProducts() throws StorageException {
        final double from = 1000000.0;
        final double to = 2000000.0;

        this.fillProductsTable(5);
        List<Product> products = repo.getProductsWithPriceRange(from, to);
        Assert.assertEquals(0, products.size());
    }

    @Test
    public void getProductsWithPriceRange_EmptyTable() throws StorageException {
        final double from = 1000000.0;
        final double to = 2000000.0;

        List<Product> products = repo.getProductsWithPriceRange(from, to);
        Assert.assertEquals(0, products.size());
    }

    @Test
    public void create() throws StorageException {
        Product expectedProduct = new Product(1, "1", 10.0, "all");
        repo.create(expectedProduct);

        Product product = repo.getById(1);

        Assert.assertEquals(expectedProduct, product);
    }

    @Test(expected = StorageException.class)
    public void create_Duplicate() throws StorageException {
        List<Product> products = this.fillProductsTable(1);
        repo.create(products.get(0));
    }

    @Test
    public void createBatch() throws StorageException {
        Product expectedProduct = new Product(1, "1", 10.0, "all");
        repo.createBatch(Collections.singletonList(expectedProduct));

        Product product = repo.getById(1);

        Assert.assertEquals(expectedProduct, product);
    }

    @Test(expected = StorageException.class)
    public void createBatch_Duplicate() throws StorageException {
        List<Product> products = this.fillProductsTable(5);
        products.add(new Product(100, "100", 1.0, "all"));
        repo.createBatch(products);
    }

    @Test
    public void update() throws StorageException {
        this.fillProductsTable(5);

        Product expectedProduct = new Product(1, "new", 1.0, "new");
        repo.update(1, expectedProduct);

        Product product = repo.getById(1);

        Assert.assertEquals(expectedProduct, product);
    }

    @Test
    public void increaseCategoryPrice() throws StorageException {
        String category = "tv";
        double basePrice = 10.0;
        double percent = 0.5;
        double expected = 15.0;

        Product tv = new Product(1, "samsung", basePrice, category);
        Product other = new Product(2, "other", basePrice, "other");

        this.fillWith(tv, other);

        repo.increaseCategoryPrice(category, percent);

        tv = repo.getById(1);
        Assert.assertEquals(expected, tv.getPrice(), 0.1);

        other = repo.getById(2);
        Assert.assertEquals(basePrice, other.getPrice(), 0.1);
    }

    @Test
    public void delete() throws StorageException {
        int amount = 5;
        this.fillProductsTable(amount);

        int idToDelete = 5;
        repo.delete(idToDelete);

        List<Product> products = repo.getAll();
        Assert.assertEquals(amount - 1, products.size());

        products = products.stream().filter(p -> p.getId() == idToDelete).toList();
        Assert.assertEquals(0, products.size());
    }

    @Test
    public void deleteAllCategoryProducts() throws StorageException {
        String category = "tv";

        Product tv = new Product(1, "samsung", 10.0, category);
        Product other = new Product(2, "other", 20.0, "other");

        this.fillWith(tv, other);

        repo.deleteAllCategoryProducts(category);

        List<Product> products = repo.getAll();
        Assert.assertEquals(1, products.size());
        Assert.assertFalse(products.stream().anyMatch(p -> p.getCategoryName().equals(category)));
    }

    @Test
    public void truncate() throws StorageException {
        this.fillProductsTable(10);

        repo.truncate();

        Assert.assertEquals(0, repo.getAll().size());
    }

    private void fillWith(Product... products) throws StorageException {
        repo.createBatch(Arrays.stream(products).toList());
    }

    private @NotNull List<Product> fillProductsTable(int amount) throws StorageException {
        List<Product> products = new ArrayList<>(amount);
        for (int i = 1; i <= amount; i++) {
            products.add(new Product(i, String.valueOf(i), i * 10.0, "all"));
        }

        repo.createBatch(products);

        return products;
    }
}
