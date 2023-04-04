package fr.abes.theses.sitemap.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JacksonXmlRootElement(namespace = "http://www.sitemaps.org/schemas/sitemap/0.9", localName = "sitemapindex")
public class ResponseUrlSiteMap implements ResponseUrl {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "sitemap", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
    private List<UrlSiteMap> liste;
}
