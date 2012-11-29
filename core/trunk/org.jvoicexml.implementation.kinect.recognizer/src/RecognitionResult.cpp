#include "stdafx.h"
#include "sapi.h"
#include "RecognitionResult.h"


RecognitionResult::RecognitionResult(void)
{
}


RecognitionResult::~RecognitionResult(void)
{
}

HRESULT RecognitionResult::SetResult(ISpRecoResult* result)
{
    SPPHRASE* pPhrase = NULL;
	HRESULT hr = result->GetPhrase(&pPhrase);
	if (FAILED(hr))
	{
		return hr;
	}

	ruleName = (pPhrase->Rule.pszName);

	// receive an XMLRecoResult from the RecoResult
	ISpeechXMLRecoResult* XMLResult;
	result->QueryInterface(IID_ISpeechXMLRecoResult, (void**)&XMLResult);

	// receive an SML String from the XMLRecoResult
	BSTR tmp = NULL;
	hr = XMLResult->GetXMLResult(SPXRO_SML, &tmp);
	if (FAILED(hr))
    {
		return hr; // could not retrieve the SML-Resultstring
	}
	sml = (WCHAR*) tmp;
	return S_OK;
}

HRESULT RecognitionResult::GetStatus()
{
	return status;
}

LPCWSTR RecognitionResult::GetSML()
{
	return sml;
}
