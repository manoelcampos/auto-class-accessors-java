package io.github.manoelcampos.accessors.sample;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductRepository repository;

    public ProductController(final ProductRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public void save(@RequestBody Product product) {
        repository.save(product);
    }

    @GetMapping("{id}")
    public Product getById(@PathVariable long id) {
        return repository.findById(id).orElseThrow();
    }

    @GetMapping
    public List<Product> findAll(){
        return repository.findAll();
    }
}
