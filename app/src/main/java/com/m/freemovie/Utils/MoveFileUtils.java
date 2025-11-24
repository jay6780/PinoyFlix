package com.m.freemovie.Utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MoveFileUtils {
    private Context context;
    private File sourceFile;
    private String subFolderName;
    private boolean success;

    public MoveFileUtils(Context context, File sourceFile, String subFolderName) {
        this.context = context;
        this.sourceFile = sourceFile;
        this.subFolderName = subFolderName;
        moveFile();
    }

    private void moveFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            success = moveFileUsingMediaStore();
        } else {
            success = moveFileToPublicDCIM();
        }

        if (success) {
            if (sourceFile.exists()) {
                sourceFile.delete();
            }
        }
    }

    private boolean moveFileUsingMediaStore() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, sourceFile.getName());
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
            String relativePath = Environment.DIRECTORY_DCIM + File.separator + subFolderName;
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath);

            Uri videoCollection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

            try {
                Uri outputUri = resolver.insert(videoCollection, contentValues);

                if (outputUri != null) {
                    try (InputStream inputStream = new FileInputStream(sourceFile);
                         OutputStream outputStream = resolver.openOutputStream(outputUri)) {

                        if (outputStream != null) {
                            byte[] buffer = new byte[4096];
                            int length;
                            while ((length = inputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, length);
                            }
                            return true;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Failed to move file: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    private boolean moveFileToPublicDCIM() {
        File dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File targetDir = new File(dcimDir, subFolderName);

        if (!targetDir.exists()) {
            if (!targetDir.mkdirs()) {
                Toast.makeText(context, "Failed to create directory", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        File destinationFile = new File(targetDir, sourceFile.getName());

        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(destinationFile)) {

            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            scanFile(context, destinationFile);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to move file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void scanFile(Context context, File file) {
        Uri contentUri = Uri.fromFile(file);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
}