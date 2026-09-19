package com.example.maitescalc.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class OpenFoodFactsSearchResponse(
    val count: Int = 0,
    val products: List<OpenFoodFactsProduct> = emptyList()
)

data class OpenFoodFactsProduct(
    val code: String = "",
    val product_name: String = "",
    val brands: String = "",
    val quantity: String = "",
    val categories: String = "",
    val image_url: String = "",
    val image_small_url: String = "",
    val ingredients_text: String = ""
)

data class OpenFoodFactsProductResponse(
    val status: Int = 0,
    val product: OpenFoodFactsProduct? = null
)

interface OpenFoodFactsApiService {
    @GET("cgi/search.pl")
    suspend fun searchProducts(
        @Query("search_terms") searchTerms: String,
        @Query("search_simple") searchSimple: Int = 1,
        @Query("action") action: String = "process",
        @Query("json") json: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("fields") fields: String = "code,product_name,brands,quantity,categories,image_url,image_small_url,ingredients_text"
    ): OpenFoodFactsSearchResponse

    @GET("api/v2/product/{barcode}")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String,
        @Query("fields") fields: String = "code,product_name,brands,quantity,categories,image_url,image_small_url,ingredients_text"
    ): OpenFoodFactsProductResponse
}

object OpenFoodFactsApi {
    private const val BASE_URL = "https://world.openfoodfacts.org/"

    val service: OpenFoodFactsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFoodFactsApiService::class.java)
    }
}
