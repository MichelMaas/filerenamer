package nl.maas.filerenamer.frontend.wicket.caches

import nl.maas.filerenamer.domain.ExtrapolationResult
import org.springframework.stereotype.Component
import java.io.File

@Component
class ModelCache : nl.maas.wicket.framework.services.ModelCache {

    var selectedFolder: File? = null
    var files: Map<File, ExtrapolationResult> = mapOf()

    override fun isEmpty(): Boolean {
        return files.isNullOrEmpty()
    }

    override fun refresh() {
        TODO("Not yet implemented")
    }

    fun isFileSelected(): Boolean {
        return selectedFolder != null
    }

}