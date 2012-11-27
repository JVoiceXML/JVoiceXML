﻿//------------------------------------------------------------------------------
// <copyright file="KinectAidopStream.h" company="Microsoft">
//   Copyright (c) Microsoft Corporation.  All rights reserved.
// </copyright>
// <summary>
//   Includes common headers and defines following classes:
//     - CStaticMediaBuffer: Helper class that implements IMediaBuffer
//     - KinectAudioStream: IStream implementation that wraps Kinect audio DMO.
// </summary>
//------------------------------------------------------------------------------
#pragma once

// For IMediaObject and related interfaces
#include <dmo.h>

// For WAVEFORMATEX
#include <mmreg.h>

// For MMCSS functionality such as AvSetMmThreadCharacteristics
#include <avrt.h>

#include <stack>
#include <queue>

// Format of Kinect audio stream
static const WORD       AudioFormat = WAVE_FORMAT_PCM;

// Number of channels in Kinect audio stream
static const WORD       AudioChannels = 1;

// Samples per second in Kinect audio stream
static const DWORD      AudioSamplesPerSecond = 16000;

// Average bytes per second in Kinect audio stream
static const DWORD      AudioAverageBytesPerSecond = 32000;

// Block alignment in Kinect audio stream
static const WORD       AudioBlockAlign = 2;

// Bits per audio sample in Kinect audio stream
static const WORD       AudioBitsPerSample = 16;

/// <summary>
/// IMediaBuffer implementation for a statically allocated buffer.
/// </summary>
class CStaticMediaBuffer : public IMediaBuffer
{
public:
    // Constructor
    CStaticMediaBuffer() : m_dataLength(0) {}

    // IUnknown methods
    STDMETHODIMP_(ULONG) AddRef() { return 2; }
    STDMETHODIMP_(ULONG) Release() { return 1; }
    STDMETHODIMP QueryInterface(REFIID riid, void **ppv)
    {
        if (riid == IID_IUnknown)
        {
            AddRef();
            *ppv = (IUnknown*)this;
            return NOERROR;
        }
        else if (riid == IID_IMediaBuffer)
        {
            AddRef();
            *ppv = (IMediaBuffer*)this;
            return NOERROR;
        }
        else
        {
            return E_NOINTERFACE;
        }
    }

    // IMediaBuffer methods
    STDMETHODIMP SetLength(DWORD length) {m_dataLength = length; return NOERROR;}
    STDMETHODIMP GetMaxLength(DWORD *pMaxLength) {*pMaxLength = sizeof(m_pData); return NOERROR;}
    STDMETHODIMP GetBufferAndLength(BYTE **ppBuffer, DWORD *pLength)
    {
        if (ppBuffer)
        {
            *ppBuffer = m_pData;
        }
        if (pLength)
        {
            *pLength = m_dataLength;
        }
        return NOERROR;
    }
    void Init(ULONG ulData)
    {
        m_dataLength = ulData;
    }

protected:
    // Statically allocated buffer used to hold audio data returned by IMediaObject
    BYTE m_pData[AudioSamplesPerSecond * AudioBlockAlign];

    // Amount of data currently being held in m_pData
    ULONG m_dataLength;
};

/// <summary>
/// Asynchronous IStream implementation that captures audio data from Kinect audio sensor in a background thread
/// and lets clients read captured audio from any thread.
/// </summary>
class KinectAudioStream : public IStream
{
public:
    /////////////////////////////////////////////
    // KinectAudioStream methods

    /// <summary>
    /// KinectAudioStream constructor.
    /// </summary>
    KinectAudioStream(IMediaObject *pKinectDmo);

    /// <summary>
    /// KinectAudioStream destructor.
    /// </summary>
    ~KinectAudioStream();

    /// <summary>
    /// Starts capturing audio data from Kinect sensor.
    /// </summary>
    /// <returns>S_OK on success, otherwise failure code.</returns>
    HRESULT StartCapture();

    /// <summary>
    /// Starts capturing audio data from Kinect sensor.
    /// </summary>
    /// <returns>S_OK on success, otherwise failure code.</returns>
    HRESULT StopCapture();

    /////////////////////////////////////////////
    // IUnknown methods
    STDMETHODIMP_(ULONG) AddRef() { return InterlockedIncrement(&m_cRef); }
    STDMETHODIMP_(ULONG) Release()
    {
        UINT ref = InterlockedDecrement(&m_cRef);
        if (ref == 0)
        {
            delete this;
        }
        return ref;
    }
    STDMETHODIMP QueryInterface(REFIID riid, void **ppv)
    {
        if (riid == IID_IUnknown)
        {
            AddRef();
            *ppv = (IUnknown*)this;
            return S_OK;
        }
        else if (riid == IID_IStream)
        {
            AddRef();
            *ppv = (IStream*)this;
            return S_OK;
        }
        else
        {
            return E_NOINTERFACE;
        }
    }

    /////////////////////////////////////////////
    // IStream methods
    STDMETHODIMP Read(void *,ULONG,ULONG *);
    STDMETHODIMP Write(const void *,ULONG,ULONG *);
    STDMETHODIMP Seek(LARGE_INTEGER,DWORD,ULARGE_INTEGER *);
    STDMETHODIMP SetSize(ULARGE_INTEGER);
    STDMETHODIMP CopyTo(IStream *,ULARGE_INTEGER,ULARGE_INTEGER *,ULARGE_INTEGER *);
    STDMETHODIMP Commit(DWORD);
    STDMETHODIMP Revert();
    STDMETHODIMP LockRegion(ULARGE_INTEGER,ULARGE_INTEGER,DWORD);
    STDMETHODIMP UnlockRegion(ULARGE_INTEGER,ULARGE_INTEGER,DWORD);
    STDMETHODIMP Stat(STATSTG *,DWORD);
    STDMETHODIMP Clone(IStream **);

private:
    // Number of audio buffers (chunks of overall ring buffer) used to capture Kinect audio data
    static const UINT NumBuffers = 20;
    typedef std::stack<CStaticMediaBuffer*> BufferPool;
    typedef std::queue<CStaticMediaBuffer*> CircularBufferQueue;
    
    // Number of references to this object
    UINT                    m_cRef;

    // Media object used to capture audio
    IMediaObject*           m_pKinectDmo;

    // Event used to signal that capture thread should stop capturing audio
    HANDLE                  m_hStopEvent;

    // Event used to signal that there's captured audio data ready to be read
    HANDLE                  m_hDataReady;

    // Audio capture thread
    HANDLE                  m_hCaptureThread;

    // Pool of unused buffers ready to be used for writing captured audio data
    BufferPool              m_BufferPool;

    // Circular buffer queue that contains audio data ready for reading by stream clients
    CircularBufferQueue     m_ReadBufferQueue;

    // Buffer where most recently captured audio data is being written
    CStaticMediaBuffer*     m_CurrentWriteBuffer;

    // Buffer from which stream client is currently reading audio data
    CStaticMediaBuffer*     m_CurrentReadBuffer;

    // Next index to be read within current read buffer
    ULONG                   m_CurrentReadBufferIndex;

    // Total number of bytes read so far by audio stream client
    ULONG                   m_BytesRead;

    // Critical section used to synchronize multithreaded access to captured audio data
    CRITICAL_SECTION        m_Lock;

    /// <summary>
    /// Get the next buffer available for writing audio data.
    /// First attempts to find a free buffer in buffer pool and, if none is available, grabs the
    /// oldest (most stale) buffer from circular queue of buffers ready for reading and re-uses it.
    /// </summary>
    /// <returns>Media buffer ready for audio capture, or NULL if none was found.</returns>
    CStaticMediaBuffer*     GetWriteBuffer();

    /// <summary>
    /// Release an audio buffer back into buffer pool.
    /// </summary>
    /// <param name="pBuffer">Buffer to be released.</param>
    void                    ReleaseBuffer(CStaticMediaBuffer* pBuffer);

    /// <summary>
    /// Release all audio buffers back into buffer pool.
    /// </summary>
    void                    ReleaseAllBuffers();

    /// <summary>
    /// Add captured audio data to the circular queue of buffers ready for client reading.
    /// </summary>
    /// <param name="pData">Pointer to audio data to be added to queue.</param>
    /// <param name="cbData">Number of bytes to be added to queue.</param>
    void                    QueueCapturedData(BYTE *pData, UINT cbData);

    /// <summary>
    /// Add buffer full of captured audio data to the circular queue of buffers ready for client reading.
    /// </summary>
    /// <param name="pBuffer">Buffer holding captured audio data.</param>
    void                    QueueCapturedBuffer(CStaticMediaBuffer *pBuffer);

    /// <summary>
    /// Perform one read operation from current read buffer until either we completely fill the specified
    /// client buffer or we read until the end of (exhaust) the current read buffer.
    /// If we exhaust the current read buffer, we get the oldest available buffer from circular queue and
    /// make that into our current read buffer.
    /// </summary>
    /// <param name="ppbData">
    /// In/Out pointer to client's audio data buffer to be filled.
    /// </param>
    /// <param name="pcbData">
    /// In/Out pointer to number of bytes remaining to be filled in client's audio data buffer.
    /// </param>
    void                    ReadOneBuffer(BYTE **ppbData, ULONG* pcbData);

    /// <summary>
    /// Starting address for audio capture thread.
    /// </summary>
    /// <param name="pParam">
    /// Thread data passed to the function using the lpParameter parameter of the CreateThread function.
    /// </param>
    /// <returns>Non-zero if thread ended successfully, zero in case of failure</returns>
    static DWORD WINAPI     CaptureThread(LPVOID pParam);

    /// <summary>
    /// Audio capture thread. Captures audio data in a loop until it is signaled to stop.
    /// </summary>
    /// <returns>Non-zero if thread ended successfully, zero in case of failure</returns>
    DWORD WINAPI            CaptureThread();

    /// <summary>
    /// Indicates whether this stream is currently capturing audio data.
    /// </summary>
    /// <returns>TRUE if audio data is currently being captured, FALSE otherwise.</returns>
    BOOL IsCapturing()
    {
        return (m_hStopEvent != NULL) && (WaitForSingleObject(m_hStopEvent,0) != WAIT_OBJECT_0);
    }
};