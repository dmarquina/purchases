package com.scoutingtcg.purchases.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.scoutingtcg.purchases.dto.OrderDetailResponse;
import com.scoutingtcg.purchases.dto.OrderItemDto;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class OrderPdfService {

    public byte[] generateOrderPdf(OrderDetailResponse orderDetailResponse) throws Exception {
        try {

            Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font headerFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            Font labelFont = new Font(Font.HELVETICA, 10, Font.BOLD);
            Font bodyFont = new Font(Font.HELVETICA, 10);

            // Shipping address
            doc.add(new Paragraph("Ship To:", labelFont));
            PdfPTable shipToTable = new PdfPTable(2); // Crear una tabla con 2 columnas
            shipToTable.setWidthPercentage(100);

            PdfPCell cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            cell.addElement(new Paragraph(orderDetailResponse.fullName(), headerFont));
            cell.addElement(new Paragraph(orderDetailResponse.address() + (orderDetailResponse.apartment() != null && !orderDetailResponse.apartment().isEmpty() ? ", Apt " + orderDetailResponse.apartment() : ""), headerFont));
            shipToTable.addCell(cell);

            PdfPCell emptyCell = new PdfPCell();
            emptyCell.setBorder(Rectangle.NO_BORDER);
            shipToTable.addCell(emptyCell); // Celda vacía para la segunda columna

            doc.add(shipToTable); // Agregar la tabla al documento
            doc.add(new Paragraph(" ", bodyFont));
            doc.add(new Paragraph("--------------------------------------------------------------------------------------------------------------------------------------------", bodyFont));


            doc.add(new Paragraph("Order Number: " + orderDetailResponse.id(), headerFont));
            doc.add(new Paragraph(" ", bodyFont));


            PdfPTable table = new PdfPTable(2); // Crear una tabla con 2 columnas
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Primera columna
            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(Rectangle.BOX);
            leftCell.addElement(new Paragraph("Shipping Address: ", labelFont));
            leftCell.addElement(new Paragraph(orderDetailResponse.fullName(), bodyFont));
            leftCell.addElement(new Paragraph(orderDetailResponse.address() + (orderDetailResponse.apartment() != null && !orderDetailResponse.apartment().isEmpty() ? ", Apt " + orderDetailResponse.apartment() : ""), bodyFont));
            leftCell.addElement(new Paragraph(" ", bodyFont));

            // Segunda columna
            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(Rectangle.BOX);
            Phrase orderDatePhrase = new Phrase();
            orderDatePhrase.add(new Chunk("Order date: ", labelFont));
            orderDatePhrase.add(new Chunk(orderDetailResponse.createdAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), bodyFont));
            rightCell.addElement(orderDatePhrase);

            Phrase shippingMethodPhrase = new Phrase();
            shippingMethodPhrase.add(new Chunk("Shipping method: ", labelFont));
            shippingMethodPhrase.add(new Chunk("Standard (10-15 days)", bodyFont));
            rightCell.addElement(shippingMethodPhrase);

            Phrase buyerNamePhrase = new Phrase();
            buyerNamePhrase.add(new Chunk("Buyer Name: ", labelFont));
            buyerNamePhrase.add(new Chunk(orderDetailResponse.fullName(), bodyFont));
            rightCell.addElement(buyerNamePhrase);

            rightCell.addElement(new Paragraph(" ", bodyFont));

            // Agregar celdas a la tabla
            table.addCell(leftCell);
            table.addCell(rightCell);
            doc.add(new Paragraph(" ", bodyFont));
            // Agregar la tabla al documento
            doc.add(table);


            // Tabla de ítems
            PdfPTable productTable = new PdfPTable(new float[]{4, 1, 2});
            productTable.setWidthPercentage(100);
            productTable.addCell(new PdfPCell(new Phrase("Item", labelFont)));
            productTable.addCell(new PdfPCell(new Phrase("Qty", labelFont)));
            productTable.addCell(new PdfPCell(new Phrase("Total", labelFont)));

            for (OrderItemDto item : orderDetailResponse.items()) {
                productTable.addCell(new Phrase(item.name() + " (" + item.presentation() + " - " + item.franchise() + ")", bodyFont));
                productTable.addCell(new Phrase("x" + item.quantity(), bodyFont));
                productTable.addCell(new Phrase(String.format("$%.2f", item.price() * item.quantity()), bodyFont));
            }

            doc.add(productTable);

            // Totales
            doc.add(new Paragraph(" ", bodyFont));
            doc.add(new Paragraph("Shipping: " + (orderDetailResponse.shippingCost() > 0 ? "$" + orderDetailResponse.shippingCost() : "FREE"), labelFont));
            doc.add(new Paragraph("Total: $" + String.format("%.2f", orderDetailResponse.total()), headerFont));

            // Mensaje final
            doc.add(new Paragraph(" ", bodyFont));
            doc.add(new Paragraph("Thank you for your purchase!", labelFont));

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new Exception("Error generating PDF: " + e.getMessage(), e);
        }
    }
}
