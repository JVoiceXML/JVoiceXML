#include "stdafx.h"
#include "RecognitionResult.h"


RecognitionResult::RecognitionResult(void)
	: status(S_OK),
	ruleName(NULL),
	sml(NULL),
	pPhrase(NULL)
{
}


RecognitionResult::~RecognitionResult(void)
{
	if (pPhrase != NULL)
	{
		::CoTaskMemFree(pPhrase);
	}
}

HRESULT RecognitionResult::SetResult(ISpRecoResult* result)
{
	status = result->GetPhrase(&pPhrase);
	if (FAILED(status))
	{
		return status;
	}

	ruleName = (pPhrase->Rule.pszName);

	// receive an XMLRecoResult from the RecoResult
	ISpeechXMLRecoResult* XMLResult;
	result->QueryInterface(IID_ISpeechXMLRecoResult, (void**)&XMLResult);

	// receive an SML String from the XMLRecoResult
	BSTR tmp = NULL;
	status = XMLResult->GetXMLResult(SPXRO_SML, &tmp);
	if (FAILED(status))
    {
		return status; // could not retrieve the SML-Resultstring
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
