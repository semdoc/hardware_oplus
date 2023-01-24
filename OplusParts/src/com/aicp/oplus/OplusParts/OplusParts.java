/*
 * Copyright (C) 2018-2022 crDroid Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aicp.oplus.OplusParts;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import androidx.preference.TwoStatePreference;

import java.util.Arrays;

import com.aicp.oplus.OplusParts.Constants;
import com.aicp.oplus.OplusParts.modeswitch.*;
import com.aicp.oplus.OplusParts.preferences.*;
import com.android.internal.util.aicp.FileUtils;

public class OplusParts extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = OplusParts.class.getSimpleName();

    public static final String KEY_CATEGORY_DISPLAY = "display";
    public static final String KEY_KCAL = "kcal";

    public static final String KEY_CATEGORY_MISC = "misc";
    public static final String KEY_QUIET_MODE_SWITCH = "quiet_mode";

    public static final String KEY_CATEGORY_UIBENCH = "uibench";
    public static final String KEY_JITTER = "jitter";

    public static final String KEY_CATEGORY_CPU = "cpu";
    public static final String KEY_POWER_EFFICIENT_WQ_SWITCH = "power_efficient_workqueue";

    private ListPreference mTopKeyPref;
    private ListPreference mMiddleKeyPref;
    private ListPreference mBottomKeyPref;

    private static TwoStatePreference mQuietModeSwitch;
    private static TwoStatePreference mPowerEfficientWorkqueueModeSwitch;

    public static final String KEY_CATEGORY_VIBRATOR = "vibrator";
    public static final String KEY_VIBSTRENGTH = "vib_strength";
    private VibratorStrengthPreference mVibratorStrength;

    public static final String KEY_CATEGORY_USB = "usb";
    public static final String KEY_USB2_SWITCH = "usb2_fast_charge";
    private static TwoStatePreference mUSB2FastChargeModeSwitch;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.main);

        Context context = this.getContext();

        // Display
        boolean displayCategory = false;

        // Kcal
        displayCategory = displayCategory | isFeatureSupported(context, R.bool.config_deviceSupportsKcal);
        if (isFeatureSupported(context, R.bool.config_deviceSupportsKcal)) {
        }
        else {
            findPreference(KEY_KCAL).setVisible(false);
        }

        if (!displayCategory) {
            getPreferenceScreen().removePreference((Preference) findPreference(KEY_CATEGORY_DISPLAY));
        }

        // CPU category
        boolean cpuCategory = false;

        // Power Efficient Workqueue
        cpuCategory = cpuCategory | isFeatureSupported(context, R.bool.config_deviceSupportsPowerEfficientWorkqueue);
        if (isFeatureSupported(context, R.bool.config_deviceSupportsPowerEfficientWorkqueue)) {
            mPowerEfficientWorkqueueModeSwitch = (TwoStatePreference) findPreference(KEY_POWER_EFFICIENT_WQ_SWITCH);
            mPowerEfficientWorkqueueModeSwitch.setEnabled(PowerEfficientWorkqueueModeSwitch.isSupported(this.getContext()));
            mPowerEfficientWorkqueueModeSwitch.setChecked(PowerEfficientWorkqueueModeSwitch.isCurrentlyEnabled(this.getContext()));
            mPowerEfficientWorkqueueModeSwitch.setOnPreferenceChangeListener(new PowerEfficientWorkqueueModeSwitch());
        }
        else {
           findPreference(KEY_POWER_EFFICIENT_WQ_SWITCH).setVisible(false);
        }

        // Misc Settings
        boolean miscCategory = false;

        // Quiet mode
        miscCategory = miscCategory | isFeatureSupported(context, R.bool.config_deviceSupportsQuietMode);
        if (isFeatureSupported(context, R.bool.config_deviceSupportsQuietMode)) {
            mQuietModeSwitch = (TwoStatePreference) findPreference(KEY_QUIET_MODE_SWITCH);
            mQuietModeSwitch.setEnabled(QuietModeSwitch.isSupported(this.getContext()));
            mQuietModeSwitch.setOnPreferenceChangeListener(new QuietModeSwitch());
        }
        else {
           findPreference(KEY_QUIET_MODE_SWITCH).setVisible(false);
        }

        if (!miscCategory) {
            getPreferenceScreen().removePreference((Preference) findPreference(KEY_CATEGORY_MISC));
        }

        // UiBench
        boolean uibenchCategory = false;

        // Jitter Test
        uibenchCategory = uibenchCategory | isFeatureSupported(context, R.bool.config_deviceSupportsJitterTest);
        if (isFeatureSupported(context, R.bool.config_deviceSupportsJitterTest)) {
        }
        else {
            findPreference(KEY_JITTER).setVisible(false);
        }

        if (!uibenchCategory) {
            getPreferenceScreen().removePreference((Preference) findPreference(KEY_CATEGORY_UIBENCH));
        }

        boolean usbCategory = false;

        // USB2 Force FastCharge
        usbCategory = usbCategory | isFeatureSupported(context, R.bool.config_deviceSupportsUSB2FC);
        if (isFeatureSupported(context, R.bool.config_deviceSupportsUSB2FC)) {
            mUSB2FastChargeModeSwitch = (TwoStatePreference) findPreference(KEY_USB2_SWITCH);
            mUSB2FastChargeModeSwitch.setEnabled(USB2FastChargeModeSwitch.isSupported(this.getContext()));
            mUSB2FastChargeModeSwitch.setChecked(USB2FastChargeModeSwitch.isCurrentlyEnabled(this.getContext()));
            mUSB2FastChargeModeSwitch.setOnPreferenceChangeListener(new USB2FastChargeModeSwitch());
        }
        else {
           findPreference(KEY_USB2_SWITCH).setVisible(false);
        }

        if (!usbCategory) {
            getPreferenceScreen().removePreference((Preference) findPreference(KEY_CATEGORY_USB));
        }

        boolean vibratorCategory = false;

        // Vibrator
        vibratorCategory = vibratorCategory | isFeatureSupported(context, R.bool.config_deviceSupportsSysVib);
        if (isFeatureSupported(context, R.bool.config_deviceSupportsSysVib)) {
            mVibratorStrength = (VibratorStrengthPreference) findPreference(KEY_VIBSTRENGTH);
            if (mVibratorStrength != null) {
                mVibratorStrength.setEnabled(VibratorStrengthPreference.isSupported(this.getContext()));
            }
        }
        else {
            findPreference(KEY_VIBSTRENGTH).setVisible(false);
        }

        if (!vibratorCategory) {
            getPreferenceScreen().removePreference((Preference) findPreference(KEY_CATEGORY_VIBRATOR));
        }

        initNotificationSliderPreference();
    }

    private void initNotificationSliderPreference() {
        registerPreferenceListener(Constants.NOTIF_SLIDER_USAGE_KEY);
        registerPreferenceListener(Constants.NOTIF_SLIDER_ACTION_TOP_KEY);
        registerPreferenceListener(Constants.NOTIF_SLIDER_ACTION_MIDDLE_KEY);
        registerPreferenceListener(Constants.NOTIF_SLIDER_ACTION_BOTTOM_KEY);

        ListPreference usagePref = (ListPreference) findPreference(
                Constants.NOTIF_SLIDER_USAGE_KEY);
        handleSliderUsageChange(usagePref.getValue());
    }

    private void registerPreferenceListener(String key) {
        Preference p = findPreference(key);
        p.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        switch (key) {
            case Constants.NOTIF_SLIDER_USAGE_KEY:
                return handleSliderUsageChange((String) newValue) &&
                        handleSliderUsageDefaultsChange((String) newValue) &&
                        notifySliderUsageChange((String) newValue);
            case Constants.NOTIF_SLIDER_ACTION_TOP_KEY:
                return notifySliderActionChange(0, (String) newValue);
            case Constants.NOTIF_SLIDER_ACTION_MIDDLE_KEY:
                return notifySliderActionChange(1, (String) newValue);
            case Constants.NOTIF_SLIDER_ACTION_BOTTOM_KEY:
                return notifySliderActionChange(2, (String) newValue);
            default:
                break;
        }

        String node = Constants.sBooleanNodePreferenceMap.get(key);
        if (!TextUtils.isEmpty(node) && FileUtils.isFileWritable(node)) {
            Boolean value = (Boolean) newValue;
            FileUtils.writeLine(node, value ? "1" : "0");
            return true;
        }
        node = Constants.sStringNodePreferenceMap.get(key);
        if (!TextUtils.isEmpty(node) && FileUtils.isFileWritable(node)) {
            FileUtils.writeLine(node, (String) newValue);
            return true;
        }

        return false;
    }

    @Override
    public void addPreferencesFromResource(int preferencesResId) {
        super.addPreferencesFromResource(preferencesResId);
        // Initialize node preferences
        for (String pref : Constants.sBooleanNodePreferenceMap.keySet()) {
            SwitchPreference b = (SwitchPreference) findPreference(pref);
            if (b == null) continue;
            String node = Constants.sBooleanNodePreferenceMap.get(pref);
            if (FileUtils.isFileReadable(node)) {
                String curNodeValue = FileUtils.readOneLine(node);
                b.setChecked(curNodeValue.equals("1"));
                b.setOnPreferenceChangeListener(this);
            } else {
                removePref(b);
            }
        }
        for (String pref : Constants.sStringNodePreferenceMap.keySet()) {
            ListPreference l = (ListPreference) findPreference(pref);
            if (l == null) continue;
            String node = Constants.sStringNodePreferenceMap.get(pref);
            if (FileUtils.isFileReadable(node)) {
                l.setValue(FileUtils.readOneLine(node));
                l.setOnPreferenceChangeListener(this);
            } else {
                removePref(l);
            }
        }
    }

    private void removePref(Preference pref) {
        PreferenceGroup parent = pref.getParent();
        if (parent == null) {
            return;
        }
        parent.removePreference(pref);
        if (parent.getPreferenceCount() == 0) {
            removePref(parent);
        }
    }

    private boolean handleSliderUsageChange(String newValue) {
        switch (newValue) {
            case Constants.NOTIF_SLIDER_FOR_NOTIFICATION:
                return updateSliderActions(
                        R.array.notification_slider_mode_entries,
                        R.array.notification_slider_mode_entry_values);
            case Constants.NOTIF_SLIDER_FOR_FLASHLIGHT:
                return updateSliderActions(
                        R.array.notification_slider_flashlight_entries,
                        R.array.notification_slider_flashlight_entry_values);
            case Constants.NOTIF_SLIDER_FOR_BRIGHTNESS:
                return updateSliderActions(
                        R.array.notification_slider_brightness_entries,
                        R.array.notification_slider_brightness_entry_values);
            case Constants.NOTIF_SLIDER_FOR_ROTATION:
                return updateSliderActions(
                        R.array.notification_slider_rotation_entries,
                        R.array.notification_slider_rotation_entry_values);
            case Constants.NOTIF_SLIDER_FOR_RINGER:
                return updateSliderActions(
                        R.array.notification_slider_ringer_entries,
                        R.array.notification_slider_ringer_entry_values);
            case Constants.NOTIF_SLIDER_FOR_NOTIFICATION_RINGER:
                return updateSliderActions(
                        R.array.notification_ringer_slider_mode_entries,
                        R.array.notification_ringer_slider_mode_entry_values);
            case Constants.NOTIF_SLIDER_FOR_REFRESH:
                return updateSliderActions(
                        R.array.notification_slider_refresh_entries,
                        R.array.notification_slider_refresh_entry_values);
            case Constants.NOTIF_SLIDER_FOR_EXTRADIM:
                return updateSliderActions(
                        R.array.notification_slider_extradim_entries,
                        R.array.notification_slider_extradim_entry_values);
            case Constants.NOTIF_SLIDER_FOR_NIGHTLIGHT:
                return updateSliderActions(
                        R.array.notification_slider_nightlight_entries,
                        R.array.notification_slider_nightlight_entry_values);
            case Constants.NOTIF_SLIDER_FOR_COLORSPACE:
                return updateSliderActions(
                        R.array.notification_slider_colorspace_entries,
                        R.array.notification_slider_colorspace_entry_values);
            default:
                return false;
        }
    }

    private boolean handleSliderUsageDefaultsChange(String newValue) {
        int defaultsResId = getDefaultResIdForUsage(newValue);
        if (defaultsResId == 0) {
            return false;
        }
        return updateSliderActionDefaults(defaultsResId);
    }

    private boolean updateSliderActions(int entriesResId, int entryValuesResId) {
        String[] entries = getResources().getStringArray(entriesResId);
        String[] entryValues = getResources().getStringArray(entryValuesResId);
        return updateSliderPreference(Constants.NOTIF_SLIDER_ACTION_TOP_KEY,
                entries, entryValues) &&
            updateSliderPreference(Constants.NOTIF_SLIDER_ACTION_MIDDLE_KEY,
                    entries, entryValues) &&
            updateSliderPreference(Constants.NOTIF_SLIDER_ACTION_BOTTOM_KEY,
                    entries, entryValues);
    }

    private boolean updateSliderActionDefaults(int defaultsResId) {
        String[] defaults = getResources().getStringArray(defaultsResId);
        if (defaults.length != 3) {
            return false;
        }

        return updateSliderPreferenceValue(Constants.NOTIF_SLIDER_ACTION_TOP_KEY,
                defaults[0]) &&
            updateSliderPreferenceValue(Constants.NOTIF_SLIDER_ACTION_MIDDLE_KEY,
                    defaults[1]) &&
            updateSliderPreferenceValue(Constants.NOTIF_SLIDER_ACTION_BOTTOM_KEY,
                    defaults[2]);
    }

    private boolean updateSliderPreference(CharSequence key,
            String[] entries, String[] entryValues) {
        ListPreference pref = (ListPreference) findPreference(key);
        if (pref == null) {
            return false;
        }
        pref.setEntries(entries);
        pref.setEntryValues(entryValues);
        return true;
    }

    private boolean updateSliderPreferenceValue(CharSequence key,
            String value) {
        ListPreference pref = (ListPreference) findPreference(key);
        if (pref == null) {
            return false;
        }
        pref.setValue(value);
        return true;
    }

    private int[] getCurrentSliderActions() {
        int[] actions = new int[3];
        ListPreference p;

        p = (ListPreference) findPreference(
                Constants.NOTIF_SLIDER_ACTION_TOP_KEY);
        actions[0] = Integer.parseInt(p.getValue());

        p = (ListPreference) findPreference(
                Constants.NOTIF_SLIDER_ACTION_MIDDLE_KEY);
        actions[1] = Integer.parseInt(p.getValue());

        p = (ListPreference) findPreference(
                Constants.NOTIF_SLIDER_ACTION_BOTTOM_KEY);
        actions[2] = Integer.parseInt(p.getValue());

        return actions;
    }

    private boolean notifySliderUsageChange(String usage) {
        sendUpdateBroadcast(getActivity().getApplicationContext(), Integer.parseInt(usage),
                getCurrentSliderActions());
        return true;
    }

    private boolean notifySliderActionChange(int index, String value) {
        ListPreference p = (ListPreference) findPreference(
                Constants.NOTIF_SLIDER_USAGE_KEY);
        int usage = Integer.parseInt(p.getValue());

        int[] actions = getCurrentSliderActions();
        actions[index] = Integer.parseInt(value);

        sendUpdateBroadcast(getActivity().getApplicationContext(), usage, actions);
        return true;
    }

    public static void sendUpdateBroadcast(Context context,
            int usage, int[] actions) {
        Intent intent = new Intent(Constants.ACTION_UPDATE_SLIDER_SETTINGS);
        intent.putExtra(Constants.EXTRA_SLIDER_USAGE, usage);
        intent.putExtra(Constants.EXTRA_SLIDER_ACTIONS, actions);
        intent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        context.sendBroadcastAsUser(intent, UserHandle.CURRENT);
        Log.d(TAG, "update slider usage " + usage + " with actions: " +
                Arrays.toString(actions));
    }

    public static void restoreSliderStates(Context context) {
        Resources res = context.getResources();
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName() + "_preferences", Context.MODE_PRIVATE);

        String usage = prefs.getString(Constants.NOTIF_SLIDER_USAGE_KEY,
                res.getString(R.string.config_defaultNotificationSliderUsage));

        int defaultsResId = getDefaultResIdForUsage(usage);
        if (defaultsResId == 0) {
            return;
        }

        String[] defaults = res.getStringArray(defaultsResId);
        if (defaults.length != 3) {
            return;
        }

        String actionTop = prefs.getString(
                Constants.NOTIF_SLIDER_ACTION_TOP_KEY, defaults[0]);

        String actionMiddle = prefs.getString(
                Constants.NOTIF_SLIDER_ACTION_MIDDLE_KEY, defaults[1]);

        String actionBottom = prefs.getString(
                Constants.NOTIF_SLIDER_ACTION_BOTTOM_KEY, defaults[2]);

        prefs.edit()
            .putString(Constants.NOTIF_SLIDER_USAGE_KEY, usage)
            .putString(Constants.NOTIF_SLIDER_ACTION_TOP_KEY, actionTop)
            .putString(Constants.NOTIF_SLIDER_ACTION_MIDDLE_KEY, actionMiddle)
            .putString(Constants.NOTIF_SLIDER_ACTION_BOTTOM_KEY, actionBottom)
            .commit();

        sendUpdateBroadcast(context, Integer.parseInt(usage), new int[] {
            Integer.parseInt(actionTop),
            Integer.parseInt(actionMiddle),
            Integer.parseInt(actionBottom)
        });
    }

    private static boolean isFeatureSupported(Context ctx, int feature) {
        try {
            return ctx.getResources().getBoolean(feature);
        }
        // TODO: Replace with proper exception type class
        catch (Exception e) {
            return false;
        }
    }

    private static int getDefaultResIdForUsage(String usage) {
        switch (usage) {
            case Constants.NOTIF_SLIDER_FOR_NOTIFICATION:
                return R.array.config_defaultSliderActionsForNotification;
            case Constants.NOTIF_SLIDER_FOR_FLASHLIGHT:
                return R.array.config_defaultSliderActionsForFlashlight;
            case Constants.NOTIF_SLIDER_FOR_BRIGHTNESS:
                return R.array.config_defaultSliderActionsForBrightness;
            case Constants.NOTIF_SLIDER_FOR_ROTATION:
                return R.array.config_defaultSliderActionsForRotation;
            case Constants.NOTIF_SLIDER_FOR_RINGER:
                return R.array.config_defaultSliderActionsForRinger;
            case Constants.NOTIF_SLIDER_FOR_NOTIFICATION_RINGER:
                return R.array.config_defaultSliderActionsForNotificationRinger;
            case Constants.NOTIF_SLIDER_FOR_REFRESH:
                return R.array.config_defaultSliderActionsForRefresh;
            case Constants.NOTIF_SLIDER_FOR_EXTRADIM:
                return R.array.config_defaultSliderActionsForExtraDim;
            case Constants.NOTIF_SLIDER_FOR_NIGHTLIGHT:
                return R.array.config_defaultSliderActionsForNightLight;
            case Constants.NOTIF_SLIDER_FOR_COLORSPACE:
                return R.array.config_defaultSliderActionsForColorSpace;
            default:
                return 0;
        }
    }
}
