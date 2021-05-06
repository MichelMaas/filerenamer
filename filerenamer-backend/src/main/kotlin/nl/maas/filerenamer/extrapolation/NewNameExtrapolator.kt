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
        if ("NONE".equals(fileMetaData.sequence)) {
            return "${fileMetaData.name}.${fileMetaData.file.extension}";
        }
        val fillerLength = numberOfFiles.toString().length
        var name = "${fileMetaData.dirName} - ${
            (fileMetaData.sequence ?: 0).toString().padStart(fillerLength, '0')
        }.${fileMetaData.file.extension}"
        return name
    }
}