package id.indosat.ml.core.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import id.indosat.ml.base.BaseFragment
import id.indosat.ml.productcontext.home.HomeSortedCourseFragment
import id.indosat.ml.productcontext.mycourse.BaseMyCoursesContentFragment
import id.indosat.ml.productcontext.mycourse.MCCompletedContentFragment
import id.indosat.ml.productcontext.mycourse.MCFutureContentFragment
import id.indosat.ml.productcontext.mycourse.MCRunningContentFragment
import id.indosat.ml.productcontext.onboard.FragmentOBBM
import id.indosat.ml.productcontext.onboard.FragmentOBImage

@Module
abstract class AMLFragmentModule{
    @ContributesAndroidInjector
    abstract fun contributeBaseFragment():BaseFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeSortedFragment():HomeSortedCourseFragment

    @ContributesAndroidInjector
    abstract fun contributeOnboardBMAFragment():FragmentOBBM

    @ContributesAndroidInjector
    abstract fun contributeOnboardBMImageFragment():FragmentOBImage

    @ContributesAndroidInjector
    abstract fun contributeMyCoursesContentFragment():BaseMyCoursesContentFragment

    @ContributesAndroidInjector
    abstract fun contributeMCRunningContentFragment():MCRunningContentFragment

    @ContributesAndroidInjector
    abstract fun contributeMCFutureContentFragment():MCFutureContentFragment

    @ContributesAndroidInjector
    abstract fun contributeMCCompletedContentFragment():MCCompletedContentFragment
}