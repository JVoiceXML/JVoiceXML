//------------------------------------------------------------------------------
// JVoiceXML - A free VoiceXML implementation.
//
// Copyright (C) 2012-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Library General Public
// License as published by the Free Software Foundation; either
// version 2 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Library General Public License for more details.
//
// You should have received a copy of the GNU Library General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//
// This code is based on Microsoft Speech Basics-D2D C++ Sample.
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
    /// Creates the recognizer and needed resources
    /// </summary>
    HRESULT Allocate();

    /// <summary>
    /// Start recognizing speech synchronously.
    /// </summary>
    /// <returns>
    /// <para>S_OK on success, otherwise failure code.</para>
    /// </returns>
	HRESULT RecognizeSpeech(RecognitionResult& result);

	/// <summary>
	/// Stops recognizing speech synchronously.
	/// </summary>
	/// <returns>
	/// <para>S_OK on success, otherwise failure code.</para>
	/// </returns>
	HRESULT StopSpeechRecognition();

	/// <summary>
	/// Releases all acquired resources.
	/// </summary>
	HRESULT Deallocate();

private:
    static LPCWSTR GrammarFileName;
    
    // Current Kinect sensor
    INuiSensor* sensor;

    // Audio stream captured from Kinect.
    KinectAudioStream* kinectAudioStream;

    // Stream given to speech recognition engine
    ISpStream* speechStream;

    // Speech recognizer
    ISpRecognizer* recognizer;

    // Speech recognizer context
    ISpRecoContext*  speechContext;

    // Speech grammar
    ISpRecoGrammar*  m_pSpeechGrammar;

    // Event triggered when we detect speech recognition
    HANDLE speechEvent;

    /// <summary>
    /// Create the first connected Kinect found.
    /// </summary>
    /// <returns>S_OK on success, otherwise failure code.</returns>
    HRESULT CreateFirstConnected();

    /// <summary>
    /// Initialize Kinect audio stream object.
    /// </summary>
    /// <returns>S_OK on success, otherwise failure code.</returns>
    HRESULT InitializeAudioStream();

    /// <summary>
    /// Create speech recognizer that will read Kinect audio stream data.
    /// </summary>
    /// <returns>
    /// <para>S_OK on success, otherwise failure code.</para>
    /// </returns>
    HRESULT CreateSpeechRecognizer();

    /// <summary>
    /// Load speech recognition grammar into recognizer.
    /// </summary>
    /// <returns>
    /// <para>S_OK on success, otherwise failure code.</para>
    /// </returns>
    HRESULT LoadSpeechGrammar();

    /// <summary>
    /// Process recently triggered speech recognition events.
    /// </summary>
    HRESULT ProcessSpeech(RecognitionResult& result);

	BOOL stopRequest;
};
