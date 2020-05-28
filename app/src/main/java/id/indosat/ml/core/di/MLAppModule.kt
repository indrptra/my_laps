package id.indosat.ml.core.di

import dagger.Module
import dagger.Provides
import id.indosat.ml.MyLearningApp
import javax.inject.Singleton

@Module
class MLAppModule(private val app:MyLearningApp){
    @Provides
    @Singleton
    fun provideApp():MyLearningApp = app
}