package com.example.explorecalijpa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.explorecalijpa.business.TourPackageService;
import com.example.explorecalijpa.business.TourService;
import com.example.explorecalijpa.model.Difficulty;
import com.example.explorecalijpa.model.Region;
import com.example.explorecalijpa.model.Tour;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class ExplorecaliJpaApplication implements CommandLineRunner {
    @Value("${ec.importfile}")
    private String importFile;

    @Autowired
    private TourPackageService tourPackageService;

    @Autowired
    private TourService tourService;

    public static void main(String[] args) {

        SpringApplication.run(ExplorecaliJpaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        createTourAllPackages();
        createToursFromFile(importFile);
    }

    /**
     * Initialize all the known tour packages
     */
    private void createTourAllPackages() {
        tourPackageService.createTourPackage("BC", "Backpack Cal");
        tourPackageService.createTourPackage("CC", "California Calm");
        tourPackageService.createTourPackage("CH", "California Hot springs");
        tourPackageService.createTourPackage("CY", "Cycle California");
        tourPackageService.createTourPackage("DS", "From Desert to Sea");
        tourPackageService.createTourPackage("KC", "Kids California");
        tourPackageService.createTourPackage("NW", "Nature Watch");
        tourPackageService.createTourPackage("SC", "Snowboard Cali");
        tourPackageService.createTourPackage("TC", "Taste of California");
    }

    /**
     * Create tour entities from an external file
     */
    private List<Tour> createToursFromFile(String fileToImport) throws IOException {
        record TourFromFile(String packageName, String title, String description,
                String blurb, Integer price, String length,
                String bullets, String keywords,
                String difficulty, String region) {
        }
        return new ObjectMapper().readValue(new File(fileToImport),
            new TypeReference<List<TourFromFile>>() {})
        .stream().map(t -> tourService
            .createTour(t.packageName(), 
                        t.title(),
                        t.description(), 
                        t.blurb(), 
                        t.price(),
                        t.length(), 
                        t.bullets(),
                        t.keywords(),
                        Difficulty.valueOf(t.difficulty()),
                        Region.valueOf(t.region()))
        ).toList();
    }
}