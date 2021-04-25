package nl.maas.filerenamer.frontend.wicket.config

import com.giffing.wicket.spring.boot.context.extensions.ApplicationInitExtension
import com.giffing.wicket.spring.boot.context.extensions.WicketApplicationInitConfiguration
import org.apache.wicket.protocol.http.WebApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
//
//@ApplicationInitExtension
//@ConditionalOnProperty(
//    prefix = nl.maas.filerenamer.frontend.wicket.config.FilerenamerProperties.PROPERTY_PREFIX,
//    value = ["enabled"],
//    matchIfMissing = true
//)
//@ConditionalOnClass(
//    FilerenamerConfig::class
//)
//@EnableConfigurationProperties(nl.maas.filerenamer.frontend.wicket.config.FilerenamerProperties::class)
class FilerenamerConfig : WicketApplicationInitConfiguration {
    @Autowired
    private val prop: nl.maas.filerenamer.frontend.wicket.config.FilerenamerProperties? = null
    override fun init(webApplication: WebApplication) {
        webApplication.cspSettings.blocking().disabled()
    }
}