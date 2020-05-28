package id.indosat.ml.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import id.indosat.ml.productcontext.auth.UserViewModel
import id.indosat.ml.viewmodel.*


@Module
abstract class AMLViewModelModule{
    /*@Binds
    @IntoMap
    @MLViewModelKey(BaseViewModel::class)
    abstract fun bindBaseViewModel(baseViewModel: BaseViewModel): ViewModel*/


    @Binds
    abstract fun bindViewModelFactory(factory: MLViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @MLViewModelKey(MLGeneralViewModel::class)
    abstract fun bindGeneralViewModel(generalViewModel: MLGeneralViewModel): ViewModel

    @Binds
    @IntoMap
    @MLViewModelKey(UserViewModel::class)
    abstract fun bindLoginViewModel(userViewModel: UserViewModel): ViewModel

    @Binds
    @IntoMap
    @MLViewModelKey(MLCourseViewModel::class)
    abstract fun bindCourseViewModel(loginViewModel: MLCourseViewModel): ViewModel

    @Binds
    @IntoMap
    @MLViewModelKey(MLSearchViewModel::class)
    abstract fun bindSearchViewModel(loginViewModel: MLSearchViewModel): ViewModel

    @Binds
    @IntoMap
    @MLViewModelKey(MLForumViewModel::class)
    abstract fun bindForumViewModel(loginViewModel: MLForumViewModel): ViewModel

    @Binds
    @IntoMap
    @MLViewModelKey(MLNotificationViewModel::class)
    abstract fun bindNotificationViewModel(notificationViewModel: MLNotificationViewModel): ViewModel

}