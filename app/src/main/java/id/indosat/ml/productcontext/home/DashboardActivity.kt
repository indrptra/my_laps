package id.indosat.ml.productcontext.home

import android.Manifest

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
//import com.theartofdev.edmodo.cropper.CropImage
//import com.theartofdev.edmodo.cropper.CropImageView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import dagger.android.AndroidInjection
import id.indosat.ml.R
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.common.coordinator.navigateTo
import id.indosat.ml.productcontext.auth.LoginActivity
import id.indosat.ml.productcontext.auth.UserViewModel
import id.indosat.ml.util.MLLog
import id.indosat.ml.util.loadURL
import id.indosat.ml.util.showToast
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.text.NumberFormat

class DashboardActivity : BaseActivity() {
    companion object {

    }
    private lateinit var userViewModel: UserViewModel
    private var leaderBoardAdapter = GroupAdapter<ViewHolder>()
    override fun onDestroy() {
        super.onDestroy()
        userViewModel.clear()
        try {
            generalViewModel.clear()
        }catch (e:Exception){
            MLLog.showLog(classTag,e.localizedMessage)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "MY DASHBOARD"
        supportActionBar?.elevation = 0f
        setOnClick()
        initList()
        initViewModel()
        /*dashboard_points_togo_progress?.
            progressDrawable?.
            setColorFilter(ContextCompat.getColor(this,R.color.colorHorizontalProgress),android.graphics.PorterDuff.Mode.SRC_IN)*/
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            dashboard_points_togo_progress?.progressDrawable = resources?.getDrawable(R.drawable.dashboard_horizontal_progress,null)
        }else{
            dashboard_points_togo_progress?.progressDrawable = resources?.getDrawable(R.drawable.dashboard_horizontal_progress)
        }

        getDashboard()
        getLeaderBoard()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val imageUri = CropImage.getPickImageResultUri(this,data)
            imageUri?.let {itImageUri ->
                CropImage.activity(itImageUri).start(this)
            }
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val result = CropImage.getActivityResult(data)
            result.uri?.path?.let {
                //PrefModel.profileImagePath = it
            }
            result?.uri?.let {
                dashboard_user_image?.setImageURI(it)
                //postUserImage(File(it.path))
            }
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
            val result = CropImage.getActivityResult(data)
            result.error?.let {itException->
                showToast(itException.localizedMessage?:"")
            }
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
            val result = CropImage.getActivityResult(data)
            result.error?.let {itException->
                showToast(itException.localizedMessage?:"")
            }
        }else{}*/
    }

    private fun setOnClick(){
        /*dashboard_cam?.setOnClickListener {
            doPictSelection()
        }*/
    }

    private fun doPictSelection(){
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {itReports->
                        if (itReports.deniedPermissionResponses.size > 0){
                            var stringPermissionName = ""
                            for (r in itReports.deniedPermissionResponses){
                                when(r.permissionName){
                                    Manifest.permission.READ_EXTERNAL_STORAGE ->
                                        stringPermissionName = "READ EXTERNAL STORAGE Permission"
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE->
                                        stringPermissionName = "WRITE EXTERNAL STORAGE Permission"
                                    Manifest.permission.CAMERA->
                                        stringPermissionName = "CAMERA Permission"
                                }
                                showToast("$stringPermissionName needed to continue")
                                break
                            }
                        }else{
                            doCropImageAct()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    private fun doCropImageAct(){
        //CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this)
        //CropImage.startPickImageActivity(this)
    }

    private fun initList(){
        val llm = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        leaderboard_rv?.layoutManager = llm
        leaderboard_rv?.adapter = leaderBoardAdapter
    }

    private fun initViewModel(){
        userViewModel = ViewModelProviders.of(this,viewModelFactory).get(UserViewModel::class.java)
    }


    private fun getDashboard(){
        toggleProgress(true)
        userViewModel.getUserDashboard { isValidToken, errorState, message, response ->
            toggleProgress(false)
            if(!isValidToken){
                navigateTo<LoginActivity>(true)
            }else{
                if(errorState){
                    showToast(message)
                }else{
                    response?.let {
                        //should do load the view here..
                        //showToast(message)
                        dashboard_user_name?.text = it.fullname

                        dashboard_user_points?.text = "${NumberFormat.getIntegerInstance().format(it.points)} POINTS"
                        dashboard_points_to_go_title?.text = "${NumberFormat.getIntegerInstance().format(it.pointtogo)} pts to go"
                        dashboard_points_togo_progress?.max = it.nextlevelpoints
                        dashboard_points_togo_progress?.progress = it.nextlevelpoints - it.pointtogo
                        if(!isFinishing) {
                            dashboard_user_image?.loadURL(it.profileimageurl)
                            dashboard_top_image?.loadURL(it.levelbadges)
                            dashboard_badge_progress?.loadURL(it.nextlevelbadges)
                        }
                    }?:run {
                        showToast(message)
                    }
                }
            }
        }
    }

    private fun getLeaderBoard(){
        leaderBoardAdapter.clear()
        toggleProgressLeaderBoard(true)
        userViewModel.getLeaderBoard{isTokenValid,errorState,message,response->
            toggleProgressLeaderBoard(false)
            if(isTokenValid){
                if(!errorState){
                    response?.let {
                        if(it.rows.isNotEmpty()){
                            leaderBoardAdapter.addAll(it.rows.map { row-> ItemLeaderBoardContent(row)})
                        }else{
                            showToast(message)
                        }
                    }?:run{
                        showToast(message)
                    }
                }else{
                    showToast(message)
                }
            }else{
                showToast(message)
            }
        }
    }

    private fun toggleProgress(state:Boolean){
        progress_dashboard?.visibility = if(state) View.VISIBLE else{View.GONE}
        dashboard_top_card?.visibility = if(state) View.GONE else{View.VISIBLE}
        dashboard_mid_card?.visibility = if(state) View.GONE else{View.VISIBLE}
    }

    private fun toggleProgressLeaderBoard(state:Boolean){
        progress_leaderboard_rv?.visibility = if(state)View.VISIBLE else{View.GONE}
        leaderboard_rv?.visibility = if(state) View.GONE else{View.VISIBLE}
    }
}
