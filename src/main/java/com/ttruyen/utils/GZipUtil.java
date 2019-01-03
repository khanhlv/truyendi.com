package com.ttruyen.utils;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;

public final class GZipUtil {

    public static void compressGZIP(String data, File output) throws IOException {
        try (GzipCompressorOutputStream out = new GzipCompressorOutputStream(new FileOutputStream(output))){
            out.write(data.getBytes("UTF-8"));
        }
    }
    public static void compressGZIP(File input, File output) throws IOException {
        try (GzipCompressorOutputStream out = new GzipCompressorOutputStream(new FileOutputStream(output))){
            IOUtils.copy(new FileInputStream(input), out);
        }
    }

    public static byte[] compressGZIP(final String data) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GzipCompressorOutputStream out = new GzipCompressorOutputStream(byteArrayOutputStream)){
            out.write(data.getBytes("UTF-8"));
            return byteArrayOutputStream.toByteArray();
        }
    }

    private static final int BUFFER_SIZE_BYTES = 512 * 1024;

    public static InputStream compress(final String data) throws IOException {
        final PipedInputStream compressedDataStream = new PipedInputStream(BUFFER_SIZE_BYTES);

        try (PipedOutputStream compressionOutput = new PipedOutputStream(compressedDataStream);
            GzipCompressorOutputStream out = new GzipCompressorOutputStream(compressionOutput)){
            out.write(data.getBytes("UTF-8"));

            return compressedDataStream;
        }

    }

    public static void decompressGZIP(File input, File output) throws IOException {
        try (GzipCompressorInputStream in = new GzipCompressorInputStream(new FileInputStream(input))){
            IOUtils.copy(in, new FileOutputStream(output));
        }
    }

    public static String decompressGZIP(File input) throws IOException {

        try (GzipCompressorInputStream in = new GzipCompressorInputStream(new FileInputStream(input))){
            return IOUtils.toString(in);
        }
    }

    public static String decompressGZIP(InputStream input) throws IOException {

        try (GzipCompressorInputStream in = new GzipCompressorInputStream(input)){
            return IOUtils.toString(in);
        }
    }

    public static void main(String[] args) throws IOException {
        InputStream ia = GZipUtil.compress("khánh khánh khánh khánh khánh");

        System.out.println("Done");

        OutputStream outStream = new FileOutputStream("D:/text.txt.gz");

        IOUtils.copy(ia, outStream);

        // System.out.println(GZipUtil.decompressGZIP(new File("/Users/khanhlv/ttruyen/text.txt.gz")));
    }
}
