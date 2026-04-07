# Favor smaller dex/class name tables.
-allowaccessmodification
-repackageclasses

# ====== Xposed ======
-adaptresourcefilecontents META-INF/xposed/java_init.list
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,AnnotationDefault,InnerClasses,EnclosingMethod,Signature
-keep,allowobfuscation,allowoptimization class * extends io.github.libxposed.api.XposedModule {
    public <init>();
    public void onModuleLoaded(...);
    public void onPackageLoaded(...);
    public void onPackageReady(...);
    public void onSystemServerLoaded(...);
}

# Strip release logging code paths.
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

-assumenosideeffects class com.yuuki.flyu.hook.utils.HookLog {
    public static void i(java.lang.String);
    public static void d(java.lang.String);
    public static void w(java.lang.String);
    public static void e(java.lang.String);
    public static void e(java.lang.String, java.lang.Throwable);
}
