package com.capgemini.web.rest;

import com.capgemini.domain.Groupe;
import com.capgemini.domain.Individu;
import com.capgemini.domain.Lieu;
import com.capgemini.domain.enumeration.Couleur;
import com.capgemini.service.GroupeService;
import com.capgemini.service.IndividuService;
import com.capgemini.service.LieuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class DataGenerationResource {

    private final Logger log = LoggerFactory.getLogger(DataGenerationResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IndividuService individuService;
    private final GroupeService groupeService;
    private final LieuService lieuService;

    public DataGenerationResource(IndividuService individuService, GroupeService groupeService, LieuService lieuService) {
        this.individuService = individuService;
        this.groupeService = groupeService;
        this.lieuService = lieuService;
    }

    @GetMapping("/generatedatas")
    public ResponseEntity<String> getGenerateData() {
        log.debug("REST request to generate DATAs");

        Set<Lieu> lieuSet = new HashSet<>();
        Lieu lieu = new Lieu();
        lieu.setNom("Village Gaulois");
        lieu.setLatitude(48.64759412857947);
        lieu.setLongitude(-2.003596545829376);
        lieuService.save(lieu);
        lieuSet.add(lieu);

        Set<Groupe> groupeSet = new HashSet<>();
        Groupe groupe = new Groupe();
        groupe.setNom("Les Irréductibles Gaulois");
        groupe.setDescription("Un village peuplé d'irréductibles Gaulois résiste encore et toujours à l'envahisseur.");
        groupe.setAdresse("Quelque part en Armorique");
        groupe.setDateCreation(Instant.parse("1987-02-05T11:34:00Z"));
        groupe.setPieceJointe(getBytesFromResourceByName("Irreductibles.pdf"));
        groupe.setPieceJointeContentType("application/pdf");
        groupe.setEstSitues(lieuSet);
        groupeService.save(groupe);
        groupeSet.add(groupe);

        Individu individu1 = new Individu();
        individu1.setNom("Asterix");
        individu1.setTaille(1.35);
        individu1.setDateDeNaissance(Instant.parse("1958-02-26T09:10:00Z"));
        individu1.setCouleurCheveux(Couleur.BLOND);
        individu1.setCoiffure("Casque");
        individu1.setPhoto(getBytesFromResourceByName("asterix1.png"));
        individu1.setPhotoContentType("image/png");
        individu1.setAppartientAS(groupeSet);
        individuService.save(individu1);

        Individu individu2 = new Individu();
        individu2.setNom("Obelix");
        individu2.setTaille(1.93);
        individu2.setDateDeNaissance(Instant.parse("1212-12-12T12:12:12Z"));
        individu2.setCouleurCheveux(Couleur.ROUX);
        individu2.setCoiffure("Casque");
        individu2.setPhoto(getBytesFromResourceByName("obelix.png"));
        individu2.setPhotoContentType("image/png");
        individu2.setAppartientAS(groupeSet);
        individuService.save(individu2);

        Individu individu3 = new Individu();
        individu3.setNom("Idefix");
        individu3.setCouleurCheveux(Couleur.BLANC);
        individu3.setPhoto(getBytesFromResourceByName("idefix.png"));
        individu3.setPhotoContentType("image/png");
        individu3.setAppartientAS(groupeSet);
        individuService.save(individu3);


        return new ResponseEntity<>("Terminé avec succès ", HttpStatus.OK);
    }

    private byte[] getBytesFromResourceByName(String resourceName) {
        byte[] returnedValue = null;

        File file = new File(getClass().getClassLoader().getResource(resourceName).getFile());
        try {
            returnedValue = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnedValue;
    }

}
