package nl.maas.filerenamer.frontend.wicket.objects

import nl.maas.filerenamer.frontend.ContextProvider
import nl.maas.filerenamer.frontend.wicket.caches.GoogleTranslator
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.io.FileUtils
import nl.maas.wicket.framework.objects.BasePageProperties

class FileRenamerBasePageProperties private constructor() : BasePageProperties<ModelCache>(
    ContextProvider.ctx.getBean(ModelCache::class.java),
    ContextProvider.ctx.getBean(GoogleTranslator::class.java),
    brandName = "File renamer",
    iconPath = FileUtils.findFile("icon.png"),
    brandPath = FileUtils.findFile("brand.png")
) {
    companion object {
        val instance = FileRenamerBasePageProperties()
        fun get(): FileRenamerBasePageProperties {
            return instance
        }
    }
}
