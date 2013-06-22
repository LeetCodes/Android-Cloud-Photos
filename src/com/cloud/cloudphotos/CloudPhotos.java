package com.cloud.cloudphotos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

public class CloudPhotos extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_photos);
        Intent ws = new Intent(this, BackgroundService.class);
        startService(ws);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        Log.v("CloudPhotos", this.getApplicationContext().getPackageName().toString());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cloud_photos, menu);
        return true;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            switch (position) {
            case 0:
                fragment = new CloudPhotosFragment_Main();
            case 1:
                fragment = new CloudPhotosFragment_StorageProviders();
            case 2:
                fragment = new CloudPhotosFragment_Settings();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
            case 0:
                return getString(R.string.my_cloudphotos);
            case 1:
                return getString(R.string.storageproviders);
            case 2:
                return getString(R.string.settings);
            }
            return null;
        }
    }

    /**
     * Represents the Main Page Fragment
     */
    public static class CloudPhotosFragment_Main extends Fragment {

        public CloudPhotosFragment_Main() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.activity_cloud_photos_main, container, false);

            return rootView;
        }
    }

    /**
     * Represents the Storage Providers Page Fragment
     */
    public static class CloudPhotosFragment_StorageProviders extends Fragment {

        public CloudPhotosFragment_StorageProviders() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.activity_cloud_photos_storage_providers, container, false);

            return rootView;
        }
    }

    /**
     * Represents the Settings Page Fragment
     */
    public static class CloudPhotosFragment_Settings extends Fragment {

        public CloudPhotosFragment_Settings() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.activity_cloud_photos_settings, container, false);

            return rootView;
        }
    }

}
