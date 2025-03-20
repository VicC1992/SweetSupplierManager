package com.mybakery.sweet_suppliers.util;

import com.lowagie.text.pdf.PdfPTable;
import com.mybakery.sweet_suppliers.Enums.OrderStatus;
import com.mybakery.sweet_suppliers.entity.Order;
import com.mybakery.sweet_suppliers.entity.OrderItem;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfGenerator {
    public void generateOrderPdf(OutputStream outputStream, Order order, List<OrderItem> orderItems) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            //title
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph(order.getName(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            //details
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Supplier: " + order.getSupplier().getName()));
            document.add(new Paragraph(" "));

            //table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            table.addCell("Product Name");
            table.addCell("Price");
            table.addCell("Quantity");
            table.addCell("Total");

            for (OrderItem item : orderItems) {
                table.addCell(item.getProduct().getName());
                table.addCell(String.format("%.2f", item.getPrice()));
                table.addCell(String.valueOf(item.getQuantity()));

                BigDecimal total = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                table.addCell(String.format("%.2f", total));
            }

            document.add(table);

            double totalPrice = orderItems.stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .mapToDouble(BigDecimal::doubleValue)
                    .sum();

            document.add(new Paragraph("Total Price: " + String.format("%.2f", totalPrice)));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = order.getReceivedAt().format(formatter);

            if (order.getStatus().equals(OrderStatus.Received)) {
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Received by: " + order.getReceivedBy().getFirstName() + " " + order.getReceivedBy().getLastName()));
                document.add(new Paragraph("Received at: " + formattedDate));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}
