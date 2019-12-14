package nl.maas.filerenamer.extrapolation

class SequenceNumberExtrapolator: SequenceExtrapolator {

    override fun findSequenceForFiles(fileData: List<FileMetaData>) {
        fileData.forEach { fileMetaData -> determineSequencePositions(fileMetaData) }
        determineSequence(fileData)
        fileData.sortedBy { fileMetaData -> fileMetaData.sequence }
    }

    private fun determineSequence(fileData: List<FileMetaData>) {
        val min = determineSmallestNumber(fileData)
        val max = min + fileData.size-1
        val sequenceMap = HashMap<Int, List<FileMetaData>>()
        for (i in min..max) {
            val list = fileData.filter { fileMetaData -> fileMetaData.hasPotentialSequenceFor(i) }
            sequenceMap.put(i, list)
        }
        val failedToDetermine = sequenceMap.keys.filter { i -> sequenceMap.get(i)?.size ?: 0 != 1 }

        if (failedToDetermine.isNotEmpty()) {
            for(nr in failedToDetermine) {
                val options = sequenceMap[nr]
                if(options?.isEmpty()?:true){
                    if(nr >= min && nr <= max) println("Er is geen geldige optie gevonden voor volgnummer ${nr}. Dit nummer wordt genegeerd")
                    sequenceMap.remove(nr)
                }else {
                    println("Het bepalen van een bestand voor volgnummer ${nr}. Geef AUB aan welk bestand hier gebruikt mag worden:")
                    for (i in 0..options!!.size-1) {
                        println("${i}: ${options[i].name}")
                    }
                    print("Maak een keuze van 0 - ${options.size-1}: ")
                    sequenceMap.put(nr, listOf(options[readLine()!!.toInt()]))
                }
            }
        }

        sequenceMap.keys.forEach { i -> sequenceMap.get(i)?.firstOrNull()?.sequence = i.toString() }

    }

    private fun determineSmallestNumber(fileData: List<FileMetaData>): Int {
        val numbers = fileData.flatMap { fileMetaData -> fileMetaData.potentialSequenceNumbers?: listOf() }
        return numbers.find { nr -> numbers.containsAll((nr..nr+fileData.size-1).toList()) }?:0
    }

    private fun determineSequencePositions(fileData: FileMetaData) {
        fileData.potentialSequenceNumbers = fileData.extrapolatedName.map{ s-> if (s.toIntOrNull() != null) s.toInt() else null }.filterNotNull()
        if(fileData.potentialSequenceNumbers?.isEmpty()?:true){
            print("No sequence number found for ${fileData.name}. Please provide the sequencenumber for this file or (s)kip: ")
            fileData.potentialSequenceNumbers = listOf(readLine()!!.toIntOrNull()).filterNotNull()
        }
    }


}