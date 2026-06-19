package incar.mobile.caring.admin.di

import incar.mobile.caring.admin.BuildConfig
import incar.mobile.caring.admin.data.AdminApiService
import incar.mobile.caring.admin.viewmodel.AdjusterListViewModel
import incar.mobile.caring.admin.viewmodel.ConsultingRequestViewModel
import incar.mobile.caring.admin.viewmodel.EducationRequestViewModel
import incar.mobile.caring.admin.viewmodel.LoginViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun adminCommonModule(baseUrl: String) = module {
    single { AdminApiService(get(), baseUrl, BuildConfig.APP_VERSION) }
    viewModelOf(::LoginViewModel)
    viewModelOf(::AdjusterListViewModel)
    viewModelOf(::ConsultingRequestViewModel)
    viewModelOf(::EducationRequestViewModel)
}
