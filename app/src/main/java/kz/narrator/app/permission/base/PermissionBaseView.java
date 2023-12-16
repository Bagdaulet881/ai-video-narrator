package kz.narrator.app.permission.base;

public interface PermissionBaseView {

    boolean isPermissionGranted();

    boolean shouldShowRequestPermissionRationale();

    void showRequestPermissionRationale(boolean isMandatory);

    void requestPermission();

    void showPermissionSettingsDialog(boolean isMandatory);

    void openSettingsActivity();

    void showPermissionNotGranted();

    void callOnPermissionGrantedListener();

    void callOnPermissionNotGrantedListener();

    void callOnPermissionExitListener();

    boolean isMandatory();

}