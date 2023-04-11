package fr.abes.theses.sitemap.controller;

import fr.abes.theses.sitemap.builder.SearchQueryBuilder;
import fr.abes.theses.sitemap.dto.ResponseUrl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
@Slf4j
@RestController
@RequestMapping("/")
public class SitemapController {

    @Autowired
    SearchQueryBuilder searchQueryBuilder;

    @Value("${robots.path}")
    String robotsPath;

    @ResponseBody
    @GetMapping(value = "SiteMap", produces = MediaType.APPLICATION_XML_VALUE)
    @ApiOperation(
            value = "renvoyer le sitemap",
            notes = "sans paramètre start, fournit l'index")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Opération terminée avec succès"),
            @ApiResponse(code = 400, message = "Mauvaise requête"),
            @ApiResponse(code = 503, message = "Service indisponible"),
    })
    public ResponseUrl sitemap (
            @RequestParam @ApiParam(name = "start", value = "début du lot d'urls", example = "10000") Optional<Integer>start) throws Exception {
        try {
            if (start.isPresent())
                return searchQueryBuilder.urls(start.orElse(0));
            else
                return searchQueryBuilder.sites();

        } catch (Exception e) {
            log.error(e.toString());
            throw e;
        }
    }

    @ResponseBody
    @GetMapping(value = "/robots.txt", produces = "text/plain")
    @ApiOperation(
            value = "renvoyer le robots.txt")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Opération terminée avec succès"),
            @ApiResponse(code = 400, message = "Mauvaise requête"),
            @ApiResponse(code = 503, message = "Service indisponible"),
    })
    public String robots() throws IOException {

        URL url = new URL(robotsPath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine.concat("\n"));
        }
        in.close();
        return content.toString();
    }
}
