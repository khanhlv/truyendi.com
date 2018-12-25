package com.ttruyen.parse;

import com.ttruyen.core.Const;
import com.ttruyen.core.MappingDB;
import com.ttruyen.core.UserAgent;
import com.ttruyen.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class ParseTruyenCV {

    public List<Chapter> readChapter(String mediaId, String type) throws Exception {
        List<Chapter> listChapter = new ArrayList<Chapter>();

        Map<String, String> mapData = new LinkedHashMap<String, String>();

        mapData.put("showChapter", "1");
        mapData.put("media_id", mediaId);
        mapData.put("number", "1");
        mapData.put("page", "9999");
        mapData.put("type", type);

        Document doc = Jsoup.connect("http://truyencv.com/index.php")
                .userAgent(UserAgent.getUserAgent())
                .timeout(Const.TIMEOUT)
                .data(mapData)
                .post();

        Element els = Jsoup.parse(doc.html());
        els.select(".text-muted").remove();

        Elements li = els.select(".item");

        for(Element elsLi : li) {
            Elements elsA = elsLi.select("a");

            Chapter chapter = new Chapter();
            chapter.setLink(elsA.attr("href"));
            chapter.setName(elsA.text());

            listChapter.add(chapter);
        }

        Thread.sleep(Const.SLEEP_CONNECT);

        if (!els.select(".label").hasClass("label-free")) {
            Collections.reverse(listChapter);
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
        int mediaId = NumberUtils.toInt(els.select(".rating-container .basic").attr("data-id"));

        detail.setMediaId(mediaId);
        detail.setMetaKeyword(doc.head().select("meta[name=keywords]").attr("content"));

        Elements elsInfo = els.select(".truyencv-detail-block .truyencv-detail-info-block");
        detail.setImage(elsInfo.select(".img-responsive").attr("src"));

        List<String> listAuthor = new ArrayList<String>();

        for(Element elItem : elsInfo.select(".info .list .item")) {

            String label = StringUtils.trim(elItem.select(".item-label").text());

            if (StringUtils.equals(label, "Tác giả:")) {
                listAuthor.add(StringUtils.trim(elItem.select(".item-value").text()));
            }

            if (StringUtils.equals(label, "Tình trạng:")) {
                detail.setStatus(MappingDB.MAP_STATUS.get(StringUtils.trim(elItem.select(".item-value").text())));

//                System.out.println(detail.getStatus());
            }
        }
        detail.setListAuthor(listAuthor);

        List<String> listCate = new ArrayList<String>();
        for(Element elCate : elsInfo.select(".categories li a")) {
            listCate.add(MappingDB.MAP_CATEGORY.get(StringUtils.trim(elCate.text())));
//            System.out.println(StringUtils.trim(elCate.text()));
        }
        detail.setListCategory(listCate);

        detail.setSource("TruyenCV");

        Elements elsDesc = els.select("#truyencv-detail-introduction");

        Whitelist whitelist = new Whitelist();
        whitelist.addTags("br");

        String contentClean = Jsoup.clean(elsDesc.select(".brief").html(), whitelist);

        detail.setDescription(contentClean.trim().replaceAll("<br>", "\n"));

//        System.out.println(detail.getMediaId());
//        System.out.println(detail.getImage());
//        System.out.println(detail.getDescription());

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

        Elements elsContent = els.select(".list-group-item-table");

        Elements elsPage = els.select(".pagination .active");
        int pageCurrent = NumberUtils.toInt(elsPage.text(), 1);
        if (pageCurrent > limit && limit != -1) {
            return;
        }
        if (pageCurrent < page) {
            return;
        }

        for(Element elP : elsContent) {
            Category category = new Category();
            category.setLink(elP.select(".content .info .title a").attr("href"));
            category.setName(elP.select(".content .info .title a").text());

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

        Elements elsContent = doc.body().select("#js-truyencv-read-content #js-truyencv-content");

        elsContent.select("div").remove();
        elsContent.select("p").remove();

        Whitelist whitelist = new Whitelist();
        whitelist.addTags("br");

        String contentClean = Jsoup.clean(elsContent.html(), whitelist);
        content.setContent(contentClean);

        Thread.sleep(Const.SLEEP_CONNECT);

        return content;
    }

    public static void main(String[] args) {
        ParseTruyenCV parseTruyenCV = new ParseTruyenCV();
        try {
//            List<Category> listC = parseTruyenCV.readCategory("http://truyencv.com/nu-hiep/", 1, -1);
//            listC.forEach((category) -> {
//                System.out.println(category.getName() + " - " + category.getLink());
//            });

//            parseTruyenCV.readDetail("http://truyencv.com/han-ngu-tham-tu-lung-danh/");

            //18513
            //18821
            parseTruyenCV.readChapter("18821", "huyen huyen chi sieu than tieu ma vuong").forEach(chapter -> {
                System.out.println(chapter.getName() + " - " + chapter.getLink());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
