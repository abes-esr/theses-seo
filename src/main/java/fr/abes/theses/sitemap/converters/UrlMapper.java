package fr.abes.theses.sitemap.converters;

import co.elastic.clients.elasticsearch.core.search.Hit;
import fr.abes.theses.sitemap.dto.UrlSiteMap;
import fr.abes.theses.sitemap.dto.UrlThese;
import fr.abes.theses.sitemap.model.These;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlMapper {
    @Value("${racine}")
    private String racine;
    public UrlThese theseToUrlThese(Hit<These> theseHit, Boolean afficheDocument) {
        return UrlThese.builder()
                .loc(!afficheDocument? racine.concat(theseHit.id()).concat("?domaine=theses"):racine.concat("api/v1/document/".concat(theseHit.id())))
                .priority("0.5")
                .build();
    }
    public UrlThese racine() {
        return UrlThese.builder()
                .loc(racine)
                .priority("1.0")
                .build();
    }
    public UrlSiteMap geturlSiteMap(int start, String dateInsertion) {
        return UrlSiteMap.builder()
                .loc(racine.concat("sitemap.xml?start=".concat(String.valueOf(start))))
                .lastmod(dateInsertion)
                .build();
    }
}
