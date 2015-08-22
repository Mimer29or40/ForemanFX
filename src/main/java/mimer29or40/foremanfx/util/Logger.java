package mimer29or40.foremanfx.util;

public class Logger
{
    private static void log(String logLevel, Object object)
    {
        System.out.println(String.format("[%s]: %s", logLevel, String.valueOf(object)));
    }

    public static void off(Object object)
    {
        log(OFF, object);
    }

    public static void off(String format, Object... objects)
    {
        off(String.format(format, objects));
    }

    public static void fatal(Object object)
    {
        log(FATAL, object);
    }

    public static void fatal(String format, Object... objects)
    {
        fatal(String.format(format, objects));
    }

    public static void error(Object object)
    {
        log(ERROR, object);
    }

    public static void error(String format, Object... objects)
    {
        error(String.format(format, objects));
    }

    public static void warn(Object object)
    {
        log(WARN, object);
    }

    public static void warn(String format, Object... objects)
    {
        warn(String.format(format, objects));
    }

    public static void info(Object object)
    {
        log(INFO, object);
    }

    public static void info(String format, Object... objects)
    {
        info(String.format(format, objects));
    }

    public static void debug(Object object)
    {
        log(DEBUG, object);
    }

    public static void debug(String format, Object... objects)
    {
        debug(String.format(format, objects));
    }

    public static void trace(Object object)
    {
        log(TRACE, object);
    }

    public static void trace(String format, Object... objects)
    {
        trace(String.format(format, objects));
    }

    public static void all(Object object)
    {
        log(ALL, object);
    }

    public static void all(String format, Object... objects)
    {
        all(String.format(format, objects));
    }

    public static final String OFF   = "OFF";
    public static final String FATAL = "FATAL";
    public static final String ERROR = "ERROR";
    public static final String WARN  = "WARN";
    public static final String INFO  = "INFO";
    public static final String DEBUG = "DEBUG";
    public static final String TRACE = "TRACE";
    public static final String ALL   = "ALL";
}
