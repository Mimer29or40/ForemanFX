package mimer29or40.foremanfx.util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListUtil
{
    public static <T> List<T> union(List<T> list1, List<T> list2)
    {
        Set<T> set = new HashSet<>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<>(set);
    }

    public static <T> List<T> intersection(List<T> list1, List<T> list2)
    {
        return list1.stream().filter(list2::contains).collect(Collectors.toList());
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map)
    {
        Map<K, V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K, V>> st = map.entrySet().stream();

        st.sorted(Comparator.comparing(Map.Entry::getValue)).forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }

    public static String listToString(List list)
    {
        String string = "";
        for (Object s : list)
        { string = string + s.toString() + " "; }
        return string;
    }
}
