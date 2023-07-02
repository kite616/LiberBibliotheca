package in.munzinger.liberbibliotheca.test;

import in.munzinger.liberbibliotheca.LiberBibliotheca;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LiberBibliothecaTest {
    LiberBibliotheca bib = new LiberBibliotheca();

    @Test
    void getFiles() {
        assertEquals("[test-files\\empty.pdf, test-files\\malformed.pdf, test-files\\[Rheinwerk] Blender 3 - Das umfassende Handbuch (978-3-8362-7156-1).pdf, test-files\\[Rheinwerk] Daily Play - Agile Spiele für Coaches und Scrum Master (978-3-8362-7887-4).pdf]", Arrays.toString(bib.getFiles("test-files")));

        // Mock setup
        File[] mockfiles = new File[1];
        mockfiles[0] = new File("mock-files\\mock.pdf");
        LiberBibliotheca mockbib = mock(LiberBibliotheca.class);
        when(mockbib.getFiles("mock")).thenReturn(mockfiles);

        assertEquals("[mock-files\\mock.pdf]", Arrays.toString(mockbib.getFiles("mock")));
    }

    @Test
    void getInformationFromGoogle() {
        assertEquals("Blender 3", bib.getInformationFromGoogle("978-3-8362-7157-8").items.get(0).volumeInfo.title);
        assertEquals("Das umfassende Handbuch", bib.getInformationFromGoogle("978-3-8362-7157-8").items.get(0).volumeInfo.subtitle);

        assertEquals(0, bib.getInformationFromGoogle("978-3-8362-7157-A").totalItems);
        assertEquals(0, bib.getInformationFromGoogle("978-3-83622-7157-8").totalItems);
    }

    @Test
    void getDocument() {
        assertThrows(FileNotFoundException.class, () -> bib.getDocument("void.pdf"));
    }
}