package com.example.testapp.clipboardtranslator.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;

import com.example.testapp.clipboardtranslator.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Util {
    private static final String TAG = Util.class.getSimpleName();

    public static String readString(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void copyContentResolverToFile(Context context, Uri uri, File file) throws IOException {
        InputStream in = new BufferedInputStream(context.getContentResolver().openInputStream(uri));
        FileOutputStream out = new FileOutputStream(file);
        Util.copyAndClose(in, out);
    }

    public static void copyAndClose(InputStream in, OutputStream out) throws IOException {
        byte[] buff = new byte[4 * 1024];
        while (true) {
            int read = in.read(buff);
            if (read < 0) break;
            out.write(buff, 0, read);
        }
        closeQuietly(in);
        closeQuietly(out);
    }

    public static void closeQuietly(Closeable c) {
        try {
            c.close();
        } catch (IOException e) {
            Log.w(TAG, e.toString(), e);
        }
    }

    public static void showMsgBox(Context context, String msg) {
        showOKCancelMsgBox(context, msg, null, false);
    }
    public static void showOKCancelMsgBox(Context context, String msg, final Runnable okHandler) {
        showOKCancelMsgBox(context, msg, okHandler, true);
    }
    private static void showOKCancelMsgBox(Context context, String msg, final Runnable okHandler, boolean showCancelButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.app_name);
        builder.setMessage(msg);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (okHandler != null) okHandler.run();
            }
        });
        if (showCancelButton) {
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                /* */
                }
            });
        }
        builder.setCancelable(true);
        builder.create().show();
    }

    abstract public static class Notifier {
        abstract public void notify(String msg);
    }
}
