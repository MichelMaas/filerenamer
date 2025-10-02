package nl.maas.filerenamer.frontend.wicket.caches

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import nl.maas.filerenamer.domain.ExtrapolationResult
import nl.maas.filerenamer.extrapolation.FileMetaData
import nl.maas.filerenamer.frontend.services.FileService
import nl.maas.filerenamer.frontend.wicket.objects.SearchCriteria
import nl.maas.wicket.framework.objects.Tuple
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import java.io.File
import javax.inject.Inject

@Component
class ModelCache : nl.maas.wicket.framework.services.ModelCache {

    var selectedFolder: File? = null
    var files: Map<File, ExtrapolationResult> = mapOf()
    var searchCriteria: SearchCriteria = SearchCriteria()
    var foundEpisodes: Map<String, List<String>> = mapOf()
    var series: MutableList<Tuple> = mutableListOf()

    @Inject
    private lateinit var fileService: FileService

    override fun isEmpty(): Boolean {
        return files.isEmpty() && foundEpisodes.isEmpty()
    }

    override fun refresh() {
        files = runBlocking {
            async {
                fileService.findAndProcessFrom(
                    searchCriteria.folderPath,
                    searchCriteria.sequence
                )
            }.await()
        }
    }

    fun isFileSelected(): Boolean {
        return selectedFolder != null
    }

    fun getSelectedResult(): ExtrapolationResult {
        return if (isFileSelected()) {
            files[selectedFolder]!!
        } else {
            ExtrapolationResult(listOf())
        }
    }

    fun getFilesForSelectedFolder(): List<FileMetaData> {
        return if (isFileSelected()) {
            files[selectedFolder]!!.files
        } else {
            listOf()
        }
    }

    fun getSelectedFolderDisplayPath(): String {
        return selectedFolder?.path?.substringAfter(searchCriteria.folderPath) ?: StringUtils.EMPTY
    }

}