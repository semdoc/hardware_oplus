/*
 * Copyright (C) 2016 The OmniROM Project
 *               2022 The Evolution X Project
 * SPDX-License-Identifier: GPL-2.0-or-later
 */

package com.aicp.oplus.OplusParts.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;
import android.util.AttributeSet;

import com.aicp.oplus.OplusParts.OplusParts;
import com.aicp.oplus.OplusParts.Utils;
import com.aicp.oplus.OplusParts.R;

public class VibratorStrengthPreference extends CustomSeekBarPreference {

    private static int mDefVal;
    private Vibrator mVibrator;

    private static final int NODE = R.string.node_vibrator_strength_preference;
    private static long testVibrationPattern[];

    public VibratorStrengthPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInterval = context.getResources().getInteger(R.integer.vibrator_strength_preference_interval);
        mShowSign = false;
        mUnits = "";
        mContinuousUpdates = false;

        int[] mAllValues = context.getResources().getIntArray(R.array.vibrator_strength_preference_array);
        mMinValue = mAllValues[1];
        mMaxValue = mAllValues[2];
        mDefaultValueExists = true;
        mDefVal = mAllValues[0];
        mDefaultValue = mDefVal;
        mValue = Integer.parseInt(loadValue(context));

        int[] tempVibrationPattern = context.getResources().getIntArray(R.array.test_vibration_pattern);
        testVibrationPattern = new long[tempVibrationPattern.length];
        for (int i = 0; i < tempVibrationPattern.length; i++) {
            testVibrationPattern[i] = tempVibrationPattern[i];
        }

        setPersistent(false);

        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private static String getFile(Context context) {
        String file = context.getString(NODE);
        if (Utils.fileWritable(file)) {
            return file;
        }
        return null;
    }

    public static boolean isSupported(Context context) {
        return Utils.fileWritable(getFile(context));
    }

    public static void restore(Context context) {
        if (!isSupported(context)) {
            return;
        }

        int[] mAllValues = context.getResources().getIntArray(R.array.vibrator_strength_preference_array);
        mDefVal = mAllValues[0];
        String storedValue = PreferenceManager.getDefaultSharedPreferences(context).getString(OplusParts.KEY_VIBSTRENGTH, String.valueOf(mDefVal));
        Utils.writeValue(getFile(context), storedValue);
    }

    public static String loadValue(Context context) {
        return Utils.getFileValue(getFile(context), String.valueOf(mDefVal));
    }

    private void saveValue(String newValue) {
        Utils.writeValue(getFile(getContext()), newValue);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putString(OplusParts.KEY_VIBSTRENGTH, newValue);
        editor.apply();
        mVibrator.vibrate(testVibrationPattern, -1);
    }

    @Override
    protected void changeValue(int newValue) {
        saveValue(String.valueOf(newValue));
    }
}
