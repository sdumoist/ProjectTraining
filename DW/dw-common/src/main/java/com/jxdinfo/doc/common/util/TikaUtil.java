package com.jxdinfo.doc.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class TikaUtil {
    private TikaUtil() {
    }

    public static String parseToString(String path) throws IOException, TikaException {
        Tika tika = new Tika();
        Metadata metadata = new Metadata();
        try(InputStream stream = new BufferedInputStream(new FileInputStream(new File(path)))){
            Reader reader = tika.parse(stream,metadata);

            return tika.parseToString(stream);
        }
    }

    public static Map<String,Object> autoParse(String path) throws IOException, SAXException, TikaException {
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(1048576000);//the content of the document's body as a plain-text string
        //ContentHandler handler = new ToXMLContentHandler();//the XHTML content of the whole document as a string
        Map<String,Object> ret = new HashMap<>();
        Metadata metadata = new Metadata();
        try (InputStream stream = new BufferedInputStream(new FileInputStream(new File(path)))) {
            parser.parse(stream, handler, metadata);
            ret.put("contentType",metadata.get("Content-Type"));
            ret.put("content",handler.toString());
            return ret;
        }
    }
}
