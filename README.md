# Halcyon's FM Tuner a.k.a. Radio

Halcyon's FM Tuner a.k.a. Radio (`com.hlcyn.tuner`) is an FM radio application for Android (AOSP, LineageOS, and Halcyon), forked from [iusmac's RevampedFMRadio](https://github.com/iusmac/RevampedFMRadio) and built on the LineageOS / AOSP FMRadio stack.

It is a privileged system app built with Jetpack Compose for devices that have built-in FM radio hardware, kernel FM drivers, and FM audio routing.

## Features

### Design and theming
- Adapts to your device's wallpaper colors in Light and Dark themes using Material 3.
- Generates distinct cover art and a soft backdrop glow for each station from your active theme colors.
- Uses variable typography backed by Google Sans Flex when present on the ROM, with a Reduce motion setting to disable spring and text animations.
- Includes translations across 87 locales and supports Android predictive back navigation.

### Tuning and playback
- Tune with a segmented frequency slider, 0.1 MHz step buttons, or automatic Previous and Next channel scanning.
- Switch output between headphones and the phone speaker with one tap. Wired headphones still act as the antenna on devices that do not have an internal FM antenna.
- Displays RDS station names and live radio text when broadcast by the station.

### Broadcast recording and library
- Record live radio in AAC (`.m4a`) or Opus (`.ogg`) at 128, 192, 256, or 320 kbps, saved to a configurable folder.
- Browse saved clips grouped by date in the built-in Recordings screen with an in-app audio player, skip controls, scrubbing bar, and rename or share actions.
- Live FM playback pauses automatically while you listen to a saved recording.

### Favorites and settings
- Save favorite stations, assign custom names, enter frequencies manually, reorder the list, and confirm deletions before removing entries.
- Configure Auto-play on startup, Reduce motion, recording format, bitrate, and storage folder from a system-styled Settings screen.

## Device Integration Guide

> [!NOTE]
> Halcyon's FM Tuner a.k.a. Radio (`FMTuner`) is included by default in Halcyon AOSP builds. If you already build Halcyon AOSP from source, skip the manifest step below.

Because `FMTuner` declares `overrides: ["FMRadio", "RevampedFMRadio"]` and `required: ["privapp_whitelist_com.hlcyn.tuner.xml", "RevampedFmRecordingsProvider"]` in `Android.bp`, you do not need to remove stock `FMRadio` repositories from your manifest. Your device tree still needs working kernel FM drivers and FM audio routing in `audio_policy_configuration.xml`.

### 1. Manifest configuration
Add the repository to a local manifest (for example `.repo/local_manifests/fmtuner.xml`), matching `revision` to your ROM branch:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<manifest>
  <project name="halcyonproject/packages_halcyon_apps_FMTuner"
           path="packages/halcyon/apps/FMTuner"
           remote="github"
           revision="17.0" />
</manifest>
```

### 2. Base device configuration
Include `FMTuner` in `device.mk`:

```make
PRODUCT_PACKAGES += \
    FMTuner
```

At runtime, `FmNative` loads the first available JNI library in order: `libmtkfmjni`, `libqcomfmjni`, `libslsifmjni`, then `libfmjni`. This repository builds `libmtkfmjni`, `libqcomfmjni`, and `libslsifmjni` from `jni/Android.bp`. When none of those three modules are included in `PRODUCT_PACKAGES`, `FmNative` falls back to the platform `libfmjni` library.

### Qualcomm (QCOM) SoCs

1. Add the Qualcomm JNI library to `device.mk`:
   ```make
   PRODUCT_PACKAGES += \
       libqcomfmjni
   ```
   If your target does not load FM firmware from the JNI (matching LineageOS `TARGET_NO_FM_FIRMWARE`), set the Soong config variable in `BoardConfig.mk`:
   ```make
   $(call soong_config_set,libfmjni,no_fm_firmware,true)
   ```

2. Set the Bluetooth transport property in `vendor.prop` (replace `cherokee` with your SoC transport, such as `hastings` or `pronto`):
   ```properties
   vendor.qcom.bluetooth.soc=cherokee
   ```
   For legacy audio HAL mixer routing, also set:
   ```properties
   ro.vendor.fm.use_audio_session=true
   ```

3. Allow `system_app` to read the Bluetooth property and talk to the FM HAL in `sepolicy/vendor/system_app.te`:
   ```
   get_prop(system_app, vendor_bluetooth_prop)
   hal_client_domain(system_app, vendor_hal_fm)
   ```

### MediaTek (MTK) SoCs

1. Add the MediaTek JNI library to `device.mk`:
   ```make
   PRODUCT_PACKAGES += \
       libmtkfmjni
   ```

2. Remove any legacy prebuilt `libfmcust` or `libfmjni.so` from your device tree. `libmtkfmjni` reads `ro.fm.chip` and `persist.vendor.connsys.fm_chipid` (including combined chip IDs such as `mt6631_6635`), along with standard LineageOS `ro.fm.*` calibration properties.

### Samsung Exynos (SLSI) SoCs

1. Add the SLSI JNI library to `device.mk` (built from `jni/slsi/`):
   ```make
   PRODUCT_PACKAGES += \
       libslsifmjni
   ```

2. Ensure your kernel V4L2 FM driver exposes `/dev/radio0` with `system_app` SELinux access and FM audio routing configured in `audio_policy_configuration.xml`.

The upstream SLSI V4L2 controller supports manual frequency tuning, Previous and Next channel seek, and RDS (`PS` and `RT`). Full-band `autoScan` is not implemented in the upstream SLSI controller, so use Previous/Next seek or manual frequency entry to find stations. Verify on device with:

```sh
adb logcat -s FMLIB_SLSI_JNI
```

### General or LineageOS `libfmjni`

Use this path if your device already uses upstream LineageOS `packages/apps/FMRadio/jni/fmr`, `vendor/qcom/opensource/libfmjni`, `hardware/samsung_slsi/fm`, Broadcom FM, or a device-specific prebuilt `libfmjni.so`.

1. Omit `libmtkfmjni`, `libqcomfmjni`, and `libslsifmjni`, and include `libfmjni` alongside `FMTuner` in `device.mk`:
   ```make
   PRODUCT_PACKAGES += \
       FMTuner \
       libfmjni
   ```

2. When building upstream LineageOS `libfmjni`, select the vendor backend in `BoardConfig.mk` if needed:
   ```make
   # For Qualcomm targets using LineageOS vendor/qcom/opensource/libfmjni
   $(call soong_config_set,libfmjni,vendor,qcom)

   # For Samsung Exynos targets using LineageOS hardware/samsung_slsi/fm
   $(call soong_config_set,libfmjni,vendor,slsi)

   # Leave libfmjni vendor unset for property-based MediaTek libfmjni
   ```

3. Because `FMTuner` runs as a privileged system app (`privileged: true`), any prebuilt `libfmjni.so` must be installed in `/system/lib64` or `/system_ext/lib64` (or listed in `public.libraries.txt`), not only in `/vendor/lib64`.

`FmNative` keeps the upstream MediaTek `libfmjni` method declarations and guards optional low-power mode calls (`setLowPowerMode` and `setNormalPowerMode`), so both 16-method and 26-method `libfmjni` libraries load without `NoSuchMethodError` or `UnsatisfiedLinkError`. Verify which JNI library loaded with:

```sh
adb logcat | grep -E "FMLIB|FmNative"
```

## Credits

- [iusmac (Max)](https://github.com/iusmac): Original RevampedFMRadio architecture and QCOM/MTK JNI ports.
- [The LineageOS Project](https://github.com/LineageOS) and AOSP: Base FMRadio implementation, Soong `libfmjni` options (Michael Bestas), MediaTek `FmNative` JNI bindings (Ash Blake), property-based MTK config (bengris32), and Samsung SLSI FM JNI (`Jan Altensen`, `TO21Consortium`).
- [The Halcyon Project](https://github.com/halcyonproject): Material 3 Compose UI, theming, typography, and Opus recording.

## License

Licensed under Apache 2.0 and the GNU General Public License, following AOSP, LineageOS FMRadio, and RevampedFMRadio.
