package com.product.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.product.entity.Products;
import com.product.service.ProductService;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String home(Model model) {
        return findPaginateAndSorting(0, "id", "asc", model);
    }

    @GetMapping("/page/{pageNo}")
    public String findPaginateAndSorting(@PathVariable(value = "pageNo") int pageNo,
            @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir, Model model) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo, 3, sort);

        Page<Products> page = productService.getAllProductsPaginated(pageable);

        List<Products> list = page.getContent();

        model.addAttribute("pageNo", pageNo);
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPage", page.getTotalPages());
        model.addAttribute("all_products", list);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("revSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "index";
    }

    @GetMapping("/load_form")
    public String loadForm() {
        return "add";
    }

    @GetMapping("/edit_form/{id}")
    public String editForm(@PathVariable(value = "id") long id, Model model) {
        Optional<Products> product = productService.getProductById(id);

        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "edit";
        } else {
            // Handle not found case
            return "redirect:/";
        }
    }

    @PostMapping("/save_products")
    public String saveProducts(@ModelAttribute Products products, Model model) {
        productService.saveProduct(products);
        model.addAttribute("message", "Product added successfully.");
        return "redirect:/load_form";
    }

    @PostMapping("/update_products")
    public String updateProducts(@ModelAttribute Products products, Model model) {
        productService.saveProduct(products);
        model.addAttribute("message", "Product updated successfully.");
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteProducts(@PathVariable(value = "id") long id, Model model) {
        productService.deleteProductById(id);
        model.addAttribute("message", "Product deleted successfully.");
        return "redirect:/";
    }
}
