package nl.maas.filerenamer.frontend.services

import nl.maas.filerenamer.domain.ExtrapolationResult
import nl.maas.filerenamer.extrapolation.FileMetaData
import nl.maas.filerenamer.extrapolation.NewNameExtrapolator
import nl.maas.filerenamer.extrapolation.SequenceExtrapolator
import nl.maas.filerenamer.extrapolation.SequenceExtrapolator.Companion.SEQUENCE
import nl.maas.filerenamer.io.FileHandler
import org.springframework.stereotype.Component
import java.io.File

@Component
class FileFinderService {

    fun findIn(path: String): Map<File, List<FileMetaData>> {
        val filesIn = FileHandler().searchFilesIn(path)
        return filesIn.mapNotNull { it.key to it.value.filter { fl -> fl.isFile }.map { fl -> FileMetaData(fl) } }
            .toMap()
    }

    fun findAndProcessFrom(path: String, sequence: SEQUENCE): Map<File, ExtrapolationResult> {
        val findIn = findIn(path)
        val sequenceExtrapolator = SequenceExtrapolator.instance(sequence)
        val sequenced = findIn.filter { it.value.isNotEmpty() }
            .map { it.key to sequenceExtrapolator.findSequenceForFiles(it.value) }.toMap()
        sequenced.forEach { NewNameExtrapolator().determineNameForFiles(it.value.files) }
        return sequenced.filter { it.value.renameRequired() }
    }
}