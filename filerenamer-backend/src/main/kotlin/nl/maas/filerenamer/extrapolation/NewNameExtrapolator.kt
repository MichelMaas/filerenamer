package nl.maas.filerenamer.extrapolation

class NewNameExtrapolator {
    private var numberOfFiles: Int = 0

    companion object {
        fun determineName(fileMetaData: FileMetaData, numberOfFiles: Int): Array<String> {
            val newNameExtrapolator = NewNameExtrapolator()
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
            arrayOf(
                "${fileMetaData.dirName} - ${
                    (fileMetaData.sequence ?: 0).toString().padStart(fillerLength, '0')
                }.${fileMetaData.file.extension}",
                "${fileMetaData.file.parentFile.parentFile.name}.${fileMetaData.dirName} - ${
                    (fileMetaData.sequence ?: 0).toString().padStart(fillerLength, '0')
                }.${fileMetaData.file.extension}"
            )
        }
        if (wasManuallyAltered(fileMetaData, names)) {
            return fileMetaData.proposedNames
        }
        return names
    }

    private fun wasManuallyAltered(fileMetaData: FileMetaData, determinedNewName: Array<String>): Boolean {
        return !fileMetaData.newName.isNullOrEmpty() && (fileMetaData.sequence.isNullOrEmpty() && !determinedNewName.contains(
            fileMetaData.newName
        ))
    }
}