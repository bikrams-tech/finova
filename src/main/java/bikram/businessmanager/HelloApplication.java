package bikram.businessmanager;

import bikram.businessmanager.controller.MainViewController;
import bikram.businessmanager.entity.Company;
import bikram.businessmanager.service.CompanyService;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.utils.Navigator;
import bikram.businessmanager.utils.SessionContext;
import bikram.businessmanager.utils.SessionPersistence;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    CompanyService companyService = ServiceProvider.services().getCompanyService();

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                HelloApplication.class.getResource("/bikram/businessmanager/view/mainview.fxml")
        );

        Scene scene = new Scene(loader.load(), 1080, 900);

        stage.setTitle("Business Manager");
        stage.setScene(scene);
        stage.setMinWidth(700);
        stage.setMinHeight(300);
        stage.setResizable(true);

        Navigator.init(stage);

        MainViewController controller = loader.getController();
        Navigator.setMainContainer(controller.getMainContainer());

        stage.show();
        startup();
    }
    public void startup(){
        Long savedCompanyId = SessionPersistence.loadCompanyId();
        if (savedCompanyId != null){
            Navigator.navigate("/bikram/businessmanager/dashboard/dashboardview.fxml");
        }
        if (savedCompanyId == null) {
            Navigator.showOverlay("/bikram/businessmanager/view/CompanySelectionView.fxml");
            //Navigator.loadView("/bikram/businessmanager/view/CompanySelectionView.fxml");
            return;
        }
        Company company = companyService.getById(savedCompanyId);
        if (company == null) {
            SessionContext.clear();
            Navigator.showOverlay("/bikram/businessmanager/form/companyregisterform.fxml");
            return;
        }
        SessionContext.setCurrentCompany(company);
        Navigator.navigate("/bikram/businessmanager/dashboard/dashboardview.fxml");
    }

    public static void main(String[] args) {
        launch();
    }
}