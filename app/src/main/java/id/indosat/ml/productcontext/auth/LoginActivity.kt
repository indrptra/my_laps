package id.indosat.ml.productcontext.auth

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import dagger.android.AndroidInjection
import id.indosat.ml.R
import id.indosat.ml.common.coordinator.navigateTo
import id.indosat.ml.common.model.MLPrefModel
import id.indosat.ml.common.model.response.ResultLogin
import id.indosat.ml.core.di.MLViewModelFactory
import id.indosat.ml.productcontext.home.HomeActivity
import id.indosat.ml.productcontext.onboard.OnBoardBMActivity
import id.indosat.ml.util.MLLog
import id.indosat.ml.viewmodel.MLGeneralViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {
    lateinit var userViewModel: UserViewModel
    lateinit var generalViewModel:MLGeneralViewModel
    @Inject
    lateinit var viewModelFactory:MLViewModelFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_submit_btn?.setOnClickListener {
            validateFormAndSubmit(login_user_id?.text?.toString() ?: "",
                login_password?.text?.toString() ?: "")
        }
        userViewModel = ViewModelProviders.of(this,viewModelFactory).get(UserViewModel::class.java)
        generalViewModel = ViewModelProviders.of(this,viewModelFactory).get(MLGeneralViewModel::class.java)
    }

    private fun validateFormAndSubmit(username:String,password:String){
        if(!username.isEmpty() && !password.isEmpty()){
            doLogin(username,password)
        }else{
            showToast(resources?.getString(R.string.tm_login_empty_form) ?: "")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        userViewModel.clear()
        try {
            generalViewModel.clear()
        }catch (e:Exception){
            MLLog.showLog("LOGIN_ACT",e.localizedMessage)
        }
    }

    private fun toggleProgress(progressState:Boolean){
        login_progress?.visibility = if(progressState) View.VISIBLE else View.GONE
        login_submit_btn?.visibility = if(progressState) View.GONE else View.VISIBLE
    }

    private fun doLogin(username: String,password: String){
        toggleProgress(true)
        userViewModel.doLogin(username,password){ errorState, message, responseResult->
            if(errorState){
                toggleProgress(false)
                showToast(message)
            }else{
                responseResult?.let {
                    saveLoginResultAndRedirect(it)

                    FirebaseInstanceId.getInstance().instanceId
                        .addOnCompleteListener(OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.w("LOGIN_ACT", "getInstanceId failed", task.exception)
                                return@OnCompleteListener
                            }

                            // Get new Instance ID token
                            val token = task.result?.token
                            generalViewModel.registerDeviveId(token!!){_, _, _, _->}
                            Log.d("LOGIN_ACT_FCM_TOKEN", token)
                         })
                }

            }
        }
    }

    private fun saveLoginResultAndRedirect(result:ResultLogin){
        Single.just(userViewModel.saveLoginResult(result))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                toggleProgress(true)
                if(!MLPrefModel.isUserAlreadyOnBoard){
                    getOnBoardData()
                }else {
                    navigateTo<HomeActivity>(true)
                }
            },{error->
                MLLog.showLog(this.localClassName,error.localizedMessage ?: "")
            })
    }

    private fun getOnBoardData(){
        generalViewModel.getOnBoarding { _, errorState, message, resultOnBoarding ->
            if(!errorState){
                Handler().postDelayed({
                    navigateTo<OnBoardBMActivity>(false,resultOnBoarding, OnBoardBMActivity.resOnBoardingName)
                }, 2000)
            }else {
                showToast(message)
                navigateTo<HomeActivity>(true)
            }
        }
    }

    private fun showToast(message:String,duration:Int = Toast.LENGTH_SHORT){
        Toast.makeText(this,message, duration).show()
    }
}
