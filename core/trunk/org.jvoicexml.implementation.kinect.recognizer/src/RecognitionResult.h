#pragma once
class RecognitionResult
{
public:
	RecognitionResult(void);
	~RecognitionResult(void);

	HRESULT SetResult(ISpRecoResult* result);
	HRESULT GetStatus();
	LPCWSTR GetSML();

private:
	HRESULT status;
	LPCWSTR ruleName;
	LPCWSTR sml;
};

