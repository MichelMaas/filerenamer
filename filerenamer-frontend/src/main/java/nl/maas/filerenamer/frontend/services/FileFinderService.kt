package nl.maas.filerenamer.frontend.services

import nl.maas.filerenamer.extrapolation.FileMetaData
import nl.maas.filerenamer.extrapolation.NewNameExtrapolator
import nl.maas.filerenamer.extrapolation.SequenceExtrapolator
import nl.maas.filerenamer.extrapolation.SequenceNumberExtrapolator
import nl.maas.filerenamer.io.FileHandler
import org.springframework.stereotype.Component
import java.io.File

@Component
open class FileFinderService {
    fun findFilesToRename(path: String): Map<String, List<FileMetaData>> {
        var filesIn = FileHandler().searchFilesIn(path)
        return processFiles(filesIn, SequenceNumberExtrapolator())
    }

    private fun processFiles(
        filesIn: Map<File, List<File>>,
        sequenceExtrapolator: SequenceExtrapolator
    ): Map<String, List<FileMetaData>> {
        var map: Map<String, List<FileMetaData>> =
            filesIn.keys.map { key -> key.absolutePath to filesIn.get(key)!!.map { file -> FileMetaData(file) } }
                .toMap()
        map.keys.forEach { key -> sequenceExtrapolator.findSequenceForFiles(map.get(key)!!) }
        map.keys.forEach { key -> NewNameExtrapolator().determineNameForFiles(map.get(key)!!) }
        return map.filterKeys { key ->
            map.get(key)!!.any { data -> !data.newName.isNullOrBlank() && !data.newName.equals(data.file.name) }
        }
    }
}