package com.hotel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * Gestió de reserves d'un hotel.
 */
public class App {

    // --------- CONSTANTS I VARIABLES GLOBALS ---------

    // Tipus d'habitació
    public static final String TIPUS_ESTANDARD = "Estàndard";
    public static final String TIPUS_SUITE = "Suite";
    public static final String TIPUS_DELUXE = "Deluxe";

    // Serveis addicionals
    public static final String SERVEI_ESMORZAR = "Esmorzar";
    public static final String SERVEI_GIMNAS = "Gimnàs";
    public static final String SERVEI_SPA = "Spa";
    public static final String SERVEI_PISCINA = "Piscina";

    // Capacitat inicial
    public static final int CAPACITAT_ESTANDARD = 30;
    public static final int CAPACITAT_SUITE = 20;
    public static final int CAPACITAT_DELUXE = 10;

    // IVA
    public static final float IVA = 0.21f;

    // Scanner únic
    public static Scanner sc = new Scanner(System.in);

    // HashMaps de consulta
    public static HashMap<String, Float> preusHabitacions = new HashMap<String, Float>();
    public static HashMap<String, Integer> capacitatInicial = new HashMap<String, Integer>();
    public static HashMap<String, Float> preusServeis = new HashMap<String, Float>();

    // HashMaps dinàmics
    public static HashMap<String, Integer> disponibilitatHabitacions = new HashMap<String, Integer>();
    public static HashMap<Integer, ArrayList<String>> reserves = new HashMap<Integer, ArrayList<String>>();

    // Generador de nombres aleatoris per als codis de reserva
    public static Random random = new Random();

    // --------- MÈTODE MAIN ---------

    /**
     * Mètode principal. Mostra el menú en un bucle i gestiona l'opció triada
     * fins que l'usuari decideix eixir.
     */
    public static void main(String[] args) {
        inicialitzarPreus();

        int opcio = 0;
        do {
            mostrarMenu();
            opcio = llegirEnter("Seleccione una opció: ");
            gestionarOpcio(opcio);
        } while (opcio != 6);

        System.out.println("Eixint del sistema... Gràcies per utilitzar el gestor de reserves!");
    }

    // --------- MÈTODES DEMANATS ---------

    /**
     * Configura els preus de les habitacions, serveis addicionals i
     * les capacitats inicials en els HashMaps corresponents.
     */
    public static void inicialitzarPreus() {
        // Preus habitacions
        preusHabitacions.put(TIPUS_ESTANDARD, 50f);
        preusHabitacions.put(TIPUS_SUITE, 100f);
        preusHabitacions.put(TIPUS_DELUXE, 150f);

        // Capacitats inicials
        capacitatInicial.put(TIPUS_ESTANDARD, CAPACITAT_ESTANDARD);
        capacitatInicial.put(TIPUS_SUITE, CAPACITAT_SUITE);
        capacitatInicial.put(TIPUS_DELUXE, CAPACITAT_DELUXE);

        // Disponibilitat inicial (comença igual que la capacitat)
        disponibilitatHabitacions.put(TIPUS_ESTANDARD, CAPACITAT_ESTANDARD);
        disponibilitatHabitacions.put(TIPUS_SUITE, CAPACITAT_SUITE);
        disponibilitatHabitacions.put(TIPUS_DELUXE, CAPACITAT_DELUXE);

        // Preus serveis
        preusServeis.put(SERVEI_ESMORZAR, 10f);
        preusServeis.put(SERVEI_GIMNAS, 15f);
        preusServeis.put(SERVEI_SPA, 20f);
        preusServeis.put(SERVEI_PISCINA, 25f);
    }

    /**
     * Mostra el menú principal amb les opcions disponibles per a l'usuari.
     */
    public static void mostrarMenu() {
        System.out.println("\n===== MENÚ PRINCIPAL =====");
        System.out.println("1. Reservar una habitació");
        System.out.println("2. Alliberar una habitació");
        System.out.println("3. Consultar disponibilitat");
        System.out.println("4. Consultar dades d'una reserva");
        System.out.println("5. Consultar reserves per tipus");
        System.out.println("6. Ixir");
    }

    /**
     * Processa l'opció seleccionada per l'usuari i crida el mètode corresponent.
     */
    public static void gestionarOpcio(int opcio) {
        switch (opcio) {
            case 1:
                reservarHabitacio();
                break;
            case 2:
                alliberarHabitacio();
                break;
            case 3:
                consultarDisponibilitat();
                break;
            case 4:
                obtindreReserva();
                break;
            case 5:
                obtindreReservaPerTipus();
                break;
            default:
                break;
        }
    }

    /**
     * Gestiona tot el procés de reserva: selecció del tipus d'habitació,
     * serveis addicionals, càlcul del preu total i generació del codi de reserva.
     */
    public static void reservarHabitacio() {
        System.out.println("\n===== RESERVAR HABITACIÓ =====");

        String roomType = seleccionarTipusHabitacioDisponible();
        ArrayList<String> selectedServices = seleccionarServeis();
        float finalPrice = calcularPreuTotal(roomType, selectedServices);
        int bookingCode = generarCodiReserva();

        ArrayList<String> bookingData = new ArrayList<>();
        bookingData.add(roomType); // Index 0: roomType
        bookingData.add(String.valueOf(finalPrice)); // Index 1: finalPrice (cast to String: el ArrayList de
                                                     // HashMap<Integer, ArrayList<String> reserves NO puede aceptar
                                                     // valores int)
        for (String service : selectedServices) {
            bookingData.add(service); // Index 2-3...n services.
        }

        reserves.put(bookingCode, bookingData); // Nueva reserva
        disponibilitatHabitacions.put(roomType, disponibilitatHabitacions.get(roomType) - 1); // Disponibilidad -1

        System.out.println("\n");
        System.out.println("Reserva creada amb èxit!");
        System.out.println("CÓDIGO DE RESERVA: " + bookingCode);
    }

    /**
     * Pregunta a l'usuari un tipus d'habitació en format numèric i
     * retorna el nom del tipus.
     */
    public static String seleccionarTipusHabitacio() {
        int opcio;

        do {
            opcio = llegirEnter("Seleccione el tipus d'habitació  (1-3): ");
        } while (opcio < 1 || opcio > 3);

        switch (opcio) {
            case 1:
                return TIPUS_ESTANDARD;
            case 2:
                return TIPUS_SUITE;
            case 3:
                return TIPUS_DELUXE;
            default:
                return null;
        }

    }

    /**
     * Mostra la disponibilitat i el preu de cada tipus d'habitació,
     * demana a l'usuari un tipus i només el retorna si encara hi ha
     * habitacions disponibles. En cas contrari, retorna null.
     */
    public static String seleccionarTipusHabitacioDisponible() {
        System.out.println("Tipus d'habitació:");
        mostrarInfoTipus(TIPUS_ESTANDARD);
        mostrarInfoTipus(TIPUS_SUITE);
        mostrarInfoTipus(TIPUS_DELUXE);

        String tipus = seleccionarTipusHabitacio();

        if (disponibilitatHabitacions.get(tipus) <= 0) {
            System.out.println("No hi ha disponibilitat per a aquest tipus.");
            return null;
        }

        System.out.println("Habitación seleccionada: " + tipus);
        return tipus;
    }

    /**
     * Permet triar serveis addicionals (entre 0 i 4, sense repetir) i
     * els retorna en un ArrayList de String.
     */
    public static ArrayList<String> seleccionarServeis() {
        ArrayList<String> selectedServices = new ArrayList<>();
        int opcio;

        System.out.println("Serveis addicionals (0-4)");
        System.out.println("0. No afegir serveis.");
        System.out.println("1. " + SERVEI_ESMORZAR + " (" + preusServeis.get(SERVEI_ESMORZAR) + "€)");
        System.out.println("2. " + SERVEI_GIMNAS + " (" + preusServeis.get(SERVEI_GIMNAS) + "€)");
        System.out.println("3. " + SERVEI_SPA + " (" + preusServeis.get(SERVEI_SPA) + "€)");
        System.out.println("4. " + SERVEI_PISCINA + " (" + preusServeis.get(SERVEI_PISCINA) + "€)");

        String[] services = {
                SERVEI_ESMORZAR,
                SERVEI_GIMNAS,
                SERVEI_SPA,
                SERVEI_PISCINA
        };

        do {
            opcio = llegirEnter("Selecciona un servei (0-4): ");

            if (opcio == 0) {
                break;
            }

            if (opcio < 1 || opcio > services.length) {
                System.out.println("Opció no vàlida");
                continue;
            }

            String newService = services[opcio - 1];

            if (selectedServices.contains(newService)) {
                System.out.println("Aquest servei ja ha sigut seleccionat");
            } else {
                selectedServices.add(newService);
                System.out.println("Servei afegit: " + newService);
            }

        } while (opcio != 0);

        return selectedServices;
    }

    /**
     * Calcula i retorna el cost total de la reserva, incloent l'habitació,
     * els serveis seleccionats i l'IVA.
     */
    public static float calcularPreuTotal(String tipusHabitacio, ArrayList<String> serveisSeleccionats) {
        System.out.println("\n");

        float preuHabitacio = preusHabitacions.get(tipusHabitacio);
        System.out.println("Preu habitació: " + preuHabitacio + "€");

        float preuServeisSeleccionats = 0;
        System.out.println("Preu Serveis: ");
        for (String servei : serveisSeleccionats) {
            System.out.println("    " + servei + ": " + preusServeis.get(servei) + "€");
            preuServeisSeleccionats += preusServeis.get(servei);
        }
        System.out.println("Total serveis: " + preuServeisSeleccionats);
        System.out.println("\n");

        float preuSenseIVA = preuHabitacio + preuServeisSeleccionats;
        System.out.println("Subtotal: " + preuSenseIVA + "€");
        System.out.println("IVA (21%): " + (preuSenseIVA * IVA) + "€");

        float preuFinal = preuSenseIVA + (preuSenseIVA * IVA);
        System.out.println("TOTAL: " + preuFinal + "€");

        return preuFinal;
    }

    /**
     * Genera i retorna un codi de reserva únic de tres xifres
     * (entre 100 i 999) que no estiga repetit.
     */
    public static int generarCodiReserva() {
        int codReserva = 0;

        do {
            codReserva = 100 + random.nextInt(900);
        } while (reserves.containsKey(codReserva));

        return codReserva;
    }

    /**
     * Permet alliberar una habitació utilitzant el codi de reserva
     * i actualitza la disponibilitat.
     */
    public static void alliberarHabitacio() {
        System.out.println("\n===== ALLIBERAR HABITACIÓ =====");

        int bookingCode;

        do {
            bookingCode = llegirEnter("Introdueix un nombre de reserva vàlid (100-999): ");
        } while (bookingCode > 999 || bookingCode < 100);

        if (reserves.containsKey(bookingCode)) {
            System.out.println("\n");
            System.out.println("Reserva encontrada!");

            String roomType = reserves.get(bookingCode).get(0); // Tipo de habitación: index 0
            disponibilitatHabitacions.put(roomType, disponibilitatHabitacions.get(roomType) + 1);

            reserves.remove(bookingCode); // Eliminar reserva
            System.out.println("Habitació alliberada correctament.");
            System.out.println("Disponibilitat actualizada.");
        } else {
            System.out.println("No s`ha trobat cap reserva amb aquest codi");
        }
    }

    /**
     * Mostra la disponibilitat actual de les habitacions (lliures i ocupades).
     */
    public static void consultarDisponibilitat() {
        System.out.println("===== DISPONIBILITAT D'HABITACIONS =====");
        System.out.println("TIPUS        LLIURES  OCUPADES");

        mostrarDisponibilitatTipus(TIPUS_ESTANDARD);
        mostrarDisponibilitatTipus(TIPUS_SUITE);
        mostrarDisponibilitatTipus(TIPUS_DELUXE);
    }

    /**
     * Funció recursiva. Mostra les dades de totes les reserves
     * associades a un tipus d'habitació.
     */
    public static void llistarReservesPerTipus(int[] codis, String tipus) {
        // TODO: Implementar recursivitat
    }

    /**
     * Permet consultar els detalls d'una reserva introduint el codi.
     */
    public static void obtindreReserva() {
        System.out.println("\n===== CONSULTAR RESERVA =====");

        int bookingCode;

        do {
            bookingCode = llegirEnter("Introdueix un nombre de reserva vàlid (100-999): ");
        } while (bookingCode > 999 || bookingCode < 100);

        if (reserves.containsKey(bookingCode)) {
            mostrarDadesReserva(bookingCode);
        } else {
            System.out.println("No s`ha trobat cap reserva amb aquest codi");
        }
    }

    /**
     * Mostra totes les reserves existents per a un tipus d'habitació
     * específic.
     */
    public static void obtindreReservaPerTipus() {
        System.out.println("\n===== CONSULTAR RESERVES PER TIPUS =====");
        // TODO: Llistar reserves per tipus
    }

    /**
     * Consulta i mostra en detall la informació d'una reserva.
     */
    public static void mostrarDadesReserva(int codi) {
        System.out.println("Dades de la reserva: ");

        ArrayList<String> bookingData = reserves.get(codi);
        System.out.println("- Tipus d`habitació: " + bookingData.get(0));
        System.out.println("- Cost total: " + bookingData.get(1));

        System.out.println("- Serveis adicionals: ");
        for (int i = 2; i < bookingData.size(); i++) {
            System.out.println("    " + bookingData.get(i));
        }
    }

    // --------- MÈTODES AUXILIARS (PER MILLORAR LEGIBILITAT) ---------

    /**
     * Llig un enter per teclat mostrant un missatge i gestiona possibles
     * errors d'entrada.
     */
    static int llegirEnter(String missatge) {
        int valor = 0;
        boolean correcte = false;
        while (!correcte) {
            System.out.print(missatge);
            valor = sc.nextInt();
            correcte = true;
        }

        return valor;
    }

    /**
     * Mostra per pantalla informació d'un tipus d'habitació: preu i
     * habitacions disponibles.
     */
    static void mostrarInfoTipus(String tipus) {
        int disponibles = disponibilitatHabitacions.get(tipus);
        int capacitat = capacitatInicial.get(tipus);
        float preu = preusHabitacions.get(tipus);
        System.out.println("- " + tipus + " (" + disponibles + " disponibles de " + capacitat + ") - " + preu + "€");
    }

    /**
     * Mostra la disponibilitat (lliures i ocupades) d'un tipus d'habitació.
     */
    static void mostrarDisponibilitatTipus(String tipus) {
        int lliures = disponibilitatHabitacions.get(tipus);
        int capacitat = capacitatInicial.get(tipus);
        int ocupades = capacitat - lliures;

        String etiqueta = tipus;
        if (etiqueta.length() < 8) {
            etiqueta = etiqueta + "\t"; // per a quadrar la taula
        }

        System.out.println(etiqueta + "\t" + lliures + "\t" + ocupades);
    }
}
