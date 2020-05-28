package id.indosat.ml.productcontext.onboard

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.ViewModelProviders
import dagger.android.AndroidInjection
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.R
import id.indosat.ml.common.coordinator.navigateTo
import id.indosat.ml.common.model.MLPrefModel
import id.indosat.ml.productcontext.auth.LoginActivity
import id.indosat.ml.productcontext.home.HomeActivity
import id.indosat.ml.util.showToast
import id.indosat.ml.viewmodel.MLGeneralViewModel


class OnBoardActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_onboard)
        generalViewModel = ViewModelProviders.of(this,viewModelFactory).get(MLGeneralViewModel::class.java)
        if(!MLPrefModel.isUserLoggedIn) {
            Handler().postDelayed({
                navigateTo<LoginActivity>(true)//this should redirect to login act
            }, 3000)
        }else{
            Handler().postDelayed({
                navigateTo<HomeActivity>(true)
            }, 2000)
        }
    }



}
