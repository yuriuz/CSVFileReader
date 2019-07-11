package com.csvfilereader.yuriuz

import java.io.File
import java.io.InputStream

/**
 *  Main function includes some examples how to use CSVFile library
 *
 *
 */

fun main(args: Array<String>) {
    try {
        var emptyMap = mapOf(Pair("",""))

        /**
         * Send existing file to constructor
        */
        var file1 = File("biostats.txt")
        var csvFile1 = CSVFile(file1, true, emptyMap)

        /**
         * Send Input Stream to constructor
         */
        var file2 = File("data.txt")
        var stream: InputStream = file2.inputStream()
        var csvFileFromStream = CSVFile(stream, false, emptyMap)

        /**
         * Send File Path to constructor
         */
        val csvFileFromPath = CSVFile("ford_escort.txt", true, emptyMap)

        /**
         * Get parsed CSV file data from file
         */
        val dataFromFile: CSVFileData = csvFile1.parse()

        /**
         * Get parsed CSV file data from Stream
         */
        val dataFromStream: CSVFileData = csvFileFromStream.parse()

        /**
         * Send existing file to constructor
         */
        // Get parsed CSV file data from path
        val dataFromPath: CSVFileData = csvFileFromPath.parse()

        /**
         * Example of usage, parsing from file
         */
        println(dataFromFile.getItem(1, 1))
        println(dataFromFile.columnAverage(2))
        //println(dataFromFile.rowAverage(2))

        /**
         * Example of usage, parsing from stream
         */
        println(dataFromStream.rowMax(1))
        println(dataFromStream.rowMin(1))
        println(dataFromStream.columnMax(2))

        /**
         * Example of usage, parsing from path
         */
        println(dataFromPath.columnMin(1))
        println(dataFromPath.columnSum(2))
        println(dataFromPath.rowSum(4))

        /**
         * Main project exception, called from any other exception
         *
         * Send map object with complete missing cells instruction
         * Key is a column number that should be completed
         * Value is instruction for missing cell
         * possible values:
         *     empty
         *     row average
         *     column average
         *     row max
         *     column max
         *     row min
         *     column min
         *    constant value
         *
         */

        val map1 = mapOf(Pair("1","ROW_AW"))
        val map2 = mapOf(Pair("2","COL_AW"))
        val map3 = mapOf(Pair("0","ROW_MAX"))
        val map4 = mapOf(Pair("3","COL_MAX"))
        val map5 = mapOf(Pair("1","ROW_MIN"))
        val map6 = mapOf(Pair("2","COL_MIN"))
        val map7 = mapOf(Pair("1","0"))

        /**
         * Example of usage using instruction map
         */
        var csvFile = CSVFile("freshman_kgs.txt", true, map1)
        //println(csvFile.parse())

        csvFile = CSVFile("freshman_kgs.txt", true, map2)
        //println(csvFile.parse())

        csvFile = CSVFile("freshman_kgs.txt", true, map3)
        //println(csvFile.parse())

        csvFile = CSVFile("freshman_kgs.txt", true, map4)
        //println(csvFile.parse())

        csvFile = CSVFile("freshman_kgs.txt", true, map5)
        //println(csvFile.parse())

        csvFile = CSVFile("freshman_kgs.txt", true, map6)
        //println(csvFile.parse())

        csvFile = CSVFile("freshman_kgs.txt", true, map7)
        println(csvFile.parse())
    } catch (e: CSVFileException) {
        println(e.message)
    }

}