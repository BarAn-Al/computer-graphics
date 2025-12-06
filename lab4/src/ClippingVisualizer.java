import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

// Класс для представления точки
class Point {
    int x, y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

// Класс для представления отрезка
class Line {
    Point p1, p2;
    
    public Line(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }
    
    public Line(int x1, int y1, int x2, int y2) {
        this.p1 = new Point(x1, y1);
        this.p2 = new Point(x2, y2);
    }
}

// Класс для представления многоугольника
class Polygon {
    List<Point> vertices;
    
    public Polygon() {
        vertices = new ArrayList<>();
    }
    
    public Polygon(List<Point> vertices) {
        this.vertices = vertices;
    }
    
    public void addVertex(Point p) {
        vertices.add(p);
    }
    
    public boolean isConvex() {
        int n = vertices.size();
        if (n < 3) return false;
        
        boolean isPositive = false;
        boolean isNegative = false;
        
        for (int i = 0; i < n; i++) {
            Point a = vertices.get(i);
            Point b = vertices.get((i + 1) % n);
            Point c = vertices.get((i + 2) % n);
            
            int crossProduct = (b.x - a.x) * (c.y - b.y) - (b.y - a.y) * (c.x - b.x);
            
            if (crossProduct > 0) {
                isPositive = true;
            } else if (crossProduct < 0) {
                isNegative = true;
            }
            
            if (isPositive && isNegative) {
                return false;
            }
        }
        
        return true;
    }
}

// Класс для реализации алгоритма Сазерленда-Коэна
class CohenSutherlandClipper {
    private static final int INSIDE = 0; // 0000
    private static final int LEFT = 1;   // 0001
    private static final int RIGHT = 2;  // 0010
    private static final int BOTTOM = 4; // 0100
    private static final int TOP = 8;    // 1000
    
    private int xMin, xMax, yMin, yMax;
    
    public CohenSutherlandClipper(int xMin, int yMin, int xMax, int yMax) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }
    
    // Определение кода точки
    private int computeCode(Point p) {
        int code = INSIDE;
        
        if (p.x < xMin) {
            code |= LEFT;
        } else if (p.x > xMax) {
            code |= RIGHT;
        }
        
        if (p.y < yMin) {
            code |= BOTTOM;
        } else if (p.y > yMax) {
            code |= TOP;
        }
        
        return code;
    }
    
    // Отсечение отрезка
    public Line clip(Line line) {
        Point p1 = line.p1;
        Point p2 = line.p2;
        
        int code1 = computeCode(p1);
        int code2 = computeCode(p2);
        
        while (true) {
            if ((code1 | code2) == 0) {
                // Оба кода равны 0 - отрезок полностью видим
                return new Line(p1, p2);
            } else if ((code1 & code2) != 0) {
                // Отрезок полностью невидим
                return null;
            } else {
                // Отрезок частично видим
                int codeOut = (code1 != 0) ? code1 : code2;
                Point p = new Point(0, 0);
                
                // Находим точку пересечения
                if ((codeOut & TOP) != 0) {
                    p.x = p1.x + (p2.x - p1.x) * (yMax - p1.y) / (p2.y - p1.y);
                    p.y = yMax;
                } else if ((codeOut & BOTTOM) != 0) {
                    p.x = p1.x + (p2.x - p1.x) * (yMin - p1.y) / (p2.y - p1.y);
                    p.y = yMin;
                } else if ((codeOut & RIGHT) != 0) {
                    p.y = p1.y + (p2.y - p1.y) * (xMax - p1.x) / (p2.x - p1.x);
                    p.x = xMax;
                } else if ((codeOut & LEFT) != 0) {
                    p.y = p1.y + (p2.y - p1.y) * (xMin - p1.x) / (p2.x - p1.x);
                    p.x = xMin;
                }
                
                // Заменяем точку вне окна точкой пересечения
                if (codeOut == code1) {
                    p1 = p;
                    code1 = computeCode(p1);
                } else {
                    p2 = p;
                    code2 = computeCode(p2);
                }
            }
        }
    }
}

// Класс для реализации алгоритма отсечения многоугольника (Sutherland-Hodgman)
class PolygonClipper {
    private int xMin, xMax, yMin, yMax;
    
    public PolygonClipper(int xMin, int yMin, int xMax, int yMax) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }
    
    // Отсечение по одной границе
    private List<Point> clipAgainstEdge(List<Point> input, int edge) {
        List<Point> output = new ArrayList<>();
        
        if (input.isEmpty()) {
            return output;
        }
        
        Point s = input.get(input.size() - 1);
        
        for (Point e : input) {
            // Проверка текущего ребра
            if (isInside(e, edge)) {
                if (!isInside(s, edge)) {
                    output.add(computeIntersection(s, e, edge));
                }
                output.add(e);
            } else if (isInside(s, edge)) {
                output.add(computeIntersection(s, e, edge));
            }
            s = e;
        }
        
        return output;
    }
    
    // Проверка, находится ли точка внутри относительно границы
    private boolean isInside(Point p, int edge) {
        switch (edge) {
            case 0: return p.x >= xMin; // Левая граница
            case 1: return p.x <= xMax; // Правая граница
            case 2: return p.y >= yMin; // Нижняя граница
            case 3: return p.y <= yMax; // Верхняя граница
            default: return false;
        }
    }
    
    // Вычисление точки пересечения
    private Point computeIntersection(Point s, Point e, int edge) {
        int x = 0, y = 0;
        
        // Для левой границы
        if (edge == 0 && s.x != e.x) {
            y = s.y + (e.y - s.y) * (xMin - s.x) / (e.x - s.x);
            x = xMin;
        }
        // Для правой границы
        else if (edge == 1 && s.x != e.x) {
            y = s.y + (e.y - s.y) * (xMax - s.x) / (e.x - s.x);
            x = xMax;
        }
        // Для нижней границы
        else if (edge == 2 && s.y != e.y) {
            x = s.x + (e.x - s.x) * (yMin - s.y) / (e.y - s.y);
            y = yMin;
        }
        // Для верхней границы
        else if (edge == 3 && s.y != e.y) {
            x = s.x + (e.x - s.x) * (yMax - s.y) / (e.y - s.y);
            y = yMax;
        }
        
        return new Point(x, y);
    }
    
    // Отсечение многоугольника
    public Polygon clip(Polygon polygon) {
        List<Point> vertices = new ArrayList<>(polygon.vertices);
        
        // Последовательно отсекаем по каждой границе
        for (int edge = 0; edge < 4; edge++) {
            vertices = clipAgainstEdge(vertices, edge);
            if (vertices.isEmpty()) {
                break;
            }
        }
        
        return new Polygon(vertices);
    }
}

// Главный класс приложения
public class ClippingVisualizer extends JFrame {
    private DrawingPanel drawingPanel;
    private JComboBox<String> algorithmComboBox;
    private JButton clipButton;
    private JButton addSegmentButton;
    private JButton addPolygonButton;
    private JButton clearButton;
    private JLabel statusLabel;
    
    private List<Line> segments;
    private Polygon polygon;
    private List<Line> clippedSegments;
    private Polygon clippedPolygon;
    
    private boolean isPolygonMode;
    private Point clippingWindowStart;
    private Point clippingWindowEnd;
    private boolean isDrawingWindow;
    
    public ClippingVisualizer() {
        setTitle("Визуализация алгоритмов отсечения");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        segments = new ArrayList<>();
        polygon = new Polygon();
        clippedSegments = new ArrayList<>();
        
        // Панель управления
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        
        algorithmComboBox = new JComboBox<>(new String[]{"Алгоритм Сазерленда-Коэна", "Алгоритм отсечения многоугольника"});
        clipButton = new JButton("Выполнить отсечение");
        addSegmentButton = new JButton("Добавить отрезок");
        addPolygonButton = new JButton("Добавить многоугольник");
        clearButton = new JButton("Очистить");
        statusLabel = new JLabel("Режим: Добавление отрезков");
        
        controlPanel.add(new JLabel("Алгоритм:"));
        controlPanel.add(algorithmComboBox);
        controlPanel.add(clipButton);
        controlPanel.add(addSegmentButton);
        controlPanel.add(addPolygonButton);
        controlPanel.add(clearButton);
        controlPanel.add(statusLabel);
        
        // Панель для рисования
        drawingPanel = new DrawingPanel();
        drawingPanel.setPreferredSize(new Dimension(800, 600));
        drawingPanel.setBackground(Color.WHITE);
        
        add(controlPanel, BorderLayout.NORTH);
        add(drawingPanel, BorderLayout.CENTER);
        
        // Обработчики событий
        clipButton.addActionListener(e -> performClipping());
        
        addSegmentButton.addActionListener(e -> {
            isPolygonMode = false;
            polygon = new Polygon();
            statusLabel.setText("Режим: Добавление отрезков. Кликните дважды для создания отрезка");
        });
        
        addPolygonButton.addActionListener(e -> {
            isPolygonMode = true;
            polygon = new Polygon();
            statusLabel.setText("Режим: Добавление многоугольника. Кликайте для добавления вершин, двойной клик - завершить");
        });
        
        clearButton.addActionListener(e -> {
            segments.clear();
            polygon = new Polygon();
            clippedSegments.clear();
            clippedPolygon = null;
            drawingPanel.repaint();
            statusLabel.setText("Очищено");
        });
        
        // Инициализация отсекающего окна (по умолчанию)
        clippingWindowStart = new Point(200, 150);
        clippingWindowEnd = new Point(500, 400);
        isDrawingWindow = false;
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    // Выполнение отсечения
    private void performClipping() {
        int selectedAlgorithm = algorithmComboBox.getSelectedIndex();
        
        if (selectedAlgorithm == 0) {
            // Алгоритм Сазерленда-Коэна для отрезков
            clippedSegments.clear();
            clippedPolygon = null;
            
            CohenSutherlandClipper clipper = new CohenSutherlandClipper(
                clippingWindowStart.x, clippingWindowStart.y,
                clippingWindowEnd.x, clippingWindowEnd.y
            );
            
            for (Line segment : segments) {
                Line clipped = clipper.clip(segment);
                if (clipped != null) {
                    clippedSegments.add(clipped);
                }
            }
            
            statusLabel.setText("Отсечение отрезков выполнено. Видимых частей: " + clippedSegments.size());
        } else {
            // Алгоритм отсечения многоугольника
            if (polygon.vertices.size() < 3) {
                JOptionPane.showMessageDialog(this, 
                    "Для отсечения многоугольника нужно как минимум 3 вершины", 
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!polygon.isConvex()) {
                JOptionPane.showMessageDialog(this, 
                    "Многоугольник должен быть выпуклым", 
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            PolygonClipper polyClipper = new PolygonClipper(
                clippingWindowStart.x, clippingWindowStart.y,
                clippingWindowEnd.x, clippingWindowEnd.y
            );
            
            clippedPolygon = polyClipper.clip(polygon);
            statusLabel.setText("Отсечение многоугольника выполнено. Вершин в результате: " + 
                               (clippedPolygon != null ? clippedPolygon.vertices.size() : 0));
        }
        
        drawingPanel.repaint();
    }
    
    // Внутренний класс для панели рисования
    class DrawingPanel extends JPanel {
        private Point tempPoint;
        
        public DrawingPanel() {
            tempPoint = null;
            
            // Обработка мыши
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        // Двойной клик
                        if (isPolygonMode && polygon.vertices.size() >= 3) {
                            // Завершение построения многоугольника
                            drawingPanel.repaint();
                            statusLabel.setText("Многоугольник построен. Вершин: " + polygon.vertices.size());
                        } else if (!isPolygonMode && tempPoint != null) {
                            // Завершение построения отрезка
                            segments.add(new Line(tempPoint, new Point(e.getX(), e.getY())));
                            tempPoint = null;
                            drawingPanel.repaint();
                            statusLabel.setText("Отрезок добавлен. Всего отрезков: " + segments.size());
                        }
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        // Левый клик - добавление точек
                        if (isPolygonMode) {
                            polygon.addVertex(new Point(e.getX(), e.getY()));
                            drawingPanel.repaint();
                            statusLabel.setText("Вершина добавлена. Вершин: " + polygon.vertices.size());
                        } else {
                            if (tempPoint == null) {
                                tempPoint = new Point(e.getX(), e.getY());
                                statusLabel.setText("Задана первая точка отрезка. Кликните для второй точки");
                            } else {
                                segments.add(new Line(tempPoint, new Point(e.getX(), e.getY())));
                                tempPoint = null;
                                drawingPanel.repaint();
                                statusLabel.setText("Отрезок добавлен. Всего отрезков: " + segments.size());
                            }
                        }
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        // Правый клик - начало рисования отсекающего окна
                        isDrawingWindow = true;
                        clippingWindowStart = new Point(e.getX(), e.getY());
                        clippingWindowEnd = new Point(e.getX(), e.getY());
                        statusLabel.setText("Рисование отсекающего окна: задайте первую точку");
                    }
                }
            });
            
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (isDrawingWindow && SwingUtilities.isRightMouseButton(e)) {
                        // Перетаскивание для рисования окна
                        clippingWindowEnd = new Point(e.getX(), e.getY());
                        drawingPanel.repaint();
                    }
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Рисование системы координат
            drawCoordinateSystem(g2d);
            
            // Рисование отсекающего окна
            g2d.setColor(new Color(0, 100, 0, 100)); // Полупрозрачный зеленый
            g2d.fillRect(
                Math.min(clippingWindowStart.x, clippingWindowEnd.x),
                Math.min(clippingWindowStart.y, clippingWindowEnd.y),
                Math.abs(clippingWindowEnd.x - clippingWindowStart.x),
                Math.abs(clippingWindowEnd.y - clippingWindowStart.y)
            );
            
            g2d.setColor(Color.GREEN);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(
                Math.min(clippingWindowStart.x, clippingWindowEnd.x),
                Math.min(clippingWindowStart.y, clippingWindowEnd.y),
                Math.abs(clippingWindowEnd.x - clippingWindowStart.x),
                Math.abs(clippingWindowEnd.y - clippingWindowStart.y)
            );
            
            // Рисование исходных отрезков
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(2));
            for (Line segment : segments) {
                g2d.drawLine(segment.p1.x, segment.p1.y, segment.p2.x, segment.p2.y);
            }
            
            // Рисование исходного многоугольника
            if (polygon.vertices.size() >= 2) {
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(2));
                
                if (polygon.vertices.size() == 2) {
                    // Если только 2 точки, рисуем отрезок
                    Point p1 = polygon.vertices.get(0);
                    Point p2 = polygon.vertices.get(1);
                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                } else {
                    // Рисуем многоугольник
                    for (int i = 0; i < polygon.vertices.size(); i++) {
                        Point p1 = polygon.vertices.get(i);
                        Point p2 = polygon.vertices.get((i + 1) % polygon.vertices.size());
                        g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }
                
                // Рисование вершин многоугольника
                g2d.setColor(Color.RED);
                for (Point vertex : polygon.vertices) {
                    g2d.fillOval(vertex.x - 4, vertex.y - 4, 8, 8);
                }
            }
            
            // Рисование результата отсечения отрезков
            g2d.setColor(Color.MAGENTA);
            g2d.setStroke(new BasicStroke(3));
            for (Line clipped : clippedSegments) {
                g2d.drawLine(clipped.p1.x, clipped.p1.y, clipped.p2.x, clipped.p2.y);
            }
            
            // Рисование результата отсечения многоугольника
            if (clippedPolygon != null && clippedPolygon.vertices.size() >= 2) {
                g2d.setColor(Color.ORANGE);
                g2d.setStroke(new BasicStroke(3));
                
                // Рисуем многоугольник
                for (int i = 0; i < clippedPolygon.vertices.size(); i++) {
                    Point p1 = clippedPolygon.vertices.get(i);
                    Point p2 = clippedPolygon.vertices.get((i + 1) % clippedPolygon.vertices.size());
                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
                
                // Заливка многоугольника
                g2d.setColor(new Color(255, 165, 0, 50));
                int[] xPoints = new int[clippedPolygon.vertices.size()];
                int[] yPoints = new int[clippedPolygon.vertices.size()];
                for (int i = 0; i < clippedPolygon.vertices.size(); i++) {
                    xPoints[i] = clippedPolygon.vertices.get(i).x;
                    yPoints[i] = clippedPolygon.vertices.get(i).y;
                }
                g2d.fillPolygon(xPoints, yPoints, clippedPolygon.vertices.size());
            }
            
            // Рисование временной точки для отрезка
            if (tempPoint != null && !isPolygonMode) {
                g2d.setColor(Color.BLUE);
                g2d.fillOval(tempPoint.x - 4, tempPoint.y - 4, 8, 8);
            }
            
            // Легенда
            drawLegend(g2d);
        }
        
        private void drawCoordinateSystem(Graphics2D g2d) {
            int width = getWidth();
            int height = getHeight();
            
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setStroke(new BasicStroke(1));
            
            // Вертикальные линии
            for (int x = 50; x < width; x += 50) {
                g2d.drawLine(x, 0, x, height);
            }
            
            // Горизонтальные линии
            for (int y = 50; y < height; y += 50) {
                g2d.drawLine(0, y, width, y);
            }
            
            // Оси
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(0, height/2, width, height/2); // Ось X
            g2d.drawLine(width/2, 0, width/2, height); // Ось Y
            
            // Подписи осей
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("X", width - 10, height/2 - 5);
            g2d.drawString("Y", width/2 + 5, 10);
            
            // Подписи делений
            for (int x = 50; x < width; x += 50) {
                if (x != width/2) {
                    g2d.drawString(String.valueOf(x - width/2), x - 10, height/2 + 15);
                }
            }
            
            for (int y = 50; y < height; y += 50) {
                if (y != height/2) {
                    g2d.drawString(String.valueOf(height/2 - y), width/2 + 5, y + 5);
                }
            }
        }
        
        private void drawLegend(Graphics2D g2d) {
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            
            int legendX = getWidth() - 200;
            int legendY = 20;
            int lineHeight = 20;
            
            // Фон легенды
            g2d.setColor(new Color(255, 255, 255, 200));
            g2d.fillRect(legendX - 10, legendY - 10, 190, 130);
            
            g2d.setColor(Color.BLACK);
            g2d.drawString("Легенда:", legendX, legendY);
            
            // Отсекающее окно
            g2d.setColor(Color.GREEN);
            g2d.fillRect(legendX, legendY + lineHeight, 15, 15);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Отсекающее окно", legendX + 25, legendY + lineHeight + 12);
            
            // Исходные отрезки
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(legendX, legendY + 2*lineHeight + 7, legendX + 15, legendY + 2*lineHeight + 7);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Исходные отрезки", legendX + 25, legendY + 2*lineHeight + 12);
            
            // Исходный многоугольник
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(legendX, legendY + 3*lineHeight + 7, legendX + 15, legendY + 3*lineHeight + 7);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Исходный многоугольник", legendX + 25, legendY + 3*lineHeight + 12);
            
            // Результат отсечения
            g2d.setColor(Color.MAGENTA);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(legendX, legendY + 4*lineHeight + 7, legendX + 15, legendY + 4*lineHeight + 7);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Результат отсечения", legendX + 25, legendY + 4*lineHeight + 12);
            
            g2d.setColor(Color.ORANGE);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(legendX, legendY + 5*lineHeight + 7, legendX + 15, legendY + 5*lineHeight + 7);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Результат (многоуг.)", legendX + 25, legendY + 5*lineHeight + 12);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ClippingVisualizer();
        });
    }
}