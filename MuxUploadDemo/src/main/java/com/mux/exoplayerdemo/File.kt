package com.mux.exoplayerdemo

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okio.*
import java.io.File

/**
 * RequestBody based on a file that reports the number of bytes written at regular intervals until
 * the file has been fully uploaded.
 */
fun File.asCountingFileBody(mediaType: MediaType?, callback: RequestBody.(Long) -> Unit): RequestBody {
    return CountingFileBody(this@asCountingFileBody, mediaType, callback)
}

/**
 * RequestBody based on a file that reports the number of bytes written at regular intervals until
 * the file has been fully uploaded.
 */
fun File.asCountingFileBody(contentType: String?, callback: RequestBody.(Long) -> Unit): RequestBody =
    asCountingFileBody(contentType?.toMediaType(), callback)

private class CountingFileBody constructor(
    private val file: File,
    private val mediaType: MediaType?,
    private val callback: RequestBody.(Long) -> Unit,
) : RequestBody() {

    private var totalBytes: Long = 0

    companion object {
        const val READ_LENGTH: Long = 2048
    }

    override fun contentLength(): Long {
        return file.length()
    }

    override fun contentType(): MediaType? = mediaType

    override fun writeTo(sink: BufferedSink) {
        file.source().use {
            var readBytes: Long
            do {
                readBytes = it.read(sink.buffer, READ_LENGTH)
                totalBytes += readBytes
                // TODO: Why double bytes??
                callback(totalBytes / 2)
                sink.flush()
            } while (readBytes >= 0)
        }
    }
}
