package nl.maas.filerenamer.extrapolation

class NewNameExtrapolator {
 var numberOfFiles: Int = 0
    fun determineNameForFiles(fileData: List<FileMetaData>){
        numberOfFiles = fileData.size
        fileData.forEach { fileMetaData -> determineName(fileMetaData) }
    }

    private fun determineName(fileMetaData: FileMetaData) {
        val fillerLength = numberOfFiles.toString().length
        var name = "${fileMetaData.dirName} - ${(fileMetaData.sequence?:0).toString().padStart(fillerLength,'0')}.${fileMetaData.file.extension}"
        fileMetaData.newName=name
    }
}