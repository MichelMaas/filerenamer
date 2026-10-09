package nl.maas.filerenamer.frontend.wicket.objects

import nl.maas.filerenamer.frontend.ContextProvider
import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.filerenamer.frontend.wicket.panels.SearchPanel
import nl.maas.wicket.framework.objects.RiaPageProperties
import nl.maas.wicket.framework.objects.enums.NavbarOrientation
import nl.maas.wicket.framework.services.TranslatorPlaceHolder

class FileRenamerRIABasePageProperties() : RiaPageProperties<ModelCache>(
    "File renamer",
    SearchPanel(),
    ContextProvider.ctx.getBean(ModelCache::class.java),
    TranslatorPlaceHolder(),
    brandName = "File renamer",
    navbarOrientation = NavbarOrientation.VERTICAL
)
