package io.dotanuki.demos.core.networking

import com.google.common.truth.Truth.assertThat
import io.dotanuki.demos.core.networking.errors.NetworkingError
import io.dotanuki.demos.core.networking.errors.RemoteServiceIntegrationError
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ManagedExecutionTests {

    @Test fun `should transform downstream error with managed execution`() {

        val otherError = IllegalStateException("Houston, we have a problem!")

        listOf(
            UnknownHostException("No Internet") to NetworkingError.HostUnreachable,
            SerializationException("Ouch") to RemoteServiceIntegrationError.UnexpectedResponse,
            httpException() to RemoteServiceIntegrationError.RemoteSystem,
            otherError to otherError,
        ).forEach { (incoming, expected) ->
            runBlocking {
                val result = runCatching { managedExecution { emulateError(incoming) } }
                val unwrapped = unwrapCaughtError(result)
                assertThat(unwrapped).isEqualTo(expected)
            }
        }
    }

    private fun unwrapCaughtError(result: Result<*>) =
        result.exceptionOrNull() ?: throw IllegalArgumentException("Not an error")

    private suspend fun emulateError(error: Throwable): Unit =
        suspendCoroutine { continuation ->
            continuation.resumeWithException(error)
        }

    private fun httpException(): HttpException {
        val jsonMediaType = "application/json".toMediaTypeOrNull()
        val body = "Server down".toResponseBody(jsonMediaType)
        return HttpException(Response.error<Nothing>(503, body))
    }
}
