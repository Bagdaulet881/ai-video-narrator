package kz.narrator.app.permission.base;

public class PermissionBasePresenter {

    private PermissionBaseView view;

    public PermissionBasePresenter(PermissionBaseView view) {
        this.view = view;
    }

    public void onRequestPermission() {
        if (view.isPermissionGranted()) {
            view.callOnPermissionGrantedListener();

        } else {

            if (view.shouldShowRequestPermissionRationale()
                    || view.isMandatory()) {
                view.showRequestPermissionRationale(view.isMandatory());

            } else {
                view.requestPermission();
            }
        }
    }

    public void onPermissionRationaleOkClick() {
        view.requestPermission();
    }

    public void onPermissionRationaleCancelClick() {
        view.callOnPermissionExitListener();
    }

    public void onPermissionGranted() {
        view.callOnPermissionGrantedListener();
    }

    public void onPermissionNotGranted() {
        if (!view.shouldShowRequestPermissionRationale()) {
            view.showPermissionSettingsDialog(view.isMandatory());

        } else {
            view.showPermissionNotGranted();
            view.callOnPermissionNotGrantedListener();
        }
    }

    public void onPermissionSettingsOkClick() {
        view.openSettingsActivity();
    }

    public void onPermissionSettingsCancelClick() {
        if (view.isMandatory()) {
            view.callOnPermissionExitListener();
        }
    }

    public void onSettingsActivityResult() {
        view.requestPermission();
    }

}