package kz.narrator.app.ui_common.base

import android.app.ProgressDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kz.narrator.app.R
import kz.narrator.app.di.utils.Injectable
import javax.inject.Inject

open class BaseDialogFragment : DialogFragment(), Injectable {

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

}