package nl.maas.filerenamer.extrapolation

import java.io.File

class SequenceNumberExtrapolator {

    fun findSequenceForFiles(fileData: List<FileMetaData>) {
        fileData.forEach { fileMetaData -> determineSequencePositions(fileMetaData) }
        determineSequence(fileData)
        fileData.sortedBy { fileMetaData -> fileMetaData.sequence }
    }

    private fun determineSequence(fileData: List<FileMetaData>) {
        val min = 1
        val max = fileData.size
        val sequenceMap = HashMap<Int, List<FileMetaData>>()
        for (i in min..max) {
            val list = fileData.filter { fileMetaData -> fileMetaData.hasPotentialSequenceIndexFor(i) }
            sequenceMap.put(i, list)
        }
        val failedToDetermine = sequenceMap.keys.filter { i -> sequenceMap.get(i)?.size ?: 0 != 1 }

        if(failedToDetermine.isNotEmpty()) {
            //TODO: Request feedback
        }

        sequenceMap.keys.forEach{i -> sequenceMap.get(i)!!.get(0)!!.sequence = i}

    }

    private fun determineSequencePositions(fileData: FileMetaData) {
        fileData.potentialSequenceNumberIndexes = fileData.extrapolatedName.mapIndexed { index, s -> if (s.toIntOrNull() != null) index else null }.filterNotNull()
    }


}