package nl.maas.filerenamer.frontend.wicket.config

import com.giffing.wicket.spring.boot.context.extensions.ApplicationInitExtension
import com.giffing.wicket.spring.boot.context.extensions.WicketApplicationInitConfiguration
import de.agilecoders.wicket.core.Bootstrap
import de.agilecoders.wicket.core.settings.ThemeProvider
import de.agilecoders.wicket.less.BootstrapLess
import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchThemeProvider
import org.apache.wicket.protocol.http.WebApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties

@ApplicationInitExtension
@ConditionalOnProperty(prefix = BootstrapProperties.PROPERTY_PREFIX, value = ["enabled"], matchIfMissing = true)
@ConditionalOnClass(
    BootstrapConfig::class
)
@EnableConfigurationProperties(
    BootstrapProperties::class
)
class BootstrapConfig : WicketApplicationInitConfiguration {
    @Autowired
    private lateinit var prop: BootstrapProperties
    override fun init(webApplication: WebApplication) {
        val themeProvider: ThemeProvider = BootswatchThemeProvider(prop.theme)
        prop.setThemeProvider(themeProvider)
        Bootstrap.install(webApplication, prop)
        BootstrapLess.install(webApplication)
    }
}