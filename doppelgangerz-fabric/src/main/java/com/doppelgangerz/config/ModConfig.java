package com.doppelgangerz.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configuracao do mod (Secao 5 da especificacao: valores devem ser
 * configuraveis - quantidade minima, atraso, intensidade inicial etc.)
 */
public class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "doppelgangerz.json";

    // --- Secao 5: progressao da construcao ---
    public int blocksToTriggerDoppelganger = 150;
    public int activationDelayTicks = 20 * 60 * 2; // 2 minutos apos atingir o limite
    public int minTicksBetweenMoveSamples = 4;

    // --- Seguranca (Secao 2) ---
    public int safetyMaxBlocksModifiedPerReplay = 400;
    public int safetyMaxRadiusFromSpawnPoint = 96;
    public boolean safetyAllowDoppelgangerToBreakBlocks = true;
    public boolean safetyAllowDoppelgangerToPlaceBlocks = true;

    // --- Replay (Secao 8: economizar memoria) ---
    public int maxRecordedEvents = 20000;
    public double doppelgangerWalkSpeed = 0.28D;
    public double doppelgangerSprintSpeed = 0.4D;

    public static ModConfig loadOrCreate() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                ModConfig config = GSON.fromJson(reader, ModConfig.class);
                if (config != null) {
                    return config;
                }
            } catch (IOException e) {
                com.doppelgangerz.DoppelgangerZ.LOGGER.warn("Falha ao ler config, usando padroes.", e);
            }
        }
        ModConfig defaults = new ModConfig();
        defaults.save();
        return defaults;
    }

    public void save() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            com.doppelgangerz.DoppelgangerZ.LOGGER.warn("Falha ao salvar config.", e);
        }
    }
}
