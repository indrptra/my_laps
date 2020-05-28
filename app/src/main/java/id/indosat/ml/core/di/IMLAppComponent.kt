package id.indosat.ml.core.di

import com.google.gson.Gson
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import id.indosat.ml.MyLearningApp
import retrofit2.Retrofit
import javax.inject.Singleton

@Component(modules = [AndroidSupportInjectionModule::class,
    AMLViewModelInjectionModule::class,MLAppModule::class,
    MLNetworkModule::class,AMLRepositoryModule::class,
    AMLViewModelModule::class,
    AMLActivityModule::class,
    AMLFragmentModule::class])
@Singleton
interface IMLAppComponent:AndroidInjector<MyLearningApp>{
    //fun inject(activity:BaseActivity)
    //fun inject(app:MyLearningApp)
    //fun inject(activity:HomeActivity)
    fun getGson(): Gson
    fun getRetrofit(): Retrofit
}