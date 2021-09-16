package nl.maas.filerenamer.extrapolation

class NewNameExtrapolator {
    private var numberOfFiles: Int = 0

    companion object {
        fun determineName(fileMetaData: FileMetaData, numberOfFiles: Int): String {
            val newNameExtrapolator = NewNameExtrapolator()
            newNameExtrapolator.numberOfFiles = numberOfFiles
            return newNameExtrapolator.determineName(fileMetaData)
        }
    }

    fun determineNameForFiles(fileData: List<FileMetaData>): List<FileMetaData> {
        numberOfFiles = fileData.size
        fileData.forEach { fileMetaData -> fileMetaData.newName = determineName(fileMetaData) }
        return ArrayList(fileData)
    }

    private fun determineName(fileMetaData: FileMetaData): String {

        val fillerLength = numberOfFiles.toString().length
        var name = if ("NONE".equals(fileMetaData.sequence)) {
            "${fileMetaData.name}.${fileMetaData.file.extension}"
        } else {
            "${fileMetaData.dirName} - ${
                (fileMetaData.sequence ?: 0).toString().padStart(fillerLength, '0')
            }.${fileMetaData.file.extension}"
        }
        if (wasManuallyAltered(fileMetaData, name)) {
            return fileMetaData.newName!!
        } else {
            return name
        }
    }

    private fun wasManuallyAltered(fileMetaData: FileMetaData, determinedNewName: String): Boolean {
        return !fileMetaData.newName.isNullOrEmpty() && (fileMetaData.sequence.isNullOrEmpty() && !fileMetaData.newName.equals(
            determinedNewName
        ))
    }
}