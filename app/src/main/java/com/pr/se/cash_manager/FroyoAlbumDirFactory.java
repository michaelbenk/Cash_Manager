package com.pr.se.cash_manager;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;

import java.io.File;

final class FroyoAlbumDirFactory extends AlbumStorageDirFactory {

    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                albumName
        );
    }
}
