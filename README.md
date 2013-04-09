# OpenVBX for Android  [![Google Play](http://developer.android.com/images/brand/en_generic_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=org.openvbx)

This repository contains the source code for the OpenVBX Android app.

Please see the [issues](https://github.com/chadsmith/OpenVBX-Android/issues) section to report any bugs or feature requests and to see the list of known issues.

## Requirements

* OpenVBX 0.90 or higher
* Android 2.2 or higher

## Building From Eclipse

* Import the repo folder as an Android project. The `.project` included will cause it to have the name `org.openvbx`.
* Create a `libs` folder at the root of the imported project.
* Copy the following JAR files to the newly created `libs` folder:
  * [android-async-http-1.4.3.jar](https://github.com/loopj/android-async-http/tree/master/releases)
* Clone the [Android-PullToRefresh](https://github.com/chrisbanes/Android-PullToRefresh) repository
  * Checkout tag `2.1.1`
  * Import the `library` folder as an Android project named `PullToRefresh`

The `org.openvbx` project should now build and be launchable and with no errors. If you still see errors try running the `Project > Clean...` menu and/or restart Eclipse.

You might find that your device doesn't let you install your build if you already have the version from the Android Market installed.  This is standard Android security as it it won't let you directly replace an app that's been signed with a different key.  Manually uninstall OpenVBX from your device and you will then be able to install your own built version.

## Trademarks

"OpenVBX" and "Twilio" are trademarks of Twilio, Inc., all rights reserved. If you want to redistribute this Android app, you must come up with your own product name. Use of the Twilio trademarks in your product name requires Twilio's written permission. This version of OpenVBX for Android was released with permission from Twilio, Inc.

## Acknowledgements

This project uses open source libraries such as:

* [Android-PullToRefresh](https://github.com/chrisbanes/Android-PullToRefresh)
* [android-async-http](https://github.com/loopj/android-async-http)

## Contributing

Please fork this repository and contribute back using [pull requests](https://github.com/chadsmith/OpenVBX-Android/pulls).

Any contributions, large or small, major features, bug fixes, additional language translations, unit/integration tests are welcomed and appreciated.