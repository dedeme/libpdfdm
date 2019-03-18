// Copyright 18-Mar-2019 ºDeme
// GNU General Public License - V3 <http://www.gnu.org/licenses/>

package es.dm;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;

/**
 * <p>
 * Converts html text from [rdr] to pdf text through [out]. You can set some
 * parameters:</p>
 * <ul>
 * <li>pageSize (width height). (default pageSize : 595.0pt, 842.0pt)</li>
 * <li>landscape true|false. (default landscape : false)</li>
 * <li>margins (left right top bottom). (default margins : 60pt,
 * 50pt, 60pt, 50pt)</li>
 * <li>startPageNumber int. (default startPageNumber : 0)</li>
 * <li>footer... Equals to header...
 * </ul>
 *
 * <p>
 * You can use a startPageNumber negative to avoid to show headers and footers
 * in first pages. For example, if you set startPageNumber to -1 the first page
 * won't show header and footer. If you set it to -2, so will happen with two
 * first pages.</p>
 *
 * <p>
 * Methods which starts with 'header' configure the document header. It is the
 * same with methods with start with 'footer'. They can take a separation
 * line.</p>
 *
 * <p>
 * This class depends on next libraries:</p>
 * <ul>
 * <li>itextpdf-5.3.4</li>
 * <li>itext-xmlworker-1.2.1</li>
 * </ul>
 *
 * @version 1.0
 * @since 09-Apr-2014
 * @author deme
 */
public class Html2pdf {

  int pageNumber;
  float width, height;
  boolean landscape;
  int left, right, top, bottom;
  int startPageNumber;
  HeaderFooter header;
  HeaderFooter footer;

  public Html2pdf() {
    width = 595f;
    height = 842f;
    landscape = false;
    left = 60;
    right = 50;
    top = 60;
    bottom = 50;
    startPageNumber = 0;

    header = new HeaderFooter();
    header.isHeader = true;
    header.show = false;
    header.text = "";
    header.fontFamily = "times";
    header.fontStyle = "n";
    header.fontSize = 10;
    header.align = 'r';
    header.yCorrect = 0f;
    header.line = false;
    header.lineXCorrect = 0f;
    header.lineYCorrect = 0f;

    footer = new HeaderFooter();
    footer.isHeader = false;
    footer.show = false;
    footer.text = "";
    footer.fontFamily = "times";
    footer.fontStyle = "n";
    footer.fontSize = 10;
    footer.align = 'r';
    footer.yCorrect = 0f;
    footer.line = false;
    footer.lineXCorrect = 0f;
    footer.lineYCorrect = 0f;

  }

  /**
   * Page width in points.
   * @return Width
   */
  public float getWidth() {
    return width;
  }

  /**
   * Page height in points.
   * @return Height
   */
  public float getHeight() {
    return height;
  }

  /**
   * Changes the page size.
   *
   * @param width In points. Default 595f
   * @param height In points. Default 842f
   * @return this
   */
  public Html2pdf setPageSize(float width, float height) {
    this.width = width;
    this.height = height;
    return this;
  }

  /**
   * Returns the orientation page.
   * @return Value
   */
  public boolean isLandscape() {
    return landscape;
  }

  /**
   * Sets the orientation page.
   *
   * @param landscape Default false
   * @return this
   */
  public Html2pdf setLandscape(boolean landscape) {
    this.landscape = landscape;
    return this;
  }

  /**
   * Sets margins
   *
   * @param left Default 60 pt
   * @param right Default 50 pt
   * @param top Default 60 pt
   * @param bottom Default 50 pt
   * @return this
   */
  public Html2pdf setMargins(int left, int right, int top, int bottom) {
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
    return this;
  }

  /**
   * return left margin in points.
   * @return Value
   */
  public int getLeft() {
    return left;
  }

  /**
   * return right margin in points.
   * @return Value
   */
  public int getRight() {
    return right;
  }

  /**
   * return top margin in points.
   * @return Value
   */
  public int getTop() {
    return top;
  }

  /**
   * return bottom margin in points.
   * @return Value
   */
  public int getBottom() {
    return bottom;
  }

  /**
   * Return the start page number
   * @return Value
   */
  public int getStartPageNumber() {
    return startPageNumber;
  }

  /**
   * <p>
   * Set the start page number.</p>
   * <p>
   * You can use a :startPageNumber negative to avoid to show headers and
   * footers in first pages. For example, if you set startPageNumber to -1 the
   * first page won't show header and footer. If you set it to -2, so will
   * happen with two first pages.</p>
   *
   * @param startPageNumber Default 0
   * @return this
   */
  public Html2pdf setStartPageNumber(int startPageNumber) {
    this.startPageNumber = startPageNumber;
    return this;
  }

  /**
   * Returns the header object for its modification. Must be modified before
   * call run().
   *
   * @return The header object.
   */
  public HeaderFooter getHeader() {
    return header;
  }

  /**
   * Returns the footer object for its modification. Must be modified before
   * call run().
   *
   * @return The footer object.
   */
  public HeaderFooter getFooter() {
    return footer;
  }

  void headerFooter(
    PdfWriter writer, Document document, HeaderFooter hf) {
    float xl = document.leftMargin();
    float xr = document.getPageSize().getWidth() - document.rightMargin();
    float yt = document.getPageSize().getHeight() - document.topMargin();
    float yb = document.bottomMargin();

    int align = (hf.align == 'l') ? Element.ALIGN_LEFT
      : (hf.align == 'c') ? Element.ALIGN_CENTER
      : Element.ALIGN_RIGHT;

    String text = hf.text.replace("{pN}", String.valueOf(pageNumber));
    int fontStyle = Font.NORMAL;
    if (hf.fontStyle.contains("b")) {
      fontStyle |= Font.BOLD;
    }
    if (hf.fontStyle.contains("i")) {
      fontStyle |= Font.ITALIC;
    }
    if (hf.fontStyle.contains("u")) {
      fontStyle |= Font.UNDERLINE;
    }
    if (hf.fontStyle.contains("s")) {
      fontStyle |= Font.STRIKETHRU;
    }
    Font font = new Font(
      (hf.fontFamily.equals("helvetica")) ? Font.FontFamily.HELVETICA
      : (hf.fontFamily.equals("courier")) ? Font.FontFamily.COURIER
      : (hf.fontFamily.equals("zapfdingbats")) ? Font.FontFamily.ZAPFDINGBATS
      : (hf.fontFamily.equals("symbol")) ? Font.FontFamily.SYMBOL
      : Font.FontFamily.TIMES_ROMAN, hf.fontSize, fontStyle);
    Phrase phrase = new Phrase(text, font);

    float xPoint = (align == Element.ALIGN_LEFT) ? xl
      : (align == Element.ALIGN_CENTER) ? (xl + xr) / 2
      : xr;

    float yPoint = ((hf.isHeader) ? yt + 19 : yb - 29) + hf.yCorrect;

    ColumnText.showTextAligned(
      writer.getDirectContent(),
      align,
      phrase,
      xPoint,
      yPoint,
      0);

    if (hf.line) {
      float rYPoint = ((hf.isHeader) ? yPoint - 4 : yPoint + 10) + hf.lineYCorrect;
      Rectangle rc = new Rectangle(
        xl
        + ((align == Element.ALIGN_CENTER) ? hf.lineXCorrect / 2
        : (align == Element.ALIGN_RIGHT) ? hf.lineXCorrect
        : 0f), rYPoint, xr
        - ((align == Element.ALIGN_CENTER) ? hf.lineXCorrect / 2
        : (align == Element.ALIGN_LEFT) ? hf.lineXCorrect
        : 0f), rYPoint);
      rc.setBorder(Rectangle.BOTTOM);
      rc.setBorderWidth(1f);
      writer.getDirectContent().rectangle(rc);
    }
  }

  /**
   * Generates the pdf.
   *
   * @param rdr Text html
   * @param out Text pdf
   */
  public void run(BufferedReader rdr, OutputStream out) {
    Rectangle pg = new Rectangle(width, height);
    if (landscape) {
      pg = pg.rotate();
    }
    Document pdf = new Document(pg, left, right, top, bottom);
    try {
      PdfWriter writer = PdfWriter.getInstance(pdf, out);
      writer.setPageEvent(new PdfPageEventHelper() {
        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
          pageNumber = startPageNumber;
        }

        @Override
        public void onStartPage(PdfWriter writer, Document document) {
          ++pageNumber;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
          if (header.show) {
            headerFooter(writer, document, header);
          }
          if (footer.show) {
            headerFooter(writer, document, footer);
          }
        }
      });
      pdf.open();
      XMLWorkerHelper.getInstance().parseXHtml(writer, pdf, rdr);
      pdf.close();
    } catch (DocumentException | IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  /**
   * Generates the pdf.
   *
   * @param html Text html
   * @param out Text pdf
   */
  public void run(String html, OutputStream out) {
    run(new BufferedReader(new StringReader(html)), out);
  }

  public static void paste(File target, File... sources)
    throws IOException, DocumentException {

    if (sources.length == 0) {
      target.createNewFile();
      return;
    }

    Document document = new Document();
    PdfCopy copy = new PdfCopy(document, new FileOutputStream(target));
    document.open();
    PdfReader reader;
    int n;
    for (File source : sources) {
      reader = new PdfReader(new FileInputStream(source));
      n = reader.getNumberOfPages();
      for (int page = 0; page < n;) {
        copy.addPage(copy.getImportedPage(reader, ++page));
      }
      copy.freeReader(reader);
      reader.close();
    }
    document.close();
  }

  /**
   * <p>
   * Allows configurate headers y footers.</p>
   *
   */
  public static class HeaderFooter {

    /**
     * Its value is true for header and false for footer. DO NOT MODIFY!
     */
    boolean isHeader;
    /**
     * Indicates if this will be showed. Default false.
     */
    public boolean show;
    /**
     * Can take the expression "{pN}" for including the page number. Default ""
     */
    public String text;
    /**
     * <p>
     * They are valid values:</p>
     * <ul>
     * <li>times</li>
     * <li>helvetica</li>
     * <li>courier</li>
     * <li>zapfdingbats</li>
     * <li>symbol</li>
     * </ul>
     * <p>
     * Default "times".</p>
     */
    public String fontFamily;
    /**
     * Can take one o more: [n|b|i|u|s]. Default 'n'. (e.g. header.fontStyle =
     * "ib")
     */
    public String fontStyle;
    /**
     * Default 10f
     */
    public float fontSize;
    /**
     * Can be [l|c|r]. Default 'r'
     */
    public char align;
    /**
     * Moves up or down this. Default 0f pt.
     */
    public float yCorrect;
    /**
     * Indicates is this will show a separation line. Default false
     */
    public boolean line;
    /**
     * Make bigger o smaller the separation line. Default 0f pt.
     */
    public float lineXCorrect;
    /**
     * Moves up or down the separation line. Default 0f pt.
     */
    public float lineYCorrect;
  }

}
