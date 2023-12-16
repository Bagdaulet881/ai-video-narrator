package kz.narrator.app.ui_common.base

import android.app.ProgressDialog
import android.view.View
import androidx.annotation.NonNull
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kz.narrator.app.R
import kz.narrator.app.di.utils.Injectable
import javax.inject.Inject

open class BaseBottomSheetDialogFragment : BottomSheetDialogFragment(), Injectable {

    /**
     * DI
     */
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected fun <T : ViewModel> getViewModel(modelClass: Class<T>): T {
        return ViewModelProviders.of(
            this,
            viewModelFactory
        ).get(modelClass)
    }

    protected fun <T : ViewModel> getViewModelOfActivity(modelClass: Class<T>): T {
        return ViewModelProviders.of(
            activity!!,
            viewModelFactory
        ).get(modelClass)
    }


    /**
     * Callback
     */
    protected fun setBottomSheetCallback(v: View) {
        var mBottomSheetBehavior = BottomSheetBehavior.from(v.parent as View)
        mBottomSheetBehavior?.setBottomSheetCallback(mBottomSheetBehaviorCallback)
    }

    private val mBottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {}
    }

    /**
     * Progress dialog
     */
    private var progressDialog: ProgressDialog? = null

    protected fun showProgressDialog() {
        if (progressDialog == null
            || !progressDialog!!.isShowing
        ) {
            progressDialog =
                ProgressDialog.show(context, "", getString(R.string.loading), true, false)
        }
    }

    protected fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    /**
     * Exception
     */
    protected fun handleException(exception: Exception?) {
        context?.let {
            ExceptionHandler.handleException(it, exception)
        }
    }

    protected fun handleExceptionView(exception: Exception?) {
        context?.let {
            ExceptionHandler.handleExceptionView(it, exception)
        }
    }

    protected fun handleExceptionDialog(exception: Exception?) {
        context?.let {
            ExceptionHandler.handleExceptionDialog(it, exception)
        }
    }

    /**
     *
     */
    protected fun getLifecycleOwner(): LifecycleOwner {
        return this
    }

}