
package com.halima.library;

import com.halima.library.model.Book;
import com.halima.library.model.Booking;
import com.halima.library.model.Contact;
import com.halima.library.model.Loan;
import com.halima.library.model.Member;

import com.halima.library.repository.BookRepo;
import com.halima.library.repository.MemberRepo;
import com.halima.library.repository.LoanRepo;
import com.halima.library.repository.BookingRepo;
import com.halima.library.repository.ContactRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class LibraryController {

    @Autowired
    private BookRepo books;

    @Autowired
    private MemberRepo members;

    @Autowired
    private LoanRepo loans;

    @Autowired
    private BookingRepo bookings;

    @Autowired
    private ContactRepo contacts;

    // LOGIN
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Login r) {

        if ("admin".equals(r.username())
                && "admin123".equals(r.password())) {

            return Map.of(
                    "name", "Library Admin",
                    "role", "ADMIN"
            );
        }

        if ("user".equals(r.username())
                && "user123".equals(r.password())) {

            return Map.of(
                    "name", "Library User",
                    "role", "USER"
            );
        }

        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid demo login"
        );
    }

    // GET ALL BOOKS AND SEARCH
    @GetMapping("/books")
    public List<Book> getBooks(
            @RequestParam(required = false) String search) {

        if (search == null || search.isBlank()) {
            return books.findAll();

        }

        return books.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
                search, search
        );
    }

    // ADD BOOK
    @PostMapping("/books")
    public Book addBook(@RequestBody Book b) {

        b.setBid(null);

        if (b.getAvailable() == null) {
            b.setAvailable(true);
        }

        return books.save(b);
    }

    // EDIT BOOK
    @PutMapping("/books/{id}")
    public Book editBook(
            @PathVariable Long id,
            @RequestBody Book b) {

        Book existing = getBook(id);

        existing.setTitle(b.getTitle());
        existing.setAuthor(b.getAuthor());
        existing.setIsbn(b.getIsbn());
        existing.setCategory(b.getCategory());

        return books.save(existing);
    }

    // DELETE BOOK
    @DeleteMapping("/books/{id}")
    public void deleteBook(@PathVariable Long id) {
        books.delete(getBook(id));
    }

    // GET MEMBERS
    @GetMapping("/members")
    public List<Member> getMembers() {
        return members.findAll();
    }

    // ADD MEMBER
    @PostMapping("/members")
    public Member addMember(@RequestBody Member m) {
        m.setId(null);
        return members.save(m);
    }

    // DELETE MEMBER
    @DeleteMapping("/members/{id}")
    public void deleteMember(@PathVariable Long id) {
        members.delete(getMember(id));
    }

    // ISSUE BOOK
    @PostMapping("/loans")
    public Loan issueBook(@RequestBody Action a) {

        Book b = getBook(a.bookId());
        Member m = getMember(a.memberId());

        if (!Boolean.TRUE.equals(b.getAvailable())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Book is unavailable"
            );
        }

        b.setAvailable(false);
        books.save(b);

        Loan l = new Loan();

        l.setBookId(b.getBid());
        l.setBookTitle(b.getTitle());
        l.setMemberId(m.getId());
        l.setMemberName(m.getName());

        l.setIssueDate(LocalDate.now());
        l.setDueDate(LocalDate.now().plusDays(14));

        return loans.save(l);
    }

    // GET LOANS
    @GetMapping("/loans")
    public List<Loan> getLoans() {
        return loans.findAll();
    }

    // RETURN BOOK
    @PostMapping("/loans/{id}/return")
    public Loan returnBook(@PathVariable Long id) {

        Loan l = loans.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Loan not found"
                ));

        if (!l.isReturned()) {

            l.setReturned(true);
            l.setReturnDate(LocalDate.now());

            Book b = getBook(l.getBookId());

            b.setAvailable(true);
            books.save(b);
        }

        return loans.save(l);
    }

    // ADVANCE BOOKING
    @PostMapping("/bookings")
    public Booking reserveBook(@RequestBody Action a) {

        Book b = getBook(a.bookId());
        getMember(a.memberId());

        Booking x = new Booking();

        x.setBookId(b.getBid());
        x.setBookTitle(b.getTitle());
        x.setMemberId(a.memberId());
        x.setBookingDate(LocalDate.now());

        x.setStatus(
                Boolean.TRUE.equals(b.getAvailable())
                        ? "REQUESTED"
                        : "WAITING"
        );

        return bookings.save(x);
    }

    // GET BOOKINGS
    @GetMapping("/bookings")
    public List<Booking> getBookings() {
        return bookings.findAll();
    }

    // CONTACT FORM
    @PostMapping("/contacts")
    public Contact saveContact(@RequestBody Contact c) {

        c.setId(null);
        c.setCreatedAt(LocalDate.now());

        return contacts.save(c);
    }

    // GET CONTACT MESSAGES
    @GetMapping("/contacts")
    public List<Contact> getContacts() {
        return contacts.findAll();
    }

    // FIND BOOK BY ID
    private Book getBook(Long id) {

        return books.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Book not found"
                ));
    }

    // FIND MEMBER BY ID
    private Member getMember(Long id) {

        return members.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Member not found"
                ));
    }

    // REQUEST DTOs
    public record Login(String username, String password) {}

    public record Action(Long bookId, Long memberId) {}
}
