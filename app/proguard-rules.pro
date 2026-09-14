# R8 full-mode rules for the minimal Compose AOT app.
#
# Design notes:
# - Library consumer rules (Compose, coroutines, serialization, AndroidX)
#   already cover their internals; this file only covers APP boundaries:
#   manifest entry points, serialization models, enums, JNI-style edges.
# - Keep attributes needed for coroutines (Signature) and crash reports
#   (SourceFile/LineNumberTable), which full mode would otherwise strip.
# - No absolute paths, no machine-specific assumptions anywhere in here.

# --- Attributes R8 full mode must not strip ---
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, *Annotation*
-keepattributes SourceFile, LineNumberTable

# --- App entry points (also kept via the manifest, spelled out for stability) ---
-keep public class com.example.aotmin.AotApp { *; }
-keep public class com.example.aotmin.MainActivity { *; }
-keep public class * extends androidx.activity.ComponentActivity { *; }
-keep public class * extends android.app.Application { *; }

# --- Kotlin ---
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.coroutines.** { *; }

# --- kotlinx.serialization: keep generated serializers + model fields ---
# The plugin generates `Companion.serializer()`; full mode must not strip or
# rename it, and model fields must survive even if only touched reflectively.
-keepattributes RuntimeVisibleAnnotations, AnnotationDefault
-keepclassmembers class kotlinx.serialization.json.** { *; }
-keepclasseswithmembernames class com.example.aotmin.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class com.example.aotmin.** {
    <fields>;
    <init>(...);
}
-keepclassmembers class com.example.aotmin.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- Enums (valueOf/values reached reflectively) ---
-keepclassmembers,allowshrinking,allowobfuscation enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# --- Parcelable / Serializable models (none today; inert until used) ---
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# --- JNI edge (no native code today; keeps the rule stable when added) ---
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}
-keepclasseswithmembernames class * {
    native <methods>;
}

# --- WebView JS bridge (none today; inert until used) ---
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# --- Compose: app code needs no extra rules ---
# Compose, coroutines and AndroidX libraries ship their own consumer rules,
# which R8 applies automatically. No blanket -keep on library code: that
# would only bloat the APK without adding stability.
