package fr.abes.theses.sitemap.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UrlThese {

    @JacksonXmlProperty(localName = "loc", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
    String loc;
    @JacksonXmlProperty(localName = "priority", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
    String priority;
}
