package org.jvoicexml.documentserver.schemestrategy.http;

import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;

public class MockStatusLine implements StatusLine {
    @Override
    public ProtocolVersion getProtocolVersion() {
        return null;
    }

    @Override
    public int getStatusCode() {
        return 0;
    }

    @Override
    public String getReasonPhrase() {
        return null;
    }
}
