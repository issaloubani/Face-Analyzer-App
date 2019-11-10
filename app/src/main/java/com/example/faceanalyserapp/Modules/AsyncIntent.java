package com.example.faceanalyserapp.Modules;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.example.faceanalyserapp.AnalyzationActivity;

public class AsyncIntent extends AsyncTask<Void, Void, Void> {

    Bitmap currentBitmap;
    Context context;

    public AsyncIntent(Context context, Bitmap bitmap) {
        this.currentBitmap = bitmap;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Intent intentToAnalyzeActivity = new Intent(context, AnalyzationActivity.class);
        intentToAnalyzeActivity.putExtra(
                "currentFrameBitmap", currentBitmap);

        intentToAnalyzeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.context.startActivity(intentToAnalyzeActivity);


    }
}
