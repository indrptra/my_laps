package id.indosat.ml.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import id.indosat.ml.R
import id.indosat.ml.core.di.GlideApp
import id.indosat.ml.MyLearningApp
fun ImageView.loadURL(url:String,showPlaceHolder:Boolean=true){
    context.applicationContext?.let {
        /*GlideApp.with(it)
            .load(url)
            //.apply(RequestOptions())
            //.optionalCenterCrop()
            //.placeholder(if(showPlaceHolder)R.mipmap.ic_logo else{-1})
            .placeholder(if(showPlaceHolder)R.mipmap.ic_mylearning_mm else{-1})
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .signature(ObjectKey(url))
            //.signature(ObjectKey(System.currentTimeMillis()))
            .dontAnimate()
            .into(this)*/
        Glide.with(it).apply {
            RequestOptions
                .skipMemoryCacheOf(false)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .signature(ObjectKey(url))
                .dontAnimate()
                .placeholder(if(showPlaceHolder)R.mipmap.ic_mylearning_mm else{-1})
        }.load(url)
            .error(Glide.with(it).load(R.mipmap.ic_logo))
            .into(this)
    }

}