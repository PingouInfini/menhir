package com.pingouincorp.service.impl;


import com.pingouincorp.domain.Groupe;
import com.pingouincorp.domain.Individu;
import com.pingouincorp.domain.Lieu;
import com.pingouincorp.domain.enumeration.Couleur;
import com.pingouincorp.service.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;

@Service
@Transactional
public class DataGenerationServiceImpl implements DataGenerationService {
    private final IndividuService individuService;
    private final GroupeService groupeService;
    private final LieuService lieuService;

    private final List<String[]> noms;
    private final List<String[]> prenoms;
    private final List<String[]> organisations;
    private final List<String[]> localites;

    public DataGenerationServiceImpl(final IndividuService individuService, final GroupeService groupeService,
                                     final LieuService lieuService) throws IOException {
        this.individuService = individuService;
        this.groupeService = groupeService;
        this.lieuService = lieuService;

        this.noms = extractDataFromCsvFile("noms.csv");
        this.prenoms = extractDataFromCsvFile("prenoms.csv");
        this.localites = extractDataFromCsvFile("localite.csv");
        this.organisations = extractDataFromCsvFile("organisation.csv");
    }

    @Override
    public void clearAllDatas() {
        for (Individu i : individuService.findAll(PageRequest.of(0, 1000))) {
            individuService.delete(i.getId());
        }
        for (Groupe g : groupeService.findAll(PageRequest.of(0, 1000))) {
            groupeService.delete(g.getId());
        }
        for (Lieu l : lieuService.findAll(PageRequest.of(0, 1000))) {
            lieuService.delete(l.getId());
        }
    }

    @Override
    public void generateDemoDataSet() {
        clearAllDatas();

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
    }

    @Override
    public void generateRandomDataSet(final Integer totalIndividuToCreate, final Integer totalGroupeToCreate, final Integer totalLieuToCreate) {
        List<Lieu> listeLieux = new ArrayList<>();
        List<Groupe> listeGroupes = new ArrayList<>();
        if (totalLieuToCreate != null && totalLieuToCreate > 0) {
            listeLieux = generateLieu(totalLieuToCreate);
        }
        if (totalGroupeToCreate != null && totalGroupeToCreate > 0) {
            listeGroupes = generateGroupe(totalGroupeToCreate, listeLieux);
        }
        if (totalIndividuToCreate != null && totalIndividuToCreate > 0) {
            final List<Individu> listeIndividus = generateIndividu(totalIndividuToCreate, listeGroupes);
        }
    }

    private List<Lieu> generateLieu(final Integer totalLieuToCreate) {
        final List<Lieu> ret = new ArrayList<>();
        for (int i = 0; i < totalLieuToCreate; i++) {
            final int randomIndexLocalite = (int) Math.round(Math.random() * (this.localites.size() - 1));

            Double latitude = null;
            try {
                latitude = Double.valueOf(this.localites.get(randomIndexLocalite)[1]);
            } catch (Exception e1) {
                System.out.println(e1);
            }

            Double longitude = null;
            try {
                longitude = Double.valueOf(this.localites.get(randomIndexLocalite)[2]);
            } catch (Exception e2) {
                System.out.println(e2);
            }

            Lieu l = new Lieu();
            l.setNom(this.localites.get(randomIndexLocalite)[0]);
            l.setLatitude(latitude);
            l.setLongitude(longitude);
            lieuService.save(l);
            ret.add(l);
        }
        return ret;
    }

    private List<Groupe> generateGroupe(final Integer totalGroupeToCreate, final List<Lieu> listeLieux) {
        final List<Groupe> ret = new ArrayList<>();

        List<String> docFiles = Arrays.asList(".pdf", ".docx", ".html");
        List<String> docTypes = Arrays.asList("application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text/html");

        for (int i = 0; i < totalGroupeToCreate; i++) {

            final int randomIndexGroupe = (int) Math.round(Math.random() * (this.organisations.size() - 1));
            final int numberOfRelation = RandomUtil.generateRandomInt(0, Math.min((listeLieux.size() - 1), 3));

            Set<Lieu> lieux = new HashSet<>();
            for (int j = 0; j < numberOfRelation; j++) {
                final int randomIndexLieux = RandomUtil.generateRandomInt(0, (listeLieux.size() - 1));
                lieux.add(listeLieux.get(randomIndexLieux));
            }

            boolean generateDocument = RandomUtil.generateRandomBoolean();
            String docName = null;
            String docContentType = null;
            if (generateDocument) {
                final int randomIndexDoc = RandomUtil.generateRandomInt(1, 5);
                final int randomIndexDocType = RandomUtil.generateRandomInt(0, 2);
                docName = "document/" + randomIndexDoc + docFiles.get(randomIndexDocType);
                docContentType = docTypes.get(randomIndexDocType);
            }


            Groupe g = createAndSaveNewIGroupe(
                this.organisations.get(randomIndexGroupe)[0],
                RandomUtil.generateRandomBoolean() ? "Description-" + RandomUtil.generateRandomString(20) : null,
                RandomUtil.generateRandomBoolean() ? "Adresse-" + RandomUtil.generateRandomString(20) : null,
                RandomUtil.generateRandomBoolean() ? RandomUtil.generateRandomDateToString("yyyy-MM-dd'T'HH:mm:ss'Z'") : null,
                generateDocument ? docName : null,
                generateDocument ? docContentType : null,
                lieux.isEmpty() ? null : lieux);
            groupeService.save(g);
            ret.add(g);
        }
        return ret;
    }

    private List<Individu> generateIndividu(final Integer totalIndividuToCreate, final List<Groupe> listeGroupe) {
        final List<Individu> ret = new ArrayList<>();

        for (int i = 0; i < totalIndividuToCreate; i++) {
            final int randomIndexNom = (int) Math.round(Math.random() * (this.noms.size() - 1));
            final int randomIndexPrenom = (int) Math.round(Math.random() * (this.prenoms.size() - 1));

            final int numberOfRelation = RandomUtil.generateRandomInt(0, Math.min((listeGroupe.size() - 1), 3));

            Set<Groupe> groupes = new HashSet<>();
            for (int j = 0; j < numberOfRelation; j++) {
                final int randomIndexLieux = RandomUtil.generateRandomInt(0, (listeGroupe.size() - 1));
                groupes.add(listeGroupe.get(randomIndexLieux));
            }

            Individu individu = createAndSaveNewIndividu(this.noms.get(randomIndexNom)[0] + " " + this.prenoms.get(randomIndexPrenom)[0],
                RandomUtil.generateRandomBoolean() ? RandomUtil.generateRandomDouble(0.70, 2.20, 2) : null,
                RandomUtil.generateRandomBoolean() ? RandomUtil.generateRandomDateToString("yyyy-MM-dd'T'HH:mm:ss'Z'") : null,
                RandomUtil.generateRandomBoolean() ? Couleur.randomCouleur() : null,
                RandomUtil.generateRandomBoolean() ? "Coiffure-" + RandomUtil.generateRandomString(10) : null,
                "images/" + RandomUtil.generateRandomInt(0, 99) + ".png",
                "image/png",
                groupes.isEmpty() ? null : groupes);

            individuService.save(individu);
            ret.add(individu);
        }

        return ret;
    }


    private Groupe createAndSaveNewIGroupe(String nom, String description, String adresse, String dateCreation, String resourceNameDoc, String resourceNameDocType, Set<Lieu> lieuSet) {
        Groupe groupe = new Groupe();
        groupe.setNom(nom);
        groupe.setDescription(description);
        groupe.setAdresse(adresse);
        if (dateCreation != null)
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

    private List<String[]> extractDataFromCsvFile(final String resourceName) throws IOException {
        return extractDataFromCsvFile(resourceName, ";");
    }

    private List<String[]> extractDataFromCsvFile(final String resourceName, final String splitSeparator) throws IOException {
        final List<String[]> datas = new ArrayList<>(300000);

        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("dataset/" + resourceName);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                datas.add(line.split(splitSeparator));
            }
        } catch (final FileNotFoundException e) {
            System.out.println("Fichier introuvable" + e);
        }
        return datas;
    }
}
