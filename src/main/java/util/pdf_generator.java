package util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import modelo.pago_modelo;

import java.io.FileOutputStream;
import java.io.File;

public class pdf_generator {

    public static boolean generate_receipt(pago_modelo pago, String dest_path) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(new File(dest_path)));
            document.open();

            Font title_font = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font regular_font = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

            Paragraph title = new Paragraph("RECIBO DE PAGO - DOJO", title_font);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Detalles de la Transacción:", title_font));
            document.add(new Paragraph(" "));
            
            document.add(new Paragraph("Alumno: " + pago.get_nombre_alumno(), regular_font));
            document.add(new Paragraph("Fecha de Pago: " + pago.get_fecha_pago(), regular_font));
            document.add(new Paragraph("Monto: $" + pago.get_monto() + " MXN", regular_font));
            document.add(new Paragraph("Método de Pago: " + pago.get_metodo_pago(), regular_font));
            document.add(new Paragraph("Concepto: Mensualidad", regular_font));

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Gracias por su pago.", regular_font);
            footer.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}