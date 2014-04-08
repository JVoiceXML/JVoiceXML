//------------------------------------------------------------------------------
// <copyright file="SpeechBasics.cpp" company="Microsoft">
//     Copyright (c) Microsoft Corporation.  All rights reserved.
// </copyright>
//------------------------------------------------------------------------------

#include "stdafx.h"
#include "JVoiceXmlKinectRecognizer.h"
#include "resource.h"
#include <iostream>

#define INITGUID
#include <guiddef.h>

// Static initializers
LPCWSTR JVoiceXmlKinectRecognizer::GrammarFileName = L"SpeechBasics-D2D.grxml";

/// <summary>
/// Constructor
/// </summary>
JVoiceXmlKinectRecognizer::JVoiceXmlKinectRecognizer() :
    sensor(NULL),
	kinectAudioStream(NULL),
	speechStream(NULL),
    recognizer(NULL),
    speechContext(NULL),
    m_pSpeechGrammar(NULL),
    speechEvent(INVALID_HANDLE_VALUE),
	stopRequest(FALSE)
{
}

/// <summary>
/// Destructor
/// </summary>
JVoiceXmlKinectRecognizer::~JVoiceXmlKinectRecognizer()
{
    if (sensor)
    {
		StopSpeechRecognition();
        sensor->NuiShutdown();
    }

    SafeRelease(sensor);
	SafeRelease(kinectAudioStream);
	SafeRelease(speechStream);
	SafeRelease(recognizer);
    SafeRelease(speechContext);
    SafeRelease(m_pSpeechGrammar);
}

HRESULT JVoiceXmlKinectRecognizer::Allocate()
{
    // Look for a connected Kinect, and create it if found
    HRESULT hr = CreateFirstConnected();
    if (FAILED(hr))
    {
        return hr;
    }

	return S_OK;
}


HRESULT JVoiceXmlKinectRecognizer::Deallocate()
{
	return S_OK;
}




/// <summary>
/// Create the first connected Kinect found.
/// </summary>
/// <returns>S_OK on success, otherwise failure code.</returns>
HRESULT JVoiceXmlKinectRecognizer::CreateFirstConnected()
{
    INuiSensor * currentSensor;
    HRESULT hr;

    int sensorCount = 0;
	hr = NuiGetSensorCount(&sensorCount);
    if (FAILED(hr))
    {
		std::cerr << "unable to connect to sensor" << std::endl;
        return hr;
    }

    // Look at each Kinect sensor
	for (int i = 0; i < sensorCount; ++i)
    {
        // Create the sensor so we can check status, if we can't create it, move on to the next
		hr = NuiCreateSensorByIndex(i, &currentSensor);
        if (FAILED(hr))
        {
            continue;
        }

        // Get the status of the sensor, and if connected, then we can initialize it
		hr = currentSensor->NuiStatus();
        if (S_OK == hr)
        {
			sensor = currentSensor;
            break;
        }

        // This sensor wasn't OK, so release it since we're not using it
		currentSensor->Release();
    }

    if (NULL != sensor) 
    {
        // Initialize the Kinect and specify that we'll be using audio signal
        hr = sensor->NuiInitialize(NUI_INITIALIZE_FLAG_USES_AUDIO); 
        if (FAILED(hr))
        {
            // Some other application is streaming from the same Kinect sensor
            SafeRelease(sensor);
        }
    }

    if (NULL == sensor || FAILED(hr))
    {
		std::cerr << "no kinect found" << std::endl;
        return E_FAIL;
    }

    hr = InitializeAudioStream();
    if (FAILED(hr))
    {
		std::cerr << "could not initialize audio stream" << std::endl;
        return hr;
    }

    hr = CreateSpeechRecognizer();
    if (FAILED(hr))
    {
		std::cerr << "Could not create speech recognizer. Please ensure that Microsoft Speech SDK and other sample requirements are installed." << std::endl;
        return hr;
    }

    // TODO move to an own method to dynamically load grammars
    hr = LoadSpeechGrammar();
    if (FAILED(hr))
    {
		std::cerr << "Could not load speech grammar. Please ensure that grammar configuration file was properly deployed." << std::endl;
        return hr;
    }

    return hr;
}

/// <summary>
/// Initialize Kinect audio stream object.
/// </summary>
/// <returns>
/// <para>S_OK on success, otherwise failure code.</para>
/// </returns>
HRESULT JVoiceXmlKinectRecognizer::InitializeAudioStream()
{
    INuiAudioBeam*      pNuiAudioSource = NULL;
    IMediaObject*       pDMO = NULL;
    IPropertyStore*     pPropertyStore = NULL;
    IStream*            stream = NULL;

    // Get the audio source
    HRESULT hr = sensor->NuiGetAudioSource(&pNuiAudioSource);
    if (SUCCEEDED(hr))
    {
        hr = pNuiAudioSource->QueryInterface(IID_IMediaObject, (void**)&pDMO);

        if (SUCCEEDED(hr))
        {
            hr = pNuiAudioSource->QueryInterface(IID_IPropertyStore, (void**)&pPropertyStore);
            // Set AEC-MicArray DMO system mode. This must be set for the DMO to work properly.
            // Possible values are:
            //   SINGLE_CHANNEL_AEC = 0
            //   OPTIBEAM_ARRAY_ONLY = 2
            //   OPTIBEAM_ARRAY_AND_AEC = 4
            //   SINGLE_CHANNEL_NSAGC = 5
            PROPVARIANT pvSysMode;
            PropVariantInit(&pvSysMode);
            pvSysMode.vt = VT_I4;
            pvSysMode.lVal = (LONG)(2); // Use OPTIBEAM_ARRAY_ONLY setting. Set OPTIBEAM_ARRAY_AND_AEC instead if you expect to have sound playing from speakers.
            pPropertyStore->SetValue(MFPKEY_WMAAECMA_SYSTEM_MODE, pvSysMode);
            PropVariantClear(&pvSysMode);

            // Set DMO output format
            WAVEFORMATEX wfxOut = {AudioFormat, AudioChannels, AudioSamplesPerSecond, AudioAverageBytesPerSecond, AudioBlockAlign, AudioBitsPerSample, 0};
            DMO_MEDIA_TYPE mt = {0};
            MoInitMediaType(&mt, sizeof(WAVEFORMATEX));
    
            mt.majortype = MEDIATYPE_Audio;
            mt.subtype = MEDIASUBTYPE_PCM;
            mt.lSampleSize = 0;
            mt.bFixedSizeSamples = TRUE;
            mt.bTemporalCompression = FALSE;
            mt.formattype = FORMAT_WaveFormatEx;	
            memcpy(mt.pbFormat, &wfxOut, sizeof(WAVEFORMATEX));
    
            hr = pDMO->SetOutputType(0, &mt, 0);

            if (SUCCEEDED(hr))
            {
				kinectAudioStream = new KinectAudioStream(pDMO);

				hr = kinectAudioStream->QueryInterface(IID_IStream, (void**)&stream);

                if (SUCCEEDED(hr))
                {
					hr = CoCreateInstance(CLSID_SpStream, NULL, CLSCTX_INPROC_SERVER, __uuidof(ISpStream), (void**)&speechStream);

                    if (SUCCEEDED(hr))
                    {
						hr = speechStream->SetBaseStream(stream, SPDFID_WaveFormatEx, &wfxOut);
                    }
                }
            }

            MoFreeMediaType(&mt);
        }
    }

	SafeRelease(stream);
    SafeRelease(pPropertyStore);
    SafeRelease(pDMO);
    SafeRelease(pNuiAudioSource);

    return hr;
}

/// <summary>
/// Create speech recognizer that will read Kinect audio stream data.
/// </summary>
/// <returns>
/// <para>S_OK on success, otherwise failure code.</para>
/// </returns>
HRESULT JVoiceXmlKinectRecognizer::CreateSpeechRecognizer()
{
    ISpObjectToken *pEngineToken = NULL;
    
    HRESULT hr = CoCreateInstance(CLSID_SpInprocRecognizer, NULL, CLSCTX_INPROC_SERVER, __uuidof(ISpRecognizer), (void**)&recognizer);

    if (SUCCEEDED(hr))
    {
		recognizer->SetInput(speechStream, FALSE);
        hr = SpFindBestToken(SPCAT_RECOGNIZERS,L"Language=409;Kinect=True",NULL,&pEngineToken);

        if (SUCCEEDED(hr))
        {
            recognizer->SetRecognizer(pEngineToken);
            hr = recognizer->CreateRecoContext(&speechContext);
        }
    }

    SafeRelease(pEngineToken);

    return hr;
}

/// <summary>
/// Load speech recognition grammar into recognizer.
/// </summary>
/// <returns>
/// <para>S_OK on success, otherwise failure code.</para>
/// </returns>
HRESULT JVoiceXmlKinectRecognizer::LoadSpeechGrammar()
{
    HRESULT hr = speechContext->CreateGrammar(1, &m_pSpeechGrammar);

    if (SUCCEEDED(hr))
    {
        // Populate recognition grammar from file
        hr = m_pSpeechGrammar->LoadCmdFromFile(GrammarFileName, SPLO_STATIC);
    }

    return hr;
}

/// <summary>
/// Start recognizing speech synchronously.
/// </summary>
/// <returns>
/// <para>S_OK on success, otherwise failure code.</para>
/// </returns>
HRESULT JVoiceXmlKinectRecognizer::RecognizeSpeech(RecognitionResult& result)
{
	stopRequest = FALSE;

	HRESULT hr = recognizer->SetInput(speechStream, FALSE);
	if (FAILED(hr))
	{
		return hr;
	}

	hr = kinectAudioStream->StartCapture();
	if (FAILED(hr))
	{
		return hr;
	}

    // Specify that all top level rules in grammar are now active
    hr = m_pSpeechGrammar->SetRuleState(NULL, NULL, SPRS_ACTIVE);
	if (FAILED(hr))
	{
		return hr;
	}

	// Specify that engine should always be reading audio
    hr = recognizer->SetRecoState(SPRST_ACTIVE_ALWAYS);
	if (FAILED(hr))
	{
		return hr;
	}

    // Specify that we're only interested in receiving recognition events
    hr = speechContext->SetInterest(SPFEI(SPEI_RECOGNITION), SPFEI(SPEI_RECOGNITION));
	if (FAILED(hr))
	{
		return hr;
	}

    // Ensure that engine is recognizing speech and not in paused state
    hr = speechContext->Resume(0);
	if (FAILED(hr))
	{
		return hr;
	}

	speechEvent = speechContext->GetNotifyEventHandle();
	hr = S_FALSE;

	// wait for an event and try to look if it occured 
	while (!stopRequest && (hr == S_FALSE))
	{
		hr = speechContext->WaitForNotifyEvent(20);
		if (hr == S_OK)
		{
			hr = ProcessSpeech(result);
			if (FAILED(hr))
			{
				return hr;
			}
		}
	}
        
    return hr;
}

HRESULT JVoiceXmlKinectRecognizer::StopSpeechRecognition()
{
	stopRequest = TRUE;
	if (NULL != kinectAudioStream)
    {
		return kinectAudioStream->StopCapture();
    }

	return S_FALSE;
}

/// <summary>
/// Process recently triggered speech recognition events.
/// </summary>
HRESULT JVoiceXmlKinectRecognizer::ProcessSpeech(RecognitionResult& result)
{
    const float ConfidenceThreshold = 0.3f;

    SPEVENT curEvent;
    ULONG fetched = 0;
    HRESULT hr = S_OK;

    speechContext->GetEvents(1, &curEvent, &fetched);

    while (fetched > 0)
    {
        switch (curEvent.eEventId)
        {
            case SPEI_RECOGNITION:
                if (SPET_LPARAM_IS_OBJECT == curEvent.elParamType)
                {
                    // this is an ISpRecoResult
                    ISpRecoResult* recoresult = reinterpret_cast<ISpRecoResult*>(curEvent.lParam);
					hr = result.SetResult(recoresult);
                }
                break;
        }

        speechContext->GetEvents(1, &curEvent, &fetched);
    }

    return hr;
}

