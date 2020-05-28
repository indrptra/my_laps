package id.indosat.ml.base
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import id.indosat.ml.core.di.MLViewModelFactory
import id.indosat.ml.viewmodel.MLGeneralViewModel
import javax.inject.Inject
import id.indosat.ml.R
abstract class BaseActivity : AppCompatActivity() {
    internal var classTag = ""

    @Inject
    lateinit var viewModelFactory:MLViewModelFactory

    lateinit var generalViewModel:MLGeneralViewModel
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        //AndroidInjection.inject(this)
        super.onCreate(savedInstanceState, persistentState)
        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        classTag = this.localClassName

    }

    override fun onResume() {
        super.onResume()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let{
            if(it.itemId == android.R.id.home){
                finish()
                return true
            }else if(it.itemId == R.id.action_settings){
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /*internal fun showToast(message:String,duration:Int = Toast.LENGTH_SHORT){
        Toast.makeText(this,message, duration).show()
    }*/
}
