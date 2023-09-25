@file:Suppress("SameParameterValue")

package eu.sirotin.kotlin.error

import kotlin.math.sign
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val SUCCESS = "Success"
private const val FAILURE = "Failure"

internal class ErrorTest {

    private var actionWithValueExecuted = false
    private var actionWithErrorExecuted = false

    @BeforeTest
    fun setUp(){
        actionWithValueExecuted = false
        actionWithErrorExecuted = false
    }

    /**
     * Test case for creating a parameterless Error object.
     *
     * This test verifies that creating an Error object without specifying a message or cause
     * results in an Error instance with default properties set.
     *
     * - It checks that the error message is null.
     * - It checks that the cause is null.
     * - It checks that the localized message is null.
     * - It verifies that the stack trace contains the expected class name.
     * - It asserts that the result of calling toString() on the Error matches the expected class name.
     */
    @Test
    fun `Parameterless Error object`() {
        // Create an Error object without specifying a message or cause.
        val error = Error()

        // Check that the error message is null.
        assertNull(error.message)

        // Check that the cause is null.
        assertNull(error.cause)

        // Check that the localized message is null.
        assertNull(error.localizedMessage)

        // Verify that the stack trace contains the expected class name.
        assertTrue(error.stackTrace[0].toString().startsWith("eu.sirotin.kotlin.error.ErrorTest.Parameterless"))

        // Assert that the result of calling toString() on the Error matches the expected class name.
        assertEquals("java.lang.Error", error.toString())
    }

    /**
     * Test case for creating an Error object with a cause.
     *
     * This test verifies that creating an Error object with a specified message and cause
     * results in an Error instance with the cause property set to the provided exception.
     *
     * - It creates an exception using `runCatching` for demonstration purposes.
     * - It creates an Error object with a specified message and the previously created exception as the cause.
     * - It asserts that the cause of the Error matches the provided exception.
     */
    @Test
    fun `Error object with cause`() {
        // Create an exception using `runCatching` for demonstration purposes.
        val exception = runCatching { "1.2".toInt() }.exceptionOrNull()

        // Create an Error object with a specified message and the previously created exception as the cause.
        val error = Error("An error occurs. See details in capture", exception)

        // Assert that the cause of the Error matches the provided exception.
        assertEquals(exception, error.cause)
    }

    /**
     * Converts the current string to an integer safely, handling exceptions naively.
     *
     * This function attempts to convert the string to an integer using the [toInt] method. If successful,
     * it returns the integer as a [Result.success]. If an exception occurs during the conversion,
     * it returns a [Result.failure] containing the caught exception.
     *
     * @return A [Result] containing the integer value if the conversion is successful, or an error result
     * if an exception occurs.
     */
    private fun String.toIntSafeNaive(): Result<Int>  {
        return try {
            // Attempt to convert the string to an integer.
            val x = this.toInt()
            // Return a success result with the integer value.
            Result.success(x)
        } catch (e: Exception) {
            // Return a failure result with the caught exception.
            Result.failure(e)
        }
    }

    /**
     * Converts the current string to an integer safely using [runCatching].
     *
     * This function safely attempts to convert the string to an integer using [runCatching].
     * It returns a [Result.success] containing the integer value if the conversion is successful,
     * or a [Result.failure] containing the caught exception if an exception occurs.
     *
     * @return A [Result] containing the integer value if the conversion is successful, or an error result
     * if an exception occurs.
     */
    private fun String.toIntSafe(): Result<Int>  = runCatching { this.toInt() }

    /**
     * Test case for comparing two realizations of the `toIntSafe` function.
     *
     * This test compares the behavior of two different realizations of the `toIntSafe` function,
     * one using the `toIntSafeNaive` implementation and the other using the standard `toIntSafe` function.
     * It validates their results in terms of success, failure, and exception messages.
     *
     * - It obtains two results for successful conversions from "21" to an integer, one from each implementation.
     * - It compares the integer values obtained from both results.
     * - It verifies that both results indicate success.
     * - It checks that both results do not indicate failure.
     * - It obtains two results for failed conversions from "21.1" to an integer, one from each implementation.
     * - It compares the exception messages obtained from both results.
     * - It verifies that both results indicate failure.
     * - It checks that both results do not indicate success.
     */
    @Test
    fun `Compare realizations of Error toIntSafe`() {
        // Obtain two results for successful conversions from "21" to an integer.
        val resultSuccess1 = "21".toIntSafeNaive()
        val resultSuccess2 = "21".toIntSafe()

        // Compare the integer values obtained from both results.
        assertEquals(resultSuccess1.getOrNull(), resultSuccess2.getOrNull())

        // Verify that both results indicate success.
        assertTrue(resultSuccess1.isSuccess)
        assertTrue(resultSuccess2.isSuccess)

        // Check that both results do not indicate failure.
        assertFalse(resultSuccess1.isFailure)
        assertFalse(resultSuccess2.isFailure)

        // Check toString()
        assertEquals("Success(21)", resultSuccess1.toString())

        // Obtain two results for failed conversions from "21.1" to an integer.
        val resultFailure1 = "21.1".toIntSafeNaive()
        val resultFailure2 = "21.1".toIntSafe()

        // Compare the exception messages obtained from both results.
        assertEquals(resultFailure1.exceptionOrNull()?.message, resultFailure2.exceptionOrNull()?.message)

        // Verify that both results indicate failure.
        assertTrue(resultFailure1.isFailure)
        assertTrue(resultFailure2.isFailure)

        // Check that both results do not indicate success.
        assertFalse(resultFailure1.isSuccess)
        assertFalse(resultFailure2.isSuccess)

        // Check toString()
        assertEquals("Failure(java.lang.NumberFormatException: For input string: \"21.1\")", resultFailure1.toString())
    }

    /**
     * Test case for using the `getOrDefault` function with `toIntSafe`.
     *
     * This test demonstrates the usage of the `getOrDefault` function with the `toIntSafe` function to handle
     * default values in case of conversion failure.
     *
     * - It obtains a result for a successful conversion from "21" to an integer using `toIntSafe`.
     * - It uses `getOrDefault` to retrieve the value from the result and provide a default value (12).
     * - It verifies that the obtained value matches the expected integer (21).
     * - It obtains a result for a failed conversion from "21.3" to an integer using `toIntSafe`.
     * - It uses `getOrDefault` to retrieve the value from the result and provide a default value (12).
     * - It verifies that the obtained value matches the provided default value (12).
     */
    @Test
    fun `Using getOrDefault`() {
        // Obtain a result for a successful conversion from "21" to an integer using `toIntSafe`.
        val result1 = "21".toIntSafe()

        // Use `getOrDefault` to retrieve the value and provide a default value (12).
        val resultValue1 = result1.getOrDefault(12)

        // Verify that the obtained value matches the expected integer (21).
        assertEquals(21, resultValue1)

        // Obtain a result for a failed conversion from "21.3" to an integer using `toIntSafe`.
        val result2 = "21.3".toIntSafe()

        // Use `getOrDefault` to retrieve the value and provide a default value (12).
        val resultValue2 = result2.getOrDefault(12)

        // Verify that the obtained value matches the provided default value (12).
        assertEquals(12, resultValue2)
    }


    /**
     * Returns an error message based on the provided exception [exception].
     *
     * @param exception The exception that occurred during the operation.
     * @return An error message based on the provided exception [exception].
     */
    private fun getFalseValue(exception: Throwable): String {
        //Expected format here: 'For input string: "1.1"'
        val arr = exception.message!!.split("\"")
        return "False format by ${arr[1]}"
    }

    /**
     * Adds two strings representing integers and returns the result as a string.
     *
     * @param a The first input string.
     * @param b The second input string.
     * @return A string representing the sum of the integer values in [a] and [b], or an error message
     * if the conversion or addition fails.
     */
    private fun addAsString(a: String, b: String): String {
        return runCatching {
            "${a.toInt() + b.toInt()}"}
            .getOrElse { e->getFalseValue(e) }
    }

    /**
     * Test case for using the `getOrElse` function with the `addAsString` function.
     *
     * This test demonstrates the usage of the `getOrElse` function with the `addAsString` function
     * to handle default values or alternative results.
     *
     * - It calls `addAsString` with valid inputs "1" and "2" and checks that the result is "3".
     * - It calls `addAsString` with an invalid input "1" and "1.1" and checks that the result is "False format by 1.1".
     */
    @Test
    fun `Using getOrElse`() {
        // Call `addAsString` with valid inputs "1" and "2" and check that the result is "3".
        val result1 = addAsString("1", "2")
        assertEquals("3", result1)

        // Call `addAsString` with an invalid input "1" and "1.1" and check that the result is "False format by 1.1".
        val result2 = addAsString("1", "1.1")
        assertEquals("False format by 1.1", result2)
    }

    /**
     * Test case for using the `getOrThrow` function with the `toIntSafe` function.
     *
     * This test demonstrates the usage of the `getOrThrow` function with the `toIntSafe` function
     * to retrieve values or throw exceptions when working with results.
     *
     * - It uses `runCatching` to call `toIntSafe` with an invalid input "1.2" and attempts to retrieve the value.
     * - It asserts that an exception of type `NumberFormatException` is thrown.
     * - It uses `kotlin.runCatching` to call `toIntSafe` with a valid input "12" and attempts to retrieve the value.
     * - It asserts that no exception is thrown, indicating a successful result.
     */
    @Test
    fun `Using getOrThrow`() {
        // Use `runCatching` to call `toIntSafe` with an invalid input "1.2" and attempt to retrieve the value.
        val result1 = runCatching { "1.2".toIntSafe().getOrThrow() }.exceptionOrNull()

        // Assert that an exception of type `NumberFormatException` is thrown.
        assertNotNull(result1)
        assertIs<NumberFormatException>(result1)

        // Use `kotlin.runCatching` to call `toIntSafe` with a valid input "12" and attempt to retrieve the value.
        val result2 = kotlin.runCatching { "12".toIntSafe().getOrThrow() }.exceptionOrNull()

        // Assert that no exception is thrown, indicating a successful result.
        assertNull(result2)
    }

    /**
     * Increases an integer value obtained from a string by 1.
     *
     * This function takes a string input [x], attempts to convert it to an integer using [toIntSafe],
     * and then increases the resulting integer value by 1 using the `map` function from the [Result] class.
     * The final result is a [Result] containing the incremented integer value.
     *
     * @param x The string representing an integer.
     * @return A [Result] containing the incremented integer value or an error result if the conversion fails.
     */
    private fun increase(x: String): Result<Int> {
        return x.toIntSafe()
            .map { it + 1 }
    }

    /**
     * Test case for using the `map` function with the `increase` function.
     *
     * This test demonstrates the usage of the `map` function to increment an integer value obtained from a string.
     *
     * - It calls the `increase` function with the valid input "112" and retrieves the result.
     * - It asserts that the result is 113, indicating a successful increment.
     * - It calls the `increase` function with an invalid input "112.9" and attempts to retrieve the exception.
     * - It asserts that an exception of type `NumberFormatException` is thrown, indicating a failed conversion.
     */
    @Test
    fun `Using map`() {
        // Call the `increase` function with the valid input "112" and retrieve the result.
        val result1 = increase("112").getOrNull()!!

        // Assert that the result is 113, indicating a successful increment.
        assertEquals(113, result1)

        // Call the `increase` function with an invalid input "112.9" and attempt to retrieve the exception.
        val result2 = increase("112.9").exceptionOrNull()!!

        // Assert that an exception of type `NumberFormatException` is thrown, indicating a failed conversion.
        assertNotNull(result2)
        assertIs<NumberFormatException>(result2)
    }


    /**
     * Adds two integers obtained from strings and returns the result as a [Result].
     *
     * This function takes two string inputs [x] and [y], attempts to convert them to integers using [toIntSafe],
     * and then adds the resulting integers. The operation is performed using the `mapCatching` function from the [Result] class.
     * The final result is a [Result] containing the sum of the integers or an error result if the conversion or addition fails.
     *
     * @param x The first string representing an integer.
     * @param y The second string representing an integer.
     * @return A [Result] containing the sum of the integers or an error result if the conversion or addition fails.
     */
    private fun add(x: String, y: String): Result<Int> {
        return x.toIntSafe()
            .mapCatching { it + y.toInt() }
    }


    /**
     * Test case for using the `mapCatching` function with the `add` function.
     *
     * This test demonstrates the usage of the `mapCatching` function to add two integers obtained from strings.
     *
     * - It calls the `add` function with valid inputs "112" and "38" and retrieves the result.
     * - It asserts that the result is 150, indicating a successful addition.
     * - It calls the `add` function with invalid inputs "112.9" and "38" and attempts to retrieve the exception.
     * - It asserts that an exception of type `NumberFormatException` is thrown, and the error message contains "112.9".
     * - It calls the `add` function with valid inputs "112" and "38.5" and attempts to retrieve the exception.
     * - It asserts that an exception of type `NumberFormatException` is thrown, and the error message contains "38.5".
     */
    @Test
    fun `Using mapCatching`() {
        // Call the `add` function with valid inputs "112" and "38" and retrieve the result.
        val result1 = add("112", "38").getOrNull()!!

        // Assert that the result is 150, indicating a successful addition.
        assertEquals(150, result1)

        // Call the `add` function with invalid inputs "112.9" and "38" and attempt to retrieve the exception.
        val result2 = add("112.9", "38").exceptionOrNull()!!

        // Assert that an exception of type `NumberFormatException` is thrown, and the error message contains "112.9".
        assertNotNull(result2)
        assertIs<NumberFormatException>(result2)
        assertTrue(result2.message!!.contains("112.9"))

        // Call the `add` function with valid inputs "112" and "38.5" and attempt to retrieve the exception.
        val result3 = add("112", "38.5").exceptionOrNull()!!

        // Assert that an exception of type `NumberFormatException` is thrown, and the error message contains "38.5".
        assertNotNull(result3)
        assertIs<NumberFormatException>(result3)
        assertTrue(result3.message!!.contains("38.5"))
    }

    /**
     * Test case for using the `recover` function with the `toIntSafe` function.
     *
     * This test demonstrates the usage of the `recover` function to handle exceptions when working with results.
     *
     * - It uses `toIntSafe` to convert "-15" to an integer and then uses `recover` to retrieve the result or
     *   the stack trace size in case of an exception. It asserts that the result is -15.
     * - It uses `toIntSafe` to convert "-15.1" to an integer and then uses `recover` to retrieve the result or
     *   the stack trace size in case of an exception. It asserts that the stack trace size is greater than 10.
     */
    @Test
    fun `Using recover`() {
        // Use `toIntSafe` to convert "-15" to an integer and use `recover` to retrieve the result or the stack trace size.
        val result1 = "-15".toIntSafe().recover { exception -> exception.stackTrace.size }

        // Assert that the result is -15.
        assertEquals(-15, result1.getOrNull())

        // Use `toIntSafe` to convert "-15.1" to an integer and use `recover` to retrieve the result or the stack trace size.
        val result2 = "-15.1".toIntSafe().recover { exception -> exception.stackTrace.size }

        // Assert that the stack trace size is greater than 10.
        assertTrue(result2.getOrNull()!! > 10)
    }


    /**
     * Test case for using the `recoverCatching` function with the `toIntSafe` function.
     *
     * This test demonstrates the usage of the `recoverCatching` function to handle exceptions when working with results.
     *
     * - It uses `runCatching` to call `toIntSafe` with "-15.1" and then uses `recover` to convert "0.0" to an integer
     *   or retrieve an exception in case of a failure. It asserts that an exception of type `NumberFormatException` is thrown,
     *   and the error message contains "0.0".
     * - It directly uses `recoverCatching` with "-15.1" and converts "0.0" to an integer. It asserts that an exception
     *   of type `NumberFormatException` is thrown, and the error message contains "0.0".
     * - It directly uses `recoverCatching` with "-15" and converts "0.0" to an integer. It asserts that the result is -15.
     * - It directly uses `recoverCatching` with "-15.1" and converts "2" to an integer. It asserts that the result is 2.
     */
    @Test
    fun `Using recoverCatching`() {
        // Use `runCatching` to call `toIntSafe` with "-15.1" and use `recover` to convert "0.0" to an integer or retrieve an exception.
        val result1 = runCatching { "-15.1".toIntSafe().recover { "0.0".toInt() }}.exceptionOrNull()

        // Assert that an exception of type `NumberFormatException` is thrown, and the error message contains "0.0".
        assertNotNull(result1)
        assertIs<NumberFormatException>(result1)
        assertTrue(result1.message!!.contains("0.0"))

        // Directly use `recoverCatching` with "-15.1" and convert "0.0" to an integer.
        val result2 =  "-15.1".toIntSafe().recoverCatching { "0.0".toInt() }.exceptionOrNull()

        // Assert that an exception of type `NumberFormatException` is thrown, and the error message contains "0.0".
        assertNotNull(result2)
        assertIs<NumberFormatException>(result2)
        assertTrue(result2.message!!.contains("0.0"))

        // Directly use `recoverCatching` with "-15" and convert "0.0" to an integer.
        val result3 =  "-15".toIntSafe().recoverCatching { "0.0".toInt() }.getOrNull()

        // Assert that the result is -15.
        assertEquals(-15, result3)

        // Directly use `recoverCatching` with "-15.1" and convert "2" to an integer.
        val result4 =  "-15.1".toIntSafe().recoverCatching { "2".toInt() }.getOrNull()

        // Assert that the result is 2.
        assertEquals(2, result4)
    }

    /**
     * Adds two strings representing integers and returns the result as a string.
     *
     * @param a The first input string.
     * @param b The second input string.
     * @return A string representing the sum of the integer values in [a] and [b], or an error message
     * if the conversion or addition fails.
     */
    private fun addAsString1(a: String, b: String): String {
        return runCatching {
            "${a.toInt() + b.toInt()}"   }
            .fold(
                {"Result: $it"},
                {getFalseValue(it)}
            )
    }

    /**
     * Adds two strings representing integers and returns the result as a string.
     *
     * @param a The first input string.
     * @param b The second input string.
     * @return A string representing the sum of the integer values in [a] and [b], or an error message
     * if the conversion or addition fails.
     */
    private fun addAsString2(a: String, b: String): String {
        return runCatching {
            "${a.toInt() + b.toInt()}"   }
            .fold(
                onSuccess = {"Result: $it"},
                onFailure = {getFalseValue(it)}
            )
    }

    /**
     * Test case for using the `fold` function with two different implementations of `addAsString`.
     *
     * This test demonstrates the usage of the `fold` function to obtain results from two different implementations
     * of the `addAsString` function and verifies their correctness.
     *
     * - It calls `addAsString1` with valid inputs "1" and "4" and checks that the result is "Result: 5".
     * - It calls `addAsString2` with the same valid inputs and verifies that the result is also "Result: 5".
     * - It calls `addAsString1` with an invalid input "1.3" and "4" and checks that the result is "False format by 1.3".
     * - It calls `addAsString2` with the same invalid inputs and verifies that the result is also "False format by 1.3".
     */
    @Test
    fun `Using fold`() {
        // Call `addAsString1` with valid inputs "1" and "4" and check that the result is "Result: 5".
        val result1 = addAsString1("1", "4")
        assertEquals("Result: 5", result1)

        // Call `addAsString2` with the same valid inputs and verify that the result is also "Result: 5".
        val result2 = addAsString2("1", "4")
        assertEquals("Result: 5", result2)

        // Call `addAsString1` with an invalid input "1.3" and "4" and check that the result is "False format by 1.3".
        val result3 = addAsString1("1.3", "4")
        assertEquals("False format by 1.3", result3)

        // Call `addAsString2` with the same invalid inputs and verify that the result is also "False format by 1.3".
        val result4 = addAsString2("1.3", "4")
        assertEquals("False format by 1.3", result4)
    }

    /**
     * Test case for using the `onSuccess` and `onFailure` functions with `toIntSafe`.
     *
     * This test demonstrates the usage of the `onSuccess` and `onFailure` functions with the `toIntSafe` function
     * to handle success and failure cases and update a result variable.
     *
     * - It uses `toIntSafe` to convert "3.5" to an integer and uses `onFailure` to set the result to "FAILURE".
     * - It asserts that the result variable is "FAILURE" since the conversion fails.
     * - It uses `toIntSafe` to convert "3.5" to an integer and uses `onSuccess` to set the result to "SUCCESS".
     * - It again uses `onFailure`, but this time after `onSuccess`, and asserts that the result remains "FAILURE"
     * - It uses `toIntSafe` to convert "35" to an integer and uses `onFailure` to set the result to "FAILURE".
     * - It asserts that the result variable is "SUCCESS" since the conversion is successful and `onFailure` is not called.
     * - It uses `toIntSafe` to convert "35" to an integer and uses `onSuccess` to set the result to "SUCCESS".
     * - It again uses `onFailure`, but this time after `onSuccess`, and asserts that the result remains "SUCCESS"
     *   since `onFailure` will be not called.
     */
    @Test
    fun `Using onSuccess and onFailure`() {
        // Initialize the result variable.
        var result = ""

        // Use `toIntSafe` to convert "3.5" to an integer and use `onFailure` to set the result to "FAILURE".
        "3.5".toIntSafe()
            .onFailure { result = FAILURE }

        // Assert that the result variable is "FAILURE" since the conversion fails.
        assertEquals(FAILURE, result)

        // Use `toIntSafe` to convert "3.5" to an integer and use `onSuccess` to set the result to "SUCCESS".
        //  but it should pass.
        "3.5".toIntSafe()
            .onSuccess { result = SUCCESS }
            .onFailure { result = FAILURE }

        // Assert that the result variable remains "FAILURE" `.
        assertEquals(FAILURE, result)

        result = SUCCESS
        // Use `toIntSafe` to convert "35" to an integer and use `onFailure` to set the result to "FAILURE".
        "35".toIntSafe()
            .onFailure { result = FAILURE }

        // Assert that the result variable is "SUCCESS" since the conversion is successful and the action
        // of `onFailure` is not called.
        assertEquals(SUCCESS, result)

        // Use `toIntSafe` to convert "35" to an integer and use `onSuccess` to set the result to "SUCCESS".
        // Then, use `onFailure`, but it should not override the previous `onSuccess` value.
        "35".toIntSafe()
            .onSuccess { result = SUCCESS }
            .onFailure { result = FAILURE }

        // Assert that the result variable remains "SUCCESS" since `onFailure` does not override the previous `onSuccess`
        //because it action was not called
        assertEquals(SUCCESS, result)
    }

    /**
     * Processes an integer value represented as a string, following the strategy of
     * "make something by success and throw by failure."
     *
     * @param value The string representation of an integer value.
     * @return A Throwable instance if there is a conversion failure, otherwise null.
     */
    private fun processIntValue(value: String): Throwable? =
        value.toIntSafe()
            .onSuccess { someActionWithValue(it) }
            .exceptionOrNull()
    /**
    * Test case to demonstrate processing an integer value as a string and handling success and failure.
    */
    @Test
    fun `Using pseudo result-less`() {

        // When "25" is processed, it should result in a null Throwable, indicating success.
        assertNull(processIntValue("25"))
        assertTrue(actionWithValueExecuted)

        // Reset the action flag for the next test.
        actionWithValueExecuted = false

        // When "25.9" is processed, it should result in a non-null Throwable, indicating a failure.
        assertNotNull(processIntValue("25.9"))
        assertFalse(actionWithValueExecuted)
    }


    /**
     * Calculates the expression ax^2 + bx + c for given values of x, a, b, and c.
     * This function employs the strategy of "stopping normal processing by the first failure in the chain."
     *
     * @param x The value of 'x' in the equation.
     * @param a The coefficient 'a' as a String.
     * @param b The coefficient 'b' as a String.
     * @param c The coefficient 'c' as a String.
     * @return A Result<Int> representing the result of the calculation or an exception if any of the conversions fail.
     */
    private fun `calculate ax2 + bx + c`(x: Int, a: String, b: String, c: String): Result<Int> =
        runCatching { a.toInt() * x * x }
            .mapCatching { it + b.toInt() * x }
            .mapCatching { it + c.toInt() }

    /**
     * Test case to demonstrate chained calculations and catching the first failure.
     */
    @Test
    fun `Using chained call with catching first failure`() {

        // Calculate the expression successfully with valid inputs.
        assertEquals(6, `calculate ax2 + bx + c`(1, "1", "2", "3").getOrNull()!!)

        // Attempt to calculate with 'a' as a non-integer should result in an exception.
        assertTrue(`calculate ax2 + bx + c`(1, "1.1", "2", "3").exceptionOrNull().toString().contains("1.1"))

        // Attempt to calculate with 'b' as a non-integer should result in an exception.
        assertTrue(`calculate ax2 + bx + c`(1, "1", "2.2", "3").exceptionOrNull().toString().contains("2.2"))

        // Attempt to calculate with 'c' as a non-integer should result in an exception.
        assertTrue(`calculate ax2 + bx + c`(1, "1", "2", "3.3").exceptionOrNull().toString().contains("3.3"))
    }

    /**
     * Converts a string to an integer using a strategy of "trying many approaches to get a value."
     * This function first attempts to convert the string to an integer safely. If that fails, it tries
     * to convert the string to a double and then to an integer. If both conversions fail, it returns
     * Int.MAX_VALUE.
     *
     * @return An integer value obtained from the string or Int.MAX_VALUE if all conversion attempts fail.
     */
    private fun String.toIntAnyway(): Int =
        this.toIntSafe()
            .recoverCatching { this.toDouble().toInt() }
            .getOrDefault(Int.MAX_VALUE)

    /**
     * Test cases to demonstrate the "try many approaches to get a value" strategy.
     */
    @Test
    fun `Using chained call anyway strategy`() {

        // Successfully convert "2" to an integer.
        assertEquals(2, "2".toIntAnyway())

        // Convert "2.2" to an integer after trying the double conversion.
        assertEquals(2, "2.2".toIntAnyway())

        // Convert "23.81e5" to an integer after trying the double conversion.
        assertEquals(2381000, " 23.81e5".toIntAnyway())

        // Conversion fails for " Very match," so it returns Int.MAX_VALUE.
        assertEquals(Int.MAX_VALUE, " Very match".toIntAnyway())
    }


    /**
     * Attempts to extract the sign of an integer from the current [String].
     *
     * @return A [Result] containing the sign of the integer as a [String] ("+" or "-"), or `null` by zero
     * if the operation is not applicable (e.g., for non-integer strings) [Result] contains exception.
     */
    private fun String.signOfInt(): Result<String>? {
        // Try to convert the string to an integer using the toIntSafe() extension function.
        val result = this.toIntSafe()

        // If the conversion fails, return a failure result with the exception.
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)

        // Determine the sign of the integer and return it as a success result.
        return when (result.getOrNull()!!.sign) {
            1 -> Result.success("+")
            -1 -> Result.success("-")
            else -> null
        }
    }

    /**
     * Test case for using the `signOfInt` function with result-less return.
     *
     * This test demonstrates the usage of the `signOfInt` function to obtain results and exceptions in scenarios where
     * the function may return a result or no result (`null`) based on the input.
     *
     * - It calls the `signOfInt` function with an invalid input "1.1" and uses `exceptionOrNull` to retrieve the exception.
     *   It asserts that an exception of type `NumberFormatException` is thrown, and the error message contains "1.1".
     * - It calls the `signOfInt` function with a valid input "0" and expects the result to be `null` since "0" has no sign.
     * - It calls the `signOfInt` function with valid inputs "+11" and "-112" and uses `getOrNull` to retrieve the result.
     *   It asserts that the results are "+", and "-", respectively.
     */
    @Test
    fun `Using not-applicable`() {
        // Call the `signOfInt` function with an invalid input "1.1" and retrieve the exception.
        val result1 = "1.1".signOfInt()?.exceptionOrNull()

        // Assert that an exception of type `NumberFormatException` is thrown, and the error message contains "1.1".
        assertNotNull(result1)
        assertIs<NumberFormatException>(result1)
        assertTrue(result1.message!!.contains("1.1"))

        // Call the `signOfInt` function with a valid input "0" and expect the result to be `null` since "0" has no sign.
        assertNull("0".signOfInt())

        // Call the `signOfInt` function with valid inputs "+11" and "-112" and retrieve the results.
        val result2 = "+11".signOfInt()?.getOrNull()
        val result3 = "-112".signOfInt()?.getOrNull()

        // Assert that the results are "+", and "-", respectively.
        assertEquals("+", result2)
        assertEquals("-", result3)
    }

    // This test case demonstrates a naive usage of the Result class for handling conversions.
    @Test
    fun `Naive using of Result 1`() {
        //Bad practices!
        val result = "12".toIntSafe()
        if(result.isSuccess){
            someActionWithValue(result.getOrNull()!!)
        }else{
            someActionWithError(result.exceptionOrNull()!!)
        }
        assertTrue(actionWithValueExecuted)
        assertFalse(actionWithErrorExecuted)

    }

    // This test case demonstrates a naive usage of the Result class for handling conversions.
    @Test
    fun `Naive using of Result 2`() {
        //Bad practices!
        val result = "12.1".toIntSafe()
        if(result.isSuccess){
            someActionWithValue(result.getOrNull()!!)
        }else{
            someActionWithError(result.exceptionOrNull()!!)
        }
        assertFalse(actionWithValueExecuted)
        assertTrue(actionWithErrorExecuted)
    }

    // This test case demonstrates the idiomatic usage of the Result class for handling conversions.
    @Test
    fun `Using Result idiomatically 1`() {

        "12".toIntSafe()
            .onSuccess {  someActionWithValue(it)}
            .onFailure { someActionWithError(it) }

        assertTrue(actionWithValueExecuted)
        assertFalse(actionWithErrorExecuted)
    }

    // This test case demonstrates the idiomatic usage of the Result class for handling conversions.
    @Test
    fun `Using Result idiomatically 2`() {

        "12.1".toIntSafe()
            .onSuccess {  someActionWithValue(it)}
            .onFailure { someActionWithError(it) }

        assertFalse(actionWithValueExecuted)
        assertTrue(actionWithErrorExecuted)
    }

    private fun someActionWithValue(ignoredValue: Int){
        actionWithValueExecuted = true
    }

    private fun someActionWithError(ignoredError: Throwable){
        actionWithErrorExecuted = true
    }
}