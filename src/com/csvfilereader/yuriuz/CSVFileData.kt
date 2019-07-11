package com.csvfilereader.yuriuz

/**
 * Data class storing parsed CSV file data
 * Data stored as two dimension ArrayList
 * ArrayLits of strings per any row in CSV file. Any string is column value of the row
 * ArrayList of ArrayList of Strings is whole file
 * Because using arrays, first row and first column are starting from zero
 */

data class CSVFileData(var resultsList: ArrayList<ArrayList<String>>, var withHeaders: Boolean) {
    /**
     * Temporary ArrayList to store data of Integers
     */
    //
    var intArray: ArrayList<Int> = ArrayList()
        get() = field
        set(value) {
            field = value
        }

    /**
    * Temporary ArrayList to store data of Longs
    */
    var longArray: ArrayList<Long> = ArrayList()
        get() = field
        set(value) {
            field = value
        }

    /**
     * Temporary ArrayList to store data of Floats
     */
    var floatArray: ArrayList<Float> = ArrayList()
        get() = field
        set(value) {
            field = value
        }

    /**
     * Temporary ArrayList to store data of Doubles
     */
    var doubleArray: ArrayList<Double> = ArrayList()
        get() = field
        set(value) {
            field = value
        }

    /**
     * Supported Types
     */
    enum class Type {
        INTEGER, LONG, FLOAT, DOUBLE, STRING
    }

    operator fun Unit.component1() = Unit

    override fun toString(): String {
        return resultsList.toString()
    }

    /**
     * Get value in selected column from selected row
     */
    fun getItem(rowNum: Int, columnNum: Int): String {
        if (resultsList.size > rowNum) {
            var row = resultsList[rowNum]
            if (row.size > columnNum && columnNum >= 0) {
                return row[columnNum].trim()
            }
        }

        return ""
    }

    /**
     * Calculate average value of selected row
     */
    fun rowAverage(rowNum: Int): String {
        var start = 0
        if (withHeaders) {
            start = 1
        }

        if (resultsList.size > start) {
            val row = resultsList[rowNum]

            populateRowArray(rowNum, row)

            return getArrayAverage()
        } else {
            throw CSVFileException("Such column does not exist", null)
        }
    }

    /**
     * Calculate max value of selected row
     */
    fun rowMax(rowNum: Int): String {
        var start = 0
        if (!withHeaders) {
            start = 1
        }

        if (resultsList.size > start) {
            val row = resultsList[rowNum]

            populateRowArray(rowNum, row)

            return getArrayMax()
        } else {
            throw CSVFileException("Such column does not exist", null)
        }
    }

    /**
     * Calculate min value of selected row
     */
    fun rowMin(rowNum: Int): String {
        var start = 0
        if (!withHeaders) {
            start = 1
        }

        if (resultsList.size > start) {
            val row = resultsList[rowNum]

            populateRowArray(rowNum, row)

            return getArrayMin()
        } else {
            throw CSVFileException("Such column does not exist", null)
        }
    }

    /**
     * Calculate sum value of selected row
     */
    fun rowSum(rowNum: Int): String {
        var start = 0
        if (!withHeaders) {
            start = 1
        }

        if (resultsList.size > start) {
            val row = resultsList[rowNum]

            populateRowArray(rowNum, row)

            return getArraySum()
        } else {
            throw CSVFileException("Such column does not exist", null)
        }
    }

    /**
     * Calculate average value of selected column
     */
    fun columnAverage(columnNum: Int): String {
        if (resultsList.size > 0) {
            var row = resultsList[0]
            if (row.size > columnNum && columnNum >= 0) {
                populateColumnArray(columnNum)
                return getArrayAverage()
            } else {
                throw CSVFileException("Such column does not exist", null)
            }
        } else {
            throw CSVFileException("File is empty", null)
        }
    }

    /**
     * Calculate max value of selected column
     */
    fun columnMax(columnNum: Int): String {
        if (resultsList.size > 0) {
            var row = resultsList[0]
            if (row.size > columnNum && columnNum >= 0) {
                populateColumnArray(columnNum)
                return getArrayMax()
            } else {
                throw CSVFileException("Such column does not exist", null)
            }
        } else {
            throw CSVFileException("File is empty", null)
        }
    }

    /**
     * Calculate min value of selected column
     */
    fun columnMin(columnNum: Int): String {
        if (resultsList.size > 0) {
            var row = resultsList[0]
            if (row.size > columnNum && columnNum >= 0) {
                populateColumnArray(columnNum)
                return getArrayMin()
            } else {
                throw CSVFileException("Such column does not exist", null)
            }
        } else {
            throw CSVFileException("File is empty", null)
        }
    }

    /**
     * Calculate sum value of selected column
     */
    fun columnSum(columnNum: Int): String {
        if (resultsList.size > 0) {
            var row = resultsList[0]
            if (row.size > columnNum && columnNum >= 0) {
                populateColumnArray(columnNum)
                return getArraySum()
            } else {
                throw CSVFileException("Such column does not exist", null)
            }
        } else {
            throw CSVFileException("File is empty", null)
        }
    }

    /**
     * Populate array with the all values from selected row according to values type
     */
    private fun populateRowArray(rowNum: Int, row: ArrayList<String>) {
        clearDataArrays()

        var columnNum = 0
        for (column in row) {
            val cellValue = column.trim()
            val t = getValueType(cellValue)

            val (_) = when (t) {
                Type.INTEGER -> populateIntArray(cellValue.toInt())

                Type.LONG -> populateLongArray(cellValue.toLong())

                Type.FLOAT -> populateFloatArray(cellValue.toFloat())

                Type.DOUBLE -> populateDoubleArray(cellValue.toDouble())

                // There is no calculations for strings
                Type.STRING -> throw CSVFileException(
                    "Row $rowNum , Column $columnNum , value $cellValue does not numeric",
                    null
                )
            }

            columnNum++
        }
    }

    /**
     * Populate array with the all values from selected column according to values type
     */
    private fun populateColumnArray(columnNum: Int) {
        var start = 0
        if (withHeaders) {
            start = 1
        }

        /**
         * Remove previous calculation data from temporary arrays
         */
        clearDataArrays()

        for (i in start..resultsList.size - 1) {
            var cellValue = resultsList.get(i).get(columnNum)
            val t = getValueType(cellValue)

            val (_) = when (t) {
                Type.INTEGER -> populateIntArray(cellValue.toInt())

                Type.LONG -> populateLongArray(cellValue.toLong())

                Type.FLOAT -> populateFloatArray(cellValue.toFloat())

                Type.DOUBLE -> populateDoubleArray(cellValue.toDouble())

                // There is no calculations for strings
                Type.STRING -> throw CSVFileException(
                    "Row $i , Column $columnNum , value $cellValue does not numeric",
                    null
                )
            }
        }
    }

    /**
     * Calculate average value of previously populated array
     */
    private fun getArrayAverage(): String {
        if (intArray.size > 0) {
            return intArray.average().toString()
        }

        if (longArray.size > 0) {
            return longArray.average().toString()
        }

        if (floatArray.size > 0) {
            return floatArray.average().toString()
        }

        if (doubleArray.size > 0) {
            return doubleArray.average().toString()
        }

        return "0"
    }

    /**
     * Calculate sum value of previously populated array
     */
    private fun getArraySum(): String {
        if (intArray.size > 0) {
            return intArray.sum().toString()
        }

        if (longArray.size > 0) {
            return longArray.sum().toString()
        }

        if (floatArray.size > 0) {
            return floatArray.sum().toString()
        }

        if (doubleArray.size > 0) {
            return doubleArray.sum().toString()
        }

        return "0"
    }

    /**
     * Calculate min value of previously populated array
     */
    private fun getArrayMin(): String {
        if (intArray.size > 0) {
            intArray.sort()
            return intArray.get(0).toString()
        }

        if (longArray.size > 0) {
            longArray.sort()
            return longArray.get(0).toString()
        }

        if (floatArray.size > 0) {
            floatArray.sort()
            return floatArray.get(0).toString()
        }

        if (doubleArray.size > 0) {
            doubleArray.sort()
            return doubleArray.get(0).toString()
        }

        return "0"
    }

    /**
     * Calculate max value of previously populated array
     */
    private fun getArrayMax(): String {
        if (intArray.size > 0) {
            intArray.sortDescending()
            return intArray.get(0).toString()
        }

        if (longArray.size > 0) {
            longArray.sortDescending()
            return longArray.get(0).toString()
        }

        if (floatArray.size > 0) {
            floatArray.sortDescending()
            return floatArray.get(0).toString()
        }

        if (doubleArray.size > 0) {
            doubleArray.sortDescending()
            return doubleArray.get(0).toString()
        }

        return "0"
    }

    /**
     * Add value to array of Integers
     */
    private fun populateIntArray(cellValue: Int) {
        intArray.add(cellValue)
    }

    /**
     * Add value to array of Longs
     */
    private fun populateLongArray(cellValue: Long) {
        longArray.add(cellValue)
    }

    /**
     * Add value to array of Floats
     */
    private fun populateFloatArray(cellValue: Float) {
        floatArray.add(cellValue)
    }

    /**
     *  Add value to array of Doubles
     */
    private fun populateDoubleArray(cellValue: Double) {
        doubleArray.add(cellValue)
    }

    /**
     * Clear temporary arrays
     */
    private fun clearDataArrays() {
        intArray.clear()
        longArray.clear()
        floatArray.clear()
        doubleArray.clear()
    }

    /**
     * Get real type of data stored as string
     */
    private fun getValueType(value: String): Type {
        if (isInteger(value)) {
            return Type.INTEGER
        }

        if (isLong(value)) {
            return Type.LONG
        }

        if (isFloat(value)) {
            return Type.FLOAT
        }

        if (isDouble(value)) {
            return Type.DOUBLE
        }

        return Type.STRING
    }

    /**
     * Check if sent value is Integer
     */
    private fun isInteger(value: String): Boolean {
        try {
            value.toInt()
            return true
        } catch (e: NumberFormatException) {
            //
        }

        return false
    }

    /**
     * Check if sent value is Long
     */
    private fun isLong(value: String): Boolean {
        try {
            value.toLong()
            return true
        } catch (e: NumberFormatException) {
            //
        }

        return false
    }

    /**
     * Check if sent value is Float
     */
    private fun isFloat(value: String): Boolean {
        try {
            value.toFloat()
            return true
        } catch (e: NumberFormatException) {
            //
        }

        return false
    }

    /**
     * Check if sent value is Double
     */
    private fun isDouble(value: String): Boolean {
        try {
            value.toDouble()
            return true
        } catch (e: NumberFormatException) {
            //
        }

        return false
    }
}