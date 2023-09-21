package eu.sirotin.kotlin.error

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for the [BusinessError] class.
*/
internal class BusinessErrorTest {

    /**
     * This test suite includes test cases to verify the behavior of the [BusinessError] class and its construction
     * with various combinations of parameters.
     *
     * - It creates a [BusinessError] instance with default parameters and asserts that the `code` property is `null`.
     * - It creates a [BusinessError] instance with a custom error code and asserts that the `code` property matches the input.
     * - It creates a [BusinessError] instance with a custom error code and message, and asserts that both properties match.
     * - It creates a [BusinessError] instance with a custom error code, message, and details, and asserts that all properties match.
     * - It creates a [BusinessError] instance with a custom error code, message, details, and a cause, and asserts that all properties match.

     */
    @Test
    fun `Using BusinessError`() {
        // Create a BusinessError instance with default parameters and assert that the code is null.
        val result1 = Result.failure<BusinessError>(BusinessError())
            .exceptionOrNull() as BusinessError

        assertNull(result1.code)

        // Create a BusinessError instance with a custom error code and assert that the code property matches the input.
        val result2 = Result.failure<BusinessError>(BusinessError("CODE312"))
            .exceptionOrNull() as BusinessError

        assertEquals("CODE312", result2.code)
        assertNull(result2.message)

        // Create a BusinessError instance with a custom error code and message, and assert that both properties match.
        val result3 = Result.failure<BusinessError>(BusinessError("CODE313", "Some error 1"))
            .exceptionOrNull() as BusinessError

        assertEquals("CODE313", result3.code)
        assertEquals("Some error 1", result3.message)
        assertNull(result3.details)

        // Create a BusinessError instance with a custom error code, message, and details, and assert that all properties match.
        val result4 = Result.failure<BusinessError>(BusinessError("CODE314",
            "Some error 2",
            "Some details")).exceptionOrNull() as BusinessError

        assertEquals("CODE314", result4.code)
        assertEquals("Some error 2", result4.message)
        assertEquals("Some details", result4.details)
        assertNull(result4.cause)
    }
}
