import controllers.PrimaryController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repos.ProductRepo;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Config cfg = Config.parse(new String[] {"jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "pass"});
        Pool pool = new Pool(cfg.getDbUrl(), cfg.getDbUser(), cfg.getDbPass());
        ProductRepo productRepo = new ProductRepo(pool);

        PrimaryController controller = new PrimaryController(productRepo);
        Scene scene = new Scene(loadFXML("primary", controller), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    private static Parent loadFXML(String fxml, Object controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        loader.setControllerFactory(controllerClass -> controller);
        return loader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
