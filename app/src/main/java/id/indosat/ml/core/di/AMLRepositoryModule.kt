package id.indosat.ml.core.di

import dagger.Binds
import dagger.Module
import id.indosat.ml.core.repository.IMLRepository
import id.indosat.ml.core.repository.IMLRepositoryImpl

@Module
abstract class AMLRepositoryModule{
    @Binds
    abstract fun bindRepository(repImpl:IMLRepositoryImpl):IMLRepository
}