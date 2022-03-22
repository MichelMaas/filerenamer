package nl.maas.filerenamer.frontend.wicket.config

//@ConfigurationProperties(prefix = FilerenamerProperties.PROPERTY_PREFIX)
class FilerenamerProperties {
    var isEnabled = true

    companion object {
        const val PROPERTY_PREFIX = "nl.maas.filerenamer.frontend.wicket.config"
    }
}