package eu.sirotin.kotlin.error

/**
 * Base class for representing business-specific errors.
 *
 * The `BusinessError` class provides a structured way to represent errors that occur within a business context.
 * It includes properties such as error `code`, `message`, `details`, and an optional `cause` throwable.
 *
 * @param code The error code associated with the business error (optional).
 * @param message A human-readable error message providing additional context (optional).
 * @param details Additional details or information about the error (optional).
 * @param cause The underlying cause of the error, such as an exception (optional).
 */
open class BusinessError(
    val code: String? = null,
    override val message: String? = null,
    val details: String? = null,
    override val cause: Throwable? = null
) : Error(message, cause)


