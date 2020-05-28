package id.indosat.ml.common.coordinator

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Parcelable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import id.indosat.ml.R
import id.indosat.ml.common.model.MLPrefModel
import id.indosat.ml.productcontext.auth.LoginActivity
import id.indosat.ml.productcontext.course.CourseDetailActivity
import id.indosat.ml.productcontext.course.ScormCourseActivity
import id.indosat.ml.productcontext.course.VideoCourseDetailActivity
import id.indosat.ml.productcontext.forum.AdvanceEditor
import id.indosat.ml.productcontext.forum.DetailForumActivity


inline fun <reified T : Activity> Activity.navigateTo(shouldFinished:Boolean=false, data:Any?=null,
                                                      dataParamName:String="data") {
    val intent = Intent(this, T::class.java)
    data?.let {
        if (it is Parcelable) {
            intent.putExtra(dataParamName, it)
        }
    }
    if(intent.component?.className ?: "" == LoginActivity::class.java.name){
        MLPrefModel.clearAll()
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
    }else{
        startActivity(intent)
        //overridePendingTransition(R.anim.enter,R.anim.exit)
    }

    if(shouldFinished){
        finish()
    }
}

inline fun <reified T : Activity> Activity.navigateToWithResult(resultCode:Int, data:Any?=null,
                                                                dataParamName:String="data") {
    val intent = Intent(this, T::class.java)
    data?.let {
        if (it is Parcelable) {
            intent.putExtra(dataParamName, it)
        }
    }

    startActivityForResult(intent,resultCode)
}

inline fun <reified T : Activity> Fragment.navigateToWithResult(resultCode:Int, data:Any?=null,
                                                                                      dataParamName:String="data") {
    this.activity?.let {fAct->
        val intent = Intent(fAct, T::class.java)
        data?.let {
            if (it is Parcelable) {
                intent.putExtra(dataParamName, it)
            }
        }
        startActivityForResult(intent,resultCode)
    }
}

fun Activity.navigateToCourseDetail(courseId:Int,dataParamName:String=CourseDetailActivity.courseID, directStart:Boolean=false) {
    this.let {fAct->
        val intent = Intent(fAct, CourseDetailActivity::class.java)
        intent.putExtra(dataParamName,courseId)
        intent.putExtra(CourseDetailActivity.KEY_DIRECT_START, directStart)
        startActivity(intent)
    }
}

fun Activity.navigateToYoutube(moduleId:Int,courseId: Int,url:String,dataParamName:String=VideoCourseDetailActivity.videoURLName) {
    this.let {fAct->
        val intent = Intent(fAct, VideoCourseDetailActivity::class.java)
        intent.putExtra("moduleId", moduleId)
        intent.putExtra("courseId", courseId)
        intent.putExtra(dataParamName,url)
        startActivity(intent)
    }
}

fun Activity.navigateToScorm(url:String,courseTitle:String,
                             dataParamName:String=ScormCourseActivity.scormUrlPathName,
                             dataCourseTitleParam:String=ScormCourseActivity.scormCourseTitle) {
    this.let {fAct->
        val intent = Intent(fAct, ScormCourseActivity::class.java)
        intent.putExtra(dataParamName,url)
        intent.putExtra(dataCourseTitleParam,courseTitle)
        startActivity(intent)
    }
}

fun Activity.navigateToAdvanceEditor(dataStringKeyVal:MutableMap<String,String>?=null,
                                     dataIntKeyVal:MutableMap<String,Int>?=null,
                                     advanceEditResultCommand:Int=AdvanceEditor.AdvanceEditorCommentRequest, resultComment:String, resultHeader:String, category: String, _discussionId: Int, postId : String, isComment: Boolean) {
    this.let {fAct->
        val intent = Intent(fAct, AdvanceEditor::class.java)
        dataStringKeyVal?.let {
            for(dpk in it){
                intent.putExtra(dpk.key, dpk.value)
            }
        }

        dataIntKeyVal?.let {
            for(dpk in it){
                intent.putExtra(dpk.key, dpk.value)
            }
        }
        if(!resultComment.isNullOrEmpty())intent.putExtra(AdvanceEditor.resultComment,resultComment) else intent.putExtra(AdvanceEditor.resultComment,"")
        if(!resultHeader.isNullOrEmpty())intent.putExtra(AdvanceEditor.resultHeader,resultHeader) else intent.putExtra(AdvanceEditor.resultHeader,"")
        intent.putExtra(AdvanceEditor.categoryPost, category)
        intent.putExtra(AdvanceEditor.discussionID, _discussionId.toString())
        intent.putExtra(AdvanceEditor.postIdComment, postId)
        intent.putExtra(AdvanceEditor.isComment, isComment)

        //startActivity(intent)
        startActivityForResult(intent,advanceEditResultCommand)
    }
}

fun Activity.navigateToForumDetail(title:String,forumId:Int,category:String,sortBy:String, canEdit: Boolean,canDelete: Boolean) {
    this.let {fAct->
        val intent = Intent(fAct, DetailForumActivity::class.java)
        intent.putExtra(DetailForumActivity.detailTitle,title)
        intent.putExtra(DetailForumActivity.detailId,forumId)
        intent.putExtra(DetailForumActivity.detailCategory,category)
        intent.putExtra(DetailForumActivity.detailSortBy,sortBy)
        intent.putExtra(DetailForumActivity.canEdit,canEdit)
        intent.putExtra(DetailForumActivity.detailSortBy,canDelete)
        startActivity(intent)
    }
}

fun Activity.getProgressDialog(isCancelable:Boolean=true):Pair<AlertDialog,View>{
    val builder = AlertDialog.Builder(this)
    val layout = layoutInflater.inflate(R.layout.content_progress_dialog,null)
    builder.setCancelable(isCancelable)
    builder.setView(layout)
    val returnedDialog = builder.create()
    val dialogWindow = returnedDialog?.window
    dialogWindow?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    dialogWindow?.setBackgroundDrawableResource(android.R.color.transparent)
    dialogWindow?.setGravity(Gravity.CENTER)
    return Pair(returnedDialog,layout)//(builder.create(),layout)
}
