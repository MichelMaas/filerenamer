package nl.maas.filerenamer.frontend;

import nl.maas.filerenamer.frontend.wicket.pages.HomePage;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
@ConditionalOnWebApplication
public class WicketApplication extends WebApplication {

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder()
                .sources(WicketApplication.class)
                .run(args);
    }


    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }

    @Override
    protected void init() {
        super.init();
        getCspSettings().blocking().disabled();
    }
}
