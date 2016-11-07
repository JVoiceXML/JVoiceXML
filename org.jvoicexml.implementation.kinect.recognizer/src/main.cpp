/*
// JVoiceXML - A free VoiceXML implementation.
//
// Copyright (C) 2014-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Library General Public
* License as published by the Free Software Foundation; either
* version 2 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Library General Public License for more details.
*
* You should have received a copy of the GNU Library General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
*/

#include "stdafx.h"
//#include "Recognizer.h"
#include "sperror.h"
#include "tchar.h"
#include <string>
//#include <log4cplus/logger.h>
//#include <log4cplus/loggingmacros.h>
//#include "log4cplus/consoleappender.h"


HWND hWnd = NULL;
static HINSTANCE hInstance = NULL;
//static log4cplus::Logger logger =
//log4cplus::Logger::getInstance(_T("org.jvoicexml.sapi.cpp.Main"));

// alternative look-up error codes returned by sapi in the file sperror.h
void GetErrorMessage(char* buffer, size_t size, const char* text, HRESULT hr)
{
	DWORD Error = GetLastError();
	LPSTR pMessage = NULL;
	DWORD length = FormatMessageA(
		FORMAT_MESSAGE_ALLOCATE_BUFFER |
		FORMAT_MESSAGE_FROM_HMODULE |
		FORMAT_MESSAGE_FROM_SYSTEM |
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
		sprintf_s(buffer, length, "%s. %s: (%#lX)", text, pMessage, hr);
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
//	LOG4CPLUS_DEBUG(logger, _T("message loop started"));
	MSG msg;
	while (GetMessage(&msg, hWnd, 0, 0) == TRUE)
	{
//		LOG4CPLUS_DEBUG(logger, (unsigned long)msg.hwnd << _T(": ") << msg.message);
		TranslateMessage(&msg);
		DispatchMessage(&msg);
	}
//	LOG4CPLUS_DEBUG(logger, _T("message loop ended"));
	return 0;
}

BOOL APIENTRY DllMain(HINSTANCE hModule, DWORD ul_reason_for_call, LPVOID lpReserved)
{
	if (hWnd != NULL)
	{
		return TRUE;
	}

	hInstance = hModule;
	TCHAR *szWindowClass = _T("JVoiceXmlKinectWindowClass");
	TCHAR *szTitle = _T("JVoiceXML Kinect");

	WNDCLASS wndclass;
	wndclass.style = CS_HREDRAW | CS_VREDRAW;
	wndclass.lpfnWndProc = WndProc;
	wndclass.cbClsExtra = 0;
	wndclass.cbWndExtra = 0;
	wndclass.hInstance = hModule;
	wndclass.hIcon = NULL;
	wndclass.hCursor = 0;
	wndclass.hbrBackground = (HBRUSH)(COLOR_WINDOW + 1);
	wndclass.lpszMenuName = NULL;
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
