# Unofficial Flipper Android App

Mobile app to rule all Flipper's family
What the Flipper mobile app should have been.

# What's Changed

* You can now select your preferred firmware to use the appropriate update server instead, available selections are presently:
* [Official](https://github.com/flipperdevices/flipperzero-firmware)
* [Unleashed](https://github.com/DarkFlippers/unleashed-firmware)
* [Momentum](https://github.com/Next-Flip/Momentum-Firmware)
* Telemetry has been removed
* Region-free - The subghz update stuff depending on region has been removed


## Download

You'll have to compile it yourself for now. At the very least, assets need to be changed and the app has to be refactored to have a different package name before making public builds.


## Module arch

```
├── instances
│   ├── app
├── components
│   ├── core
│   ├── bridge
│   ├── feature1
│   ├── feature2
```

- `app` - Main application module with UI
- `components/core` - Core library with deps and utils
- `components/bridge` - Communication between android and Flipper
- `components/*` - Features modules, which connect to root application
