package in.partake.base;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.twitter.Regex;


public final class Util {
    private static final Logger logger = Logger.getLogger(Util.class);
    private static final Random random = new Random();

    private static final Pattern REMOVETAG_PATTERN = Pattern.compile("(<!--.+?-->)|(<.+?>)", Pattern.DOTALL | Pattern.MULTILINE);
    private static final String ALNUM = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // ----------------------------------------------------------------------
    // UUID

    public static boolean isUUID(String str) {
        if (str == null)
            return false;

        try {
            UUID.fromString(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // ----------------------------------------------------------------------
    // Numeric

    public static long ensureMin(long value, long min) {
        if (value < min)
            return min;
        return value;
    }

    public static int ensureMin(int value, int min) {
        if (value < min)
            return min;
        return value;
    }

    public static int ensureRange(int value, int min, int max) {
        assert min <= max;
        if (value < min)
            return min;
        if (max < value)
            return max;
        return value;
    }

    // ----------------------------------------------------------------------
    // Text

    public static <T extends JSONable> JSONArray toJSONArray(List<T> list) {
        if (list == null)
            return null;

        JSONArray array = new JSONArray();
        for (JSONable jsonable : list)
            array.add(jsonable.toJSON());

        return array;
    }

    public static <T extends SafeJSONable> JSONArray toSafeJSONArray(List<T> list) {
        if (list == null)
            return null;

        JSONArray array = new JSONArray();
        for (SafeJSONable jsonable : list)
            array.add(jsonable.toSafeJSON());

        return array;
    }

    public static Boolean parseBooleanParameter(String value) {
        if ("true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value) || "checked".equalsIgnoreCase(value))
            return true;
        if ("false".equalsIgnoreCase(value) || "off".equalsIgnoreCase(value))
            return false;

        return null;
    }

    public static boolean parseBooleanParameter(String value, boolean defaultValue) {
        Boolean result = parseBooleanParameter(value);
        if (result != null)
            return result;
        else
            return defaultValue;
    }

    public static Map<UUID, List<String>> parseEnqueteAnswers(JSONObject map) {
        Map<UUID, List<String>> enqueteAnswers = new HashMap<UUID, List<String>>();
        for (Object entryObj : map.entrySet()) {
            Entry<?, ?> entry = (Entry<?, ?>) entryObj;
            if (!Util.isUUID(entry.getKey().toString()))
                continue;

            if (!(entry.getValue() instanceof JSONArray))
                continue;

            JSONArray array = (JSONArray) entry.getValue();
            List<String> answers = new ArrayList<String>();
            for (int i = 0; i < array.size(); ++i)
                answers.add(array.getString(i));

            enqueteAnswers.put(UUID.fromString(entry.getKey().toString()), answers);
        }

        return enqueteAnswers;
    }

    public static boolean isValidHashtag(String hashTag) {
        return Regex.AUTO_LINK_HASHTAGS.matcher(hashTag).matches();
    }

    public static String randomString(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            builder.append(ALNUM.charAt(random.nextInt(ALNUM.length())));
        }
        return builder.toString();
    }

    public static int codePointCount(String s) {
        return s.codePointCount(0, s.length());
    }

    public static String substring(String source, int startCodePoints) {
        final int endCodePoints = source.codePointCount(0, source.length());
        return substring(source, startCodePoints, endCodePoints);
    }

    public static String substring(String source, int startCodePoints, int endCodePoints) {
        final int startIndex = source.offsetByCodePoints(0, startCodePoints);
        final int endIndex = source.offsetByCodePoints(startIndex, endCodePoints - startCodePoints);
        return source.substring(startIndex, endIndex);
    }

    public static String shorten(String message, int maxLength) {
        if (message.codePointCount(0, message.length()) <= maxLength) { return message; }

        return substring(substring(message, 0, Math.max(maxLength - 3, 0)) + "...", 0, Math.max(maxLength, 0));
    }

    /**
     * 文字列から'#'と後続の文字列を取り除いたものを返す。
     * URLから # + fragment を取り除区などの用途を想定。
     *
     * @param str 改行を含まない加工対象文字列
     * @return '#'と後続の文字列を取り除いた文字列
     */
    public static String removeURLFragment(String str) {
        if (str == null) { return null; }
        return str.replaceAll("#.*", "");
    }

    public static void writeFromFile(BufferedWriter writer, File inFile, String encode) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), encode));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("@charset") || line.isEmpty()) continue;
                writer.write(line);
                writer.newLine();
            }
        } finally {
            try {
                reader.close();
            } catch (IOException ignore) {
                logger.warn("Reader#close throw IOException, but it's ignored.", ignore);
            }
        }
    }

    // ----------------------------------------------------------------------
    // Image

    public static boolean isImageContentType(String s) {
        if (s == null) { return false; }

        if ("image/jpeg".equals(s)) { return true; }
        if ("image/png".equals(s)) { return true; }
        if ("image/gif".equals(s)) { return true; }
        if ("image/pjpeg".equals(s)){ return true; }

        return false;
    }

    /**
     * file の内容を byte array に変換する
     */
    public static byte[] getContentOfFile(File file) throws IOException {
        if (file == null)
            return new byte[0];

        InputStream is = new BufferedInputStream(new FileInputStream(file));
        return getContentOfInputStream(is);
    }

    public static byte[] getContentOfInputStream(InputStream is) throws IOException {
        if (is == null)
            return null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final int SIZE = 1024 * 1024;
            byte[] buf = new byte[SIZE];

            int len;
            while ((len = is.read(buf)) > 0) {
                baos.write(buf, 0, len);
            }

            return baos.toByteArray();
        } finally {
            is.close();
        }

    }

    public static InputStream createInputSteram(String resource) throws IOException {
        InputStream stream = Util.class.getResourceAsStream(resource);
        if (stream != null) { return stream; }

        stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        if (stream != null) { return stream; }

        try {
            return new FileInputStream(new File(resource));
        } catch (Exception e) {
            return null;
        }
    }

    // ----------------------------------------------------------------------
    // HTML

    /**
     * validなHTMLから、HTMLタグとコメントを取り除く。
     *
     * @param html 加工するHTML文字列
     * @return HTMLタグとコメントを取り除いた文字列
     */
    public static String removeTags(String html) {
        if (html == null) { return null; }
        return REMOVETAG_PATTERN.matcher(html).replaceAll("");
    }

    // ----------------------------------------------------------------------
    // URI

    /**
     * Javascriptの同名関数と同様、
     * 文字列をURIのパラメータとして使用できるようにエンコードを施す。
     * @see https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/encodeURIComponent
     */
    public static String encodeURIComponent(String uri) {
        try {
            return URLEncoder.encode(uri, "UTF-8")
                                 .replaceAll("\\+", "%20")
                                 .replaceAll("\\%21", "!")
                                 .replaceAll("\\%27", "'")
                                 .replaceAll("\\%28", "(")
                                 .replaceAll("\\%29", ")")
                                 .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            logger.warn("safely returns empty string.", e);
            return "";
        }
    }
}
