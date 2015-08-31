package mimer29or40.foremanfx.util;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

public class Util
{
    public static File directoryChooser(String title)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showDialog(null, title);
        return chooser.getSelectedFile();
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
        { string = string + s.toString() + " "; }
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

    public static <T> List<T> union(List<T> list1, List<T> list2)
    {
        Set<T> set = new HashSet<T>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<T>(set);
    }

    public static <T> List<T> intersection(List<T> list1, List<T> list2)
    {
        List<T> list = new ArrayList<T>();

        for (T t : list1)
        {
            if (list2.contains(t))
            {
                list.add(t);
            }
        }
        return list;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map)
    {
        Map<K, V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K, V>> st = map.entrySet().stream();

        st.sorted(Comparator.comparing(Map.Entry::getValue)).forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }
}
