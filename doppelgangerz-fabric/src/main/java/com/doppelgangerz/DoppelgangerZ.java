package com.doppelgangerz;

import com.doppelgangerz.config.ModConfig;
import com.doppelgangerz.registry.ModEntities;
import com.doppelgangerz.replay.ReplayManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Ponto de entrada principal do DoppelgangerZ - Beta 1 (porte para Fabric 1.16.5).
 *
 * Esta Beta implementa o NUCLEO da especificacao original:
 *  - Secao 4: deteccao do primeiro bloco colocado;
 *  - Secao 5: progressao/contagem da construcao ate o limiar configurado;
 *  - Secao 6: nascimento do Doppelganger usando a skin do jogador;
 *  - Secoes 7 a 9: gravacao e reproducao FISICA (sem teleporte) das acoes do
 *    jogador (mover, quebrar e colocar blocos);
 *  - Secao 2: mecanismos de seguranca (raio maximo, limite de blocos
 *    modificados, protecao de blocos criticos).
 *
 * As camadas de personalidade, memoria, decisao autonoma, corrupcao visual,
 * dialogo, placas, sons e forma final (Secoes 10-47) NAO estao nesta versao -
 * ver README_BETA.md para o plano de proximas iteracoes.
 */
public class DoppelgangerZ implements ModInitializer {

    public static final String MOD_ID = "doppelgangerz";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static ModConfig CONFIG;

    @Override
    public void onInitialize() {
        LOGGER.info("[DoppelgangerZ] Inicializando Beta 1 (Fabric 1.16.5)...");

        CONFIG = ModConfig.loadOrCreate();

        ModEntities.register();
        ReplayManager.register();

        ServerTickEvents.END_SERVER_TICK.register(ReplayManager::onServerTick);

        LOGGER.info("[DoppelgangerZ] Inicializacao concluida.");
    }
}
