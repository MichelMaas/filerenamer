package nl.maas.filerenamer.frontend.wicket.objects

import nl.maas.filerenamer.frontend.ContextProvider
import nl.maas.filerenamer.frontend.wicket.caches.GoogleTranslator
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.frontend.wicket.panels.SearchPanel
import nl.maas.filerenamer.io.FileUtils
import nl.maas.wicket.framework.objects.RiaPageProperties
import nl.maas.wicket.framework.objects.enums.NavbarOrientation

class FileRenamerRIABasePageProperties() : RiaPageProperties<ModelCache>(
    "File renamer",
    SearchPanel(),
    ContextProvider.ctx.getBean(ModelCache::class.java),
    ContextProvider.ctx.getBean(GoogleTranslator::class.java),
    brandName = "File renamer",
    navbarOrientation = NavbarOrientation.VERTICAL
)
