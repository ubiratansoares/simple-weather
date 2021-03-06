package io.dotanuki.demos.core.navigator

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.common.truth.Truth.assertAbout
import com.google.common.truth.Truth.assertThat
import io.dotanuki.demos.testing.commons.truth.EspeculativeExecution
import io.dotanuki.demos.core.navigator.Screen.FactsList
import io.dotanuki.demos.core.navigator.Screen.SearchQuery
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class NavigatorTests {

    class LauncherActivity : FragmentActivity()
    class DestinationActivity : Activity()

    private lateinit var navigator: Navigator
    private lateinit var launcher: LauncherActivity

    private val links = mapOf<Screen, Class<out Activity>>(
        SearchQuery to DestinationActivity::class.java
    )

    @Before fun `before each test`() {
        val origin = LauncherActivity::class.java
        launcher = Robolectric.buildActivity(origin).create(Bundle.EMPTY).get()
        navigator = Navigator(launcher, links)
    }

    @Test fun `should navigate to supported screen`() {
        val shadowActivity = shadowOf(launcher)

        navigator.navigateTo(SearchQuery)

        val launched = shadowActivity.nextStartedActivity
        assertThat(launched.component?.shortClassName).isEqualTo(DestinationActivity::class.java.name)
    }

    @Test fun `should throw when navigating to unsupported screen`() {

        val execution = EspeculativeExecution { navigator.navigateTo(FactsList) }

        val expectedError = UnsupportedNavigation(FactsList)

        assertAbout(execution).that(expectedError).hasBeingThrown()
    }
}
