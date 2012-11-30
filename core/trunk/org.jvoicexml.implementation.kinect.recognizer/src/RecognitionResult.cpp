#include "stdafx.h"
#include "RecognitionResult.h"


RecognitionResult::RecognitionResult(void)
	: status(S_OK),
	ruleName(NULL),
	sml(NULL),
	pPhrase(NULL),
	xmlresult(NULL)
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
	status = result->QueryInterface(IID_ISpeechXMLRecoResult, (void**)&xmlresult);
	if (FAILED(status))
	{
		return status;
	}

	// receive an SML String from the XMLRecoResult
	status = xmlresult->GetXMLResult(SPXRO_SML, &sml);
	if (FAILED(status))
    {
		return status; // could not retrieve the SML-Resultstring
	}
	return S_OK;
}

HRESULT RecognitionResult::GetStatus()
{
	return status;
}

BSTR RecognitionResult::GetSML()
{
	return (WCHAR*) sml;
}
