package kz.narrator.app.permission.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import kz.narrator.app.R;
import kz.narrator.app.permission.utils.PermissionUtils;

public abstract class PermissionBase implements PermissionBaseView {

    private Context context;
    private Activity activity;
    private Fragment fragment;
    private boolean isMandatory;

    private OnPermissionGrantedListener onPermissionGrantedListener;

    protected PermissionBasePresenter presenter;

    public PermissionBase(
            Context context,
            Activity activity,
            Fragment fragment,
            OnPermissionGrantedListener onPermissionGrantedListener,
            boolean isMandatory
    ) {
        this.context = context;
        this.activity = activity;
        this.fragment = fragment;
        this.onPermissionGrantedListener = onPermissionGrantedListener;
        this.isMandatory = isMandatory;

        initPresenter();
    }

    protected abstract void initPresenter();

    //call this from fragment
    public void checkPermission() {
        presenter.onRequestPermission();
    }

    private Context getContext() {
        return context;
    }

    private Activity getActivity() {
        return activity;
    }

    protected String getString(int resId) {
        return context.getString(resId);
    }

    @Override
    public boolean isPermissionGranted() {
        return PermissionUtils.isPermissionGranted(
                getContext(),
                getManifestPermission()
        );
    }

    @Override
    public boolean shouldShowRequestPermissionRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(
                getActivity(),
                getManifestPermission()
        );
    }

    @Override
    public void showRequestPermissionRationale(boolean isMandatory) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getContext())
                        .setCancelable(!isMandatory)
                        .setTitle(getTitle())
                        .setMessage(getMessage())
                        .setPositiveButton(getString(R.string.permission_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                presenter.onPermissionRationaleOkClick();
                                dialog.cancel();
                            }
                        });

        if (isMandatory) {
            builder
                    .setNegativeButton(getString(R.string.permission_exit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            presenter.onPermissionRationaleCancelClick();
                            dialog.cancel();
                        }
                    });
        }

        builder
                .create()
                .show();
    }

    @Override
    public void requestPermission() {
        //if from fragment
        if (fragment != null
                && fragment.isAdded()) {
            fragment.requestPermissions(
                    getManifestPermissions(), getRequestCode()

            );
        }
        //if from activity implement code
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == getRequestCode()) {

            if (PermissionUtils.verifyPermissions(grantResults)) {
                presenter.onPermissionGranted();

            } else {
                presenter.onPermissionNotGranted();
            }
        }
    }

    @Override
    public void showPermissionSettingsDialog(boolean isMandatory) {
        new AlertDialog.Builder(getContext())
                .setCancelable(!isMandatory)
                .setTitle(getSettingsTitle())
                .setMessage(getSettingsMessage())
                .setPositiveButton(getString(R.string.permission_go_to_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onPermissionSettingsOkClick();
                        dialog.cancel();
                    }
                })
                .setNegativeButton(
                        isMandatory ? getString(R.string.permission_exit) : getString(R.string.permission_not_now),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                presenter.onPermissionSettingsCancelClick();
                                dialog.cancel();
                            }
                        })
                .create()
                .show();
    }

    @Override
    public void openSettingsActivity() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
        intent.setData(uri);
        fragment.startActivityForResult(intent, getRequestCodeSettings());
    }

    @Override
    public void showPermissionNotGranted() {
        Toast.makeText(getContext(), getPermissionNotGrantedText(), Toast.LENGTH_SHORT).show();
        //to do
//        ToastUtils.showError(getContext(), getPermissionNotGrantedText(), activity.getWindow());
    }

    @Override
    public void callOnPermissionGrantedListener() {
        onPermissionGrantedListener.onPermissionGranted();
    }

    @Override
    public void callOnPermissionNotGrantedListener() {
        onPermissionGrantedListener.onPermissionNotGranted();
    }

    @Override
    public void callOnPermissionExitListener() {
        onPermissionGrantedListener.onPermissionExit();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == getRequestCodeSettings()) {
            presenter.onSettingsActivityResult();
        }
    }

    @Override
    public boolean isMandatory() {
        return isMandatory;
    }

    /**
     * ABSTRACT METHODS
     */
    protected abstract String getManifestPermission();

    protected abstract String[] getManifestPermissions();

    protected abstract int getRequestCode();

    protected abstract int getRequestCodeSettings();

    /**
     * STRINGS
     */
    protected abstract String getTitle(); //

    protected abstract String getMessage(); //

    protected abstract String getSettingsTitle(); //

    protected abstract String getSettingsMessage(); //

    protected abstract String getPermissionNotGrantedText(); //

}