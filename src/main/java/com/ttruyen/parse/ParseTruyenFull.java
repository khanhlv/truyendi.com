package com.ttruyen.parse;

import com.ttruyen.core.Const;
import com.ttruyen.core.MappingDB;
import com.ttruyen.core.UserAgent;
import com.ttruyen.model.Category;
import com.ttruyen.model.Chapter;
import com.ttruyen.model.Content;
import com.ttruyen.model.Detail;
import com.ttruyen.utils.GZipUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ParseTruyenFull {

    public List<Chapter> readChapter(String url, int page) throws Exception {
        List<Chapter> listChapter = new ArrayList<Chapter>();
        for (int i = 1; i <= page; i++) {

            Document doc = Jsoup.connect(url + "trang-" + i)
                    .userAgent(UserAgent.getUserAgent())
                    .timeout(Const.TIMEOUT)
                    .get();

            Element els = doc.body();
            Elements li = els.select("#list-chapter .row .list-chapter li");

            for(Element elsLi : li) {
                Chapter chapter = new Chapter();
                chapter.setLink(elsLi.select("a").attr("href"));
                chapter.setName(elsLi.select("a").text());

                listChapter.add(chapter);
            }
            Thread.sleep(Const.SLEEP_CONNECT);
        }

        return listChapter;
    }

    public Detail readDetail(String url) throws Exception {
        Detail detail = new Detail();

        Document doc = Jsoup.connect(url)
                .userAgent(UserAgent.getUserAgent())
                .timeout(Const.TIMEOUT)
                .get();

        Element els = doc.body();
        int totalPage = NumberUtils.toInt(els.select("#total-page").val());

        detail.setPageTotal(totalPage);
        detail.setMetaKeyword(doc.head().select("meta[name=keywords]").attr("content"));

        Elements elsInfo = els.select(".col-info-desc .info-holder");
        detail.setImage(elsInfo.select("img").attr("src"));

        List<String> listAuthor = new ArrayList<String>();
        for(Element elAuthor : elsInfo.select(".info a[itemprop=author]")) {
            listAuthor.add(elAuthor.text().trim());
        }
        detail.setListAuthor(listAuthor);

        List<String> listCate = new ArrayList<String>();
        for(Element elCate : elsInfo.select(".info a[itemprop=genre]")) {
            listCate.add(MappingDB.MAP_CATEGORY.get(elCate.text().trim()));
        }
        detail.setListCategory(listCate);

        detail.setSource(elsInfo.select(".source").text().trim());

        if (elsInfo.select(".text-primary").size() > 0) {
            detail.setStatus(MappingDB.MAP_STATUS.get(elsInfo.select(".text-primary").text().trim()));
        }

        if (elsInfo.select(".text-success").size() > 0) {
            detail.setStatus(MappingDB.MAP_STATUS.get(elsInfo.select(".text-success").text().trim()));
        }

        if (elsInfo.select(".text-warning").size() > 0) {
            detail.setStatus(MappingDB.MAP_STATUS.get(elsInfo.select(".text-warning").text().trim()));
        }

        Elements elsDesc = els.select(".col-info-desc .desc");

        Whitelist whitelist = new Whitelist();
        whitelist.addTags("br");

        String contentClean = Jsoup.clean(elsDesc.select(".desc-text").html(), whitelist);

        detail.setDescription(contentClean.trim().replaceAll("<br>", "\n"));

        Thread.sleep(Const.SLEEP_CONNECT);

        return detail;
    }

    public List<Category> readCategory(String url, int startPage, int limit) throws Exception {
        List<Category> listCategory = new ArrayList<Category>();

        readCategory(url, startPage, listCategory, limit);

        return listCategory;
    }

    private void readCategory(String url, int page, List<Category> listCategory, int limit) throws Exception {

        Document doc = Jsoup.connect(url + "trang-"+page)
                .userAgent(UserAgent.getUserAgent())
                .timeout(Const.TIMEOUT)
                .get();

        Element els = doc.body();

        Elements elsContent = els.select(".col-truyen-main .list-truyen .row");

        Elements elsPage = els.select(".pagination .active");
        elsPage.select(".sr-only").remove();
        int pageCurrent = NumberUtils.toInt(elsPage.text(), 1);
        if (pageCurrent > limit && limit != -1) {
            return;
        }
        if (pageCurrent < page) {
            return;
        }

        for(Element elP : elsContent) {
            Category category = new Category();
            category.setLink(elP.select(".truyen-title a").attr("href"));
            category.setName(elP.select(".truyen-title a").text());

            listCategory.add(category);
        }

        Thread.sleep(Const.SLEEP_CONNECT);

        readCategory(url, pageCurrent + 1, listCategory, limit);
    }

    public Content readContent(String ur) throws Exception {

        Content content = new Content();

        Document doc = Jsoup.connect(ur)
                .userAgent(UserAgent.getUserAgent())
                .timeout(Const.TIMEOUT)
                .get();

        Elements elsContent = doc.body().select(".chapter .chapter-c");

        elsContent.select("div").remove();
        elsContent.select("a").remove();

        Whitelist whitelist = new Whitelist();
        whitelist.addTags("br");
        whitelist.addTags("p");

        String contentClean = Jsoup.clean(elsContent.html(), whitelist).replaceAll("<p>","").replaceAll("</p>","<br><br>");
        content.setContent(contentClean);

        Thread.sleep(Const.SLEEP_CONNECT_CONTENT);

        return content;
    }

    public static void main(String[] args) {
        try {
            ParseTruyenFull parseTruyenFull = new ParseTruyenFull();
//            List<Category> listC = parseTruyenFull.readCategory("http://truyenfull.vn/the-loai/tien-hiep/", 1, -1);
//            listC.forEach((category) -> {
//                System.out.println(category.getName() + " - " + category.getLink());
//            });

//            Detail detail = parseTruyenFull.readDetail("http://truyenfull.vn/luu-manh-lao-su/");
//            detail.getListCategory().stream().forEach(System.out::println);
//            detail.getListAuthor().stream().forEach(System.out::println);
//            System.out.println(detail.getStatus());
//
//            List<Chapter> chapterList = parseTruyenFull.readChapter("http://truyenfull.vn/luu-manh-lao-su/", detail.getPageTotal());
//
//            chapterList.stream().forEach((d) -> {
//                System.out.println(d.getName().split(":")[0].trim());
//            });

            Content content = parseTruyenFull.readContent("https://truyenfull.vn/thuong-thien/chuong-797/");

            System.out.println(content.getContent());
//            GZipUtil.compressGZIP(content.getContent(), new File("/Users/khanhlv/ttruyen/data/"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
