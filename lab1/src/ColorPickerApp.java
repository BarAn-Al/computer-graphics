import java.awt.*;
import javax.swing.*;

public class ColorPickerApp extends JFrame {
    private Color currentColor = Color.WHITE;
    
    // RGB компоненты
    private JSlider redSlider, greenSlider, blueSlider;
    private JSpinner redSpinner, greenSpinner, blueSpinner;
    
    // HLS компоненты
    private JSlider hueSlider, saturationSlider, lightnessSlider;
    private JSpinner hueSpinner, saturationSpinner, lightnessSpinner;
    
    // CMYK компоненты
    private JSlider cyanSlider, magentaSlider, yellowSlider, blackSlider;
    private JSpinner cyanSpinner, magentaSpinner, yellowSpinner, blackSpinner;
    
    // Отображение
    private JPanel colorDisplayPanel;
    private JLabel hexLabel;
    private JTextField rgbField, hlsField, cmykField;
    
    // Флаг для предотвращения рекурсии
    private boolean updating = false;
    
    public ColorPickerApp() {
        setTitle("Color Picker - RGB/HLS/CMYK");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLayout(new BorderLayout());
        
        initComponents();
        pack();
        setLocationRelativeTo(null);
        updateAllDisplays(currentColor);
    }
    
    private void initComponents() {
        // Панель отображения цвета
        JPanel topPanel = new JPanel(new BorderLayout());
        colorDisplayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(currentColor);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
        };
        colorDisplayPanel.setPreferredSize(new Dimension(0, 80));
        colorDisplayPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        hexLabel = new JLabel("#FFFFFF", SwingConstants.CENTER);
        hexLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        topPanel.add(colorDisplayPanel, BorderLayout.CENTER);
        topPanel.add(hexLabel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        
        // Основная панель с табами
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("RGB", createRGBPanel());
        tabbedPane.addTab("HLS", createHLSPanel());
        tabbedPane.addTab("CMYK", createCMYKPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Нижняя панель с текстовыми полями
        add(createBottomPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createRGBPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Красный
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Красный:"), gbc);
        
        redSlider = new JSlider(0, 255, 255);
        redSlider.addChangeListener(e -> updateFromRGB(true));
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(redSlider, gbc);
        
        redSpinner = new JSpinner(new SpinnerNumberModel(255, 0, 255, 1));
        redSpinner.addChangeListener(e -> updateFromRGB(false));
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(redSpinner, gbc);
        
        // Зеленый
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Зеленый:"), gbc);
        
        greenSlider = new JSlider(0, 255, 255);
        greenSlider.addChangeListener(e -> updateFromRGB(true));
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(greenSlider, gbc);
        
        greenSpinner = new JSpinner(new SpinnerNumberModel(255, 0, 255, 1));
        greenSpinner.addChangeListener(e -> updateFromRGB(false));
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(greenSpinner, gbc);
        
        // Синий
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("Синий:"), gbc);
        
        blueSlider = new JSlider(0, 255, 255);
        blueSlider.addChangeListener(e -> updateFromRGB(true));
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(blueSlider, gbc);
        
        blueSpinner = new JSpinner(new SpinnerNumberModel(255, 0, 255, 1));
        blueSpinner.addChangeListener(e -> updateFromRGB(false));
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(blueSpinner, gbc);
        
        return panel;
    }
    
    private JPanel createHLSPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Оттенок
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Оттенок (0-359):"), gbc);
        
        hueSlider = new JSlider(0, 359, 0);
        hueSlider.addChangeListener(e -> updateFromHLS(true));
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(hueSlider, gbc);
        
        hueSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 359, 1));
        hueSpinner.addChangeListener(e -> updateFromHLS(false));
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(hueSpinner, gbc);
        
        // Насыщенность
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Насыщенность (%):"), gbc);
        
        saturationSlider = new JSlider(0, 100, 0);
        saturationSlider.addChangeListener(e -> updateFromHLS(true));
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(saturationSlider, gbc);
        
        saturationSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        saturationSpinner.addChangeListener(e -> updateFromHLS(false));
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(saturationSpinner, gbc);
        
        // Яркость
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("Яркость (%):"), gbc);
        
        lightnessSlider = new JSlider(0, 100, 100);
        lightnessSlider.addChangeListener(e -> updateFromHLS(true));
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(lightnessSlider, gbc);
        
        lightnessSpinner = new JSpinner(new SpinnerNumberModel(100, 0, 100, 1));
        lightnessSpinner.addChangeListener(e -> updateFromHLS(false));
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(lightnessSpinner, gbc);
        
        return panel;
    }
    
    private JPanel createCMYKPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Cyan
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Голубой (%):"), gbc);
        
        cyanSlider = new JSlider(0, 100, 0);
        cyanSlider.addChangeListener(e -> updateFromCMYK(true));
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(cyanSlider, gbc);
        
        cyanSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        cyanSpinner.addChangeListener(e -> updateFromCMYK(false));
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(cyanSpinner, gbc);
        
        // Magenta
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Пурпурный (%):"), gbc);
        
        magentaSlider = new JSlider(0, 100, 0);
        magentaSlider.addChangeListener(e -> updateFromCMYK(true));
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(magentaSlider, gbc);
        
        magentaSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        magentaSpinner.addChangeListener(e -> updateFromCMYK(false));
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(magentaSpinner, gbc);
        
        // Yellow
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("Желтый (%):"), gbc);
        
        yellowSlider = new JSlider(0, 100, 0);
        yellowSlider.addChangeListener(e -> updateFromCMYK(true));
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(yellowSlider, gbc);
        
        yellowSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        yellowSpinner.addChangeListener(e -> updateFromCMYK(false));
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(yellowSpinner, gbc);
        
        // Black
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(new JLabel("Черный (%):"), gbc);
        
        blackSlider = new JSlider(0, 100, 0);
        blackSlider.addChangeListener(e -> updateFromCMYK(true));
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(blackSlider, gbc);
        
        blackSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        blackSpinner.addChangeListener(e -> updateFromCMYK(false));
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(blackSpinner, gbc);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Текстовые поля
        JPanel textPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        
        rgbField = new JTextField("RGB(255, 255, 255)");
        rgbField.setEditable(false);
        
        hlsField = new JTextField("HLS(0°, 0%, 100%)");
        hlsField.setEditable(false);
        
        cmykField = new JTextField("CMYK(0%, 0%, 0%, 0%)");
        cmykField.setEditable(false);
        
        textPanel.add(rgbField);
        textPanel.add(hlsField);
        textPanel.add(cmykField);
        
        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton resetButton = new JButton("Сбросить");
        resetButton.addActionListener(e -> {
            currentColor = Color.WHITE;
            updateAllDisplays(currentColor);
        });

        JButton palleteButton = new JButton("Выбрать цвет");
        palleteButton.addActionListener(e -> {
            JColorChooser pallete = new JColorChooser();
            int res = JOptionPane.showConfirmDialog(null, pallete, "Выберите цвет", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                Color c = pallete.getColor();
                currentColor = new Color(c.getRed(), c.getGreen(), c.getBlue());
                updateAllDisplays(currentColor);
            }
        });
        
        buttonPanel.add(palleteButton);
        buttonPanel.add(resetButton);
        
        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void updateFromRGB(boolean from_slider) {
        if (updating) return;
        
        int r = redSlider.getValue();
        int g = greenSlider.getValue();
        int b = blueSlider.getValue();
        if (!from_slider) {
            r = (int)redSpinner.getValue();
            g = (int)greenSpinner.getValue();
            b = (int)blueSpinner.getValue();
        }
        
        setCMYK(RgbToCmyk(r, g, b));
        setHLS(RgbToHls(r, g, b));
        setRGB(new float[]{r, g, b});
    }
    
    private void updateFromHLS(boolean from_slider) {
        if (updating) return;
        
        int h = hueSlider.getValue();
        int s = saturationSlider.getValue();
        int l = lightnessSlider.getValue();
        if (!from_slider) {
            h = (int)hueSpinner.getValue();
            s = (int)saturationSpinner.getValue();
            l = (int)lightnessSpinner.getValue();
        }
        
        setHLS(new float[]{h, l, s});
        float[] rgb = HlsToRgb(h, l, s);
        setCMYK(RgbToCmyk(rgb[0], rgb[1], rgb[2]));
        setRGB(rgb);
    }
    
    private void updateFromCMYK(boolean from_slider) {
        if (updating) return;
        
        int c = cyanSlider.getValue();
        int m = magentaSlider.getValue();
        int y = yellowSlider.getValue();
        int k = blackSlider.getValue();
        
        if (!from_slider) {
            c = (int)cyanSpinner.getValue();
            m = (int)magentaSpinner.getValue();
            y = (int)yellowSpinner.getValue();
            k = (int)blackSpinner.getValue();
        }

        setCMYK(new float[]{c, m, y, k});
        float[] rgb = CmykToRgb(c, m, y, k);
        setHLS(RgbToHls(rgb[0], rgb[1], rgb[2]));
        setRGB(rgb);

    }
    
    private void setHLS(float[] hls) {
        updating = true;

        hueSlider.setValue(Math.round(hls[0]));
        hueSpinner.setValue(Math.round(hls[0]));
        lightnessSlider.setValue(Math.round(hls[1]));
        lightnessSpinner.setValue(Math.round(hls[1]));
        saturationSlider.setValue(Math.round(hls[2]));
        saturationSpinner.setValue(Math.round(hls[2]));

        hlsField.setText(String.format("HLS(%d, %d, %d)", Math.round(hls[0]), Math.round(hls[1]), Math.round(hls[2])));
    }

    private void setRGB(float[] rgb) {
        updating = true;

        redSlider.setValue(Math.round(rgb[0]));
        redSpinner.setValue(Math.round(rgb[0]));
        greenSlider.setValue(Math.round(rgb[1]));
        greenSpinner.setValue(Math.round(rgb[1]));
        blueSlider.setValue(Math.round(rgb[2]));
        blueSpinner.setValue(Math.round(rgb[2]));

        rgbField.setText(String.format("RGB(%d, %d, %d)", Math.round(rgb[0]), Math.round(rgb[1]), Math.round(rgb[2])));
        hexLabel.setText(String.format("#%02X%02X%02X", Math.round(rgb[0]), Math.round(rgb[1]), Math.round(rgb[2])));
        currentColor = new Color(Math.round(rgb[0]), Math.round(rgb[1]), Math.round(rgb[2]));
        colorDisplayPanel.repaint();
        updating = false;
    }

    private void setCMYK(float[] cmyk) {
        updating = true;

        cyanSlider.setValue(Math.round(cmyk[0]));
        cyanSpinner.setValue(Math.round(cmyk[0]));
        magentaSlider.setValue(Math.round(cmyk[1]));
        magentaSpinner.setValue(Math.round(cmyk[1]));
        yellowSlider.setValue(Math.round(cmyk[2]));
        yellowSpinner.setValue(Math.round(cmyk[2]));
        blackSlider.setValue(Math.round(cmyk[3]));
        blackSpinner.setValue(Math.round(cmyk[3]));

        cmykField.setText(String.format("CMYK(%d, %d, %d, %d)", 
            Math.round(cmyk[0]), Math.round(cmyk[1]), Math.round(cmyk[2]), Math.round(cmyk[3])));
    }
    
    private void updateAllDisplays(Color color) {
        updating = true;
        
        // Обновляем RGB
        redSlider.setValue(color.getRed());
        redSpinner.setValue(color.getRed());
        greenSlider.setValue(color.getGreen());
        greenSpinner.setValue(color.getGreen());
        blueSlider.setValue(color.getBlue());
        blueSpinner.setValue(color.getBlue());
        
        // Обновляем HLS
        float[] hls = RgbToHls(color.getRed(), color.getGreen(), color.getBlue());
        hueSlider.setValue(Math.round(hls[0]));
        hueSpinner.setValue(Math.round(hls[0]));
        lightnessSlider.setValue(Math.round(hls[1]));
        lightnessSpinner.setValue(Math.round(hls[1]));
        saturationSlider.setValue(Math.round(hls[2]));
        saturationSpinner.setValue(Math.round(hls[2]));
        
        // Обновляем CMYK
        float[] cmyk = RgbToCmyk(color.getRed(), color.getGreen(), color.getBlue());
        cyanSlider.setValue(Math.round(cmyk[0]));
        cyanSpinner.setValue(Math.round(cmyk[0]));
        magentaSlider.setValue(Math.round(cmyk[1]));
        magentaSpinner.setValue(Math.round(cmyk[1]));
        yellowSlider.setValue(Math.round(cmyk[2]));
        yellowSpinner.setValue(Math.round(cmyk[2]));
        blackSlider.setValue(Math.round(cmyk[3]));
        blackSpinner.setValue(Math.round(cmyk[3]));
        
        // Обновляем текстовые поля
        rgbField.setText(String.format("RGB(%d, %d, %d)", 
            color.getRed(), color.getGreen(), color.getBlue()));
        
        hlsField.setText(String.format("HLS(%d, %d, %d)",
            Math.round(hls[0]), Math.round(hls[1]), Math.round(hls[2])));
        
        cmykField.setText(String.format("CMYK(%d, %d, %d, %d)", 
            Math.round(cmyk[0]), Math.round(cmyk[1]), Math.round(cmyk[2]), Math.round(cmyk[3])));
        
        // Обновляем HEX
        hexLabel.setText(String.format("#%02X%02X%02X", 
            color.getRed(), color.getGreen(), color.getBlue()));
        
        // Перерисовываем панель отображения цвета
        colorDisplayPanel.repaint();
        
        updating = false;
    }

    private float[] HlsToRgb(int h, int l1, int s1) {
        float l = l1 / 100.0f;
        float s = s1 / 100.0f;
        float C = (1 - Math.abs(2 * l - 1)) * s;
        float X = C * (1 - Math.abs((h / 60.0f) % 2 - 1));
        float m = l - C / 2;

        float r0 = 0, g0 = 0, b0 = 0;
        if (h < 60) {
            r0 = C;
            g0 = X;
        } else if (h < 120) {
            r0 = X;
            g0 = C;
        } else if (h < 180) {
            g0 = C;
            b0 = X;
        } else if (h < 240) {
            g0 = X;
            b0 = C;
        } else if (h < 300) {
            r0 = X;
            b0 = C;
        } else {
            r0 = C;
            b0 = X;
        }

        float r = (r0 + m) * 255;
        float g = (g0 + m) * 255;
        float b = (b0 + m) * 255;

        return new float[]{r, g, b};
    }

    private float[] RgbToHls(float r, float g, float b) {
        float h, s, l;
        float r0 = r / 255.0f;
        float g0 = g / 255.0f;
        float b0 = b / 255.0f;
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float d = max - min;
        if (d == 0) {
            h = 0.0f;
            s = 0.0f;
            l = (max + min) * 100.0f / 2.0f / 255.0f;
        } else {
            l = (max + min) / 2.0f / 255.0f;
            float d0 = d / 255.0f;
            s = 100.0f * d0 / (1.0f - Math.abs(2 * l - 1.0f));
            if (max == r) {
                h = 60.0f * (g0 - b0) / d0;
            } else if (max == g) {
                h = 60.0f * ((b0 - r0) / d0 + 2.0f);
            } else {
                h = 60.0f * ((r0 - g0) / d0 + 4.0f);
            }
            if (h < 0) h += 360.0f;
            l *= 100.0f;
        }
        
        return new float[]{Math.round(h), Math.round(l), Math.round(s)};
    }

    private float[] CmykToRgb(int c, int m, int y, int k) {
        float r = 255 * (1 - c / 100.0f) * (1 - k / 100.0f);
        float g = 255 * (1 - m / 100.0f) * (1 - k / 100.0f);
        float b = 255 * (1 - y / 100.0f) * (1 - k / 100.0f);

        return new float[]{r, g, b};
    }

    private float[] RgbToCmyk(float r, float g, float b) {
        if (r == 0 && g == 0 && b == 0) {
            return new float[]{0, 0, 0, 100};
        }
        
        float c = 1 - r / 255.0f;
        float m = 1 - g / 255.0f;
        float y = 1 - b / 255.0f;
        
        float k = Math.min(c, Math.min(m, y));
        float c1 = (c - k) / (1 - k) * 100;
        float m1 = (m - k) / (1 - k) * 100;
        float y1 = (y - k) / (1 - k) * 100;
        
        return new float[]{c1, m1, y1, k * 100};
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Устанавливаем Look and Feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            ColorPickerApp app = new ColorPickerApp();
            app.setVisible(true);
        });
    }
}