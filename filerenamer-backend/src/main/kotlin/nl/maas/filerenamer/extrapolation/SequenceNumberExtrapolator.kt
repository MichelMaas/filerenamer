package nl.maas.filerenamer.extrapolation

import nl.maas.filerenamer.domain.ExtrapolationFailure
import nl.maas.filerenamer.domain.ExtrapolationFailures
import nl.maas.filerenamer.domain.ExtrapolationResult
import nl.maas.filerenamer.domain.Warning

class SequenceNumberExtrapolator : SequenceExtrapolator {

    override fun findSequenceForFiles(fileData: List<FileMetaData>): ExtrapolationResult {
        val result = ExtrapolationResult(fileData)
        fileData.forEach { fileMetaData -> determineSequencePositions(result) }
        return determineSequence(result)
    }

    private fun determineSequence(result: ExtrapolationResult): ExtrapolationResult {
        val fileData = result.files
        val min = determineSequenceNumbers(fileData, "MIN")
        val max = determineSequenceNumbers(fileData, "MAX")
        val sequenceMap = HashMap<Int, List<FileMetaData>>()
        var extrapolationFailures = ExtrapolationFailures();
        val warnings = ArrayList<Warning>();
        for (i in min..max) {
            val list = fileData.filter { fileMetaData -> fileMetaData.hasPotentialSequenceFor(i) }
            sequenceMap.put(i, list)
        }
        val failedToDetermine = sequenceMap.keys.filter { i -> sequenceMap.get(i)?.size ?: 0 != 1 }

        if (failedToDetermine.isNotEmpty()) {
            for (nr in failedToDetermine) {
                val options = sequenceMap[nr]
                if (options?.isEmpty() ?: true) {
                    if (nr >= min && nr <= max) {
                        val message =
                            "Er is geen geldige optie gevonden voor volgnummer ${nr}. Dit nummer wordt genegeerd"
                        sequenceMap[min]?.let { opt ->
                            opt[0]?.let {
                                warnings.add(Warning(it.dirName, message))
                            }
                        }
                        println(message)
                    }
                    sequenceMap.remove(nr)
                } else {
                    val message =
                        "Het bepalen van een bestand voor volgnummer ${nr}."
                    println(message)
                    var extrapolationFailure =
                        ExtrapolationFailure(ExtrapolationFailures.FailureType.TooMany, nr, options!!)
                    extrapolationFailures.add(sequenceMap[min]!![0].dirName, extrapolationFailure)
                }
            }
        }

        sequenceMap.keys.forEach { i -> sequenceMap.get(i)?.firstOrNull()?.sequence = i.toString() }
        result.failures.addAll<Int>(extrapolationFailures.getTypedFailures())
        return result
    }

    private fun determineSequenceNumbers(fileData: List<FileMetaData>, edgeNumber: String): Int {
        var numbers = fileData.flatMap { fileMetaData ->
            fileMetaData.potentialSequenceNumbers.map { it.toIntOrNull() }.filterNotNull()
        }
        val lowest = numbers.sorted().get(0)
        val lastValid = numbers.sorted()
            .reduceIndexed { index, previous, current -> if (current - previous > 10) previous else current }
        val range = lowest..lastValid
        val notPresent = range.filterNot { nr -> numbers.contains(nr) }

        return if (edgeNumber.equals("MIN")) lowest else lastValid
    }

    private fun determineSequencePositions(result: ExtrapolationResult): ExtrapolationResult {
        val files = result.files
        files.forEach { fileData ->
            fileData.potentialSequenceNumbers =
                fileData.extrapolatedName.map { it.toIntOrNull() }.filterNotNull().map { it.toString() }.toMutableSet()
            if (fileData.potentialSequenceNumbers.isEmpty()) {
//                print("No sequence number found for ${fileData.name}. Please provide the sequencenumber for this file or (s)kip: ")
//                fileData.potentialSequenceNumbers = listOf(readLine()!!.toIntOrNull()).filterNotNull()
                result.failures.add(
                    fileData.dirName,
                    ExtrapolationFailure(ExtrapolationFailures.FailureType.NoNumberForFile, "", fileData)
                )
            }
        }
        return result
    }


}