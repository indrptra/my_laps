package id.indosat.ml.core.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import id.indosat.ml.core.config.MLConfig
import id.indosat.ml.core.network.IMLNetworkService
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class MLNetworkModule{

    @Provides
    @Singleton
    fun provideGson():Gson = GsonBuilder().serializeNulls().setLenient().create()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val newSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2)
            .cipherSuites(CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA).build()
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.MINUTES) // write timeout
            .readTimeout(5, TimeUnit.MINUTES) // read timeout
            .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS,newSpec))
            //.addInterceptor(BasicAuthInterceptor(BISNetworkConfig.CLIENT_ID,BISNetworkConfig.CLIENT_KEY))
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient,gson:Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(MLConfig.BaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideMLRemoteService(retrofit: Retrofit):IMLNetworkService = retrofit.create(IMLNetworkService::class.java)
}