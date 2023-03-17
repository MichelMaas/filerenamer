package nl.maas.filerenamer.frontend.wicket.caches

import nl.maas.filerenamer.domain.ExtrapolationResult
import nl.maas.filerenamer.frontend.services.FileService
import nl.maas.filerenamer.frontend.wicket.objects.SearchCriteria
import org.springframework.stereotype.Component
import java.io.File
import javax.inject.Inject

@Component
class ModelCache : nl.maas.wicket.framework.services.ModelCache {

    var selectedFolder: File? = null
    var files: Map<File, ExtrapolationResult> = mapOf()
    var searchCriteria: SearchCriteria = SearchCriteria()

    @Inject
    private lateinit var fileService: FileService

    override fun isEmpty(): Boolean {
        return files.isNullOrEmpty()
    }

    override fun refresh() {
        files = fileService.findAndProcessFrom(
            searchCriteria.folderPath,
            searchCriteria.sequence
        )
    }

    fun isFileSelected(): Boolean {
        return selectedFolder != null
    }

}