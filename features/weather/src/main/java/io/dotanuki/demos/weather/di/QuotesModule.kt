package io.dotanuki.demos.weather.di

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.dotanuki.bootstrap.common.kodein.KodeinTags
import io.dotanuki.demos.weather.infrastrucure.QuotesInfrastructure
import io.dotanuki.demos.weather.infrastrucure.QuotesRestService
import io.dotanuki.demos.weather.presentation.QuotesViewModel
import io.dotanuki.demos.weather.ui.QuotesScreen
import io.dotanuki.demos.weather.ui.WrappedQuotesScreen
import io.dotanuki.bootstrap.networking.RestServiceBuilder
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import org.kodein.di.singleton

val quotesModule = DI.Module("quotes-module") {

    bind<QuotesRestService> {
        singleton {
            RestServiceBuilder.build(QuotesRestService.BASE_URL)
        }
    }

    bind {
        provider {
            @Suppress("UNCHECKED_CAST") val factory = object : ViewModelProvider.Factory {

                val infrastructure = QuotesInfrastructure(instance())

                override fun <VM : ViewModel> create(modelClass: Class<VM>) =
                    QuotesViewModel(infrastructure) as VM
            }

            val host: FragmentActivity = instance(KodeinTags.hostActivity)
            ViewModelProvider(host, factory)[QuotesViewModel::class.java]
        }
    }

    bind<QuotesScreen> {
        provider {
            WrappedQuotesScreen()
        }
    }
}