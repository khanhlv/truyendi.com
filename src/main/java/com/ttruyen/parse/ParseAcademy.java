package com.ttruyen.parse;

import com.ttruyen.core.Const;
import com.ttruyen.core.UserAgent;
import com.ttruyen.model.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

public class ParseAcademy {

    public void readContent(String ur) throws Exception {

        Content content = new Content();

        Document doc = Jsoup.connect(ur)
                .userAgent(UserAgent.getUserAgent())
                .timeout(Const.TIMEOUT)
                .get();

        Elements elsContent = doc.body().select(".pdp-container");

        String title = elsContent.select("h1[data-auid=\"PDP_ProductName\"]").text();

        String image = elsContent.select("div[data-auid=\"PDP_MediaClick\"]").select("img").toString();

        System.out.println(title);
        System.out.println(image);
    }

    public static void main(String[] args) {
        try {
            new ParseAcademy().readContent("https://www.academy.com/shop/pdp/remington-golden-bullet-22-lr-40-grain-rimfire-ammunition#repChildCatid=3043460");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
