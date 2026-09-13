# DoppelgangerZ — Beta 1 (Núcleo, Fabric 1.16.5)

Este projeto é a conversão do prompt-mestre original (Forge 1.16.5) para
**Fabric 1.16.5**, com o **núcleo funcional** do conceito implementado. Ele
foi escrito seguindo fielmente as seções 1, 2, 4, 5, 6, 7, 8 e 9 da
especificação original. As camadas de personalidade, memória, decisão
autônoma, corrupção visual, diálogo, placas, sons e forma final (Seções
10–47) **ainda não estão nesta versão** — ficam para as próximas iterações,
conforme combinado.

## O que está implementado

- **Configuração** (`config/doppelgangerz.json`, gerado automaticamente):
  limite de blocos para ativar o Doppelganger, atraso de ativação, limites
  de segurança, velocidade do Doppelganger etc. (Seção 5).
- **Detecção do primeiro bloco** colocado pelo jogador e **contagem de
  progressão da construção** (Seções 4 e 5).
- **Gravação (replay)** de posição/rotação (amostrada, não a cada tick — Seção
  8), além de eventos discretos de quebrar/colocar bloco.
- **Nascimento do Doppelganger** ao atingir o limite configurado (padrão:
  150 blocos), usando a **skin do jogador copiado** (Seção 6, sem corrupção
  ainda).
- **Reprodução física** do replay: o Doppelganger anda, olha e
  quebra/coloca os mesmos blocos que o jogador — **nunca por teleporte**
  como comportamento normal (Seção 9). Teleporte só é usado como
  recuperação de erro se a entidade ficar presa por muito tempo.
- **Segurança (Seção 2)**: raio máximo de modificação de blocos a partir do
  ponto de nascimento do Doppelganger, limite de blocos modificados por
  replay, proteção contra quebra de bedrock, e nenhuma função que apague o
  save, corrompa o mundo ou trave o jogo.

## O que NÃO está nesta Beta (próximas iterações)

Personalidade, memória de longo prazo, percepção contextual, decisões
autônomas, divergência progressiva do replay, corrupção visual gradual da
skin, forma final, diálogo/placas, identidade sonora, eventos de analog
horror. Essas são as Seções 10 em diante do documento original — o núcleo
acima é a fundação sobre a qual elas serão construídas.

## ⚠️ Sobre compilação — leia antes de reportar erro

Eu **não consegui compilar este projeto de verdade**: o ambiente onde
escrevi o código não tem acesso aos repositórios Maven do Fabric/Mojang
(`maven.fabricmc.net`, `libraries.minecraft.net`), que são obrigatórios
para o Gradle baixar Minecraft, as mappings Yarn e a Fabric API. Por isso,
não há garantia de 100% de acerto nas assinaturas de método da Yarn
1.16.5+build.10 — segui as mappings que conheço, mas alguns pontos têm
risco maior de precisar de um pequeno ajuste manual:

- `player.yaw` / `player.pitch` (campos públicos, não `getYaw()`/`getPitch()`
  — isso mudou em versões posteriores do Minecraft);
- `server.getTicks()` (contador de tick do `MinecraftServer`);
- `BlockPos.isWithinDistance(...)`;
- `PlayerSkinProvider.getTextures(...)` / `loadSkin(...)`.

Se o Gradle/IDE apontar erro em alguma dessas linhas, é normal — abra o
projeto no IntelliJ IDEA (com o plugin Minecraft Development, opcional) ou
VSCode com Gradle sincronizado; o autocomplete das mappings reais vai
mostrar o nome correto do método, e a correção costuma ser trocar por
`getYaw()`/`setYaw()` (ou o equivalente) na mesma linha.

## Como compilar (se você programa e tem o ambiente pronto)

```bash
./gradlew build
```

O `.jar` final aparece em `build/libs/doppelgangerz-0.1.0-beta1.jar`. Coloque
o `.jar` principal (não o `-sources.jar`) na pasta `mods` de uma instalação
Fabric 1.16.5 com Fabric Loader e Fabric API instalados.

## Como compilar SEM programar e SEM pagar nada (GitHub Actions)

Este projeto já vem com um robô de build pronto
(`.github/workflows/build.yml`). Você só precisa subir os arquivos para o
GitHub — sem terminal, sem comandos:

1. Crie uma conta gratuita em https://github.com (se ainda não tiver).
2. Clique em **New repository** (botão verde "New" na página inicial),
   dê um nome (ex: `doppelgangerz`), marque como **Public**, e clique em
   **Create repository**.
3. Na página do repositório vazio, clique em **"uploading an existing
   file"**.
4. Extraia o `.zip` que eu gerei no seu computador, e arraste **a pasta
   `doppelgangerz-fabric` inteira** (ou todo o conteúdo dela) para a área de
   upload do GitHub.
5. Clique em **Commit changes** (pode deixar a mensagem padrão).
6. Vá na aba **Actions**, no topo do repositório. Vai aparecer um build
   rodando automaticamente ("Build DoppelgangerZ"). Espere terminar (ícone
   verde ✅, leva alguns minutos).
7. Clique no build concluído, role até **Artifacts**, e baixe
   **DoppelgangerZ-jar** — é um `.zip` contendo o `.jar` compilado de
   verdade, pronto para colocar na pasta `mods`.

Isso roda 100% nos servidores do GitHub (gratuitos para repositórios
públicos), com acesso total à internet — por isso ele consegue baixar o
Minecraft/Fabric e compilar de verdade, diferente do ambiente onde eu
escrevi o código.

Requisitos: apenas uma conta GitHub gratuita. Nenhum programa precisa ser
instalado no seu computador.

## Testando

1. Entre em um mundo novo, singleplayer.
2. Construa normalmente (ande, colete madeira, coloque blocos).
3. Depois de colocar ~150 blocos (configurável), aguarde o atraso
   configurado (padrão 2 minutos) — o Doppelganger vai nascer perto de você
   e começar a reproduzir fisicamente o que você fez.

## Próximos passos sugeridos

1. Corrupção visual progressiva da skin (Seções 6 e 38).
2. Camada de memória + percepção (Seções 11+) para permitir pequenos desvios
   do replay original.
3. Sistema de decisão contextual simples (fila de prioridades) antes de
   qualquer "consciência emergente" mais complexa.
4. Sons e placas de analog horror (Seções 34–37).
