package in.munzinger.liberbibliotheca.book;

import java.util.ArrayList;

public class Book {
    public String kind;
    public int totalItems;
    public ArrayList<Item> items;

    @Override
    public String toString() {
        StringBuilder returnString;
        returnString = new StringBuilder("kind: " + kind + " - totalItems: " + totalItems);

        if (totalItems >= 1) {
            for (Item item : items) {
                returnString.append(" - Link: ").append(item.selfLink).append(" - Titel: ").append(item.volumeInfo.title).append(" - Subtitel: ").append(item.volumeInfo.subtitle);
            }
        }
        return returnString.toString();
    }
}

