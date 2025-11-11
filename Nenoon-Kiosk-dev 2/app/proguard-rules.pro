# 프로젝트별 ProGuard 규칙
# build.gradle의 proguardFiles 설정으로 적용됨
#
# 자세한 내용:
#   http://developer.android.com/guide/developing/tools/proguard.html

# ==================== 보안 & 난독화 ====================

# 디버깅용 라인 번호 유지 (단, 소스 파일명은 숨김)
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# 릴리즈 빌드에서 로그 제거
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# 적극적 난독화
-optimizationpasses 5
-repackageclasses ''
-allowaccessmodification

# ==================== Android Keystore ====================

# Keystore 관련 클래스 유지
-keep class androidx.security.crypto.** { *; }
-keep class javax.crypto.** { *; }
-keep class java.security.** { *; }

# ==================== Jetpack Compose ====================

# Compose 런타임 클래스 유지
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ==================== Hilt ====================

# Hilt 생성 클래스 유지
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keepclasseswithmembernames class * {
    @dagger.hilt.** <methods>;
}

# ==================== Kotlin ====================

# Kotlin 메타데이터 유지
-keepattributes *Annotation*
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }

# ==================== Retrofit & OkHttp ====================

# Retrofit 설정
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# OkHttp 설정
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

# ==================== Gson ====================

-keepattributes Signature
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# ==================== Jackson ====================

-keep class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.**

# ==================== ML Kit ====================

-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# ==================== TensorFlow Lite ====================

-keep class org.tensorflow.** { *; }
-dontwarn org.tensorflow.**