package kz.narrator.app.ui.attach_files

interface AttachFilesCallback {

    fun onAttachFileBtnClick()

    fun clearAttachedFiles()

    fun getAttachedFiles(): List<Any>

    fun setOldAttachedFiles(list: List<Any>)

    fun showNotSelectedError()

    fun showError(error: String?)

}