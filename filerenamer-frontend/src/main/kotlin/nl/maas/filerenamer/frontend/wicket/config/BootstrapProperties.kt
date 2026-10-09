package nl.maas.filerenamer.frontend.wicket.config

import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchTheme
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = BootstrapProperties.PROPERTY_PREFIX)
class BootstrapProperties : AbstractBootstrapProperties() {
    init {
        theme = BootswatchTheme.Litera
    }


    companion object {
        const val PROPERTY_PREFIX = "nl.maas.filerenamer.frontend.wicket.config"
    }
}