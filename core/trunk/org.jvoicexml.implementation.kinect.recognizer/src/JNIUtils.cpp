#include "stdafx.h"
#include <sapi.h>
#include "JNIUtils.h"

#define INITGUID
#include <guiddef.h>
//#include <log4cplus/logger.h>
//#include <log4cplus/loggingmacros.h>
//#include <log4cplus/configurator.h>

// This is the class ID we expect for the Microsoft Speech recognizer.
// Other values indicate that we're using a version of sapi.h that is
// incompatible with this sample.
DEFINE_GUID(CLSID_ExpectedRecognizer, 0x495648e7, 0xf7ab, 0x4267, 0x8e, 0x0f, 0xca, 0xfb, 0x7a, 0x33, 0xc1, 0x60);

//static log4cplus::Logger logger =
//    log4cplus::Logger::getInstance(L"org.jvoicexml.kinect.recognizer.JNIUtils");


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved)
{
	//log4cplus::BasicConfigurator config;
	//config.configure();

    if (CLSID_ExpectedRecognizer != CLSID_SpInprocRecognizer)
    {
        char buffer[1024];
        //GetErrorMessage(buffer, sizeof(buffer), "Incompatible SAPI versions! Please ensure that Microsoft Speech SDK and other requirements are installed!",
        //    hr);
        return JNI_ERR;
    }

	// Initialize COM
	HRESULT hr = ::CoInitializeEx(NULL, COINIT_MULTITHREADED);
    if (FAILED(hr))
    {
        char buffer[1024];
        //GetErrorMessage(buffer, sizeof(buffer), "Initializing COM failed!",
        //    hr);
        return JNI_ERR;
    }

	return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *jvm, void *reserved)
{
	::CoUninitialize();
}


void ThrowJavaException(JNIEnv* env, char* exceptionClassName, char* message)
{
    jclass exception = env->FindClass(exceptionClassName);
    if (exception == 0) /* Unable to find the new exception class, give up. */
    {
        return;
    }
    env->ThrowNew(exception, message);
}

BOOL GetMethodId(JNIEnv* env, const char* className, const char* methodName,
                 const char* sig, jclass& clazz, jmethodID& methodId)
{
    clazz = env->FindClass(className);
    if (clazz == NULL)
    {
        char msg[512];
        _snprintf(msg, sizeof(msg), "Unable to find class %s!", className);
        ThrowJavaException(env, "java/lang/NullPointerException", msg);
        return FALSE;
    }
    methodId = env->GetMethodID(clazz, methodName, sig);
    if (methodId == NULL)
    {
        char msg[1024];
        _snprintf(msg, sizeof(msg), "Unable to find method '%s(%s)'!", methodName, sig);
        ThrowJavaException(env, "java/lang/NullPointerException", msg);
        return FALSE;
    }
    return TRUE;
}

BOOL GetStaticMethodId(JNIEnv* env, const char* className, const char* methodName,
                 const char* sig, jclass& clazz, jmethodID& methodId)
{
    clazz = env->FindClass(className);
    if (clazz == NULL)
    {
        char msg[512];
        _snprintf(msg, sizeof(msg), "Unable to %s!", className);
        ThrowJavaException(env, "java/lang/NullPointerException", msg);
        return FALSE;
    }
    methodId = env->GetStaticMethodID(clazz, methodName, sig);
    if (methodId == NULL)
    {
        char msg[1024];
        _snprintf(msg, sizeof(msg), "Unable to find method '%s(%s)'!", methodName, sig);
        ThrowJavaException(env, "java/lang/NullPointerException", msg);
        return FALSE;
    }
    return TRUE;
}

BOOL GetStaticObjectField(JNIEnv* env, const char* className, const char* fieldName,
                 const char* sig, jobject& object)
{
    jclass clazz = env->FindClass(className);
    if (clazz == NULL)
    {
        char msg[512];
        _snprintf(msg, sizeof(msg), "Unable to %s!", className);
        ThrowJavaException(env, "java/lang/NullPointerException", msg);
        return FALSE;
    }
    jfieldID fieldId = env->GetStaticFieldID(clazz, fieldName, sig);
    if (fieldId == NULL)
    {
        char msg[1024];
        _snprintf(msg, sizeof(msg), "Unable to find field '%s(%s)'!", fieldName, sig);
        ThrowJavaException(env, "java/lang/NullPointerException", msg);
        return FALSE;
    }
    object = env->GetStaticObjectField(clazz, fieldId);
    return TRUE;
}

