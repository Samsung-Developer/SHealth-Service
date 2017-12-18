/**
 * Copyright (C) 2014 Samsung Electronics Co., Ltd. All rights reserved.
 *
 * Mobile Communication Division,
 * IT & Mobile Communications, Samsung Electronics Co., Ltd.
 *
 * This software and its documentation are confidential and proprietary
 * information of Samsung Electronics Co., Ltd.  No part of the software and
 * documents may be copied, reproduced, transmitted, translated, or reduced to
 * any electronic medium or machine-readable form without the prior written
 * consent of Samsung Electronics.
 *
 * Samsung Electronics makes no representations with respect to the contents,
 * and assumes no responsibility for any errors that might appear in the
 * software and documents. This publication and the contents hereof are subject
 * to change without notice.
 */

package com.samsung.android.app.sampleservice;

import com.samsung.android.sdk.shealth.Shealth;
import com.samsung.android.sdk.shealth.tracker.TrackerInfo;
import com.samsung.android.sdk.shealth.tracker.TrackerManager;
import com.samsung.android.sdk.shealth.tracker.TrackerTileManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final String LOG_TAG = "SampleService";
    private static final String STORE_URL = "market://details?id=com.sec.android.app.shealth";
    private static final String MY_TRACKER_ID = "tracker.sample";
    private TrackerManager mTrackerManager = null;

    private TextView mTileIdsTextView;
    private Button mRemoveButton;
    private TrackerTileManager mTrackerTileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Shealth shealth = new Shealth();
        try {
            shealth.initialize(this);
            if (shealth.isFeatureEnabled(Shealth.FEATURE_TRACKER_TILE, Shealth.FEATURE_TRACKER_LAUNCH_EXTENDED)) {
                mTrackerManager = new TrackerManager(this);
                mTrackerTileManager = new TrackerTileManager(this);
            } else {
                Log.d(LOG_TAG, "SHealth should be upgraded");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(STORE_URL));
                this.startActivity(intent);
                finish();
                return;
            }
        } catch (IllegalStateException e) {
            Log.e(LOG_TAG, e.toString());
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            finish();
            return;
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, e.toString());
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mRemoveButton = (Button) findViewById(R.id.btn_remove);
        mTileIdsTextView = (TextView) findViewById(R.id.txt_state);

        mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ArrayList<String> postedTileIds = mTrackerTileManager.getPostedTrackerTileIds(MY_TRACKER_ID);
                    if (postedTileIds != null) {
                        for (String tileId : postedTileIds) {
                            mTrackerTileManager.remove(MY_TRACKER_ID, tileId);
                        }
                        updatePostedTileIdsInfo();
                    }
                } catch (IllegalArgumentException e) {
                    Log.e(LOG_TAG, e.toString());
                }
            }
        });

        addStressMenu();
        addWaterMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePostedTileIdsInfo();
    }

    // Update Posted Tile Ids Information
    private void updatePostedTileIdsInfo() {
        ArrayList<String> tileIds = new ArrayList<String>();
        try {
            tileIds = mTrackerTileManager.getPostedTrackerTileIds(MY_TRACKER_ID);

            SpannableStringBuilder builder = new SpannableStringBuilder();
            if (tileIds.isEmpty()) {
                builder.append("No posted TrackerTile");
                mRemoveButton.setEnabled(false);
            } else {
                int start = builder.toString().length();
                for (String tileId : tileIds) {
                    builder.append(tileId);
                    builder.append(" ");
                }
                int end = builder.toString().length();
                builder.setSpan(new ForegroundColorSpan(Color.RED), start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                mRemoveButton.setEnabled(true);
            }

            mTileIdsTextView.setText(builder.toString());
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, e.toString());
        }
    }

    private void addStressMenu() {
        TrackerInfo trackerInfo = mTrackerManager.getTrackerInfo(TrackerManager.TrackerId.STRESS);

        if(trackerInfo == null) {
            return;
        }

        ((ImageView)findViewById(R.id.stress_icon_img)).setImageDrawable(trackerInfo.getIcon());
        ((TextView)findViewById(R.id.stress_service_name)).setText(trackerInfo.getDisplayName());

        findViewById(R.id.btn_stress_track).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mTrackerManager.startActivity((Activity)v.getContext(), TrackerManager.TrackerId.STRESS);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                } catch (IllegalStateException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btn_stress_trend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mTrackerManager.startActivity((Activity)v.getContext(), TrackerManager.TrackerId.STRESS, TrackerManager.Destination.TRENDS);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                } catch (IllegalStateException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addWaterMenu() {
        TrackerInfo trackerInfo = mTrackerManager.getTrackerInfo(TrackerManager.TrackerId.WATER);

        if(trackerInfo == null) {
            return;
        }

        ((ImageView)findViewById(R.id.water_icon_img)).setImageDrawable(trackerInfo.getIcon());
        ((TextView)findViewById(R.id.water_service_name)).setText(trackerInfo.getDisplayName());

        findViewById(R.id.btn_water_trend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mTrackerManager.startActivity((Activity)v.getContext(), TrackerManager.TrackerId.WATER,
                            TrackerManager.Destination.TRACK);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                } catch (IllegalStateException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btn_water_track).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mTrackerManager.startActivity((Activity)v.getContext(), TrackerManager.TrackerId.WATER,
                            TrackerManager.Destination.TRENDS);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                } catch (IllegalStateException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btn_water_target).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mTrackerManager.startActivity((Activity)v.getContext(), TrackerManager.TrackerId.WATER,
                            TrackerManager.Destination.TARGET);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                } catch (IllegalStateException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
