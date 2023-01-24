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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import androidx.preference.PreferenceManager;

import com.aicp.oplus.OplusParts.Utils;
import com.aicp.oplus.OplusParts.modeswitch.*;
import com.aicp.oplus.OplusParts.preferences.*;

public class Startup extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(final Context context, final Intent bootintent) {

        OplusParts.restoreSliderStates(context);
        BluePreference.restore(context);
        ContrastPreference.restore(context);
        GreenPreference.restore(context);
        HuePreference.restore(context);
        RedPreference.restore(context);
        SaturationPreference.restore(context);
        ValuePreference.restore(context);
        VibratorStrengthPreference.restore(context);

        boolean enabled = false;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        enabled = sharedPrefs.getBoolean(OplusParts.KEY_POWER_EFFICIENT_WQ_SWITCH, false);
        if (enabled) {
            restore(PowerEfficientWorkqueueModeSwitch.getFile(context), enabled);
               }
        enabled = sharedPrefs.getBoolean(OplusParts.KEY_QUIET_MODE_SWITCH, false);
        if (enabled) {
            restore(QuietModeSwitch.getFile(context), enabled);
        }
        enabled = sharedPrefs.getBoolean(OplusParts.KEY_USB2_SWITCH, false);
        if (enabled) {
        restore(USB2FastChargeModeSwitch.getFile(context), enabled);
        }
    }

    private void restore(String file, boolean enabled) {
        if (file == null) {
            return;
        }
        if (enabled) {
            Utils.writeValue(file, "1");
        }
    }

    private void restore(String file, String value) {
        if (file == null) {
            return;
        }
        Utils.writeValue(file, value);
    }
}
