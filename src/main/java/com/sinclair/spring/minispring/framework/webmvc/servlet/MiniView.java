package com.sinclair.spring.minispring.framework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description 具体的页面
 * @Date 2021/1/28 17:52
 * @Author by Saiyong.Chen
 */
public class MiniView {

    private File viewFile;

    public MiniView(File templateFile) {
        this.viewFile = templateFile;
    }

    /**
     * 数据渲染并输出
     *
     * @param request
     * @param response
     * @param model
     */
    public void render(HttpServletRequest request, HttpServletResponse response, Map<String,?> model) {
        StringBuffer sb = new StringBuffer();
        try {
            RandomAccessFile ra = new RandomAccessFile(this.viewFile, "r");

            String line = null;
            while (null != (line = ra.readLine())) {
                line = new String(line.getBytes("ISO-8859-1"), "utf-8");
                Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}",Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(line);
                while(matcher.find()){
                    String paramName = matcher.group();
                    paramName  = paramName.replaceAll("￥\\{|\\}", "");
                    Object paramValue = model.get(paramName);
                    line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                    matcher = pattern.matcher(line);
                }
                sb.append(line);
            }
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理特殊字符
     * @param str
     * @return
     */
    public static String makeStringForRegExp(String str) {
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }
}
