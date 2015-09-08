package mimer29or40.foremanfx.util;

public class StringUtil
{
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
}
