����   5 
 module-info  org.objectweb.asm  6.0 	java.base  org/objectweb/asm  org/objectweb/asm/signature 
 Module�               "      �     	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
-dontwarn okio.**
-keep class okio.** { *;}
​
-dontwarn okhttp3.**
-keep class okhttp3.** { *;}
​
-dontwarn com.google.gson.**
-keep class com.google.gson.** { *;}
​
-dontwarn android.support.**
-keep class android.support.** { *;}
​
-dontwarn com.ximalaya.ting.android.player.**
-keep class com.ximalaya.ting.android.player.** { *;}
​
-dontwarn com.ximalaya.ting.android.opensdk.**
-keep interface com.ximalaya.ting.android.opensdk.** {*;}
-keep class com.ximalaya.ting.android.opensdk.** { *; }