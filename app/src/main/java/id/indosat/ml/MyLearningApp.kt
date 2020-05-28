package id.indosat.ml

import android.app.Activity
import android.app.Service
import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.chibatching.kotpref.Kotpref
import com.crashlytics.android.Crashlytics
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.zopim.android.sdk.api.ZopimChat
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import dagger.android.support.HasSupportFragmentInjector
import id.indosat.ml.core.di.DaggerIMLAppComponent
import id.indosat.ml.core.di.IMLAppComponent
import io.fabric.sdk.android.Fabric
import javax.inject.Inject

class MyLearningApp:MultiDexApplication(),HasActivityInjector,HasSupportFragmentInjector, HasServiceInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var dispatchingAndroidFragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

    companion object {
        lateinit var mlAppComponent: IMLAppComponent
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
        Fabric.with(this,Crashlytics())
        mlAppComponent = DaggerIMLAppComponent.create()
        mlAppComponent.inject(this)
        ZopimChat.init("Zj1cSxm0HuAyc3SBsOXMoKzBeOvhBHMk")
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseInstanceId.getInstance()?.instanceId?.addOnSuccessListener { i ->
            Log.d("FCM TOKEN", i.token)
        }
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidFragmentInjector
    }

    override fun serviceInjector(): AndroidInjector<Service> {
        return dispatchingServiceInjector
    }
}