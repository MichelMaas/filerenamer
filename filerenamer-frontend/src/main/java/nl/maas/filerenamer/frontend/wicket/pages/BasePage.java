package nl.maas.filerenamer.frontend.wicket.pages;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarComponents;
import org.apache.wicket.markup.html.GenericWebPage;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import static de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType.search_s;

public class BasePage extends GenericWebPage<Void> {

    public BasePage(final PageParameters parameters) {
        super(parameters);

        add(newNavbar("navbar"));
    }

    protected Navbar newNavbar(String markupId) {
        Navbar navbar = new Navbar(markupId);

        navbar.setPosition(Navbar.Position.TOP);
        navbar.setInverted(true);

        navbar.setBrandName(Model.of("Filerenamer"));

        navbar.addComponents(NavbarComponents.transform(Navbar.ComponentPosition.LEFT,
                new NavbarButton<Void>(SearchPage.class, Model.of("Find"))
                        .setIconType(search_s)));


        return navbar;
    }

}
