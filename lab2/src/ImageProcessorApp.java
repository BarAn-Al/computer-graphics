import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class ImageProcessorApp extends JFrame {
    private BufferedImage originalImage;
    private BufferedImage processedImage;
    private JLabel originalImageLabel;
    private JLabel processedImageLabel;
    private JLabel originalHistogramLabel;
    private JLabel processedHistogramLabel;
    
    public ImageProcessorApp() {
        setTitle("Image Processor - Обработка изображений");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        
        createMenuBar();
        
        setupUI();
        
        setVisible(true);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("Файл");
        JMenuItem openItem = new JMenuItem("Открыть изображение");
        JMenuItem saveItem = new JMenuItem("Сохранить изображение");
        JMenuItem exitItem = new JMenuItem("Выход");
        
        openItem.addActionListener(e -> openImage());
        saveItem.addActionListener(e -> saveImage());
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        JMenu histogramMenu = new JMenu("Гистограмма");
        JMenuItem showHistogramItem = new JMenuItem("Построить гистограммы");
        JMenuItem equalizeHistogramRGBItem = new JMenuItem("Эквализация (RGB)");
        JMenuItem equalizeHistogramHSVItem = new JMenuItem("Эквализация (HSV - только яркость)");
        
        showHistogramItem.addActionListener(e -> showHistograms());
        equalizeHistogramRGBItem.addActionListener(e -> equalizeHistogramRGB());
        equalizeHistogramHSVItem.addActionListener(e -> equalizeHistogramHSV());
        
        histogramMenu.add(showHistogramItem);
        histogramMenu.addSeparator();
        histogramMenu.add(equalizeHistogramRGBItem);
        histogramMenu.add(equalizeHistogramHSVItem);
        
        JMenu contrastMenu = new JMenu("Контрастирование");
        JMenuItem linearContrastItem = new JMenuItem("Линейное контрастирование");
        
        linearContrastItem.addActionListener(e -> linearContrast());
        contrastMenu.add(linearContrastItem);
        
        JMenu elementwiseMenu = new JMenu("Поэлементные операции");
        
        JMenuItem addConstantItem = new JMenuItem("Добавить константу");
        JMenuItem negativeItem = new JMenuItem("Негатив");
        JMenuItem multiplyConstantItem = new JMenuItem("Умножить на константу");
        JMenuItem powerTransformItem = new JMenuItem("Степенное преобразование");
        JMenuItem logTransformItem = new JMenuItem("Логарифмическое преобразование");
        
        addConstantItem.addActionListener(e -> addConstant());
        negativeItem.addActionListener(e -> negativeTransform());
        multiplyConstantItem.addActionListener(e -> multiplyConstant());
        powerTransformItem.addActionListener(e -> powerTransform());
        logTransformItem.addActionListener(e -> logTransform());
        
        elementwiseMenu.add(addConstantItem);
        elementwiseMenu.add(negativeItem);
        elementwiseMenu.add(multiplyConstantItem);
        elementwiseMenu.addSeparator();
        elementwiseMenu.add(powerTransformItem);
        elementwiseMenu.add(logTransformItem);
        
        menuBar.add(fileMenu);
        menuBar.add(histogramMenu);
        menuBar.add(contrastMenu);
        menuBar.add(elementwiseMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel imagePanel = new JPanel(new GridLayout(2, 2, 10, 10));
        
        JPanel originalImagePanel = new JPanel(new BorderLayout());
        originalImagePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Исходное изображение", 
            TitledBorder.CENTER, TitledBorder.TOP));
        originalImageLabel = new JLabel("Откройте изображение", SwingConstants.CENTER);
        originalImageLabel.setPreferredSize(new Dimension(400, 300));
        originalImagePanel.add(originalImageLabel, BorderLayout.CENTER);
        
        JPanel processedImagePanel = new JPanel(new BorderLayout());
        processedImagePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Обработанное изображение", 
            TitledBorder.CENTER, TitledBorder.TOP));
        processedImageLabel = new JLabel("Результат обработки", SwingConstants.CENTER);
        processedImageLabel.setPreferredSize(new Dimension(400, 300));
        processedImagePanel.add(processedImageLabel, BorderLayout.CENTER);
        
        JPanel originalHistogramPanel = new JPanel(new BorderLayout());
        originalHistogramPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Гистограмма исходного", 
            TitledBorder.CENTER, TitledBorder.TOP));
        originalHistogramLabel = new JLabel("Гистограмма", SwingConstants.CENTER);
        originalHistogramLabel.setPreferredSize(new Dimension(400, 150));
        originalHistogramPanel.add(originalHistogramLabel, BorderLayout.CENTER);
        
        JPanel processedHistogramPanel = new JPanel(new BorderLayout());
        processedHistogramPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Гистограмма обработанного", 
            TitledBorder.CENTER, TitledBorder.TOP));
        processedHistogramLabel = new JLabel("Гистограмма", SwingConstants.CENTER);
        processedHistogramLabel.setPreferredSize(new Dimension(400, 150));
        processedHistogramPanel.add(processedHistogramLabel, BorderLayout.CENTER);
        
        imagePanel.add(originalImagePanel);
        imagePanel.add(processedImagePanel);
        imagePanel.add(originalHistogramPanel);
        imagePanel.add(processedHistogramPanel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton resetButton = new JButton("Сбросить изменения");
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.setPreferredSize(new Dimension(200, 40));
        resetButton.addActionListener(e -> resetImage());
        
        buttonPanel.add(resetButton);
        
        mainPanel.add(imagePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void openImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Изображения", "jpg", "jpeg", "png", "bmp", "gif"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                originalImage = ImageIO.read(file);
                if (processedImage == null) {
            processedImage = copyImage(originalImage);
        }
                
                updateImageDisplays();
                showHistograms();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка загрузки изображения: " + ex.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveImage() {
        if (processedImage == null) {
            JOptionPane.showMessageDialog(this, "Нет изображения для сохранения", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Сохранить изображение");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "PNG изображения", "png"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new File(file.getAbsolutePath() + ".png");
                }
                ImageIO.write(processedImage, "png", file);
                JOptionPane.showMessageDialog(this, "Изображение сохранено", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка сохранения изображения", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateImageDisplays() {
        if (originalImage != null) {
            ImageIcon originalIcon = new ImageIcon(scaleImage(originalImage, 400, 300));
            originalImageLabel.setIcon(originalIcon);
            originalImageLabel.setText("");
        }
        
        if (processedImage != null) {
            ImageIcon processedIcon = new ImageIcon(scaleImage(processedImage, 400, 300));
            processedImageLabel.setIcon(processedIcon);
            processedImageLabel.setText("");
        }
    }
    
    private Image scaleImage(BufferedImage image, int width, int height) {
        return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
    
    private BufferedImage copyImage(BufferedImage source) {
        BufferedImage copy = new BufferedImage(
            source.getWidth(), source.getHeight(), source.getType());
        Graphics2D g = copy.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return copy;
    }
    
    private void showHistograms() {
        if (originalImage == null) {
            JOptionPane.showMessageDialog(this, "Сначала откройте изображение",
                "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        BufferedImage originalHistogram = createHistogramImage(originalImage);
        originalHistogramLabel.setIcon(new ImageIcon(originalHistogram.getScaledInstance(400, 300, Image.SCALE_SMOOTH)));
        originalHistogramLabel.setText("");
        
        BufferedImage processedHistogram = createHistogramImage(processedImage);
        processedHistogramLabel.setIcon(new ImageIcon(processedHistogram.getScaledInstance(400, 300, Image.SCALE_SMOOTH)));
        processedHistogramLabel.setText("");
    }
    
    private BufferedImage createHistogramImage(BufferedImage image) {
        int width = 255;
        int height = 300;
        BufferedImage histogram = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = histogram.createGraphics();
        
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        
        int[] redHist = new int[256];
        int[] greenHist = new int[256];
        int[] blueHist = new int[256];
        int[] grayHist = new int[256];
        
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb);
                redHist[color.getRed()]++;
                greenHist[color.getGreen()]++;
                blueHist[color.getBlue()]++;
                int gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                grayHist[gray]++;
            }
        }
        
        int max = 0;
        for (int i = 0; i < 256; i++) {
            max = Math.max(max, grayHist[i]);
        }
        
        g.setColor(Color.BLACK);
        for (int i = 0; i < 256; i++) {
            int barHeight = (int)((double)grayHist[i] / max * height);
            g.drawLine(i, height, i, height - barHeight);
        }
        
        for (int i = 0; i < 256; i++) {
            int redHeight = (int)((double)redHist[i] / max * height * 0.7);
            g.setColor(new Color(255, 0, 0, 128));
            g.drawLine(i, height, i, height - redHeight);
            
            int greenHeight = (int)((double)greenHist[i] / max * height * 0.7);
            g.setColor(new Color(0, 255, 0, 128));
            g.drawLine(i, height, i, height - greenHeight);
            
            int blueHeight = (int)((double)blueHist[i] / max * height * 0.7);
            g.setColor(new Color(0, 0, 255, 128));
            g.drawLine(i, height, i, height - blueHeight);
        }
        
        g.dispose();
        return histogram;
    }
    
    private void equalizeHistogramRGB() {
        if (originalImage == null) return;
        
        if (processedImage == null) {
            processedImage = copyImage(originalImage);
        }
        int width = processedImage.getWidth();
        int height = processedImage.getHeight();
        
        int[] redHist = new int[256];
        int[] greenHist = new int[256];
        int[] blueHist = new int[256];
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = processedImage.getRGB(x, y);
                Color color = new Color(rgb);
                redHist[color.getRed()]++;
                greenHist[color.getGreen()]++;
                blueHist[color.getBlue()]++;
            }
        }
        
        int[] redCDF = new int[256];
        int[] greenCDF = new int[256];
        int[] blueCDF = new int[256];
        
        redCDF[0] = redHist[0];
        greenCDF[0] = greenHist[0];
        blueCDF[0] = blueHist[0];
        
        for (int i = 1; i < 256; i++) {
            redCDF[i] = redCDF[i-1] + redHist[i];
            greenCDF[i] = greenCDF[i-1] + greenHist[i];
            blueCDF[i] = blueCDF[i-1] + blueHist[i];
        }
        
        int totalPixels = width * height;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = processedImage.getRGB(x, y);
                Color color = new Color(rgb);
                
                int newRed = (int)(255.0 * redCDF[color.getRed()] / totalPixels);
                int newGreen = (int)(255.0 * greenCDF[color.getGreen()] / totalPixels);
                int newBlue = (int)(255.0 * blueCDF[color.getBlue()] / totalPixels);
                
                newRed = Math.min(255, Math.max(0, newRed));
                newGreen = Math.min(255, Math.max(0, newGreen));
                newBlue = Math.min(255, Math.max(0, newBlue));
                
                Color newColor = new Color(newRed, newGreen, newBlue);
                processedImage.setRGB(x, y, newColor.getRGB());
            }
        }
        
        updateImageDisplays();
        showHistograms();
    }
    
    private void equalizeHistogramHSV() {
        if (originalImage == null) return;
        
        if (processedImage == null) {
            processedImage = copyImage(originalImage);
        }
        int width = processedImage.getWidth();
        int height = processedImage.getHeight();
        
        float[] hsv = new float[3];
        float[] brightnessValues = new float[width * height];
        int index = 0;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = processedImage.getRGB(x, y);
                Color.RGBtoHSB(
                    (rgb >> 16) & 0xFF,
                    (rgb >> 8) & 0xFF,
                    rgb & 0xFF,
                    hsv
                );
                brightnessValues[index++] = hsv[2];
            }
        }
        
        int[] hist = new int[256];
        for (float value : brightnessValues) {
            int bin = (int)(value * 255);
            hist[bin]++;
        }
        
        int[] cdf = new int[256];
        cdf[0] = hist[0];
        for (int i = 1; i < 256; i++) {
            cdf[i] = cdf[i-1] + hist[i];
        }
        
        int totalPixels = width * height;
        float[] equalizedValues = new float[256];
        
        for (int i = 0; i < 256; i++) {
            equalizedValues[i] = (float)cdf[i] / totalPixels;
        }
        
        index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = processedImage.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                
                Color.RGBtoHSB(r, g, b, hsv);
                
                int oldValue = (int)(hsv[2] * 255);
                hsv[2] = equalizedValues[oldValue];
                
                int newRGB = Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]);
                processedImage.setRGB(x, y, newRGB);
            }
        }
        
        updateImageDisplays();
        showHistograms();
    }
    
    private void linearContrast() {
        if (originalImage == null) return;
        
        int minBrightness = 255;
        int maxBrightness = 0;
        
        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                Color color = new Color(originalImage.getRGB(x, y));
                int brightness = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                minBrightness = Math.min(minBrightness, brightness);
                maxBrightness = Math.max(maxBrightness, brightness);
            }
        }
        
        if (minBrightness == 0 && maxBrightness == 255) {
            JOptionPane.showMessageDialog(this, 
                "Изображение уже использует полный диапазон яркости (0-255)",
                "Информация", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (processedImage == null) {
            processedImage = copyImage(originalImage);
        }

        double scale = 255.0 / (maxBrightness - minBrightness);
        
        for (int y = 0; y < processedImage.getHeight(); y++) {
            for (int x = 0; x < processedImage.getWidth(); x++) {
                Color color = new Color(processedImage.getRGB(x, y));
                
                int newRed = (int)((color.getRed() - minBrightness) * scale);
                int newGreen = (int)((color.getGreen() - minBrightness) * scale);
                int newBlue = (int)((color.getBlue() - minBrightness) * scale);
                
                newRed = Math.min(255, Math.max(0, newRed));
                newGreen = Math.min(255, Math.max(0, newGreen));
                newBlue = Math.min(255, Math.max(0, newBlue));
                
                Color newColor = new Color(newRed, newGreen, newBlue);
                processedImage.setRGB(x, y, newColor.getRGB());
            }
        }
        
        updateImageDisplays();
        showHistograms();
        JOptionPane.showMessageDialog(this, 
            String.format("Линейное контрастирование выполнено\nИсходный диапазон: %d-%d\nНовый диапазон: 0-255",
                minBrightness, maxBrightness),
            "Информация", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void addConstant() {
        if (originalImage == null) return;
        
        String input = JOptionPane.showInputDialog(this, 
            "Введите целочисленную константу (-255 до 255):", "50");
        if (input == null) return;
        
        try {
            int constant = Integer.parseInt(input);
            constant = Math.min(255, Math.max(-255, constant));
            
            if (processedImage == null) {
                processedImage = copyImage(originalImage);
            }
            
            for (int y = 0; y < processedImage.getHeight(); y++) {
                for (int x = 0; x < processedImage.getWidth(); x++) {
                    Color color = new Color(processedImage.getRGB(x, y));
                    
                    int newRed = color.getRed() + constant;
                    int newGreen = color.getGreen() + constant;
                    int newBlue = color.getBlue() + constant;
                    
                    newRed = Math.min(255, Math.max(0, newRed));
                    newGreen = Math.min(255, Math.max(0, newGreen));
                    newBlue = Math.min(255, Math.max(0, newBlue));
                    
                    Color newColor = new Color(newRed, newGreen, newBlue);
                    processedImage.setRGB(x, y, newColor.getRGB());
                }
            }
            
            updateImageDisplays();
            showHistograms();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Введите корректное число",
                "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void negativeTransform() {
        if (originalImage == null) return;
        
        if (processedImage == null) {
            processedImage = copyImage(originalImage);
        }

        for (int y = 0; y < processedImage.getHeight(); y++) {
            for (int x = 0; x < processedImage.getWidth(); x++) {
                Color color = new Color(processedImage.getRGB(x, y));
                
                int newRed = 255 - color.getRed();
                int newGreen = 255 - color.getGreen();
                int newBlue = 255 - color.getBlue();
                
                Color newColor = new Color(newRed, newGreen, newBlue);
                processedImage.setRGB(x, y, newColor.getRGB());
            }
        }
        
        updateImageDisplays();
        showHistograms();
    }
    
    private void multiplyConstant() {
        if (originalImage == null) return;
        
        String input = JOptionPane.showInputDialog(this, 
            "Введите множитель (0.1 до 5.0):", "1.5");
        if (input == null) return;
        
        try {
            double multiplier = Double.parseDouble(input);
            multiplier = Math.min(5.0, Math.max(0.1, multiplier));
            
            if (processedImage == null) {
                processedImage = copyImage(originalImage);
            }
            
            for (int y = 0; y < processedImage.getHeight(); y++) {
                for (int x = 0; x < processedImage.getWidth(); x++) {
                    Color color = new Color(processedImage.getRGB(x, y));
                    
                    int newRed = (int)(color.getRed() * multiplier);
                    int newGreen = (int)(color.getGreen() * multiplier);
                    int newBlue = (int)(color.getBlue() * multiplier);
                    
                    newRed = Math.min(255, Math.max(0, newRed));
                    newGreen = Math.min(255, Math.max(0, newGreen));
                    newBlue = Math.min(255, Math.max(0, newBlue));
                    
                    Color newColor = new Color(newRed, newGreen, newBlue);
                    processedImage.setRGB(x, y, newColor.getRGB());
                }
            }
            
            updateImageDisplays();
            showHistograms();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Введите корректное число",
                "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void powerTransform() {
        if (originalImage == null) return;
        
        String input = JOptionPane.showInputDialog(this, 
            "Введите показатель степени (0.1 до 5.0):\n<1 - увеличит яркость, >1 - уменьшит яркость", "1.0");
        if (input == null) return;
        
        try {
            double gamma = Double.parseDouble(input);
            gamma = Math.min(5.0, Math.max(0.1, gamma));
            
            if (processedImage == null) {
                processedImage = copyImage(originalImage);
            }
            
            for (int y = 0; y < processedImage.getHeight(); y++) {
                for (int x = 0; x < processedImage.getWidth(); x++) {
                    Color color = new Color(processedImage.getRGB(x, y));
                    
                    double red = color.getRed() / 255.0;
                    double green = color.getGreen() / 255.0;
                    double blue = color.getBlue() / 255.0;
                    
                    red = Math.pow(red, gamma);
                    green = Math.pow(green, gamma);
                    blue = Math.pow(blue, gamma);
                    
                    int newRed = (int)(red * 255);
                    int newGreen = (int)(green * 255);
                    int newBlue = (int)(blue * 255);
                    
                    newRed = Math.min(255, Math.max(0, newRed));
                    newGreen = Math.min(255, Math.max(0, newGreen));
                    newBlue = Math.min(255, Math.max(0, newBlue));
                    
                    Color newColor = new Color(newRed, newGreen, newBlue);
                    processedImage.setRGB(x, y, newColor.getRGB());
                }
            }
            
            updateImageDisplays();
            showHistograms();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Введите корректное число",
                "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void logTransform() {
        if (originalImage == null) return;
        
        if (processedImage == null) {
            processedImage = copyImage(originalImage);
        }

        for (int y = 0; y < processedImage.getHeight(); y++) {
            for (int x = 0; x < processedImage.getWidth(); x++) {
                Color color = new Color(processedImage.getRGB(x, y));
                
                double red = Math.log(1 + color.getRed()) / Math.log(256) * 255;
                double green = Math.log(1 + color.getGreen()) / Math.log(256) * 255;
                double blue = Math.log(1 + color.getBlue()) / Math.log(256) * 255;
                
                int newRed = (int)red;
                int newGreen = (int)green;
                int newBlue = (int)blue;
                
                newRed = Math.min(255, Math.max(0, newRed));
                newGreen = Math.min(255, Math.max(0, newGreen));
                newBlue = Math.min(255, Math.max(0, newBlue));
                
                Color newColor = new Color(newRed, newGreen, newBlue);
                processedImage.setRGB(x, y, newColor.getRGB());
            }
        }
        
        updateImageDisplays();
        showHistograms();
    }
    
    private void resetImage() {
        if (originalImage != null) {
            processedImage = copyImage(originalImage);
                
            updateImageDisplays();
            showHistograms();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ImageProcessorApp();
        });
    }
}