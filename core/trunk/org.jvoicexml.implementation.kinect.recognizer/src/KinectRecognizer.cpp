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

HWND hWnd = NULL;
static HINSTANCE hInstance = NULL;

void GetErrorMessage(char* buffer, size_t size, const char* text, HRESULT hr) 
{	
	DWORD Error = GetLastError();
	LPSTR pMessage = NULL;
	DWORD length = FormatMessageA(
							FORMAT_MESSAGE_ALLOCATE_BUFFER  |
							FORMAT_MESSAGE_FROM_HMODULE |
							FORMAT_MESSAGE_FROM_SYSTEM	|
							FORMAT_MESSAGE_IGNORE_INSERTS |
							FORMAT_MESSAGE_MAX_WIDTH_MASK,
							hInstance,
							hr,
							MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
							(LPSTR)pMessage,
							0,
							NULL);
    if (length > 0)
    {		
		sprintf_s( buffer, length, "%s. %s: (%#lX)", text, pMessage, hr);	
		LocalFree(pMessage);
	}
    else
    {
		sprintf_s(buffer, size, "%s. ErrorCode: %#lX", text, hr);
    }	
}

LRESULT CALLBACK WndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam)
{
	switch (message)
	{
	case WM_NCCREATE:
		DefWindowProc(hWnd, message, lParam, wParam);
		return 1;
	case WM_CLOSE:
		DestroyWindow(hWnd);
		return 0;
	case WM_DESTROY:
		PostQuitMessage(0);
		return 0;
	}

	return DefWindowProcA(hWnd, message, lParam, wParam);
}

DWORD MessageLoop(void)
{
    MSG msg;
    while (GetMessage(&msg, hWnd, 0, 0) == TRUE)
    {
        TranslateMessage(&msg);
        DispatchMessage(&msg);
    }
    return 0;
}

BOOL APIENTRY DllMain(HINSTANCE hModule, DWORD ul_reason_for_call, LPVOID lpReserved)
{	
    if (hWnd != NULL)
    {
        return TRUE;
    }

    hInstance = hModule;
	TCHAR *szWindowClass=_T("JVcoiceXmlKinectRecognizerClass");
	TCHAR *szTitle=_T("JVcoiceXmlKinectRecognizer");

	WNDCLASS wndclass;
    wndclass.style         = CS_HREDRAW | CS_VREDRAW;
    wndclass.lpfnWndProc   = WndProc;
    wndclass.cbClsExtra    = 0;
    wndclass.cbWndExtra    = 0;
    wndclass.hInstance     = hModule;
    wndclass.hIcon         = NULL;
    wndclass.hCursor       = 0;
    wndclass.hbrBackground = (HBRUSH)(COLOR_WINDOW+1);
    wndclass.lpszMenuName  = NULL;
    wndclass.lpszClassName = szWindowClass;
    ATOM atom = RegisterClass(&wndclass);
    if (atom == NULL)
	{
        return FALSE;
	}

	hWnd = CreateWindow(
	  szWindowClass,
	  szTitle,
  	  WS_OVERLAPPEDWINDOW,
  	  CW_USEDEFAULT,
  	  0,
  	  CW_USEDEFAULT,
  	  0,
	  HWND_MESSAGE,
	  NULL,
	  hModule,
	  NULL);
	if (hWnd == 0)
	{
        return FALSE;
	}
    return TRUE;
}

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
	HRESULT hr = recognizer->StartSpeechRecognition();
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
	HRESULT hr = recognizer->StopSpeechRecognition();
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
JNIEXPORT void JNICALL Java_org_jvoicexml_implementation_kinect_KinectRecognizer_kinectDeallocate
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

