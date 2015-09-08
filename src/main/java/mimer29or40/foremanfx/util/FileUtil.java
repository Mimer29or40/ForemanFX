package mimer29or40.foremanfx.util;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileUtil
{
    public static File directoryChooser(String title)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showDialog(null, title);
        return chooser.getSelectedFile();
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
