package cn.fanstars.framework.common.util.fan;

import cn.hutool.core.date.DatePattern;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class FanDateUtils {

    /**
     * 获取当天日期 yyyyMMdd
     */
    public static String getDate() {
        LocalDate today = LocalDate.now();
        return today.format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN));
    }

    /**
     * 获取当前时间 yyyy-MM-dd HH:mm:ss
     */
    public static String getTime() {
        return formatDate(LocalDateTime.now());
    }

    /**
     * 将日期格式化为 yyyy-MM-dd HH:mm:ss 字符串
     *
     * @param date 要格式化的日期对象
     * @return 格式化后的日期字符串
     */
    public static String formatDate(Date date) {
        return formatDate(date, DatePattern.NORM_DATETIME_PATTERN);
    }

    /**
     * 将日期格式化为 yyyyMMdd 字符串
     *
     * @param date 要格式化的日期对象
     * @return 格式化后的日期字符串
     */
    public static String formatDateToYMD(Date date) {
        return formatDate(date, DatePattern.PURE_DATE_PATTERN);
    }

    /**
     * 将日期格式化为指定格式的字符串
     *
     * @param date       要格式化的日期对象
     * @param dateFormat 指定的日期格式
     * @return 格式化后的日期字符串
     */
    public static String formatDate(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(date);
    }

    /**
     * 将日期格式化为 yyyy-MM-dd HH:mm:ss 字符串
     *
     * @param dateTime 要格式化的日期对象
     * @return 格式化后的日期字符串
     */
    public static String formatDate(LocalDateTime dateTime) {
        return formatDate(dateTime, DatePattern.NORM_DATETIME_PATTERN);
    }

    /**
     * 将日期格式化为 yyyyMMdd 字符串
     *
     * @param dateTime 要格式化的日期对象
     * @return 格式化后的日期字符串
     */
    public static String formatDateToYMD(LocalDateTime dateTime) {
        return formatDate(dateTime, DatePattern.PURE_DATE_PATTERN);
    }

    /**
     * 将日期格式化为 yyyyMMddHHmmss 字符串（无分隔符）
     *
     * @param dateTime 要格式化的日期对象
     * @return 格式化后的日期字符串
     */
    public static String formatDateToPureDatetime(LocalDateTime dateTime) {
        return formatDate(dateTime, DatePattern.PURE_DATETIME_PATTERN);
    }

    /**
     * 将日期格式化为指定格式的字符串
     *
     * @param dateTime   要格式化的日期对象
     * @param dateFormat 指定的日期格式
     * @return 格式化后的日期字符串
     */
    public static String formatDate(LocalDateTime dateTime, String dateFormat) {
        return dateTime.format(DateTimeFormatter.ofPattern(dateFormat));
    }

    /**
     * 格式化 LocalDate 为字符串，使用默认格式 "yyyy-MM-dd"。
     *
     * @param date 要格式化的 LocalDate 对象
     * @return 格式化后的日期字符串
     */
    public static String formatLocalDate(LocalDate date) {
        return formatLocalDate(date, "yyyy-MM-dd");
    }

    /**
     * 格式化 LocalDate 为字符串，使用自定义格式。
     *
     * @param date    要格式化的 LocalDate 对象
     * @param pattern 自定义的日期格式
     * @return 格式化后的日期字符串
     */
    public static String formatLocalDate(LocalDate date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }

    /**
     * 将字符串解析为日期对象
     *
     * @param dateString 要解析的日期字符串
     * @return 解析后的日期对象
     * @throws ParseException 如果解析失败，抛出ParseException异常
     */
    public static Date parseDate(String dateString) throws ParseException {
        return parseDate(dateString, DatePattern.NORM_DATETIME_PATTERN);
    }

    /**
     * 将字符串解析为日期对象
     *
     * @param dateString 要解析的日期字符串
     * @param dateFormat 指定的日期格式
     * @return 解析后的日期对象
     * @throws ParseException 如果解析失败，抛出ParseException异常
     */
    public static Date parseDate(String dateString, String dateFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.parse(dateString);
    }

    /**
     * 将字符串解析为 LocalDate yyyyMMdd
     */
    public static LocalDate parseLocalDateByYMD(String dateString) {
        return parseLocalDate(dateString, DatePattern.PURE_DATE_PATTERN);
    }

    /**
     * 将字符串根据指定的格式解析为 LocalDate
     */
    public static LocalDate parseLocalDate(String dateString, String dateFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return LocalDate.parse(dateString, formatter);
    }

    /**
     * 将字符串解析为 LocalDateTime yyyyMMdd
     */
    public static LocalDateTime parseLocalDateTimeByYMD(String dateString) {
        return parseLocalDate(dateString, DatePattern.PURE_DATE_PATTERN).atStartOfDay();
    }

    /**
     * 将字符串解析为 LocalDateTime yyyy-MM-dd HH:mm:ss
     */
    public static LocalDateTime parseLocalDateTime(String dateString) {
        return parseLocalDateTime(dateString, DatePattern.NORM_DATETIME_PATTERN);
    }

    /**
     * 将字符串根据指定的格式解析为 LocalDateTime
     */
    public static LocalDateTime parseLocalDateTime(String dateString, String dateFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return LocalDateTime.parse(dateString, formatter);
    }

    /**
     * 根据指定的时间单位和值，对当前时间进行加减操作
     *
     * @param chronoUnit 时间单位，可选值为 ChronoUnit 中定义的枚举常量
     * @param value      加减的值，可以为正数或负数
     * @return 计算后的日期
     */
    public static LocalDateTime addOrSubtractDate(ChronoUnit chronoUnit, int value) {
        return addOrSubtractDate(LocalDateTime.now(), chronoUnit, value);
    }

    /**
     * 根据指定的时间单位和值，对指定时间进行加减操作
     *
     * @param localDateTime 要进行加减的时间
     * @param chronoUnit    时间单位，可选值为 ChronoUnit 中定义的枚举常量
     * @param value         加减的值，可以为正数或负数
     * @return 计算后的日期
     */
    public static LocalDateTime addOrSubtractDate(LocalDateTime localDateTime, ChronoUnit chronoUnit, int value) {
        if (chronoUnit == ChronoUnit.MINUTES) {
            return localDateTime.plusMinutes(value);
        } else if (chronoUnit == ChronoUnit.HOURS) {
            return localDateTime.plusHours(value);
        } else if (chronoUnit == ChronoUnit.DAYS) {
            return localDateTime.plusDays(value);
        } else if (chronoUnit == ChronoUnit.WEEKS) {
            return localDateTime.plusWeeks(value);
        } else if (chronoUnit == ChronoUnit.MONTHS) {
            return localDateTime.plusMonths(value);
        } else if (chronoUnit == ChronoUnit.YEARS) {
            return localDateTime.plusYears(value);
        } else {
            throw new IllegalArgumentException("不支持的时间单位");
        }
    }

    /**
     * 计算给定时间单位和值所对应的毫秒数
     *
     * @param chronoUnit 时间单位，可以是ChronoUnit中定义的任何枚举值
     * @param value      要计算的时间单位的数量值
     * @return 给定时间单位和值所对应的毫秒数
     */
    public static long calculateMillis(ChronoUnit chronoUnit, int value) {
        return chronoUnit.getDuration().toMillis() * value;
    }

    /**
     * 将 LocalDateTime 转换为 Date
     *
     * @param localDateTime LocalDateTime 对象
     * @return Date 对象
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * 将LocalDateTime转换为时间戳
     *
     * @param localDateTime 要转换的LocalDateTime实例
     * @return 对应的时间戳
     */
    public static long convertToTimestamp(LocalDateTime localDateTime) {
        return localDateTimeToDate(localDateTime).getTime();
    }

    /**
     * 将毫秒级时间戳转换为 LocalDateTime
     *
     * @param timestamp 毫秒级时间戳
     * @return LocalDateTime 对象
     */
    public static LocalDateTime longToLDT(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    /**
     * 将秒级时间戳转换为 LocalDate
     *
     * @param timestamp 秒级时间戳
     * @return LocalDate 对象
     */
    public static LocalDate longToLDBySecond(long timestamp) {
        return Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 将毫秒级时间戳格式化为 yyyy-MM-dd HH:mm:ss 字符串
     *
     * @param millisTimestamp 毫秒级时间戳
     * @return 格式化后的日期字符串
     */
    public static String formatMillisTimestamp(long millisTimestamp) {
        Instant instant = Instant.ofEpochMilli(millisTimestamp);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return formatDate(dateTime);
    }

    /**
     * 将秒级时间戳格式化为 yyyy-MM-dd HH:mm:ss 字符串
     *
     * @param timestamp 秒级时间戳
     * @return 格式化后的日期字符串
     */
    public static String formatSecondTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return formatDate(dateTime);
    }

}
