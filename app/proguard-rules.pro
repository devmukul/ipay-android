# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/Android Studio.app/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html
# Add any project specific keep options here:
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# Obfuscation parameters:
#-dontobfuscate
-useuniqueclassmembernames
-allowaccessmodification
-keep class com.google.** { *; }
-keep class com.journeyapps.** { *; }
-keep class com.makeramen.** { *; }
-keep class com.github.** { *; }
-keep class org.apache.** { *; }
-keep class com.flipboard.** { *; }
-keep class com.android.** { *; }
-keep class com.mikepenz.** { *; }
-keep class junit.** { *; }
-keep class org.mockito.** { *; }
-keep class org.aspectj.** { *; }
-keep class android.support.v7.widget.SearchView { *; }
-keep class bd.com.ipay.ipayskeleton.Model.** { *; }
-keepclassmembers class bd.com.ipay.ipayskeleton.Model.** { *; }

-keepattributes Signature
-keepattributes *Annotation*

-dontwarn com.google.**
-dontwarn org.apache.**
-dontwarn android.support.**
-dontwarn org.junit.**
-dontwarn org.mockito.**
-dontwarn com.makeramen.**

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** w(...);
    public static *** i(...);
    public static *** e(...);
}


