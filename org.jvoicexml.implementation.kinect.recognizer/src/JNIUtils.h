#pragma once

#include <jni.h>

extern JavaVM *jvm; //Handle to the Java Virtual Machine

void ThrowJavaException(JNIEnv* env, char* exceptionClassName, char* message);

BOOL GetMethodId(JNIEnv* env, const char* className, const char* methodName,
                 const char* sig, jclass& clazz, jmethodID& methodId);

BOOL GetStaticMethodId(JNIEnv* env, const char* className, const char* methodName,
                 const char* sig, jclass& clazz, jmethodID& methodId);

BOOL GetStaticObjectField(JNIEnv* env, const char* className, const char* fieldName,
                 const char* sig, jobject& object);
