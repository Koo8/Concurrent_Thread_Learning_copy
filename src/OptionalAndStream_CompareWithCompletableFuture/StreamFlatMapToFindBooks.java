package OptionalAndStream_CompareWithCompletableFuture;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * use set to remove repeated books
 * use flatMap to flat the stream of collections,
 *
 */

public class StreamFlatMapToFindBooks {
    public static void main(String[] args) {
        // create a few book list

        BookList list1 = new BookList();
        list1.setTitle("computer science");
        list1.addBook("Best view from java perspective");
        list1.addBook("Python for computer");

        BookList list2 = new BookList();
        list2.setTitle("Learn to code");
        list2.addBook("Beginner book to learning Python");
        list2.addBook("Python for computer");         //NOTE: same book in other list
        list2.addBook("Most popular language for networking");

        BookList list3 = new BookList();
        list3.setTitle("Advanced computing");
        list3.addBook("Python for electrical engineering");
        list3.addBook("Python for computer");         //NOTE: same book in other list
        list3.addBook("Java Advanced approach for data science");

        Set<BookList> library = new HashSet<>();
        library.add(list1);
        library.add(list2);
        library.add(list3);

        // to find all python books and put them in a set<string>  and create a new bookList called "Python"
        Set<String> phthonbooks = library.stream()
                .flatMap(x->x.getBookList().stream())// use flatmap() because it is a multilayered collection
                .filter(w ->w.toLowerCase().contains("python"))
                .collect(Collectors.toSet());

        for(String s:phthonbooks) {
            System.out.println(s );
        }
    }

}
// create a book set
class BookList {
    Set<String> bookList;   // use set -> no same book will be added again.
    String title;
    public Set<String> getBookList() {
        return bookList;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addBook(String bookName) {
        if(bookList == null) {
            bookList = new HashSet<>();
        }
        bookList.add(bookName) ;
    }
   
}
