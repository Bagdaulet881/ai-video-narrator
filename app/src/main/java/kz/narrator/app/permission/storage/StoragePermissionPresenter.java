package kz.narrator.app.permission.storage;

import kz.narrator.app.permission.base.PermissionBasePresenter;

public class StoragePermissionPresenter extends PermissionBasePresenter {

    private StoragePermissionView view;

    public StoragePermissionPresenter(StoragePermissionView view) {
        super(view);
        this.view = view;
    }

}