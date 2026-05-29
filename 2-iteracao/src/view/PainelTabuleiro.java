package view;

import model.Jogo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Painel central do tabuleiro — desenha tudo via Graphics2D e trata cliques do mouse.
 */
public class PainelTabuleiro extends JPanel implements MouseListener {

    // variaveis
    private final Jogo jogo;
    private BufferedImage imgTabuleiro;
    private final HashMap<String, BufferedImage> imgPiaos;
    private final BufferedImage[] imgDados;
    private List<Integer> casasAlcancaveis;
    private int[] ultimoLancamento;
    private final int larguraCelula;
    private final int alturaCelula;

    // métodos

    /**
     * Cria o painel do tabuleiro associado ao jogo fornecido.
     * Carrega todas as imagens no construtor (nunca dentro de paintComponent).
     */
    public PainelTabuleiro(Jogo jogo) {
        this.jogo = jogo;
        this.imgPiaos = new HashMap<String, BufferedImage>();
        this.imgDados = new BufferedImage[6];
        this.casasAlcancaveis = null;
        this.ultimoLancamento = null;
        this.larguraCelula = 40;
        this.alturaCelula = 30;

        setPreferredSize(new Dimension(
                larguraCelula * jogo.getColunasTabuleiro(),
                alturaCelula * jogo.getLinhasTabuleiro()));

        carregarImagens();
        addMouseListener(this);
    }

    private void carregarImagens() {
        imgTabuleiro = carregarImagem("../assets/Tabuleiros/Tabuleiro-Clue-A.jpg");

        imgPiaos.put("Vermelho", carregarImagem("../assets/Suspeitos/Scarlet.jpg"));
        imgPiaos.put("Amarelo",  carregarImagem("../assets/Suspeitos/Mustard.jpg"));
        imgPiaos.put("Branco",   carregarImagem("../assets/Suspeitos/White.jpg"));
        imgPiaos.put("Verde",    carregarImagem("../assets/Suspeitos/Green.jpg"));
        imgPiaos.put("Violeta",  carregarImagem("../assets/Suspeitos/Peacock.jpg"));
        imgPiaos.put("Azul",     carregarImagem("../assets/Suspeitos/Plum.jpg"));

        for (int i = 0; i < 6; i++) {
            imgDados[i] = carregarImagem("../assets/Tabuleiros/dado" + (i + 1) + ".jpg");
        }
    }

    private BufferedImage carregarImagem(String caminho) {
        try {
            File f = new File(caminho);
            if (f.exists()) {
                return ImageIO.read(f);
            }
        } catch (IOException e) {
            // fallback: retângulo colorido desenhado em paintComponent
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int boardW = larguraCelula * jogo.getColunasTabuleiro();
        int boardH = alturaCelula  * jogo.getLinhasTabuleiro();

        // 1. Tabuleiro
        if (imgTabuleiro != null) {
            g2.drawImage(imgTabuleiro, 0, 0, boardW, boardH, null);
        } else {
            desenharTabuleiroFallback(g2, boardW, boardH);
        }

        // 2. Destaque semitransparente nas casas alcançáveis
        if (casasAlcancaveis != null) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            g2.setColor(Color.GREEN);
            for (int id : casasAlcancaveis) {
                int col   = id % jogo.getColunasTabuleiro();
                int linha = id / jogo.getColunasTabuleiro();
                g2.fillRect(col * larguraCelula, linha * alturaCelula,
                        larguraCelula, alturaCelula);
            }
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        // 3. Piões de todos os jogadores
        for (int i = 0; i < jogo.getNumeroJogadores(); i++) {
            String cor   = jogo.getCorJogador(i);
            int    idCasa = jogo.getIdCasaJogador(i);
            BufferedImage img = imgPiaos.get(cor);
            if (img != null) {
                desenharPiao(g2, idCasa, img);
            } else {
                desenharPiaoFallback(g2, idCasa, corParaColor(cor));
            }
        }

        // 4. Dados no canto inferior direito
        if (ultimoLancamento != null) {
            int dadoW = larguraCelula * 2;
            int dadoH = alturaCelula  * 2;
            int x1 = boardW - dadoW * 2 - 10;
            int x2 = boardW - dadoW - 5;
            int y1 = boardH - dadoH - 10;
            desenharDado(g2, ultimoLancamento[0], x1, y1, dadoW, dadoH);
            desenharDado(g2, ultimoLancamento[1], x2, y1, dadoW, dadoH);
        }

        // 5. Borda colorida (10px) com a cor do jogador atual
        g2.setColor(corParaColor(jogo.getCorJogadorAtual()));
        g2.setStroke(new BasicStroke(10f));
        g2.drawRect(5, 5, boardW - 10, boardH - 10);
        g2.setStroke(new BasicStroke(1f));
    }

    private void desenharTabuleiroFallback(Graphics2D g2, int boardW, int boardH) {
        g2.setColor(new Color(34, 85, 34));
        g2.fillRect(0, 0, boardW, boardH);
        g2.setColor(new Color(50, 100, 50));
        for (int c = 0; c <= jogo.getColunasTabuleiro(); c++) {
            g2.drawLine(c * larguraCelula, 0, c * larguraCelula, boardH);
        }
        for (int l = 0; l <= jogo.getLinhasTabuleiro(); l++) {
            g2.drawLine(0, l * alturaCelula, boardW, l * alturaCelula);
        }
    }

    private void desenharPiao(Graphics2D g2, int idCasa, BufferedImage img) {
        int col   = idCasa % jogo.getColunasTabuleiro();
        int linha = idCasa / jogo.getColunasTabuleiro();
        int piaoW = larguraCelula - 4;
        int piaoH = alturaCelula  - 4;
        int px    = col   * larguraCelula + 2;
        int py    = linha * alturaCelula  + 2;
        g2.drawImage(img, px, py, piaoW, piaoH, null);
    }

    private void desenharPiaoFallback(Graphics2D g2, int idCasa, Color cor) {
        int col   = idCasa % jogo.getColunasTabuleiro();
        int linha = idCasa / jogo.getColunasTabuleiro();
        int piaoW = larguraCelula - 4;
        int piaoH = alturaCelula  - 4;
        int px    = col   * larguraCelula + 2;
        int py    = linha * alturaCelula  + 2;
        g2.setColor(cor);
        g2.fillOval(px, py, piaoW, piaoH);
        g2.setColor(Color.BLACK);
        g2.drawOval(px, py, piaoW, piaoH);
    }

    private void desenharDado(Graphics2D g2, int valor, int x, int y, int w, int h) {
        if (valor >= 1 && valor <= 6 && imgDados[valor - 1] != null) {
            g2.drawImage(imgDados[valor - 1], x, y, w, h, null);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(x, y, w, h, 8, 8);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(x, y, w, h, 8, 8);
            g2.setFont(new Font("Arial", Font.BOLD, h / 2));
            g2.drawString(String.valueOf(valor), x + w / 3, y + (int)(h * 0.7));
        }
    }

    private Color corParaColor(String cor) {
        switch (cor) {
            case "Vermelho": return Color.RED;
            case "Amarelo":  return Color.YELLOW;
            case "Branco":   return Color.WHITE;
            case "Verde":    return Color.GREEN;
            case "Violeta":  return new Color(128, 0, 128);
            case "Azul":     return Color.BLUE;
            default:         return Color.GRAY;
        }
    }

    /** Define as casas alcançáveis para destaque visual. Null limpa o destaque. */
    public void setCasasAlcancaveis(List<Integer> casas) {
        this.casasAlcancaveis = casas;
    }

    /** Define o último lançamento para exibição dos dados. Null oculta os dados. */
    public void setUltimoLancamento(int[] lancamento) {
        this.ultimoLancamento = lancamento;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int col   = e.getX() / larguraCelula;
        int linha = e.getY() / alturaCelula;
        int id    = linha * jogo.getColunasTabuleiro() + col;
        try {
            jogo.deslocarPiao(id);
            casasAlcancaveis = null;
            ultimoLancamento = null;
            repaint();
        } catch (IllegalArgumentException ex) {
            // Casa inválida — ignora silenciosamente
        } catch (IllegalStateException ex) {
            // Dados não lançados — ignora silenciosamente
        }
    }

    @Override public void mousePressed(MouseEvent e)  { }
    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e)  { }
    @Override public void mouseExited(MouseEvent e)   { }
}
