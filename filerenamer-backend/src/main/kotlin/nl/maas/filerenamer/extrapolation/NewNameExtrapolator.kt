package nl.maas.filerenamer.extrapolation

import nl.maas.filerenamer.domain.enums.SEQUENCE
import nl.maas.filerenamer.io.NominatimClient

class NewNameExtrapolator(val sequence: SEQUENCE) {
    private var numberOfFiles: Int = 0

    companion object {
        fun determineName(fileMetaData: FileMetaData, numberOfFiles: Int, sequence: SEQUENCE): Array<String> {
            val newNameExtrapolator = NewNameExtrapolator(sequence)
            newNameExtrapolator.numberOfFiles = numberOfFiles
            return newNameExtrapolator.determineName(fileMetaData)
        }
    }

    fun determineNameForFiles(fileData: List<FileMetaData>): List<FileMetaData> {
        numberOfFiles = fileData.size
        fileData.forEach { fileMetaData -> fileMetaData.proposedNames = determineName(fileMetaData) }
        return ArrayList(fileData)
    }

    private fun determineName(fileMetaData: FileMetaData): Array<String> {

        val fillerLength = numberOfFiles.toString().length
        var names = if ("NONE".equals(fileMetaData.sequence)) {
            arrayOf(
                "${fileMetaData.name}.${fileMetaData.file.extension}",
                "${fileMetaData.file.parentFile.parentFile.name}.${fileMetaData.name}.${fileMetaData.file.extension}"
            )
        } else {
            when (sequence) {
                SEQUENCE.NUMBER ->
                    arrayOf(
                        "${fileMetaData.dirName} - ${
                            (fileMetaData.sequence ?: 0).toString().padStart(fillerLength, '0')
                        }.${fileMetaData.file.extension}",
                        "${fileMetaData.file.parentFile.parentFile.name}.${fileMetaData.dirName} - ${
                            (fileMetaData.sequence ?: 0).toString().padStart(fillerLength, '0')
                        }.${fileMetaData.file.extension}"
                    )

                else -> arrayOf(
                    "${fileMetaData.sequence} - ${NominatimClient.fetchLocation(fileMetaData.file)}.${fileMetaData.file.extension}",
                    "${fileMetaData.dirName} - ${
                        (fileMetaData.sequence ?: 0).toString().padStart(fillerLength, '0')
                    }.${fileMetaData.file.extension}"
                )
            }
        }
        if (wasManuallyAltered(fileMetaData, names)) {
            return fileMetaData.proposedNames
        }
        return names.map { sanitizeFileName(it) }.toTypedArray()
    }

    private fun sanitizeFileName(name: String): String {
        return name
            .replace(Regex("""[\\/:*?"<>|\p{Cntrl}]+"""), " - ")
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    private fun wasManuallyAltered(fileMetaData: FileMetaData, determinedNewName: Array<String>): Boolean {
        return !fileMetaData.newName.isNullOrEmpty() && (fileMetaData.sequence.isNullOrEmpty() && !determinedNewName.contains(
            fileMetaData.newName
        ))
    }
}