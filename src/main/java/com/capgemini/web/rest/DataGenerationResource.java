package com.capgemini.web.rest;

import com.capgemini.service.DataGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DataGenerationResource {

    private final Logger log = LoggerFactory.getLogger(DataGenerationResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DataGenerationService dataGenerationService;

    public DataGenerationResource(DataGenerationService dataGenerationService) {
        this.dataGenerationService = dataGenerationService;
    }

    @GetMapping("/generateDemoDataSet")
    public ResponseEntity<String> getRegenerateData() {
        log.debug("REST request to generate DATAs for DEMO");

        dataGenerationService.generateDemoDataSet();

        return new ResponseEntity<>("Terminé avec succès ", HttpStatus.OK);
    }


    @GetMapping("/generateRandomDataSet")
    public ResponseEntity<String> generateRandomDataSet(Integer totalIndividuToCreate, Integer totalGroupeToCreate, Integer totalLieuToCreate) {
        log.debug("REST request to generate RANDOM DATAs");

        dataGenerationService.generateRandomDataSet(totalIndividuToCreate, totalGroupeToCreate, totalLieuToCreate);

        return new ResponseEntity<>("Terminé avec succès ", HttpStatus.OK);
    }


    @GetMapping("/eraseAllDatas")
    public ResponseEntity<String> eraseAllDatas(Boolean yesImSure) {
        log.debug("REST request to clear DATAs");
        if(yesImSure)
            dataGenerationService.clearAllDatas();

        return new ResponseEntity<>("Terminé avec succès ", HttpStatus.OK);
    }

}
