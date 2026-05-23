package view;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import model.Jogo;

/**
 * Centraliza o carregamento das imagens usadas pela View.
 *
 * Estrutura esperada em "resources/images/":
 *   Tabuleiros/Tabuleiro-Clue-A.jpg  — tabuleiro principal
 *   Tabuleiros/dado1.jpg … dado6.jpg — faces dos dados
 *   Suspeitos/Scarlet.jpg, Mustard.jpg, …
 *   Armas/Cano.jpg, Castical.jpg, …
 *   Comodos/Cozinha.jpg, SalaDeMusica.jpg, …
 *
 * Quando uma imagem não está presente no disco, é gerada em
 * runtime uma BufferedImage placeholder razoável. Em qualquer
 * caso, toda renderização passa por drawImage() — requisito
 * do enunciado da 2ª iteração.
 *
 * As imagens de cartas são indexadas por id em um HashMap,
 * garantindo recuperação em tempo constante O(1).
 */
public class ResourceLoader {

    private static final String DIR = "resources/images/";

    private BufferedImage tabuleiro;
    private final BufferedImage[] dados = new BufferedImage[7]; // índice 1..6
    private final BufferedImage[] pinos = new BufferedImage[6];
    private final Map<Integer, BufferedImage> cartas = new HashMap<Integer, BufferedImage>();

    // Mapa fixo: nome da carta → arquivo relativo a DIR
    private static final Map<String, String> ARQUIVO_CARTA = new HashMap<String, String>();

    static {
        // Personagens
        ARQUIVO_CARTA.put("Srta. Scarlet",   "Suspeitos/Scarlet.jpg");
        ARQUIVO_CARTA.put("Coronel Mustard", "Suspeitos/Mustard.jpg");
        ARQUIVO_CARTA.put("Sra. White",      "Suspeitos/White.jpg");
        ARQUIVO_CARTA.put("Reverendo Green", "Suspeitos/Green.jpg");
        ARQUIVO_CARTA.put("Sra. Peacock",    "Suspeitos/Peacock.jpg");
        ARQUIVO_CARTA.put("Professor Plum",  "Suspeitos/Plum.jpg");

        // Armas
        ARQUIVO_CARTA.put("Castiçal",     "Armas/Castical.jpg");
        ARQUIVO_CARTA.put("Revólver",     "Armas/Revolver.jpg");
        ARQUIVO_CARTA.put("Faca",         "Armas/Faca.jpg");
        ARQUIVO_CARTA.put("Cano",         "Armas/Cano.jpg");
        ARQUIVO_CARTA.put("Corda",        "Armas/Corda.jpg");
        ARQUIVO_CARTA.put("Chave Inglesa","Armas/ChaveInglesa.jpg");

        // Cômodos
        ARQUIVO_CARTA.put("Cozinha",          "Comodos/Cozinha.jpg");
        ARQUIVO_CARTA.put("Sala de Música",   "Comodos/SalaDeMusica.jpg");
        ARQUIVO_CARTA.put("Jardim de Inverno","Comodos/JardimInverno.jpg");
        ARQUIVO_CARTA.put("Sala de Jantar",   "Comodos/SalaDeJantar.jpg");
        ARQUIVO_CARTA.put("Salão de Jogos",   "Comodos/SalaoDeJogos.jpg");
        ARQUIVO_CARTA.put("Biblioteca",       "Comodos/Biblioteca.jpg");
        ARQUIVO_CARTA.put("Sala de Estar",    "Comodos/SalaDeEstar.jpg");
        ARQUIVO_CARTA.put("Entrada",          "Comodos/Entrada.jpg");
        ARQUIVO_CARTA.put("Escritório",       "Comodos/Escritorio.jpg");
    }

    public ResourceLoader(Jogo jogo, int largTab, int altTab,
                          int pinoTam, int dadoTam, int cartaLarg, int cartaAlt) {
        carregarTabuleiro(jogo, largTab, altTab);
        carregarDados(dadoTam);
        carregarPinos(pinoTam);
        carregarTodasAsCartas(jogo, cartaLarg, cartaAlt);
    }

    // ============================================================
    // Acesso público
    // ============================================================

    public BufferedImage getTabuleiro() {
        return tabuleiro;
    }

    public BufferedImage getDado(int valor) {
        if (valor < 1 || valor > 6) valor = 1;
        return dados[valor];
    }

    public BufferedImage getPino(int indiceJogador) {
        return pinos[indiceJogador % pinos.length];
    }

    /** Recupera, em O(1), a imagem da carta de id "idCarta". */
    public BufferedImage getCarta(int idCarta) {
        return cartas.get(Integer.valueOf(idCarta));
    }

    // ============================================================
    // Tabuleiro
    // ============================================================

    private void carregarTabuleiro(Jogo jogo, int larg, int alt) {
        BufferedImage img = lerImagem("Tabuleiros/Tabuleiro-Clue-A.jpg");
        if (img != null) {
            tabuleiro = img;
        } else {
            tabuleiro = gerarTabuleiroPlaceholder(jogo, larg, alt);
        }
    }

    private BufferedImage gerarTabuleiroPlaceholder(Jogo jogo, int larg, int alt) {
        BufferedImage img = new BufferedImage(larg, alt, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        int linhas = jogo.getLinhasTabuleiro();
        int colunas = jogo.getColunasTabuleiro();
        double cw = larg / (double) colunas;
        double ch = alt  / (double) linhas;

        g.setColor(new Color(245, 240, 220));
        g.fillRect(0, 0, larg, alt);

        for (int l = 0; l < linhas; l++) {
            for (int c = 0; c < colunas; c++) {
                int x = (int) Math.round(c * cw);
                int y = (int) Math.round(l * ch);
                int w = (int) Math.round((c + 1) * cw) - x;
                int h = (int) Math.round((l + 1) * ch) - y;
                String tipo = jogo.getTipoCasa(l, c);
                Color fundo = corDaCasa(tipo, jogo.getNomeComodoEm(l, c));
                g.setColor(fundo);
                g.fillRect(x, y, w, h);
                if ("CORREDOR".equals(tipo) || "PORTA".equals(tipo)) {
                    g.setColor(new Color(180, 170, 140));
                    g.drawRect(x, y, w, h);
                }
                if ("PORTA".equals(tipo)) {
                    g.setColor(new Color(70, 110, 50));
                    g.fillOval(x + w / 3, y + h / 3, w / 3, h / 3);
                }
            }
        }
        g.setColor(new Color(40, 40, 40));
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        for (String nome : jogo.getNomesComodos()) {
            int[] bb = jogo.getBoundingBoxComodo(nome);
            if (bb == null) continue;
            int cx = (int) Math.round(((bb[1] + bb[3] + 1) / 2.0) * cw);
            int cy = (int) Math.round(((bb[0] + bb[2] + 1) / 2.0) * ch);
            int sw = g.getFontMetrics().stringWidth(nome);
            g.drawString(nome, cx - sw / 2, cy);
        }
        g.setColor(new Color(80, 60, 30));
        g.setStroke(new BasicStroke(3f));
        g.drawRect(0, 0, larg - 1, alt - 1);
        g.dispose();
        return img;
    }

    private Color corDaCasa(String tipo, String nomeComodo) {
        if ("CORREDOR".equals(tipo)) return new Color(250, 245, 215);
        if ("PORTA".equals(tipo))    return new Color(250, 245, 215);
        if ("BLOQUEADA".equals(tipo)) return new Color(150, 140, 120);
        if (nomeComodo == null) return new Color(200, 200, 200);
        switch (nomeComodo) {
            case "Cozinha":           return new Color(120, 95, 75);
            case "Sala de Música":    return new Color(240, 140, 50);
            case "Jardim de Inverno": return new Color(60, 110, 175);
            case "Sala de Jantar":    return new Color(150, 80, 40);
            case "Salão de Jogos":    return new Color(50, 140, 70);
            case "Biblioteca":        return new Color(210, 175, 50);
            case "Sala de Estar":     return new Color(45, 105, 70);
            case "Entrada":           return new Color(60, 70, 145);
            case "Escritório":        return new Color(190, 50, 50);
            default:                  return new Color(160, 160, 160);
        }
    }

    // ============================================================
    // Dados
    // ============================================================

    private void carregarDados(int tam) {
        for (int v = 1; v <= 6; v++) {
            BufferedImage img = lerImagem("Tabuleiros/dado" + v + ".jpg");
            if (img == null) img = gerarDadoPlaceholder(v, tam);
            dados[v] = img;
        }
    }

    private BufferedImage gerarDadoPlaceholder(int valor, int tam) {
        BufferedImage img = new BufferedImage(tam, tam, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRoundRect(2, 2, tam - 4, tam - 4, tam / 4, tam / 4);
        g.setColor(Color.DARK_GRAY);
        g.setStroke(new BasicStroke(3f));
        g.drawRoundRect(2, 2, tam - 4, tam - 4, tam / 4, tam / 4);
        int p = tam / 7;
        int e = tam / 4;
        int m = tam / 2;
        int d = 3 * tam / 4;
        g.setColor(Color.BLACK);
        switch (valor) {
            case 1: ponto(g, m, m, p); break;
            case 2: ponto(g, e, e, p); ponto(g, d, d, p); break;
            case 3: ponto(g, e, e, p); ponto(g, m, m, p); ponto(g, d, d, p); break;
            case 4: ponto(g, e, e, p); ponto(g, e, d, p); ponto(g, d, e, p); ponto(g, d, d, p); break;
            case 5: ponto(g, e, e, p); ponto(g, e, d, p); ponto(g, d, e, p); ponto(g, d, d, p); ponto(g, m, m, p); break;
            case 6: ponto(g, e, e, p); ponto(g, e, m, p); ponto(g, e, d, p); ponto(g, d, e, p); ponto(g, d, m, p); ponto(g, d, d, p); break;
            default: break;
        }
        g.dispose();
        return img;
    }

    private void ponto(Graphics2D g, int cx, int cy, int r) {
        g.fillOval(cx - r, cy - r, 2 * r, 2 * r);
    }

    // ============================================================
    // Pinos — gerados programaticamente (sem imagem no EAD)
    // ============================================================

    private void carregarPinos(int tam) {
        for (int i = 0; i < Jogo.CORES_PADRAO.length; i++) {
            pinos[i] = gerarPinoPlaceholder(corPino(Jogo.CORES_PADRAO[i]), tam);
        }
    }

    private BufferedImage gerarPinoPlaceholder(Color cor, int tam) {
        BufferedImage img = new BufferedImage(tam, tam, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
        // Sombra
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.30f));
        g.setColor(Color.BLACK);
        g.fillOval(2, tam - tam / 4, tam - 4, tam / 6);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        // Corpo
        g.setColor(cor);
        g.fillOval(2, 2, tam - 4, tam - 4);
        g.setColor(cor.darker());
        g.setStroke(new BasicStroke(2f));
        g.drawOval(2, 2, tam - 4, tam - 4);
        // Brilho
        g.setColor(new Color(255, 255, 255, 120));
        g.fillOval(tam / 4, tam / 5, tam / 4, tam / 5);
        g.dispose();
        return img;
    }

    public static Color corPino(String nome) {
        switch (nome) {
            case "Vermelho": return new Color(200, 30, 30);
            case "Amarelo":  return new Color(235, 200, 50);
            case "Branco":   return new Color(240, 240, 240);
            case "Verde":    return new Color(40, 140, 60);
            case "Azul":     return new Color(40, 80, 180);
            case "Violeta":  return new Color(130, 50, 165);
            default:         return Color.GRAY;
        }
    }

    // ============================================================
    // Cartas — todas as 21, indexadas por id em HashMap
    // ============================================================

    private void carregarTodasAsCartas(Jogo jogo, int larg, int alt) {
        for (String[] info : jogo.getTodasAsCartas()) {
            int id       = Integer.parseInt(info[0]);
            String nome  = info[1];
            String arquivo = ARQUIVO_CARTA.get(nome);
            BufferedImage img = null;
            if (arquivo != null) {
                img = lerImagem(arquivo);
            }
            if (img == null) {
                img = gerarCartaPlaceholder(nome, info[2], larg, alt);
            }
            cartas.put(Integer.valueOf(id), img);
        }
    }

    private BufferedImage gerarCartaPlaceholder(String nome, String tipo,
                                                int larg, int alt) {
        BufferedImage img = new BufferedImage(larg, alt, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
        Color cor = "PERSONAGEM".equals(tipo) ? new Color(210, 170, 100)
                  : "ARMA".equals(tipo)       ? new Color(180, 100, 100)
                                               : new Color(100, 160, 100);
        g.setColor(cor);
        g.fillRoundRect(0, 0, larg - 1, alt - 1, 12, 12);
        g.setColor(cor.darker());
        g.setStroke(new BasicStroke(3f));
        g.drawRoundRect(0, 0, larg - 1, alt - 1, 12, 12);
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        // Quebra o nome em múltiplas linhas se necessário
        String[] palavras = nome.split(" ");
        int y = alt / 3;
        StringBuilder linha = new StringBuilder();
        for (String p : palavras) {
            if (g.getFontMetrics().stringWidth(linha + p) > larg - 16) {
                int sw = g.getFontMetrics().stringWidth(linha.toString().trim());
                g.drawString(linha.toString().trim(), (larg - sw) / 2, y);
                y += 18;
                linha = new StringBuilder();
            }
            linha.append(p).append(" ");
        }
        if (linha.length() > 0) {
            String s = linha.toString().trim();
            int sw = g.getFontMetrics().stringWidth(s);
            g.drawString(s, (larg - sw) / 2, y);
        }
        g.dispose();
        return img;
    }

    // ============================================================
    // I/O
    // ============================================================

    private BufferedImage lerImagem(String caminho) {
        try {
            File f = new File(DIR + caminho);
            if (!f.exists()) return null;
            return ImageIO.read(f);
        } catch (IOException e) {
            return null;
        }
    }
}
