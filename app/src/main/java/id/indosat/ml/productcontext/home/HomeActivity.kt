package id.indosat.ml.productcontext.home

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.google.android.material.navigation.NavigationView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import dagger.android.AndroidInjection
import id.indosat.ml.R
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.common.coordinator.navigateTo
import id.indosat.ml.common.coordinator.navigateToCourseDetail
import id.indosat.ml.common.coordinator.navigateToForumDetail
import id.indosat.ml.common.ml_enum.MLContextMenu
import id.indosat.ml.common.ml_enum.MLESliderType
import id.indosat.ml.common.ml_enum.MLESortCourseType
import id.indosat.ml.common.model.MLPrefModel
import id.indosat.ml.common.model.response.ResultSortedCourse
import id.indosat.ml.common.model.response.ResultStaticSlider
import id.indosat.ml.core.config.MLConfig
import id.indosat.ml.productcontext.auth.LoginActivity
import id.indosat.ml.productcontext.auth.UserViewModel
import id.indosat.ml.productcontext.course.MainCourseActivity
import id.indosat.ml.productcontext.course.SubCategoriesActivity
import id.indosat.ml.productcontext.forum.MainKnowledgeForumActivity
import id.indosat.ml.productcontext.forum.MyDiscussionMainActivity
import id.indosat.ml.productcontext.mycourse.MyCourseMainActivity
import id.indosat.ml.productcontext.notification.NotificationActivity
import id.indosat.ml.productcontext.search.SearchActivity
import id.indosat.ml.productcontext.support.SupportWebActivity
import id.indosat.ml.util.*
import id.indosat.ml.viewmodel.MLCourseViewModel
import id.indosat.ml.viewmodel.MLGeneralViewModel
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_home.*
import kotlinx.android.synthetic.main.menu_right_side.*
import kotlinx.android.synthetic.main.nav_header_home.*
import java.text.NumberFormat


class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var mainCatAdapter = GroupAdapter<ViewHolder>()
    private var iconMenuAdapter = GroupAdapter<ViewHolder>()
    private var bannerMenuAdapter = GroupAdapter<ViewHolder>()
    private var menuItemCount = 0
    private lateinit var courseViewModel:MLCourseViewModel
    private lateinit var userViewModel:UserViewModel
    private var stateExpanded = false
    private var contextMenu:MLContextMenu = MLContextMenu.HOME
    private var isContextMenuChanged = false
    override fun onDestroy() {
        super.onDestroy()
        try {
            userViewModel.clear()
            courseViewModel.clear()
            generalViewModel.clear()
        }catch (e:Exception){
            MLLog.showLog(classTag,e.localizedMessage)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        MLLog.showLog(classTag,"ONCREATE")
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        toolbar?.title = ""
        supportActionBar?.title = ""
        /*val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )*/

        val toggle = object : ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                super.onDrawerSlide(drawerView,0f)
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                //courseViewModel.getCategoryCourse()
                if(!isContextMenuChanged){return}
                when(contextMenu){
                    MLContextMenu.HOME ->{
                        checkMainMenuCategories()
                        getHomeRequests()
                    }
                    MLContextMenu.COURSES->{
                        navigateTo<MyCourseMainActivity>()
                    }
                    MLContextMenu.DASHBOARD->{
                        navigateTo<DashboardActivity>()
                    }
                    MLContextMenu.DISCUSSION->{
                        navigateTo<MyDiscussionMainActivity>()
                    }
                    MLContextMenu.OTHERS->{goToMainCourse()}
                    MLContextMenu.KNOWLEDGE_FORUM->{
                        navigateTo<MainKnowledgeForumActivity>()
                    }
                    MLContextMenu.INBOX->{
                        navigateTo<NotificationActivity>()
                    }
                    MLContextMenu.SUPPORT->{
                        navigateTo<SupportWebActivity>()
                    }
                }

            }
        }
        setRefreshLayout()
        //put event to
        addAnswerHomeEvent()
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this,R.color.colorAccent)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        setMenuExpandableAction()
        setClickListener()
        setUserProperties()
        setMainCatList()
        setIconMenuList()
        setSliderContainer()
        setStaticSlider()
        initiateViewModel()
        checkMainMenuCategories()
        getHomeRequests()

    }

    private fun setRefreshLayout(){
        home_refresh_layout?.setOnRefreshListener {
            home_refresh_layout?.isRefreshing = false
            getHomeRequests()
        }
    }

    private fun addAnswerHomeEvent(){
        Answers.getInstance().logCustom(CustomEvent("home_loaded"))
    }

    override fun onResume() {
        super.onResume()
        isContextMenuChanged = false
        contextMenu = MLContextMenu.HOME
        getUserPoints()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return  false
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }*/



    override fun onStop() {
        home_slider?.stopAutoCycle()
        super.onStop()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun initiateViewModel(){
        generalViewModel = ViewModelProviders.of(this,viewModelFactory).get(MLGeneralViewModel::class.java)
        courseViewModel = ViewModelProviders.of(this,viewModelFactory).get(MLCourseViewModel::class.java)
        userViewModel = ViewModelProviders.of(this,viewModelFactory).get(UserViewModel::class.java)
    }

    private fun checkMainMenuCategories(){
        generalViewModel.getMainMenu{isValidToken,isError,message,response->
            if(!isValidToken){
                doLogout(message)
            }else{
                //showToast(message)
                //TO DO :: need to cache the previous result
                //just in case network problem and we need to do it seamlessly
                response?.let {rmm->
                    mainCatAdapter.clear()
                    menuItemCount = rmm.rows.size

                    mainCatAdapter.addAll(rmm.rows.map { MainCatItem(it){rowMainCat->
                        isContextMenuChanged = true
                        contextMenu = MLContextMenu.OTHERS
                        courseViewModel.selectedCat = rowMainCat.id
                        drawer_layout?.closeDrawers()
                    } })

                    val iconItems = rmm.rows.filter { !it.iconurl.isEmpty() }
                    //(icon_menu_rv_container?.layoutParams?.width ?: 0)
                    //var eachW = (getDisplayMetrics().widthPixels - dpToPx(56))/iconItems.size
                    val iconDivider = if(iconItems.size >=5){5}else{iconItems.size}
                    val eachW = (getDisplayMetrics().widthPixels - dpToPx(56))/iconDivider
                    iconMenuAdapter.clear()
                    iconMenuAdapter.addAll(iconItems.map { ItemIconMenu(it,eachW){ rowMainCat->
                        //drawer_layout?.closeDrawers()
                        contextMenu = MLContextMenu.OTHERS
                        getCoursePerCat(rowMainCat.id)
                    } })
                }
            }

        }
    }

    private fun getCoursePerCat(id:Int){
        courseViewModel.selectedCat = id
        //courseViewModel.getCategoryCourse()
        goToMainCourse()
    }

    private fun goToMainCourse(){
        val intent = Intent(this,MainCourseActivity::class.java)
        intent.putExtra(MainCourseActivity.courseCatIdName,courseViewModel.selectedCat)
        startActivity(intent)
    }

    private fun getUserPoints(){
        userViewModel.getMyPoints { isTokenValid, errorState, message, resultMyPoint ->
            if(!isTokenValid){
                navigateTo<LoginActivity>(true)
            }else{
                resultMyPoint?.let {
                    user_points?.text = "${NumberFormat.getIntegerInstance().format(it.points)} Points"
                }
            }
        }
    }

    private fun setMainCatList(){
        val mainLLManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        menu_main_categories_rv?.setHasFixedSize(true)
        menu_main_categories_rv?.layoutManager = mainLLManager
        menu_main_categories_rv?.adapter = mainCatAdapter
        menu_main_categories_rv?.layoutTransition?.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun setIconMenuList(){
        val iconLLManager = GridLayoutManager(this, 3, RecyclerView.VERTICAL, false)
        //val iconLLManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        icon_menu_rv?.setHasFixedSize(true)
        icon_menu_rv?.layoutManager = iconLLManager
        icon_menu_rv?.adapter = iconMenuAdapter
        icon_menu_rv?.layoutTransition?.enableTransitionType(LayoutTransition.CHANGING)
    }


    private fun setStaticSlider(){

        val bannerLLManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        banner_rv?.setHasFixedSize(true)
        banner_rv?.layoutManager = bannerLLManager
        banner_rv?.adapter = bannerMenuAdapter
        banner_rv?.layoutTransition?.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun setSliderContainer(){
        val dblWidth = getDisplayMetrics().widthPixels.toDouble()
        val height = (dblWidth * 0.45)
        home_slider_main_container?.layoutParams?.height = height.toInt()
        home_slider_main_container?.layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
    }

    private fun setClickListener(){
        menu_btn_signout?.setOnClickListener {
            MLPrefModel.clearAll()
            navigateTo<LoginActivity>(true)
        }

        menu_btn_course?.setOnClickListener {
            isContextMenuChanged = true
            contextMenu = MLContextMenu.COURSES
            drawer_layout?.closeDrawers()
        }

        menu_btn_home?.setOnClickListener {
            isContextMenuChanged = true
            contextMenu = MLContextMenu.HOME
            drawer_layout?.closeDrawers()
        }

        search_bar_button_home?.setOnClickListener {
            navigateTo<SearchActivity>()
        }

        menu_btn_discussion?.setOnClickListener {
            isContextMenuChanged = true
            contextMenu = MLContextMenu.DISCUSSION
            drawer_layout?.closeDrawers()
        }

        menu_btn_dashboard?.setOnClickListener {
            isContextMenuChanged = true
            contextMenu =MLContextMenu.DASHBOARD
            drawer_layout?.closeDrawers()
        }

        menu_btn_support?.setOnClickListener {
            isContextMenuChanged = true
            contextMenu = MLContextMenu.SUPPORT
            drawer_layout?.closeDrawers()
        }

        menu_btn_knowledge?.setOnClickListener {
            isContextMenuChanged = true
            contextMenu = MLContextMenu.KNOWLEDGE_FORUM
            drawer_layout?.closeDrawers()
        }

        menu_btn_inbox?.setOnClickListener{
            isContextMenuChanged = true
            contextMenu = MLContextMenu.INBOX
            drawer_layout?.closeDrawers()
        }
    }

    private fun getHomeRequests(){
        getSortedCourseData(MLESortCourseType.POPULAR)
        getSortedCourseData(MLESortCourseType.LATEST)
        getSortedCourseData(MLESortCourseType.TOPRATED)
        getHomeSlider()
        getStaticSlider()
        //getUserPoints()
    }

    private fun setUserProperties(){
        if(!isFinishing) {
            user_image?.loadURL(MLPrefModel.userImageLarge)
        }
        user_fullname?.text = if(MLPrefModel.userFullName.isEmpty()) "-" else{MLPrefModel.userFullName}
        user_job?.text = if(MLPrefModel.userDept.isEmpty()) "-" else{MLPrefModel.userDept}
        user_points?.text = "0 Points"
    }


    private fun setMenuExpandableAction(){
        toggleRV()
        menu_btn_course_cat?.setOnClickListener {
            if(!stateExpanded){
                if(menuItemCount > 0){
                    stateExpanded = true
                    course_cat_arrow_image?.animate()?.rotation(-180f)?.start()
                }
            }else{
                stateExpanded = false
                course_cat_arrow_image?.animate()?.rotation(0f)?.start()

            }
            toggleRV()
        }
    }

    private fun toggleRV(){
        val lParams = menu_main_categories_rv?.layoutParams
        lParams?.height = if(stateExpanded)ViewGroup.LayoutParams.WRAP_CONTENT else{0}
        menu_main_categories_rv?.layoutParams = lParams
    }



    fun getSortedCourseData(type:MLESortCourseType){
        toggleProgress(true,type)
        courseViewModel.getSortedCourse(type.name.toLowerCase()){isValidToken,errorState,message,response->
            if(!isValidToken){
                doLogout(message)
            }else{
                toggleProgress(false,type)
                response?.let {
                    /*if(!errorState) {
                        mapSortedCourseToView(type, it)
                    }else{
                        showToast(message)
                    }*/
                    mapSortedCourseToView(type, it)
                }?:run {
                    //showToast(message)
                    mapSortedCourseToView(type, null)
                }

            }
        }
    }


    private fun goToSubMainCourse(catId:Int,subCatName:String){
        val intent = Intent(this, SubCategoriesActivity::class.java)
        intent.putExtra(SubCategoriesActivity.courseCatIdName,catId)
        intent.putExtra(SubCategoriesActivity.courseCatName,subCatName)
        startActivity(intent)
    }

    private fun goToBannerWebView(data: ResultStaticSlider){
        val intent = Intent(this, BannerWebViewActivity::class.java)
        intent.putExtra("URL", data.url)
        intent.putExtra("NAME", data.name)
        startActivity(intent)
    }

    private fun getHomeSlider(){
        home_slider?.removeAllSliders()
        generalViewModel.getHomeSlider { isValidToken,errorState, message, resultHomeSlider ->
            home_refresh_layout?.isRefreshing = false
            if(!isValidToken){
                doLogout(message)
            }else{
                if(!errorState){
                    resultHomeSlider?.let {response->
                        for (res in response.rows){
                            home_slider?.addSlider(DefaultSliderView(this@HomeActivity)
                                .image(res.url)
                                .description("").setOnSliderClickListener {
                                    if(res.type == MLESliderType.COURSE.type){
                                        navigateToCourseDetail(res.id)
                                    }else if(res.type == MLESliderType.DISCUSSION.type){
                                        if(res.id <= 0){
                                            navigateTo<MainKnowledgeForumActivity>()
                                        }else {
                                            navigateToForumDetail("", res.id, "", "",false,false)
                                        }
                                    }else if(res.type == MLESliderType.CATEGORY_COURSE.type){
                                        //courseViewModel.selectedCat = res.id
                                        //goToMainCourse()
                                        goToSubMainCourse(res.id,"Loading...")
                                    } else{
                                        MLLog.showLog(classTag,"UnImplemented Slider Clicked")
                                    }
                                })
                        }

                        home_slider?.setPresetTransformer(SliderLayout.Transformer.Default)
                        //home_slider_container?.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
                        home_slider?.setCustomIndicator(custom_indicator)
                        home_slider?.setDuration(4000)
                        home_slider?.stopAutoCycle()
                        home_slider?.startAutoCycle(MLConfig.HomePagerSlideDelay,MLConfig.HomePagerSlideTime,true)


                    }
                }else{
                    showToast(message)
                }
            }
        }
    }

    private fun getStaticSlider(){
         generalViewModel.getStaticSlider{ isValidToken,errorState, message, resultStaticSlider ->
             if(!isValidToken){
                doLogout(message)
            }else{
                if(!errorState){
                    resultStaticSlider?.let {response->
                        val eachWidthCard = (this.getDisplayMetrics().widthPixels - this.dpToPx(40)) / 2
                        bannerMenuAdapter.clear()
                        bannerMenuAdapter.addAll(response.map { StaticSliderItem(it,eachWidthCard,this.dpToPx(160)){ slider ->
                            goToBannerWebView(slider)
                        }})
                    }
                }else{
                    showToast(message)
                }
            }
        }
    }

    private fun mapSortedCourseToView(type:MLESortCourseType,response:ResultSortedCourse?){
        val frag = HomeSortedCourseFragment.newInstance(type,response)

        supportFragmentManager?.let {sfm->
            val transaction = sfm.beginTransaction()
            when(type){
                MLESortCourseType.TOPRATED->{
                    if(sfm.findFragmentById(R.id.frame_top_rated) != null) {
                        transaction.replace(R.id.frame_top_rated, frag)
                    }else{
                        transaction.add(R.id.frame_top_rated, frag)
                    }

                }
                MLESortCourseType.POPULAR->{
                    if(sfm.findFragmentById(R.id.frame_popular) != null) {
                        transaction.replace(R.id.frame_popular, frag)
                    }else{
                        transaction.add(R.id.frame_popular, frag)
                    }
                }
                MLESortCourseType.LATEST->{
                    if(sfm.findFragmentById(R.id.frame_latest) != null) {
                        transaction.replace(R.id.frame_latest, frag)
                    }else{
                        transaction.add(R.id.frame_latest, frag)
                    }

                }
            }
            transaction.commitAllowingStateLoss()
                sfm.executePendingTransactions()

        }
    }

    private fun toggleProgress(state:Boolean,type:MLESortCourseType){
        when(type){
            MLESortCourseType.POPULAR->{
                frame_popular_progress?.visibility = if(state) View.VISIBLE else{View.GONE}
                frame_popular?.visibility = if(state) View.GONE else{View.VISIBLE}
            }
            MLESortCourseType.LATEST->{
                frame_latest_progress?.visibility = if(state) View.VISIBLE else{View.GONE}
                frame_latest?.visibility = if(state) View.GONE else{View.VISIBLE}
            }
            MLESortCourseType.TOPRATED->{
                frame_toprated_progress?.visibility = if(state) View.VISIBLE else{View.GONE}
                frame_top_rated?.visibility = if(state) View.GONE else{View.VISIBLE}
            }
        }
    }

    private fun doLogout(message:String=""){
        MLPrefModel.clearAll()
        showToast(message)
        navigateTo<LoginActivity>(true)
    }




}
