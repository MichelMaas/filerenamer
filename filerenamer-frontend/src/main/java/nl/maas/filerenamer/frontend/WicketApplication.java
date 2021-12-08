package nl.maas.filerenamer.frontend;

import javafx.application.Application;
import nl.maas.filerenamer.frontend.javafx.JavaFXApplication;
import nl.maas.filerenamer.frontend.wicket.pages.SearchPage;
import org.apache.wicket.Page;
import org.apache.wicket.cdi.CdiConfiguration;
import org.apache.wicket.protocol.http.WebApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class WicketApplication extends WebApplication {

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder()
                .sources(WicketApplication.class)
                .run(args);
//        Application.launch(JavaFXApplication.class);
    }

    @Override
    protected void init() {
        super.init();
        CdiConfiguration.get(this).configure(this);
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return SearchPage.class;
    }
}
