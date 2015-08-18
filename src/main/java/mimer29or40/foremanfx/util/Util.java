package mimer29or40.foremanfx.util;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Util
{
    public static File directoryChooser(String title)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showDialog(null, title);
        return chooser.getSelectedFile();
    }

    public static String removeWhitespace(String s)
    {
        return s.replaceAll("\\s+", "");
    }

    public static boolean isNullOrWhitespace(String string)
    {
        return string == null || isWhitespace(string);
    }

    private static boolean isWhitespace(String string)
    {
        int length = string.length();
        if (length > 0)
        {
            for (int start = 0, middle = length / 2, end = length - 1; start <= middle; start++, end--)
            {
                if (string.charAt(start) > ' ' || string.charAt(end) > ' ')
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static String listToString(List list)
    {
        String string = "";
        for (Object s : list)
        { string = string + s.toString(); }
        return string;
    }

    public static List<String> readFile(File file)
    {
        try
        {
            return Files.readAllLines(file.toPath());
        }
        catch (IOException e)
        {
            System.out.println("File: " + file.getName() + " cannot be read.");
        }
        return null;
    }
}
