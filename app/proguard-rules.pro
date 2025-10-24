# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-keep class com.bea.xml.stream.*

-dontwarn  org.apache.batik.anim.dom.SAXSVGDocumentFactory
-dontwarn  org.apache.batik.bridge.BridgeContext
-dontwarn  org.apache.batik.bridge.DocumentLoader
-dontwarn  org.apache.batik.bridge.GVTBuilder
-dontwarn  org.apache.batik.bridge.UserAgent
-dontwarn  org.apache.batik.bridge.UserAgentAdapter
-dontwarn  org.apache.batik.util.XMLResourceDescriptor
-dontwarn  org.osgi.framework.Bundle
-dontwarn  org.osgi.framework.BundleContext
-dontwarn  org.osgi.framework.FrameworkUtil
-dontwarn  org.osgi.framework.ServiceReference

-dontwarn org.osgi.**
-dontwarn aQute.bnd.annotation.**
-dontwarn edu.umd.cs.findbugs.annotations.**

-keep class androidx.room.** { *; }
-keep @androidx.room.Dao class * { *; }

# Keep Retrofit/Gson models
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }