# TappyTaps App Sharing Library

This library serves for easy sharing within apps on Android. It supports direct sharing with few major social networks and some messaging apps.

## How to use it

### Add gradle dependency

  1. Add URL and password as `myMavenRepoUrl` and `myMavenRepoPassword` inside `gradle.properties`
  2. Add repository inside your app's `build.gradle`
```gradle
allprojects {
    repositories {
        maven {
            url myMavenRepoUrl
            credentials {
                username 'myMavenRepo'
                password myMavenRepoPassword
            }
        }
    }
}
```
  3. Add dependency inside your module's `build.gradle`
```gradle
implementation('com.tappytaps.android:app-sharing:1.2.0@aar') {
    transitive = true
}
```

### Usage in code

You can use `ShareAppFragment.Builder` to create and show instance as shown in the following example. All possible variables are set in this example, although only `setUrl()` is required.

- `setTwitterMessage()` may be used to customize message for Twitter tweet, if it isn't set explicitly, simple message will be used
- `setQrCodeUrl()` may be used for different url in QR code, if not set url will be used
- `setListener()` can be used to pass instance of `ShareAppFragment.Listener` that will be called each time user performs share action and when the `ShareAppFragment` gets dismissed, passed listener must also implement `Parcelable`

> Note: Strings may contain `{url}` to place URL inside

```java
new ShareAppFragment.Builder()
        .setSimpleMessage("String to send in messaging {url}")
        .setEmailSubject("Subject in email")
        .setTwitterMessage("Text for Twitter tweet {url}.")
        .setUrl("https://www.example.com")
        .setQrCodeUrl("https://www.example.org")
        .setStyle(yourAppTheme)
        .setListener(new MyExampleListener())
        .show(getChildFragmentManager());
```

## How to publish

This library is intended to be published using private maven repository on MyMavenRepo. To make it work, place write URL and password inside `local.properties` using following names:

- `myMavenRepoWriteUrl` for URL
- `myMavenRepoWritePassword` for password

To successfully publish one must raise version name in appsharing module's `build.gradle` file and issue command `./gradlew clean build publish`.