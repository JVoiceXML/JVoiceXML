//------------------------------------------------------------------------------
// <copyright file="SpeechBasics.h" company="Microsoft">
//     Copyright (c) Microsoft Corporation.  All rights reserved.
// </copyright>
//------------------------------------------------------------------------------

#pragma once

#include "KinectAudioStream.h"

// For configuring DMO properties
#include <wmcodecdsp.h>

// For FORMAT_WaveFormatEx and such
#include <uuids.h>

// For Kinect SDK APIs
#include <NuiApi.h>

// For speech APIs
// NOTE: To ensure that application compiles and links against correct SAPI versions (from Microsoft Speech
//       SDK), VC++ include and library paths should be configured to list appropriate paths within Microsoft
//       Speech SDK installation directory before listing the default system include and library directories,
//       which might contain a version of SAPI that is not appropriate for use together with Kinect sensor.
#include <sapi.h>
#include <sphelper.h>
#include "RecognitionResult.h"

/// <summary>
/// The bridge between the Microsoft Kinect and JVoiceXML.
/// </summary>
class JVoiceXmlKinectRecognizer
{
public:
    /// <summary>
    /// Constructor
    /// </summary>
    JVoiceXmlKinectRecognizer();

    /// <summary>
    /// Destructor
    /// </summary>
    ~JVoiceXmlKinectRecognizer();

    /// <summary>
    /// Creates the main window and begins processing
    /// </summary>
    /// <param name="hInstance">handle to the application instance</param>
    /// <param name="nCmdShow">whether to display minimized, maximized, or normally</param>
    //int                     Run(HINSTANCE hInstance, int nCmdShow);

    HRESULT Allocate();

    /// <summary>
    /// Start recognizing speechsynchronously.
    /// </summary>
    /// <returns>
    /// <para>S_OK on success, otherwise failure code.</para>
    /// </returns>
	HRESULT                 RecognizeSpeech(RecognitionResult& result);

    HRESULT                 StopSpeechRecognition();

    HRESULT Deallocate();

private:
    static LPCWSTR          GrammarFileName;
    
    // Main application dialog window
    HWND                    m_hWnd;

    // Current Kinect sensor
    INuiSensor*             m_pNuiSensor;

    // Audio stream captured from Kinect.
    KinectAudioStream*      m_pKinectAudioStream;

    // Stream given to speech recognition engine
    ISpStream*              m_pSpeechStream;

    // Speech recognizer
    ISpRecognizer*          m_pSpeechRecognizer;

    // Speech recognizer context
    ISpRecoContext*         m_pSpeechContext;

    // Speech grammar
    ISpRecoGrammar*         m_pSpeechGrammar;

    // Event triggered when we detect speech recognition
    HANDLE                  m_hSpeechEvent;

    /// <summary>
    /// Create the first connected Kinect found.
    /// </summary>
    /// <returns>S_OK on success, otherwise failure code.</returns>
    HRESULT                 CreateFirstConnected();

    /// <summary>
    /// Initialize Kinect audio stream object.
    /// </summary>
    /// <returns>S_OK on success, otherwise failure code.</returns>
    HRESULT                 InitializeAudioStream();

    /// <summary>
    /// Create speech recognizer that will read Kinect audio stream data.
    /// </summary>
    /// <returns>
    /// <para>S_OK on success, otherwise failure code.</para>
    /// </returns>
    HRESULT                 CreateSpeechRecognizer();

    /// <summary>
    /// Load speech recognition grammar into recognizer.
    /// </summary>
    /// <returns>
    /// <para>S_OK on success, otherwise failure code.</para>
    /// </returns>
    HRESULT                 LoadSpeechGrammar();

    /// <summary>
    /// Process recently triggered speech recognition events.
    /// </summary>
    HRESULT                 ProcessSpeech(RecognitionResult& result);
};
