# TappyTaps app sharing library

This library serves for easy sharing within apps on Android. It supports direct sharing with few major social networks and some messaging apps.

## How to use it

### Add gradle dependency

  1. Add URL and password as `myMavenRepoUrl` and `myMavenPassword` inside `gradle.properties`
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
implementation('com.tappytaps.android:app-sharing:0.3.6@aar') {
    transitive = true
}
```

### Usage in code

You can use ShareAppFragment.Builder to create and show instance as shown in the following example. All possible variables are set in this example, although only `setUrl()` is required.

- `setFacebookQuote()` for Facebook or `setTwitterMessage()` for Twitter may be used to customize messages for these netowrks, if they aren't set explicitly, simple message will be used
- `setQrCodeUrl()` may be used for different url in QR code, if not set url will be used

> Note that tweet may contain `{url}` to place link inside

```java
new ShareAppFragment.Builder()
        .setSimpleMessage("String to send in messaging")
        .setEmailSubject("Subject in email")
        .setFacebookQuote("Text for Facebook post")
        .setTwitterMessage("Text for Twitter tweet {url}.")
        .setUrl("https://www.example.com")
        .setQrCodeUrl("https://www.example.org")
        .setStyle(yourAppTheme)
        .show(getChildFragmentManager());
```

## How to publish

This library is intended to be published using private maven repository on MyMavenRepo. To make it work, place write URL and password inside `local.properties` using following names:

- `myMavenRepoWirteUrl` for URL
- `myMavenRepoWritePassword` for password

To succesfully publish one must raise version name in appsharing module's `build.gradle` file and issue command `./gradlew clean build publish`.