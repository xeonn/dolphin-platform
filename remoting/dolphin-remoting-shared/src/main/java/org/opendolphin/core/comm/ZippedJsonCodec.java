package org.opendolphin.core.comm;

import groovy.lang.Closure;
import groovy.lang.Reference;
import org.codehaus.groovy.runtime.EncodingGroovyMethods;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.runtime.StringGroovyMethods;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * A json codec that uses gzip compression for requests beyond a given size.
 * In general, one can assume that it is better to just rely on the HTTP mechanisms for
 * gzipping requests and responses but in reality, not every server is able to handle that -
 * or the effort to make it run prohibitively high.
 * In order to transfer the binary (compressed) parts reliably, we use base 64 encoding
 * on top of gzip, which adds to the processing time and transfer size.
 * In the context of OpenDolphin commands, this facility compresses to between 10 and 20 percent.
 */
public class ZippedJsonCodec extends JsonCodec {

    private static final Logger LOG  = Logger.getLogger(ZippedJsonCodec.class.getName());

    @Override
    public String encode(List<Command> commands) {
        final Reference<String> result = new Reference<String>(super.encode(commands));
        if (StringGroovyMethods.size(result.get()) <= maxUnzippedSize) {
            LOG.finest("not zipping since text size " + String.valueOf(StringGroovyMethods.size(result.get())) + " does not exceed " + String.valueOf(maxUnzippedSize));
            return ((String) (result.get()));
        }


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(StringGroovyMethods.size(result.get()));
        try {
            IOGroovyMethods.withWriter(new GZIPOutputStream(outputStream), encoding, new Closure<Object>(this, this) {
                public void doCall(Writer writer) {
                    try {
                        writer.write(result.get());
                    } catch (IOException e) {
                        throw new RuntimeException("Internal Exception", e);
                    }
                }

            });
        } catch (IOException e) {
            throw new RuntimeException("Internal Exception", e);
        }

        LOG.finest("zipped text of size " + String.valueOf(StringGroovyMethods.size(result.get())));
        result.set(EncodingGroovyMethods.encodeBase64(outputStream.toByteArray()).toString());
        LOG.finest("to zip of size " + String.valueOf(StringGroovyMethods.size(result.get())));
        return ((String) (result.get()));
    }

    @Override
    public List<Command> decode(String transmitted) {
        if (StringGroovyMethods.size(transmitted) <= MIN_ENCODED_SIZE) return super.decode(transmitted);

        final Reference<String> raw = new Reference<String>(transmitted);
        try {
            byte[] bytes = EncodingGroovyMethods.decodeBase64(transmitted);
            GZIPInputStream zipStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
            raw.set(IOGroovyMethods.getText(zipStream, encoding));
            LOG.finest("unzipped transmission of size " + String.valueOf(bytes.length) + " to text of size " + String.valueOf(StringGroovyMethods.size(raw.get())));
        } catch (Exception e) {
            LOG.fine("could not unzip transmission, assuming it is not zipped.\nText:" + transmitted);
        }


        return super.decode(raw.get());
    }

    /**
     * The max amount of characters in the transfer string that will not result in compressing.
     * Whatever fits in one "packet" of the underlying infrastructure makes no sense compressing.
     * Note that depending of the String encoding, one character can result in multiple bytes for
     * transmission.
     * It makes sense to keep this value rather low.
     */
    private int maxUnzippedSize = 500;
    private String encoding = "UTF-8";
    protected final int MIN_ENCODED_SIZE = 16;
}
