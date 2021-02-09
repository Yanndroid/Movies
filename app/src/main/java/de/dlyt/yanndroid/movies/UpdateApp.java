package de.dlyt.yanndroid.movies;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;

public class UpdateApp {
    public static void DownloadAndInstall(Context context, String url, String fileName, String NotiTitle, String NotiDescription) {
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName;
        File file = new File(destination);

        if (file.exists()) {
            file.delete();
        }

        final Uri uri = Uri.parse("file://" + destination);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(NotiDescription);
        request.setTitle(NotiTitle);
        request.setDestinationUri(uri);

        final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = manager.enqueue(request);

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {

                Uri apkfileuri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File("/storage/emulated/0/Download/" + fileName));
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                install.setDataAndType(apkfileuri, "application/vnd.android.package-archive");
                context.startActivity(install);

                context.unregisterReceiver(this);
            }
        };
        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
}
