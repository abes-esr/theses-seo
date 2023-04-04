package fr.abes.theses.sitemap.converters;

import co.elastic.clients.elasticsearch.core.search.Hit;
import fr.abes.theses.sitemap.dto.UrlSiteMap;
import fr.abes.theses.sitemap.dto.UrlThese;
import fr.abes.theses.sitemap.model.These;

public class UrlMapper {

    public UrlThese theseToUrlThese(Hit<These> theseHit, Boolean afficheDocument) {
        return UrlThese.builder()
                .loc(!afficheDocument? "http://www.theses.fr/".concat(theseHit.id()):"http://www.theses.fr/".concat(theseHit.id()).concat("/document"))
                .priority("0.5")
                .build();
    }
    public UrlThese racine() {
        return UrlThese.builder()
                .loc("http://www.theses.fr/")
                .priority("1.0")
                .build();
    }
    public UrlSiteMap geturlSiteMap(int start, String dateInsertion) {
        return UrlSiteMap.builder()
                .loc("http://www.theses.fr/SiteMap?start=".concat(String.valueOf(start)))
                .lastmod(dateInsertion)
                .build();
    }
}
