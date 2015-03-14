# ClipboardTranslator

gradle setting
--------------

    $ cat ~/.gradle/gradle.properties

    RELEASE_STORE_FILE=/home/xxx/xxxxx.keystore
    RELEASE_STORE_PASSWORD=*****
    RELEASE_KEY_ALIAS=*****
    RELEASE_KEY_PASSWORD=*****

keystore setting
----------------

    $ keytool -genkey -v -keystore xxxxx.keystore -alias xxxxx -keyalg RSA -validity 10000

build
-----

    $ ./gradlew assembleLiveRelease

intall
------

    $ adb install app/build/outputs/apk/app-live-release.apk

