package com.capgemini.service;

import com.capgemini.domain.Groupe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface DataGenerationService {

    void generateDemoDataSet();
    void generateRandomDataSet(Integer totalIndividuToCreate, Integer totalGroupeToCreate, Integer totalLieuToCreate);
}
