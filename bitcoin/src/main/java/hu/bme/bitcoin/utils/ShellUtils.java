package hu.bme.bitcoin.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;

public class ShellUtils {

public static StringBuffer executeShell(String[] shell) throws IOException {
    Process ls = Runtime.getRuntime().exec(shell);
    InputStream inputStream = ls.getInputStream();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    String line = null;
    StringBuffer buffer = new StringBuffer();
    while ((line = bufferedReader.readLine()) != null) {
        buffer.append(line);
    }
    return buffer;
}
}
