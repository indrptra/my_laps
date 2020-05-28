package id.indosat.ml.core.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.productcontext.auth.LoginActivity
import id.indosat.ml.productcontext.course.*
import id.indosat.ml.productcontext.forum.*
import id.indosat.ml.productcontext.home.DashboardActivity
import id.indosat.ml.productcontext.home.HomeActivity
import id.indosat.ml.productcontext.mycourse.MyCourseMainActivity
import id.indosat.ml.productcontext.notification.NotificationActivity
import id.indosat.ml.productcontext.notification.NotificationDetailActivity
import id.indosat.ml.productcontext.onboard.OnBoardActivity
import id.indosat.ml.productcontext.onboard.OnBoardBMActivity
import id.indosat.ml.productcontext.search.SearchActivity
import id.indosat.ml.productcontext.search.SearchForumActivity
import id.indosat.ml.productcontext.support.SupportWebActivity

@Module
abstract class AMLActivityModule{
    @ContributesAndroidInjector
    abstract fun contributeBaseActivity():BaseActivity

    @ContributesAndroidInjector
    abstract fun contributeLoginActivity():LoginActivity

    @ContributesAndroidInjector
    abstract fun contributeOnboardActivity():OnBoardActivity

    @ContributesAndroidInjector
    abstract fun contributeHomeActivity(): HomeActivity

    @ContributesAndroidInjector
    abstract fun contributeOnBoardBMActivity(): OnBoardBMActivity

    @ContributesAndroidInjector
    abstract fun contributeSearchctivity(): SearchActivity

    @ContributesAndroidInjector
    abstract fun contributeMainCourseActivity(): MainCourseActivity

    @ContributesAndroidInjector
    abstract fun contributeDashboardActivity(): DashboardActivity

    @ContributesAndroidInjector
    abstract fun contributeCourseDetailActivity(): CourseDetailActivity

    @ContributesAndroidInjector
    abstract fun contributeCourseSeeAllActivity(): SortedCourseSeeAllActivity

    @ContributesAndroidInjector
    abstract fun contributeSupportActivity(): SupportWebActivity

    @ContributesAndroidInjector
    abstract fun contributeNotificationActivity(): NotificationActivity

    @ContributesAndroidInjector
    abstract fun contributeNotificationDetailActivity(): NotificationDetailActivity

    @ContributesAndroidInjector
    abstract fun contributeMyCourseMainActivity(): MyCourseMainActivity

    @ContributesAndroidInjector
    abstract fun contributeSubCategoriesActivity(): SubCategoriesActivity

    @ContributesAndroidInjector
    abstract fun contributeVideoCourseDetailActivity(): VideoCourseDetailActivity

    @ContributesAndroidInjector
    abstract fun contributeMyDiscussionMainActivity(): MyDiscussionMainActivity

    @ContributesAndroidInjector
    abstract fun contributeMainKnowledgeForumActivity(): MainKnowledgeForumActivity

    @ContributesAndroidInjector
    abstract fun contributeDetailForumActivity(): DetailForumActivity

    @ContributesAndroidInjector
    abstract fun contributeSearchForumActivity(): SearchForumActivity

    @ContributesAndroidInjector
    abstract fun contributeAdvanceEditorActivity(): AdvanceEditor

    @ContributesAndroidInjector
    abstract fun contributeReportActivity(): ReportActivity

}