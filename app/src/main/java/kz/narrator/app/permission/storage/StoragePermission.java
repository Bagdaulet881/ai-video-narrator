package kz.narrator.app.permission.storage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;

import kz.narrator.app.R;
import kz.narrator.app.data.enums.app.PermissionRequestCodeEnums;
import kz.narrator.app.data.enums.app.RequestCodeEnums;
import kz.narrator.app.permission.base.OnPermissionGrantedListener;
import kz.narrator.app.permission.base.PermissionBase;

public class StoragePermission extends PermissionBase
        implements StoragePermissionView {

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final String PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    public StoragePermission(
            Context context,
            Activity activity,
            Fragment fragment,
            OnPermissionGrantedListener onPermissionGrantedListener,
            boolean isMandatory
    ) {
        super(context, activity, fragment, onPermissionGrantedListener, isMandatory);
    }

    @Override
    protected void initPresenter() {
        presenter = new StoragePermissionPresenter(this);
    }

    @Override
    protected String getManifestPermission() {
        return PERMISSION;
    }

    @Override
    protected String[] getManifestPermissions() {
        return PERMISSIONS;
    }

    @Override
    protected int getRequestCode() {
        return PermissionRequestCodeEnums.STORAGE.getId();
    }

    @Override
    protected int getRequestCodeSettings() {
        return RequestCodeEnums.SETTINGS_STORAGE.getId();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.permission_rationale_title_storage);
    }

    @Override
    protected String getMessage() {
        return getString(R.string.permission_rationale_message_storage);
    }

    @Override
    protected String getSettingsTitle() {
        return getString(R.string.permission_settings_title_storage);
    }

    @Override
    protected String getSettingsMessage() {
        return getString(R.string.permission_settings_message_storage);
    }

    @Override
    protected String getPermissionNotGrantedText() {
        return getString(R.string.permission_not_granted_storage);
    }

}