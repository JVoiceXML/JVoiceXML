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
    m_pNuiSensor(NULL),
    m_pKinectAudioStream(NULL),
    m_pSpeechStream(NULL),
    m_pSpeechRecognizer(NULL),
    m_pSpeechContext(NULL),
    m_pSpeechGrammar(NULL),
    m_hSpeechEvent(INVALID_HANDLE_VALUE),
	stopRequest(FALSE)
{
}

/// <summary>
/// Destructor
/// </summary>
JVoiceXmlKinectRecognizer::~JVoiceXmlKinectRecognizer()
{
    if (m_pNuiSensor)
    {
		StopSpeechRecognition();
        m_pNuiSensor->NuiShutdown();
    }

    SafeRelease(m_pNuiSensor);
    SafeRelease(m_pKinectAudioStream);
    SafeRelease(m_pSpeechStream);
    SafeRelease(m_pSpeechRecognizer);
    SafeRelease(m_pSpeechContext);
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
    INuiSensor * pNuiSensor;
    HRESULT hr;

    int iSensorCount = 0;
    hr = NuiGetSensorCount(&iSensorCount);
    if (FAILED(hr))
    {
		std::err << "unable to coonect to sensor" << std::endl;
        return hr;
    }

    // Look at each Kinect sensor
    for (int i = 0; i < iSensorCount; ++i)
    {
        // Create the sensor so we can check status, if we can't create it, move on to the next
        hr = NuiCreateSensorByIndex(i, &pNuiSensor);
        if (FAILED(hr))
        {
            continue;
        }

        // Get the status of the sensor, and if connected, then we can initialize it
        hr = pNuiSensor->NuiStatus();
        if (S_OK == hr)
        {
            m_pNuiSensor = pNuiSensor;
            break;
        }

        // This sensor wasn't OK, so release it since we're not using it
        pNuiSensor->Release();
    }

    if (NULL != m_pNuiSensor)
    {
        // Initialize the Kinect and specify that we'll be using audio signal
        hr = m_pNuiSensor->NuiInitialize(NUI_INITIALIZE_FLAG_USES_AUDIO); 
        if (FAILED(hr))
        {
            // Some other application is streaming from the same Kinect sensor
            SafeRelease(m_pNuiSensor);
        }
    }

    if (NULL == m_pNuiSensor || FAILED(hr))
    {
        //SetStatusMessage(L"No ready Kinect found!");
		std::err << "no kinect found" << std::endl;
        return E_FAIL;
    }

    hr = InitializeAudioStream();
    if (FAILED(hr))
    {
        //SetStatusMessage(L"Could not initialize audio stream.");
		std::err << "could not initialize audio stream" << std::endl;
        return hr;
    }

    hr = CreateSpeechRecognizer();
    if (FAILED(hr))
    {
        //SetStatusMessage(L"Could not create speech recognizer. Please ensure that Microsoft Speech SDK and other sample requirements are installed.");
		std::err << "Could not create speech recognizer. Please ensure that Microsoft Speech SDK and other sample requirements are installed." << std::endl;
        return hr;
    }

    // TODO move to an own method to dynamically load grammars
    hr = LoadSpeechGrammar();
    if (FAILED(hr))
    {
		std::err << "Could not load speech grammar. Please ensure that grammar configuration file was properly deployed." << std::endl;
        //SetStatusMessage(L"Could not load speech grammar. Please ensure that grammar configuration file was properly deployed.");
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
    IStream*            pStream = NULL;

    // Get the audio source
    HRESULT hr = m_pNuiSensor->NuiGetAudioSource(&pNuiAudioSource);
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
                m_pKinectAudioStream = new KinectAudioStream(pDMO);

                hr = m_pKinectAudioStream->QueryInterface(IID_IStream, (void**)&pStream);

                if (SUCCEEDED(hr))
                {
                    hr = CoCreateInstance(CLSID_SpStream, NULL, CLSCTX_INPROC_SERVER, __uuidof(ISpStream), (void**)&m_pSpeechStream);

                    if (SUCCEEDED(hr))
                    {
                        hr = m_pSpeechStream->SetBaseStream(pStream, SPDFID_WaveFormatEx, &wfxOut);
                    }
                }
            }

            MoFreeMediaType(&mt);
        }
    }

    SafeRelease(pStream);
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
    
    HRESULT hr = CoCreateInstance(CLSID_SpInprocRecognizer, NULL, CLSCTX_INPROC_SERVER, __uuidof(ISpRecognizer), (void**)&m_pSpeechRecognizer);

    if (SUCCEEDED(hr))
    {
        m_pSpeechRecognizer->SetInput(m_pSpeechStream, FALSE);
        hr = SpFindBestToken(SPCAT_RECOGNIZERS,L"Language=409;Kinect=True",NULL,&pEngineToken);

        if (SUCCEEDED(hr))
        {
            m_pSpeechRecognizer->SetRecognizer(pEngineToken);
            hr = m_pSpeechRecognizer->CreateRecoContext(&m_pSpeechContext);
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
    HRESULT hr = m_pSpeechContext->CreateGrammar(1, &m_pSpeechGrammar);

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
    HRESULT hr = m_pKinectAudioStream->StartCapture();

    if (SUCCEEDED(hr))
    {
        // Specify that all top level rules in grammar are now active
        m_pSpeechGrammar->SetRuleState(NULL, NULL, SPRS_ACTIVE);

		// Specify that engine should always be reading audio
        m_pSpeechRecognizer->SetRecoState(SPRST_ACTIVE_ALWAYS);

        // Specify that we're only interested in receiving recognition events
        m_pSpeechContext->SetInterest(SPFEI(SPEI_RECOGNITION), SPFEI(SPEI_RECOGNITION));

        // Ensure that engine is recognizing speech and not in paused state
        hr = m_pSpeechContext->Resume(0);
        if (SUCCEEDED(hr))
        {
            m_hSpeechEvent = m_pSpeechContext->GetNotifyEventHandle();
        }

		hr = S_FALSE;

		// wait for an event and try to look if it occured 
		while (!stopRequest && (hr == S_FALSE))
		{
			hr = m_pSpeechContext->WaitForNotifyEvent(20);
			if(hr == S_OK)
			{
				hr = ProcessSpeech(result);
				if (FAILED(hr))
				{
					return hr;
				}
			}
		}
    }
        
    return hr;
}

HRESULT JVoiceXmlKinectRecognizer::StopSpeechRecognition()
{
	stopRequest = TRUE;

    if (NULL != m_pKinectAudioStream)
    {
        m_pKinectAudioStream->StopCapture();
    }

	return S_OK;
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

    m_pSpeechContext->GetEvents(1, &curEvent, &fetched);

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

        m_pSpeechContext->GetEvents(1, &curEvent, &fetched);
    }

    return hr;
}

