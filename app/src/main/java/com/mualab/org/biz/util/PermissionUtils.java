package com.mualab.org.biz.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.mualab.org.biz.helper.Constants;


/**
 * Created by hemant
 * Date: 12/12/17
 */

public final class PermissionUtils {

    private PermissionUtils() {
        // This permission class is not publicly instantiable
    }

    //Permission function starts from here
    public static boolean RequestMultiplePermissionCamera(Activity activity) {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (FirstPermissionResult != PackageManager.PERMISSION_GRANTED | SecondPermissionResult != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            // Creating String Array with Permissions.
            ActivityCompat.requestPermissions(activity, new String[]
                    {
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                    }, Constants.REQUEST_MULTIPLE_CAMERA_PERMISSIONS);
            return false;
        } else {
            return true;
        }
    }

    public static boolean RequestMultiplePermission(Activity activity) {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);

        if (FirstPermissionResult != PackageManager.PERMISSION_GRANTED | SecondPermissionResult != PackageManager.PERMISSION_GRANTED | ThirdPermissionResult != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            // Creating String Array with Permissions.
            ActivityCompat.requestPermissions(activity, new String[]
                    {
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                    }, Constants.REQUEST_MULTIPLE_PERMISSIONS);
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkLocationPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.MY_PERMISSIONS_REQUEST_LOCATION);

            return false;
        } else {
            return true;
        }
    }

    public static boolean checkCameraPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA},
                    Constants.MY_PERMISSIONS_REQUEST_CAMERA);
            return false;
        } else {
            return true;
        }
    }

}

