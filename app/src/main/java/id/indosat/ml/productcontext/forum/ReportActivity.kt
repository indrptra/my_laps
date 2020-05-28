package id.indosat.ml.productcontext.forum

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import dagger.android.AndroidInjection
import id.indosat.ml.R
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.common.coordinator.navigateTo
import id.indosat.ml.common.model.response.ReportDiscussion
import id.indosat.ml.productcontext.auth.LoginActivity
import id.indosat.ml.util.MLLog
import id.indosat.ml.util.showToast
import id.indosat.ml.viewmodel.MLForumViewModel
import kotlinx.android.synthetic.main.content_popup_report.*

class ReportActivity : BaseActivity() {
    private lateinit var forumViewModel: MLForumViewModel

    override fun onDestroy() {
        super.onDestroy()
        forumViewModel.clear()
        try {
            generalViewModel.clear()
        }catch (e:Exception){
            MLLog.showLog(classTag,e.localizedMessage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        supportActionBar?.title = "REPORT DISCUSSION"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f

        forumViewModel = ViewModelProviders.of(this,viewModelFactory).get(MLForumViewModel::class.java)

        intent.getParcelableExtra<ReportDiscussion>("data")?.let { r ->

            var strings = arrayOf("Inappropriate", "Spam")

            val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, strings)
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sp_category!!.adapter = aa

            et_subject?.setText(r.subject)

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                et_message?.setText(Html.fromHtml(r.message, Html.FROM_HTML_MODE_LEGACY))
            }else{
                et_message?.setText(Html.fromHtml(r.message))
            }

            et_reason?.setText("")

            btnSubmit?.setOnClickListener{
                val category = sp_category?.selectedItem!!.toString()
                val reason = et_reason?.text!!.toString()

                if (reason.isEmpty()) {
                    showToast("Please fill reason field")
                    return@setOnClickListener
                }

                forumViewModel.reportForum(r.id,r.discussionid, category, reason){isTokenValid, errorState, message, result->
                    if(!isTokenValid){
                        navigateTo<LoginActivity>(true)
                    }else{
                        if(!errorState){
                            val forumIntent = Intent()
                            forumIntent.putExtra("data", result?.message)
                            setResult(Activity.RESULT_OK, forumIntent)
                            finish()
                        }else if(result == null){
                            Toast.makeText(this, "Something Wrong, Please try again later", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            btnCancel?.setOnClickListener{
                val forumIntent = Intent()
                setResult(Activity.RESULT_CANCELED, forumIntent)
                finish()
            }
        }
    }
}
