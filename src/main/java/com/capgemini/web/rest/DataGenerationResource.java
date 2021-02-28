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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Arrays;
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

    @GetMapping("/regeneratedatas")
    public ResponseEntity<String> getRegenerateData() {
        log.debug("REST request to generate DATAs");

        for (Individu i : individuService.findAll(PageRequest.of(0, 1000))) {
            individuService.delete(i.getId());
        }
        for (Groupe g : groupeService.findAll(PageRequest.of(0, 1000))) {
            groupeService.delete(g.getId());
        }
        for (Lieu l : lieuService.findAll(PageRequest.of(0, 1000))) {
            lieuService.delete(l.getId());
        }


        Lieu village = new Lieu();
        village.setNom("Village Gaulois");
        village.setLatitude(48.64759412857947);
        village.setLongitude(-2.003596545829376);
        lieuService.save(village);

        Lieu lutece = new Lieu();
        lutece.setNom("Lutece");
        lutece.setLatitude(448.8569575753533);
        lutece.setLongitude(2.3421079333023087);
        lieuService.save(lutece);

        Groupe irrecductibles = createAndSaveNewIGroupe("Les Irréductibles Gaulois", "Un village peuplé d'irréductibles Gaulois résiste encore et toujours à l'envahisseur.", "Quelque part en Armorique",
            "1987-02-05T11:34:00Z", "document/Irreductibles.pdf", "application/pdf", new HashSet<>(Arrays.asList(village)));
        Groupe perso2nd = createAndSaveNewIGroupe("Les personnages secondaires", null, "Capitale de la Gaule",
            "1992-03-06T12:35:08Z", "document/perso_2nd.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", new HashSet<>(Arrays.asList(lutece)));
        Groupe bagarreurs = createAndSaveNewIGroupe("Les bagarreurs", "Ils adorent se taper dessus", null,
            "1995-05-23T23:59:59Z", "document/bagarre.html", "text/html", new HashSet<>(Arrays.asList(village, lutece)));

        createAndSaveNewIndividu("Asterix", 1.35, "1958-02-26T09:10:00Z", Couleur.BLOND, "Casque ailé", "images/asterix.png", "image/png", new HashSet<>(Arrays.asList(irrecductibles)));
        createAndSaveNewIndividu("Obelix", 1.93, "1212-12-12T12:12:12Z", Couleur.ROUX, "Casque", "images/obelix.png", "image/png", new HashSet<>(Arrays.asList(irrecductibles, bagarreurs)));
        createAndSaveNewIndividu("Idefix", null, null, Couleur.BLANC, null, "images/idefix.png", "image/png", null);
        createAndSaveNewIndividu("Abraracourcix", null, null, Couleur.ROUX, "Couvre-chef", "images/abraracourcix.png", "image/png", new HashSet<>(Arrays.asList(irrecductibles, bagarreurs)));
        createAndSaveNewIndividu("Agecanonix", 0.73, "1052-06-06T12:34:56Z", null, null, "images/agecanonix.png", "image/png", new HashSet<>(Arrays.asList(bagarreurs, perso2nd)));
        createAndSaveNewIndividu("Assurancetourix", null, "1111-11-11T11:11:11Z", Couleur.BLOND, null, "images/assurancetourix.png", "image/png", new HashSet<>(Arrays.asList(irrecductibles)));
        createAndSaveNewIndividu("Bonemine", 1.66, null, null, null, "images/bonemine.png", "image/png", null);
        createAndSaveNewIndividu("Cetautomatix", null, "1945-06-06T17:53:41Z", Couleur.BLOND, null, "images/cetautomatix.png", "image/png", new HashSet<>(Arrays.asList(irrecductibles, bagarreurs)));
        createAndSaveNewIndividu("Falbala", 1.85, "2008-02-29T00:35:00Z", Couleur.BLOND, null, "images/falbala.png", "image/png", new HashSet<>(Arrays.asList(irrecductibles, perso2nd)));
        createAndSaveNewIndividu("Ordralfabetix", null, null, Couleur.BLOND, null, "images/ordralfabetix.png", "image/png", new HashSet<>(Arrays.asList(irrecductibles, bagarreurs, perso2nd)));
        createAndSaveNewIndividu("Panoramix", 1.88, null, Couleur.BLANC, "dégarni", "images/panoramix.png", "image/png", new HashSet<>(Arrays.asList(irrecductibles)));

        return new ResponseEntity<>("Terminé avec succès ", HttpStatus.OK);
    }

    private Groupe createAndSaveNewIGroupe(String nom, String description, String adresse, String dateCreation, String resourceNameDoc, String resourceNameDocType, Set<Lieu> lieuSet) {
        Groupe groupe = new Groupe();
        groupe.setNom(nom);
        groupe.setDescription(description);
        groupe.setAdresse(adresse);
        groupe.setDateCreation(Instant.parse(dateCreation));
        if (resourceNameDoc != null) {
            groupe.setPieceJointe(getBytesFromResourceByName(resourceNameDoc));
            groupe.setPieceJointeContentType(resourceNameDocType);
        }
        groupe.setEstSitues(lieuSet);
        groupeService.save(groupe);
        return groupe;
    }

    private Individu createAndSaveNewIndividu(String nom, Double taille, String ddn, Couleur couleurCheveux, String coiffure, String resourceNamePhoto, String resourceNamePhotoType, Set<Groupe> groupeSet) {
        Individu individu;
        individu = new Individu();
        individu.setNom(nom);
        individu.setTaille(taille);
        if (ddn != null)
            individu.setDateDeNaissance(Instant.parse(ddn));
        individu.setCouleurCheveux(couleurCheveux);
        individu.setCoiffure(coiffure);
        if (resourceNamePhoto != null) {
            individu.setPhoto(getBytesFromResourceByName(resourceNamePhoto));
            individu.setPhotoContentType(resourceNamePhotoType);
        }
        individu.setAppartientAS(groupeSet);
        individuService.save(individu);
        return individu;
    }

    private byte[] getBytesFromResourceByName(String resourceName) {
        byte[] returnedValue = null;

        try {
            File file = new File(getClass().getClassLoader().getResource(resourceName).getFile());
            returnedValue = Files.readAllBytes(file.toPath());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error managing file :" + resourceName);
        }
        return returnedValue;
    }

}
