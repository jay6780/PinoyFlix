package com.m.freemovie.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;

public class FileUtil {
    /**
     * 获取文件扩展名
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1).toLowerCase();
            }
        }
        return filename;
    }

    /**
     * 获取不带扩展名的文件名
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * 从文件存储路径中获取文件名
     */
    public static String getFileNameFromPath(String path){
        if (path!=null&&path.length()>0){
            int dot=path.lastIndexOf('/');
            if (dot>-1&&dot<path.length()){
                return path.substring(dot+1);
            }
        }
        return path;
    }

    /**
     * 从文件存储路径中获取当前目录
     */
    public static String getDirFromPath(String path){
        if (path!=null&&path.length()>0){
            int dot=path.lastIndexOf('/');
            if (dot>-1&&dot<path.length()){
                return path.substring(0,dot);
            }
        }
        return path;
    }

    public static Intent getFileData(File file, Context context) {
        Intent intent;
        String type = getMIMEType(file);

        if (type.equals("text/html") || type.equals("application/zip") ||
                type.equals("application/x-rar-compressed")) {
            intent = new Intent("com.zbm.dainty.action.VIEW");
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
        }

        Log.i("tag", "type=" + type);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(context,
                    context.getPackageName() + ".fileprovider", file);
            intent.setDataAndType(contentUri, type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            Uri fileUri = Uri.fromFile(file);
            intent.setDataAndType(fileUri, type);
        }

        return intent;
    }

    public static Intent getFileIntent(File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent ;
        String type = getMIMEType(file);
        if (type.equals("text/html")||type.equals("application/zip")||
                type.equals("application/x-rar-compressed"))
            intent = new Intent("com.zbm.dainty.action.VIEW");
        else
            intent = new Intent("android.intent.action.VIEW");
        Log.i("tag", "type=" + type);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, type);
        return intent;
    }

    private static String getMIMEType(File f) {
        String type;
        String fName = f.getName();
        /* 取得扩展名 */
        String end = getExtensionName(fName);

        /* 依扩展名的类型决定MimeType */
        switch (end) {
            case "pdf":
                type = "application/pdf";
                break;

            case "m4a":
            case "mp3":
            case "mid":
            case "xmf":
            case "ogg":
            case "wav":
                type = "audio/*";
                break;

            case "3gp":
            case "mp4":
                type = "video/*";
                break;

            case "jpg":
            case "gif":
            case "png":
            case "jpeg":
            case "bmp":
                type = "image/*";
                break;

            case "apk":
                /* android.permission.INSTALL_PACKAGES */
                type = "application/vnd.android.package-archive";
                break;
            case "htm":
            case "html":
            case "jsp":
            case "php":
            case "xml":
                type="text/html";
                break;
            case "txt":
                type="text/plain";
                break;
            case "zip":
                type="application/zip";
                break;
            case "doc":
            case "docx":
                type="application/msword";
                break;
            case "xls":
            case "xlsx":
                type="application/vnd.ms-excel";
                break;
            case "ppt":
            case "pptx":
                type="application/vnd.ms-powerpoint";
                break;
            case "7z":
                type="application/x-7z-compressed";
                break;
            case "rar":
                type="application/x-rar-compressed";
                break;
            default:
                /*如果无法直接打开，就跳出软件列表给用户选择 */
                type = "*/*";
                break;
        }
        return type;
    }
}
