package in.munzinger.liberbibliotheca;

import com.google.gson.Gson;
import in.munzinger.liberbibliotheca.book.Book;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Minimal proof-of-concept for text extraction and
 * information gathering using Googles Book API
 *
 * @author Maximilian Munzinger
 */
public class LiberBibliotheca {
    public LiberBibliotheca() {
    }

    /**
     * @param path Path to books, format of files must be pdf
     * @return returns all found files
     */
    public File[] getFiles(String path) {
        File dir = new File(path);
        System.out.println(Arrays.toString(dir.listFiles((dir1, name) -> name.toLowerCase().endsWith(".pdf"))));
        return dir.listFiles((dir1, name) -> name.toLowerCase().endsWith(".pdf"));
    }

    /**
     * @param file single pdf file
     * @return PDDocument for further parsing operations
     */
    public PDDocument getDocument(String file) throws FileNotFoundException {
        try {
            PDDocument document = Loader.loadPDF(new File(file));
            AccessPermission ap = document.getCurrentAccessPermission();
            if (!ap.canExtractContent()) {
                throw new IOException("Missing Permission: Text extraction failed.");
            }
            return document;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        throw new FileNotFoundException("Specified file not found.");
    }

    /**
     * @param doc name of document
     * @throws IOException primary error source are inaccessible files
     */
    public void getIsbnFromDocument(String doc) throws IOException {
        PDDocument document = getDocument(doc);
        PDFTextStripper stripper = new PDFTextStripper();

        // This example uses sorting, but in some cases it is more useful to switch it off,
        // e.g. in some files with columns where the PDF content stream respects the
        // column order.
        stripper.setSortByPosition(true);

        for (int p = 1; p <= 8; ++p) {
            // Extract current page
            stripper.setStartPage(p);
            stripper.setEndPage(p);

            // Extract text
            String text = stripper.getText(document);

            // Search for ISBN-like Numbers
            Pattern pattern = Pattern.compile("(\\d-?){13}");
            Matcher matcher = pattern.matcher(text.trim());

            while (matcher.find()) {
                String pageStr = String.format("Found ISBN on page %d: ", p);
                System.out.print(pageStr);
                System.out.println(matcher.group());
                System.out.println(getInformationFromGoogle(matcher.group()).toString());
                System.out.println();
            }
        }
    }

    /**
     * @param isbn Format has to be ISBN-13
     * @return Book-Object
     */
    public Book getInformationFromGoogle(String isbn) {
        Book book = null;
        try {
            System.out.print("Pulling data from Google: ");
            // Connect to the URL using java's native library
            URL url = new URI("https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn).toURL();
            System.out.println(url);

            InputStreamReader reader = new InputStreamReader(url.openStream());
            Gson gson = new Gson();
            book = gson.fromJson(reader, Book.class);

            System.out.println("Found Items: " + book.totalItems);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return book;
    }

    /**
     * Read in files from given directory, extract ISBN,
     * lookup information for ISBN using Googles Book API,
     * print title and subtitle of book
     *
     * @param args Only valid argument is a directory
     */
    public static void main(String[] args) {
        LiberBibliotheca bibliotheca = new LiberBibliotheca();
        try {
            File[] files = bibliotheca.getFiles(args[0]);
            for (File file : files) {
                System.out.println(file.getName());
                bibliotheca.getIsbnFromDocument(file.getAbsolutePath());
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}