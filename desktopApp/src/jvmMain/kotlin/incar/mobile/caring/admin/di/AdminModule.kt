package incar.mobile.caring.admin.di

import incar.mobile.caring.admin.data.AdminApiService
import incar.mobile.caring.admin.viewmodel.AdjusterListViewModel
import incar.mobile.caring.admin.viewmodel.LoginViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf

val adminModule = module {
    single { AdminApiService(get(), get(named("baseUrl")), "1.0") }
    viewModelOf(::LoginViewModel)
    viewModelOf(::AdjusterListViewModel)
}
