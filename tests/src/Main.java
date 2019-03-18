// Copyright 18-Mar-2019 ºDeme
// GNU General Public License - V3 <http://www.gnu.org/licenses/>


import es.dm.Html2pdf;
import es.dm.Std;
import java.io.*;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) {
      Std.sysInit("libjdmTests");
      try {
        String img = Std.fpath(Std.home(), "asterisk.png");
        Files.copy(
          ClassLoader.getSystemResourceAsStream("asterisk.png"),
          new File(img).toPath(),
          StandardCopyOption.REPLACE_EXISTING
        );

        Html2pdf h2p = new Html2pdf();
        Html2pdf.HeaderFooter footer = h2p.getFooter();
        footer.show = true;
        footer.text = "Cañón and Page {pN}.";
        footer.fontFamily = "helvetica";
        footer.fontStyle = "bius";
        footer.yCorrect = 50f;
        footer.align = 'c';
        footer.line = true;
        footer.lineXCorrect = 100;

        Html2pdf.HeaderFooter header = h2p.getHeader();
        header.show = true;
        header.text = "Page {pN}";
        header.fontFamily = "zapfdingbats";
        header.fontSize = 20f;
        header.align = 'l';
        header.line = true;

        String tx = ""
          + "<html>"
          + "<style>body {color:#000080} p {indent:40px}"
          + " th {border-bottom-width:1px}{</style>"
          + "<body style='font-family:times;font-size:10px;'>"
          + "<p style='margin-left:100px;'>H<b>e</b>llo!</p>"
          + "<p style='text-align:left'><img src='" + img.toString() + "'/></p>"
          + "<table style='repeat-header:yes;repeat-footer:yes'>"
          + "<tr><th>Apellidos y nombre</th>"
          + "<th>Calificación</th></tr>"
          + "<tr><td>Martín, José</td><td align='right'>3,04</td></tr>"
          + "<tr><td>Fernádez, Federico</td><td align='right'>8,70</td></tr>"
          + "</table>"
          + "<p>Arañazo al avión</p>"
          + "<span style='page-break-before:always'></span>"
          + "<table style='width:110%'><tr><td>"
          + "<table style='border:1px;repeat-header:yes;repeat-footer:yes'>"
          + "<tr><th width='200'>Name</th><th width='200'>Age</th></tr>"
          + "<tr><td style='border:0px'>Peter</td><td>33</td></tr>"
          + "</table></td><td style='width:300px'></td></tr></table>"
          + "<p style='margin-left:200px'>Texto con margen izquierdo</p>"
          + "</body></html>";
        h2p.run(tx, new FileOutputStream(Std.fpath(Std.home(), "out.pdf")));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
}
