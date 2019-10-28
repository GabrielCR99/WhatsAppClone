package com.cursoandroid.whatsappclone.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permission {

    public static boolean validatePermission(int requestCode, Activity activity, String[] permissions) {

        if (Build.VERSION.SDK_INT >= 23) {

            List<String> permissionList = new ArrayList<String>();
            /*
            Percorre as permissoes passadas, verificando uma a uma
            se ja tem a permissao liberada
             */

            for (String permission : permissions) {
                boolean validatePermission = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
                if (!validatePermission) permissionList.add(permission);
            }

            /*
            caso a lista esteja vazia
             */

            if (permissionList.isEmpty()) return true;

            String[] newPermission = new String[permissionList.size()];
            permissionList.toArray(newPermission);


            //solicita permissao
            ActivityCompat.requestPermissions(activity, newPermission, requestCode);


        }
        return true;
    }
}
