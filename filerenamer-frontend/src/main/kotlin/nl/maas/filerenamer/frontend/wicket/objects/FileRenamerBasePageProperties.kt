package nl.maas.filerenamer.frontend.wicket.objects

import nl.maas.filerenamer.frontend.ContextProvider
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.io.FileUtils
import nl.maas.wicket.framework.objects.BasePageProperties
import nl.maas.wicket.framework.services.TranslatorPlaceHolder

class FileRenamerBasePageProperties private constructor() : BasePageProperties<ModelCache>(
    ContextProvider.ctx.getBean(ModelCache::class.java),
    TranslatorPlaceHolder(),
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
