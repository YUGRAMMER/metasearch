package com.forcewin.metasearch.apiserver;

import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.List;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RESTApi {

    static Logger logger = LoggerFactory.getLogger(RESTApi.class);
    @GetMapping("/home")
    public String welcome(){
        return "The server is running well.";
    }

    @GetMapping("/search")
    @ResponseBody
    public String search(
        @RequestParam(name="query",defaultValue="") String query,
        @RequestParam(name="pageNum",defaultValue = "0")int pageNum,
        @RequestParam(name="market",defaultValue="0")int market
    )throws Exception{
        JSONObject result = new JSONObject();
       
        int Code = 200;
        String Message = "Ok";
        JSONArray contents = new JSONArray();
        Connection conn = null;
        try{
                
            if( query.equals("") ){
                Code = 403;
                throw new Exception("Query is null.");
            }
           switch( market ){
               case 0 :
                    Code = 403;
                    throw new Exception("Select Market Number");
               case 1 :
                    // 당근마켓

                    // 당근마켓 크롤링
                    String URL1 = "https://www.daangn.com/search/" + URLEncoder.encode(query,"UTF-8");

                    // 당근마켓 페이지 처리
                    if( pageNum != 0 ) URL1 += "/more/flea_market?page=" + pageNum ;

                    conn = Jsoup.connect(URL1);
                    Document doc = conn.get();
                    doc.html();
                    Elements elems = doc.select(".flea-market-article");
                    //logger.info(elems.toString());

                    logger.info("elems size:"+elems.size());
                    for( Element elem : elems ){
                        JSONObject item = new JSONObject();
                        String item_name = elem.select(".article-title").text();
                        String item_price = elem.select(".article-price").text();
                        String img_path = elem.select("img").attr("src");
                        String item_url = "https://www.daangn.com" + elem.select(".flea-market-article-link").attr("href");

                        item.put("item_name", item_name);
                        item.put("item_price", item_price);
                        item.put("img_path", img_path);
                        item.put("item_url", item_url);

                        contents.put(item);
                    }
                break;
                case 2 :
                    try (Playwright playwright = Playwright.create()) {
                        Browser browser = playwright.webkit().launch();
                        Page page = browser.newPage();
                        page.navigate("https://m.bunjang.co.kr/search/products?q="+ URLEncoder.encode(query,"UTF-8"));
                        List<ElementHandle> list = page.querySelectorAll("#modal");
                        logger.info("size:"+list.size());
                        /*
                        for( ElementHandle eh :  list ){
                            JSONObject item = new JSONObject();
                            String item_name = eh.querySelector(".sc-fQejPQ").innerText();
                            String item_price = eh.querySelector(".sc-clNaTc").innerText();
                            String img_path = eh.querySelector("img").getAttribute("src");
                            String item_url = "https://m.bunjang.co.kr"+eh.querySelector(".sc-RcBXQ").getAttribute("href");
                            item.put("item_name", item_name);
                            item.put("item_price", item_price);
                            item.put("img_path", img_path);
                            item.put("item_url", item_url);
    
                            contents.put(item);
                        }
                        */
                    }
                
                break;
                case 3 :

                break;
                default:
                    Code = 403;
                    throw new Exception("Unknown Market Number.");
            }
        }catch(Exception e){
            if( Code == 200 ){
                Code = 500;
                logger.error("ERROR",e);
            }
            
            Message = e.getMessage();
            
        }finally{
            result.put("Code",Code);
            result.put("Message",Message);
            result.put("contents",contents);
        }


        return result.toString();
    }
}
