package nl.maas.filerenamer.frontend;

import javafx.application.Application;
import nl.maas.filerenamer.frontend.javafx.JavaFXApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class WicketApplication {

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder()
                .sources(WicketApplication.class)
                .run(args);
        Application.launch(JavaFXApplication.class);
    }

}
