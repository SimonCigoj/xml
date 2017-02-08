package hr.altima.util;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by simon on 8.2.2017.
 */
public class FileUtils {

    public static void createFileWithContent(final String content,final String filePath) throws IOException {
        createFileWithContent(content,filePath,true);
    }

    public static void createFileWithContent(final String content,final String filePath, final boolean appendTofile) throws IOException {
        Preconditions.checkArgument(StringUtils.isNotEmpty(filePath));
        Preconditions.checkArgument(StringUtils.isNotEmpty(content));

        File file = new File(filePath);
        file.getParentFile().mkdirs();

        BufferedWriter out = new BufferedWriter(new FileWriter(file,appendTofile));
        out.write(content);
        out.flush();
        out.close();
    }
}
