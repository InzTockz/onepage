package com.battilana.onepage.util.report;

import com.battilana.onepage.dto.facturas.FacturasPorCobrarClientResponse;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.RingPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class EstadoCuentaGraficos {
    private static final DateTimeFormatter FMT_API = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public long calcularDias(String vencimiento) {
        return ChronoUnit.DAYS.between(LocalDate.parse(vencimiento, FMT_API), LocalDate.now());
    }

    public String estadoVence(long dias) {
        if (dias <= 0) return "No Vencido";
        if (dias <= 30) return "0 - 30";
        if (dias <= 45) return "31 - 45";
        if (dias <= 60) return "46 - 60";
        if (dias <= 90) return "61 - 90";
        if (dias <= 180) return "91 - 180";
        return "+ 180";
    }

    public BigDecimal sumaSaldo(List<FacturasPorCobrarClientResponse> datos, boolean soloVencido) {
        BigDecimal t = BigDecimal.ZERO;
        for (FacturasPorCobrarClientResponse f : datos) {
            boolean venc = calcularDias(f.vencimiento()) > 0;
            if (!soloVencido || venc)
                t = t.add(f.saldo() != null ? f.saldo() : BigDecimal.ZERO);
        }
        return t;
    }

    public byte[] top10Png(List<FacturasPorCobrarClientResponse> datos) throws IOException {
        Map<String, BigDecimal> porCliente = new LinkedHashMap<>();
        for (FacturasPorCobrarClientResponse f : datos) {
            if (calcularDias(f.vencimiento()) > 0) {
                BigDecimal s = f.saldo() != null ? f.saldo() : BigDecimal.ZERO;
                porCliente.merge(f.nombre(), s, BigDecimal::add);
            }
        }
        List<Map.Entry<String, BigDecimal>> top = porCliente.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(10)
                .toList();

        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (Map.Entry<String, BigDecimal> e : top)                  // el mayor arriba
            ds.addValue(e.getValue(), "Vencido", e.getKey());

        JFreeChart chart = ChartFactory.createBarChart(
                "TOP 10 Vencidos", null, null, ds, PlotOrientation.HORIZONTAL, false, false, false);
        chart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        CategoryAxis domain = plot.getDomainAxis();
        domain.setTickLabelFont(new Font("Arial", Font.PLAIN, 8));
        domain.setMaximumCategoryLabelWidthRatio(0.9f);
        domain.setMaximumCategoryLabelLines(2);

        double max = top.isEmpty() ? 0 : top.get(0).getValue().doubleValue();
        final double umbral = max * 0.15;

        BarRenderer r = new BarRenderer() {
            @Override
            public Paint getItemLabelPaint(int row, int column) {
                Number v = ds.getValue(row, column);
                double val = v == null ? 0 : v.doubleValue();
                return val >= umbral ? Color.WHITE : new Color(0x22, 0x22, 0x22);
            }
        };
        plot.setRenderer(r);
        r.setSeriesPaint(0, new Color(226, 0, 0));
        r.setBarPainter(new StandardBarPainter());
        r.setShadowVisible(false);
        r.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("#,##0.00")));
        r.setDefaultItemLabelsVisible(true);
        r.setDefaultItemLabelFont(new Font("Arial", Font.BOLD, 9));
        r.setDefaultPositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.INSIDE3, TextAnchor.CENTER_RIGHT));
        r.setPositiveItemLabelPositionFallback(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE3, TextAnchor.CENTER_LEFT));

        return aPng(chart.createBufferedImage(546, 315));
    }

    public byte[] donaPng(List<FacturasPorCobrarClientResponse> datos) throws IOException {
        BigDecimal total = sumaSaldo(datos, false);
        BigDecimal vencido = sumaSaldo(datos, true);
        double pct = total.signum() == 0 ? 0
                : vencido.divide(total, 4, RoundingMode.HALF_UP).doubleValue() * 100;

        DefaultPieDataset<String> ds = new DefaultPieDataset<>();
        ds.setValue("Vencido", pct);
        ds.setValue("No vencido", 100 - pct);

        JFreeChart chart = ChartFactory.createRingChart(null, ds, false, false, false);
        RingPlot plot = (RingPlot) chart.getPlot();
        plot.setSectionPaint("Vencido", new Color(226, 0, 0));
        plot.setSectionPaint("No vencido", new Color(46, 50, 71));
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setSectionDepth(0.35);
        plot.setLabelGenerator(null);
        chart.setBackgroundPaint(Color.WHITE);

        BufferedImage ring = chart.createBufferedImage(170, 170);
        BufferedImage canvas = new BufferedImage(320, 180, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = canvas.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE); g.fillRect(0, 0, 320, 180);
        g.drawImage(ring, 5, 5, null);

        g.setColor(new Color(46, 50, 71));
        g.setFont(new Font("Arial", Font.BOLD, 26));
        String p = Math.round(pct) + "%";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(p, 90 - fm.stringWidth(p) / 2, 90 + (fm.getAscent() - fm.getDescent()) / 2);

        g.setColor(new Color(226, 0, 0));
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("VENCIDO USD", 185, 82);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(new DecimalFormat("#,##0.00").format(vencido), 185, 108);
        g.dispose();

        return aPng(canvas);
    }

    private byte[] aPng(BufferedImage img) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "PNG", out);
        return out.toByteArray();
    }
}
