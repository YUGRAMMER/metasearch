package com.forcewin.metasearch.apiserver;

import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RESTApi {

    static Logger logger = LoggerFactory.getLogger(RESTApi.class);
    @GetMapping("/home")
    public String welcome(){
        return "The server is running well.";
    }

    @GetMapping("/search")
    public JSONObject search(String query)throws Exception{
        JSONObject result = new JSONObject();
        
        // 당근마켓 크롤링
        String URL1 = "https://www.daangn.com/search/" + URLEncoder.encode(query,"UTF-8");

        Document doc = Jsoup.connect(URL1).get();
        doc.html();
        Elements elem = doc.select("div.flea-market-article flat-card");
        logger.info(elem.toString());
        return result;
    }
}
