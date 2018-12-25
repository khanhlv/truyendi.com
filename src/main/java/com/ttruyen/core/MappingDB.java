package com.ttruyen.core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MappingDB {
    public final static Map<String, String> MAP_CATEGORY = new LinkedHashMap<>();
    public final static Map<String, String> MAP_STATUS = new LinkedHashMap<String, String>();
    static  {
        MAP_CATEGORY.put("Tiên Hiệp", "1");
        MAP_CATEGORY.put("Kiếm Hiệp", "2");
        MAP_CATEGORY.put("Ngôn Tình", "3");
        MAP_CATEGORY.put("Đô Thị", "4");
        MAP_CATEGORY.put("Quan Trường", "5");
        MAP_CATEGORY.put("Võng Du", "6");
        MAP_CATEGORY.put("Khoa Huyễn", "7");
        MAP_CATEGORY.put("Huyền Huyễn", "8");
        MAP_CATEGORY.put("Dị Giới", "9");
        MAP_CATEGORY.put("Dị Năng", "10");
        MAP_CATEGORY.put("Quân Sự", "11");
        MAP_CATEGORY.put("Lịch Sử", "12");
        MAP_CATEGORY.put("Xuyên Không", "13");
        MAP_CATEGORY.put("Trọng Sinh", "14");
        MAP_CATEGORY.put("Trinh Thám", "15");
        MAP_CATEGORY.put("Thám Hiểm", "16");
        MAP_CATEGORY.put("Linh Dị", "17");
        MAP_CATEGORY.put("Sắc", "18");
        MAP_CATEGORY.put("Ngược", "19");
        MAP_CATEGORY.put("Sủng", "20");
        MAP_CATEGORY.put("Cung Đấu", "21");
        MAP_CATEGORY.put("Nữ Cường", "22");
        MAP_CATEGORY.put("Gia Đấu", "23");
        MAP_CATEGORY.put("Đông Phương", "25");
        MAP_CATEGORY.put("Đam Mỹ", "26");
        MAP_CATEGORY.put("Bách Hợp", "27");
        MAP_CATEGORY.put("Hài Hước", "28");
        MAP_CATEGORY.put("Điền Văn", "29");
        MAP_CATEGORY.put("Cổ Đại", "30");
        MAP_CATEGORY.put("Mạt Thế", "31");
        MAP_CATEGORY.put("Truyện Teen", "32");
        MAP_CATEGORY.put("Tiểu Thuyết Phương Tây", "33");
        MAP_CATEGORY.put("Phương Tây", "33");
        MAP_CATEGORY.put("Nữ Phụ", "34");
        MAP_CATEGORY.put("Light Novel", "35");
        MAP_CATEGORY.put("Văn học Việt Nam", "36");
        MAP_CATEGORY.put("Việt Nam", "36");
        MAP_CATEGORY.put("Đoản Văn", "37");
        MAP_CATEGORY.put("Khác", "38");
    }

    static  {
        MAP_STATUS.put("Đang ra", "0");
        MAP_STATUS.put("Full", "1");
        MAP_STATUS.put("Drop", "2");
        MAP_STATUS.put("Hoàn thành", "1");
    }

    public static void main(String[] args) {
        System.out.println(MAP_CATEGORY.get("Khác"));
    }
}
