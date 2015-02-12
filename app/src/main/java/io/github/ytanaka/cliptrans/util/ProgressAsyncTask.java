package io.github.ytanaka.cliptrans.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

abstract public class ProgressAsyncTask {
    private ProgressDialog progress;
    private MyTask task;
    protected Util.Notifier notifier;

    public ProgressAsyncTask(Context context, String title) {
        progress = new ProgressDialog(context);
        progress.setTitle(title);
        progress.setMessage("");
        progress.setCancelable(false);
        progress.show();

        task = new MyTask();
        notifier = new Util.Notifier() {
            @Override
            public void notify(String msg) {
                task.showProgressMsg(msg);
            }
        };
        task.execute((Object)null);
    }

    protected abstract void run();
    protected void finished() {

    }

    private class MyTask extends AsyncTask<Object, String, String> {
        void showProgressMsg(String msg) {
            publishProgress(msg);
        }
        @Override
        protected String doInBackground(Object... params) {
            run();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progress.setMessage(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            finished();
        }
    }
}
