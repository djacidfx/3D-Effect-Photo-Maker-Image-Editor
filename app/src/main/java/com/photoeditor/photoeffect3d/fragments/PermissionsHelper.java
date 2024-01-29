package com.photoeditor.photoeffect3d.fragments;

import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class PermissionsHelper extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = PermissionsHelper.class.getCanonicalName();
    private static PermissionsHelper __instance;
    private String[] mPermissions = null;

    public interface PermissionsListener {
        void onPermissionsFailed(String[] strArr);

        void onPermissionsSatisfied();
    }

    public static PermissionsHelper getInstance() {
        if (__instance == null) {
            __instance = new PermissionsHelper();
            __instance.setRetainInstance(true);
        }
        return __instance;
    }

    public void setRequestedPermissions(String... permissions) {
        this.mPermissions = permissions;
    }

    public boolean checkPermissions() {
        if (this.mPermissions == null) {
            throw new RuntimeException("String[] permissions is null, please call setRequestedPermissions!");
        } else if (!hasSelfPermission(this.mPermissions) || getParent() == null) {
            requestPermissions(this.mPermissions, PERMISSION_REQUEST_CODE);
            return false;
        } else {
            getParent().onPermissionsSatisfied();
            return true;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult() " + Arrays.toString(permissions) + Arrays.toString(grantResults));
        if (requestCode != PERMISSION_REQUEST_CODE || getParent() == null) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else if (verifyPermissions(grantResults)) {
            getParent().onPermissionsSatisfied();
        } else {
            getParent().onPermissionsFailed(getFailedPermissions(permissions, grantResults));
        }
    }

    private String[] getFailedPermissions(String[] permissions, int[] grantResults) {
        ArrayList<String> failedPermissions = new ArrayList();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == -1) {
                failedPermissions.add(permissions[i]);
            }
        }
        return (String[]) failedPermissions.toArray(new String[failedPermissions.size()]);
    }

    public static boolean isMorHigher() {
        return VERSION.SDK_INT >= 23;
    }

//    public static <ParentFrag extends Fragment & PermissionsListener> PermissionsHelper attach(FragmentActivity parent) {
//        return attach(parent.getChildFragmentManager());
//    }

    public static <ParentActivity extends FragmentActivity & PermissionsListener> PermissionsHelper attach(ParentActivity parent) {
        return attach(parent.getSupportFragmentManager());
    }

    private static PermissionsHelper attach(FragmentManager fragmentManager) {
        PermissionsHelper frag = (PermissionsHelper) fragmentManager.findFragmentByTag(TAG);
        if (frag != null) {
            return frag;
        }
        frag = getInstance();
        fragmentManager.beginTransaction().add(frag, TAG).commit();
        return frag;
    }

    private PermissionsListener getParent() {
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof PermissionsListener) {
            return (PermissionsListener) parentFragment;
        }
        FragmentActivity activity = getActivity();
        if (activity instanceof PermissionsListener) {
            return (PermissionsListener) activity;
        }
        return null;
    }

    public boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != 0) {
                return false;
            }
        }
        return true;
    }

    public boolean hasSelfPermission(String[] permissions) {
        for (String permission : permissions) {
            if (VERSION.SDK_INT >= 23 && getActivity().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public boolean hasSelfPermission(String permission) {
        if (VERSION.SDK_INT < 23 || getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }
}
