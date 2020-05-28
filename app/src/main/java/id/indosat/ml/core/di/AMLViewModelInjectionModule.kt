package id.indosat.ml.core.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import id.indosat.ml.base.FCMService


@Module
abstract class AMLViewModelInjectionModule{
    @Binds
    abstract fun bindViewModelFactory(factory: MLViewModelFactory): ViewModelProvider.Factory
    @ContributesAndroidInjector
    internal abstract fun contributeFCMService(): FCMService
}