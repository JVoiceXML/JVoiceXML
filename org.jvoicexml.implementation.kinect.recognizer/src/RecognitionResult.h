#pragma once

#include "sapi.h"

class RecognitionResult
{
public:
	RecognitionResult(void);
	~RecognitionResult(void);

	HRESULT SetResult(ISpRecoResult* result);
	HRESULT GetStatus();
	BSTR GetSML();

private:
	HRESULT status;
	LPCWSTR ruleName;
	BSTR sml;
	SPPHRASE* pPhrase;
	ISpeechXMLRecoResult* xmlresult;
};

