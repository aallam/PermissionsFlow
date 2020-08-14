# Permissions Flow 

![Maven Central](https://img.shields.io/maven-central/v/com.aallam.permissionsflow/permissionsflow)

This library allows the usage of Kotlin Flow with Android runtime permissions system.

## Download

The latest release is available on [Maven Central](https://search.maven.org/artifact/com.aallam.permissionsflow/permissionsflow).

```groovy
implementation 'com.aallam.permissionsflow:permissionsflow:0.2.0'
```

## Usage
Create a `PermissionsFlow` instance :
```kotlin
val permissionsFlow = PermissionsFlow(this)
```
⚠️ `this` can be an activity or a fragment. Inside a fragment you should pass the fragment instance as parameter rather than the activity.

### Permission Request
To request permissions, use  `request`: 
```kotlin
permissionsFlow
    .request(Manifest.permission.CAMERA)
    .onEach { granted ->
        when(granted) {
            true -> Log.d(TAG, "Granted! YAY!") 
            false -> Log.d(TAG, "Oops! Denied.") 
        }
    }
```

Or, use `request` extension function:
```kotlin
button.clicks()
    .request(permissionsFlow, Manifest.permission.CAMERA) // or, request(Manifest.permission.CAMERA)
    .onEach { granted ->
        when(granted) {
            true -> Log.d(TAG, "Granted! YAY!") 
            false -> Log.d(TAG, "Oops! Denied.") 
        }
    }
```

### Detailed Results
For detailed results, use `requestEach`:
```kotlin
button.clicks()
    .requestEach(permissionsFlow, Manifest.permission.CAMERA) // or, requestEach(Manifest.permission.CAMERA)
    .onEach { permission ->
        when {
            permission.granted -> Log.d(TAG, "Granted! YAY!") 
            permission.shouldShowRequestPermissionRationale -> Log.d(TAG, "Denied without ask never again :(") 
            else -> Log.d(TAG, "Oops! Denied.") 
        }
    }
```

### Combined Results
For combined results, use `requestEachCombined`:
```kotlin
button.clicks()
    .requestEachCombined(permissionsFlow, Manifest.permission.CAMERA) // or, requestEachCombined(Manifest.permission.CAMERA)
    .onEach { permission -> // will emit 1 Permission object
        when {
            permission.granted -> Log.d(TAG, "Granted! YAY!") 
            permission.shouldShowRequestPermissionRationale -> Log.d(TAG, "Denied without ask never again :(") 
            else -> Log.d(TAG, "Oops! Denied.") 
        }
    }
```
ℹ️ Please check the sample app for more details.

## Credit
This library inspired by [RxPermissions](https://github.com/tbruyelle/RxPermissions)

## License
PermissionsFlow is distributed under the terms of the Apache License (Version 2.0). See [LICENSE](LICENSE) for details.
