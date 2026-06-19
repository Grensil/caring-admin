package incar.mobile.caring.data.di

import incar.mobile.caring.data.service.JvmCryptoService
import incar.mobile.caring.domain.service.CryptoService
import org.koin.core.module.Module
import org.koin.dsl.module

fun platformCryptoModule(): Module = module {
    single<CryptoService> { JvmCryptoService() }
}

fun platformDatabaseModule(): Module = module { }
