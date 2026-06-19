package incar.mobile.caring.di

import incar.mobile.caring.data.di.dataModule
import incar.mobile.caring.data.di.networkModule
import incar.mobile.caring.data.di.platformCryptoModule
import incar.mobile.caring.data.di.platformDatabaseModule
import incar.mobile.caring.data.di.platformStorageModule
import incar.mobile.caring.domain.service.CertificationService
import incar.mobile.caring.domain.service.TokenService
import incar.mobile.caring.domain.usecase.CertifyFromImpUidUseCase
import incar.mobile.caring.domain.usecase.CertifyUseCase
import incar.mobile.caring.domain.usecase.CheckAuthUseCase
import incar.mobile.caring.domain.usecase.GetHomeDataUseCase
import incar.mobile.caring.domain.usecase.GetUserInfoUseCase
import incar.mobile.caring.domain.usecase.GetUserTypeUseCase
import incar.mobile.caring.domain.usecase.LogoutUseCase
import incar.mobile.caring.domain.usecase.SendAgreementUseCase
import incar.mobile.caring.domain.usecase.SignupUseCase
import incar.mobile.caring.viewmodel.AddCarViewModel
import incar.mobile.caring.viewmodel.AuthViewModel
import incar.mobile.caring.viewmodel.HomeViewModel
import incar.mobile.caring.viewmodel.MainViewModel
import incar.mobile.caring.viewmodel.MoreViewModel
import incar.mobile.caring.viewmodel.SignupViewModel
import incar.mobile.caring.viewmodel.SplashViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::TokenService)
    factoryOf(::CertificationService)
    factoryOf(::CheckAuthUseCase)
    factoryOf(::CertifyUseCase)
    factoryOf(::CertifyFromImpUidUseCase)
    factoryOf(::SendAgreementUseCase)
    factoryOf(::GetUserTypeUseCase)
    factoryOf(::GetHomeDataUseCase)
    factoryOf(::GetUserInfoUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::SignupUseCase)
}

val viewModelModule = module {
    factoryOf(::SplashViewModel)
    factoryOf(::AuthViewModel)
    factoryOf(::MainViewModel)
    factoryOf(::HomeViewModel)
    factoryOf(::MoreViewModel)
    factoryOf(::AddCarViewModel)
    factoryOf(::SignupViewModel)
}

fun initKoin(
    baseUrl: String,
    appVersion: String,
    iamportImpKey: String,
    iamportImpSecret: String,
    kicaaBaseUrl: String = "https://www.car-ing.kr",
    appDeclaration: KoinAppDeclaration = {},
    extraModules: List<Module> = emptyList(),
) {
    startKoin {
        appDeclaration()
        modules(
            module { },  // security (불필요)
            platformCryptoModule(),
            networkModule(baseUrl, appVersion, iamportImpKey, iamportImpSecret, kicaaBaseUrl),
            platformDatabaseModule(),
            platformStorageModule(),
            dataModule,
            domainModule,
            viewModelModule,
            *extraModules.toTypedArray(),
        )
    }
}
