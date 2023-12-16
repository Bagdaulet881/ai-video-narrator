package kz.narrator.app.permission.camera;

import kz.narrator.app.permission.base.PermissionBasePresenter;

public class CameraPermissionPresenter extends PermissionBasePresenter {

    private CameraPermissionView view;

    public CameraPermissionPresenter(CameraPermissionView view) {
        super(view);
        this.view = view;
    }

}