// tag::runner[]
package book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;


@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(AccountRepository accountRepository,
                           BookRepository bookRepository) {
        return (evt) -> Arrays.asList(
                "jhoeller,dsyer,pwebb,ogierke,rwinch,mfisher,mpollack,jlong".split(","))
                .forEach(
                        a -> {
                            Account account = accountRepository.save(new Account(a,
                                    "password"));
                            bookRepository.save(new Book(account,
                                    "name1" + a, "dmitriy", "description"));

                        });
    }

}

// end::runner[]


@RestController
@RequestMapping("/user")
class UserRestController {

    private final AccountRepository accountRepository;

    @Autowired
    UserRestController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    ResponseEntity<?> add(@RequestBody Account account) {
        Account result = this.accountRepository.save(account);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("")
                .buildAndExpand(result.getId()).toUri());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);

    }

    @RequestMapping(value = "/allUsers", method = RequestMethod.GET)
    Collection<Account> readUsers() {
        return this.accountRepository.findAll();
    }

    @RequestMapping(value = "/removeUser/{userId}", method = RequestMethod.DELETE)
    ResponseEntity<?> remove(@PathVariable String userId) {
        Optional<Account> result = this.accountRepository.findByUsername(userId);
        this.accountRepository.delete(result.get());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("")
                .buildAndExpand(result.get().getId()).toUri());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/changeUser/{userId}", method = RequestMethod.PUT)
    Account changeAccount(@RequestBody Account newAccount, @PathVariable String userId){
        return this.accountRepository.findByUsername(userId).map(account -> {
            account.setUsername(newAccount.getUsername());
            return accountRepository.save(account);
        }).orElseGet(() ->{
            newAccount.setUsername(userId);
            return accountRepository.save(newAccount);
        });
    }
}


@RestController
@RequestMapping("/{userId}/books")
class BookRestController {

    private final BookRepository bookRepository;

    private final AccountRepository accountRepository;

    @Autowired
    BookRestController(BookRepository bookRepository,
                       AccountRepository accountRepository) {
        this.bookRepository = bookRepository;
        this.accountRepository = accountRepository;
    }

    @RequestMapping(value = "/removeBook/{bookId}", method = RequestMethod.DELETE)
    ResponseEntity<?> remove(@PathVariable Long bookId) {
        Book result = this.bookRepository.getOne(bookId);
        this.bookRepository.delete(result);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("")
                .buildAndExpand(result.getId()).toUri());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@PathVariable String userId, @RequestBody Book input) {
        this.validateUser(userId);
        return this.accountRepository
                .findByUsername(userId)
                .map(account -> {
                    bookRepository.save(new Book(account, input.name,
                            input.author, "description"));

                    return new ResponseEntity<>(null, HttpStatus.CREATED);
                }).get();

    }

    @RequestMapping(value = "/{bookId}", method = RequestMethod.GET)
    Optional<Book> readBookmark(@PathVariable String userId, @PathVariable Long bookId) {
        this.validateUser(userId);
        return this.bookRepository.findById(bookId);
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<Book> readBookmarks(@PathVariable String userId) {
        this.validateUser(userId);
        return this.bookRepository.findByAccountUsername(userId);
    }

    private void validateUser(String userId) {
        this.accountRepository.findByUsername(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
    }
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String userId) {
        super("could not find user '" + userId + "'.");
    }
}
