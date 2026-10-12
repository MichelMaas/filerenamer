package nl.maas.filerenamer.frontend.wicket.config

import com.giffing.wicket.spring.boot.context.extensions.ApplicationInitExtension
import nl.maas.wicket.framework.config.AbstractBootstrapConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties

@ApplicationInitExtension
@EnableConfigurationProperties(
    BootstrapProperties::class
)
class BootstrapConfig @Autowired constructor(prop: BootstrapProperties) : AbstractBootstrapConfig(prop) {
    init {
        println("### BOOTSTRAP CONFIG CONSTRUCTED ###")

    }
}