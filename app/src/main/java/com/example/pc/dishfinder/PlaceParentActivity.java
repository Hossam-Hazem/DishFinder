package com.example.pc.dishfinder;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import layout.PlaceImageFragment;
import layout.PlaceReviewsFragment;

public abstract class PlaceParentActivity extends AppCompatActivity {


    //TODO
    public void openReviewsFragment(Bundle bundle) {
        showDialog(PlaceReviewsFragment.newInstance(), bundle);
    }
//
//    public void openTrailersFragment(Bundle bundle) {
//        showDialog(MovieTrailersFragment.newInstance(), bundle);
//    }
//
    public void openImageFragment(Bundle bundle) {
        showDialog(PlaceImageFragment.newInstance(), bundle);
    }

    private void showDialog(DialogFragment newFragment) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        closeOpenedDialog(ft);

        // Create and show the dialog.
        newFragment.show(ft, "dialog");

    }

    private void showDialog(DialogFragment newFragment, Bundle bundle) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        closeOpenedDialog(ft);

        newFragment.setArguments(bundle);
        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    private void closeOpenedDialog(FragmentTransaction ft) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
    }

    //TODO
//    public Intent createShareMovieIntent(String name, String url) {
//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//        shareIntent.setType("text/plain");
//        shareIntent.putExtra(Intent.EXTRA_TITLE, name);
//        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
//        return shareIntent;
//    }
}
