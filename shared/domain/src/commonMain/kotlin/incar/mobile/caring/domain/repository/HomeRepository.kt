package incar.mobile.caring.domain.repository

import incar.mobile.caring.domain.model.HomeData

interface HomeRepository {
    suspend fun getHomeData(tokenKey: String): HomeData
}
