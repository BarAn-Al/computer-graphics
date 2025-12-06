import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

class Pixel {
    int x, y;
    Color color;
    
    Pixel(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }
}

class DrawingPanel extends JPanel {
    private List<Pixel> pixels = new ArrayList<>();
    private int scale = 15;
    private int originX, originY;
    private String currentAlgorithm = "Step-by-Step";
    private boolean showGrid = true;
    private boolean showAxes = true;
    private boolean showLabels = true;
    
    public DrawingPanel() {
        setBackground(Color.WHITE);
    }
    
    public void setScale(int scale) {
        this.scale = scale;
        repaint();
    }
    
    public void setCurrentAlgorithm(String algorithm) {
        this.currentAlgorithm = algorithm;
    }
    
    public void clearPixels() {
        pixels.clear();
        repaint();
    }
    
    public void addPixel(int x, int y, Color color) {
        pixels.add(new Pixel(x, y, color));
    }
    
    public void drawLine(int x1, int y1, int x2, int y2) {
        clearPixels();
        
        long startTime = System.nanoTime();
        
        switch(currentAlgorithm) {
            case "Step-by-Step" -> drawLineStepByStep(x1, y1, x2, y2, Color.RED);
            case "DDA" -> drawLineDDA(x1, y1, x2, y2, Color.BLUE);
            case "Bresenham Line" -> drawLineBresenham(x1, y1, x2, y2, Color.GREEN);
            case "Bresenham Circle" -> {
                int radius = (int)Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
                drawCircleBresenham(x1, y1, radius, Color.MAGENTA);
            }
        }
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        double secs = duration / 1000000000.0;
        
        System.out.println(currentAlgorithm + " время выполнения: " + secs + " c");
        
        repaint();
    }
    
    private void drawLineStepByStep(int x1, int y1, int x2, int y2, Color color) {
        if (x1 == x2 && y1 == y2) {
            addPixel(x1, y1, color);
            return;
        }
        
        int dx = x2 - x1;
        int dy = y2 - y1;
        
        if (Math.abs(dx) >= Math.abs(dy)) {
            float m = (float)dy / dx;
            float y = y1;
            
            if (dx > 0) {
                for (int x = x1; x <= x2; x++) {
                    addPixel(x, Math.round(y), color);
                    y += m;
                }
            } else {
                for (int x = x1; x >= x2; x--) {
                    addPixel(x, Math.round(y), color);
                    y -= m;
                }
            }
        } else {
            float m = (float)dx / dy;
            float x = x1;
            
            if (dy > 0) {
                for (int y = y1; y <= y2; y++) {
                    addPixel(Math.round(x), y, color);
                    x += m;
                }
            } else {
                for (int y = y1; y >= y2; y--) {
                    addPixel(Math.round(x), y, color);
                    x -= m;
                }
            }
        }
    }
    
    private void drawLineDDA(int x1, int y1, int x2, int y2, Color color) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        
        if (steps == 0) {
            addPixel(x1, y1, color);
            return;
        }
        
        float xIncrement = (float)dx / steps;
        float yIncrement = (float)dy / steps;
        
        float x = x1;
        float y = y1;
        
        for (int i = 0; i <= steps; i++) {
            addPixel(Math.round(x), Math.round(y), color);
            x += xIncrement;
            y += yIncrement;
        }
    }
    
    private void drawLineBresenham(int x1, int y1, int x2, int y2, Color color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        
        int err = dx - dy;
        
        while (true) {
            addPixel(x1, y1, color);
            
            if (x1 == x2 && y1 == y2) break;
            
            int err2 = 2 * err;
            
            if (err2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            
            if (err2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }
    
    private void drawCircleBresenham(int xc, int yc, int r, Color color) {
        int x = 0;
        int y = r;
        int d = 3 - 2 * r;
        
        drawCirclePoints(xc, yc, x, y, color);
        
        while (y >= x) {
            x++;
            
            if (d > 0) {
                y--;
                d = d + 4 * (x - y) + 10;
            } else {
                d = d + 4 * x + 6;
            }
            
            drawCirclePoints(xc, yc, x, y, color);
        }
    }
    
    private void drawCirclePoints(int xc, int yc, int x, int y, Color color) {
        addPixel(xc + x, yc + y, color);
        addPixel(xc - x, yc + y, color);
        addPixel(xc + x, yc - y, color);
        addPixel(xc - x, yc - y, color);
        addPixel(xc + y, yc + x, color);
        addPixel(xc - y, yc + x, color);
        addPixel(xc + y, yc - x, color);
        addPixel(xc - y, yc - x, color);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        originX = getWidth() / 2;
        originY = getHeight() / 2;
        
        for (Pixel pixel : pixels) {
            g2d.setColor(pixel.color);
            int x = originX + pixel.x * scale;
            int y = originY - pixel.y * scale; 
            g2d.fillRect(x, y, scale, scale);
            
            g2d.setColor(Color.BLACK);
        }
        
        if (showGrid) {
            g2d.setColor(new Color(240, 240, 240));
            
            for (int x = originX % scale; x < getWidth(); x += scale) {
                g2d.drawLine(x, 0, x, getHeight());
            }
            
            for (int y = originY % scale; y < getHeight(); y += scale) {
                g2d.drawLine(0, y, getWidth(), y);
            }
        }
        
        if (showAxes) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            
            g2d.drawLine(0, originY, getWidth(), originY);
            g2d.drawLine(originX, 0, originX, getHeight());
            
            int arrowSize = 8;
            g2d.fillPolygon(new int[]{getWidth() - arrowSize, getWidth(), getWidth() - arrowSize},
                           new int[]{originY - arrowSize/2, originY, originY + arrowSize/2}, 3);
            g2d.fillPolygon(new int[]{originX - arrowSize/2, originX, originX + arrowSize/2},
                           new int[]{arrowSize, 0, arrowSize}, 3);
            
            if (showLabels) {
                g2d.drawString("X", getWidth() - 20, originY - 10);
                g2d.drawString("Y", originX + 10, 20);
                
                for (int x = originX + scale; x < getWidth(); x += scale) {
                    if ((x - originX) / scale % 5 == 0) {
                        g2d.drawString(Integer.toString((x - originX) / scale), x - 5, originY + 15);
                        g2d.drawLine(x, originY - 3, x, originY + 3);
                    }
                }
                
                for (int x = originX - scale; x >= 0; x -= scale) {
                    if ((originX - x) / scale % 5 == 0) {
                        g2d.drawString(Integer.toString(-(originX - x) / scale), x - 10, originY + 15);
                        g2d.drawLine(x, originY - 3, x, originY + 3);
                    }
                }
                
                for (int y = originY + scale; y < getHeight(); y += scale) {
                    if ((y - originY) / scale % 5 == 0) {
                        g2d.drawString(Integer.toString(-(y - originY) / scale), originX + 5, y + 5);
                        g2d.drawLine(originX - 3, y, originX + 3, y);
                    }
                }
                
                for (int y = originY - scale; y >= 0; y -= scale) {
                    if ((originY - y) / scale % 5 == 0) {
                        g2d.drawString(Integer.toString((originY - y) / scale), originX + 5, y + 5);
                        g2d.drawLine(originX - 3, y, originX + 3, y);
                    }
                }
            }
        }
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Алгоритм: " + currentAlgorithm, 10, 20);
        g2d.drawString("Масштаб: " + scale + "px/единица", 10, 40);
        g2d.drawString("Координаты (0,0) в центре окна", 10, 60);
    }
}

public class RasterAlgorithmsApp extends JFrame {
    private DrawingPanel drawingPanel;
    private JComboBox<String> algorithmComboBox;
    private JSpinner scaleSpinner;
    private JTextField x1Field, y1Field, x2Field, y2Field;
    
    public RasterAlgorithmsApp() {
        setTitle("Растровые алгоритмы построения линий и окружностей");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        drawingPanel = new DrawingPanel();
        mainPanel.add(drawingPanel, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Алгоритм:"), gbc);
        
        String[] algorithms = {"Step-by-Step", "DDA", "Bresenham Line", "Bresenham Circle"};
        algorithmComboBox = new JComboBox<>(algorithms);
        algorithmComboBox.addActionListener(e -> {
            drawingPanel.setCurrentAlgorithm((String)algorithmComboBox.getSelectedItem());
        });
        gbc.gridx = 1; gbc.gridy = 0;
        controlPanel.add(algorithmComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("Масштаб (px/ед):"), gbc);
        
        scaleSpinner = new JSpinner(new SpinnerNumberModel(15, 1, 50, 1));
        scaleSpinner.addChangeListener(e -> {
            drawingPanel.setScale((Integer)scaleSpinner.getValue());
        });
        gbc.gridx = 1; gbc.gridy = 1;
        controlPanel.add(scaleSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        controlPanel.add(new JLabel("X1:"), gbc);
        
        x1Field = new JTextField("0", 5);
        gbc.gridx = 1; gbc.gridy = 2;
        controlPanel.add(x1Field, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        controlPanel.add(new JLabel("Y1:"), gbc);
        
        y1Field = new JTextField("0", 5);
        gbc.gridx = 1; gbc.gridy = 3;
        controlPanel.add(y1Field, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        controlPanel.add(new JLabel("X2:"), gbc);
        
        x2Field = new JTextField("10", 5);
        gbc.gridx = 1; gbc.gridy = 4;
        controlPanel.add(x2Field, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        controlPanel.add(new JLabel("Y2:"), gbc);
        
        y2Field = new JTextField("5", 5);
        gbc.gridx = 1; gbc.gridy = 5;
        controlPanel.add(y2Field, gbc);
        
        JButton drawButton = new JButton("Построить");
        drawButton.addActionListener(e -> drawShape());
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        controlPanel.add(drawButton, gbc);
        
        JButton clearButton = new JButton("Очистить");
        clearButton.addActionListener(e -> drawingPanel.clearPixels());
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        controlPanel.add(clearButton, gbc);
        
        mainPanel.add(controlPanel, BorderLayout.WEST);
        
        add(mainPanel);
    }
    
    private void drawShape() {
        try {
            int x1 = Integer.parseInt(x1Field.getText());
            int y1 = Integer.parseInt(y1Field.getText());
            int x2 = Integer.parseInt(x2Field.getText());
            int y2 = Integer.parseInt(y2Field.getText());
            
            drawingPanel.drawLine(x1, y1, x2, y2);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Пожалуйста, введите целые числа для координат", 
                "Ошибка ввода", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RasterAlgorithmsApp app = new RasterAlgorithmsApp();
            app.setVisible(true);
        });
    }
}