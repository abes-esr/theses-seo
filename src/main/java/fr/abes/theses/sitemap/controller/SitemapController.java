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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    @GetMapping(value = "/robots.txt", produces = "text/plain")
    public ResponseEntity robots() throws IOException {
        InputStream inputStream = new FileInputStream(new File(robotsPath));
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(Files.size(Paths.get(robotsPath)));
        return new ResponseEntity(inputStreamResource, headers, HttpStatus.OK);
    }
}
