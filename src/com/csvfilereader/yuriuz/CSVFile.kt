package com.csvfilereader.yuriuz

import java.io.*
import java.util.*

/**
 * Representation of comma separated file
 *
 * Main class performing parsing and missing data completion of any Comma Separated Values file
 * May work with three different data sources:
 *      FilePath
 *      File
 *      InputStream
 *
 * @property csvFilePath the CSV file path
 * @property csvFile CSV file name
 * @property csvFileInputStream CSV file Input Stream
 * @property withHeaders flag, meaning if selected CSV file has a header as first row
 * @property instructionMap Map defining set of instructions how to complete
 *                             missing data in specific row
 *
 * @constructor Constructor for FilePath data source.
 * @constructor Constructor for File data source.
 * @constructor Constructor for InputStream data source.
 */

class CSVFile() {
    private var csvFilePath: String = ""
    private var csvFile: File? = null
    private var csvFileInputStream: InputStream? = null
    private var withHeaders: Boolean = false
    private var instructionMap: Map<String, String>? = null

    /**
     * Constructor for FilePath data source     *
     */
    constructor(filePath: String, withHeaders: Boolean, map: Map<String, String>) : this() {
        this.withHeaders = withHeaders
        this.instructionMap = map

        val csvFile = File(filePath)
        if (!csvFile.exists()) {
            throw CSVFileException("File does not exists", null)
        } else {
            csvFilePath = filePath
        }
    }

    /**
     * Constructor for File data source
     *
     * @exception <CSVFileException>
     */
    constructor(csvFile: File, withHeaders: Boolean, map: Map<String, String>) : this() {
        this.withHeaders = withHeaders
        this.instructionMap = map

        if (!csvFile.exists()) {
            throw CSVFileException("File does not exists", null)
        } else {
            this.csvFile = csvFile;
        }
    }

    /**
     * Constructor for InputStream data source
     */
    constructor(stream: InputStream, withHeaders: Boolean, map: Map<String, String>?) : this() {
        this.withHeaders = withHeaders
        this.csvFileInputStream = stream
        this.instructionMap = map
    }

    /**
     * Create CSVFileData file from Buffer Reader
     * @return parsed CSVFileData
     * @throws <IOException>, @exception <CSVFileException>
     */
    private fun parseFromReaderBuffer(fileReader: BufferedReader?): CSVFileData {
        val resultsList: ArrayList<ArrayList<String>> = ArrayList()

        if (fileReader != null) {
            try {
                var line: String?

                // read data from buffer
                line = fileReader.readLine()
                while (line != null && line.trim().length > 0) {
                    val row: ArrayList<String> = ArrayList()

                    // parse data separated by colon to ArrayList
                    val tokens = line.split(",")
                    if (tokens.size > 0) {
                        var index: Int = 0
                        for (token in tokens) {
                            row.add(token)
                            index++
                        }

                        resultsList.add(row)
                    }

                    line = fileReader.readLine()
                }
            } catch (e: IOException) {
                throw CSVFileException("Reading CS Error!", e.cause)
            } finally {
                try {
                    fileReader!!.close()
                } catch (e: IOException) {
                    throw CSVFileException("Closing fileReader Error!", e.cause)
                }

                return completeFileWithInstructionMap(CSVFileData(resultsList, withHeaders))
            }
        } else {
            throw CSVFileException("Empty Buffer Reader", null)
        }
    }

    /**
     * Complete missing data according to instructions in Map
     * @param csvFileData file data with some missing values
     * @return Completed CSVFileData
     * @throws <IOException>, @exception <CSVFileException>
     */
    private fun completeFileWithInstructionMap(csvFileData: CSVFileData): CSVFileData {
        if (csvFileData.resultsList.size > 0) {
            var maxColumnsInRow: Int = 0
            var temp: Int

            // Get maximal columns count from the all file rows
            for (row in csvFileData.resultsList) {
                if (maxColumnsInRow == 0) {
                    maxColumnsInRow = row.size
                    continue;
                }

                temp = row.size
                if (temp > maxColumnsInRow) {
                    maxColumnsInRow = temp
                }
            }

            var rowNum: Int = 0
            // find cell with missing value
            for (row in csvFileData.resultsList) {
                val columnsInCurrRow = row.size
                if (maxColumnsInRow > columnsInCurrRow) {
                    val keys: Set<String> = instructionMap!!.keys

                    if (keys.size > 0) {
                        val key = keys.elementAt(0).toString()

                        // Get required coulmn number according to map key
                        val columnNumber = Integer.parseInt(key)
                        val operation = instructionMap!!.get(key)

                        // Select required operation according to map value
                        when (operation) {
                            // Set missing data as current row average value
                            "ROW_AW" -> row.add(columnNumber, csvFileData.rowAverage(rowNum))

                            // Set missing data as current column average value
                            "COL_AW" -> row.add(columnNumber, csvFileData.columnAverage(columnNumber))

                            // Set missing data as current row max value
                            "ROW_MAX" -> row.add(columnNumber, csvFileData.rowMax(rowNum))

                            // Set missing data as current column max value
                            "COL_MAX" -> row.add(columnNumber, csvFileData.columnMax(columnNumber))

                            // Set missing data as current row min value
                            "ROW_MIN" -> row.add(columnNumber, csvFileData.rowMin(rowNum))

                            // Set missing data as current column min value
                            "COL_MIN" -> row.add(columnNumber, csvFileData.columnMin(columnNumber))

                            // Set missing data as constant value
                            else -> row.add(columnNumber, operation ?: "")
                        }
                    }
                }

                rowNum++
            }
        }

        return csvFileData
    }

    /**
     * Main public method
     * Creating BufferedReader from selected data source and calling to parser
     *
     * @return Parsed and completed CSVFileData
     */

    fun parse(): CSVFileData {
        if (!csvFilePath.isEmpty()) {
            val fileReader: BufferedReader? = BufferedReader(FileReader(csvFilePath))
            return parseFromReaderBuffer(fileReader)
        } else if (csvFile != null) {
            if (csvFile is File) {
                val file: File = csvFile as File
                return parseFromReaderBuffer(file.bufferedReader())
            }
        } else if (csvFileInputStream != null) {
            val stream: InputStream = csvFileInputStream as InputStream
            return parseFromReaderBuffer(stream.bufferedReader())
        }

        return CSVFileData(ArrayList(), withHeaders)
    }
}

