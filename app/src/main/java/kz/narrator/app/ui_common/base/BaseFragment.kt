package kz.narrator.app.ui_common.base

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kz.narrator.app.R
import kz.narrator.app.di.utils.Injectable
import javax.inject.Inject

open class BaseFragment : Fragment(), Injectable {

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
            requireActivity(),
            viewModelFactory
        ).get(modelClass)
    }

    /**
     * Toolbar title
     */
    protected fun setToolbarTitle(title: String) {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity)?.supportActionBar?.title = title
        }
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

    protected fun showErrorDialog(title: String, message: String) {
        context?.let {
            ExceptionHandler.showErrorDialog(it, title, message)
        }
    }

}