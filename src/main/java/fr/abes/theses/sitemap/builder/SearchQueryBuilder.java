package fr.abes.theses.sitemap.builder;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import fr.abes.theses.sitemap.converters.UrlMapper;
import fr.abes.theses.sitemap.dto.ResponseUrlSiteMap;
import fr.abes.theses.sitemap.dto.ResponseUrlThese;
import fr.abes.theses.sitemap.dto.UrlSiteMap;
import fr.abes.theses.sitemap.dto.UrlThese;
import fr.abes.theses.sitemap.model.These;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
public class SearchQueryBuilder {

    @Autowired
    private UrlMapper urlMapper;
    @Value("${es.hostname}")
    private String esHostname;
    @Value("${es.port}")
    private String esPort;
    @Value("${es.protocol}")
    private String esScheme;
    @Value("${es.username}")
    private String esUserName;
    @Value("${es.password}")
    private String esPassword;
    private ElasticsearchClient client;
    @Value("${es.theses.indexname}")
    private String esIndexName;
    @Value("${nbUrlsPage}")
    private String nbUrlsPage;


    private ElasticsearchClient getElasticsearchClient() throws Exception {
        if (this.client == null) {
            try {
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(esUserName, esPassword));

                final SSLContext sslContext = SSLContexts.custom()
                        .loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
                        .build();

                RestClient client = RestClient.builder(new HttpHost(esHostname, Integer.parseInt(esPort), esScheme)).setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider)
                        .setSSLContext(sslContext)
                        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                ).build();

                ElasticsearchTransport transport = new RestClientTransport(
                        client, new JacksonJsonpMapper());

                return new ElasticsearchClient(transport);
            } catch (Exception e) {
                log.error(e.toString());
                throw e;
            }
        }
        return this.client;
    }

    private MatchAllQuery buildMatchAllQuery() {
        MatchAllQuery.Builder builder = new MatchAllQuery.Builder();
        return builder.build()._toQuery().matchAll();
    }

    public ResponseUrlThese urls(Integer start) throws Exception {

        log.info("les urls de theses à partir de " + start);

        SearchResponse<These> response = this.getElasticsearchClient().search(
                s -> s
                        .index(esIndexName)
                        .query(q -> q.matchAll(this.buildMatchAllQuery()))
                        .from(start)
                        .size(Integer.parseInt(nbUrlsPage))
                        .sort(addTri())
                        .trackTotalHits(t -> t.enabled(Boolean.TRUE)),
                These.class
        );

        List<UrlThese> liste = new ArrayList<>();
        Iterator<Hit<These>> iterator = response.hits().hits().iterator();
        liste.add(urlMapper.racine());

        while (iterator.hasNext()) {
            Hit<These> theseHit = iterator.next();
            liste.add(urlMapper.theseToUrlThese(theseHit, false));
        }

        ResponseUrlThese res = new ResponseUrlThese();
        res.setListe(liste);
        return res;
    }

    public ResponseUrlSiteMap sites() throws Exception {

        log.info("les urls de sitemap...");

        SearchResponse<These> response = this.getElasticsearchClient().search(
                s -> s
                        .index(esIndexName)
                        .query(q -> q.matchAll(this.buildMatchAllQuery()))
                        .size(1)
                        .sort(addTri())
                        .trackTotalHits(t -> t.enabled(Boolean.TRUE)),
                These.class
        );
        long total = response.hits().total().value();
        long indexes = total / Long.parseLong(nbUrlsPage);
        indexes++;

        String dateInsert = response.hits().hits().get(0).source().getDateInsertionDansES();

        List<UrlSiteMap> liste = new ArrayList<>();
        for (int i = 0; i < indexes; i++) {
            int start = i * Integer.parseInt(nbUrlsPage);
            liste.add(urlMapper.geturlSiteMap(start, dateInsert));
        }

        ResponseUrlSiteMap res = new ResponseUrlSiteMap();
        res.setListe(liste);
        return res;
    }

    private List<SortOptions> addTri() {
        List<SortOptions> list = new ArrayList<>();
        SortOptions sort = new SortOptions.Builder().field(f -> f.field("dateInsertionDansES").order(SortOrder.Desc)).build();
        list.add(sort);
        return list;
    }
}
