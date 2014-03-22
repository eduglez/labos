package ooxmlreports;


import java.io.BufferedReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.*;

import org.apache.velocity.*;
import org.apache.velocity.app.Velocity;

public class Helper {

    public static ByteArrayOutputStream openPattern(String path, VelocityContext context) {
        ZipInputStream zis = null;
        ZipOutputStream zos = null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buf = new byte[512];

        try {

            zis = new ZipInputStream(new FileInputStream(path));
            zos = new ZipOutputStream(baos);

            ZipEntry zeO;
            ZipEntry zeN;
            while ((zeO = zis.getNextEntry()) != null) {

//                System.out.println(zeO.getName());
                if (zeO.getName().equals("word/document.xml")) {
                    zeN = new ZipEntry("word/document.xml");

                    InputStream i=doIt(zis, context);
                    zos.putNextEntry(zeN);
                    int size;
                    while ((size = i.read(buf, 0, buf.length)) != -1) {
                        zos.write(buf, 0, size);
                    }
                    zos.flush();
                    zos.closeEntry();

                } else {
                    zeN = new ZipEntry(zeO.getName());
                    zeN.setSize(zeO.getSize());
                    zeN.setCrc(zeO.getCrc());
                    zos.putNextEntry(zeN);

                    int size;
                    while ((size = zis.read(buf, 0, buf.length)) != -1) {
                        zos.write(buf, 0, size);
                    }


                    zos.flush();

                    zos.closeEntry();
                }
            }

            zos.close();
            zis.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (zis != null) {
                    zis.close();
                }

                if (zos != null) {
                    zos.close();
                }
            } catch (IOException ioe) {
            }
        }

        return baos;

    }

    private static ByteArrayInputStream doIt(InputStream in, VelocityContext context) {
        CharArrayWriter caw = new CharArrayWriter();

        CharArrayWriter cawOutput = new CharArrayWriter();

        
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
        Pattern p = Pattern.compile("(\\x3c\\x21\\x2d\\x2d)|(\\x2d\\x2d\\x3e)");
        Matcher m = p.matcher("");
        
            while (br.ready()) {
                m.reset(br.readLine());
                caw.write(m.replaceAll(""));
                caw.write("\n");
            }
            caw.flush();

            Velocity.init();
            Velocity.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
            Velocity.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
            Velocity.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
            Velocity.evaluate(context, cawOutput, "Reporting", new CharArrayReader(caw.toCharArray()));

            return new ByteArrayInputStream(cawOutput.toString().getBytes("UTF-8"));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }


}
