package kz.narrator.app.permission.base;

public interface OnPermissionGrantedListener {

    void onPermissionGranted();

    void onPermissionNotGranted();

    void onPermissionExit();

}