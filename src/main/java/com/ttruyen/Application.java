package com.ttruyen;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Application {

    private static final String UTF_8 = "UTF-8";

    /**
     * Find page with keyword
     *
     * @param keyword
     * @param path is path directory
     * @return
     * @throws IOException
     */
    private static List<Integer> findPageWithKeyword(String keyword, String path) throws IOException {
        List<Integer> listPage = new ArrayList<>();

        File file = new File(path);

        if (file.isDirectory()) {
            File[] files = file.listFiles();

            for (File filePage : files) {
                String content = readFileToString(filePage, UTF_8).toLowerCase();
                if (content.contains(keyword.toLowerCase())) {
                    Integer page = Integer.parseInt(filePage.getName().replaceAll(".txt",""));
                    listPage.add(page);
                }
            }
        }

        Collections.sort(listPage);

        return listPage;
    }

    /**
     * Find position page first, middle, last page
     *
     * @param listPage
     * @param option is param, if option = 1 is first page, 2 is middle page, 3 is last page
     * @return page
     */
    private static Integer findOptionPage(List<Integer> listPage, int option) {
        if (listPage.size() > 0) {
            if (option == 1) {
                return listPage.get(0);
            }

            if (option == 2) {
                int index = listPage.size() / 2;
                return listPage.get(index);
            }

            if (option == 3) {
                return listPage.get(listPage.size() - 1);
            }
        }

        return -1;
    }

    /**
     * Read file to string
     *
     * @param file
     * @param encoding
     * @return
     * @throws IOException
     */
    private static String readFileToString(File file, String encoding) throws IOException {
        InputStream in = null;
        BufferedReader reader = null;
        StringBuilder out = new StringBuilder();
        try {
            in = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(in, encoding));

            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe) {
                // ignore
            }
        }
        return out.toString();
    }

    /**
     * Write string to file
     *
     * @param file
     * @param data
     * @param encoding
     * @throws IOException
     */
    private static void writeStringToFile(File file, String data, String encoding) throws IOException {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
            out.write(data);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    /**
     * Find page with keyword and option
     *
     * @param keyword
     * @param option
     * @param path
     * @throws IOException
     */
    public static void findBook(String keyword, int option, String path, String pathResult) throws IOException {
        long start = System.currentTimeMillis();
        List<Integer> listPage = findPageWithKeyword(keyword, path);
        Integer page = findOptionPage(listPage, option);
        long end = System.currentTimeMillis() - start;
        if (page == -1) {
            System.out.println("Not found page");
            return;
        }
        System.out.println("Path book: " + path);
        System.out.println("Keyword: " + keyword);
        System.out.println("Option: " + (option == 1 ? "First page" : (option == 2 ? "Middle page" : "Last page")));
        System.out.println("Page: " + page);
        System.out.println("Time: " + end / 1000);
        String data = keyword + " - " + page + " - " + (end / 1000);
        writeStringToFile(new File(pathResult + "result.txt"), data, UTF_8);
        System.out.println("Result path: " + pathResult + "result.txt");
    }

    public static void main(String[] args) throws IOException {
        findBook("Vương Trạch Vinh", 2,"C:\\book", "C:\\");
    }
}
