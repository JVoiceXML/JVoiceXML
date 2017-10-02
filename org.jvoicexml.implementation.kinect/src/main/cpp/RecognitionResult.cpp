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
//------------------------------------------------------------------------------

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
