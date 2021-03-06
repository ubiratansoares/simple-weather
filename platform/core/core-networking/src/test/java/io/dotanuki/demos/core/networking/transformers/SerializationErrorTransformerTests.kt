package io.dotanuki.demos.core.networking.transformers

import com.google.common.truth.Truth.assertThat
import io.dotanuki.demos.core.networking.transformers.CheckErrorTransformation.Companion.checkTransformation
import io.dotanuki.demos.core.networking.errors.RemoteServiceIntegrationError
import kotlinx.serialization.SerializationException
import org.junit.Test

class SerializationErrorTransformerTests {

    @Test fun `should transform serialization error from downstream`() {
        val parseError = SerializationException("Found comments inside this JSON")
        val expected = RemoteServiceIntegrationError.UnexpectedResponse
        assertTransformation(parseError, expected)
    }

    @Test fun `should not handle any other errors`() {
        val otherError = IllegalStateException("Something broke here ...")
        assertTransformation(otherError, otherError)
    }

    private fun assertTransformation(target: Throwable, expected: Throwable) {
        checkTransformation(
            from = target,
            using = SerializationErrorTransformer,
            check = { transformed -> assertThat(transformed).isEqualTo(expected) }
        )
    }
}
