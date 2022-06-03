package controllers;

import entities.Product;
import exceptions.StorageException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import repos.ProductRepo;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PrimaryController implements Initializable {
    private final ProductRepo repo;

    @FXML
    private TableView<Product> productsTable;

    @FXML
    private TableColumn<Product, Integer> idCol;

    @FXML
    private TableColumn<Product, String> goodCol;

    @FXML
    private TableColumn<Product, Double> priceCol;

    @FXML
    private TableColumn<Product, String> categoryCol;

    private final ObservableList<Product> products = FXCollections.observableArrayList();

    public PrimaryController(ProductRepo repo) {
        this.repo = repo;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        this.goodCol.setCellValueFactory(new PropertyValueFactory<>("good"));
        this.priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        this.categoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

        this.productsTable.setItems(products);
    }

    @FXML
    private void loadProducts(ActionEvent event) {
        List<Product> allProducts;
        try {
            allProducts = this.repo.getAll();
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }

        this.products.clear();
        this.products.addAll(allProducts);
    }
}
