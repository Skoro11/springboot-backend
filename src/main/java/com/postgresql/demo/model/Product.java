package com.postgresql.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Data
@Entity
@Table(name = "products") // Match database table
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private Double stars; // DECIMAL(2,1) is handled as Double

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price; // DECIMAL(10,2) is handled as Double

    private Integer numOfReviews;

    private String tag;

    private Double discountedPrice; // DECIMAL(10,2) is handled as Double

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String specialCategory;
}

// Repository
@RepositoryRestResource
interface ProductRepository extends JpaRepository<Product, Long> {}

// Service
@Service
class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return repository.findById(id);
    }

    public Product saveProduct(Product product) {
        return repository.save(product);
    }

    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }
}

// Controller
@RestController
@RequestMapping("/products")
class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return service.getAllProducts();
    }

    @GetMapping("/{id}")
    public Optional<Product> getProductById(@PathVariable Long id) {
        return service.getProductById(id);
    }

    @PostMapping
    public Product saveProduct(@RequestBody Product product) {
        return service.saveProduct(product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);
    }
}

@Service
class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String name, String email, String phone, String message) {
        // Log the sender's name, email, phone, and message in the console
        System.out.println("Email sent by: " + name + " " + email + " " + phone + " " + message);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("test@example.com"); // Replace with recipient email
        mailMessage.setSubject("New Contact Form Submission");
        mailMessage.setText("Name: " + name + "\nEmail: " + email + "\nPhone: " + phone + "\n\nMessage: \n" + message);
        mailSender.send(mailMessage);
    }
}

@RestController
@RequestMapping("/contact")
class ContactController {
    private final EmailService emailService;

    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public String sendEmail(@RequestBody ContactRequest contactRequest) {
        emailService.sendEmail(contactRequest.getName(), contactRequest.getEmail(), contactRequest.getPhone(), contactRequest.getMessage());
        return "Message sent !";
    }
}

class ContactRequest {
    private String name;
    private String email;
    private String phone;
    private String message;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

