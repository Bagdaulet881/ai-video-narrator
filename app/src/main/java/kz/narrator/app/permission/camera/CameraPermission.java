package kz.narrator.app.permission.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;

import kz.narrator.app.R;
import kz.narrator.app.data.enums.app.PermissionRequestCodeEnums;
import kz.narrator.app.data.enums.app.RequestCodeEnums;
import kz.narrator.app.permission.base.OnPermissionGrantedListener;
import kz.narrator.app.permission.base.PermissionBase;

public class CameraPermission extends PermissionBase
        implements CameraPermissionView {

    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA
    };

    private static final String PERMISSION = Manifest.permission.CAMERA;

    public CameraPermission(
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
        presenter = new CameraPermissionPresenter(this);
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
        return PermissionRequestCodeEnums.CAMERA.getId();
    }

    @Override
    protected int getRequestCodeSettings() {
        return RequestCodeEnums.SETTINGS_CAMERA.getId();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.permission_rationale_title_camera);
    }

    @Override
    protected String getMessage() {
        return getString(R.string.permission_rationale_message_camera);
    }

    @Override
    protected String getSettingsTitle() {
        return getString(R.string.permission_settings_title_camera);
    }

    @Override
    protected String getSettingsMessage() {
        return getString(R.string.permission_settings_message_camera);
    }

    @Override
    protected String getPermissionNotGrantedText() {
        return getString(R.string.permission_not_granted_camera);
    }

}