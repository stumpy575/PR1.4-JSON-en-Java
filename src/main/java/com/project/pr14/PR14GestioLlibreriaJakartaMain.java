package com.project.pr14;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.project.objectes.Llibre;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;

/**
 * Classe principal que gestiona la lectura i el processament de fitxers JSON per obtenir dades de llibres
 * utilitzant Jakarta JSON Processing (JSON-P).
 */
public class PR14GestioLlibreriaJakartaMain {

    private final File dataFile;

    /**
     * Constructor de la classe PR14GestioLlibreriaJakartaMain.
     *
     * @param dataFile Fitxer on es troben els llibres.
     */
    public PR14GestioLlibreriaJakartaMain(File dataFile) {
        this.dataFile = dataFile;
    }

    public static void main(String[] args) {
        File dataFile = new File(System.getProperty("user.dir"), "data/pr14" + File.separator + "llibres_input.json");
        PR14GestioLlibreriaJakartaMain app = new PR14GestioLlibreriaJakartaMain(dataFile);
        app.processarFitxer();
    }

    /**
     * Processa el fitxer JSON per carregar, modificar, afegir, esborrar i guardar les dades dels llibres.
     */
    public void processarFitxer() {
        List<Llibre> llibres = carregarLlibres();
        if (llibres != null) {
            modificarAnyPublicacio(llibres, 1, 1995);
            afegirNouLlibre(llibres, new Llibre(4, "Històries de la ciutat", "Miquel Soler", 2022));
            esborrarLlibre(llibres, 2);
            guardarLlibres(llibres);
        }
    }

    /**
     * Carrega els llibres des del fitxer JSON.
     * Si el fitxer no existeix o no conté un JSON vàlid, mostra un missatge d'error i retorna null
     * (no ha de propagar cap excepció).
     *
     * @return Llista de llibres o null si hi ha hagut un error en la lectura.
     */
    public List<Llibre> carregarLlibres() {
        try (JsonReader jsonReader = Json.createReader(new FileReader(dataFile))) {
            JsonArray llibresArray = jsonReader.readArray();
            List<Llibre> llibres = new ArrayList<>();
            for (JsonValue valor : llibresArray) {
                JsonObject llibre = valor.asJsonObject();
                llibres.add(new Llibre(llibre.getInt("id"), llibre.getString("titol"), llibre.getString("autor"), llibre.getInt("any")  ));
            }  
            return llibres;
        } catch (Exception e) {
            System.err.println("Error en carregar els llibres: " + e.getMessage());
            return null;
        }
    }

    /**
     * Modifica l'any de publicació d'un llibre amb un id específic.
     *
     * @param llibres Llista de llibres.
     * @param id Identificador del llibre a modificar.
     * @param nouAny Nou any de publicació.
     */
    public void modificarAnyPublicacio(List<Llibre> llibres, int id, int nouAny) {
        for (int i = 0; i < llibres.size(); i++) {
            Llibre llibre = llibres.get(i);
            if (llibre.getId() == id) {
                llibres.set(i, new Llibre(llibre.getId(), llibre.getTitol(), llibre.getAutor(), nouAny));
                break;
            }
        }
    }

    /**
     * Afegeix un nou llibre a la llista de llibres.
     *
     * @param llibres Llista de llibres.
     * @param nouLlibre Nou llibre a afegir.
     */
    public void afegirNouLlibre(List<Llibre> llibres, Llibre nouLlibre) {
llibres.add(nouLlibre); 
    }

    /**
     * Esborra un llibre amb un id específic de la llista de llibres.
     *
     * @param llibres Llista de llibres.
     * @param id Identificador del llibre a esborrar.
     */
    public void esborrarLlibre(List<Llibre> llibres, int id) {
llibres.removeIf(llibre -> llibre.getId() == id);  
    }

    /**
     * Guarda la llista de llibres en un fitxer nou anomenat llibres_output_jakarta.json,
     * dins del mateix directori que el fitxer d'entrada.
     *
     * @param llibres Llista de llibres a guardar.
     */
    public void guardarLlibres(List<Llibre> llibres) {
JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Llibre llibre : llibres) {
            JsonObject llibreJson = Json.createObjectBuilder()
                .add("id", llibre.getId())
                .add("titol", llibre.getTitol())
                .add("autor", llibre.getAutor())
                .add("any", llibre.getAny())
                .build();
            arrayBuilder.add(llibreJson);
        }
        JsonArray jsonArray = arrayBuilder.build();
        try (Writer writer = Files.newBufferedWriter(Paths.get(dataFile.getParent(), "llibres_output_jakarta.json"), StandardCharsets.UTF_8);
             JsonWriter jsonWriter = Json.createWriter(writer)) {
            jsonWriter.writeArray(jsonArray);
        } catch (IOException | JsonException e) {
            System.err.println("Error en guardar els llibres: " + e.getMessage());
        }
    }
}
