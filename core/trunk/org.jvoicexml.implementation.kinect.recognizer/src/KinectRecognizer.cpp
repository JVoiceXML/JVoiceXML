/*
 * KinectRecognizer.cpp
 *
 *  Created on: Nov 27, 2012
 *      Author: dirk
 */

#include "stdafx.h"
#include "JNIUtils.h"
#include "org_jvoicexml_implementation_kinect_KinectRecognizer.h"
#include "JVoiceXmlKinectRecognizer.h"

/*
 * Class:     org_jvoicexml_implementation_kinect_KinectRecognizer
 * Method:    kinectAllocate
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_org_jvoicexml_implementation_kinect_KinectRecognizer_kinectAllocate
  (JNIEnv *env, jobject caller)
{
	JVoiceXmlKinectRecognizer* recognizer = new JVoiceXmlKinectRecognizer();
	HRESULT hr = recognizer->Allocate();
	if (FAILED(hr))
	{
        char buffer[1024];
        GetErrorMessage(buffer, sizeof(buffer), "Allocation of recognizer failed", hr);
        ThrowJavaException(env, "org/jvoicexml/implementation/kinect/KinectRecognizerException", buffer);
		return 0;
	}
	return (jlong) recognizer;
}

/*
 * Class:     org_jvoicexml_implementation_kinect_KinectRecognizer
 * Method:    kinectStartRecognition
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_jvoicexml_implementation_kinect_KinectRecognizer_kinectStartRecognition
  (JNIEnv *env, jobject caller, jlong handle)
{
	JVoiceXmlKinectRecognizer* recognizer = (JVoiceXmlKinectRecognizer*) handle;
	HRESULT hr = recognizer->StartRecognition();
	if (FAILED(hr))
	{
        char buffer[1024];
        GetErrorMessage(buffer, sizeof(buffer), "Starting recognition failed", hr);
        ThrowJavaException(env, "org/jvoicexml/implementation/kinect/KinectRecognizerException", buffer);
		return;
	}
}

/*
 * Class:     org_jvoicexml_implementation_kinect_KinectRecognizer
 * Method:    kinectStopRecognition
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_jvoicexml_implementation_kinect_KinectRecognizer_kinectStopRecognition
  (JNIEnv *env, jobject caller, jlong handle)
{
	JVoiceXmlKinectRecognizer* recognizer = (JVoiceXmlKinectRecognizer*) handle;
	HRESULT hr = recognizer->StopRecognition();
	if (FAILED(hr))
	{
        char buffer[1024];
        GetErrorMessage(buffer, sizeof(buffer), "Stopping recognition failed", hr);
        ThrowJavaException(env, "org/jvoicexml/implementation/kinect/KinectRecognizerException", buffer);
		return;
	}
}

/*
 * Class:     org_jvoicexml_implementation_kinect_KinectRecognizer
 * Method:    kinectDeallocate
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_jvoicexml_implementation_kinect_KinectRecognizer_kinectDeallocate
  (JNIEnv *env, jobject caller, jlong handle)
{
	JVoiceXmlKinectRecognizer* recognizer = (JVoiceXmlKinectRecognizer*) handle;
	HRESULT hr = recognizer->Deallocate();
	if (FAILED(hr))
	{
        char buffer[1024];
        GetErrorMessage(buffer, sizeof(buffer), "Stopping recognition failed", hr);
        ThrowJavaException(env, "org/jvoicexml/implementation/kinect/KinectRecognizerException", buffer);
		return;
	}
}

