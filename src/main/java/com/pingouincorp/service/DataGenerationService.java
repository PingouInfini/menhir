package com.pingouincorp.service;


public interface DataGenerationService {

    void clearAllDatas();
    void generateDemoDataSet();
    void generateRandomDataSet(Integer totalIndividuToCreate, Integer totalGroupeToCreate, Integer totalLieuToCreate);
}
