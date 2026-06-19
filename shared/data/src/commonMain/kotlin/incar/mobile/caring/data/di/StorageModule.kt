package incar.mobile.caring.data.di

import incar.mobile.caring.data.storage.JvmSecureStorage
import incar.mobile.caring.domain.service.SecureStorage
import org.koin.core.module.Module
import org.koin.dsl.module

fun platformStorageModule(): Module = module {
    single<SecureStorage> { JvmSecureStorage() }
}
